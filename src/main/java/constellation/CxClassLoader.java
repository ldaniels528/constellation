package constellation;

import static java.lang.String.format;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;


/**
 * Constellation Class Loader
 * @author lawrence.daniels@gmail.com
 */
public class CxClassLoader extends URLClassLoader {
	// logger instance
	private static final Logger logger = Logger.getLogger( CxClassLoader.class );
	
	// singleton instance
	private static CxClassLoader instance;
	
	/**
	 * Default constructor
	 */
	private CxClassLoader() {
		super( getJarFileURLs() );
	}
	
	/**
	 * Returns the singleton instance
	 * @param caller the calling class instance
	 * @return the singleton instance
	 */
	public static CxClassLoader getInstance() {
		if( instance == null ) {
			instance = new CxClassLoader();
		}
		return instance;
	}
	
	/**
	 * Returns an array of URLs of the .jar files found in the "./lib/" directory
	 * @return an array of {@link URL URLs}
	 */
	private static URL[] getJarFileURLs() {
		// get the libraries directory
		final File libDir = CxConfigurationUtil.getLibrariesDirectory();
		
		// get all the .JAR files
		final File[] jarFiles = libDir.listFiles( new JarFileFilter() );
		
		// create a list of URLs 
		try {
			final URL[] urls = new URL[ jarFiles.length ];
			for( int n = 0; n < urls.length; n++ ) {
				logger.info( format( "Adding Jar '%s'...", jarFiles[n].getAbsolutePath() ) );
				urls[n] = jarFiles[n].toURI().toURL();
			}
			return urls;
		}
		catch( final Exception e ) {
			return new URL[0];
		}
	}
	
	/**
	 * Constellation JAR File Filter
	 * @author lawrence.daniels@gmail.com
	 */
	private static class JarFileFilter implements FileFilter {

		/* 
		 * (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept( final File file ) {
			return file.getName().toLowerCase().endsWith( ".jar" );
		}
		
	}
}
