package constellation.tools.pdm.server;

import static java.lang.String.format;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandReader;
import constellation.commands.CxCommandWriter;
import constellation.tools.pdm.client.commands.PDMCommandFactory;
import constellation.tools.pdm.client.commands.SendModelsCommand;

/**
 * Product Data Manager (PDM) Client Handler Thread
 * @author lawrence.daniels@gmail.com
 */
public class PDMClientHandlerThread implements Runnable  {
	private final Logger logger = Logger.getLogger( getClass() );
	private final LinkedList<Socket> queue;
	private Thread thread;
	private boolean alive;
	
	/**
	 * Register the PDM remote commands
	 */
	static {
		PDMCommandFactory.init();
	}
	
	/**
	 * Default constructor
	 */
	public PDMClientHandlerThread( final LinkedList<Socket> queue ) {
		this.queue 	= queue;
	}
	
	/**
	 * Causes this thread to cease processing
	 */
	public void die() {
		alive = false;
		thread.interrupt();
		thread = null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while( alive ) {
			// get the next client connection
			final Socket socket = getNextConnection();
			
			// if a socket was retrieved ...
			if( socket != null ) {
				handleClient( socket );
			}
		}
	}
	
	/**
	 * Starts the thread
	 */
	public void start() {
		if( thread == null ) {
			alive = true;
			thread = new Thread( this );
			thread.start();
		}
	}
	
	/**
	 * Handles the socket connection
	 * @param socket the given {@link Socket socket} connection
	 */
	private void handleClient( final Socket socket ) {
		CxCommandReader in = null;
		CxCommandWriter out = null;
		boolean connected = true;
		
		try {
			// notify the operator
			logger.info( format( "Client connection from %s", socket.getInetAddress().getCanonicalHostName() ) );
			
			// open the input/output streams
			in = new CxCommandReader( socket.getInputStream() );
			out = new CxCommandWriter( socket.getOutputStream() );
			
			// send the model file
			// TODO remove this after testing
			out.write( SendModelsCommand.create( PDMDatabase.getInstance().getFileList() ) );
			
			// cycle indefinitely
			while( connected ) {
				// read a command
				final CxCommand command = in.read();
				
				logger.info( format( "Received command '%s'", command ) );
			}
		} 
		catch( final IOException e ) {
			logger.error( "A client error occurred", e );
		}
		finally {
			// close the streams
			try { in.close(); } catch( final IOException e ) { }
			try { out.close(); } catch( final IOException e ) { }
			
			// close the socket
			try { socket.close(); } catch( final IOException e ) { }
		}
		
		// notify the operator
		logger.info( "Client disconnected." );
	}
	
	/**
	 * Returns the next socket connection from the queue
	 * @return the {@link Socket socket} connection
	 */
	private Socket getNextConnection() {
		Socket socket = null;
		synchronized( queue ) {
			// wait for at least one element to enter the queue
			while( alive && queue.isEmpty() ) {
				try { queue.wait(); } 
				catch( final InterruptedException e ) { 
					return null;
				}
			}
			
			// is something in the queue
			if( !queue.isEmpty() ) {
				socket = queue.removeFirst();
			}
			queue.notifyAll();
		}
		return socket;
	}
	
}
