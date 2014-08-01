package constellation.thirdparty.formats.iges.entities.x400;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES Property Entity
 * @author lawrence.daniels@gmail.com
 */
public class IGESPropertyEntity implements IGESEntity {
	private Collection<String> properties;
	
	/**
	 * Default constructor
	 */
	public IGESPropertyEntity() {
		this.properties = new LinkedList<String>();
	}
	
	/**
	 * Adds the given property to this entity
	 * @param property the given property
	 */
	public void addProperty( final String property ) {
		properties.add( property );
	}
	
	/**
	 * Adds the given property to this entity
	 * @param properties the given {@link Collection collection} of properties
	 */
	public void addProperties( final Collection<String> properties ) {
		this.properties.addAll( properties );
	}
	
	/**
	 * Returns the properties contained within this entity
	 * @return the array of properties
	 */
	public Collection<String> getProperties() {
		return properties;
	}

	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return properties.toString();
	}

}
