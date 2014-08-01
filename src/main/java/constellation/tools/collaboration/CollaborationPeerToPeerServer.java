package constellation.tools.collaboration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.tools.collaboration.commands.CollaborativeCommandFactory;
import constellation.tools.collaboration.components.HostSessionDialog;

/**
 * Constellation Collaborative Design Server (Peer to Peer Only)
 * @author lawrence.daniels@gmail.com
 */
public class CollaborationPeerToPeerServer {
	private final Logger logger = Logger.getLogger( getClass() ); 
	private final ApplicationController controller;
	private ServerSocket serverSocket;
	private ConnectionListener listener;
	private CollaborativeGeometricModel model;
	private int port;

	/** 
	 * Creates a new instance of the sharing server
	 * @param controller the given {@link ApplicationController controller}
	 * @param port the given listen port number
	 */
	public CollaborationPeerToPeerServer( final ApplicationController controller, final int port ) {
		this.controller	= controller;
		this.port 		= port;
	}
	
	/** 
	 * Starts the collaboration server
	 * @throws IOException 
	 */
	public void start() 
	throws IOException {
		// create the listener socket
		serverSocket = new ServerSocket( port );
		
		// start the listener thread
		listener = new ConnectionListener();
		listener.start();
	}
	
	/** 
	 * Stop the collaboration server
	 */
	public void stop() {
		// shutdown the listener thread
		if( listener != null ) {
			listener.die();
			listener = null;
		}
		
		// close the listener socket
		if( serverSocket != null ) {
			try { serverSocket.close(); } catch( final Exception e ) { }
			serverSocket = null;
		}
	}
	
	/** 
	 * Connection Listener
	 * @author lawrence.daniels@gmail.com
	 */
	private class ConnectionListener implements Runnable {
		private boolean alive;
		private Thread thread;
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while( alive ) {
				try {
					// get the client socket
					final Socket socket = serverSocket.accept();
					logger.info( "Got connection from " + socket.getInetAddress().getHostName() );
					
					// if not already connected ...
					if( model == null ) {
						// create a new server model
						model = new CollaborativeGeometricModel( controller, controller.getModel(), socket, new SessionCloseListener(), true );
						controller.setModel( model );
						
						// set the connection status
						HostSessionDialog.getInstance( controller ).setConnectionStatus( true );
						
						// synchronize with the remote host
						model.queue( CollaborativeCommandFactory.createModelSynchronizationCommands( model ) );
					}
					
					// otherwise, refuse the connection
					else {
						logger.warn( "The maximum number of connections has been reached" );
						try { socket.close(); }
						catch( final Exception e ) {
							logger.error( "Error closing connection", e );
						}
					}
				}
				catch( final SocketException e ) {
					logger.error( "Error getting connection", e );
					if( e.getMessage().equals( "Socket closed" ) ) {
						alive = false;
					}
				}
				catch( final Exception e ) {
					logger.error( "Error getting connection", e );
					
					// set the connection status
					HostSessionDialog.getInstance( controller ).setConnectionStatus( false );
				}
			}
			
			// disconnect the networked model
			if( model != null ) {
				model.close();
				model = null;
			}
			
			// set the connection status
			HostSessionDialog.getInstance( controller ).setConnectionStatus( false );
		}
		
		/** 
		 * Starts the thread
		 */
		public void start() {
			if( thread == null ) {
				alive = true;
				thread = new Thread( this );
				thread.setDaemon( true );
				thread.start();
			}
		}

		/** 
		 * Stops the thread
		 */
		public void die() {
			alive = false;
			if( thread != null ) {
				thread.interrupt();
			}
		}
	}
	
	/** 
	 * Collaborative Session Closure Listener
	 * @author lawrence.daniels@gmail.com
	 */
	private class SessionCloseListener implements SessionDisconnectCallBack {

		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.collaboration.SessionDisconnectCallBack#disconnected()
		 */
		public void disconnected() {
			HostSessionDialog.getInstance( controller ).setConnectionStatus( false );
		}
	}
	
}
