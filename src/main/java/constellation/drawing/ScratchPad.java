package constellation.drawing;

import static java.lang.String.format;

import java.awt.Point;

import org.apache.log4j.Logger;

import constellation.drawing.entities.VerticesXY;

/**
 * This object is used by geometric representations as a
 * scratch area for transformations from space to screen
 * coordinates.
 * @author lawrence.daniels@gmail.com
 */
public class ScratchPad {
	private static final Logger logger = Logger.getLogger( ScratchPad.class );
	private static Point[] projectedPoints = createProjectedPoints( 1000 );
	
	/**
	 * Private Constructor
	 */
	private ScratchPad() {
		super();
	}
	
	public static Point getProjectionPoint() {
		return projectedPoints[0];
	}
	
	public static Point[] getProjectionPoints( final VerticesXY vertices ) {
		insureCapacity( vertices );
		return projectedPoints;
	}
	
	/** 
	 * Creates the reusable projected points for transforming geometry
	 * @param count the given number of scratch points to create
	 * @return the {@link Point scratch points}
	 */
	private static Point[] createProjectedPoints( final int count ) {
		final Point[] points = new Point[ count ];
		for( int n = 0; n < points.length; n++ ) {
			points[n] = new Point( 0, 0 );
		}
		return points;
	}
	
	/**
	 * Insures the capacity of screen points for projecting the given points
	 * @param vertices the given array of {@link VerticesXY points} for projecting
	 */
	private static void insureCapacity( final VerticesXY vertices ) {
		// get the required capacity
		final int capacity = vertices.length();
		
		// reallocate if necessary
		if( capacity > projectedPoints.length ) {
			logger.info( format( "insureCapacity: Increasing projection points from %d to %d",  projectedPoints.length, capacity ) );
			projectedPoints = createProjectedPoints( capacity );
		}
	}
	
}
