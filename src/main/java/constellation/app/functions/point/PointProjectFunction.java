package constellation.app.functions.point;

import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The POINT::PROJECT function
 * @author lawrence.daniels@gmail.com
 */
public class PointProjectFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Select a #line or #curve",
		"Select the #point to project"
	);
	
	/**
	 * Default constructor
	 */
	public PointProjectFunction() {
		super( 
			"PROJECT", 
			"images/commands/point/point-project.png", 
			"docs/functions/point/project.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.functions.ApplicationController, constellation.drawing.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			switch( steps.currentIndex() ) {
				case STEP_1: handleStep1_SelectHostElement( controller, element ); break;
				case STEP_2: handleStep2_PointSelection( controller, element ); break;
			}
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#process(constellation.commands.FunctionController, constellation.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the current step
		switch( steps.currentIndex() ) {
			// lookup all complex elements within the click boundary
			case STEP_1: 
				final ModelElement element = 
					ElementDetectionUtil.lookupComplexElementByRegion( controller, mouseClick );
				handleStep1_SelectHostElement( controller, element );
				break;
				
			// was a geometry element selected?
			case STEP_2: 
				final ModelElement point = 
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep2_PointSelection( controller, point ); 
				break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep1_SelectHostElement( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the element
			model.selectGeometry( element ); 
			
			// notify the operator
			controller.setStatusMessage( format( "Selected element '%s'", element ) );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handleStep2_PointSelection( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// get the selected elements
			final ModelElement[] elements = new ModelElement[1];
			model.getSelectedGeometry( elements );
			
			// get the selected lines/curve
			final ModelElement element = elements[0];
			final PointXY vertex = projectPoint( EntityRepresentationUtil.getPoint( point ), element );
	
			// add the point to the model
			if( vertex != null ) {
				addPointToModel( controller, vertex );
			}
		}
	}

	/**
	 * Attempts to determine the intersection point between the given
	 * geometric elements.
	 * @param pointToProject the given {@link PointXY point} to project.
	 * @param element the given {@link ModelElement geometric element}
	 * @return the intersection point or <tt>null</tt> if no intersection exists
	 */
	private PointXY projectPoint( final PointXY pointToProject, final ModelElement element ) {
		PointXY p = null;
		switch( element.getType() ) {
			case LINE:
				final LineXY line = EntityRepresentationUtil.getLine( element );
				p = PointXY.getProjectedPoint( pointToProject, line );
				break;
			
			case CIRCLE:
				final CurveXY curve = EntityRepresentationUtil.getCurve( element );
				p = PointXY.getProjectedPoint( pointToProject, curve );
				break;	
				
			default:
				logger.error( format( "Error projecting unhandled type '%s'", element.getType() ) );
		}
		
		// no intersection found
		return p;
	}	

}