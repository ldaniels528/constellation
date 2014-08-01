package constellation.app;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import constellation.CxConfigurationUtil;
import constellation.app.preferences.CxSystemPreferences;
import constellation.preferences.SystemPreferences;

/**
 * Constellation Advanced Drawing Package
 * @author lawrence.daniels@gmail.com
 */
@CxVersion("0.49RC4")
public class Constellation {
	private static final Logger logger = Logger.getLogger( Constellation.class );
	private final SystemPreferences systemPreferences;
	
	/**
	 * Default constructor
	 */
	public Constellation( final File configFile ) {
		this.systemPreferences = getSystemPreferences( configFile );
	}
	
	/**
	 * For stand alone operation
	 * @param args the given command line arguments
	 */
	public static void main( final String[] args ) {
		// setup dynamic layout
		Toolkit.getDefaultToolkit().setDynamicLayout( true );
		
		// set the system look and feel
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} 
		catch( final Exception e ) {
			logger.error( "Error setting system look and feel", e );
		}
		
		// get the XML configuration file
		final File configFile = CxConfigurationUtil.getXMLConfigurationFile();
		
		// start the application
		( new Constellation( configFile ) ).execute();
	}

	/**
	 * Executes the application 
	 */
	public void execute() {
		// launch an initial application window
		CxApplicationController.launch( systemPreferences );
		
		// add the shutdown hook
		Runtime.getRuntime().addShutdownHook( new ShutdownHook() );
	}
	
	/** 
	 * Retrieving the system preferences via the given configuration file
	 * @param configFile the given {@link File configuration file}
	 * @return the {@link SystemPreferences system preferences}
	 */
	private SystemPreferences getSystemPreferences( final File configFile ) {
		try {
			return CxSystemPreferences.load( configFile );
		} 
		catch( final Exception e ) {
			logger.error( "Error reading configuration file", e );
			return CxSystemPreferences.getInstance();
		} 
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Shutdown Hook (Inner Class)
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Shutdown Hook 
	 * @author lawrence.daniels@gmail.com
	 */
	private class ShutdownHook extends Thread {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// update the configuration
			logger.info( "Writting configuration file..." );
			try {
				systemPreferences.save();
			} 
			catch( final IOException cause ) {
				logger.error( "Error saving the system preferences", cause );
			}
	
			// final shutdown message
			logger.info( "Shutting down..." );
		}
		
	}
}
