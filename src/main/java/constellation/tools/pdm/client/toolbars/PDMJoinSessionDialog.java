package constellation.tools.pdm.client.toolbars;

import static constellation.preferences.ServerPreferences.DEFAULT_SERVER_PORT;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JLabel;
import javax.swing.JTextField;

import constellation.ApplicationController;
import constellation.ThreadPool;
import constellation.commands.VirtualModel;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.CollaborationClient;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;
import constellation.ui.components.fields.CxIntegerField;
import constellation.ui.components.fields.CxStringField;

/**
 * Constellation PDM Join Server Session Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PDMJoinSessionDialog extends CxDialog {
	// singleton instance
	private static PDMJoinSessionDialog instance = null;
	
	// connection constants
	private static final int STATUS_CONNECTING 	 = 0;
	private static final int STATUS_CONNECTED	 = 1;
	private static final int STATUS_NOT_CONNECTED = 2;
	private static final String[] STATUSES = {
		"Connecting...", "Connected", "Not Connected"
	};
	// internal fields
	private final ThreadPool threadPool;
	private CollaborationClient sharingClient;
	private JTextField ipAddressField;
	private CxIntegerField portField;
	private JLabel statusField;
	private CxButton connectButton;
	private CxButton disconnectButton;

	/** 
	 * Creates a new host session dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	public PDMJoinSessionDialog( final ApplicationController controller ) {
		super( controller, "PDM::Join Server" );
		this.threadPool = controller.getThreadPool();
		
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the single instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the {@link PDMJoinSessionDialog dialog} instance
	 */
	public static PDMJoinSessionDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {
			// instantiate an instance of the dialog
			instance = new PDMJoinSessionDialog( controller );
			
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
		setConnectionStatus( STATUS_NOT_CONNECTED );
		
		// set the host name
		try {
			ipAddressField.setText( InetAddress.getLocalHost().getHostAddress() );
		}
		catch( final IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Indicates whether a connection to the PDM has been established
	 * @return true, if there is currently a connection in place
	 */
	public boolean isConnected() {
		// TODO PDMConnection object?
		return true;
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private CxPanel createContentPane() {
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		
		// create the 'Start' & 'Stop' buttons
		connectButton = new CxButton( "Connect", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				threadPool.queue( new ConnectTask() );
			} 
		} );
		disconnectButton = new CxButton( "Disconnect", new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				threadPool.queue( new DisconnectTask() );
			} 
		} );
		
		// row #1
		int row = -1;
		cp.attach( 0, ++row, new JLabel( "Host/IP:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, ipAddressField = new CxStringField( 15 ) );
		cp.gbc.gridwidth = 1;
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Port:")  );
		cp.gbc.gridwidth = 2;
		cp.attach( 1,   row, portField = new CxIntegerField( DEFAULT_SERVER_PORT ) );
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
			setConnectionStatus( STATUS_CONNECTING );
			final GeometricModel model = sharingClient.connect();
			
			// successful connection
			setConnectionStatus( STATUS_CONNECTED );		
			controller.setModel( model );
		}
		catch( final IOException e ) {
			controller.showErrorDialog( "PDM Server", e );
			setConnectionStatus( STATUS_NOT_CONNECTED );
		}
	}
	
	/** 
	 * Disconnects from the remote host
	 */
	private void disconnect() {
		// start the client
		sharingClient.disconnect();

		// set the status
		setConnectionStatus( STATUS_NOT_CONNECTED );
		
		// if the model is virtual, 
		// swap it for the "host" model
		if( controller.getModel().isVirtual() ) {
			final VirtualModel collaborativeModel = (VirtualModel)controller.getModel();
			final GeometricModel model = collaborativeModel.getHostModel();
			controller.setModel( model );
		}
	}
	
	/** 
	 * Sets the connection status
	 * @param status the given connection status
	 */
	private void setConnectionStatus( final int status ) {
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
				
			case STATUS_NOT_CONNECTED:
				connectButton.setEnabled( true );
				disconnectButton.setEnabled( false );
				ipAddressField.setEnabled( true );
				portField.setEnabled( true );
				break;
		}
		
		// set the status
		statusField.setText( STATUSES[status] );
		statusField.repaint();
	}
	
	/**
	 * Connect Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class ConnectTask implements Runnable {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			connect();
		}
	}

	/**
	 * Disconnect Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class DisconnectTask implements Runnable {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			disconnect();
		}
	}
	
}
