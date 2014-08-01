package constellation.util;

import static java.lang.String.format;

/**
 * Constellation String Utilities
 * @author lawrence.daniels@gmail.com
 */
public class StringUtil {
	private static final String BLANK = "";
	
	/**
	 * Returns the elapsed time in "hh:mm:ss" format
	 * @param elapsedMsec the elapsed time in milliseconds
	 * @return the elapsed time in "hh:mm:ss" format
	 */
	public static String getElapsedTimeString( final long elapsedMsec ) {
		// compute the elapsed time
		final long elapsedSec	= ( elapsedMsec / 1000 );
		final long elapsedMin	= ( elapsedSec / 60 );
		
		// convert the time to hh:mm:ss format
		final int hh = (int)( elapsedMin / 60 );
		final int mm = (int)( elapsedMin % 60 );
		final int ss = (int)( ( elapsedSec - elapsedMin * 60 ) % 60 );
		
		// return the formatted string
		return format( "%02d:%02d:%02d", hh, mm, ss );
	}	
	
	/**
	 * Determines whether the given string is blank (<tt>null</tt> or empty)
	 * @param s the given {@link String string}
	 * @return true, if the string is <tt>null</tt> or empty
	 */
	public static boolean isBlank( final String s ) {
		return ( s == null ) || ( s.trim().equals( BLANK ) );
	}

	/**
	 * Returns the non-null version of the given string
	 * @param s the given source string
	 * @return a non-null string
	 */
	public static String notNull( final String s ) {
		return ( s == null ) ? BLANK : s; 
	}
	
}
