package constellation.thirdparty.formats.vsd;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Microsoft VISIO/VSD Work Sheet
 * @author lawrence.daniels@gmail.com
 */
public class VSDWorkSheet {
	private final List<VSDElement> elements;
	private String label;
	private byte code;
	
	/**
	 * Default Constructor
	 */
	public VSDWorkSheet( final String label ) {
		this.label		= label;
		this.elements 	= new LinkedList<VSDElement>();
	}
	
	/**
	 * Adds the given VSD Element to the model
	 * @param element the given {@link VSDElement VSD Element}
	 */
	public void add( final VSDElement element ) {
		elements.add( element );
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel( final String label ) {
		this.label = label;
	}
	
	/**
	 * @return the code
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode( final byte code ) {
		this.code = code;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return format( "%s[%02X]", label, code );
	}
	
}
