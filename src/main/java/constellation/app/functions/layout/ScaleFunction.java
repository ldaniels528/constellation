package constellation.app.functions.layout;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.functions.MouseClick;
import constellation.functions.Steps;

/**
 * The LAYOUT::SCALE function
 * See <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/rotate/">Reference</a>
 * @author lawrence.daniels@gmail.com
 */
public class ScaleFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select a point",
		"Select a point or Indicate to scale the selected points" 
	);
	
	/**
	 * Default constructor
	 */
	public ScaleFunction() {
		super( 
			"LAYOUT", "SCALE", 
			"images/commands/layout/scale.png", 
			"docs/functions/layout/scale.html", 
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