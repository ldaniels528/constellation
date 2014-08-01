package constellation.app.functions.layout;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.functions.MouseClick;
import constellation.functions.Steps;

/**
 * The LAYOUT::ROTATE function
 * See <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/rotate/">Reference</a>
 * @author lawrence.daniels@gmail.com
 */
public class RotateFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select a point",
		"Select a point or Indicate to rotate the selected points" 
	);
	
	/**
	 * Default constructor
	 */
	public RotateFunction() {
		super( 
			"LAYOUT", "ROTATE", 
			"images/commands/layout/rotate.png", 
			"docs/functions/layout/rotate.html", 
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