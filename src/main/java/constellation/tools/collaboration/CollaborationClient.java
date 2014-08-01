package constellation.tools.collaboration;

import java.io.IOException;
import java.net.Socket;

import constellation.ApplicationController;
import constellation.model.DefaultGeometricModel;

/**
 * Constellation Collaborative Design Client
 * @author lawrence.daniels@gmail.com
 */
public class CollaborationClient implements SessionDisconnectCallBack {
	private final ApplicationController controller;
	private RemoteGeometricModel model;
	private Socket socket;
	private String host;
	private int port;

	/** 
	 * Creates a new instance of the sharing client
	 * @param controller the given {@link ApplicationController controller}
	 * @param host the given host name or IP Address
	 * @param port the given listen port
	 */
	public CollaborationClient( final ApplicationController controller, 
						  		final String host, 
						  		final int port ) {
		this.controller	= controller;
		this.host 		= host;
		this.port 		= port;
	}
	
	/** 
	 * Starts the collaboration client
	 * @return the {@link RemoteGeometricModel collaborative model}
	 * @throws IOException 
	 */
	public synchronized RemoteGeometricModel connect() 
	throws IOException {
		// open the socket connection
		socket = new Socket( host, port );
		
		// create a remotely connected model
		model = new CollaborativeGeometricModel( controller, DefaultGeometricModel.newModel(), socket, this, false );
		
		// return the remotely connected model
		return model;
	}
	
	/** 
	 * Stop the collaboration client
	 */
	public synchronized void disconnect() {
		if( model != null ) {
			model.close();
			model = null;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.collaboration.SessionDisconnectCallBack#disconnected()
	 */
	public void disconnected() {
		//disconnect();
	}
	
}
