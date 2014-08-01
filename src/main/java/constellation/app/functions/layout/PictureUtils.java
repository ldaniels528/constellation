package constellation.app.functions.layout;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Constellation Picture Utilities
 * @author lawrence.daniels@gmail.com
 */
public class PictureUtils {
	private static Set<String> IMAGE_FORMATS = new HashSet<String>(
				Arrays.asList( new String[] { "bmp", "jpeg", "jpg", "gif", "png" } )
			);
	
	/**
	 * Indicates whether the given file is an image file
	 * @param file the given {@link File file}
	 * @return true, if the extension of the file is 
	 * "*.jp[e]g", "*.png", or other accepted formats.
	 */
	public static boolean isImageFile( final File file ) {
		// cache the file name
		final String fileName = file.getName();
		
		// get the file extension
		final int index = fileName.lastIndexOf( '.' );
		if( index != -1 ) {
			final String formatId = fileName.substring( index + 1, fileName.length() ).toLowerCase();
			return IMAGE_FORMATS.contains( formatId );
		}
		return false;
	}

}
