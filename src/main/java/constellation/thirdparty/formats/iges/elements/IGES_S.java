package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/**
 * IGES Start Element
 * @author lawrence.daniels@gmail.com
 */
public class IGES_S extends IGESElement {
	private String description;
	
	/**
	 * Default constructor
	 */
	public IGES_S() {
		super();
	}
	
	/**
	 * Creates a new IGES "S" record
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException
	 */
	protected IGES_S( final LineCollection lines ) 
	throws ModelFormatException {
		this.description = parseString( lines.next(), 0, 72 );
	}

	/** 
	 * Returns the description
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/** 
	 * Sets the description
	 * @param description the description
	 */
	public void setDescription( final String description ) {
		this.description = description;
	}

	/** 
	 * {@inheritDoc}
	 */
	public String getType() {
		return TYPE_S;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "description '%s', %s", description, super.toString() );
	}

}
