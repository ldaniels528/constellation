package constellation.app.functions.point;

import static constellation.drawing.EntityTypes.POINT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.CxIntersectionUtil.Intersection;
import constellation.model.GeometricModel;

/**
 * The POINT::INTERSECT function
 * @author lawrence.daniels@gmail.com
 */
public class PointIntersectionFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Select primary #line or #curve",
		"Select secondary #line or #curve"
	);
	
	/**
	 * Default constructor
	 */
	public PointIntersectionFunction() {
		super( 
			"INTERSECT", 
			"images/commands/point/intersection.png", 
			"docs/functions/point/intersect.html", 
			STEPS 
		);
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// the element can't be a point
			if( element.getType() == POINT ) {
				controller.setStatusMessage( "Point elements are not allowed" );
			}
			
			// handle the current step
			else {
				switch( steps.currentIndex() ) {
					case STEP_1: handleStep1( controller, element ); break;
					case STEP_2: handleStep2( controller, element ); break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// attempt to select an element
		final ModelElement element = 
			ElementDetectionUtil.lookupComplexElementByRegion( controller, mouseClick );
			
		// handle the element selection
		elementSelected( controller, element );
	}
	
	/**
	 * Handles step #1: Select a Line or Curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep1( final ApplicationController controller, final ModelElement element ) {
		// select the element
		select( controller, element );
		
		// advance to the next step
		advanceToNextStep( controller );
	}
	
	/**
	 * Handles step #2: Select a Line or Curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep2( final ApplicationController controller, final ModelElement element ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// select the element
		select( controller, element );
		
		// get the selected elements
		final ModelElement[] elements = new ModelElement[2];
		model.getSelectedGeometry( elements );
		
		// cache the geometric elements
		final ModelElement elementA = elements[0];
		final ModelElement elementB = elements[1];
		
		// get the geometric representations of the elements
		final EntityRepresentation repA = elementA.getRepresentation();
		final EntityRepresentation repB = elementB.getRepresentation(); 
		
		// get the intersection points
		final Intersection solution = PointXY.intersection( repA, repB );
		
		// was a solution found?
		if( !solution.hasError() ) {
			// add the points to the model
			model.addPhysicalElement( CxModelElement.createElements( solution.getPointArray() ) );
			
			// notify the user
			if( solution.size() == 1 ) {
				controller.setStatusMessage( format( "created point at %s", solution.getPoints().get( 0 ) ) );
			}
			else {
				controller.setStatusMessage( format( "%d point(s) created", solution.size() ) );
			}
		}
		
		// display the status message
		else {
			controller.setStatusMessage( solution.getErrorMessage() );
		}
		
		// advance to the next step
		advanceToNextStep( controller );
	}
	
}