package constellation.app.functions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents an indexed model element
 * @author lawrence.daniels@gmail.com
 */
public class IndexedModelElement implements ModelElement {
	private final ModelElement element;
	private final int index;
	
	/**
	 * Creates a new indexed model element
	 * @param element the given {@link ModelElement model element}
	 * @param index the given index of the element within the list
	 */
	public IndexedModelElement( final ModelElement element, final int index ) {
		this.element = element;
		this.index	 = index;
	}
	
	/**
	 * Returns the array of elements wrapped as indexed elements
	 * @param elements the given {@link ModelElement elements}
	 * @return the array of {@link ModelElement elements}
	 */
	public static ModelElement[] createIndexedSet( final ModelElement[] elements ) {
		final IndexedModelElement[] indexedElements = new IndexedModelElement[ elements.length ];
		for( int n = 0; n < elements.length; n++ ) {
			indexedElements[n] = new IndexedModelElement( elements[n], n );
		}
		return indexedElements;
	}
	
	/**
	 * Returns the collection of elements wrapped as indexed elements
	 * @param elements the given {@link ModelElement elements}
	 * @return the array of {@link ModelElement elements}
	 */
	public static ModelElement[] createIndexedSet( final Collection<ModelElement> elements ) {
		final IndexedModelElement[] indexedElements = new IndexedModelElement[ elements.size() ];
		int n = 0;
		for( final ModelElement element : elements ) {
			indexedElements[n] = new IndexedModelElement( element, n++ );
		}
		return indexedElements;
	}
	
	/**
	 * Returns the host element
	 * @return the {@link ModelElement element}
	 */
	public ModelElement getHostElement() {
		return element;
	}
	
	/**
	 * Returns the index of the element
	 * @return the index of the element
	 */
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds(MatrixWCStoSCS matrix) {
		return element.getBounds( matrix );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getColor()
	 */
	public Color getColor() {
		return element.getColor();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getLabel()
	 */
	public String getLabel() {
		return element.getLabel();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getLayer()
	 */
	public int getLayer() {
		return element.getLayer();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getPattern()
	 */
	public LinePatterns getPattern() {
		return element.getPattern();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getRepresentation()
	 */
	public EntityRepresentation getRepresentation() {
		return element.getRepresentation();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getThickness()
	 */
	public int getThickness() {
		return element.getThickness();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#getType()
	 */
	public EntityTypes getType() {
		return element.getType();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return element.getCategoryType();
	}
	
	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return element.intersects( boundary, matrix );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#isLabeled()
	 */
	public boolean isLabeled() {
		return element.isLabeled();
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#setColor(constellation.drawing.DrawingColors)
	 */
	public ModelElement setColor( final Color color ) {
		return element.setColor( color );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#setLabel(java.lang.String)
	 */
	public void setLabel( final String label ) {
		element.setLabel( label );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#setLayer(int)
	 */
	public void setLayer( final int layer ) {
		element.setLayer( layer );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#setPattern(constellation.drawing.LinePatterns)
	 */
	public void setPattern( final LinePatterns pattern ) {
		element.setPattern( pattern );
	}

	/* (non-Javadoc)
	 * @see constellation.model.ModelElement#setRepresentation(constellation.drawing.entities.InternalRepresentation)
	 */
	public void setRepresentation( final EntityRepresentation representation ) {
		element.setRepresentation( representation );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#setThickness(int)
	 */
	public void setThickness( final int thickness ) {
		element.setThickness( thickness );
	}

	/* (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color)
	 */
	public void render( final ApplicationController controller,
						final GeometricModel model, 
						final MatrixWCStoSCS matrix, 
						final Rectangle clipper,
						final Graphics2D g, 
						final Color color) {
		element.render( controller, model, matrix, clipper, g, color );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( final Object o ) {
		return element.equals( o );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return element.hashCode();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return element.toString();
	}

}
