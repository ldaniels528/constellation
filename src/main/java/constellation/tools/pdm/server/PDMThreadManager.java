package constellation.tools.pdm.server;

import java.net.Socket;
import java.util.LinkedList;

/**
 * Product Data Manager (PDM) Thread Manager
 * @author lawrence.daniels@gmail.com
 */
public class PDMThreadManager {
	private final LinkedList<PDMClientHandlerThread> threads;
	private final LinkedList<Socket> queue;

	/**
	 * Creates a new PDM Thread Manager instance
	 * @param intialThreads the number of initial threads
	 */
	public PDMThreadManager( final int intialThreads ) {
		this.queue	 = new LinkedList<Socket>();
		this.threads = createThreads( queue, intialThreads );
	}
	
	/**
	 * Initializes all threads in the pool
	 */
	public void init() {
		synchronized( threads ) {
			for( final PDMClientHandlerThread thread : threads ) {
				thread.start();
			}
		}
	}
	
	/** 
	 * Queues the given socket connection for processing
	 * @param socket the given {@link Socket socket} connection 
	 */
	public void queue( final Socket socket ) {
		synchronized( queue ) {
			queue.add( socket );
			queue.notifyAll();
		}
	}

	/**
	 * Shutdowns the thread manager
	 */
	public void shutdown() {
		synchronized( threads ) {
			for( final PDMClientHandlerThread thread : threads ) {
				thread.start();
			}
		}
	}
	
	/** 
	 * Creates the collection of initial threads
	 * @param queue the given {@link Socket socket} queue
	 * @param intialCount
	 * @return
	 */
	private static LinkedList<PDMClientHandlerThread> createThreads( final LinkedList<Socket> queue, 
																	 final int intialCount ) {
		final LinkedList<PDMClientHandlerThread> threads = new LinkedList<PDMClientHandlerThread>();
		for( int n = 0; n < intialCount; n++ ) {
			final PDMClientHandlerThread thread = new PDMClientHandlerThread( queue );
			threads.add( thread );
		}
		return threads;
	}
	
}
