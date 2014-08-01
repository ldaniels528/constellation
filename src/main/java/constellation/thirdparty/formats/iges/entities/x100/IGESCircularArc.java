package constellation.thirdparty.formats.iges.entities.x100;

import static constellation.thirdparty.formats.iges.IGESConstants.IGES_COLORS;
import constellation.drawing.LinePatterns;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.PointXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * Represents an IGES Circular Arc
 * @author lawrence.daniels@gmail.com
 */
public class IGESCircularArc implements IGESEntity {
	private final int colorNumber;
	private final int lineFontPattern;
	private final double cx;
	private final double cy;
	private final double ax;
	private final double ay;
	private final double bx;
	private final double by;
	private final double zt;
	
	/** 
	 * Creates a circular arc
	 * @param cx the x-coordinate of the center point
	 * @param cy the y-coordinate of the center point
	 * @param ax the x-coordinate of the start point
	 * @param ay the y-coordinate of the start point
	 * @param bx the x-coordinate of the termination point
	 * @param by the y-coordinate of the termination point
	 * @param zt the z-coordinate for all points
	 */
	public IGESCircularArc( final double cx, 
							final double cy, 
							final double ax, 
							final double ay,
							final double bx, 
							final double by, 
							final double zt,
							final int colorNumber,
							final int lineFontPattern ) {
		this.cx	 			= cx;
		this.cy 			= cy;
		this.ax 			= ax;
		this.ay 			= ay;
		this.bx 			= bx;
		this.by 			= by;
		this.zt 			= zt;
		this.colorNumber 	= colorNumber;
		this.lineFontPattern= lineFontPattern;
	}

	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// create the points
		final PointXY center = new PointXY( cx, cy );
		final PointXY start	 = new PointXY( ax, ay );
		final PointXY end 	 = new PointXY( bx, by );
		
		// compute the radius
		final double radius	= PointXY.getDistance( center, start ); 
		
		// get the arc angles
		final double angle1 = PointXY.getAngle( center, start );
		final double angle2 = PointXY.getAngle( center, end );
		
		// create the arc
		final ModelElement crvElem = new CxModelElement( new ArcXY( cx, cy, radius, angle1, angle2 ) );
		crvElem.setColor( IGES_COLORS[ colorNumber ] );
		crvElem.setPattern( LinePatterns.values()[ lineFontPattern ] );
		
		// return the curve
		return new ModelElement[] { crvElem };
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "ZT=%.2f Center (%.2f,%.2f) Start (%.2f,%.2f) Terminate (%.2f,%.2f)", zt, cx, cy, ax, ay, bx, by );
	}
	
}
