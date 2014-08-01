package constellation.drawing.elements;

import java.awt.Color;

import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.RenderableElement;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;

/**
 * This interface is implemented by all classes that contain
 * drawing attribute information such as line pattern, color, 
 * and etc., which affects the rendering outcome.
 * @author lawrence.daniels@gmail.com
 */
public interface ModelElement extends RenderableElement {
	
	///////////////////////////////////////////////////
	//		Type-related Method(s)
	///////////////////////////////////////////////////
	
	/**
	 * Returns the model element's internal representation
	 * @return the {@link EntityRepresentation internal representation}
	 */
	EntityRepresentation getRepresentation();
	
	/**
	 * Sets the model element's internal representation
	 * @param representation the given {@link EntityRepresentation internal representation}
	 */
	void setRepresentation( EntityRepresentation representation );
	
	/**
	 * Returns the type of the element
	 * @return the {@link EntityTypes entity type}
	 */
	EntityTypes getType();
	
	/**
	 * Returns the category type of the element
	 * @return the {@link EntityCategoryTypes category type}
	 */
	EntityCategoryTypes getCategoryType();
	
	///////////////////////////////////////////////////
	//		Label Method(s)
	///////////////////////////////////////////////////
	
	/**
	 * Returns the label of the drawing element
	 * @return the label of the drawing element
	 */
	String getLabel();
	
	/**
	 * Sets the label of the drawing element
	 * @param label the label of the drawing element
	 */
	void setLabel( String label );
	
	/**
	 * Indicates whether the drawing element has been labeled
	 * @return true, if the drawing element has been labeled
	 */
	boolean isLabeled();
	
	///////////////////////////////////////////////////
	//		Rendering-related Method(s)
	///////////////////////////////////////////////////
	
	/**
	 * Returns the color of the drawing element
	 * @return the current {@link Color color}
	 */
	Color getColor();
	
	/** 
	 * Sets the color of the drawing element
	 * @param color the given {@link Color color}
	 * @return a reference to the object's self
	 */
	ModelElement setColor( Color color );
	
	/**
	 * Returns the current drawing element layer
	 * @return the {@link Integer layer}
	 */
	int getLayer();
	
	/** 
	 * Sets the current drawing element layer
	 * @param layer the {@link Integer layer}
	 */
	void setLayer( int layer );
	
	/**
	 * Returns the line pattern for the drawing element
	 * @return the {@link LinePatterns line pattern}
	 */
	LinePatterns getPattern();
	
	/**
	 * Sets the line settings for the drawing element
	 * @param pattern the {@link LinePatterns line pattern}
	 */
	void setPattern( LinePatterns pattern );
	
	/**
	 * Returns the thickness of the lines
	 * @return the thickness
	 */
	int getThickness();

	/**
	 * Sets the thickness of the lines
	 * @param thickness the given thickness value
	 */
	void setThickness( int thickness );
	
	///////////////////////////////////////////////////
	//		Spatial Method(s)
	///////////////////////////////////////////////////
	
	/**
	 * Returns the bounds of the drawing element given its
	 * current position and size.
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return the {@link RectangleXY bounds}
	 */
	RectangleXY getBounds( MatrixWCStoSCS matrix );
	
	/**
	 * Indicates whether the drawing element intersects the given boundary
	 * @param boundary the given {@link RectangleXY spatial boundary}
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return true, if the drawing element intersects the given boundary
	 */
	boolean intersects( RectangleXY boundary, MatrixWCStoSCS matrix );
	
}
