package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents an elliptic arc on a two-dimensional plane
 * <div><a href="http://en.wikipedia.org/wiki/Ellipse">Wikipedia reference</a></div>
 * @author lawrence.daniels@gmail.com
 */
public class EllipticArcXY extends CurveXY {
	private static final int SECTIONS = 72; // factor of 4
	private final PointXY location;
	private final double angleStart; 
	private final double angleEnd;
	private final double width; 
	private final double height;
	private final double a;
	private final double b;
	
	/**
	 * Creates a new elliptic arc
	 * @param p the given {@link PointXY location} in two-dimensional space
	 * @param width the given width of the ellipse
	 * @param height the given height of the ellipse
	 * @param angleStart the starting angle of the ellipse (arc only)
	 * @param angleEnd the starting angle of the ellipse (arc only)
	 */
	public EllipticArcXY( final PointXY p, 
						  final double width, 
						  final double height,
						  final double angleStart, 
						  final double angleEnd) {
		this.location	= p;
		this.width		= width;
		this.height		= height;
		this.angleStart	= angleStart;
		this.angleEnd	= angleEnd;
		this.a			= width / 2d;
		this.b			= height / 2d;
	}
	
	/**
	 * Creates a new elliptic arc
	 * @param cx the given x-coordinate
	 * @param cy the given y-coordinate
	 * @param width the given width of the ellipse
	 * @param height the given height of the ellipse
	 * @param angleStart the starting angle of the ellipse (arc only)
	 * @param angleEnd the starting angle of the ellipse (arc only)
	 */
	public EllipticArcXY( final double cx, 
						  final double cy, 
						  final double width, 
						  final double height,
						  final double angleStart, 
						  final double angleEnd) {
		this( new PointXY( cx, cy ), width, height, angleStart, angleEnd );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.CurveXY#duplicate(constellation.drawing.entities.PointXY)
	 */
	public CurveXY duplicate( double dx, double dy ) {
		final PointXY p = location.getOffset( dx, dy );
		return new EllipticArcXY( p.x, p.y, width, height, angleStart, angleEnd );
	}

	/**
	 * Returns the starting angle of the ellipse (arc only)
	 * @return the starting angle of the ellipse
	 */
	public double getAngleStart() {
		return angleStart;
	}
	
	/**
	 * Returns the end angle of the ellipse (arc only)
	 * @return the end angle of the ellipse
	 */
	public double getAngleEnd() {
		return angleEnd;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		final double cx = getCenterX();
		final double cy = getCenterY();
		return new RectangleXY( cx - a, cy - b, width, height );
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
	 * @see constellation.drawing.entities.CurveXY#getParallelCurve(constellation.drawing.entities.PointXY)
	 */
	public CurveXY getParallelCurve( final PointXY offset ) {		
		// compute the distance
		final double distance = PointXY.getDistance( getMidPoint(), offset );
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// return the new elliptic arc
		return new EllipticArcXY( cx, cy, width + distance, height + distance, angleStart, angleEnd );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getLimits()
	 */
	public VerticesXY getLimits() {
		// get the center point axes
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// return the vertices
		return new VerticesXY(
				cx - a, cy,
				cx + a, cy,
				cx, 	cy,
				cx, 	cy - b,
				cx, 	cy + b
		);
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.ComplexInternalRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return new PointXY( getCenterX(), getCenterY() );
	}
	
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.ELLIPTIC_ARC;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.ComplexInternalRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		// compute the necessary values
		final double delta = PI / (double)( SECTIONS - 1 );
		
		// determine the start and end angles
		final double start	= angleStart;
		final double end	= angleEnd;
		
		// get the center point axis
		final double cx = getCenterX();
		final double cy = getCenterY();
		
		// compute 'a' and 'b'
		final double a	= width / 2.0d;
		final double b	= height / 2.0d;
		
		// construct the ellipse
		final List<PointXY> points = new LinkedList<PointXY>();
		for( double t = start; t < end; t += delta ) {
			// compute (x,y)
			final double rx = cx + a * cos(t);
			final double ry = cy + b * sin(t);
			
			// add the points
			points.add( new PointXY( rx, ry ) );
		}
		
		// return the points
		return EntityRepresentationUtil.toLimits( points );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, MatrixWCStoSCS matrix ) {
		return boundary.intersects( getBounds( matrix ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#length()
	 */
	public double length() {
		return ( ( sqrt( 0.5d * ( ( height * height ) + ( width * width ) ) ) ) * ( PI * 2.0d ) ) / 2.0d; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public EllipticArcXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new EllipticArcXY( p, width, height, angleStart, angleEnd );
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
	 * @see constellation.geometry.ConicXY#toEquation()
	 */
	public String toEquation() {
		return format( "(%5.4f, %5.4f), w = %1.4f, h = %1.4f", getCenterX(), getCenterY(), width, height );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toEquation();
	}

}
