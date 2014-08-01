package constellation.tools.collaboration.components;

import static constellation.preferences.ServerPreferences.DEFAULT_PEER_TO_PEER_PORT;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ThreadPool;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.CollaborationClient;
import constellation.tools.collaboration.RemoteGeometricModel;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;
import constellation.ui.components.fields.CxIntegerField;
import constellation.ui.components.fields.CxStringField;

/**
 * Constellation Join Peer-to-Peer Session Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class JoinSessionDialog extends CxDialog {
	// singleton instance
	private static JoinSessionDialog instance = null;
	
	// connection status enumeration
	private static enum ConnectionStatus {
		STATUS_CONNECTING, STATUS_CONNECTED, STATUS_UNCONNECTED
	};
	
	// image definitions
	private static final CxContentManager CONTENT_MANAGER = CxContentManager.getInstance(); 
	private static final Icon CONNECTED_ICON 	= CONTENT_MANAGER.getIcon( "images/extensions/collaborate/connected.png" );
	private static final Icon CONNECTING_ICON 	= CONTENT_MANAGER.getIcon( "images/extensions/collaborate/connecting.png" );
	private static final Icon UNCONNECTED_ICON 	= CONTENT_MANAGER.getIcon( "images/extensions/collaborate/disconnected.png" );
	
	// define the status to icon mapping
	private static final Map<ConnectionStatus, Icon> ICONS;
	static {
		ICONS = new HashMap<ConnectionStatus, Icon>( 3 );
		ICONS.put( ConnectionStatus.STATUS_CONNECTED, CONNECTED_ICON );
		ICONS.put( ConnectionStatus.STATUS_CONNECTING, CONNECTING_ICON );
		ICONS.put( ConnectionStatus.STATUS_UNCONNECTED, UNCONNECTED_ICON );
	}
	
	// define the status to status description mapping
	private static final Map<ConnectionStatus, String> STATUSES;
	static {
		STATUSES = new HashMap<ConnectionStatus, String>( 3 );
		STATUSES.put( ConnectionStatus.STATUS_CONNECTED, "Connected" );
		STATUSES.put( ConnectionStatus.STATUS_CONNECTING, "Connecting..." );
		STATUSES.put( ConnectionStatus.STATUS_UNCONNECTED, "Not Connected" );
	}
	
	// internal fields
	private final ThreadPool threadPool;
	private CollaborationClient sharingClient;
	private JTextField ipAddressField;
	private CxIntegerField portField;
	private JLabel statusIcon;
	private JLabel statusField;
	private CxButton connectButton;
	private CxButton disconnectButton;

	/** 
	 * Creates a new host session dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	private JoinSessionDialog( final ApplicationController controller ) {
		super( controller, "Join Collaborative Session" );
		this.threadPool = controller.getThreadPool();
		
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
		super.pack();
		
		// position the dialog
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the single instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the {@link JoinSessionDialog dialog} instance
	 */
	public static JoinSessionDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {			
			// instantiate an instance of the dialog
			instance = new JoinSessionDialog( controller );
			
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
		setConnectionStatus( ConnectionStatus.STATUS_UNCONNECTED );
		
		// set the host name
		try {
			ipAddressField.setText( InetAddress.getLocalHost().getHostAddress() );
		}
		catch( final IOException e ) {
			logger.error( "Error obtaining host address",  e );
		}
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private CxPanel createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		
		// create the 'Connect' button
		connectButton = new CxButton( "Connect", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				threadPool.queue( new ConnectionTask() );
			} 
		} );
		
		// create the 'Disconnect' button
		disconnectButton = new CxButton( "Disconnect", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				threadPool.queue( new DisconnectionTask() );
			} 
		} );
		
		// row #1, column #1-2
		int row = -1;
		cp.attach( 0, ++row, new JLabel( "Host/IP:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, ipAddressField = new CxStringField( 15 ) );
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
		cp.attach( 1, ++row, connectButton );
		cp.attach( 2,   row, disconnectButton );
		return cp;
	}
	
	/** 
	 * Attempts to connect to a remote host
	 */
	private void connect() {
		// get the port number
		final String host = ipAddressField.getText();
		final int port = portField.getInteger();
		
		// initialize the sharing server
		sharingClient = new CollaborationClient( controller, host, port );
		
		try {
			// start the client
			setConnectionStatus( ConnectionStatus.STATUS_CONNECTING );
			final GeometricModel model = sharingClient.connect();
			
			// successful connection
			setConnectionStatus( ConnectionStatus.STATUS_CONNECTED );		
			controller.setModel( model );
		}
		catch( final IOException cause ) {
			controller.showErrorDialog( "Sharing Server", cause );
			setConnectionStatus( ConnectionStatus.STATUS_UNCONNECTED );
		}
	}
	
	/** 
	 * Disconnects from the remote host
	 */
	private void disconnect() {
		// start the client
		sharingClient.disconnect();

		// set the status
		setConnectionStatus( ConnectionStatus.STATUS_UNCONNECTED );
		
		// if the model is collaborative, 
		// swap it for the "host" model
		if( controller.getModel().isVirtual() ) {
			final RemoteGeometricModel collaborativeModel = (RemoteGeometricModel)controller.getModel();
			final GeometricModel model = collaborativeModel.getHostModel();
			controller.setModel( model );
		}
	}
	
	/** 
	 * Sets the connection status
	 * @param status the given connection status
	 */
	private void setConnectionStatus( final ConnectionStatus status ) {
		switch( status ) {
			case STATUS_CONNECTING:
				connectButton.setEnabled( false );
				disconnectButton.setEnabled( false );
				ipAddressField.setEnabled( false );
				portField.setEnabled( false );
				break;
				
			case STATUS_CONNECTED:
				connectButton.setEnabled( false );
				disconnectButton.setEnabled( true );
				ipAddressField.setEnabled( false );
				portField.setEnabled( false );
				break;
				
			case STATUS_UNCONNECTED:
				connectButton.setEnabled( true );
				disconnectButton.setEnabled( false );
				ipAddressField.setEnabled( true );
				portField.setEnabled( true );
				break;
		}
		
		// set the status
		statusField.setText( STATUSES.get( status ) );
		statusIcon.setIcon( ICONS.get( status ) );
		statusField.repaint();
		statusIcon.repaint();
	}
	
	/**
	 * Session Connection Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class ConnectionTask implements Runnable {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			connect();
		}
	}

	/**
	 * Session Disconnection Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class DisconnectionTask implements Runnable {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			disconnect();
		}
	}
	
}