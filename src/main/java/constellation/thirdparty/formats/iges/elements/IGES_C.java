package constellation.thirdparty.formats.iges.elements;

import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.LineCollection;

/** 
 * IGES Compressed ASCII Flag Element
 * @author lawrence.daniels@gmail.com
 */
public class IGES_C extends IGESElement {
	
	/**
	 * Default constructor
	 */
	public IGES_C() {
		super();
	}

	/**
	 * Creates a new IGES "C" element
	 * @param lines the given {@link LineCollection line collection}
	 * @throws ModelFormatException
	 */
	protected IGES_C( final LineCollection lines ) 
	throws ModelFormatException {
		throw new ModelFormatException( "Compress ASCII IGES files are not supported" );
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return TYPE_C;
	}

}
