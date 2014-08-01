package constellation.math;

import static constellation.math.CxMathUtil.*;
import static java.lang.Math.*;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import constellation.drawing.EntityRepresentation;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;

/** 
 * Constellation Intersection Utilities
 * <div>
 * This class contains intersection computations between 
 * all geometry types (lines, circles, ellipses, etc.)
 * <div/>
 * @author lawrence.daniels@gmail.com
 */
public class CxIntersectionUtil {
	public static final Intersection NO_INTERSECTION = 
		new Intersection( "No solution could be found - Unimplemented method" );
	private static final Logger logger = Logger.getLogger( CxIntersectionUtil.class );
	
	/**
	 * Attempts to determine the intersection point between the lines #1 and line #2
	 * @param line1 the given {@link LineXY line A}
	 * @param line2 the given {@link LineXY line B}
	 * @return the intersection point or <tt>null</tt> if no intersection exists
	 */
	public static PointXY getIntersectionPoint( final LineXY line1, final LineXY line2 ) {
		// get the slope of Lines #1 and #2
		final Double m1 = line1.getSlope();
		final Double m2 = line2.getSlope();
		
		// if the slopes are equal, parallel lines
		if( ( m1 != null ) && ( m2 != null ) && isEqual( m1, m2 ) ) { 
			return null;
		}
		
		// get the Y-intercepts of Lines #1 and #2
		final Double b1 = line1.getYIntecept();
		final Double b2 = line2.getYIntecept();
		
		// if line1 or line2 is vertical 
		if( line1.isVertical() || line2.isVertical() ) {
			// get the x and y values of the lines
			final double x1 = line1.getX1();
			final double y1 = line1.getY1();
			final double x2 = line2.getX2();
			final double y2 = line2.getY2();
			
			// if line1 or line2 is horizontal
			if( line1.isHorizontal() || line2.isHorizontal() ) {
				final double x = line1.isHorizontal() ? x2 : x1;
				final double y = line1.isHorizontal() ? y1 : y2;
				return new PointXY( x, y );
			}
			
			// must be a vertical & angle intersection
			else {
				// use the x of the vertical line to compute the y of the angled
				// line, and compare the Y values
				final double xV = line1.isVertical() ? x1 : x2; 
				final double yH = line1.isVertical() ? m2 * xV + b2 : m1 * xV + b1;
				return new PointXY( xV, yH );
			}
		}
		
		// must be two angled lines
		else { 
			// compute the x and y's
			final Double x = ( b2 - b1 ) / ( m1 - m2 );
			final Double y1 = m1 * x + b1;
			final Double y2 = m2 * x + b2;
			
			// return the point, if the Y values match
			return isEqual( y1, y2 ) ? new PointXY( x, y1 ) : null;
		}
	}

	/**
	 * Attempts to determine the intersection point(s) between the given
	 * entity representations (e.g. lines, curves, etc.)
	 * @param elemA the given {@link EntityRepresentation entity A}
	 * @param elemB the given {@link EntityRepresentation entity B}
	 * @return the {@link Intersection intersection}
	 */
	public static Intersection getIntersectionPoints( final EntityRepresentation elemA, 
												      final EntityRepresentation elemB ) {
		switch( elemA.getType() ) {
			case ARC:
				final ArcXY arcA = (ArcXY)elemA;
				switch( elemB.getType() ) {
					case LINE:  		return getIntersectionPoints( arcA, (LineXY)elemB );
				}
				break;
				
			case CIRCLE:
				final CircleXY circleA = (CircleXY)elemA;
				switch( elemB.getType() ) {
					case CIRCLE: 		return getIntersectionPoints( circleA, (CircleXY)elemB );
					case COMPOSITION:	return getIntersectionPoints( circleA, (CompositionXY)elemB );
					case ELLIPSE: 		return getIntersectionPoints( circleA, (EllipseXY)elemB );
					case LINE: 			return getIntersectionPoints( circleA, (LineXY)elemB );
				}
				break;
				
			case COMPOSITION:
				final CompositionXY comp = (CompositionXY)elemA;
				switch( elemB.getType() ) {
					case CIRCLE: 		return getIntersectionPoints( (CircleXY)elemB, comp );
					case COMPOSITION:	return getIntersectionPoints( comp, (CompositionXY)elemB );
					case ELLIPSE: 		return getIntersectionPoints( comp, (EllipseXY)elemB );
					case LINE: 			return getIntersectionPoints( comp, (LineXY)elemB );
				}
				break;
				
			case ELLIPSE:
				final EllipseXY ellipse = (EllipseXY)elemA;
				switch( elemB.getType() ) {
					case CIRCLE: 		return getIntersectionPoints( (CircleXY)elemB, ellipse );
					case COMPOSITION:	return getIntersectionPoints( (CompositionXY)elemB, ellipse );
					case ELLIPSE: 		return getIntersectionPoints( ellipse, (EllipseXY)elemB );
					case LINE: 			return getIntersectionPoints( ellipse, (LineXY)elemB );
				}
				break;
				
			case LINE: 
				final LineXY lineA = (LineXY)elemA;
				switch( elemB.getType() ) {
					case ARC:			return getIntersectionPoints( (ArcXY)elemB, lineA ); 
					case CIRCLE: 		return getIntersectionPoints( (CircleXY)elemB, lineA );
					case COMPOSITION:	return getIntersectionPoints( (CompositionXY)elemB, lineA );
					case ELLIPSE: 		return getIntersectionPoints( (EllipseXY)elemB, lineA );
					case LINE: 			return getIntersectionPoints( (LineXY)elemB, lineA );
				}
				break;
		}
		
		throw new IllegalArgumentException( format( "Unhandled insersection event between '%s' and '%s'", elemA.getType(), elemB.getType() ) );
	}
	
	/**
	 * Determines the whether the given ellipse intersects the given boundary.
	 * <div>Equation: ab = (x-i)^2+(mx+c-j)^2</div>
	 * @see http://www.vb-helper.com/howto_net_line_circle_intersections.html
	 * @return true, if an intersection exists
	 */
	public static boolean intersects( final RectangleXY boundary, final EllipseXY ellipse ) {
		// cache the boundary's attributes
		final double x1 = boundary.getMinX();
		final double y1 = boundary.getMinY();
		final double x2 = boundary.getMaxX();
		final double y2 = boundary.getMaxY();
		
		// cache the ellipse's attributes
		final double cx = ellipse.getCenterX();
		final double cy	= ellipse.getCenterY();
		final double a	= ellipse.getHalfWidth();
		final double b  = ellipse.getHalfHeight();
	   	 
		//logger.info( "Checking 4..." );
		
		// determine if there is an intersection
		return intersectsEllipse( cx, cy, a, b, x1, y1, x2, y1 ) ||
				intersectsEllipse( cx, cy, a, b, x1, y1, x1, y2 ) ||		
				intersectsEllipse( cx, cy, a, b, x1, y2, x2, y2 ) ||
				intersectsEllipse( cx, cy, a, b, x2, y1, x2, y2 );
	}
	
	/**
	 * Determines the whether the given ellipse intersects the given boundary.
	 * <div>Equation: ab = (x-i)^2+(mx+c-j)^2</div>
	 * @see http://www.vb-helper.com/howto_net_line_circle_intersections.html
	 * @return true, if an intersection exists
	 */
	private static boolean intersectsEllipse( final double cx,
											  final double cy,
											  final double a,
											  final double b,
											  final double x1,
											  final double y1, 
											  final double x2,
											  final double y2 ) {
		// Quick Fail: is the line within the ellipse's boundary?
		if( ( x1 < cx - a ) || ( x2 > cx + a ) ||
				( y1 < cy - b ) || ( y2 > cy + b ) ) {
			return false;
		}
		
		// is the line horizontal?
		/*else if( y1 - y2 == 0 ) {
			// compute the X-axis value
			//final double angle = CxMathUtil.getAngle( cx, cy, x1, y1 );
			final double angle = asin( ( cy - y1 ) / b );
			final double degrees = convertRadiansToDegrees( angle );
			final double px = cx + a * cos( angle );
			
			//final double px = sqrt( ( sqr(a) - ( sqr(b) - sqr(y1) ) ) / sqr(b) );
			
			logger.info( format( "H C(%3.2f,%3.2f) I(%3.2f,%3.2f) L(%3.2f,%3.2f)-(%3.2f,%3.2f) a=%3.2f (%3.1f) ? %s",
					cx, cy, px, y1, x1, y1, x2, y2, angle, degrees, ( px >= x1 && px <= x2 ) ) );
			
			// does the line contain the point?
	        return ( px >= x1 && px <= x2 );
		}*/
		
		// is the line vertical?
		else if( x1 - x2 == 0 ) {
			// compute the Y-axis value
			//final double angle = CxMathUtil.getAngle( cx, cy, x1, y1 );
			final double angle = acos( ( cx - x1 ) / a );
			final double degrees = convertRadiansToDegrees( angle );
			final double py = cy + b * sin(angle);
			
			// y = 
			//final double py = sqrt( ( sqr(b) - ( sqr(a) - sqr(x1) ) ) / sqr(a) );
			
			//logger.info( format( "V C(%3.2f,%3.2f) I(%3.2f,%3.2f) L(%3.2f,%3.2f)-(%3.2f,%3.2f) a=%3.2f (%3.1f) ? %s", 
			//		cx, cy, x1, py,  x1, y1, x2, y2, angle, degrees, ( py >= y1 && py <= y2 ) ) );
			
			// does the line contain the point?
	        return ( py >= y1 && py <= y2 );
		}
		
		// otherwise, use the determine to determine if an intersection exists
		else {
			// compute the slope & Y-intercept
			final double m  = ( y2 - y1 ) / ( x2 - x1 );
			final double n	= y1 - ( m * x1 );
			
			// compute the determinant
			final double e 	= m * a / b;
			final double f 	= ( m * cx + n - cy ) / b;
			final double det = sqr(e) - sqr(f) + 1d;
			
			// No solution?
			if( det < 0 ) {
				return false;
			}
			
			// One solution
			else if( det == 0 ) {
				final double px	= -a * e * f/(1d + sqr(e)) + cx;
		        final double py	= m * px + n;
		        return ( px >= x1 && px <= x2 ) && ( py >= y1 && py <= y2 );
			}
			
			// Two solutions
			else {
		    	// compute the square root of the determinant
		    	final double sqrtdet = sqrt(det);
		    	
		    	// compute the coordinates for point #1
		        final double px1	= a*(-e*f - sqrtdet)/(1d + sqr(e)) + cx;
		        final double py1	= m * px1 + n;
		        
		        // compute the coordinates for point #2
		        final double px2	= a * (-e * f + sqrtdet ) / (1d + sqr(e) ) + cx;
		        final double py2	= m * px2 + n;
		        
		        return ( ( px1 >= x1 && px1 <= x2 ) && ( py1 >= y1 && py1 <= y2 ) ) || 
		        		( ( px2 >= x1 && px2 <= x2 ) && ( py2 >= y1 && py2 <= y2 ) );
			}
		}
		//return false;
	}
	
	/**
	 * Attempts to determine the intersection point between the given
	 * circle and line.
	 * @see http://local.wasp.uwa.edu.au/~pbourke/geometry/2circle/
	 * @param circleA the given {@link CircleXY circle A}
	 * @param circleB the given {@link CircleXY circle B}
	 * @return the intersection point or <tt>null</tt> if no intersection exists
	 */
	private static Intersection getIntersectionPoints( final CircleXY circleA, final CircleXY circleB ) {
		// get the center points
		final PointXY p0 = circleA.getMidPoint();
		final PointXY p1 = circleB.getMidPoint();
		
		// get the radius of each circle
		final double r0 = circleA.getRadius();
		final double r1 = circleB.getRadius();
		
		// get the distance between center points
		final double d = PointXY.getDistance( p0, p1 );
		
		// If d > r0 + r1 then there are no solutions, the circles are separate
		if( d > r0 + r1 ) {
			return new Intersection( "There are no solutions, the circles are separate" );
		}
		
		// If d < |r0 - r1| then there are no solutions because one circle is contained within the other
		if( d < abs( r0 - r1 ) ) {
			return new Intersection( "there are no solutions because one circle is contained within the other" );
		}
		
		// If d = 0 and r0 = r1 then the circles are coincident and there are an infinite number of solutions
		if( ( d == 0 ) && ( r0 == r1 ) ) {
			return new Intersection( "the circles are coincident and there are an infinite number of solutions" );
		}
		
		// Considering the two triangles P0,P2,P3 and P1,P2,P3 we can write:
		//		a^2 + h^2 = r0^2 and b^2 + h^2 = r1^2
		
		//	Using d = a + b we can solve for a,
		//		a = (r0^2 - r1^2 + d^2 ) / (2 * d)
		final double a = ( r0*r0 - r1*r1 + d*d ) / ( 2.0d * d );
		
		//	It can be readily shown that this reduces to r0 when the two circles touch at one point, ie: d = r0 + r1
		//	Solve for h by substituting a into the first equation, h^2 = r0^2 - a^2
		final double h = sqrt( r0*r0 - a*a );
		
		//	So:	P2 = P0 + a ( P1 - P0 ) / d
		final PointXY p2 = new PointXY( p0.x + a * ( p1.x - p0.x ) / d, p0.y + a * ( p1.y - p0.y ) / d );
				
		//	And finally, P3 = (x3,y3) in terms of P0 = (x0,y0), P1 = (x1,y1) and P2 = (x2,y2), is
		//		x3 = x2 +|- h ( y1 - y0 ) / d
		//		y3 = y2 -|+ h ( x1 - x0 ) / d
		final PointXY p3 = new PointXY( p2.x + h * ( p1.y - p0.y ) / d, p2.y - h * ( p1.x - p0.x ) / d );		
		final PointXY p4 = new PointXY( p2.x - h * ( p1.y - p0.y ) / d, p2.y + h * ( p1.x - p0.x ) / d );
		
		// return the points
		return !p3.equals( p4 ) ? new Intersection( p3, p4 ) : new Intersection( p3 );
	}
	
	/**
	 * Attempts to determine the intersection points between the circle and composite geometry
	 * @param circle the given {@link CircleXY circle}
	 * @param comp the given {@link CompositionXY composite geometry}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final CircleXY circle, final CompositionXY comp ) {
		// create a container for the intersection points
		final Intersection intersection = new Intersection();
		
		// get the intersection points for each element
		final List<EntityRepresentation> elements = comp.getElements();
		for( final EntityRepresentation element : elements ) {
			logger.info( format( "Attempting to compute the intersection between a circle and a '%s'", element.getType() ) );
			intersection.addAll( getIntersectionPoints( circle, element ) );
		}
		
		// return the array of points
		return intersection;
	}

	/**
	 * Attempts to determine the intersection point(s) between the given ellipse and line.
	 * @see http://mathworld.wolfram.com/Circle-EllipseIntersection.html
	 * @param circle the given {@link CircleXY circle}
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final CircleXY circle, final EllipseXY ellipse ) {
		// cache the circle's properties
		final double r = circle.getRadius();
		
		// cache the ellipse's properties
		final double a = ellipse.getWidth() / 2d;
		final double b = ellipse.getHeight() / 2d;
		
		// compute the four intersections
		final double x1 = +a * sqrt( ( pow( r, 2 ) - pow( b, 2 ) ) / ( pow( a, 2 ) - pow( b, 2 ) ) );
		final double x2 = -a * sqrt( ( pow( r, 2 ) - pow( b, 2 ) ) / ( pow( a, 2 ) - pow( b, 2 ) ) );
		
		final double y1 = +a * sqrt( ( pow( a, 2 ) - pow( r, 2 ) ) / ( pow( a, 2 ) - pow( b, 2 ) ) );
		final double y2 = -a * sqrt( ( pow( a, 2 ) - pow( r, 2 ) ) / ( pow( a, 2 ) - pow( b, 2 ) ) );
		
		
		// TODO compute the intersection
		return NO_INTERSECTION;
	}
	
	/**
	 * Attempts to determine the intersection point between the given circle and line.
	 * <div>Equation: r^2 = (x-a)^2+(mx+c-b)^2</div>
	 * <div><a href="http://www.vb-helper.com/howto_net_line_circle_intersections.html">Reference</a></div>
	 * @param arc the given {@link ArcXY arc}
	 * @param line the given {@link LineXY line}
	 * @return the intersection point or an empty array if no intersection exists
	 */
	private static Intersection getIntersectionPoints( final ArcXY arc, final LineXY line ) {
		// cache the circle's characteristics
		final double cx = arc.getCenterX();
		final double cy = arc.getCenterY();
		final double r 	= arc.getRadius();
		
		// cache the line's characteristics
		final double x1 = line.getX1();
		final double y1 = line.getY1();
		final double x2 = line.getX2();
		final double y2 = line.getY2();
		
		// compute the deltas between the X and Y values
		final double dx = x2 - x1;
		final double dy = y2 - y1;
	
	    // compute the determinant:
		// Determines the number of solutions. If this value < 0, there are no 
		// solutions, your line doesn't intersect the circle. If D = 0, there's 
		// exactly one tangent touching point, and if D > 0 there are two 
		// intersections.
		
		final double a		= dx * dx + dy * dy;
		final double a2 	= 2.0d * a;
		final double b		= 2.0d * ( dx * ( x1 - cx ) + dy * ( y1 - cy ) );
	    final double c		= pow( x1 - cx, 2.0d ) + pow( y1 - cy, 2.0d ) - r * r;
	    final double det	= ( b * b ) - 4.0d * a * c;
	    
	    // No solution can be found
	    if( ( a <= 0.0000001d ) || ( det < 0.0d ) ) {
	        return new Intersection( "No solution can be found" );
	    }
	    
	    // One solution found ...
	    else if( det == 0d ) {
	    	// compute the point coordinates
	    	final double t 		= -b / a2;
	        final double px		= x1 + t * dx;
	        final double py		= y1 + t * dy;
	        
	        // return the point
	        return new Intersection( new PointXY( px, py )  );
	    }
	    
	    // Two solutions found ...
	    else {   	
	    	// compute common elements
	    	final double sqrtdet= sqrt( det );
	    	
	    	// compute the first point coordinates
	    	final double t1		= ( -b + sqrtdet ) / a2;
	        final double px1	= x1 + t1 * dx;
	        final double py1	= y1 + t1 * dy;
	        
	    	// compute the second point coordinates
	        final double t2		= ( -b - sqrtdet ) / a2;
	        final double px2	= x1 + t2 * dx;
	        final double py2	= y1 + t2 * dy;
	        
	        // return the points
	        return new Intersection( 
	        		new PointXY( px1, py1 ), 
	        		new PointXY( px2, py2 ) 
	        );
	    }
	}

	/**
	 * Attempts to determine the intersection point between the given circle and line.
	 * <div>Equation: r^2 = (x-a)^2+(mx+c-b)^2</div>
	 * <div><a href="http://www.vb-helper.com/howto_net_line_circle_intersections.html">Reference</a></div>
	 * @param circle the given {@link CircleXY circle}
	 * @param line the given {@link LineXY line}
	 * @return the intersection point or an empty array if no intersection exists
	 */
	private static Intersection getIntersectionPoints( final CircleXY circle, final LineXY line ) {
		// cache the circle's characteristics
		final double cx = circle.getCenterX();
		final double cy = circle.getCenterY();
		final double r 	= circle.getRadius();
		
		// cache the line's characteristics
		final double x1 = line.getX1();
		final double y1 = line.getY1();
		final double x2 = line.getX2();
		final double y2 = line.getY2();
		
		// compute the deltas between the X and Y values
		final double dx = x2 - x1;
		final double dy = y2 - y1;
	
	    // compute the determinant:
		// Determines the number of solutions. If this value < 0, there are no 
		// solutions, your line doesn't intersect the circle. If D = 0, there's 
		// exactly one tangent touching point, and if D > 0 there are two 
		// intersections.
		
		final double a		= dx * dx + dy * dy;
		final double a2 	= 2.0d * a;
		final double b		= 2.0d * ( dx * ( x1 - cx ) + dy * ( y1 - cy ) );
	    final double c		= pow( x1 - cx, 2.0d ) + pow( y1 - cy, 2.0d ) - r * r;
	    final double det	= ( b * b ) - 4.0d * a * c;
	    
	    // No solution can be found
	    if( ( a <= 0.0000001d ) || ( det < 0.0d ) ) {
	        return new Intersection( "No solution can be found" );
	    }
	    
	    // One solution found ...
	    else if( det == 0d ) {
	    	// compute the point coordinates
	    	final double t 		= -b / a2;
	        final double px		= x1 + t * dx;
	        final double py		= y1 + t * dy;
	        
	        // return the point
	        return new Intersection( new PointXY( px, py )  );
	    }
	    
	    // Two solutions found ...
	    else {   	
	    	// compute common elements
	    	final double sqrtdet= sqrt( det );
	    	
	    	// compute the first point coordinates
	    	final double t1		= ( -b + sqrtdet ) / a2;
	        final double px1	= x1 + t1 * dx;
	        final double py1	= y1 + t1 * dy;
	        
	    	// compute the second point coordinates
	        final double t2		= ( -b - sqrtdet ) / a2;
	        final double px2	= x1 + t2 * dx;
	        final double py2	= y1 + t2 * dy;
	        
	        // return the points
	        return new Intersection( 
	        		new PointXY( px1, py1 ), 
	        		new PointXY( px2, py2 ) 
	        );
	    }
	}
	
	/**
	 * Attempts to determine the intersection points between the line and composite geometry
	 * @param compA the given {@link CompositionXY geometric composition}
	 * @param compB the given {@link CompositionXY geometric composition}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final CompositionXY compA, final CompositionXY compB ) {
		// create a container for the intersection points
		final Intersection intersection = new Intersection();
		
		// get the collections of elements
		final List<EntityRepresentation> elementsA = compA.getElements();
		final List<EntityRepresentation> elementsB = compB.getElements();
		
		// get the intersection points for each element
		for( final EntityRepresentation elementA : elementsA ) {
			for( final EntityRepresentation elementB : elementsB ) {
				intersection.addAll( getIntersectionPoints( elementA, elementB ) );
			}
		}
		
		// return the array of points
		return intersection;
	}
	
	/**
	 * Attempts to determine the intersection points between the line and composite geometry
	 * @param comp the given {@link CompositionXY geometric composition}
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final CompositionXY comp, final EllipseXY ellipse ) {
		// create a container for the intersection points
		final Intersection intersection = new Intersection();
		
		// get the intersection points for each element
		final List<EntityRepresentation> elements = comp.getElements();
		for( final EntityRepresentation element : elements ) {
			intersection.addAll( getIntersectionPoints( ellipse, element ) );
		}
		
		// return the array of points
		return intersection;
	}

	/**
	 * Attempts to determine the intersection points between the line and composite geometry
	 * @param comp the given {@link CompositionXY geometric composition}
	 * @param line the given {@link LineXY line}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final CompositionXY comp, final LineXY line ) {
		// create a container for the intersection points
		final Intersection intersection = new Intersection();
		
		// get the intersection points for each element
		final List<EntityRepresentation> elements = comp.getElements();
		for( final EntityRepresentation element : elements ) {
			intersection.addAll( getIntersectionPoints( line, element ) );
		}
		
		// return the array of points
		return intersection;
	}
	
	/**
	 * Attempts to determine the intersection point(s) between the given ellipse and line.
	 * @see http://www.freemathhelp.com/forum/viewtopic.php?f=10&t=34855
	 * @param ellipseA the given {@link EllipseXY ellipse A}
	 * @param ellipseB the given {@link EllipseXY ellipse B}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final EllipseXY ellipseA, final EllipseXY ellipseB ) {
		// X^2 = b2 - f2(sqrt( ( b1 - f1(X - c1)^2 - c2 ) ) )^2
		
		
		
		
		// TODO compute the intersection
		return NO_INTERSECTION;
	}
	
	/**
	 * Attempts to determine the intersection point(s) between the given ellipse and line.
	 * @see http://www.mombu.com/programming/delphi/t-calculate-ellipse-line-intersection-653276.html
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @param line the given {@link LineXY line}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPoints( final EllipseXY ellipse, final LineXY line ) {
		// test for the only condition that is not handled by this method:
		// is the line vertical?
		if( line.isVertical() ) {
			return getIntersectionPointsVL( ellipse, line );
		}
		
		// cache the ellipse's characteristics
		final double cx	= ellipse.getCenterX();
		final double cy	= ellipse.getCenterY();
		final double a 	= ellipse.getWidth() / 2d;
		final double b 	= ellipse.getHeight() / 2d;

		// cache the line's characteristics
		final double m	= line.getSlope();
		final double n  = line.getYIntecept();
		
	    // compute the determinant:
		// Determines the number of solutions. If this value < 0, there are no 
		// solutions, your line doesn't intersect the ellipse. If D = 0, there's 
		// exactly one tangent touching point, and if D > 0 there are two 
		// intersections.

		final double e 	= m * a / b;
		final double f 	= ( m * cx + n - cy ) / b;
		final double det = sqr(e) - sqr(f) + 1d;
		
	    // No solution can be found
	    if( det < 0.0d ) {
	        return new Intersection( "No solution can be found" );
	    }
	    
	    // One solution found ...
	    else if( det == 0 ) {
	    	// compute the point coordinates
	        final double px	= -a * e * f/(1d + sqr(e)) + cx;
	        final double py	= m * px + n;
	        
	        // return the point
	        return new Intersection( new PointXY( px, py )  );
	    }
	    
	    // Two solutions found ...
	    else {   	
	    	// compute the square root of the determinant
	    	final double sqrtdet = sqrt(det);
	    	
	    	// compute the coordinates for point #1
	        final double px1	= a*(-e*f - sqrtdet)/(1d + sqr(e)) + cx;
	        final double py1	= m * px1 + n;
	        
	        // compute the coordinates for point #2
	        final double px2	= a * (-e * f + sqrtdet ) / (1d + sqr(e) ) + cx;
	        final double py2	= m * px2 + n;
	        
	        // return the points
	        return new Intersection( 
	        		new PointXY( px1, py1 ), 
	        		new PointXY( px2, py2 ) 
	        );
	    }
	}
	
	/**
	 * Attempts to determine the intersection point(s) between the given ellipse and vertical line.
	 * @param ellipse the given {@link EllipseXY ellipse}
	 * @param line the given {@link LineXY vertical line}
	 * @return the {@link Intersection solution} 
	 */
	private static Intersection getIntersectionPointsVL( final EllipseXY ellipse, final LineXY line ) {
		// cache the ellipse's characteristics
		final double cx	= ellipse.getCenterX();
		final double cy	= ellipse.getCenterY();
		final double a 	= ellipse.getWidth() / 2d;
		final double b 	= ellipse.getHeight() / 2d;
		
		// retrieve the X-axis value from the vertical line
		final double x  = line.getX1();
		
		// is the line within striking distance of the ellipse?
		if( ( x < cx - a ) || ( x > cx + a ) ) {
			return NO_INTERSECTION;
		}
		
		// compute the Y-axis value
		final double dy = b * sin( acos( abs( cx - x ) / a ) );
		
		 // return the points
        return new Intersection( 
        		new PointXY( x, cy - dy ), 
        		new PointXY( x, cy + dy ) 
        );
	}
	
	/**
	 * Attempts to determine the intersection point between the lines #1 and line #2
	 * @param line1 the given {@link LineXY line A}
	 * @param line2 the given {@link LineXY line B}
	 * @return the intersection point or <tt>null</tt> if no intersection exists
	 */
	private static Intersection getIntersectionPoints( final LineXY line1, final LineXY line2 ) {
		final PointXY point = getIntersectionPoint( line1, line2 );
		
		// was a solution found?
		if( point != null ) {
			return new Intersection( point );
		}
		
		// are the lines parallel?
		else if( line1.isParallelTo( line2 ) ) {
			return new Intersection( "No solutions exist; lines are parallel." );
		}
		
		// the must be separate
		else {
			return new Intersection( "No solutions exist; lines are separate." );
		}
	}
	
	/** 
	 * Represents an intersection between two geometric entities
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Intersection {
		private static final List<PointXY> NO_POINTS = new ArrayList<PointXY>( 0 );
		private List<PointXY> points;
		private String message;
		
		/** 
		 * Creates a new intersection
		 * @param points the given intersection points
		 */
		public Intersection( final PointXY... points ) {
			this.points = new LinkedList<PointXY>( Arrays.asList( points ) );
		}
		
		/** 
		 * Creates a new intersection
		 * @param points the given intersection points
		 */
		public Intersection( final Collection<PointXY> points ) {
			this.points = new LinkedList<PointXY>( points );
		}
		
		/** 
		 * Creates a new intersection with an error message
		 * @param message the given error message
		 */
		public Intersection( final String message ) {
			this.message	= message;
			this.points		= NO_POINTS;
		}
		
		public void addAll( final Intersection intersection ) {
			points.addAll( intersection.getPoints() );
		}
		
		/** 
		 * Returns the intersection points
		 * @return the intersection {@link PointXY points}
		 */
		public PointXY[] getPointArray() {
			return points.toArray( new PointXY[ points.size() ]);
		}
		
		/** 
		 * Returns the intersection points
		 * @return the intersection {@link PointXY points}
		 */
		public List<PointXY> getPoints() {
			return points;
		}
		
		/**
		 * Indicates whether the 
		 * @return
		 */
		public boolean hasError() {
			return message != null;
		}

		public String getErrorMessage() {
			return message;
		}

		/** 
		 * Returns the number of solutions
		 * @return the number of solutions
		 */
		public int size() {
			return points.size();
		}
		
	}

}
