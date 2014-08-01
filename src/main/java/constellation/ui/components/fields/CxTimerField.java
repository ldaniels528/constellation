package constellation.ui.components.fields;

import javax.swing.JLabel;

import constellation.CxFontManager;
import constellation.util.StringUtil;

/**
 * Constellation Timer Field
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxTimerField extends JLabel {
	private static final String ZEROES = "--:--:--";
	private final TimeDisplayUpdateThread thread;
	private boolean alive;
	private long startTime;
	
	/** 
	 * Default Constructor
	 */
	public CxTimerField() {
		super( ZEROES  );
		
		// create the thread
		thread = new TimeDisplayUpdateThread();
		
		// set the default thread
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Resets the timer
	 */
	public void reset() {
		super.setText( ZEROES );
	}
	
	/**
	 * Starts the timer
	 */
	public void start() {
		// start the thread
		synchronized( thread ) {
			if( !alive ) {
				// capture the start time
				startTime = System.currentTimeMillis();
				
				// keep the thread alive
				alive = true;
				thread.start();
			}
		}
	}
	
	/**
	 * Stops the timer
	 */
	public void stop() {
		alive = false;
	}
	
	/**
	 * Time Display Update Thread
	 * @author lawrence.daniels@gmail.com
	 */
	private class TimeDisplayUpdateThread extends Thread {
		
		/** 
		 * {@inheritDoc}
		 */
		public void run() {
			while( alive ) {
				// compute the elapsed time
				final long elapsedTime = System.currentTimeMillis() - startTime;
				
				// update the display
				setText( StringUtil.getElapsedTimeString( elapsedTime ) );
				
				// sleep for 1000ms
				try { Thread.sleep( 1000 ); }
				catch( final InterruptedException e ) { }
			}
		}
	}

}
