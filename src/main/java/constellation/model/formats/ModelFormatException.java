package constellation.model.formats;

/**
 * Represents an exception that occurs while reading
 * or writing an external format (e.g. 'IGES')
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class ModelFormatException extends Exception {
	
	/**
	 * Creates an import exception instance
	 * @param cause the given error message
	 */
	public ModelFormatException( final String cause ) {
		super( cause );
	}
	
	/**
	 * Creates an import exception instance
	 * @param cause the given {@link Throwable cause}
	 */
	public ModelFormatException( final Throwable cause ) {
		super( cause );
	}

}
