package constellation.drawing.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityTypes;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents a composition of geometric representations
 * @author lawrence.daniels@gmail.com
 */
public class CompositionXY implements EntityRepresentation {
	private final List<EntityRepresentation> elements;
	
	/**
	 * Default Constructor
	 */
	public CompositionXY() {
		this.elements = new LinkedList<EntityRepresentation>();
	}
	
	/**
	 * Creates a new composite geometric representation
	 * @param elements the given array of {@link CompositionXY complex geometric representations}
	 */
	public CompositionXY( final Collection<EntityRepresentation> elements ) {
		this.elements = new ArrayList<EntityRepresentation>( elements );
	}
	
	/**
	 * Creates a new composite geometric representation
	 * @param elements the given collection of {@link CompositionXY complex geometric representations}
	 */
	public CompositionXY( final EntityRepresentation ... elements ) {
		this.elements = new ArrayList<EntityRepresentation>( Arrays.asList( elements ) );
	}
	
	/** 
	 * Creates a new rectangle
	 * @param xa the x-coordinate of the starting point
	 * @param ya the y-coordinate of the starting point
	 * @param xb the x-coordinate of the end point
	 * @param yb the y-coordinate of the end point
	 * @return a new {@link CompositionXY rectangle}
	 */
	public static CompositionXY createRectangle( final double xa, 
												 final double ya, 
												 final double xb, 
												 final double yb ) {		
		return new CompositionXY(
			new LineXY( xa, ya, xb, ya ),
			new LineXY( xa, ya, xa, yb ),
			new LineXY( xb, ya, xb, yb ),
			new LineXY( xa, yb, xb, yb )
		);
	}
	
	/** 
	 * Creates a new rectangle
	 * @param startPoint the starting {@link PointXY point} of the rectangle
	 * @param endPoint the end {@link PointXY point} of the rectangle
	 * @return a new {@link CompositionXY rectangle}
	 */
	public static CompositionXY createRectangle( final PointXY start, final PointXY end ) {
		return createRectangle( start.getX(), start.getY(), end.getX(), end.getY() );
	}
	
	/** 
	 * Adds the given complex geometric element to the composition
	 * @param element the given {@link EntityRepresentation internal element}
	 */
	public void add( final EntityRepresentation element ) {
		elements.add( element );
	}
	
	/** 
	 * Adds the given complex geometric element to the composition
	 * @param elements the given {@link EntityRepresentation internal elements}
	 */
	public void addAll( final EntityRepresentation ... elementArray ) {
		elements.addAll( Arrays.asList( elementArray ) );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CompositionXY duplicate( double dx, double dy ) {
		final CompositionXY comp = new CompositionXY();
		for( final EntityRepresentation element : elements ) {
			comp.add( element.duplicate( dx, dy ) );
		}
		return comp;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		final RectangleXY bounds = !elements.isEmpty() ? elements.get( 0 ).getBounds( matrix ) : null;
		if( bounds != null ) {
			for( final EntityRepresentation element : elements ) {
				bounds.add( element.getBounds( matrix ) );
			}
		}
		return bounds;
	}

	/** 
	 * Returns the elements that make up this composite element
	 * @return the elements that make up this composite element
	 */
	public List<EntityRepresentation> getElements() {
		return elements;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityTypes getType() {
		return EntityTypes.COMPOSITION;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.COMPOUND;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		for( final EntityRepresentation element : elements ) {
			if( element.intersects( boundary, matrix ) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CompositionXY mirror( final LineXY plane ) {
		final CompositionXY comp = new CompositionXY();
		for( final EntityRepresentation element : elements ) {
			comp.add( element.mirror( plane ) );
		}
		return comp;
	}

	/**
	 * {@inheritDoc}
	 */
	public void render( final ApplicationController controller, 
						final GeometricModel model,
						final MatrixWCStoSCS matrix, 
						final Rectangle clipper, 
						final Graphics2D g,
						final Color color) {
		for( final EntityRepresentation element : elements ) {
			element.render( controller, model, matrix, clipper, g, color );
		}
	}

}
