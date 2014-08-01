package constellation.drawing.entities;

import static constellation.drawing.entities.PointXY.getDistance;
import static constellation.math.CxMathUtil.TOLERANCE;
import static constellation.math.CxMathUtil.clipValue;
import static constellation.math.CxMathUtil.isEqual;
import static java.lang.Double.NaN;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.ComplexInternalRepresentation;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.elements.ModelElement;
import constellation.math.CxIntersectionUtil;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * A pure mathematical representation of a line on a two-dimensional plane
 * @author lawrence.daniels@gmail.com
 */
public class LineXY implements ComplexInternalRepresentation {
	public static final double BIG_NUMBER = 250000;
	
	// computed fields
	private Double yintercept;
	private Double slope;
	
	// internal fields
	public final PointXY p1;
	public final PointXY p2;
	
	/**
	 * Creates a new line through points (x1,y1) and (x2,y2)
	 * @param x1 the x-coordinate of the starting point
	 * @param y1 the y-coordinate of the starting point
	 * @param x2 the x-coordinate of the end point
	 * @param y2 the y-coordinate of the end point
	 */
	public LineXY( final double x1,
				   final double y1,
				   final double x2,
				   final double y2 ) {
		this.p1 = new PointXY( x1, y1 ); 
		this.p2 = new PointXY( x2, y2 );
	}
	
	/**
	 * Creates a new line though point1 and point2
	 * @param p1 the starting point of the line
	 * @param p2 the end point of the line
	 */
	public LineXY( final PointXY p1, final PointXY p2 ) {
		this.p1 = p1;
		this.p2	= p2;
	}
	
	/**
	 * Clips the given points to the extents of the given clip plane (rectangle)
	 * @param clipper the given clip plane ({@link Rectangle rectangle}) 
	 * @param p1 the starting {@link PointXY point} of the line
	 * @param p2 the ending {@link PointXY point} of the line
	 */
	public static void clipLine( final Rectangle clipper, 
						   		 final Point p1, 
						   		 final Point p2 ) {
		// cache the 2 points (xA,yA) and (xB,yB)
		final int xA = p1.x; 
		final int yA = p1.y;
		final int xB = p2.x;
		final int yB = p2.y;
		
		// compute the delta Y (rise) and delta X (run) of the line
		final double dy = ( yB - yA );
		final double dx = ( xB - xA );
		  
		// is the line horizontal?
		if( dy == 0 ) {
			p1.x = clipValue( xA, 0, clipper.width );
			p2.x = clipValue( xB, 0, clipper.width );
		}
		
		// is the line vertical?
		else if( dx == 0 ) {
			p1.y = clipValue( yA, 0, clipper.height );
			p2.y = clipValue( yB, 0, clipper.height );
		}
		
		// must be angular
		else {			
			// compute the slope and Y-intercept of the line
			// equation: u = ( y2 - y1 )/( x2 - x1 )
			final double m = (double)dy / (double)dx;
			final double b = yA - ( m * (double)xA );
					
			// adjust the lower X boundary
			if( xA < 0 ) {
				p1.x = 0;
				p1.y = (int)( m * (double)p1.x + b );
			}
			
			// adjust the upper X boundary
			if( xA > clipper.width ) {
				p1.x = clipper.width;
				p1.y = (int)( m * (double)p1.x + b );
			}
			
			// adjust the lower Y boundary
			if( xB < 0 ) {
				p2.x = 0;
				p2.y = (int)( m * (double)p2.x + b );
			}
			
			// adjust the upper Y boundary
			if( xB > clipper.width ) {
				p2.x = clipper.width;
				p2.y = (int)( m * (double)p2.x + b );
			}
		}		
	}

	/**
	 * Computes the slope of the line
	 * @return the slope of the line
	 */
	public static Double computeSlope( final double x1, final double y1, final double x2, final double y2 ) {
		// equation:  u = ( y2 - y1 )/( x2 - x1 )
		final double rise = ( y2 - y1 );
		final double run = ( x2 - x1 );
		return ( run != 0 ) ? rise / run : NaN;
	}

	/**
	 * Computes the Y-intercept of the line
	 * @return the Y-intercept of the line
	 */
	public static Double computeYIntercept( final double x, final double y, final Double slope ) {
		return slope.equals( NaN ) ? slope : y - ( slope * x );
	}

	/**
	 * Creates a new angled line based on the given host line and angle
	 * @param hostLine the line which serves as the base line for the angled line
	 * @param angle the given angle in radians
	 * @param length the given length of the new line
	 * @return the new angled {@link LineXY line}
	 */
	public static LineXY createAngledLine( final LineXY hostLine, 
										   final PointXY anchorPt,
										   final double angle,
										   final double length ) {
		// cache the coordinate values
		final double ax = anchorPt.getX();
		final double ay = anchorPt.getY();
		
		// compute the new end point
		final double ex = ax + length * cos(angle);
		final double ey = ay + length * sin(angle);
	
		// create the new perpendicular line
		return new LineXY( anchorPt, new PointXY( ex, ey ) );
	}
	
	/**
	 * Creates a new angled line based on the given host line and angle
	 * @param hostLine the line which serves as the base line for the angled line
	 * @param angle the given angle in radians
	 * @param length the given length of the new line
	 * @return the new angled {@link LineXY line}
	 */
	public static LineXY createBisectLine( final LineXY lineA, 
										   final LineXY lineB ) {
		// create an intersection between the two lines
		final PointXY p1 = CxIntersectionUtil.getIntersectionPoint( lineA, lineB );
		if( p1 == null ) {
			return null;
		}
		
		// project the end point from line A to line B
		final PointXY epA = lineA.getEndPoint();
		final PointXY pp  = PointXY.getProjectedPoint( epA, lineB ); 
		
		// create the end point of the line
		final PointXY p2 = PointXY.getMidPoint( epA, pp );
		
		// return the new line
		return new LineXY( p1, p2 );
	}
	
	/**
	 * Creates a new horizontal line segment
	 * @return a new horizontal {@link LineXY line} segment
	 */
	public static LineXY createHorizontalLine( final PointXY p1, final PointXY p2 ) {
		// cache the Y-axis
		final double y = p1.getY();		
		
		// create the horizontal line segment
		return new LineXY( p1.getX(), y, p2.getX(), y );
	}
	
	/**
	 * Creates a new infinite horizontal line
	 * @return a new infinite horizontal {@link LineXY line}
	 */
	public static LineXY createInfiniteHorizontalLine( final PointXY p1, final PointXY p2 ) {
		// cache the Y-axis
		final double y = p1.getY();		
		
		// create the horizontal infinite line
		return new LineXY( -BIG_NUMBER, y, BIG_NUMBER, y );
	}
	
	/**
	 * Creates a two-dimensional infinite line
	 * @param lineSeg the given line segment
	 * @return the {@ink LineXY infinite line}
	 */
	public static LineXY createInfiniteLine( final LineXY lineSeg ) {
		// get the start and end points
		final PointXY p1 = lineSeg.getBeginPoint();
		final PointXY p2 = lineSeg.getEndPoint();
		
		// now create an infinite line through the points
		// having the same slope
		LineXY lineInf = null;
		
		// if the line segment is horizontal
		if( lineSeg.isHorizontal() ) {
			lineInf = new LineXY( -BIG_NUMBER, p1.y, BIG_NUMBER, p2.y );
		}
		
		// if the line segment is vertical
		else if( lineSeg.isVertical() ) {
			lineInf = new LineXY( p1.x, -BIG_NUMBER, p2.x, BIG_NUMBER );
		}
		
		// the line segment must be angular
		else {			
			// compute the slope & Y-intercept of the parallel line
			final double m = lineSeg.getSlope();
			final double b = lineSeg.getYIntecept(); 
			
			// compute first new point of the infinite line
			// y = m * x + b
			final double x1 = -BIG_NUMBER;
			final double y1 = m * x1 + b;
			
			// compute second new point of the infinite line
			// y = m * x + b
			final double x2 = BIG_NUMBER;
			final double y2 = m * x2 + b;
			
			// create the infinite line
			lineInf = new LineXY( x1, y1, x2, y2 );
		}
		
		// return the infinite line
		return lineInf;
	}

	/**
	 * Creates a two-dimensional infinite line
	 * @param p1 the given {@link PointXY point #1}
	 * @param p2 the given {@link PointXY point #2}
	 * @return the {@ink LineXY infinite line}
	 */
	public static LineXY createInfiniteLine( final PointXY p1, final PointXY p2 ) {
		// get the start and end points
		final LineXY lineSeg = new LineXY( p1, p2 );
		
		// return the infinite line
		return createInfiniteLine( lineSeg );
	}

	/**
	 * Creates a new vertical infinite line
	 * @return a new vertical infinite {@link ModelElement line}
	 */
	public static LineXY createInfiniteVerticalLine( final PointXY p1, final PointXY p2 ) {
		return new LineXY( p1.x, -BIG_NUMBER, p1.x, BIG_NUMBER );
	}

	/**
	 * Creates a new line that is normal (perpendicular) to 
	 * the given line through the given point.
	 * @param hostLine the given of the host {@link LineXY line}
	 * @param anchorPt the given {@link PointXY anchor point}
	 * @param distance the given distance vector from the anchor point (may be positive or negative)
	 * @return a new {@link LineXY line}
	 */
	public static LineXY createNormalLine( final LineXY hostLine, 
										   final PointXY anchorPt,
										   final double distance ) {
		// cache the coordinate values
		final double ax = anchorPt.getX();
		final double ay = anchorPt.getY();
		
		// is the host line horizontal?
		if( hostLine.isHorizontal() ) {
			// compute the normal (vertical) line
			return new LineXY( ax, ay, ax, ay + distance );
		}
		
		// is the host line vertical?
		else if( hostLine.isVertical() ) { 
			// compute the normal (horizontal) line
			return new LineXY( ax, ay, ax + distance, ay );
		}
		
		// the host line must be an angular
		else {			
			// compute the slope & Y-intercept of the normal line
			final double m = -1.0/hostLine.getSlope();
			final double b = computeYIntercept( ax, ay, m );
			
			// compute the end point for the normal line
			// y = m * x + b
			final double x = ax + ( distance / m );
			final double y = m * x + b;
	
			// create the new perpendicular line
			return new LineXY( anchorPt, new PointXY( x, y ) );
		}
	}

	/**
	 * Creates a new line that is normal (perpendicular) to 
	 * the given line through the given point.
	 * @param hostLine the given of the host {@link LineXY line}
	 * @param startPt the given {@link PointXY starting point}
	 * @param endPt the given {@link PointXY ending point}
	 * @return a new {@link LineXY line}
	 */
	public static LineXY createNormalLine( final LineXY hostLine, 
										   final PointXY startPt,
										   final PointXY endPt ) {
		// cache the coordinate values
		final double ax = startPt.getX();
		final double ay = startPt.getY();
		final double bx = endPt.getX();
		final double by = endPt.getY();
		
		// is the host line horizontal?
		if( hostLine.isHorizontal() ) {
			// compute the normal (vertical) line
			return new LineXY( ax, ay, ax, by );
		}
		
		// is the host line vertical?
		else if( hostLine.isVertical() ) { 
			// compute the normal (horizontal) line
			return new LineXY( ax, ay, bx, ay );
		}
		
		// the host line must be an angular
		else {			
			// compute the slope & Y-intercept of the normal line
			final double m = -1.0/hostLine.getSlope();
			final double b = computeYIntercept( ax, ay, m );
			
			// compute the end point for the normal line
			// y = m * x + b
			final double x = bx;
			final double y = m * x + b;
	
			// create the new perpendicular line
			return new LineXY( startPt, new PointXY( x, y ) );
		}
	}

	/**
	 * Creates a new line that is parallel the given line through the given points.
	 * @param hostLine the given of the host {@link LineXY line}
	 * @param startPt the given {@link PointXY starting point}
	 * @param endPt the given {@link PointXY ending point}
	 * @return a new parallel {@link LineXY line}
	 */
	public static LineXY createParallelLine( final LineXY hostLine, 
										   	 final PointXY startPt,
										   	 final PointXY endPt ) {
		// cache the coordinate values
		final double ax = startPt.getX();
		final double ay = startPt.getY();
		final double bx = endPt.getX();
		final double by = endPt.getY();
		
		// is the host line horizontal?
		if( hostLine.isHorizontal() ) {
			return new LineXY( ax, ay, bx, ay );
		}
		
		// is the host line vertical?
		else if( hostLine.isVertical() ) {
			return new LineXY( ax, ay, ax, by );
		}
		
		// the host line must be angular
		else {
			// compute the slope & Y-intercept of the parallel line
			final double m = hostLine.getSlope();
			final double b = computeYIntercept( ax, ay, m );
			
			// compute the end point for the parallel line
			// y = m * x + b
			final double x = bx;
			final double y = m * x + b;
		
			// create the new parallel line
			return new LineXY( ax, ay, x, y );
		}
	}
	
	/**
	 * Creates a new line that is tangent to the given arc through the given intersection point.
	 * @param arc the given of the {@link ArcXY arc} that the line will be tangent to.
	 * @param intersectionPt the given {@link PointXY intersection point} of the desired line
	 * @param startPt the given {@link PointXY starting point} of the desired line
	 * @param endPt the given {@link PointXY opposite point} of the desired line
	 * @return a new {@link LineXY line} that is tangent to the given arc.
	 */
	public static LineXY createTangentLine( final ArcXY arc,
											final PointXY intersectionPt, 
											final PointXY startPt, 
											final PointXY endPt ) {
		// get the center point of the curve (circles only)
		final PointXY centerPt = arc.getMidPoint();
		
		// create a line through the center point and intersection point
		final LineXY lineA = new LineXY( intersectionPt, centerPt );
	
		// create the normal line (perpendicular to the 'line A')
		final LineXY lineB = createNormalLine( lineA, intersectionPt, endPt );
	
		// create a new starting point for the tangent line
		final PointXY newStartPt = PointXY.getProjectedPoint( startPt, lineB );
		
		// create the tangent line (parallel to the 'line B')
		return createParallelLine( lineB, newStartPt, endPt );
	}

	/**
	 * Creates a new line that is tangent to the given circle through the given intersection point.
	 * @param circle the given of the {@link CircleXY circle} that the line will be tangent to.
	 * @param intersectionPt the given {@link PointXY intersection point} of the desired line
	 * @param startPt the given {@link PointXY starting point} of the desired line
	 * @param endPt the given {@link PointXY opposite point} of the desired line
	 * @return a new {@link LineXY line} that is tangent to the given circle.
	 */
	public static LineXY createTangentLine( final CircleXY circle,
											final PointXY intersectionPt, 
											final PointXY startPt, 
											final PointXY endPt ) {
		// get the center point of the curve (circles only)
		final PointXY centerPt = circle.getMidPoint();
		
		// create a line through the center point and intersection point
		final LineXY lineA = new LineXY( intersectionPt, centerPt );
	
		// create the normal line (perpendicular to the 'line A')
		final LineXY lineB = createNormalLine( lineA, intersectionPt, endPt );
	
		// create a new starting point for the tangent line
		final PointXY newStartPt = PointXY.getProjectedPoint( startPt, lineB );
		
		// create the tangent line (parallel to the 'line B')
		return createParallelLine( lineB, newStartPt, endPt );
	}
	
	/**
	 * Creates a new line that is tangent to the given ellipse through the given intersection point.
	 * @see http://mathworld.wolfram.com/EllipseTangent.html
	 * @param ellipse the given of the {@link EllipseXY ellipse} that the line will be tangent to.
	 * @param intersectionPt the given {@link PointXY intersection point} of the desired line
	 * @param startPt the given {@link PointXY starting point} of the desired line
	 * @param endPt the given {@link PointXY opposite point} of the desired line
	 * @return a new {@link LineXY line} that is tangent to the given ellipse.
	 */
	public static LineXY createTangentLine( final EllipseXY ellipse,
											final PointXY intersectionPt, 
											final PointXY startPt, 
											final PointXY endPt ) {
		// get the center point of the curve (circles only)
		final PointXY centerPt = ellipse.getMidPoint();
		
		// create a line through the center point and intersection point
		final LineXY lineA = new LineXY( intersectionPt, centerPt );
	
		// create the normal line (perpendicular to the 'line A')
		final LineXY lineB = createNormalLine( lineA, intersectionPt, endPt );
	
		// create a new starting point for the tangent line
		final PointXY newStartPt = PointXY.getProjectedPoint( startPt, lineB );
		
		// create the tangent line (parallel to the 'line B')
		return createParallelLine( lineB, newStartPt, endPt );
	}
	
	/**
	 * Creates a new vertical line 
	 * @return a new vertical {@link ModelElement line} 
	 */
	public static LineXY createVerticalLine( final PointXY p1, final PointXY p2 ) {
		// determine the coordinates
		final double x = p1.getX();
		
		// create the perpendicular line
		return new LineXY( x, p1.getY(), x, p2.getY() );
	}

	/**
	 * Indicates whether the line segment represented by the given value
	 * is orthogonal (horizontal or vertical).
	 * @param x1 the given x-axis value of the starting point
	 * @param y1 the given y-axis value of the starting point
	 * @param x2 the given x-axis value of the ending point
	 * @param y2 the given y-axis value of the ending point
	 * @return true, if the line segment represented by the given value
	 * is orthogonal.
	 */
	public static boolean isOrthoganol( final int x1, final int y1, final int x2, final int y2 ) {
		return ( x1 - x2 < TOLERANCE ) || ( y1 - y2 < TOLERANCE );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} 
		catch( final CloneNotSupportedException cause ) {
			throw new IllegalStateException( format( "Error cloning class '%s'", getClass().getName() ), cause );
		}
	}

	/** 
	 * Indicates whether the given point is contained by the line
	 * @param p the given {@link PointXY point}
	 * @return true, if the point contained by the line
	 */
	public boolean contains( final PointXY p ) {
		// is the line horizontal?
		if( this.isHorizontal() ) {
			return ( p.x >= p1.x && p.x <= p2.x ) && ( p.y == p1.y && p.y == p2.y );
		}
		
		// is the line vertical?
		if( this.isVertical() ) {
			return ( p.x == p1.x && p.x == p2.x ) && ( p.y >= p1.y && p.y <= p2.y );
		}
		
		// slope is defined
		else {
			// compute the Y-value: y = mx + b
			final double y = getSlope() * p.x + getYIntecept(); 
			return ( y == p.y );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#duplicate(constellation.drawing.entities.PointXY)
	 */
	public LineXY duplicate( double dx, double dy ) {
		return new LineXY( p1.getOffset( dx, dy ), p2.getOffset( dx, dy ) );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see Object#equals(Object)
	 */
	public boolean equals( final Object object ) {
		// is the object the right type?
		if( ( object != null ) && ( object instanceof LineXY ) ) {
			final LineXY line = (LineXY)object;
			return isEqual( line.getX1(), p1.x ) && 
					isEqual( line.getY1(), p1.y ) &&
					isEqual( line.getX2(), p2.x ) && 
					isEqual( line.getY2(), p2.y );
		}
		return false;
	}

	/** 
	 * Returns the acute angle of the line relative to the given line
	 * @param line the line to use for comparison
	 * @return the angle (in radians)
	 * @see http://www.tpub.com/math2/5.htm
	 */
	public double getAcuteAngle( final LineXY line ) {
		// get the intersection point
		final PointXY p1 = PointXY.getIntersectionPoint( this, line );
		if( p1 == null ) {
			throw new IllegalArgumentException( format( "No intersection found" ) );
		}
		
		// get the points of both lines
		final PointXY pa1 = this.getBeginPoint();
		final PointXY pa2 = this.getEndPoint();
		final PointXY pb1 = line.getBeginPoint();
		final PointXY pb2 = line.getEndPoint();
		
		final PointXY p2 = PointXY.getFarthestPoint( p1, pa1, pa2 );
		final PointXY p3 = PointXY.getFarthestPoint( p1, pb1, pb2 );
		
		// are both lines orthogonal?
		if( this.isOrthogonal() && line.isOrthogonal() ) {
			return this.isParallelTo( line ) ? 0 : Math.PI;
		}
		
		// is the either line orthogonal?
		else if( this.isOrthogonal() || line.isOrthogonal() ) {
			// cos t = ( -a^2 + b^2 - c^2 ) / 2cb 
			final double a = getDistance( p1, p2 );
			final double b = getDistance( p1, p3 );
			final double c = getDistance( p2, p3 );;
			return acos( ( -pow(a, 2d) + pow(b, 2d) - pow(c, 2d) ) / ( 2d * c * b ) );
		}
		
		// both must be angular
		else {
			// tan t = ( m1 - m2 ) / ( 1 + m1 * m2 ); where m2 > m1
			double m1 = this.getSlope();
			double m2 = line.getSlope();
			if( m1 > m2 ) {
				final double mt = m1;
				m1 = m2;
				m2 = mt;
			}
			
			// compute the angle
			return atan( ( m1 - m2 ) / ( 1 + m1 * m2 ) );
		}
	}
	
	/**
	 * Returns the angle of the line
	 * @return the {@link Double angle} in radians
	 */
	public Double getAngle() {
		return PointXY.getAngle( p1, p2 );
	}

	/**
	 * Returns the start point of the line
	 * @return the start {@link PointXY point} of the line
	 */
	public PointXY getBeginPoint() {
		return p1;
	}

	/**
	 * Returns the end point of the line
	 * @return the end {@link PointXY point} of the line
	 */
	public PointXY getEndPoint() {
		return p2;
	}

	/** 
	 * Returns the length of the line
	 * @return the length of the line
	 */
	public double getLength() {
		return getDistance( p1, p2 );
	}
	
	/**
	 * Returns the slope of the line (<i>m = (y1-y2)/(x1-x2)</i>)
	 * @return the slope of the line, or <tt>NaN</tt> if undefined
	 */
	public Double getSlope() {
		if( slope == null ) {
			slope = computeSlope( p1.x, p1.y, p2.x, p2.y );
		}
		return slope;
	}
	
	/** 
	 * Returns the X-axis of the start point
	 * @return the X-axis of the start point
	 */
	public double getX1() {
		return p1.x;
	}
	
	/** 
	 * Returns the Y-axis of the start point
	 * @return the Y-axis of the start point
	 */
	public double getY1() {
		return p1.y;
	}
	
	/** 
	 * Returns the X-axis of the end point
	 * @return the X-axis of the end point
	 */
	public double getX2() {
		return p2.x;
	}
	
	/** 
	 * Returns the Y-axis of the end point
	 * @return the Y-axis of the end point
	 */
	public double getY2() {
		return p2.y;
	}
	
	/**
	 * Returns the Y value at the point where the line crosses the x-axis (<i>b = y-m*x</i>)
	 * <br><a href="http://en.wikipedia.org/wiki/Y-intercept">Wkipedia reference</a>
	 * @return the Y-intercept of the line
	 */
	public Double getYIntecept() {
		if( yintercept == null ) {
			yintercept = computeYIntercept( p2.x, p2.y, getSlope() );
		}
		return yintercept;
	}
	
	/**
	 * Indicates whether the line is above the given point.
	 * @param point the given {@link PointXY point} 
	 * @return true, if the line is above the given point.
	 */
	public boolean isAbove( final PointXY point ) {
		// get the point's Y-axis value
		final double y = point.getY();
		
		// is the line above the point?
		return ( this.getY1() < y ) && ( this.getY2() < y );
	}
	
	/**
	 * Indicates whether the line is above the given point.
	 * @param x the given X-axis coordinate
	 * @param y the given Y-axis coordinate
	 * @return true, if the line is above the given point.
	 */
	public boolean isAbove( final double x, final double y ) {
		// is the line above the point?
		return ( this.getY1() < y ) && ( this.getY2() < y );
	}
	
	/**
	 * Indicates whether the line is below the given point.
	 * @param point the given {@link PointXY point} 
	 * @return true, if the line is above the given point.
	 */
	public boolean isBelow( final PointXY point ) {
		// get the point's Y-axis value
		final double y = point.getY();
		
		// is the line below the point?
		return ( this.getY1() > y ) && ( this.getY2() > y );
	}
	
	/**
	 * Indicates whether the line is below the given point.
	 * @param x the given X-axis coordinate
	 * @param y the given Y-axis coordinate
	 * @return true, if the line is above the given point.
	 */
	public boolean isBelow( final double x, final double y ) {
		// is the line below the point?
		return ( this.getY1() > y ) && ( this.getY2() > y );
	}

	/**
	 * Determines whether the line is horizontal
	 * @return true, if the slope is zero
	 */
	public boolean isHorizontal() {
		return ( getSlope() == 0.0d );
	}
	
	/**
	 * Indicates whether the line's boundary is infinite
	 * @return true, if the line is an infinite line
	 */
	public boolean isInfinite() {
		return length() > BIG_NUMBER;
	}
	
	/**
	 * Determines whether the line is orthogonal 
	 * @return true, if the line is horizontal or vertical.
	 */
	public boolean isOrthogonal() {
		return isHorizontal() || isVertical();
	}
	
	/**
	 * Determines whether the given line is parallel to the host line
	 * @param line the given {@link LineXY line} to compare against
	 * @return true, if the slopes are identical
	 */
	public boolean isParallelTo( final LineXY line ) {
		// are the lines horizontal?
		if( this.isHorizontal() && line.isHorizontal() ) {
			return true;
		}
		
		// are the lines horizontal?
		else if( this.isVertical() && line.isVertical() ) {
			return true;
		}
		
		// compare the slopes
		else {
			final Double m1 = this.getSlope();
			final Double m2 = line.getSlope();
			return ( m1 != null ) && m1.equals( m2 );
		}
	}
	
	/**
	 * Determines whether the line is vertical
	 * @return true, if the slope is undefined
	 */
	public boolean isVertical() {
		final Double slope = getSlope();
		return slope.equals( NaN );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		// cache the point values
		final double x1 = p1.x;
		final double y1 = p1.y;
		final double x2 = p2.x;
		final double y2 = p2.y;
		
		// determine the boundary
		final double xa = ( x1 < x2 ) ? x1 : x2;
		final double ya = ( y1 < y2 ) ? y1 : y2;
		final double xb = ( x1 < x2 ) ? x2 : x1;
		final double yb = ( y1 < y2 ) ? y2 : y1;
		
		// return the boundary
		return new RectangleXY( xa, ya, abs( xb - xa ), abs( yb - ya ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.GeometryXY#getLimits()
	 */
	public VerticesXY getLimits()  {
		return new VerticesXY( 
					p1.x, p1.y, 
					p2.x, p2.y 
				); 
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.GeometryXY#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return PointXY.getMidPoint( p1,  p2 );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.LINE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.LINEAR;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		return getLimits();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.intersectsLine( p1.x, p1.y, p2.x, p2.y );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.GeometryXY#length()
	 */
	public double length() {
		return getDistance( p1, p2 ); 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CxElement#render(constellation.model.GeometricModel, constellation.geometry.MatrixWCStoSCS, java.awt.Rectangle, java.awt.Graphics2D, java.awt.Color, boolean)
	 */
	public void render( ApplicationController controller,
						final GeometricModel model,
						final MatrixWCStoSCS matrix,
						final Rectangle clipper, 
						final Graphics2D g, 
						final Color color ) {
		EntityRepresentationUtil.render( controller, model, matrix, clipper, g, this, color );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public LineXY mirror( final LineXY plane ) {
		// mirror the start point
		final PointXY pa = p1.mirror( plane );
		
		// mirror the end point
		final PointXY pb = p2.mirror( plane );
		
		// return the new line
		return new LineXY( pa, pb );
	}

	/**
	 * Returns a new line that represents the line created if the host
	 * line was to be trimmed or extended based on the given projection point.
	 * @param point the given limit {@link PointXY point}.
	 * @return a new {@link LineXY line representation}
	 */
	public LineXY trimOrExtendTo( final PointXY point ) {
		// compute the new begin/end point of the line
		final double x,y;
		
		// cache the point values
		final double x1 = p1.x;
		final double y1 = p1.y;
		final double x2 = p2.x;
		final double y2 = p2.y;
		
		// is the line horizontal?
		if( isHorizontal() ) {
			x = point.getX();
			y = y1;
		}
		
		// is the line vertical?
		else if( isVertical() ) {
			x = x1;
			y = point.getY();
		}
		
		// must be angular ...
		else {
			x = point.getX();
			y = getSlope() * x + getYIntecept();
		}
		
		// if the point exists on the line,
		// create a new shorter line
		if( contains( point ) ) { 
			return getDistance( x1, y1, x, y ) < getDistance( x, y, x2, y2 ) 
						? new LineXY( x1, y1, x, y ) 
						: new LineXY( x, y, x2, y2 );
		}
		
		// otherwise, create a longer line
		else {
			return getDistance( x1, y1, x, y ) > getDistance( x, y, x2, y2 ) 
						? new LineXY( x1, y1, x, y ) 
						: new LineXY( x, y, x2, y2 );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		// is it a horizontal line?
		if( isHorizontal() ) {
			return format( "y=%.2f", p1.y );
		}
		// is it a vertical line?
		else if( isVertical() ) {
			return format( "x=%.2f", p1.x );
		}
		// otherwise ...
		else {
			return format( ( getYIntecept() >= 0 ) ? "y=%.2fx+%.2f" : "y=%.2fx%.2f", getSlope(), getYIntecept() );
		}
	}

}

