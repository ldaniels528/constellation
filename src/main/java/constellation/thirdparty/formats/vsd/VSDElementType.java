package constellation.thirdparty.formats.vsd;

/**
 * Represents a Microsoft VISIO/VSD Entity Type
 * @author lawrence.daniels@gmail.com
 */
public class VSDElementType implements VSDElement {
	private final String label;
	
	/**
	 * Creates a new VSD Element Type
	 * @param label the given label
	 */
	public VSDElementType( final String label ) {
		this.label = label;
	}
	
	/**
	 * Returns the label of the type
	 * @return the label of the type
	 */
	public String getLabel() {
		return label;
	}

}
