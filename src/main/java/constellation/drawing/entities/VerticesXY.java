package constellation.drawing.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import constellation.ApplicationController;
import constellation.drawing.RenderableElement;
import constellation.drawing.ScratchPad;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * A class that encapsulates and array of two-dimensional vertices (points).
 * @author lawrence.daniels@gmail.com
 */
public class VerticesXY implements RenderableElement, Cloneable {
	// public fields
	public double[] x;
	public double[] y;
	
	// internal fields
	private int capacity;
	private int position;
	private int length;

	////////////////////////////////////////////////////////////
	//      Constructors
	////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty array of two-dimensional points with size "n"
	 * @param capacity the given capacity of the data structure
	 */
	public VerticesXY( final int capacity ) {
		this.x        	= new double[capacity];
		this.y        	= new double[capacity];
		this.capacity	= capacity;
		this.length  	= 0;
	}

	/**
	 * Constructs an array of two-dimensional points with the supplied vectors.
	 * @param capacity the given capacity of the data structure
	 * @param xy the array of X- and Y-values (e.g. '{ x1,y2, x2,y2, ... }')
	 */
	public VerticesXY( final double ... xy ) {
		this.capacity	= xy.length / 2;
		this.x     		= new double[capacity];
		this.y     		= new double[capacity];
		this.length		= capacity;
		this.position	= length;
			
		for( int n = 0; n < length; n++ ) {
			final int m = n*2;
			this.x[n] = xy[m];
			this.y[n] = xy[m+1];
		}
	}
	
	/**
	 * Constructs an array of two-dimensional points with the supplied vectors.
	 * @param capacity the given capacity of the data structure
	 * @param xy the array of X- and Y-values (e.g. '{ x1,y2, x2,y2, ... }')
	 */
	public VerticesXY( final double[] x, final double[] y ) {
		this.capacity	= x.length;
		this.length		= capacity;
		this.x     		= x;
		this.y     		= y;
	}

	////////////////////////////////////////////////////////////
	//      Service Methods
	////////////////////////////////////////////////////////////
	
	/**
	 * Adds the given point to the vertex set
	 * @param px the given x-coordinate of the point
	 * @param py the given y-coordinate of the point
	 */
	public void add( final double px, final double py ) {
		// expand the x & y arrays
		if( position + 1 >= x.length ) {
			// create new larger arrays
			final double[] xa = new double[ length + 5 ];
			final double[] ya = new double[ length + 5 ];
			
			// copy the existing data
			System.arraycopy( x, 0, xa, 0, length );
			System.arraycopy( y, 0, ya, 0, length );
			
			// point to the new arrays
			x = xa;
			y = ya;
		}
		
		// add the data
		x[position] = px;
		y[position] = py;
		position++;
		
		if( position > length ) {
			length = position;
		}
	}
	 
	/** 
	 * Adds the set of vertices to this set
	 * @param vertices the given {@link VerticesXY vertices}
	 */
	public void addAll( final VerticesXY vertices ) {
		// allocate the total capacity needed
		this.allocateCapacity( length + vertices.length );
		
		// mass copy the points
		System.arraycopy( vertices.x, 0, x, length, vertices.length );
		System.arraycopy( vertices.y, 0, y, length, vertices.length );
		
		// adjust the length
		length += vertices.length;
	}

	/**
	 * Insures the capacity of screen points for projecting points
	 * @param capacity the given maximum capacity for projecting points
	 */
	public void allocateCapacity( final int capacity ) {
		this.x			= new double[capacity];
		this.y			= new double[capacity];
		this.length 	= capacity;
	}
	
	public void close() {
		add( x[0], y[0] );
	}
	
	/**
	 * Returns the point at the given index
	 * @param index the given index
	 * @return the {@link PointXY point}
	 */
	public PointXY indexOf( final int index ) {
		return new PointXY( x[index], y[index] );
	}
	
	/**
	 * Returns the number of vertex points
	 * @return the number of vertex points
	 */
	public int length() {
		return length;
	}
	
	/**
	 * Sets the length of vertex set
	 * @param length the number of vertices in the set
	 */
	public void setLength( final int length ) {
		this.length = length;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch( final CloneNotSupportedException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Explodes the vertex set into an array of points
	 * @return an array of {@link PointXY points}
	 */
	public PointXY[] explode() {
		final PointXY[] points = new PointXY[ length ];
		for( int n = 0; n < length; n++ ) {
			points[n] = new PointXY( x[n], y[n] );
		}
		return points;
	}
	
	/** 
	 * Returns the current position 
	 * @return the current position 
	 */
	public int position() {
		return position;
	}
	
	/** 
	 * Sets the current position 
	 * @param position the current position 
	 */
	public void position( final int position ) {
		this.position = position;
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
						final Color color ) {
		// this instance is the limits
		final VerticesXY limits = this;
		
		// get the projecting points
		final Point[] projectedPoints = ScratchPad.getProjectionPoints( limits );

		// project the points
		matrix.transform( limits, projectedPoints );
		
		for( int n = 0; n < limits.length(); n++ ) {
			// cache the points of the line
			final Point p = projectedPoints[n];
			
			// draw the point (if visible)
			if( clipper.contains( p ) ) {
				PointXY.renderVertex( g, p.x, p.y, color );
			}
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder( length * 15 );
		for( int n = 0; n < length; n++ ) {
			if( n > 0 ) { sb.append( ", "); }
			sb.append( String.format( "(%3.2f,%3.2f)", x[n], y[n] ) );
		}
		return sb.toString();
	}

}