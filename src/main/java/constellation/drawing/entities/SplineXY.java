package constellation.drawing.entities;

import static java.lang.Double.MAX_VALUE;
import static java.util.Arrays.asList;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import constellation.ApplicationController;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.VertexContainer;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * The mathematical representation of a Bezier spline
 * @see http://en.wikipedia.org/wiki/Bezier_spline
 * @see http://www.math.ucla.edu/~baker/java/hoefer/Spline.htm
 * @see http://www.math.ucla.edu/~baker/149.1.02w/handouts/dd_splines.pdf
 * @author lawrence.daniels@gmail.com
 */
public class SplineXY extends CurveXY implements VertexContainer {
	public static final int MAX_POINTS = 6;
	private final List<PointXY> controlPoints;
	private VerticesXY limits;
	private VerticesXY shape;
	
	/**
	 * Default constructor
	 */
	public SplineXY() {
		this.controlPoints = new LinkedList<PointXY>();
	}
	
	/** 
	 * Creates a spline using the given point array
	 * @param points the given array of {@link PointXY points}
	 */
	public SplineXY( final VerticesXY points ) {
		this.controlPoints 	= new LinkedList<PointXY>( asList( EntityRepresentationUtil.fromLimits( points ) ) );
		this.limits		= points;
	}
	
	/** 
	 * Creates a spline using the given point collection
	 * @param pointSet the given collection of {@link PointXY points}
	 */
	public SplineXY( final Collection<PointXY> pointSet ) {
		this.controlPoints 	= new LinkedList<PointXY>( pointSet );
		this.limits		= EntityRepresentationUtil.toLimits( pointSet );
	}
	
	/** 
	 * Creates a spline using the given point array
	 * @param pointArray the given array of {@link PointXY points}
	 */
	public SplineXY( final PointXY[] pointArray ) {
		this.controlPoints 	= new LinkedList<PointXY>( asList( pointArray ) );
		this.limits		= EntityRepresentationUtil.toLimits( pointArray );
	}
	
	/** 
	 * Creates a new rectangle
	 * @param startPoint the starting point of the rectangle
	 * @param endPoint the end point of the rectangle
	 * @return a new {@link SplineXY rectangle}
	 */
	public static SplineXY createRectangle( final PointXY start, final PointXY end ) {
		final double xa = start.getX();
		final double ya = start.getY();
		final double xb = end.getX();
		final double yb = end.getY();
		return new SplineXY( 
				new PointXY[] {
					new PointXY( xa, ya ),
					new PointXY( xb, ya ),
					new PointXY( xb, yb ),
					new PointXY( xa, yb ),
					new PointXY( xa, ya )
				} 
			);
	}
	
	/** 
	 * Appends a vertex array to the spline
	 * @param segment the given array of {@link PointXY vertices}
	 */
	public void append( final PointXY ... points ) {
		synchronized( controlPoints ) {
			if( controlPoints.size() < MAX_POINTS ) {
				controlPoints.addAll( asList( points ) );
				resetCache();		
			}
		}
	}

	/* (non-Javadoc)
	 * @see constellation.geometry.CurveXY#contains(constellation.geometry.PointXY)
	 */
	public boolean contains( final PointXY vertex ) {
		synchronized( controlPoints ) {
			for( final PointXY p : controlPoints ) {
				if( p.equals( vertex ) ) {
					return true;
				}
			}
			return false;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.CurveXY#duplicate(double, double)
	 */
	public CurveXY duplicate( double dx, double dy ) {
		// get the source points
		final VerticesXY sp = getLimits();
		
		// create an array of destination points
		final VerticesXY dp = new VerticesXY( sp.length() );
		
		// copy and translate all points
		for( int n = 0; n < dp.length(); n++ ) {
			dp.add( sp.x[n] + dx, sp.y[n] + dy );
		}
		
		// return new spline
		return new SplineXY( dp );
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
		for( final PointXY p : controlPoints ) {
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
			synchronized( controlPoints ) {
				limits = EntityRepresentationUtil.toLimits( controlPoints );
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
		for( final PointXY vertex : controlPoints ) {
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
		synchronized( controlPoints ) {
			if( !controlPoints.isEmpty() ) {
				final int index = controlPoints.size() / 2;
				return controlPoints.get( index );
			}
			return null;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getOffsetCurve(constellation.drawing.entities.representations.PointXY)
	 */
	public CurveXY getParallelCurve( final PointXY offset ) {
		synchronized( controlPoints ) {
			// create a new set of points
			final PointXY[] points = new PointXY[ controlPoints.size() ];
			
			int n = 0;
			for( final PointXY vertex : controlPoints ) {
				// compute the new width and height
				final double dx	= vertex.x - offset.x;
				final double dy	= vertex.y - offset.y;
				
				// TODO each point needs to be computed along a normal line leading away from the spline
				
				// create the new point
				points[n++] = new PointXY( vertex.x + dx, vertex.y + dy );
			}
			
			// return the new spline
			return new SplineXY( points );
		}
	}

	/** 
	 * Returns the number of segments contained within the spline
	 * @return the number of segments contained within the spline
	 */
	public int getSegmentCount() {
		synchronized( controlPoints ) {
			return controlPoints.size();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.CurveXY#getType()
	 */
	public EntityTypes getType() {
		return EntityTypes.SPLINE;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.ComplexGeometricRepresentation#getVertices(constellation.math.MatrixWCStoSCS)
	 */
	public VerticesXY getVertices( final MatrixWCStoSCS matrix ) {
		if( shape == null ) {
			// capture the count of vertices
			final int numpoints = controlPoints.size();
			
			// get the vertex points as an array
			final PointXY[] anchorPts = controlPoints.toArray( new PointXY[ numpoints ] );
			
			// compute the control points
			final double[] cpx = new double[ numpoints ];
			final double[] cpy = new double[ numpoints ];
			interpolateControlPoints( anchorPts, cpx, cpy );
			
			// compute the spline's vertices
			shape = generateSplineVertices( anchorPts, cpx, cpy );
		}
		return shape;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.drawing.entities.representations.GeometricRepresentation#intersects(java.awt.geom.RectangleXY, constellation.math.MatrixWCStoSCS)
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		// get the vertices
		final VerticesXY p = getLimits();
		
		// get the half count
		final int count = p.length() / 2;
	
		// test each segment
		int n = 0;
		while( n < count ) {
			// compute the index of the next point
			final int m = n+1;
			
			// cache the coordinates
			final double x1 = p.x[n];
			final double y1 = p.y[n];
			final double x2 = p.x[m];
			final double y2 = p.y[m];
			
			// move to the next set
			n += 2; 
			
			// determine whether there is an intersection
			return boundary.contains( x1, y1 ) || 
					boundary.contains( x2, y2 ) || 
					boundary.intersectsLine( x1, y1, x2, y2 );
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
	public SplineXY mirror( final LineXY plane ) {
		final List<PointXY> nvp = new ArrayList<PointXY>( controlPoints.size() ); 
		for( final PointXY p : controlPoints ) {
			nvp.add( p.mirror( plane ) );
		}
		return new SplineXY( nvp );
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

	private double[][] generateDerivativeMatrix( final int size, final double[][] basedata ) {
		// create the grid
		final double[][] matrix = new double[size][size];
		
		// compute the last element index
		final int nth	= size - 1;
		final int half	= size / 2;
		
		for( int i = 0; i < size; i++ ) {
			// ignore the 1st and last indices
			if( i != 0 && i != nth ) {
				// initialize the current row
				matrix[i] = new double[size];
				
				// populate the data
				for( int j = 0; j < size; j++ ) {
					final int n = ( j < half ) ? j : nth - j;
					final int m = ( j < half ) ? i : nth - i;
					
					matrix[i][j] = basedata[n][m];
				}
			}
			else {
				matrix[i] = new double[0];
			}
		}
		
		return matrix;
	}

	/** 
	 * Generates the spline's vertices
	 * @param anchorPts the given spline anchor points
	 * @param cpx the given array of X-axis control points
	 * @param cpy the given array of Y-axis control points
	 * @return the generated {@link VerticesXY vertices}
	 */
	private VerticesXY generateSplineVertices( final PointXY[] anchorPts,
											   final double[] cpx,
											   final double[] cpy ) {
		// create the spline vertices
		final Collection<PointXY> curve = new LinkedList<PointXY>();
		
		// draw n Bezier curves using Bernstein Polynomials
		final double k = 0.05;
		double x,y;
		
		// attach the first point
		curve.add( anchorPts[0] );
		
		// compute all others
		for( int i = 1; i < anchorPts.length; i++ ) {
			for( double t = i - 1; t <= i; t += k ) {
				// compute the X-coordinate 
				x =  ( anchorPts[i - 1].x + (t - (i - 1) )
						* (-3 * anchorPts[i - 1].x + 3 * (.6667 * cpx[i - 1] + .3333 * cpx[i] ) + 
							( t - ( i - 1 ) ) * (3 * anchorPts[i - 1].x - 
										6 * ( .6667 * cpx[i - 1] + .3333 * cpx[i] ) + 
										3 * ( .3333 * cpx[i - 1] + .6667 * cpx[i]) + 
										( -anchorPts[i - 1].x + 
										  3 * (.6667 * cpx[i - 1] + .3333 * cpx[i] ) - 
										  3 * (.3333 * cpx[i - 1] + .6667 * cpx[i] ) + anchorPts[i].x )
										* (t - (i - 1) ) ) ) );
				
				// compute the Y-coordinate
				y =  (anchorPts[i - 1].y + (t - (i - 1)) * (-3 * anchorPts[i - 1].y + 3
								* (.6667 * cpy[i - 1] + .3333 * cpy[i]) + (t - (i - 1))
								* (3 * anchorPts[i - 1].y
										- 6
										* (.6667 * cpy[i - 1] + .3333 * cpy[i])
										+ 3
										* (.3333 * cpy[i - 1] + .6667 * cpy[i]) + (-anchorPts[i - 1].y
										+ 3
										* (.6667 * cpy[i - 1] + .3333 * cpy[i])
										- 3
										* (.3333 * cpy[i - 1] + .6667 * cpy[i]) + anchorPts[i].y)
										* (t - (i - 1)))));
				
				// add the point
				curve.add( new PointXY( x, y ) );
			}
		}
		
		return EntityRepresentationUtil.toLimits( curve );
	}

	/** 
	 * Computes the control points of the spline,
	 * and populates them into the given control point arrays.
	 * @param anchorPts the given anchor points
	 * @param cpx the given array of X-axis control points
	 * @param cpy the given array of Y-axis control points
	 */
	private void interpolateControlPoints( final PointXY[] anchorPts, 
									   	   final double[] cpx, 
									   	   final double[] cpy ) {
			// compute the 1st point - derivatives equal zero
		cpx[0] = anchorPts[0].x;
		cpy[0] = anchorPts[0].y;
		
		// compute the Nth point - derivatives equal zero
		final int nth = anchorPts.length - 1;
		cpx[nth] = anchorPts[nth].x;
		cpy[nth] = anchorPts[nth].y;
		
		final double[][] basedata = {
				{ 0, -0.26794, 0.07177, -0.019139, 0.004785, 0 },
				{ 0,  1.6077, -0.43062,  0.11483, -0.028708, 0 },
				{ 0, -0.43062, 1.7225,  -0.45933,  0.114835, 0 }
		};
		
		// derivative values for 1st thru Nth points
		/*
		final double[][] matrix = {
				{ },
				{ -0.26794,   1.6077,  -0.43062,   0.114835, -0.028708,  0.004785 },
				{  0.07177,  -0.43062,  1.7225,   -0.45933,  0.11483,  -0.019139 },
				{ -0.019139,  0.11483, -0.45933,   1.7225,  -0.43062,   0.07177 },
				{  0.004785, -0.028708, 0.114835, -0.43062,  1.6077,   -0.26794 },
				{ }
		};*/
		final double[][] matrix = generateDerivativeMatrix( anchorPts.length, basedata );
		
		// interpolate all points between 0 and Nth (non-inclusive)
		for( int i = 1; i < nth; i++ ) {
			
			// populate the X- and Y-axis control point values for the current index
			for( int j = 0; j < anchorPts.length; j++ ) {
				cpx[i] += matrix[i][j] * anchorPts[j].x;
				cpy[i] += matrix[i][j] * anchorPts[j].y;
			}
		}
	}

	/** 
	 * Computes the control points of the spline,
	 * and populates them into the given control point arrays.
	 * @param anchorPts the given anchor points
	 * @param cpx the given array of X-axis control points
	 * @param cpy the given array of Y-axis control points
	 */
	@Deprecated
	protected void interpolateControlPoints2( final PointXY[] anchorPts, 
									   		  final double[] cpx, 
									   		  final double[] cpy ) {
		// compute the index of the last point
		final int nth = anchorPts.length - 1;
		
		// set #1
		cpx[0] = anchorPts[0].x;
		cpy[0] = anchorPts[0].y;
		
		// set #6
		cpx[ nth ] = anchorPts[ nth ].x;
		cpy[ nth ] = anchorPts[ nth ].y;
		
		// set #2
		cpx[1] = ( 1.6077 * anchorPts[1].x - 
					.26794 * anchorPts[0].x - 
					.43062 *  anchorPts[2].x + 
					.11483 * anchorPts[3].x - 
					.028708 *  anchorPts[4].x + 
					.004785 *  anchorPts[5].x );
		
		cpy[1] = ( 1.6077 * anchorPts[1].y - 
					.26794 *  anchorPts[0].y - 
					.43062 *  anchorPts[2].y + 
					.11483 *  anchorPts[3].y - 
					.028708 *  anchorPts[4].y + 
					.004785 *  anchorPts[5].y );
	
		// set #3
		cpx[2] = ( -.43062 * anchorPts[1].x + 
					.07177 * anchorPts[0].x + 
					1.7225 *  anchorPts[2].x - 
					.45933 * anchorPts[3].x + 
					.11483 *  anchorPts[4].x - 
					.019139 *  anchorPts[5].x ); //  points[3].x
		
		cpy[2] = ( -.43062 *  anchorPts[1].y + 
					.07177 * anchorPts[0].y + 
					1.7225 *  anchorPts[2].y - 
					.45933 * anchorPts[3].y + 
					.11483 *  anchorPts[4].y - 
					.019139 *  anchorPts[5].y ); //  points[3].y
	
		// set #4
		cpx[3] = ( .11483 * anchorPts[1].x - 
					.019139 * anchorPts[0].x - 
					.45933 *  anchorPts[2].x + 
					1.7225 * anchorPts[3].x - 
					.43062 *  anchorPts[4].x + 
					.07177 *  anchorPts[5].x );
		
		cpy[3] = ( .11483 * anchorPts[1].y - 
					.019139 *  anchorPts[0].y - 
					.45933 *  anchorPts[2].y + 
					1.7225 *  anchorPts[3].y - 
					.43062 *  anchorPts[4].y + 
					.07177 *  anchorPts[5].y);
	
		// set #5
		cpx[4] = ( -.028708 * anchorPts[1].x + 
					.004785 * anchorPts[0].x + 
					.114835 * anchorPts[2].x - 
					.43062 * anchorPts[3].x + 
					1.6077 * anchorPts[4].x - 
					.26794 * anchorPts[5].x );
		
		cpy[4] = ( -.028708 * anchorPts[1].y + 
					.004785 * anchorPts[0].y + 
					.114835 * anchorPts[2].y - 
					.43062  * anchorPts[3].y + 
					1.6077 *  anchorPts[4].y - 
					.26794 * anchorPts[5].y );
	}
	
}
