package constellation.app.functions.edit;

import static constellation.app.math.ElementDetectionUtil.lookupPhantomElementByRegion;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.SelectionMode;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The EDIT::RESTORE function
 * @author lawrence.daniels@gmail.com
 */
public class RestoreFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps( 
		"Select elements to undelete them" 
	);
	
	/**
	 * Default constructor
	 */
	public RestoreFunction() {
		super( 
			"EDIT", "RESTORE", 
			"images/commands/edit/restore.gif", 
			"docs/functions/edit/restore.html", 
			STEPS 
		);
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.ApplicationController, constellation.drawing.ModelElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handleElementSection( controller, element );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// change the selection mode
		controller.setSelectionMode( SelectionMode.PHANTOM_ELEMENTS );
		
		// redraw the scene
		controller.requestRedraw();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onFinish(constellation.functions.ApplicationController)
	 */
	@Override
	public void onFinish( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onFinish( controller );
		
		// change the selection mode
		controller.setSelectionMode( SelectionMode.PHYSICAL_ELEMENTS );
		
		// redraw the scene
		controller.requestRedraw();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected an element
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement element = 
					lookupPhantomElementByRegion( controller, mouseClick );
				
				// handle the element
				handleElementSection( controller, element );
				break;
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link PointXY click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleElementSection( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// remove the element from the list of phantom elements
			model.erasePhantom( element );
			
			// re-add the element to the model
			model.addPhysicalElement( element );
			
			controller.setStatusMessage( format( "Restored element '%s'", element.getLabel() ) );
			
			// request a redraw
			controller.requestRedraw();
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}	
		
}