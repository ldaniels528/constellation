package constellation.app.functions.line;

import static constellation.app.functions.DefaultPopupMenu.PROPERTIES_ICON;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.functions.DefaultPopupMenu.PropertiesAction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.menu.CxMenuItem;

/**
 * The LINE::PT-TO-PT function
 * @author lawrence.daniels@gmail.com
 */
public class LinePtToPtFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps( 
		"Select or Indicate the start #point of the line",
		"Select or Indicate the end #point of the line"	 
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon SEGMENT_ICON 	= cxm.getIcon( "images/commands/line/popup/segment.png" );
	private final Icon INFINITE_ICON 	= cxm.getIcon( "images/commands/line/popup/infinite.png" );
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// internal fields
	private boolean isInfinite;
	private PointXY currentPos;
	private JPopupMenu popupMenu;
	private PointXY anchorPoint;
	
	/**
	 * Default constructor
	 */
	public LinePtToPtFunction() {
		super( 
			"LINE", "PT-TO-PT", 
			"images/commands/line/line-2pts.png", 
			"docs/functions/line/pt-pt.html", 
			STEPS
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			handleClickPoint( controller, EntityRepresentationUtil.getPoint( element ), false );
		}
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
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// initialize the editor dialog
		dialog.resetIdentity( EntityCategoryTypes.LINEAR );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.AbstractFunction#processMouseClick(constellation.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the clicked point
				final ModelElement pickedPtElem = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				
				// handle the "selected" point
				handleClickPoint( controller, EntityRepresentationUtil.getPoint( pickedPtElem ), false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// get the new point
				final PointXY newPoint = controller.untransform( mouseClick );
				
				// handle the "indicated" point
				handleClickPoint( controller, newPoint, true );
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.AbstractFunction#processMouseMovement(constellation.ApplicationController, java.awt.Point)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {	
		// allow parent to perform dynamic highlighting
		super.processMouseMovement( controller, mousePos );
		
		// get the current space position
		currentPos = controller.untransform( mousePos );
		
		// if we're on the 2nd step, move the temporary line
		if( steps.currentIndex() == STEP_2 ) {
			// get the model
			final GeometricModel model = controller.getModel();
			
			// create the planar point
			final PointXY endPoint = currentPos;
 			
			// create the temporary line
			model.setTemporaryElement( createTemporaryLine( anchorPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link PointXY click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleClickPoint( final ApplicationController controller, 
								   final PointXY point, 
								   final boolean addToModel ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
						
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( point );
			}
			
			// handle the current step
			switch( steps.currentIndex() ) {
				case STEP_1:
					// set the anchor point
					anchorPoint = point;
					break;
					
				case STEP_2:
					// add the line
					final ModelElement line = 
						new CxModelElement( createLine( anchorPoint, point ) );
					
					// update the line's settings
					dialog.exportSettings( line );
					
					// add the line
					model.addPhysicalElement( line );
					
					// set the status message
					controller.setStatusMessage( format( "Created line '%s'", line ) );
					break;
			}
			
			// request a redraw
			controller.requestRedraw();
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Indicates whether the line to be created is an infinite line
	 * @return true, if the line to be created is an infinite line 
	 */
	private boolean isInfiniteLine() {
		return isInfinite;
	}
	
	/**
	 * Sets the infinite line state flag
	 * @param infinite the infinite line state flag
	 */
	protected void setInfiniteLine( final boolean infinite ) {
		this.isInfinite = infinite;
	}
	
	/**
	 * Creates a two-dimensional line
	 * @param p1 the initial {@link PointXY point}
	 * @param p2 the termination {@link PointXY point}
	 * @return a {@ink CxLine two-dimensional line}
	 */
	private LineXY createLine( final PointXY p1, final PointXY p2 ) {
		return isInfiniteLine() ? LineXY.createInfiniteLine( p1, p2 ) : new LineXY( p1, p2 );			
	}

	/**
	 * Creates a temporary line
	 * @param p1 the initial {@link PointXY point}
	 * @param p2 the termination {@link PointXY point}
	 * @return a {@ink CxLine two-dimensional line}
	 */
	private RenderableElement createTemporaryLine( final PointXY p1, final PointXY p2 ) {
		// create the temporary line
		final LineXY line = createLine( p1, p2 );
		
		// create the HUD
		final HUDXY hud = new HUDXY( line, p1, p2 );
		
		// create the length note
		if( !isInfiniteLine() ) {
			hud.add( new TextNoteXY( line.getMidPoint(), 
					format( "L=%3.2f", line.length() ) ) );
		}
		
		// return the composition
		return hud;
	}
	
	/** 
	 * Line: Point-to-Point Pop-up Menu
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super.add( new CxMenuItem( "New point", NEW_PT_ICON, new NewPointAction( controller ) ) );
			super.add( new CxMenuItem( "Segment", SEGMENT_ICON, new InfiniteLineAction( controller, false ) ) );
			super.add( new CxMenuItem( "Infinite", INFINITE_ICON, new InfiniteLineAction( controller, true ) ) );
			super.add( new JSeparator() );
			super.add( new CxMenuItem( "Properties", PROPERTIES_ICON, new PropertiesAction( controller ) ) );
		}
	}
	
	/**
	 * Infinite Line Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class InfiniteLineAction implements ActionListener {
		private final ApplicationController controller;
		private final boolean isInfinite;
		
		/**
		 * Creates a new plug-in action listener
		 * @param controller the given {@link ApplicationController controller}
		 * @param isInfinite indicates whether the line is infinite (true) or a segment (false)
		 */
		public InfiniteLineAction( final ApplicationController controller, final boolean isInfinite ) {
			this.controller = controller;
			this.isInfinite	= isInfinite;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			setInfiniteLine( isInfinite );
			controller.requestRedraw();
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
			// add the point to the model
			final GeometricModel model = controller.getModel();
			model.addPhysicalElement( currentPos );
			
			// treat it like a clicked point
			handleClickPoint( controller, currentPos, true );
		}
	}
	
}