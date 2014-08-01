package constellation.app.functions.point;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import constellation.ApplicationController;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * The POINT::MID-POINT function
 * @author lawrence.daniels@gmail.com
 */
public class MidPointFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Select the #line, #curve, or starting #point",
		"Select the ending #point"
	);
	
	/**
	 * Default constructor
	 */
	public MidPointFunction() {
		super( 
			"MID-POINT", 
			"images/commands/point/midpoint.gif", 
			"docs/functions/point/midpoint.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
		case STEP_1: handleStep1_SelectElementOrPoint( controller, element ); break;
		case STEP_2: handleStep2_SelectEndPoint( controller, element );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick, constellation.geometry.CameraModes)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, 
								   final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: handleStep1( controller, mouseClick ); break;
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
		}		
	}
	
	/**
	 * Handles step #1: Select the host line or curve or start point
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {			
			// determine the line to select
			final ModelElement element = 
				ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
			
			// handle the element
			handleStep1_SelectElementOrPoint( controller, element );
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep1_SelectElementOrPoint( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// handle the element
			PointXY midPoint = null;
			switch( element.getType() ) {
				// for points
				case POINT:
					midPoint = (PointXY)element.getRepresentation();
					select( controller, element );
					advanceToNextStep( controller );
					break;
					
				case LINE:
					final LineXY line = EntityRepresentationUtil.getLine( element );
					addPointToModel( controller, midPoint = line.getMidPoint() );
					break;
					
				// for everything else
				default:
					// get the internal representation
					final EntityRepresentation ir = element.getRepresentation();
					
					// is the element a curve?
					if( ir instanceof CurveXY ) {
						final CurveXY curve = (CurveXY)ir;
						addPointToModel( controller, midPoint = curve.getMidPoint() );
					}
					
					// must be a text note, picture, etc.
					else {
						// create the midpoint
						final MatrixWCStoSCS matrix = controller.getMatrix();
						midPoint = ir.getBounds( matrix ).getMidPoint();
						addPointToModel( controller, midPoint );
					}
			}
		}
	}

	/**
	 * Handles step #1: Select the host line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// determine the point to select
			final ModelElement endPoint = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
			
			// handle the selection
			handleStep2_SelectEndPoint( controller, endPoint );
		}
	}
	
	/**
	 * Handles step #2: Select the end point
	 * @param controller the given {@link ApplicationController controller}
	 * @param ModelElement the given {@link ModelElement end point}
	 */
	private void handleStep2_SelectEndPoint( final ApplicationController controller, final ModelElement endPoint ) {
		if( endPoint != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// get the selected elements
			final ModelElement[] elements = new ModelElement[1];
			model.getSelectedGeometry( elements );
			
			// get the start point
			final ModelElement startPoint = elements[0];
			
			// get the internal representations
			final PointXY sp = EntityRepresentationUtil.getPoint( startPoint );
			final PointXY ep = EntityRepresentationUtil.getPoint( endPoint );
		
			// compute the midpoint
			final PointXY midPoint = PointXY.getMidPoint( sp, ep );
			
			// add the point to the model
			addPointToModel( controller, midPoint );
		}
	}
	
}