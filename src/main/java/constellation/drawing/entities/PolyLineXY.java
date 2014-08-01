package constellation.drawing.entities;

import static java.lang.Double.MAX_VALUE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import constellation.ApplicationController;
import constellation.drawing.ComplexInternalRepresentation;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.VertexContainer;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Represents a digitized or free-hand line
 * @author lawrence.daniels@gmail.com
 */
public class PolyLineXY implements ComplexInternalRepresentation, VertexContainer {
	private final List<PointXY> vertices;
	private VerticesXY limits;
	private VerticesXY shape;
	
	/**
	 * Default constructor
	 */
	public PolyLineXY() {
		this.vertices = new LinkedList<PointXY>();
	}
	
	/** 
	 * Creates a spline using the given point array
	 * @param points the given array of {@link PointXY points}
	 */
	public PolyLineXY( final VerticesXY points ) {
		this.vertices 	= new LinkedList<PointXY>( Arrays.asList( EntityRepresentationUtil.fromLimits( points ) ) );
		this.limits		= points;
	}
	
	/** 
	 * Creates a spline using the given point collection
	 * @param pointSet the given collection of {@link PointXY points}
	 */
	public PolyLineXY( final Collection<PointXY> pointSet ) {
		this.vertices 	= new LinkedList<PointXY>( pointSet );
		this.limits		= EntityRepresentationUtil.toLimits( pointSet );
	}
	
	/** 
	 * Creates a spline using the given point array
	 * @param pointArray the given array of {@link PointXY points}
	 */
	public PolyLineXY( final PointXY[] pointArray ) {
		this.vertices 	= new LinkedList<PointXY>( Arrays.asList( pointArray ) );
		this.limits		= EntityRepresentationUtil.toLimits( pointArray );
	}
	
	/** 
	 * Appends a vertex array to the spline
	 * @param segment the given array of {@link PointXY vertices}
	 */
	public void append( final PointXY ... points ) {
		synchronized( vertices ) {
			vertices.addAll( Arrays.asList( points ) );
			resetCache();		
		}
	}

	/* (non-Javadoc)
	 * @see constellation.geometry.CurveXY#contains(constellation.geometry.PointXY)
	 */
	public boolean contains( final PointXY vertex ) {
		synchronized( vertices ) {
			for( final PointXY p : vertices ) {
				if( p.equals( vertex ) ) {
					return true;
				}
			}
			return false;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.InternalRepresentation#duplicate(double, double)
	 */
	public PolyLineXY duplicate( final double dx, final double dy ) {
		// get the source points
		final VerticesXY sp = getLimits();
		
		// create an array of destination points
		final VerticesXY dp = new VerticesXY( sp.length() );
		
		// copy and translate all points
		for( int n = 0; n < dp.length(); n++ ) {
			dp.add( sp.x[n] + dx, sp.y[n] + dy );
		}
		
		// return new spline
		return new PolyLineXY( dp );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getBounds()
	 */
	public RectangleXY getBounds(MatrixWCStoSCS matrix) {
		double minX = +MAX_VALUE;
		double minY = +MAX_VALUE;
		double maxX = -MAX_VALUE;
		double maxY = -MAX_VALUE;

		// check each vertex
		for( final PointXY p : vertices ) {
			if( minX > p.x ) { minX = p.x; }
			if( minY > p.y ) { minY = p.y; }
			if( maxX > p.x ) { maxX = p.x; }
			if( maxY > p.y ) { maxY = p.y; }
		}
		
		// return the bounds
		return new RectangleXY( minX, minY, maxX - minX, maxY - minY );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CurveXY#getLimits()
	 */
	public VerticesXY getLimits() {
		if( limits == null ) {
			synchronized( vertices ) {
				limits = EntityRepresentationUtil.toLimits( vertices );
			}
		}
		return limits;
	}
	
	/**
	 * Returns the vertex found at the given (x,y) coordinate
	 * @param x the given X-axis coordinate
	 * @param y the given Y-axis coordinate
	 * @return the {@link PointXY vertex}
	 */
	public PointXY getLimitVertexAt( final double x, final double y ) {
		for( final PointXY vertex : vertices ) {
			if( ( vertex.x == x ) && ( vertex.y == y ) ) {
				return vertex;
			}
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.math.geometric.GeometricElement#getMidPoint()
	 */
	public PointXY getMidPoint() {
		synchronized( vertices ) {
			if( !vertices.isEmpty() ) {
				final int index = vertices.size() / 2;
				return vertices.get( index );
			}
			return null;
		}
	}

	/** 
	 * Returns the number of segments contained within the spline
	 * @return the number of segments contained within the spline
	 */
	public int getSegmentCount() {
		synchronized( vertices ) {
			return vertices.size();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.POLYLINE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#getCategoryType()
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.IMAGE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		if( shape == null ) {
			shape = EntityRepresentationUtil.toLimits( vertices );
		}		
		return shape;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		// test each vertex
		for( final PointXY vertex : vertices ) {
			if( boundary.contains( vertex ) ) {
				return true;
			}
		}
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#length()
	 */
	public double length() {
		double length = 0;
		
		// get the vertices
		final VerticesXY limits = getLimits();
		
		// draw the lines
		final int count = limits.length() - 1;
		for( int n = 0; n < count; n++ ) {
			final int m = n+1;
			length += PointXY.getDistance( limits.x[n], limits.y[n], limits.x[m], limits.y[m] );
		}
		return length;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.EntityRepresentation#mirror(constellation.drawing.entities.LineXY)
	 */
	public PolyLineXY mirror( final LineXY plane ) {
		final List<PointXY> nvp = new ArrayList<PointXY>( vertices.size() ); 
		for( final PointXY p : vertices ) {
			nvp.add( p.mirror( plane ) );
		}
		return new PolyLineXY( nvp );
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

	/**
	 * Resets the spline; allowing its vertices (limits)
	 * to be re-computed.
	 */
	public void resetCache() {
		limits	= null;
		shape	= null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#toEquation()
	 */
	public String toEquation() {
		return "(spline)undefined";
	}
	
}