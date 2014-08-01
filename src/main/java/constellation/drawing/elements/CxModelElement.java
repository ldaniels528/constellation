package constellation.drawing.elements;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.drawing.LinePatterns;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.VerticesXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents a generic model element (e.g. text notes, curves, lines, and points)
 * @author lawrence.daniels@gmail.com
 */
public class CxModelElement implements ModelElement, Cloneable {
	private EntityRepresentation entity;
	private LinePatterns pattern;
	private Color color;
	private String label;
	private int thickness;
	private int layer;
	
	/**
	 * Creates a new geometric entity
	 * @param type the given geometry type
	 */
	public CxModelElement( final EntityRepresentation representation ) {
		this.entity 	= representation;
		this.color		= null;
		this.pattern	= LinePatterns.PATTERN_UNSPEC;
		this.layer		= -1;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} 
		catch( final CloneNotSupportedException cause ) {
			throw new IllegalStateException( format( "Unable to clone class '%s'", getClass().getName() ) );
		}
	}
	
	/** 
	 * Returns an array of model elements based on the given internal representations
	 * @param representations the given {@link EntityRepresentation internal representations}
	 * @return the {@link ModelElement model elements}
	 */
	public static ModelElement[] createElements( final EntityRepresentation[] representations ) {
		final ModelElement[] elements = new ModelElement[ representations.length ];
		for( int n = 0; n < elements.length; n++ ) {
			elements[n] = new CxModelElement( representations[n] );
		}
		return elements;
	}
	
	/** 
	 * Returns an array of model elements based on the given set of vertices
	 * @param vertices the given {@link VerticesXY set of vertices}
	 * @return the {@link ModelElement model elements}
	 */
	public static ModelElement[] createElements( final VerticesXY vertices ) {
		final PointXY[] points = vertices.explode();
		final ModelElement[] elements = new ModelElement[ points.length ];
		for( int n = 0; n < elements.length; n++ ) {
			elements[n] = new CxModelElement( points[n] );
		}
		return elements;
	}
	
	/** 
	 * Copies the properties (color, pattern, and layer) of the 
	 * source element to destination element.
	 * @param src the given source {@link ModelElement element}
	 * @param dest the given destination {@link ModelElement element}
	 */
	public static void copyProperties( final ModelElement src, final ModelElement dest ) {
		dest.setColor( src.getColor() );
		dest.setPattern( src.getPattern() );
		dest.setLayer( src.getLayer() );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public final boolean equals( final Object object ) {
		if( object instanceof ModelElement ) {
			final ModelElement element = (ModelElement)object;
			return label.equals( element.getLabel() );
		}
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		return entity.getBounds( matrix );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#getColor()
	 */
	public Color getColor() {
		return color;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#setColor(constellation.drafting.LineColors)
	 */
	public ModelElement setColor( final Color color ) {
		this.color = color;
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.GeometricElement#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.GeometricElement#setLabel(java.lang.String)
	 */
	public void setLabel( final String label ) {
		this.label = label;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.GeometricElement#isLabeled()
	 */
	public boolean isLabeled() {
		return ( label != null );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#getLayer()
	 */
	public int getLayer() {
		return layer;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#setLayer(constellation.layers.Layer)
	 */
	public void setLayer( final int layer ) {
		this.layer = layer;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#getPattern()
	 */
	public LinePatterns getPattern() {
		return pattern;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#setPattern(int)
	 */
	public void setPattern( final LinePatterns pattern ) {
		this.pattern = pattern;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getRepresentation()
	 */
	public EntityRepresentation getRepresentation() {
		return entity;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#setRepresentation(constellation.drawing.EntityRepresentation)
	 */
	public void setRepresentation( final EntityRepresentation representation ) {
		this.entity = representation;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getThickness()
	 */
	public int getThickness() {
		return thickness;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#setThickness(int)
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.Geometry#getType()
	 */
	public EntityTypes getType() {
		return entity.getType();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.model.ModelElement#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return entity.getCategoryType();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.ModelElement#intersects(constellation.drawing.entities.representations.BoundaryXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return entity.intersects( boundary, matrix ); 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public final int hashCode() {
		return ( label != null ) ? label.hashCode() : -1;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color, boolean)
	 */
	public void render( final ApplicationController controller, 
						final GeometricModel model,
						final MatrixWCStoSCS matrix, 
						final Rectangle clipper, 
						final Graphics2D g,
						final Color color) {
		entity.render( controller, model, matrix, clipper, g, color );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return label;
	}

}
