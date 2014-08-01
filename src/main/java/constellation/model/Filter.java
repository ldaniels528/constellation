package constellation.model;

/**
 * Represents a filter for determining which geometry 
 * layers will be displayed
 * @author lawrence.daniels@gmail.com
 */
public class Filter {
	public static final int TOTAL_LAYERS = 256;
	private final boolean[] layers;
	private String name;
	
	/**
	 * Creates a new layer
	 * @param name the name of the layer
	 */
	public Filter( final String name ) {
		this.name 	= name;
		this.layers	= new boolean[ TOTAL_LAYERS ];
	}

	/**
	 * Indicates whether the filter is a system filter
	 * @return true, if the filter is a system filter
	 */
	public boolean isSystemFilter() {
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( final Object object ) {
		return ( object != null ) && 
				( object instanceof Filter ) &&
					((Filter)object).name.equals( name );
	}
	
	/**
	 * Returns the name of the layer
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the name of the layer
	 * @param name the name to set
	 */
	public void setName( final String name ) {
		this.name = name;
	}
	
	/**
	 * Returns the layer state for the given layer number
	 * @param layerNumber the given layer number
	 * @return the layer state
	 */
	public boolean containsLayer( final int layerNumber ) {
		return layers[ layerNumber ];
	}

	/**
	 * Returns the collection of layers
	 * @return the collection of layers
	 */
	public boolean[] getLayerStates() {
		return layers;
	}

	/**
	 * Adds a layer to the filter
	 * @param layerNumber the given {@link Integer layer number}
	 * @param active indicates whether the layer is active in the filter
	 */
	public void setLayerState( final int layerNumber, final boolean active ) {
		layers[layerNumber] = active;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
}
