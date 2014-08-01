package constellation.ui.components;

import javax.swing.JDialog;

import org.apache.log4j.Logger;

import constellation.ApplicationController;

/**
 * Constellation Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxDialog extends JDialog {
	protected static final Logger logger = Logger.getLogger( CxDialog.class );
	protected final ApplicationController controller;
	
	/**
	 * Creates a new dialog
	 * @param controller the given {@link ApplicationController controller}
	 * @param title the given title of the dialog
	 */
	public CxDialog( final ApplicationController controller, final String title ) {
		super( controller.getFrame(), title );
		super.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		super.setResizable( false );
		this.controller = controller;
	}

	/** 
	 * Returns the controller
	 * @return the {@link ApplicationController controller}
	 */
	public ApplicationController getController() {
		return controller;
	}
	
	/**
	 * Makes the dialog visible and requests focus
	 */
	public void makeVisible() {
		// make the dialog
		if( !this.isShowing() ) {
			this.setVisible( true );
		}
		
		// request focus
		this.requestFocus();
	}
	
}
