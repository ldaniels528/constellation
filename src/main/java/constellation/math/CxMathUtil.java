package constellation.math;

import static constellation.math.CxMathUtil.Quadrant.*;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.String.format;

/** 
 * Constellation Math Utilities
 * @author lawrence.daniels@gmail.com
 */
public class CxMathUtil {
	public  static final double TOLERANCE = 0.0001;
	static final double PI_0_5 = PI * 0.5d;
	static final double PI_1_5 = PI * 1.5d;
	
	/**
	 * Clips the given value to insure it is within the given
	 * minimum and maximum bounds.
	 * @param value the given value
	 * @param min the given minimum bound
	 * @param max the given maximum bound
	 * @return the clipped value
	 */
	public static int clipValue( final int value, final int min, final int max ) {
		return ( value < min ) ? min : ( value > max ) ? max : value;
	}

	/** 
	 * Converts the given angle (in radians) to degrees
	 * <div>1 radians = 57.2957795 degrees</div>
	 * @return the angle in degrees
	 */
	public static double convertRadiansToDegrees( final double radians ) {
		return radians * 57.2957795d;
	}
	
	/** 
	 * Converts the given angle (in degrees) to radians
	 * <div>1 degrees = 0.0174532925 radians</div>
	 * @return the angle in radians
	 */
	public static double convertDegreesToRadians( final double degrees ) {
		return degrees * 0.0174532925d;
	}
	
	/** 
	 * Computes the angle through the given center- and end- points
	 * @param cx the given X-axis of the center point
	 * @param cy the given Y-axis of the center point
	 * @param ex the given X-axis of the end point
	 * @param ey the given Y-axis of the end point
	 * @return the angle (in radians)
	 */
	public static double getAngle( final double cx, final double cy, final double ex, double ey ) {
		// compute the delta values
		final double dy = ( ey - cy );
		final double dx = ( ex - cx );
		
		// determine the point's quadrant 
		final Quadrant q = getQuadrant( cx, cy, ex, ey );
		
		// is the line horizontal?
		double angle;
		if( dy == 0 ) {
			angle = ( q == Q1 ) ? 0 : 0;
		}
		
		// is the line vertical?
		else if( dx == 0 ) {
			angle = ( q == Q2 ) ? PI_0_5 : -PI_0_5;
		}
		
		// line is oblique
		else {
			// compute the angle
			// tan t = ( y2 - y1 ) / ( x2 - x1 )
			angle = atan( dy / dx );
		}
					
		// adjust the angle based on the quadrant
		switch( q ) {
			case Q1: angle = PI_0_5 + angle; break;
			case Q2: angle = PI_0_5 + angle; break;
			case Q3: angle = PI_1_5 + angle; break;
			case Q4: angle = PI_1_5 + angle; break;
		}
		
		return angle;
	}
	
	/** 
	 * Returns the quadrant the given end point is located in
	 * @param cx the given X-axis of the center point
	 * @param cy the given Y-axis of the center point
	 * @param ex the given X-axis of the end point
	 * @param ey the given Y-axis of the end point
	 * @return the {@link Quadrant quadrant}
	 */
	public static Quadrant getQuadrant( final double cx, final double cy, final double ex, double ey ) {
		// determine the quadrant (Q1-Q4)
		//	Q4 | Q1
		//  -------
		//	Q3 | Q2
		
		Quadrant q = null;
			 if( ex >= cx && ey <= cy ) { q = Q1; }
		else if( ex >= cx && ey >= cy ) { q = Q2; }
		else if( ex <= cx && ey >= cy ) { q = Q3; }
		else if( ex <= cx && ey <= cy ) { q = Q4; }
		else {
			throw new IllegalStateException( format( "The quandrant could not be identified; cp=(%3.2f,%3.2f), ep=(%3.2f,%3.2f)", cx, cy, ex, ey) );
		}
			 
		//Logger.getLogger( CurveXY.class ).info( format( "Quadrant data: cp=%s, ep=%s, q=%s", cp, ep, q ) );
		return q;
	}
	
	/**
	 * Compares the given doubles within 0.0001 tolerance
	 * @param d1 one of two {@link Double double}
	 * @param d2 one of two {@link Double double}
	 * @return true, if the doubles are equal within +/- 0.0001
	 */
	public static boolean isEqual( final Double d1, final Double d2 ) {
		return abs( d1 - d2 ) < TOLERANCE;
	}

	/**
	 * Squares the given value
	 * @param value the given value 
	 * @return the square of the value
	 */
	public static double sqr( final double value ) {
		return value*value;
	}
	
	/**
	 * Represents one of four quadrants in two-dimensional space
	 * <pre>
	 *	Q4 | Q1
	 *  -------
	 *	Q3 | Q2
	 * </pre>
	 * @author lawrence.daniels@gmail.com
	 */
	public static enum Quadrant {
		
		/**
		 * The upper right quadrant
		 */
		Q1, 
		
		/**
		 * The lower right quadrant
		 */
		Q2, 
		
		/**
		 * The lower left quadrant
		 */
		Q3, 
		
		/**
		 * The upper left quadrant
		 */
		Q4
	}

}
