package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.ComplexInternalRepresentation;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * A pure mathematical representation of a spiral on a two-dimensional plane.
 * @see http://en.wikipedia.org/wiki/Circle">Wikipedia reference
 * @see http://www.mathematische-basteleien.de/spiral.htm
 * @author lawrence.daniels@gmail.com
 */
public class SpiralXY extends RadialCurveXY implements ComplexInternalRepresentation {
	private static final int SECTIONS = 72;
	
	// internal fields
	private final PointXY location;
	private final double radiusInner;
	private final double radiusOuter;
	private final double increment;
	private final int revolutions;
	private final boolean clockWise;
	
	/**
	 * Creates a new spiral
	 * @param cx the given x-coordinate of the center point of the spiral 
	 * @param cy the given y-coordinate of the center point of the spiral
	 * @param radius the given initial radius of the spiral
	 * @param increment the given radial increment
	 * @param revolutions the given number of spiral revolutions
	 */
	public SpiralXY( final double cx, 
			    	 final double cy, 
			    	 final double radius,
			    	 final double increment,
			    	 final int revolutions ) {
		this( new PointXY( cx, cy ), radius, increment, revolutions );
	}
	
	/**
	 * Creates a new spiral
	 * @param cx the given x-coordinate of the center point of the spiral
	 * @param cy the given y-coordinate of the center point of the spiral
	 * @param radius the given initial radius of the spiral
	 * @param increment the given radial increment
	 * @param revolutions the given number of spiral revolutions
	 */
	public SpiralXY( final PointXY p, 
			    	 final double radius,
			    	 final double increment,
			    	 final int revolutions ) {
		this.location		= p;
		this.increment		= increment;
		this.revolutions	= revolutions;
		this.radiusInner	= radius;
		this.radiusOuter	= computeSize();
		this.clockWise		= false;
	}
	
	/* (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#duplicate(constellation.drawing.entities.representations.PointXY)
	 */
	public SpiralXY duplicate( double dx, double dy ) {
		return new SpiralXY( location.getX() + dx, location.getY() + dy, radiusInner, increment, revolutions );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		final double x	= getCenterX();
		final double y 	= getCenterY();
		final double r 	= computeSize();
		final double d 	= r * 2.0d;
		return new RectangleXY( x - r, y - r, d, d );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.CURVE;
	}

	/** 
	 * Returns the X-axis of the center position
	 * @return the X-axis of the center position
	 */
	public double getCenterX() {
		return location.getX();
	}
	
	/**
	 * Returns the Y-axis of the center position
	 * @return the Y-axis of the center position
	 */
	public double getCenterY() {
		return location.getY();
	}
	
	/**
	 * Indicates whether the spiral is computed in
	 * a clockwise (or conversely counter-clockwise) method.
	 * @return true, if the spiral is clockwise
	 */
	public boolean isClockWise() {
		return clockWise;
	}
	
	/** 
	 * Returns the radial increment
	 * @return the radial increment
	 */
	public double getIncrement() {
		return increment;
	}

	/* (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getLimits()
	 */
	public VerticesXY getLimits() {
		// get the center point axes
		final double cx = getCenterX();
		final double cy = getCenterY();
		final double r 	= radiusOuter;
		
		// return the vertices
		return new VerticesXY(
				cx - r, cy,
				cx + r, cy,
				cx,   	cy,
				cx, 	cy - r,
				cx, 	cy + r
		);
	}

	/* (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return location;
	}

	/** 
	 * Returns the end point of the spiral
	 * @return the {@link PointXY end point}
	 */
	public PointXY getEndPoint() {
		return new PointXY( location.x - radiusOuter, location.y );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.CurveXY#getParallelCurve(constellation.drawing.entities.PointXY)
	 */
	@Override
	public CurveXY getParallelCurve( final PointXY offset ) {
		// compute the radius
		final double radius = PointXY.getDistance( getMidPoint(), offset );
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// return the new circle
		return new CircleXY( cx, cy, radius );
	}

	/** 
	 * Returns the inner radius of the spiral
	 * @return the inner radius of the spiral
	 */
	public double getRadius() {
		return radiusInner;
	}

	/** 
	 * Returns the outer radius of the spiral
	 * @return the outer radius of the spiral
	 */
	public double getOuterRadius() {
		return radiusOuter;
	}
	
	/** 
	 * Returns the number of revolutions
	 * @return the number of revolutions
	 */
	public int getRevolutions() {
		return revolutions;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		final double cx = getCenterX();
		final double cy = getCenterY();
		final double delta = PI / (double)SECTIONS;

		// get the initial radius
		double r = this.radiusInner;
		
		// create the vertex set
		final VerticesXY vertices = new VerticesXY( SECTIONS );
		
		// create the points
		for( int n = 0; n < revolutions; n++ ) {
			for( double a = -PI; a < PI; a += delta ) {
				// compute (x,y)
				final double x = cx + ( r * cos(a) );
				final double y = cy + ( r * sin(a) );
				
				// add the point
				vertices.add( x, y );
				
				// increment the radius
				r += increment;
			}
		}
		return vertices;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.SPIRAL;
	}
	
	/* (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#length()
	 */
	public double length() {
		// See http://www.newton.dep.anl.gov/askasci/math99/math99015.htm
		// L = pi*N*(D+d)/2
		return PI * revolutions * ( radiusInner + ( radiusInner * revolutions * increment ) ) / 2d;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public SpiralXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new SpiralXY( p, radiusInner, increment, revolutions );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.intersects( getBounds( matrix ) );
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
		EntityRepresentationUtil.render( controller, model, matrix, clipper, g, this, color );
	}

	/**
	 * Computes the radial dimension of the spiral
	 * @return the radial dimension of the spiral
	 */
	private double computeSize() {
		final double delta = PI / (double)SECTIONS;
		double size = radiusInner;
		for( int n = 0; n < revolutions; n++ ) {
			for( double a = -PI; a < PI; a += delta ) {
				size += increment;
			}
		}
		return size;
	}

	/* (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#toEquation()
	 */
	public String toString() {
		return format( "Spiral(%3.4f x %3.4f)@(%3.4f,%3.4f)", radiusInner, increment, getCenterX(), getCenterY() );
	}

}
