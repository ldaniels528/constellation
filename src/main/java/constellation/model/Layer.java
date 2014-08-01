package constellation.model;

import java.util.Collection;
import java.util.HashSet;

import constellation.drawing.elements.ModelElement;

/**
 * Represents a GeometricElement Layer
 * @author lawrence.daniels@gmail.com
 */
public class Layer {
	private final Collection<ModelElement> geometrySet;
	private String name;
	
	/**
	 * Creates a new layer
	 * @param name the name of the layer
	 */
	public Layer( final String name ) {
		this.name 			= name;
		this.geometrySet	= new HashSet<ModelElement>( 100 );
	}

	/** 
	 * Adds the geometry to the collection
	 * @param geometry the given {@link ModelElement geometry}
	 */
	public void add( final ModelElement geometry ) {
		synchronized( geometrySet ) {
			geometrySet.add( geometry );
		}
	}
	
	/**
	 * Adds the given collection of geometric elements to this object
	 * @param elements the given {@link Collection collection} of {@link ModelElement geometry}
	 */
	public void addAll( final Collection<ModelElement> elements ) {
		synchronized( geometrySet ) {
			geometrySet.addAll( elements );
		}
	}
	
	/**
	 * Returns the complete set of geometry
	 * @return the complete set of geometry
	 */
	public Collection<ModelElement> getGeometry() {
		synchronized( geometrySet ) {
			return new HashSet<ModelElement>( geometrySet );
		}
	}
	
	/**
	 * Removes the given geometry from the model
	 * @param geometry the given {@link ModelElement geometry}
	 * @return true, if the element was successfully removed
	 */
	public boolean remove( final ModelElement geometry ) {
		synchronized( geometrySet ) {
			return geometrySet.remove( geometry );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( final Object object ) {
		return ( object != null ) && 
				( object instanceof Layer ) &&
					((Layer)object).name.equals( this.name );
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
	 * Returns the number of geometric elements on the layer
	 * @return the number of geometric elements 
	 */
	public int getSize() {
		synchronized( geometrySet ) {
			return geometrySet.size();
		}
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
