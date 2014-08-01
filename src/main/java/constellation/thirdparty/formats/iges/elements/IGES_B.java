package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/** 
 * IGES Binary Flag Element
 * @author lawrence.daniels@gmail.com
 */
public class IGES_B extends IGESElement {
	
	/**
	 * Default constructor
	 */
	public IGES_B() {
		super();
	}

	/**
	 * Creates a new IGES "B" element
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException
	 */
	protected IGES_B( final LineCollection lines ) 
	throws ModelFormatException {
		throw new ModelFormatException( "Binary IGES files are not supported" );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return TYPE_B;
	}

}
