package constellation;

import static java.io.File.separator;
import static java.lang.String.format;

import java.io.File;

/**
 * Constellation Configuration Utility
 * @author lawrence.daniels@gmail.com
 */
public class CxConfigurationUtil {
	// singleton instance
	private static CxConfigurationUtil instance = new CxConfigurationUtil();
	
	/**
	 * Default constructor
	 */
	private CxConfigurationUtil() {
		super();
	}
	
	/**
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static CxConfigurationUtil getInstance() {
		return instance;
	}
	
	/**
	 * Returns the model file reference to the given model name
	 * @param modelName the given model file name
	 * @return the {@link File model file reference}
	 */
	public File getModelReference( final String modelName ) {
		// get the model directory
		final File modelDirectory = getModelDirectory();
		
		// get a reference to the configuration file
		final File modelFile = 
			new File( format( "%s%s%s", modelDirectory.getAbsolutePath(), separator, modelName ) );
		
		// return the configuration file
		return modelFile;
	}

	/**
	 * Returns a reference to the base directory (e.g. "/Users/ldaniels/Constellation/")
	 * @return the configuration {@link File directory}
	 */
	public static File getBaseDirectory() {
		// get a reference to the home directory
		final File homeDirectory = getUserHomeDirectory();
		
		// get a reference to the home directory
		final File configDirectory = new File( format( "%s%s%s", 
				homeDirectory.getAbsolutePath(), 
				separator, 
				"Constellation" ) );
		
		// if the configuration directory doesn't exist, create it.
		if( !configDirectory.exists() ) {
			configDirectory.mkdir();
		}
	
		// return the configuration directory
		return configDirectory;
	}

	/**
	 * Returns a reference to the configuration directory (e.g. "/Users/ldaniels/Constellation/conf/")
	 * @return the configuration {@link File directory}
	 */
	public static File getConfigurationDirectory() {
		// get the base directory
		final File baseDirectory = getBaseDirectory();
		
		// get a reference to the home directory
		final File configDirectory = new File( format( "%s%s%s", 
				baseDirectory.getAbsolutePath(), 
				separator, 
				"conf" ) );
		
		// if the configuration directory doesn't exist, create it.
		if( !configDirectory.exists() ) {
			configDirectory.mkdir();
		}
	
		// return the configuration directory
		return configDirectory;
	}

	/**
	 * Returns a reference to the configuration directory (e.g. "/Users/ldaniels/Constellation/lib/")
	 * @return the configuration {@link File directory}
	 */
	public static File getLibrariesDirectory() {
		// get the base directory
		final File baseDirectory = getBaseDirectory();
		
		// get a reference to the home directory
		final File libDirectory = new File( format( "%s%s%s", 
				baseDirectory.getAbsolutePath(), 
				separator, 
				"lib" ) );
		
		// if the configuration directory doesn't exist, create it.
		if( !libDirectory.exists() ) {
			libDirectory.mkdir();
		}
	
		// return the configuration directory
		return libDirectory;
	}

	/**
	 * Returns a reference to the model directory (e.g. "/Users/ldaniels/Constellation/models/")
	 * @return the configuration {@link File directory}
	 */
	public static File getModelDirectory() {
		// get the base directory
		final File baseDirectory = getBaseDirectory();
		
		// get a reference to the model directory
		final File modelDirectory = new File( format( "%s%s%s", 
				baseDirectory.getAbsolutePath(), 
				separator, 
				"models" ) );
		
		// if the model directory doesn't exist, create it.
		if( !modelDirectory.exists() ) {
			modelDirectory.mkdir();
		}
		
		return modelDirectory;
	}
	
	/**
	 * Returns a reference to the model directory (e.g. "/Users/ldaniels/Constellation/recordings/")
	 * @return the configuration {@link File directory}
	 */
	public static File getRecordingsDirectory() {
		// get the base directory
		final File baseDirectory = getBaseDirectory();
		
		// get a reference to the recordings directory
		final File recordingsDirectory = new File( format( "%s%s%s", 
				baseDirectory.getAbsolutePath(), 
				separator, 
				"recordings" ) );
		
		// if the model directory doesn't exist, create it.
		if( !recordingsDirectory.exists() ) {
			recordingsDirectory.mkdir();
		}
		
		return recordingsDirectory;
	}

	/**
	 * Returns a reference to the model directory (e.g. "/Users/ldaniels/Constellation/screenshots/")
	 * @return the configuration {@link File directory}
	 */
	public static File getSnapshotsDirectory() {
		// get the base directory
		final File baseDirectory = getBaseDirectory();
		
		// get a reference to the snapshots directory
		final File snapShotsDirectory = new File( format( "%s%s%s", 
				baseDirectory.getAbsolutePath(), 
				separator, 
				"screenshots" ) );
		
		// if the snapshots directory doesn't exist, create it.
		if( !snapShotsDirectory.exists() ) {
			snapShotsDirectory.mkdir();
		}
		
		return snapShotsDirectory;
	}

	/**
	 * Returns a reference to the configuration directory (e.g. "/Users/ldaniels/")
	 * @return the configuration {@link File directory}
	 */
	public static File getUserHomeDirectory() {
		// get the user's home directory path
		final String userHomePath = System.getProperty( "user.home" );
		
		// return the directory reference
		return new File( userHomePath );
	}
	
	/**
	 * Returns a reference to the XML configuration file (e.g. "/Users/ldaniels/Constellation/conf/Constellation.xml")
	 * @return the configuration {@link File file}
	 */
	public static File getXMLConfigurationFile() {
		// get the configuration directory
		final File configDirectory = getConfigurationDirectory();
		
		// get a reference to the configuration file
		final File configFile = 
			new File( format( "%s%s%s", 
					configDirectory.getAbsolutePath(), 
					separator,
					"Constellation.xml" ) );
	
		// return the configuration file
		return configFile;
	}
	
}
