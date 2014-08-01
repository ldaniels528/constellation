package constellation.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import constellation.ThreadPool;

/**
 * Constellation Thread Pool
 * @author lawrence.daniels@gmail.com
 */
public class CxThreadPool implements ThreadPool {
	private final Collection<WorkerThread> threads;
	private final LinkedList<Runnable> queue;
	private final Timer timer;
	
	/** 
	 * Creates a new thread pool instance
	 */
	public CxThreadPool() {
		this.queue	 = new LinkedList<Runnable>();
		this.threads = createThreads( 3 );
		this.timer 	 = new Timer();
	}
	
	/** 
	 * Queues the given task for processing
	 * @param task the given {@link Runnable task}
	 */
	public void queue( final Runnable task ) {
		synchronized( queue ) {
			queue.add( task );
			queue.notifyAll();
		}
	}
	
	/** 
	 * Schedules the given task for processing on the given interval
	 * @param task the given {@link TimerTask task}
	 * @param interval the given interval specified in milliseconds
	 */
	public void schedule( final TimerTask task, final long interval ) {
		timer.schedule( task, 0, interval );
	}
	
	/**
	 * Shuts down all threads
	 */
	public void shutdown() {
		// kill the timer
		timer.cancel();
		
		// kill all threads
		synchronized( threads ) {
			for( final WorkerThread thread : threads ) {
				thread.die();
			}
		}
	}
	
	/** 
	 * Creates a collection of threads
	 * @param count the number of threads to create
	 * @return the {@link Collection collection} of {@link WorkerThread worker thread}
	 */
	private Collection<WorkerThread> createThreads( final int count ) {
		final Collection<WorkerThread> threads = new ArrayList<WorkerThread>( count );
		for( int n = 0; n < count; n++ ) {
			threads.add( new WorkerThread() );
		}
		return threads;
	}
	
	/**
	 * Worker thread
	 * @author lawrence.daniels@gmail.com
	 */
	private class WorkerThread implements Runnable {
		private Thread thread;
		private boolean alive;
		
		/**
		 * Default constructor
		 */
		public WorkerThread() {
			start();
		}
		
		/**
		 * Causes the thread to cease running
		 */
		public void die() {
			alive = false;
			thread.interrupt();
		}

		/** 
		 * Retrieves the next task in the queue
		 * @return the {@link Runnable task}
		 */
		private Runnable getNext() {
			Runnable runnable = null;
			synchronized( queue ) {
				// wait until a task is available
				while( alive && queue.isEmpty() ) {
					try { queue.wait(); } catch( InterruptedException e ) { return null; }
				}
				
				// get the task
				runnable = queue.removeFirst();
			}
			return runnable;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while( alive ) {
				// get the next task from the queue
				final Runnable task = getNext();
				
				// execute the task
				if( task != null ) {
					task.run();
				}
			}
		}
		
		/**
		 * Starts the execution of the thread
		 */
		private void start() {
			if( !alive ) {
				alive = true;
				thread = new Thread( this );
				thread.setDaemon( true );
				thread.start();
			}
		}
	}
	
}
