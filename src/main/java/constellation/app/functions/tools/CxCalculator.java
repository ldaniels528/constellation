package constellation.app.functions.tools;

import static java.lang.String.format;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;

import constellation.util.OSPlatformUtil;

/**
 * This class provides a means of loading the
 * platform dependent system calculator.
 * @author lawrence.daniels@gmail.com
 */
public class CxCalculator {
	private static final Logger logger = Logger.getLogger( CxCalculator.class );
	
	/**
	 * Private Constructor
	 */
	private CxCalculator() {
		super();
	}
	
	/**
	 * Launches the host operating system's calculator
	 * @throws IOException
	 */
	public static void launch() 
	throws IOException {
		// get the runtime instance
		final Runtime runtime = Runtime.getRuntime();
		Process process = null;
		
		// if the host OS is MacOS X ...
		if( OSPlatformUtil.isMacOS() ) {
			// invoke the application
			logger.info( "Using AppleScript to invoke the Calculator application..." );
			process = runtime.exec( new String[] { "osascript", "-e", "tell app \"Calculator\" to activate" } );
		}
		
		// if the host OS is Microsoft Windows ...
		else if( OSPlatformUtil.isWindowsOS() ) {
			// invoke the application
			logger.info( "Invoke the Calculator application on Windows..." );
			process = runtime.exec( new String[] { "Calc.exe" } );
		}
		
		// Unrecognized platform
		else {
			logger.error( format( "Unrecognized platform '%s'", OSPlatformUtil.getOperatingSystemName() ) ); 
		}
		
		// handle the termination event
		if( process != null ) {
			// TODO do something here
		}
	}
	
	/**
	 * Calculator Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class CalculatorAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			try {	
				CxCalculator.launch();
			} 
			catch( final IOException e ) {
				logger.error( "Error executing system calculator", e );
			}
		}
	}

}
