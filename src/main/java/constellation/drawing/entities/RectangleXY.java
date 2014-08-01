package constellation.drawing.entities;

import java.awt.geom.Rectangle2D;

/**
 * Represents a two-dimensional rectangle
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class RectangleXY extends Rectangle2D.Double {
	
	/** 
	 * Creates a new rectangle instance
	 * @param x the X-axis coordinate of the start of the rectangle or boundary
	 * @param y the Y-axis coordinate of the start of the rectangle or boundary
	 * @param width the width of the rectangle or boundary
	 * @param height the height of the rectangle or boundary
	 */
	public RectangleXY( final double x, 
						final double y, 
						final double width, 
						final double height ) {
		super( x, y, width, height );
	}
	
	/**
	 * Returns the line segments that comprise the rectangle
	 * @param rect the given {@link RectangleXY rectangle}
	 * @return the {@link LineXY lines}
	 */
	public static LineXY[] explode( final RectangleXY rect ) {
		// cache the rectangle coordinates
		final double x1 = rect.getMinX();
		final double y1 = rect.getMinY();
		final double x2 = rect.getMaxX();
		final double y2 = rect.getMaxY();
		
		// create the line 
		return new LineXY[] {
			new LineXY( x1, y1, x2, y1 ),
			new LineXY( x1, y1, x1, y2 ),
			new LineXY( x2, y1, x2, y2 ),
			new LineXY( x1, y2, x2, y2 )
		};
	}
	
	/**
     * Tests if a specified coordinate is inside the boundary of this
     * <code>RectangleXY</code>.
     * @param x,&nbsp;y the coordinates to test
     * @return <code>true</code> if the specified coordinates are
     * inside the boundary of this <code>RectangleXY</code>;
     * <code>false</code> otherwise.
     */
	public boolean contains( final PointXY p ) {
		return super.contains( p.x, p.y );
	}

	/**
	 * Returns the center point of the boundary
	 * @return the {@link PointXY midpoint}
	 */
	public PointXY getMidPoint() {
		final double x = super.getMinX();
		final double y = super.getMinY();
		final double w = super.getWidth();
		final double h = super.getHeight();
		
		final double cx = x + ( w / 2.0d );
		final double cy = y + ( h / 2.0d );
		return new PointXY( cx, cy );
	}

}
