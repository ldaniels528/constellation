package constellation.drawing;

import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;

/**
 * Represents a generic two-dimensional entity representation
 * @author lawrence.daniels@gmail.com
 */
public interface EntityRepresentation extends RenderableElement, Cloneable {
	
	/**
	 * Duplicates the representation positioning via the given delta values.
	 * @param dx the given delta X-coordinate of the duplicate representation
	 * @param dy the given delta Y-coordinate of the duplicate representation
	 * @return the duplicate {@link EntityRepresentation entity representation}
	 */
	EntityRepresentation duplicate( double dx, double dy );
	
	/**
	 * Returns the bounds of the drawing element given its
	 * current position and size.
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return the {@link RectangleXY bounds}
	 */
	RectangleXY getBounds( MatrixWCStoSCS matrix );
	
	/**
	 * Mirrors the representation via the given mirroring plane
	 * @param plane the given mirroring plane
	 * @return the resultant {@link EntityRepresentation representation}
	 */	
	EntityRepresentation mirror( LineXY plane );
	
	/**
	 * Returns the type of the element
	 * @return the {@link EntityTypes entity type}
	 */
	EntityTypes getType();
	
	/**
	 * Returns the category type of the entity
	 * @return the {@link EntityCategoryTypes category type}
	 */
	EntityCategoryTypes getCategoryType();
	
	/**
	 * Indicates whether the drawing element intersects the given boundary
	 * @param boundary the given {@link RectangleXY spatial boundary}
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return true, if the drawing element intersects the given boundary
	 */
	boolean intersects( RectangleXY boundary, MatrixWCStoSCS matrix );
	
}
