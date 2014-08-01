package constellation;

import java.util.TimerTask;

/**
 * Constellation Thread Pool
 * @author lawrence.daniels@gmail.com
 */
public interface ThreadPool {
	
	/** 
	 * Queues the given task for processing
	 * @param task the given {@link Runnable task}
	 */
	void queue( Runnable task );
	
	/** 
	 * Schedules the given task for processing on the given interval
	 * @param task the given {@link TimerTask task}
	 * @param interval the given interval specified in milliseconds
	 */
	void schedule( TimerTask task, long interval );
	
	/**
	 * Shuts down all threads
	 */
	void shutdown();

}
