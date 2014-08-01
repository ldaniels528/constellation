package constellation.thirdparty.formats.iges.entities;

import constellation.drawing.elements.ModelElement;

/**
 * Represents an IGES Entity
 * @author lawrence.daniels@gmail.com
 */
public interface IGESEntity {
	
	/**
	 * Returns the geometry represented by the given entity
	 * @return the {@link ModelElement geometry}
	 */
	ModelElement[] toDrawingElements();

}
