package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.ScratchPad;
import constellation.math.CxIntersectionUtil;
import constellation.math.CxMathUtil;
import constellation.math.MatrixWCStoSCS;
import constellation.math.CxIntersectionUtil.Intersection;
import constellation.math.CxMathUtil.Quadrant;
import constellation.model.GeometricModel;

/**
 * Represents a construction point for complex geometry
 * @author lawrence.daniels@gmail.com
 */
public class PointXY implements EntityRepresentation {
	// constants
	public static final int POINT_FULL	= 6;
	public static final int POINT_HALF	= POINT_FULL / 2;
	
	// internal fields
	public double x;
	public double y;
	
	/**
	 * Default constructor
	 */
	public PointXY() {
		this( 0d, 0d );
	}
	
	/**
	 * Creates a new vertex point
	 * @param x the given X-axis coordinate
	 * @param y the given Y-axis coordinate
	 */
	public PointXY( final double x, final double y ) {
		this.x = x;
		this.y = y;
	}
	
	/** 
	 * Computes the angle through the given center- and end- points
	 * @param line the given {@link LineXY line}
	 * @return the angle (in radians)
	 */
	public static double getAngle( final PointXY cp, final PointXY ep ) {
		return CxMathUtil.getAngle( cp.x, cp.y, ep.x, ep.y );
	}

	/**
	 * Returns the closest of the end points to the given source point.
	 * @param srcPt the given {@link PointXY point}
	 * @param points the given {@link PointXY points}
	 * @return the closest point within the given set of points 
	 */
	public static PointXY getClosestPoint( final PointXY srcPt, final PointXY... points ) {
		double shortestDistance = java.lang.Double.MAX_VALUE;
		PointXY selectedPt = null;
		
		// evaluate each point
		for( final PointXY p : points ) {
			// get the distance between the points
			final double distance = getDistance( p, srcPt );
			
			// is the distance shorter than the shortest distance
			if( distance < shortestDistance ) {
				shortestDistance = distance;
				selectedPt = p;
			}
		}
		
		// return the closest point
		return selectedPt; 
	}

	/**
	 * Returns the closest of the end points to the given source point.
	 * @param srcPt the given {@link PointXY point}
	 * @param vertices the given {@link VerticesXY vertices}
	 * @return the closest point within the given set of points 
	 */
	public static PointXY getClosestPoint( final PointXY srcPt, final VerticesXY vertices ) {
		// get the point array
		final PointXY[] points = EntityRepresentationUtil.fromLimits( vertices );
		
		// perform the search
		return getClosestPoint( srcPt, points );
	}

	/**
	 * Returns the distance between two points, from (x1,y1) to (x2,y2)
	 * <b><a href="http://freespace.virgin.net/hugo.elias/routines/r_dist.htm">Reference</a>
	 * @param x1 the x-coordinate of the first point
	 * @param y1 the y-coordinate of the first point
	 * @param x2 the x-coordinate of the second point
	 * @param y2 the y-coordinate of the second point
	 * @return the distance between the points
	 */
	public static double getDistance( final double x1, 
									  final double y1, 
									  final double x2, 
									  final double y2 ) {
		return sqrt( pow( y1 - y2, 2 ) + pow( x1 - x2, 2 ) );
	}

	/**
	 * Returns the distance between two points
	 * <b><a href="http://freespace.virgin.net/hugo.elias/routines/r_dist.htm">Reference</a>
	 * @param p1 the first of two {@link PointXY points}
	 * @param p2 the second of two {@link PointXY points}
	 * @return the distance between the points
	 */
	public static double getDistance( final PointXY p1, final PointXY p2 ) {
		return sqrt( pow( p1.getY() - p2.getY(), 2 ) + pow( p1.getX() - p2.getX(), 2 ) );
	}

	/**
	 * Returns the farthest of the end points to the given source point.
	 * @param srcPt the given {@link PointXY point}
	 * @param points the given {@link PointXY points}
	 * @return the farthest point within the given set of points 
	 */
	public static PointXY getFarthestPoint( final PointXY srcPt, final PointXY... points ) {
		double fartestDistance = -java.lang.Double.MAX_VALUE;
		PointXY selectedPt = null;
		
		// evaluate each point
		for( final PointXY p : points ) {
			// get the distance between the points
			final double distance = getDistance( p, srcPt );
			
			// is the distance shorter than the shortest distance
			if( distance > fartestDistance ) {
				fartestDistance = distance;
				selectedPt = p;
			}
		}
		
		// return the closest point
		return selectedPt; 
	}

	/**
	 * Returns the farthest of the end points to the given source point.
	 * @param srcPt the given {@link PointXY point}
	 * @param vertices the given {@link VerticesXY vertices}
	 * @return the farthest point within the given set of points 
	 */
	public static PointXY getFarthestPoint( final PointXY srcPt, final VerticesXY vertices ) {
		// get the point array
		final PointXY[] points = EntityRepresentationUtil.fromLimits( vertices );
		
		// perform the search
		return getFarthestPoint( srcPt, points );
	}

	/**
	 * Attempts to determine the intersection point between the lines #1 and line #2
	 * @param line1 the given {@link LineXY line A}
	 * @param line2 the given {@link LineXY line B}
	 * @return the intersection point or <tt>null</tt> if no intersection exists
	 */
	public static PointXY getIntersectionPoint( final LineXY line1, final LineXY line2 ) {
		return CxIntersectionUtil.getIntersectionPoint( line1, line2 );
	}
	
	/**
	 * Returns the mid-point between the given points
	 * @param x1 the given x-coordinate of point #1
	 * @param y1 the given y-coordinate of point #1
	 * @param x2 the given x-coordinate of point #2
	 * @param y2 the given y-coordinate of point #2
	 * @return the {@link PointXY mid-point}
	 */
	public static PointXY getMidPoint( final double x1, final double y1, final double x2, final double y2 ) {
		final double x = ( x1 + x2 ) / 2.0d;
		final double y = ( y1 + y2 ) / 2.0d;
		return new PointXY( x, y );
	}

	/**
	 * Returns the mid-point between the given points
	 * @param p1 one of two points
	 * @param p2 two of two points
	 * @return the {@link PointXY mid-point}
	 */
	public static PointXY getMidPoint( final PointXY p1, final PointXY p2 ) {
		final double x = ( p1.x + p2.x ) / 2d;
		final double y = ( p1.y + p2.y ) / 2d;
		return new PointXY( x, y );
	}

	/** 
	 * Returns an offset point that follows the path of 
	 * the given line for the given distance
	 * @param line the given {@link LineXY line}
	 * @param startPoint the given {@link PointXY start point}
	 * @param distance the given distance vector (negative values = toward, positive values = away)
	 * @return the resultant {@link PointXY point}
	 */
	public static PointXY getPointAlongLine( final LineXY line, 
											  final PointXY startPoint,
											  final double distance ) {
		// cache the X- and Y-values of the starting point
		final double sx = startPoint.x;
		final double sy = startPoint.y;
		
		// is the line horizontal?
		if( line.isHorizontal() ) {
			return new PointXY( sx + distance, sy );
		}
		
		// is the line vertical?
		else if( line.isVertical() ) {
			return new PointXY( sx, sy + distance );
		}
		
		// must be oblique
		else {
			final double m = line.getSlope();
			final double x = sx +  distance;
			final double y = m * x + line.getYIntecept();
			return new PointXY( x, y );
		}
	}

	/** 
	 * Returns a point that offsets the given source point by DX and DY
	 * @param srcPt the given source {@link PointXY point}
	 * @param dx the given X-delta value
	 * @param dy the given Y-delta value
	 * @return the offset {@link PointXY point}
	 */
	public static PointXY getOffsetPoint( final PointXY srcPt, final double dx, final double dy ) {
		return new PointXY( srcPt.x + dx, srcPt.y + dy );
	}
	
	/** 
	 * Projects the given point onto the circle, and returns the new projected point.
	 * @param circle the given {@link CircleXY circle}
	 * @param p the given {@link PointXY point} to project onto the circle
	 * @return the new projected {@link PointXY point} or <tt>null</tt> if not intersection
	 */
	public static PointXY getPointOnCircle( final CircleXY circle, final PointXY p ) {
		// create the line from the center-point through the end-point 
		final LineXY line = new LineXY( circle.getMidPoint(), p );
	
		// project the angle start point onto the circle
		final Intersection isct = PointXY.intersection( circle, line );
		
		// create a new line
		return !isct.hasError() ? isct.getPoints().get( 0 ) : null;
	}
	
	/** 
	 * Projects the given point onto the circle, and returns the new projected point.
	 * @param circle the given {@link CircleXY circle}
	 * @param angle the given angle of the imaginary line that intersections the circle
	 * @return the new projected {@link PointXY point} 
	 */
	public static PointXY getPointOnCircle( final CircleXY circle, final double angle ) {
		// get the center point axes
		final double cx = circle.getCenterX();
		final double cy = circle.getCenterY();
		
		// compute the end point
		final double r = circle.getRadius();
		final double a  = angle - PI/2d;
		final double ex = cx + r * cos(a);
		final double ey = cy + r * sin(a);
		
		// create a new line
		return new PointXY( ex, ey );
	}
	
	/** 
	 * Projects the given point onto the ellipse, and returns the new projected point.
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @param angle the given angle of the imaginary line that intersections the ellipse
	 * @return the new projected {@link PointXY point} 
	 */
	public static PointXY getPointOnEllipse( final EllipseXY ellipse, final double angle ) {
		// get the center point axes
		final double cx = ellipse.getCenterX();
		final double cy = ellipse.getCenterY();
		
		// compute the end point
		final double a = ellipse.getWidth() / 2d;
		final double b = ellipse.getHeight() / 2d;
		final double t  = angle - PI/2d;
		final double ex = cx + a * cos(t);
		final double ey = cy + b * sin(t);
		
		// create a new line
		return new PointXY( ex, ey );
	}

	/** 
	 * Projects the given point onto the given curve.
	 * @param pointToProject the given {@link PointXY point} to project.
	 * @param curve the given {@link CurveXY curve}
	 * @return the projected {@link PointXY point}
	 */
	public static PointXY getProjectedPoint( final PointXY pointToProject, final CurveXY curve ) {
		// is the curve a circle?
		if( curve instanceof CircleXY ) {
			// get a point that's on the circle
			final CircleXY circle = (CircleXY)curve;
			final double x = pointToProject.getX();
			final double y = CircleXY.computeRadialYValue( circle.getRadius(), x );
			return new PointXY( x, y );
		}
		return null;
	}

	/** 
	 * Projects the given point onto the given line.
	 * @param p the given {@link PointXY point} to project.
	 * @param line the given {@link LineXY line}
	 * @return the projected {@link PointXY point}
	 */
	public static PointXY getProjectedPoint( final PointXY p, final LineXY line ) {
		// get a point that's on the line
		final PointXY pointOnLine = line.getBeginPoint();
		double x,y;
		
		// is the line horizontal?
		if( line.isHorizontal() ) {
			x = p.getX();
			y = line.getY1();
		}
		
		// is the line vertical?
		else if( line.isVertical() ) {
			x = line.getX1();
			y = p.getY();			
		}
		
		// the line must be oblique
		else {
			// compute the slope and Y-intercept
			final double m = line.getSlope();
			final double b = LineXY.computeYIntercept( pointOnLine.getX(), pointOnLine.getY(), m );
			
			// compute the (x,y) coordinate for the new point
			x = p.getX();
			y = m * x + b;
		}
		
		// return the point
		return new PointXY( x, y );
	}

	/** 
	 * Returns the quadrant the given end point is located in
	 * @param cp the given center {@link PointXY point} of the curve
	 * @param ep the given end {@link PointXY point} of the curve
	 * @return the {@link Quadrant quadrant}
	 */
	public static Quadrant getQuadrant( final PointXY cp, final PointXY ep ) {
		return CxMathUtil.getQuadrant( cp.x, cp.y, ep.x, ep.y );
	}

	/**
	 * Attempts to determine the intersection point between the given
	 * internal representations (e.g. lines or curves).
	 * @param elemA the given {@link EntityRepresentation element A}
	 * @param elemB the given {@link EntityRepresentation element B}
	 * @return the {@link Intersection intersection}
	 */
	public static Intersection intersection( final EntityRepresentation elemA, final EntityRepresentation elemB ) {
		return CxIntersectionUtil.getIntersectionPoints( elemA, elemB );
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
			throw new IllegalStateException( format( "Error cloning class '%s'", getClass().getName() ), cause );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#duplicate(double, double)
	 */
	public PointXY duplicate( final double dx, final double dy) {
		return new PointXY( x + dx, y + dy );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		return new RectangleXY( x, y, 1, 1 );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return this;
	}

	/** 
	 * Returns a new point whose location is an offset from the host point's location
	 * @param dx the given X-axis offset
	 * @param dy the given Y-axis offset
	 * @return the {@link PointXY point}
	 */
	public PointXY getOffset( final double dx, final double dy ) {
		return new PointXY( x + dx, y + dy );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.POINT;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.VERTEX;
	}
	
	/** 
	 * Returns the X-axis coordinate
	 * @return the X-axis coordinate
	 */
	public double getX() {
		return x;
	}
	
	/** 
	 * Returns the Y-axis coordinate
	 * @return the Y-axis coordinate
	 */
	public double getY() {
		return y;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.intersectsLine( x, y, x, y );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.model.GeometricModel, constellation.drawing.entities.LineXY)
	 */
	public PointXY mirror( final LineXY plane ) {
		// is the plane horizontal?
		if( plane.isHorizontal() ) {
			final double dy = 2d * ( y - plane.p1.y );
			return new PointXY( x, y - dy );
		}
		
		// is the plane vertical?
		else if( plane.isVertical() ) {
			final double dx = 2d * ( x - plane.p1.x );
			return new PointXY( x - dx, y );
		}
		
		// is the plane oblique?
		else {
			// define the line normal to the plane through the end points
			final LineXY nl = LineXY.createNormalLine( plane, this, 1d );
			
			// get the intersection between the normal and the plane
			final PointXY ixp = CxIntersectionUtil.getIntersectionPoint( nl, plane );
			
			// get the delta values
			final double dx = 2d * ( x - ixp.x );
			final double dy = 2d * ( y - ixp.y );

			// create the mirrored point
			return new PointXY( x - dx, y - dy );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.functions.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color, boolean)
	 */
	public void render( final ApplicationController controller,
						final GeometricModel model,
						final MatrixWCStoSCS matrix,
						final Rectangle clipper, 
						final Graphics2D g, 
						final Color color ) {
		// get a space point from the scratch pad
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// translate the point to screen coordinates
		matrix.transform( this, screenPt );
		
		// get the (x,y) model coordinates of the point
		final int dx = screenPt.x;
		final int dy = screenPt.y;
		
		// get the (x,y) coordinates and height and width of the point
		final int x = dx - POINT_HALF;
		final int y = dy - POINT_HALF;
		
		// is the point is visible ...
		if( clipper.intersects( x, y, POINT_FULL, POINT_FULL ) ) {
			// draw the point 
			g.setColor( color );
			g.fillOval( x, y, POINT_FULL, POINT_FULL );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.RenderableElement#render(constellation.functions.ApplicationController, constellation.model.GeometricModel, constellation.math.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color, boolean)
	 */
	public void renderLabel( final ApplicationController controller,
							 final GeometricModel model,
							 final MatrixWCStoSCS matrix,
							 final Rectangle clipper, 
							 final Graphics2D g, 
							 final Color color, 
							 final String label ) {
		// get a space point from the scratch pad
		final Point screenPt = ScratchPad.getProjectionPoint();
		
		// translate the point to screen coordinates
		matrix.transform( this, screenPt );
		
		// get the (x,y) model coordinates of the point
		final int dx = screenPt.x;
		final int dy = screenPt.y;
		
		// get the (x,y) coordinates and height and width of the point
		final int x = dx - POINT_HALF;
		final int y = dy - POINT_HALF;
		
		// is the point is visible ...
		if( clipper.intersects( x, y, POINT_FULL, POINT_FULL ) ) {
			// compute the center to the text
			final int textWidth = g.getFontMetrics().stringWidth( label );
			
			// draw the point
			g.setColor( color );
			g.drawString( label, dx - textWidth/2, dy );		
		}
	}
	
	/**
	 * Draws a point centered at the given (x,y) coordinates
	 * @param g the given {@ink Graphics2D graphics context}
	 * @param cx the given X-coordinate
	 * @param cy the given Y-coordinate
	 * @param color the given {@link Color color}
	 */
	public static void renderVertex( final Graphics2D g, final int cx, final int cy, final Color color ) {
		final int x = cx - POINT_HALF;
		final int y = cy - POINT_HALF;
		g.setColor( color );
		g.fillOval( x, y, POINT_FULL, POINT_FULL );
	}

	/** 
	 * Sets the location of this point
	 * @param x the given X-axis coordinate 
	 * @param y the given Y-axis coordinate 
	 */
	public void setLocation( final double x, final double y ) {
		this.x = x;
		this.y = y;
	}
	
	/** 
	 * Sets the location of this point
	 * @param p the given {@link PointXY location} in two-dimensional space
	 */
	public void setLocation( final PointXY p ) {
		this.x = p.x;
		this.y = p.y;
	}

	/**
	   * Translates this point by the given point's X and Y values respectively
	   * @param delta the given point
	   * @return a reference to this point
	   */
	public PointXY translate( final PointXY delta ) {
		this.x += delta.x;
	    this.y += delta.y;
	    return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format( "(%3.4f,%3.4f)", x, y );
	}
	
}
