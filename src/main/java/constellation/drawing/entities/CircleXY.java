package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.CxIntersectionUtil;
import constellation.math.CxMathUtil;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * A pure mathematical representation of a circle on a two-dimensional plane.
 * <div><a href="http://en.wikipedia.org/wiki/Circle">Wikipedia reference</a></div>
 * @author lawrence.daniels@gmail.com
 */
public class CircleXY extends RadialCurveXY {
	private static final int SECTIONS = 72; // factor of 4
	private final PointXY location;
	private final double radius;
	
	/**
	 * Creates a new circle
	 * @param cp the given {@link PointXY center point}
	 * @param radius the given radius of the circle
	 */
	public CircleXY( final PointXY cp, 
			    	 final double radius ) {
		this.location	= cp;
		this.radius		= radius;
	}
	
	/**
	 * Creates a new circle
	 * @param cx the given x-coordinate of the center point of the circle
	 * @param cy the given y-coordinate of the center point of the circle
	 * @param radius the given radius of the circle
	 */
	public CircleXY( final double cx, 
			    	 final double cy, 
			    	 final double radius ) {
		this( new PointXY( cx, cy ), radius );
	}
	
	/**
	 * Creates a new circle using the given start and end points
	 * @param startPoint the starting point of the circle
	 * @param endPoint the end point of the circle
	 * @return a new {@link CircleXY circle}
	 */
	public static CircleXY createCircleByDiameter( final PointXY startPoint, final PointXY endPoint ) {		
		final double HALF = 2.0d;
		
		// get the x, y, and radius
		final double x1 = startPoint.getX();
		final double y1 = startPoint.getY();
		final double x2 = endPoint.getX();
		final double y2 = endPoint.getY();
		
		// compute the center point
		final double cx = ( x1 + x2 ) / HALF;
		final double cy = ( y1 + y2 ) / HALF;
		
		// compute the radius
		final double radius = PointXY.getDistance( startPoint, endPoint ) / HALF;
		
		// create the circle geometry
		return new CircleXY( cx, cy, radius );
	}

	/**
	 * Creates a new circle using the given start and end points
	 * @param centerPoint the center point of the circle
	 * @param endPoint the end point of the circle
	 * @return a new {@link CircleXY circle}
	 */
	public static CircleXY createCircleByRadius( final PointXY centerPoint, final PointXY endPoint ) {
		// get the cx, cy, and radius
		final double cx 	= centerPoint.getX();
		final double cy 	= centerPoint.getY();
		final double radius = PointXY.getDistance( centerPoint, endPoint );
		
		// create the circle geometry
		return new CircleXY( cx, cy, radius );
	}

	/**
	 * Creates a new circle using the given three points
	 * <br><a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/circlefrom3/">Reference</a>
	 * @param p1 the point #1 of the circle
	 * @param p2 the point #2 of the circle
	 * @param p3 the point #3 of the circle
	 * @return a new {@link CircleXY circle}
	 */
	public static CircleXY createCircleThru3Points( final PointXY p1, final PointXY p2, final PointXY p3 ) {
		// create line A: between points P1 & P2
		final LineXY lineA = new LineXY( p1, p2 );
		
		// create line B: between points P2 & P3
		final LineXY lineB = new LineXY( p2, p3 );		
		
		// get the mid points of lines A & B
		final PointXY mpA = lineA.getMidPoint();
		final PointXY mpB = lineB.getMidPoint();
		
		// create the center lines (normal to Lines A & B)
		final LineXY lineNA = LineXY.createNormalLine( lineA, mpA, PointXY.getPointAlongLine( lineA, mpA, 10 ) );
		final LineXY lineNB = LineXY.createNormalLine( lineB, mpB, PointXY.getPointAlongLine( lineB, mpB, 10 ) );
		
		// get the center point (the intersection of NA & NB)
		final PointXY cp = CxIntersectionUtil.getIntersectionPoint( lineNA, lineNB );
		
		// get the x, y, and radius
		final double x 		= cp.getX();
		final double y 		= cp.getY();
		final double radius	= PointXY.getDistance( cp, p1 );
		
		// create the circle geometry
		return new CircleXY( x, y, radius );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.geom.RectangularShape#contains(java.awt.geom.PointXY)
	 */
	public boolean contains( final PointXY point ) {
		// equation: x = sqrt(r^2 - y^2)
		final double y = point.getY();
		final double x = Math.sqrt( Math.pow( radius, 2 ) - Math.pow( y, 2 ) );
		
		// if the x's are equal, we're good
		return CxMathUtil.isEqual( point.getX(), x );
	}
	
	/**
	 * Checks for an intersection between this circle and the given rectangle
	 * <div>See <a href="http://ftp.arl.mil/ftp/pub/Gems/original/CircleRectIntersect.c">Circle and Rectangle intersecton</a></div>
	 * @param boundary the given {@link RectangleXY boundary}
	 * @return true, if the rectangle contains or is contained within the given rectangle
	 */
	public boolean contains( final RectangleXY boundary ) {
		// cache the center points
		final double cx = this.getCenterX();
		final double cy = this.getCenterY();
		
		// compute the radius squared
		final double radsq = radius * radius;
		
		// Translate coordinates, placing circle at the origin. 
		final double x1 = boundary.getMinX() - cx;
		final double y1 = boundary.getMinY() - cy;
		final double x2 = boundary.getMaxX() - cx;
		final double y2 = boundary.getMaxY() - cy;
	
		 // radius to left of circle center 
		 if( x2 < 0 ) { 			
			 // radius in lower left corner 
		   	if( y2 < 0 ) { 		
		     	return ((x2 * x2 + y2 * y2) < radsq);
		   	}
		   	// radius in upper left corner 
		   	else if( y1 > 0 ) {
		     	return ((x2 * x2 + y1 * y1) < radsq);
		   	}
		   	// radius due West of circle 
		   	else {
		     	return( abs( x2 ) < radius );
		   	}
		 }
		 // radius to right of circle center 
		 else if( x1 > 0 ) {
			 // radius in lower right corner 
		   	if( y2 < 0 ) {	
		     	return ( ( x1 * x1 ) < radsq );
		   	}
		   	// radius in upper right corner 
		   	else if( y1 > 0 )  {
		     	return ( ( x1 * x1 + y1 + y1 ) < radsq );
		   	}
		   	// radius due East of circle 
		   	else {	
		     	return ( x1 < radius );
		   	}
		 }
		 // radius on circle vertical center-line 
		 else {
			 // radius due South of circle 
		   	if( y2 < 0 ) {
		     	return ( abs(y2) < radius );
		   	}
		   	// radius due North of circle 
		   	else if( y1 > 0 ) {
		     	return (y1 < radius);
		   	}
		   	// radius contains circle center-point 
		   	else { 					
		     	return true;
		   	}
		 }
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CurveXY#duplicate(double, double)
	 */
	public CurveXY duplicate( final double dx, final double dy ) {
		final PointXY p = location.getOffset( dx, dy );
		return new CircleXY( p.x, p.y, radius );
	}

	/**
	 * Returns the area of the circle (<tt>A=PI * r^2</tt>)
	 * @return the area of the circle
	 */
	public double getArea() {
		return PI * radius * radius;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
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
	
	/**
	 * Returns the circumference of the circle (<tt>C=2*PI*r</tt>)
	 * @return the circumference of the circle
	 */
	public double getCircumference() {
		return 2d * PI * radius;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getLimits()
	 */
	public VerticesXY getLimits() {
		// get the center point axes
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// return the vertices
		return new VerticesXY(
				cx - radius, cy,
				cx + radius, cy,
				cx, 		 cy,
				cx, 		 cy - radius,
				cx, 		 cy + radius
		);
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getMidPoint()
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
		return new CircleXY( cx, cy, radius );
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
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.CIRCLE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		// compute the necessary values
		final double delta 	= PI / (double)( SECTIONS - 1 );
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// create the vertex set
		final VerticesXY vertices = new VerticesXY( SECTIONS );
		
		// construct the ellipse
		for( double a =  -PI; a < +PI; a += delta ) {
			// compute (x,y)
			final double px = cx + radius * cos(a);
			final double py = cy + radius * sin(a);
	
			// add the points
			vertices.add( px, py );
		}
		
		// add the last point to close the curve
		vertices.close();
		
		// return the points
		return vertices;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
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
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#length()
	 */
	public double length() {
		return 2d * PI * radius; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public CircleXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new CircleXY( p, radius );
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
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "%s, r = %3.4f", location, radius );
	}

}