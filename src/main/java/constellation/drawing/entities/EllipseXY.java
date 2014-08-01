package constellation.drawing.entities;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.CxIntersectionUtil;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents an ellipse on a two-dimensional plane
 * @see http://en.wikipedia.org/wiki/Ellipse
 * @see http://home.scarlet.be/~ping1339/Pana.htm
 * @see http://www.cut-the-knot.org/Curriculum/Geometry/AngleBisectorsInEllipse.shtml
 * @author lawrence.daniels@gmail.com
 */
public class EllipseXY extends CurveXY {
	private static final int SECTIONS = 72; // factor of 4
	private PointXY location;
	private final double width; 
	private final double height;
	private final double a;
	private final double b;
	
	/**
	 * Creates a new ellipse
	 * @param p the given {@link PointXY position} in two-dimensional space
	 * @param width the given width of the ellipse
	 * @param height the given height of the ellipse
	 */
	public EllipseXY( final PointXY p, 
					  final double width, 
					  final double height ) {
		this.location	= p;
		this.width		= width;
		this.height		= height;
		this.a			= width / 2d;
		this.b			= height / 2d;
	}
	
	/**
	 * Creates a new ellipse
	 * @param cx the given x-coordinate
	 * @param cy the given y-coordinate
	 * @param width the given width of the ellipse
	 * @param height the given height of the ellipse
	 */
	public EllipseXY( final double cx, 
					  final double cy, 
					  final double width, 
					  final double height ) {
		this( new PointXY( cx, cy ), width, height );
	}

	/**
	 * Creates a new ellipse through two points
	 * @param cp the center point of the ellipse
	 * @param ep an outer point of the ellipse
	 * @return a new {@link EllipseXY ellipse}
	 */
	public static EllipseXY createEllipseThru2Points( final PointXY cp, final PointXY ep ) {
		// get the x, y, width, and height
		final double x 	= cp.getX();
		final double y 	= cp.getY();
		final double a 	= abs( cp.getX() - ep.getX() );
		final double b	= abs( cp.getY() - ep.getY() );
		
		// return the ellipse
		return new EllipseXY( x, y, a * 2d, b * 2d );
	}

	/**
	 * Creates a new ellipse using the given three points
	 * <br><a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/circlefrom3/">Reference</a>
	 * @param p1 the point #1 of the ellipse
	 * @param p2 the point #2 of the ellipse
	 * @param p3 the point #3 of the ellipse
	 * @return a new {@link EllipseXY ellipse}
	 */
	public static EllipseXY createEllipseThru3Points( final PointXY p1, final PointXY p2, final PointXY p3 ) {
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
		final double x 	= cp.getX();
		final double y 	= cp.getY();
		final double a	= PointXY.getDistance( cp, p1 );
		final double b	= PointXY.getDistance( cp, p1 );
		
		// create the circle geometry
		return new EllipseXY( x, y, a * 2d, b * 2d );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CurveXY#duplicate(double, double)
	 */
	public CurveXY duplicate( final double dx, final double dy ) {
		return new EllipseXY( location.getOffset( dx, dy ), width, height );
	}

	/**
	 * Returns the area of the ellipse
	 * @return the area of the ellipse
	 * @see http://mathworld.wolfram.com/Ellipse.html
	 */
	public double getArea() {
		return PI * a * b;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#getBounds(constellation.math.MatrixWCStoSCS)
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		final double cx = location.x;
		final double cy = location.y;
		return new RectangleXY( cx - a, cy - b, width, height );
	}
	
	/** 
	 * Returns the X-axis of the center position
	 * @return the X-axis of the center position
	 */
	public double getCenterX() {
		return location.getX();
	}
	
	/**
	 * Returns the Y-axis of the center position
	 * @return the Y-axis of the center position
	 */
	public double getCenterY() {
		return location.getY();
	}
	
	/**
	 * Returns the circumference of the circle (<tt>C=PI(a+b)</tt>)
	 * @return the circumference of the circle
	 */
	public double getCircumference() {
		return PI * ( a + b );
	}
	
	/** 
	 * Returns the eccentricity of the ellipse
	 * <div>Formula: <tt>e = sqrt( (a^2-b^2)/a^2 )</tt></div>
	 * @return the eccentricity of the ellipse
	 */
	public double getEccentricity() {
		return sqrt( ( a*a - b*b ) / a*a );
	}
	
	/**
	 * Returns the half width (a) of the ellipse
	 * @return the width
	 */
	public double getHalfWidth() {
		return a;
	}
	
	/**
	 * Returns the width of the ellipse
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Returns the half height (b) of the ellipse
	 * @return the height
	 */
	public double getHalfHeight() {
		return b;
	}

	/**
	 * Returns the height of the ellipse
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CurveXY#getLimits()
	 */
	public VerticesXY getLimits() {
		// get the center point axes
		final double cx = location.x;
		final double cy = location.y;
		
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
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return location;
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
		
		// return the new ellipse
		return new EllipseXY( cx, cy, width + distance, height + distance );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.ELLIPSE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		// compute the necessary values
		final double delta = PI / (double)( SECTIONS - 1 );
		
		// get the center point axis
		final double cx = location.x;
		final double cy = location.y;
		
		// create the vertex set
		final VerticesXY vertices = new VerticesXY( SECTIONS );
		
		// construct the ellipse
		for( double t = -PI; t < +PI; t += delta ) {
			// compute (x,y)
			final double px = cx + a * cos(t);
			final double py = cy + b * sin(t);
			
			// add the points
			vertices.add( px, py );
		}
		
		// add the last point to close the curve
		vertices.close();
		
		// return the vertices
		return vertices;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return CxIntersectionUtil.intersects( boundary, this );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#length()
	 */
	public double length() {
		return ( ( sqrt( 0.5d * ( ( height * height ) + ( width * width ) ) ) ) * ( PI * 2d ) ) / 2d;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public EllipseXY mirror( final LineXY plane ) {
		// mirror the center point (x,y)
		final PointXY p = location.mirror( plane );
		
		// create the ellipse
		return new EllipseXY( p, width, height );
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
		return format( "%s, a = %1.4f, b = %1.4f", location, a, b );
	}
	
}
