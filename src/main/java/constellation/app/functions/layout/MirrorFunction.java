package constellation.app.functions.layout;

import static constellation.app.math.ElementDetectionUtil.lookupElementByRegion;
import static constellation.app.math.ElementDetectionUtil.lookupLineByRegion;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LAYOUT::MIRROR function
 * @author lawrence.daniels@gmail.com
 */
public class MirrorFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
			"Select the #line to use as the mirroring plane",
			"Select the #element to be mirrored or Indicate to end selection"
	);
	private LineXY plane;
	
	/**
	 * Default constructor
	 */
	public MirrorFunction() {
		super( 
			"LAYOUT", "MIRROR", 
			"images/commands/layout/mirror.gif", 
			"docs/functions/layout/mirror.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleMirroringPlaneSelection( controller, element ); break;
			case STEP_2: handleElementSelection( controller, element ); break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1( controller, mouseClick ); break;
			case STEP_2: handleStep2( controller, mouseClick ); break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		super.processMouseMovement( controller, mousePos );
		
		if( steps.currentIndex() >= STEP_2 ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// set the temporary plane (infinite line)
			model.setTemporaryElement( plane );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/** 
	 * Handles Step 1: Select the geometry to be mirrored or Indicate to end selection
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// process the click
		switch( mouseClick.getButton() ) {
			// selecting a line ...
			case BUTTON_SELECT:
				// determine the selected line	
				final ModelElement line = lookupLineByRegion( controller, mouseClick );
				
				// select the line
				handleMirroringPlaneSelection( controller, line );
				break;
		}
	}

	/** 
	 * Handles Step 2: Select the line to use as the mirroring plane
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( mouseClick.getButton() ) {
			// selecting some geometry ...
			case BUTTON_SELECT:
				// determine the selected element			
				final ModelElement element = lookupElementByRegion( controller, mouseClick );
				
				// select the element
				handleElementSelection( controller, element );
				break;
		
			// move the selected points ...
			case BUTTON_INDICATE:
				advanceToNextStep( controller );
				break;
		}
	}
	
	/**
	 * Handles the selection of an element to mirror
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleElementSelection( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// perform the mirroring action
			mirror( controller, element );
		}
	}
	
	/**
	 * Handles the selection of an mirroring plane (line)
	 * @param controller the given {@link ApplicationController controller}
	 * @param planeElem the given mirroring plane {@link ModelElement element}
	 */
	private void handleMirroringPlaneSelection( final ApplicationController controller, final ModelElement planeElem ) {
		if( planeElem != null ) {
			// cache the plane
			this.plane = LineXY.createInfiniteLine( EntityRepresentationUtil.getLine( planeElem ) );
			
			// set the temporary plane (infinite line)
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( plane );
			
			// request a redraw
			controller.requestRedraw();
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/** 
	 * Performs the actual mirror of the elements
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given selected {@link ModelElement element}
	 */
	private void mirror( final ApplicationController controller, final ModelElement element ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the internal representation of the element
		final EntityRepresentation src = element.getRepresentation();

		// create the mirror copy of the representation
		final EntityRepresentation dest = src.mirror( plane );
		
		// add the representation to model
		model.addPhysicalElement( dest );
		
		// set the status message
		controller.setStatusMessage( format( "copied element '%s'", element ) );
	}
	
}