package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * IGES Terminate Element
 * <pre>
 *	1 records in Start Section
 *	4 records in Global Section
 *	12 records in Directory Entry Section (6 entities)
 *	6 records in Parameter Data Section
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES_T extends IGESElement {
	private int startCount;
	private int globalCount;
	private int directoryEntryCouunt;
	private int parameterDataCount;
	
	/**
	 * Default constructor
	 */
	public IGES_T() {
		super();
	}

	/**
	 * Creates a new IGES "T" record
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException
	 */
	protected IGES_T( final LineCollection lines ) 
	throws ModelFormatException {
		// get the next line
		final String line = lines.next();
		
		// parse the line
		this.startCount 			= parseInt( line, 1, 8 );
		this.globalCount			= parseInt( line, 10, 16 );
		this.directoryEntryCouunt	= parseInt( line, 18, 24 );
		this.parameterDataCount		= parseInt( line, 26, 32 );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String getType() {
		return TYPE_T;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format( "Start '%d' Global '%d' Directory Entries '%d' Parameters '%d' %s", 
				startCount, globalCount, directoryEntryCouunt, parameterDataCount,
				super.toString() );
	}

}
