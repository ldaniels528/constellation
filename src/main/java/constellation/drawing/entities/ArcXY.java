package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * A pure mathematical representation of a circular arc on a two-dimensional plane.
 * @author lawrence.daniels@gmail.com
 */
public class ArcXY extends RadialCurveXY {
	// get the logger instance
	private final Logger logger = Logger.getLogger( getClass() );
	
	// vertices computation constants
	private static final int SECTIONS 	= 72; // factor of 4
	private static final double DELTA 	= PI / (double)( SECTIONS - 1 );
	private static final double HALF_PI	= PI * 0.5d;
	
	// internal fields
	private final PointXY location;
	private final double angleStart; 
	private final double angleEnd;
	private final double radius;
	
	/**
	 * Creates a new circular arc
	 * @param cp the given {@link PointXY center point} of the arc
	 * @param radius the given radius of the arc
	 * @param angleStart the starting angle of the arc
	 * @param angleEnd the starting angle of the arc
	 */
	public ArcXY( final PointXY cp, 
				  final double radius, 
				  final double angleStart, 
				  final double angleEnd ) {
		this.location	= cp;
		this.radius		= radius;
		this.angleStart	= angleStart;
		this.angleEnd	= angleEnd;
	}
	
	/**
	 * Creates a new circular arc
	 * @param cx the given x-coordinate of the center point of the arc
	 * @param cy the given y-coordinate of the center point of the arc
	 * @param radius the given radius of the arc
	 * @param angleStart the starting angle of the arc
	 * @param angleEnd the starting angle of the arc
	 */
	public ArcXY( final double cx, 
				  final double cy, 
				  final double radius, 
				  final double angleStart, 
				  final double angleEnd ) {
		this( new PointXY( cx, cy ), radius, angleStart, angleEnd );
	}

	/**
	 * Creates a new circular arc using the given circle to determine
	 * the arc's size and position in space.
	 * @param circle the given {@link CircleXY circle}
	 * @param angleStart the starting angle of the arc
	 * @param angleEnd the starting angle of the arc
	 */
	public static ArcXY createArc( final CircleXY circle, 
								   final double angleStart, 
								   final double angleEnd ) {
		return new ArcXY( circle.getMidPoint(), circle.getRadius(), angleStart, angleEnd );
	}
	
	/**
	 * Creates a new arc through the center and end points
	 * @param cp the given center {@link PointXY point} of the arc
	 * @param p1 the starting {@link PointXY point} of the arc
	 * @param p2 the end {@link PointXY point} of the arc
	 * @return an {@link ArcXY arc}
	 */
	public static ArcXY createArcThruCenterAndEndPoints( final PointXY cp, final PointXY p1, final PointXY p2 ) {
		// first create a circle through the points
		final CircleXY circle 	= CircleXY.createCircleByRadius( cp, p1 );
		
		// get the basic values
		final double cx			= circle.getCenterX();
		final double cy			= circle.getCenterY();
		final double radius 	= circle.getRadius();	
		
		// compute the start and end angles
		final double angleStart = PointXY.getAngle( cp, p1 );
		final double angleEnd 	= PointXY.getAngle( cp, p2 );
			
		// return a new arc
		return new ArcXY( cx, cy, radius, angleStart, angleEnd );
	}

	/**
	 * Creates a new arc through three points.
	 * @param p1 the given start {@link PointXY point} of the arc
	 * @param p2 the given middle {@link PointXY point} of the arc
	 * @param p3 the given end {@link PointXY point} of the arc
	 * @return an {@link ArcXY arc}
	 */
	public static ArcXY createArcThru3Points( final PointXY p1, final PointXY p2, final PointXY p3 ) {
		// first create a circle through the points
		final CircleXY circle 	= CircleXY.createCircleThru3Points( p1, p2, p3 );
				
		// get the mid-point of the circle
		final PointXY cp 		= circle.getMidPoint();
		
		// get the basic values
		final double cx			= circle.getCenterX();
		final double cy			= circle.getCenterY();
		final double radius 	= circle.getRadius();	
		
		// compute the start and end angles
		final double angleStart = PointXY.getAngle( cp, p1 );
		final double angleEnd 	= PointXY.getAngle( cp, p3 );
		
		// create and return the arc
		return new ArcXY( cx, cy, radius, angleStart, angleEnd );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#duplicate(constellation.drawing.entities.representations.PointXY)
	 */
	public CurveXY duplicate( double dx, double dy ) {
		final PointXY p = location.getOffset( dx, dy );
		return new ArcXY( p.x, p.y, radius, angleStart, angleEnd );
	}

	/** 
	 * Returns an end-point of the arc that corresponds to the given angle
	 * @param angle the given angle
	 * @return the end {@link PointXY point}
	 */
	public PointXY getArcEndPoint( final double angle ) {
		// get the center point axes
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// compute the end point
		final double a  = angle - HALF_PI;
		final double ex = cx + radius * cos(a);
		final double ey = cy + radius * sin(a);
		
		// create a new line
		return new PointXY( ex, ey );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.ConicArcXY#getAngleStart()
	 */
	public double getAngleStart() {
		return angleStart;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.ConicArcXY#getAngleEnd()
	 */
	public double getAngleEnd() {
		return angleEnd;
	}
	
	/**
	 * Returns the area of the arc (<tt>A=0.5r^2a</tt>) 
	 * @return the area of the arc
	 */
	public double getArea() {
		return 0.5d * radius * radius * ( angleEnd - angleStart );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds(MatrixWCStoSCS matrix) {
		final double rh = radius / 2d; 
		return new RectangleXY( location.x - rh, location.y - rh, radius, radius );
	}
	
	/**
	 * Returns the X-coordinate of the center point
	 * @return the X-coordinate
	 */
	public double getCenterX() {
		return location.getX();
	}
	
	/**
	 * Returns the Y-coordinate of the center point
	 * @return the Y-coordinate
	 */
	public double getCenterY() {
		return location.getY();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getLimits()
	 */
	public VerticesXY getLimits() {
		// get the center point axes
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// get the end points
		final PointXY p1 = getArcEndPoint( angleStart );
		final PointXY p2 = getArcEndPoint( angleEnd );
		logger.info( format( "p1 = %s, p2 = %s", p1, p2 ) );
		
		// return the vertices
		return new VerticesXY(
				cx,		cy,
				p1.x, 	p1.y,
				p2.x, 	p2.y
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return new PointXY( getCenterX(), getCenterY() );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getOffsetCurve(constellation.drawing.entities.representations.PointXY)
	 */
	public CurveXY getParallelCurve( final PointXY offset ) {
		// compute the radius
		final double radius = PointXY.getDistance( getMidPoint(), offset );
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// return the new circle
		return new ArcXY( cx, cy, radius, angleStart, angleEnd );
	}

	/** 
	 * Returns the radius of the curve
	 * @return the radius of the curve
	 */
	public double getRadius() {
		return radius;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.ARC;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.ComplexInternalRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		// determine the start and end angles
		final double start	= angleStart - HALF_PI;
		final double end	= angleEnd - HALF_PI;
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// create the vertices
		final VerticesXY vertices = new VerticesXY( SECTIONS / 2 );
		
		// draw the arc clockwise?
		if( end > start ) {
			for( double t = start; t < end; t += DELTA ) {
				// compute (x,y)
				final double rx = cx + radius * cos(t);
				final double ry = cy + radius * sin(t);
		
				// add the points
				vertices.add( rx, ry );
			}
		}
		
		// draw the arc counter-clockwise
		else {
			for( double t = start; t > end; t -= DELTA ) {
				// compute (x,y)
				final double rx = cx - radius * cos(t);
				final double ry = cy - radius * sin(t);
				
				// add the points
				vertices.add( rx, ry );
			}
		}
		
		// return the points
		return vertices;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects(RectangleXY boundary, MatrixWCStoSCS matrix) {
		// cache the boundary's limits
		final double xa 	= boundary.getMinX();
		final double ya 	= boundary.getMinY();
		final double xb 	= boundary.getMaxX();
		final double yb 	= boundary.getMaxY();
		
		// determine whether a intersection exists
		return  checkIntersection( boundary, xa, ya, xb, ya, radius ) ||
				checkIntersection( boundary, xa, ya, xa, yb, radius ) ||
				checkIntersection( boundary, xb, ya, xb, yb, radius ) ||		
				checkIntersection( boundary, xa, yb, xb, yb, radius );
	}

	/**
	 * Returns the length of the arc  (<tt>A=a * PI * r/180</tt>) 
	 * @return the length of the arc
	 */
	public double length() {
		return ( ( angleEnd - angleStart ) * radius );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public ArcXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY cp = location.mirror( plane );

		// compute the angle of the line (from a horizontal base line)
		final PointXY p1 = plane.p1;
		final LineXY horizLine = new LineXY( p1.x, p1.y, 1d + p1.x, p1.y );
		final double angle = horizLine.getAcuteAngle( plane );
		
		// create the arc
		return new ArcXY( cp, radius, angle - angleStart, angle - angleEnd );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color)
	 */
	public void render( final ApplicationController controller, 
						final GeometricModel model,
						final MatrixWCStoSCS matrix, 
						final Rectangle clipper, 
						final Graphics2D g,
						final Color color) {
		EntityRepresentationUtil.render( controller, model, matrix, clipper, g, this, color );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "(x+%3.2f)^2 + (y+%3.2f)^2 = (%3.2f)^2", getCenterX(), getCenterY(), radius );
	}
	
}