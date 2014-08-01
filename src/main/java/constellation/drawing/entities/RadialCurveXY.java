package constellation.drawing.entities;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Represents the radial curve
 * @author lawrence.daniels@gmail.com
 */
public abstract class RadialCurveXY extends CurveXY {
	
	/**
	 * Default constructor
	 */
	protected RadialCurveXY() {
		super();
	} 
	
	/** 
	 * Computes a point based on the given Y-value
	 * @param radius the given radius
	 * @param y the given Y-value
	 * @return the X-axis coordinate value
	 */
	public static double computeRadialXValue( final double radius, final double y ) {
		// equation: x = sqrt(r^2 - y^2)
		return sqrt( pow( radius, 2d ) - pow( y, 2d ) );
	}

	/** 
	 * Computes a point based on the given Y-value
	 * @param radius the given radius
	 * @param x the given X-value
	 * @return the Y-axis coordinate value
	 */
	public static double computeRadialYValue( final double radius, final double x ) {
		// equation: y = sqrt(r^2 - x^2)
		return sqrt( pow( radius, 2d ) - pow( x, 2d ) );
	}

	/**
	 * Returns the X-coordinate of the center point
	 * @return the X-coordinate
	 */
	public abstract double getCenterX();
	
	/**
	 * Returns the Y-coordinate of the center point
	 * @return the Y-coordinate
	 */
	public abstract double getCenterY();
	
	/** 
	 * Returns the midpoint of the curve
	 * @return the {@link PointXY midpoint} of the curve
	 */
	public abstract PointXY getMidPoint();
	
	/**
	 * Returns the radius
	 * @return the radius
	 */
	public abstract double getRadius();
	
	/**
	 * Attempts to determine the intersection point between the given circle and line.
	 * <div>Equation: r^2 = (x-a)^2+(mx+c-b)^2</div>
	 * @param boundary the given {@link RectangleXY boundary}
	 * @param x1 the given minimum X-coordinate of point P1
	 * @param y1 the given minimum Y-coordinate of point P1
	 * @param x2 the given maximum X-coordinate of point P2
	 * @param y2 the given maximum Y-coordinate of point P2
	 * @param r the given outer radius
	 * @see http://www.vb-helper.com/howto_net_line_circle_intersections.html
	 * @return true, if an intersection exists
	 */
	protected boolean checkIntersection( final RectangleXY boundary,
									   	 final double x1,
									   	 final double y1,
									   	 final double x2,
									   	 final double y2, 
									   	 final double r ) {
		// cache the center points
		final double cx 	= this.getCenterX();
		final double cy 	= this.getCenterY();
		
		// compute the deltas between the X and Y values
		final double dx 	= x2 - x1;
		final double dy 	= y2 - y1;
	
	    // compute the determinant
		final double a		= dx * dx + dy * dy;
		final double b		= 2.0d * ( dx * ( x1 - cx ) + dy * ( y1 - cy ) );
	    final double c		= pow( x1 - cx, 2.0d ) + pow( y1 - cy, 2.0d ) - r * r;
	    final double det	= ( b * b ) - 4 * a * c;
	    
	    // No solution can be found
	    if( ( a <= 0.0000001 ) || ( det < 0 ) ) {
	        return false;
	    }
	    
	    // One solution found ...
	    else if( det == 0 ) {
	    	// compute the point coordinates
	    	final double t 		= -b / ( 2.0d * a );
	        final double px		= x1 + t * dx;
	        final double py		= y1 + t * dy;
	        
	        // does the boundary contain the point?
	        return boundary.contains( px, py );
	    }
	    
	    // Two solutions found ...
	    else {   	
	    	// compute common elements
	    	final double sqrtdet	= sqrt( det );
	    	final double a2 		= 2.0d * a;
	    	
	    	// compute the first point coordinates
	    	final double t1		= ( -b + sqrtdet ) / a2;
	        final double px1	= x1 + t1 * dx;
	        final double py1	= y1 + t1 * dy;
	        
	    	// compute the second point coordinates
	        final double t2		= ( -b - sqrtdet ) / a2;
	        final double px2	= x1 + t2 * dx;
	        final double py2	= y1 + t2 * dy;
	        
	        // does the boundary contain one of the points?
	        return boundary.contains( px1, py1 ) || boundary.contains( px2, py2 );
	    }
	}
	
}
