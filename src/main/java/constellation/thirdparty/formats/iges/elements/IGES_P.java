package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * IGES Parameter Data Element
 * @author lawrence.daniels@gmail.com
 */
public class IGES_P extends IGESElement {
	private String[] parameters;
	private int lineNumber;
	
	/**
	 * Default constructor
	 */
	public IGES_P() {
		super();
	}
	
	/**
	 * Creates a new IGES "P" element
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException 
	 */
	public IGES_P( final LineCollection lines ) 
	throws ModelFormatException {
		// get the line number
		this.lineNumber = parseInt( lines.peek(), 73, 80 );
		
		// get the parameters
		this.parameters	= parseParameters( lines );
	}
	
	/**
	 * Returns the number of parameters
	 * @return the number of parameters
	 */
	public int getCount() {
		return parameters.length;
	}
	
	/** 
	 * Returns the line number
	 * @return the line number
	 */
	public int getSequenceNumber() {
		return lineNumber;
	}

	/**
	 * Returns the parameter data array
	 * @return the parameter data array
	 */
	public String[] getParameters() {
		return parameters;
	}

	/*** 
	 * Sets the parameter data array
	 * @param parameters the parameter data array
	 */
	public void setParameters( final String[] parameters ) {
		this.parameters = parameters;
	}
	
	/**
	 * Returns the parameter at the given index
	 * @param index the given index
	 */
	public double getDoubleParameter( final int index ) {
		return Double.parseDouble( parameters[index] );
	}
	
	/**
	 * Returns the parameter at the given index
	 * @param index the given index
	 */
	public int getIntegerParameter( final int index ) {
		return Integer.parseInt( parameters[index] );
	}
	
	/**
	 * Returns the parameter at the given index
	 * @param index the given index
	 */
	public String getStringParameter( final int index ) {
		return parameters[index];
	}

	/** 
	 * {@inheritDoc}
	 */
	public String getType() {
		return TYPE_P;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer( 300 );
		sb.append( " { " );
		for( int n = 0; n < parameters.length; n++ ) {
			sb.append( String.format( "p%02d='%s' ", n, parameters[n] ) );
		}
		sb.append( "}" );
		return sb.toString();
	}
	
	/**
	 * Parses the parameters
	 * @param lines the line collection
	 * @return the array of parameters
	 * @throws ModelFormatException
	 */
	private static String[] parseParameters( final LineCollection lines ) 
	throws ModelFormatException {		
		final StringBuffer sb = new StringBuffer( 300 );

		// build the parameter string
		String line;
		do {
			// get the next line
			line = lines.next();
		
			// add the content to the buffer
			sb.append( parseString( line, 0, 67 ).trim() );
		} 
		while( !line.contains( ";" ) );
		
		// get the parameter string
		final String paramString = sb.substring( 0, sb.length() - 1 ).trim();
		
		// divide the string into pieces
		return paramString.split( "[,]" );
	}

}
