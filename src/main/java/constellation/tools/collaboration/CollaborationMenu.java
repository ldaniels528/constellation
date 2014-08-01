package constellation.tools.collaboration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenu;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.commands.RemoteMessagingDialog;
import constellation.tools.collaboration.components.HostSessionDialog;
import constellation.tools.collaboration.components.JoinSessionDialog;
import constellation.ui.components.menu.CxMenuItem;

/**
 * Constellation Collaborative Design Menu
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CollaborationMenu extends JMenu {
	// get the content manager instance
	private final CxContentManager contentManager = CxContentManager.getInstance();
	
	// internal fields
	private final ApplicationController controller;
	
	// icon declarations
	private final Icon CHAT_ICON			= contentManager.getIcon( "images/extensions/collaborate/chat.png" ); 
	private final Icon MAIN_ICON			= contentManager.getIcon( "images/extensions/collaborate/main.gif" );
	private final Icon HOST_SESSION_ICON	= contentManager.getIcon( "images/extensions/collaborate/host-session.gif" );
	private final Icon JOIN_SESSION_ICON	= contentManager.getIcon( "images/extensions/collaborate/join-session.png" );
	
	/**
	 * Default Constructor
	 */
	public CollaborationMenu( final ApplicationController controller ) {
		super( "Collaboration" );
		super.setIcon( MAIN_ICON );
		super.setToolTipText( "Collaboratively create models" );
		
		// save the controller instance
		this.controller = controller;

		// add the 'Collaboration' menu
		super.add( new CxMenuItem( "Host Session", HOST_SESSION_ICON, new HostSessionAction() ) );
		super.add( new CxMenuItem( "Join Session", JOIN_SESSION_ICON, new JoinSessionAction() ) );
		super.add( new CxMenuItem( "Messaging Client", CHAT_ICON, new MessagingAction() ) );
	}
	
	/**
	 * Host Collaborative Selection Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class HostSessionAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get an instance of the dialog
			final HostSessionDialog hostSessionDialog = HostSessionDialog.getInstance( controller );
			
			// show the dialog
			hostSessionDialog.makeVisible();
		}	
	}
	
	/**
	 * Join Collaborative Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class JoinSessionAction implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get an instance of the dialog
			final JoinSessionDialog joinSessionDialog = JoinSessionDialog.getInstance( controller );
			
			// show the dialog
			joinSessionDialog.makeVisible();
		}	
	}
	
	/** 
	 * Messaging Dialog Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class MessagingAction implements ActionListener {
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// is the model collaborative?
			if( model instanceof RemoteGeometricModel ) {
				// cast the model to the appropriate type
				final RemoteGeometricModel remoteModel = (RemoteGeometricModel)model;
				
				// get the messaging dialog
				final RemoteMessagingDialog messagingDialog = remoteModel.getMessagingDialog();
				messagingDialog.makeVisible();
			}
			else {
				controller.showMessageDialog( 
					"You must first establish a shared connection", 
					"Sharing Error"
				);
			}
		}
	}
	
}
