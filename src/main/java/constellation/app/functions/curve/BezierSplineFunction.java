package constellation.app.functions.curve;

import static constellation.drawing.EntityTypes.POINT;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static java.lang.String.format;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.SplineXY;
import constellation.drawing.entities.VerticesXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.ui.components.menu.CxMenuItem;

/**
 * The SPLINE::BEZIER function
 * @author lawrence.daniels@gmail.com
 */
public class BezierSplineFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select a #point or Indicate to create the #spline"
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	private final Icon FINISH_ICON 		= cxm.getIcon( "images/commands/common/popup/finish.gif" );
	
	// internal fields
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	private SplineXY spline;

	/**
	 * Default constructor
	 */
	public BezierSplineFunction() {
		super( 
			"SPLINE", "BEZIER", 
			"images/commands/curve/spline-pts.png", 
			"docs/functions/curve/spline_pts.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handlePointSelection( controller, element ); 
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu( final ApplicationController controller ) {
		if( popupMenu == null ) {
			popupMenu = new MyPopupMenu( controller );
		}
		return popupMenu;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to do it's job
		super.onStart( controller );
		
		// reset the values
		reset( controller );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				final ModelElement pickedPt = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				
				// handle the selection
				handlePointSelection( controller, pickedPt ); 
				break;
				
			// create the spline
			case BUTTON_INDICATE:
				if( spline != null ) {
					handleSplineCreation( controller, model );
					advanceToNextStep( controller );
				}
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseDrag(constellation.functions.ApplicationController, double, double)
	 */
	@Override
	public boolean processMouseDrag( final ApplicationController controller, 
									 final MouseClick oldMousePos, 
									 final MouseClick newMousePos ) {
		// if no spline is selected, fail fast
		if( spline == null ) {
			return false;	
		}
		
		// get the bounds of the click area
		final RectangleXY boundary = controller.untransform( oldMousePos.getClickBounds() );
		
		// get the limit vertices as points
		final VerticesXY limits = spline.getLimits();	
		final ModelElement[] points = CxModelElement.createElements( limits );
		
		// determine the selected point
		final ModelElement pickedPt = lookupPointByRegion( controller, boundary, points );
		
		// was a point found?
		if( pickedPt != null ) {
			// get the selected spline vertex
			final PointXY p = EntityRepresentationUtil.getPoint( pickedPt );
			final PointXY vertex = spline.getLimitVertexAt( p.getX(), p.getY() );
			
			// was the vertex found?
			if( vertex != null ) {
				// determine the real world position of the mouse
				final PointXY oldPos = controller.untransform( oldMousePos );
				final PointXY newPos = controller.untransform( newMousePos );
				
				// get the delta X and Y
				final double dx = newPos.x - oldPos.x;
				final double dy = newPos.y - oldPos.y;
			
				// move the vertex
				vertex.x += dx;
				vertex.y += dy;
				
				// reset the limits
				spline.resetCache();
				
				// drag event successful
				return true;
			}
		}
		
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow the parent function to perform highlights
		super.processMouseMovement( controller, mousePos );
		
		// get the current space position
		currentPos = controller.untransform( mousePos );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#supportsDrag()
	 */
	@Override
	public boolean supportsDrag() {
		return true;
	}
	
	/**
	 * Handles the selection of a point, which extends the spline 
	 * @param controller the given {@link ApplicationController controller}
	 * @param pointElem the given {@link ModelElement point}
	 */
	private void handlePointSelection( final ApplicationController controller, final ModelElement pointElem ) {
		if( pointElem != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the point
			model.selectGeometry( pointElem );
			
			// if at least two points are selected ...
			if( spline == null ) {
				if( model.getSelectedElementCount() > 1 ) {
					// create the spline
					spline = createSpline( controller, model );	
					
					// set it as temporary geometry
					model.setTemporaryElement( spline );
				}
			}
			
			// otherwise, just append the point
			else {
				// insure we don't exceed the maximum number of points
				if( spline.getSegmentCount() < SplineXY.MAX_POINTS ) {
					final PointXY p = EntityRepresentationUtil.getPoint( pointElem );
					spline.append( p );
				}
				else {
					controller.setStatusMessage( format( "A maximum of %d points may be used", SplineXY.MAX_POINTS ) );
				}
			}
		}
	}

	/**
	 * Handles the creation of the spline 
	 * @param controller the given {@link ApplicationController function controller}
	 * @param model the given {@link GeometricModel geometric model}
	 */
	private void handleSplineCreation( final ApplicationController controller, 
			   						   final GeometricModel model ) {
		// create the spline element
		final ModelElement splineElem = new CxModelElement( spline );
		
		// add the spline to the model
		model.addPhysicalElement( splineElem );
		
		// notify the operator
		controller.setStatusMessage( format( "Created spline '%s'", splineElem.getLabel() ) );	
		
		// reset everything
		reset( controller );
	}

	/**
	 * Creates a spline through the selected points
	 * @return the spline {@link SplineXY spline}
	 */
	private SplineXY createSpline( final ApplicationController controller, final GeometricModel model ) {
		// get the selected points
		final PointXY[] points = getSelectedPoints( controller, model );
		
		// create the spline representation
		final SplineXY spline = new SplineXY( points );
		
		// return the spline curve
		return spline;
	}
	
	/**
	 * Looks up the point at the given boundary
	 * @param controller the given {@link RectangleXY boundary}
	 * @param boundary the given {@link RectangleXY boundary}
	 * @param points the collection of {@link ModelElement points}
	 * @return the resultant {@link ModelElement point}, or <tt>null</tt> if not found
	 */
	private ModelElement lookupPointByRegion( final ApplicationController controller,
										 	  final RectangleXY boundary, 
										 	  final ModelElement[] points) {
		// get the matrix instance
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// perform the lookup operation
		for( final ModelElement point : points ) {
			if( ( point.getType() == POINT ) && 
					point.intersects( boundary, matrix ) ) {
				return point;
			}
		}
		
		return null;
	}

	/**
	 * Retrieves the selected points
	 * @param model the given {@link GeometricModel model}
	 * @return the array of {@link PointXY points}
	 */
	private PointXY[] getSelectedPoints( final ApplicationController controller, final GeometricModel model ) {
		// create a point array
		final PointXY[] pointArray = new PointXY[ model.getSelectedElementCount() ];
		
		// get the selected elements
		final RenderableElement[] selectedGeometry = new RenderableElement[pointArray.length];
		model.getSelectedGeometry( selectedGeometry );
		
		// create a point array
		for( int n = 0; n < pointArray.length; n++ ) {
			pointArray[n] = EntityRepresentationUtil.getPoint( selectedGeometry[n] );
		}
		return pointArray;
	}

	/**
	 * Resets the function 
	 * @param model the given {@link GeometricModel model}
	 */
	private void reset( final ApplicationController controller ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// clear the selected points
		model.clearSelectedElements();
		
		// clear the temporary spline
		model.clearTemporaryElement();
		
		// clear the spline reference
		spline = null;
	}
				
	/** 
	 * Curve: Bezier Pop-up Menu 
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super( "Curve: Bezier" );
			super.add( new CxMenuItem( "New point", NEW_PT_ICON, new NewPointAction( controller ) ) );
			super.add( new CxMenuItem( "Complete", FINISH_ICON, new CompleteAction( controller ) ) );
		}
	}
	
	/** 
	 * Complete Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class CompleteAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a "Complete" action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public CompleteAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			if( spline != null ) {
				// get the model instance
				final GeometricModel model = controller.getModel();
				
				// create the curve
				handleSplineCreation( controller, model );
				advanceToNextStep( controller );
			}
		}
	}
	
	/** 
	 * "New Point" Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class NewPointAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a "New Point" action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public NewPointAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			if( currentPos != null ) {
				// create a new point
				final ModelElement newPointElem = new CxModelElement( currentPos );
				
				// add the point to the model
				final GeometricModel model = controller.getModel();
				model.addPhysicalElement( newPointElem );
				
				// select the point
				handlePointSelection( controller, newPointElem );
				
				// request a redraw
				controller.requestRedraw();
			}
		}
	}

}