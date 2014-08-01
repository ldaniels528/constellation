package constellation.drawing.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents a User Defined Conic
 * @author lawrence.daniels@gmail.com
 */
public class UserDefinedConicXY extends CurveXY {
	// arc-related fields
	private final Double angleStart; 
	private final Double angleEnd;
	
	// axial fields
	private final PointXY p1;
	private final PointXY p2;
	
	// coefficient fields
	private final double a; 
	private final double b;
	private final double c; 
	private final double d; 
	private final double e; 
	private final double f; 
	
	/**
	 * Creates a new User-defined Conic curve
	 * @param x1 the X-axis coordinate of point #1
	 * @param y1 the Y-axis coordinate of point #1
	 * @param x2 the X-axis coordinate of point #2
	 * @param y2 the Y-axis coordinate of point #2
	 * @param a coefficient #1 of 6
	 * @param b coefficient #2 of 6
	 * @param c coefficient #3 of 6
	 * @param d coefficient #4 of 6
	 * @param e coefficient #5 of 6
	 * @param f coefficient #6 of 6
	 */
	public UserDefinedConicXY( final double x1, 
							   final double y1, 
							   final double x2, 
							   final double y2, 
							   final double a, 
							   final double b, 
							   final double c, 
							   final double d, 
							   final double e, 
							   final double f ) {
		this.p1			= new PointXY( x1, y1 );
		this.p2			= new PointXY( x2, y2 );
		this.a			= a;
		this.b			= b;
		this.c			= c;
		this.d			= d;
		this.e 			= e;
		this.f			= f;
		this.angleStart	= null;
		this.angleEnd	= null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getBounds()
	 */
	public RectangleXY getBounds(MatrixWCStoSCS matrix) {
		return null;
	}


	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ConicXY#getX()
	 */
	public double getX() {
		return p1.x;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ConicXY#getY()
	 */
	public double getY() {
		return p1.y;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ConicXY#getAngleStart()
	 */
	public Double getAngleStart() {
		return angleStart;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ConicXY#getAngleEnd()
	 */
	public Double getAngleEnd() {
		return angleEnd;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#contains(constellation.drawing.entities.representations.PointXY)
	 */
	public boolean contains( final Point2D point ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#duplicate(constellation.drawing.entities.representations.PointXY)
	 */
	public CurveXY duplicate( double dx, double dy ) {
		return new UserDefinedConicXY( 
				dx + p1.x, dy + p1.y, 
				dx + p2.x, dy + p2.y, 
				a, b, c, d, e, f 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getLimits()
	 */
	public VerticesXY getLimits() {
		return new VerticesXY( p1.x, p1.y, p2.x, p2.y );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#getMidPoint()
	 */
	public PointXY getMidPoint() {
		return PointXY.getMidPoint( p1, p2 );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getOffsetCurve(constellation.drawing.entities.representations.PointXY)
	 */
	public CurveXY getParallelCurve(PointXY offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.USER_DEFINED;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		final VerticesXY vertices = new VerticesXY();
		// TODO Auto-generated method stub
		return vertices;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(constellation.drawing.entities.representations.BoundaryXY)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		return boundary.contains( p1 ) || boundary.contains( p2 );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#length()
	 */
	public double length() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public LineXY mirror( final LineXY plane ) {
		throw new IllegalStateException( "Not yet implemented" );
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
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#toEquation()
	 */
	public String toEquation() {
		return "A * XT^2 + B * XT * YT + C * YT^2 + D * XT + E * YT + F = 0";
	}

}
