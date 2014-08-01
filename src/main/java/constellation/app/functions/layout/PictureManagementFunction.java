package constellation.app.functions.layout;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.functions.Steps;
import constellation.ui.components.CxDialog;

/**
 * The LAYOUT::PICTURES function
 * @author lawrence.daniels@gmail.com
 */
public class PictureManagementFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Import pictures for use in this model"
	);
	private PictureManagementDialog dialog;

	/**
	 * Default constructor
	 */
	public PictureManagementFunction() {
		super( 
			"LAYOUT", "PICTURES", 
			"images/commands/layout/pictures.png", 
			"docs/functions/layout/pictures.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#getInputDialog()
	 */
	@Override
	public CxDialog getInputDialog() {
		return dialog;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#onStart(constellation.FunctionController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {			
		// setup the dialog
		if( dialog == null ) {
			dialog = new PictureManagementDialog( controller );
		}
		dialog.setVisible( true );
		dialog.reset();
		dialog.requestFocus();
		
		// allow the parent to update
		super.onStart( controller );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#onFinish(constellation.FunctionController)
	 */
	@Override
	public void onFinish( final ApplicationController controller ) {
		if( dialog != null ) {
			dialog.setVisible( false );
		}
	}
	
}