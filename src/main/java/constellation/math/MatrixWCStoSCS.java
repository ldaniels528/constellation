package constellation.math;

import java.awt.Point;
import java.awt.Rectangle;

import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.VerticesXY;

/**
 * This matrix is responsible for converting values from the 
 * World Coordinate System (SCS) to the Screen Coordinate System (MCS)
 * and back.
 * @author lawrence.daniels@gmail.com
 */
public class MatrixWCStoSCS {
	// internal fields
	private final Matrix2D xy2scs;
	private final Matrix2D scs2xy;
	private boolean dirty;
	private double cx,cy;
	private double scale;
	private double unitScale;
	private double rx,ry; 
	private double tx,ty;

	/** 
	 * Default constructor
	 */
	public MatrixWCStoSCS() {
		this.xy2scs		= new Matrix2D();
		this.scs2xy		= new Matrix2D();
		this.unitScale	= 1.0d;
		this.scale		= unitScale;
		this.dirty		= true;
	}
	
	/** 
	 * Sets the center of the view port
	 * @param cx the given center x-coordinate
	 * @param cy the given center y-coordinate
	 */
	public void setOrigin( final double cx, final double cy ) {
		this.cx 	= cx;
		this.cy 	= cy;
		this.tx		= cx;
		this.ty		= cy;
		this.rx		= 0.0;
		this.ry		= 0.0;
		this.dirty 	= true;
	}
	
	/**
	 * Moves the camera relative to the specified delta values
	 * @param dx the given delta x-coordinate 
	 * @param dy the given delta y-coordinate
	 */
	public void moveBy( final double dx, final double dy ) {
		this.tx 	+= dx;
		this.ty 	+= dy;
		this.dirty 	= true;
	}
	
	/**
	 * Moves the camera to the specified location
	 * @param x the given x-coordinate
	 * @param y the given y-coordinate
	 */
	public void moveTo( final double x, final double y ) {
		this.tx 	= x;
		this.ty 	= y;
		this.dirty 	= true;
	}
	
	/**
	 * Adjusts the current 3-D rotation by the X-, Y-, and Z-axis delta values
	 * @param angleDX the given delta angle of rotation about the X-axis
	 * @param angleDY the given delta angle of rotation about the Y-axis
	 */
	public void adjustRotation( final double angleDX, final double angleDY ) {
		this.rx 	+= angleDX;
		this.ry 	+= angleDY;
		this.dirty	= true;
	}
	
	/**
	 * Rotate about the X-axis
	 * @param angle the given angle of rotation
	 */
	public void rotateAboutXAxis( final double angle ) {
		this.rx 	= angle;
		this.dirty	= true;
	}
	
	/**
	 * Rotate about the Y-axis
	 * @param angle the given angle of rotation
	 */
	public void rotateAboutYAxis( final double angle ) {
		this.ry 	= angle;
		this.dirty	= true;
	}
	
	/** 
	 * Returns the current scaling factor
	 * @return the current scaling factor
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * Sets the scale
	 * @param scale the given scale
	 */
	public void setScale( final double scale ) {
		this.scale	= scale;
		this.dirty 	= true;
	}
	
	/** 
	 * Returns the current unit scaling factor
	 * @return the current unit scaling factor
	 */
	public double getUnitScale() {
		return unitScale;
	}

	/**
	 * Sets the model scale
	 * @param unitScale the given model scale
	 */
	public void setUnitScale( final double unitScale ) {
		this.unitScale	= unitScale;
		this.dirty 		= true;
	}

	/**
	 * Repositions the camera to the given (x,y) coordinates
	 */
	public void reset() {		
		this.tx 	= cx;
		this.ty 	= cy;
		this.rx		= 0.0d;
		this.ry		= 0.0d;
		this.scale	= 1.0;
		this.dirty 	= true;
	}
	
	/**
	 * Transforms the given source points, and stores the result in the given
	 * destination points
	 * @param src the given source {@link VerticesXY points}
	 * @param dest the given array of destination {@link Point points}
	 */
	public void transform( final VerticesXY src, final Point[] dest ) {
		// update the matrix
		updateMatrices();
		
		// perform the transformation
		xy2scs.transform( src, dest );
	}

	/**
	 * Transforms the given source point, and stores the result in the given
	 * destination point
	 * @param src the given source {@link PointXY point}
	 * @param dest the given destination {@link Point point}
	 */
	public void transform( final PointXY src, final Point dest ) {
		// update the matrix
		updateMatrices();
		
		// perform the transformation
		xy2scs.transform( src, dest );
	}
	
	/**
	 * Transforms the given space boundary to the Screen Coordinate System (SCS)
	 * @param boundary the given source {@link RectangleXY space boundary}
	 * @return the {@link Rectangle screen boundary}
	 */
	public Rectangle transform( final RectangleXY boundary ) {
		// update the matrix
		updateMatrices();
		
		// get the top-left and bottom-right screen points
		final PointXY mp1 = new PointXY( boundary.getX(), boundary.getY() );
		final PointXY mp2 = new PointXY( boundary.getX() + boundary.getWidth(), boundary.getY() + boundary.getHeight() );
		
		// create the destination model points
		final Point sp1 = new Point();
		final Point sp2 = new Point();
		transform( mp1, sp1 );
		transform( mp2, sp2 );
		
		// return the selection boundary
		return new Rectangle( sp1.x, sp1.y, sp2.x - sp1.x, sp2.y - sp1.y );
	}
	
	/**
	 * Transforms the given screen boundary
	 * @param boundary the given source {@link Rectangle screen boundary}
	 * @return the {@link RectangleXY model space boundary}
	 */
	public RectangleXY untransform( final Rectangle boundary ) {
		// update the matrix
		updateMatrices();
		
		// get the top-left and bottom-right screen points
		final Point sp1 = new Point( boundary.x, boundary.y );
		final Point sp2 = new Point( boundary.x + boundary.width, boundary.y + boundary.height );
		
		// create the destination model points
		final PointXY mp1 = untransform( sp1 );
		final PointXY mp2 = untransform( sp2 );
		
		// return the selection boundary
		return new RectangleXY( mp1.getX(), mp1.getY(), mp2.getX() - mp1.getX(), mp2.getY() - mp1.getY() );
	}
	
	/**
	 * Transforms the given screen coordinates to model coordinates
	 * @param src the given {@link Point screenPoint}
	 * @return a {@link PointXY point} in the model coordinate system
	 */
	public PointXY untransform( final Point src ) {
		// update the matrix
		updateMatrices();
		
		// perform the transformation
		final PointXY dest = new PointXY();
		scs2xy.transform( src, dest );
		return dest;
	}
	
	/**
	 * Updates the matrix once any changes have been made
	 * @return true, if the matrix indeed required an update
	 */
	protected boolean updateMatrices() {
		// if the matrix is dirty, update it.
		if( dirty ) {
			// compute the effective and the inverse effective scales
			final double effScale	= scale * unitScale;
			final double invScale 	= 1.0d / effScale;
			
			// translate the "MCS to SCS" matrix
			xy2scs.setIdentity();
			xy2scs.scale( effScale );
			xy2scs.translate( tx, ty );
			
			// translate the "SCS to MCS" inverse matrix
			scs2xy.setIdentity();
			scs2xy.translate( -tx, -ty );
			scs2xy.scale( invScale );
			
			// matrix is no longer dirty
			dirty = false;
			
			// notify the caller that the matrix was updated
			return true;
		}
		return false;
	}
	
}