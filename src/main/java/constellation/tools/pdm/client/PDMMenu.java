package constellation.tools.pdm.client;

import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_O;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenu;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.tools.pdm.client.toolbars.PDMJoinSessionDialog;
import constellation.tools.pdm.client.toolbars.PDMOpenServerModelDialog;
import constellation.ui.components.menu.CxMenuItem;
import constellation.util.OSPlatformUtil;

/**
 * Constellation Product Data Management (PDM) Menu
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PDMMenu extends JMenu {
	// get the meta key for the host Operating System
	private static final int META_KEY 	= OSPlatformUtil.getMetaKey();
	
	// get the content manager instance
	private final CxContentManager contentManager = CxContentManager.getInstance();
	
	// icon declarations
	private final Icon PDM_ICON 	= contentManager.getIcon( "images/extensions/PDM/PDM24.gif" );
	private final Icon CONNECT_ICON = contentManager.getIcon( "images/extensions/PDM/PDMConnect24.gif" );
	private final Icon OPEN_ICON 	= contentManager.getIcon( "images/extensions/PDM/PDMFileOpen24.png" );
	
	// internal fields
	private final ApplicationController controller;
	
	/**
	 * Default Constructor
	 */
	public PDMMenu( final ApplicationController controller ) {
		super( "PDM" );
		super.setIcon( PDM_ICON );
		super.setToolTipText( "Product Data Management" );
		
		// save the controller instance
		this.controller = controller;
		
		// add the menu items
		super.add( new CxMenuItem( "Join Server", CONNECT_ICON, new PDMJoinServerAction() ) );
		super.add( new CxMenuItem( "Open Model", OPEN_ICON, getKeyStroke( VK_O, SHIFT_MASK | META_KEY ), new PDMOpenModelAction() ) );
	}
	
	/**
	 * PDM Join Server Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class PDMJoinServerAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			final PDMJoinSessionDialog pdmSessionDialog = PDMJoinSessionDialog.getInstance( controller );
			pdmSessionDialog.makeVisible();
		}	
	}
	
	/**
	 * PDM Open Model Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class PDMOpenModelAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get an instance of the join session dialog
			final PDMJoinSessionDialog pdmSessionDialog = PDMJoinSessionDialog.getInstance( controller );
			
			// make sure there is a connection to the PDM Server
			if( !pdmSessionDialog.isConnected() ) {
				controller.showErrorDialog(
					"There is currently no connection to the PDM", 
					"PDM Connection Error" 
				);
				return;
			}

			// get an instance of the open session dialog
			final PDMOpenServerModelDialog pdmOpenModelDialog = PDMOpenServerModelDialog.getInstance( controller );
			
			// update the model list
			pdmOpenModelDialog.update( PDMClient.getInstance().getModelFiles() );
			
			// show the dialog
			pdmOpenModelDialog.makeVisible();
		}
	}
	
}
