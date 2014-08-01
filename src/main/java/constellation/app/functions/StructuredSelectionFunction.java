package constellation.app.functions;

import constellation.ApplicationController;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * Multiple Step Selection Function
 * @author lawrence.daniels@gmail.com
 */
public abstract class StructuredSelectionFunction extends AbstractFunction {
	protected Steps steps;
	
	/** 
	 * Creates a structured selection function instance
	 * @param familyName the name of the function's family
	 * @param functionName the name of the sub-function
	 * @param iconPath the given icon path
	 * @param helpPath the given help path
	 * @param steps the {@link Steps instructional steps}
	 */
	protected StructuredSelectionFunction( final String familyName, 
										   final String functionName, 
										   final String iconPath, 
										   final String helpPath, 
										   final Steps steps ) {
		this( familyName, functionName, iconPath, helpPath, steps, InputDialog.class );
	}
	
	/** 
	 * Creates a structured selection function instance
	 * @param familyName the name of the function's family
	 * @param functionName the name of the sub-function
	 * @param iconPath the given icon path
	 * @param helpPath the given help path
	 * @param steps the {@link Steps instructional steps}
	 * @param dialogClass the given {@link Class dialog class}
	 */
	protected StructuredSelectionFunction( final String familyName, 
										   		final String functionName, 
										   		final String iconPath, 
										   		final String helpPath,
										   		final Steps steps, 
										   		final Class<? extends InputDialog> dialogClass ) {
		super( familyName, functionName, iconPath, helpPath, dialogClass );
		this.steps 	= steps;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// reset the step indicator
		steps.reset();
		
		// set the status message
		setInstruction( controller );	
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#supportsSelection()
	 */
	@Override
	public boolean supportsSelection() {
		return true;
	}
	
	/**
	 * Advances to the next step
	 * @param controller the given {@link ApplicationController controller}
	 */
	protected void advanceToNextStep( final ApplicationController controller ) {
		// advance the step
		if( steps.next() ) {
			// reset the step position
			steps.reset();
			
			// call the "onStart" method
			this.onStart( controller );
			
			// clear selected and picked geometry
			final GeometricModel model = controller.getModel();
			model.clearPickedElement();
			model.clearSelectedElements();
			model.clearTemporaryElement();
			
			// request a redraw
			controller.requestRedraw();
		}
		
		// set the status message
		setInstruction( controller );
		
		// call onStep
		onStepChange( controller );
	}
	
	/**
	 * Called at each advance to the next step
	 * @param controller the given {@link ApplicationController controller}
	 */
	protected void onStepChange( final ApplicationController controller ) {
		// This method may be overridden
	}
	
	/**
	 * Sets instruction for step for the given step index
	 * @param controller the given {@link ApplicationController controller}
	 */
	protected void setInstruction( final ApplicationController controller ) {
		controller.setInstructionalSteps( steps );
	}
	
	/**
	 * Setups a new set of steps
	 * @param controller the given {@link ApplicationController controller}
	 * @param steps the given {@link Steps steps}
	 */
	protected void setSteps( final ApplicationController controller, final Steps steps ) {
		// capture the new steps
		this.steps = steps;
		
		// restart the function
		onStart( controller );
	}
}