package constellation.thirdparty.formats.iges.elements;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * Represents an IGES Element 
 * @author lawrence.daniels@gmail.com
 */
public abstract class IGESElement {
	public static final Logger logger = Logger.getLogger( IGESElement.class );
	public static final int RECORD_LENGTH = 80;
	public static final String TYPE_B 	= "B";
	public static final String TYPE_C 	= "C";
	public static final String TYPE_D 	= "D";
	public static final String TYPE_G 	= "G";
	public static final String TYPE_P 	= "P"; 
	public static final String TYPE_S 	= "S";
	public static final String TYPE_T 	= "T";	
	
	// internal fields
	private int sequenceNumber;
	
	/**
	 * Default constructor
	 */
	protected IGESElement() {
		super();
	}
	
	/** 
	 * Parses the given text line and returns an IGES element
	 * @param line the given text line
	 * @return an {@link IGESElement IGES element}
	 * @throws ModelFormatException
	 */
	public static IGESElement parse( final LineCollection lines ) 
	throws ModelFormatException {
		// get the next line
		final String line = lines.peek();
		
		// verify the record length
		if( line.length() != RECORD_LENGTH ) {
			throw new ModelFormatException( String.format( "Record length must be %d characters", RECORD_LENGTH ) );
		}
		
		// get the record type
		final String type 		= line.substring( 72, 73 );
		final int sequenceNumber= parseInt( line, 73, 80 );
		
		// get the appropriate element
		IGESElement element;
		if( TYPE_D.equals( type ) ) element = new IGES_D( lines );
		else if( TYPE_G.equals( type ) ) element = IGES_G.parse( lines );	
		else if( TYPE_P.equals( type ) ) element = new IGES_P( lines );
		else if( TYPE_S.equals( type ) ) element = new IGES_S( lines );	
		else if( TYPE_T.equals( type ) ) element = new IGES_T( lines );	
		else throw new ModelFormatException( String.format( "Invalid record type '%s'", type ) );
		
		// set the sequence number of the element
		element.setSequenceNumber( sequenceNumber ); 
		
		// return the element
		logger.debug( String.format( "[%03d] %s |%s|", sequenceNumber, line, element ) );
		return element;
	}
	
	
	/** 
	 * Returns the sequence number
	 * @return the sequence number
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/** 
	 * Sets the sequence number
	 * @param sequenceNumber the sequence number
	 */
	public void setSequenceNumber( final int sequenceNumber ) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Returns the type of the element
	 * @return the type of the element
	 */
	public abstract String getType();
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "Type '%s' ", getType() );
	}
	
	/**
	 * Parses the given data string into tokens
	 * @param dataString the given data string
	 * @return an array of tokens
	 * @throws ModelFormatException
	 */
	public static LinkedList<String> parseTokens( final String dataString ) 
	throws ModelFormatException {
		final LinkedList<String> tokens = new LinkedList<String>();
		final char[] data = dataString.toCharArray();
		int position = 0;
		int start;
		
		// iterate the data array
		while( position < data.length ) {
			// capture the current position
			start = position;
			
			// is it an H?
			switch( data[position++] ) {
				// 'H' delimiter
				case 'H':
					// get the specified size
					final int count = parseInt( tokens.removeLast() );
					tokens.add( dataString.substring( position, position + count ) );
					position += count;
					break;
				
				// comma?
				case ',':
					// if the next character is a comma,
					// add blank lines
					while( ( position < data.length ) && ( data[position] == ',' ) ) {
						tokens.add( "" );
						position++;
					}
					break;
				
				// semicolon?
				case ';':
					break;
				
				// otherwise, check for a number
				default:
					// is it a number?
					if( isNumberChar( data[start] ) ) {
						while( ( position < data.length ) && isNumberChar( data[position] ) ) {
							position++;
						}
						tokens.add( dataString.substring( start, position ) );
					}
					else {
						throw new ModelFormatException( String.format( "Unhandled character '%c'", data[start] ) );
					}
			}
		}
		
		return tokens;
	}
	
	/** 
	 * Indicates whether the given character is a numeric character
	 * @param ch the given character 
	 * @return true, if the character is '.' or '0'-'9'
	 */
	private static boolean isNumberChar( final char ch ) {
		return ( ( ch >= '0' && ch <= '9' ) || ch == '+' || ch == '-' || ch == 'E' || ch == '.' );
	}
	
	/** 
	 * Parses the integer from the given string
	 * @param s the given line
	 * @return the integer value
	 */
	protected static int parseInt( final String s ) {
		if( s.equals( "" ) ) {
			return 0;
		}
		try {
			return Integer.parseInt( s );
		}
		catch( final NumberFormatException e ) {
			logger.error( String.format( "Error parsing '%s'", s ) ); 
			return 0;
		}
	}
	
	/** 
	 * Parses the integer from the given line between the given start and end positions
	 * @param line the given line
	 * @param start the given start position
	 * @param end the given end position
	 * @return the integer value
	 */
	protected static int parseInt( final String line, final int start, final int end ) {
		final String s = parseString( line, start, end ).trim();
		return parseInt( s );
	}
	
	/** 
	 * Parses the double from the given string 
	 * @param s the given string
	 * @return the double value
	 */
	protected static double parseDouble( final String s ) {
		if( s.equals( "" ) ) {
			return 0;
		}
		try {
			return Double.parseDouble( s );
		}
		catch( final NumberFormatException e ) {
			logger.error( String.format( "Error parsing '%s'", s ) ); 
			return 0;
		}
	}
	
	/** 
	 * Parses the string from the given line between the given start and end positions
	 * @param line the given line
	 * @param start the given start position
	 * @param end the given end position
	 * @return the string
	 */
	protected static String parseString( final String line, final int start, final int end ) {
		return line.substring( start, end ).trim();
	}
	
}
