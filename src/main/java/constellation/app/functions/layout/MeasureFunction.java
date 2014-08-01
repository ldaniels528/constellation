package constellation.app.functions.layout;

import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.functions.MouseClick;
import constellation.functions.Steps;

/**
 * The LAYOUT::MEASURE function
 * @author lawrence.daniels@gmail.com
 */
public class MeasureFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps( 
			"Select a #point, #line, or #curve",
			"Select a second #point, #line, or #curve"	 
		);
	
	/**
	 * Default constructor
	 */
	public MeasureFunction() {
		super( 
			"LAYOUT", "MEASURE", 
			"images/commands/layout/measure.gif", 
			"docs/functions/layout/measure.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		controller.showErrorDialog( format( "%s::%s", getFamilyName(), getName() ), "Function not implemented" );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.AbstractFunction#processMouseClick(constellation.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		
	}

}
