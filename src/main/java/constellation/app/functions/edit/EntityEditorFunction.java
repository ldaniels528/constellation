package constellation.app.functions.edit;

import static constellation.drawing.EntityTypes.POINT;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.util.Arrays;
import java.util.Collection;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.EntityRepresentationUtil;
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
import constellation.ui.components.CxDialog;

/** 
 * The EDIT::MODIFY function
 * @author lawrence.daniels@gmail.com
 */
public class EntityEditorFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Key the curve parameters"
	);
	private EntityEditorDialog dialog;
	
	/**
	 * Default constructor
	 */
	public EntityEditorFunction() {
		super( 
			"EDIT", "MODIFY", 
			"images/commands/edit/modify.gif", 
			"docs/functions/edit/modify.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handleElementSelection( controller, element );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#getInputDialog()
	 */
	@Override
	public CxDialog getInputDialog() {
		return dialog;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#onStart(constellation.Controller)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {			
		// allow the parent to update
		super.onStart( controller );
		
		// setup the dialog
		if( dialog == null ) {
			dialog = EntityEditorDialog.getInstance( controller );
		}
		
		// make the dialog visible
		dialog.setCallingFunction( this, EntityTypes.LINE );
		dialog.makeVisible();
		dialog.reset();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#onFinish(constellation.Controller)
	 */
	@Override
	public void onFinish( final ApplicationController controller ) {
		// allow the parent to update
		super.onFinish( controller );
		
		// cancel the selection of the temporary geometry
		final GeometricModel model = controller.getModel();
		
		// reset the temporary geometry
		model.clearTemporaryElement();
		
		// hide the dialog
		if( dialog != null ) {
			dialog.setVisible( false );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// get the model and click point
		final PointXY clickPoint = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) { 
			// if it's a Select
			case BUTTON_SELECT:
				final ModelElement element = 
					ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
				handleElementSelection( controller, element );
				break;
		
			// if it's an Indicate
			case BUTTON_INDICATE:
				dialog.setCoordinates( clickPoint );
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
		// get the selected element
		final ModelElement element = dialog.getSelectedElement();
		
		// determine what to do with the element
		if( element != null ) {
			// is the element a spline?
			switch( element.getType() ) {
				// attempt to move the spline
				case SPLINE:
					return deformSpline( controller, oldMousePos, newMousePos, element );
			}
		}
		
		return false;
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
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleElementSelection( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			switch( element.getType() ) {
				// is it a point?
				case POINT:
					final PointXY p = EntityRepresentationUtil.getPoint( element );
					dialog.setCoordinates( p );
					break;
					
				default:
					dialog.importSettings( element );
					break;
			}
		}
	}
	
	/**
	 * Allows the deformation of splines
	 * @param controller the given {@link ApplicationController functional controller}
	 * @param oldMousePos the given old {@link MouseClick mouse click}
	 * @param newMousePos the given new {@link MouseClick mouse click}
	 * @param splineElem the given spline {@link ModelElement spine}
	 * @return true, if the spline was successfully deformed
	 */
	private boolean deformSpline( final ApplicationController controller, 
								  final MouseClick oldMousePos, 
								  final MouseClick newMousePos,
								  final ModelElement splineElem ) {
		// get the bounds of the click area
		final RectangleXY boundary = controller.untransform( oldMousePos.getClickBounds() );
		
		// get the limit vertices as points
		final SplineXY spine = (SplineXY)splineElem.getRepresentation(); 
		final VerticesXY limits = spine.getLimits();	
		final Collection<ModelElement> points = Arrays.asList( CxModelElement.createElements( limits ) );
		
		// determine the selected point
		final ModelElement pickedPt = lookupPointByRegion( controller, boundary, points );
		
		// was a point found?
		if( pickedPt != null ) {
			// get the selected spline vertex
			final SplineXY splineXY = EntityRepresentationUtil.getSpline( splineElem );
			final PointXY p = EntityRepresentationUtil.getPoint( pickedPt );
			final PointXY vertex = splineXY.getLimitVertexAt( p.getX(), p.getY() );
			
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
				splineXY.resetCache();
				
				// drag event successful
				return true;
			}
		}
		
		return false;
	}
	
	/** 
	 * Retrieves a single point from the given screen boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @param elements the collection of elements to examine
	 * @return the resultant {@link ModelElement point} or <tt>null</tt> if no element was found
	 */
	private ModelElement lookupPointByRegion( final ApplicationController controller, 
										 final RectangleXY boundary,
										 final Collection<? extends ModelElement> elements ) {
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// perform the lookup operation
		for( final ModelElement element : elements ) {
			if( ( element.getType() == POINT ) && 
					element.intersects( boundary, matrix ) ) {
				return element;
			}
		}
		
		return null;
	}
	
}