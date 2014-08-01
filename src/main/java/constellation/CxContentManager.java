package constellation;

import static java.lang.String.format;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Constellation Content Manager
 * @author lawrence.daniels@gmail.com
 */
public class CxContentManager {
	// singleton instance
	private static CxContentManager instance = new CxContentManager();
	
	// internal fields
	private final ClassLoader classLoader;
		
	/**
	 * Private constructor
	 */
	private CxContentManager() {
		this.classLoader = CxClassLoader.getInstance();
	}
	
	/**
	 * Returns the singleton instance of the Content Manager
	 * @return the singleton instance
	 */
	public static CxContentManager getInstance() {
		return instance;
	}
		
	/**
	 * Retrieves the icon from the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link Icon image icon} or <tt>null</tt> if not found
	 */
	public ImageIcon getIcon( final String resourcePath ) {
		return new ImageIcon( getResourceURL( resourcePath ) );
	}
	
	/**
	 * Retrieves the image from the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link Image image} or <tt>null</tt> if not found
	 */
	public Image getImage( final String resourcePath ) {
		return getIcon( resourcePath ).getImage();
	}

	/**
	 * Retrieves the icon from the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link Icon image icon} or <tt>null</tt> if not found
	 */
	public String getImagePath( final String resourcePath ) {
		return getResourceURL( resourcePath ).toExternalForm();
	}

	/**
	 * Retrieves the URL to the given resource path 
	 * @param resourcePath the given resource path
	 * @return the requested {@link URL URL} or <tt>null</tt> if not found
	 */
	public URL getResourceURL( final String resourcePath ) {		
		// first attempt to locate the resource's URL from the .jar file
		final URL url = classLoader.getResource( resourcePath );
		if( url != null ) {
			return url;
		}
		
		// if that fails, try to locate it locally
		else {
			final File localFile = new File( format( "resources/%s", resourcePath ) );
			if( !localFile.exists() ) {
				throw new IllegalArgumentException( format( "Resource '%s' does not exist", localFile.getAbsolutePath() ) );
			}
			try {
				return ( new File( localFile.getAbsolutePath() ) ).toURI().toURL();
			} 
			catch( final MalformedURLException e ) {
				throw new IllegalArgumentException( format( "Error retrieving resource '%s'", resourcePath ), e );
			}
		}
	}
	
}
