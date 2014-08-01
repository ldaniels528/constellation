package constellation.tools.pdm.server;

import static constellation.preferences.ServerPreferences.*;
import static java.lang.String.format;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import constellation.tools.pdm.CxPDMVersion;

/**
 * Constellation Server Product Data Manager (PDM) 
 * @author lawrence.daniels@gmail.com
 */
@CxPDMVersion("0.01")
public class ConstellationServer {
	private final Logger logger = Logger.getLogger( getClass() );
	private final int port;
	
	// mutable fields
	private ServerSocket listener;
	private boolean alive;
	
	/**
	 * Default constructor
	 */
	public ConstellationServer( final int port ) {
		this.port = port;
	}
	
	/**
	 * For stand alone operation
	 * @param args the given command line arguments
	 * @throws Throwable
	 */
	public static void main( final String[] args ) 
	throws Throwable {
		// get the port parameter (optional)
		final int port = ( args.length > 0 ) ? Integer.parseInt( args[0] ) : DEFAULT_SERVER_PORT;
		
		// start the daemon
		( new ConstellationServer( port ) ).execute();
	}

	/**
	 * Executes the application
	 * @throws IOException 
	 */
	public void execute() 
	throws IOException {		
		// display the server version
		final CxPDMVersion version = ConstellationServer.class.getAnnotation( CxPDMVersion.class );
		logger.info( format( "Constellation PDM Server v%s", version.value() ) );
		
		// capture the start time
		final long startTime = System.currentTimeMillis();
		
		// startup the listener
		logger.info( format( "Binding to port %d...", port ) );
		listener = new ServerSocket( port );
		
		// startup the thread manager
		final PDMThreadManager manager = new PDMThreadManager( 3 );
		manager.init();
		
		// capture server start up elapsed time
		final long elapsedTime = System.currentTimeMillis() - startTime;
		
		// report up state
		logger.info( format( "Server started up in %d msecs", elapsedTime ) );
		
		// cycle indefinitely
		alive = true;
		while( alive ) {
			try {
				// attempt to get a client connection
				final Socket socket = listener.accept();
				
				// queue the connection for processing
				manager.queue( socket );
			}
			catch( IOException e ) {
				logger.error( "An I/O error occured", e );
			}
		}
		
		// shutdown the thread manager
		logger.info( "Shutting down..." );
		manager.shutdown();
	}

}
