package constellation.app.functions.layout;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.functions.MouseClick;
import constellation.functions.Steps;

/**
 * The LAYOUT::FOLD function
 * @see http://local.wasp.uwa.edu.au/~pbourke/geometry/rotate/
 * @author lawrence.daniels@gmail.com
 */
public class FoldFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select a closed element",
		"Select the folding line" 
	);
	
	/**
	 * Default constructor
	 */
	public FoldFunction() {
		super( 
			"LAYOUT", "FOLD", 
			"images/commands/layout/fold.gif", 
			"docs/functions/layout/fold.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		controller.showErrorDialog( "Not yet implemented", "Function Error" );
	}
}