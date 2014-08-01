package constellation.tools.collaboration.components;

import static constellation.preferences.ServerPreferences.DEFAULT_PEER_TO_PEER_PORT;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.Icon;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.tools.collaboration.CollaborationPeerToPeerServer;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;
import constellation.ui.components.fields.CxIntegerField;

/**
 * Constellation Host Peer-to-Peer Session Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class HostSessionDialog extends CxDialog {
	// singleton instance
	private static HostSessionDialog instance = null;
	
	// image definitions
	private static final CxContentManager CONTENT_MANAGER = CxContentManager.getInstance(); 
	private static final Icon CONNECTED_ICON 	= CONTENT_MANAGER.getIcon( "images/extensions/collaborate/connected.png" );
	private static final Icon UNCONNECTED_ICON 	= CONTENT_MANAGER.getIcon( "images/extensions/collaborate/disconnected.png" );
	
	// internal fields
	private CollaborationPeerToPeerServer sharingServer;
	private JLabel ipAddressField;
	private CxIntegerField portField;
	private JLabel statusField;
	private JLabel statusIcon;
	private CxButton startButton;
	private CxButton stopButton;

	/** 
	 * Creates a new host session dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	private HostSessionDialog( final ApplicationController controller ) {
		super( controller, "Host Collaborative Session" );
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
		super.pack();
		
		// position the dialog
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the single instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the {@link HostSessionDialog dialog} instance
	 */
	public static HostSessionDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {
			// instantiate an instance of the dialog
			instance = new HostSessionDialog( controller );
			
			// initialize the dialog
			instance.init();
		}
		return instance;
	}
	
	/** 
	 * Initializes the dialog
	 */
	public void init() {
		// set the initial status
		setListeningStatus( false );
		
		// set the host name
		try {
			ipAddressField.setText( InetAddress.getLocalHost().getHostAddress() );
		}
		catch( final IOException e ) {
			logger.error( "Error obtaining host address",  e );
		}
	}
	
	/** 
	 * Sets the current connection status
	 * @param connected indicates whether or not a connection is present
	 */
	public void setConnectionStatus( final boolean connected ) {
		statusIcon.setIcon( connected ? CONNECTED_ICON : UNCONNECTED_ICON );
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private CxPanel createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		cp.gbc.fill = GridBagConstraints.NONE;
		
		// create the 'Start' button
		startButton = new CxButton( "Start", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				startServer();
			} 
		} );
		
		// create the 'Stop' button
		stopButton = new CxButton( "Stop", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				stopServer();
			} 
		} );
		
		// row #1, column #1-2
		int row = -1;
		cp.attach( 0, ++row, new JLabel( "Host/IP:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, ipAddressField = new JLabel() );
		cp.gbc.gridwidth = 1;
		
		// row #1, column 4
		cp.gbc.gridheight = 4;
		cp.attach( 3, row, statusIcon = new JLabel( UNCONNECTED_ICON ) );
		cp.gbc.gridheight = 1;
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Port:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, portField = new CxIntegerField( DEFAULT_PEER_TO_PEER_PORT ) );
		cp.gbc.gridwidth = 1;
		
		// row #3
		cp.attach( 0, ++row, new JLabel( "Status:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, statusField = new JLabel() );
		cp.gbc.gridwidth = 1;
		
		// row #4
		cp.attach( 1, ++row, startButton );
		cp.attach( 2,   row, stopButton );
		return cp;
	}

	/** 
	 * Attempts to starts the server
	 */
	private void startServer() {
		// get the port number
		final int port = portField.getInteger();
		
		// initialize the sharing server
		sharingServer = new CollaborationPeerToPeerServer( controller, port );
		try {
			// start the server
			sharingServer.start();
			setListeningStatus( true );
		}
		catch( final IOException e ) {
			controller.showErrorDialog(  "Sharing Server", e );
		}
	}
	
	/** 
	 * Stops the server
	 */
	private void stopServer() {
		// start the server
		sharingServer.stop();

		// set the status
		setListeningStatus( false );
	}
	
	/** 
	 * Sets the server status
	 * @param started indicates whether the status is 'Started' or 'Stopped'
	 */
	private void setListeningStatus( final boolean started ) {
		startButton.setEnabled( !started );
		stopButton.setEnabled( started );
		portField.setEnabled( !started );
		statusField.setText( started ? "Started" : "Stopped" );
	}
	
}
