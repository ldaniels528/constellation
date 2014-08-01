package constellation.app.functions.line;

import constellation.ApplicationController;

/**
 * The LINE::PT-TO-PT (Infinite Line Version) function
 * @author lawrence.daniels@gmail.com
 */
public class LinePtToPtInfiniteFunction extends LinePtToPtFunction {
	
	/**
	 * Default constructor
	 */
	public LinePtToPtInfiniteFunction() {
		super();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.StructureSelectionDialogFunction#onStart(constellation.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event
		super.onStart( controller );
		
		// set the line to infinite 
		setInfiniteLine( true );
	}
}