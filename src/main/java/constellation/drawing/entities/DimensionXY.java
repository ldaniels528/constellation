package constellation.drawing.entities;

import static constellation.math.CxMathUtil.convertRadiansToDegrees;
import static java.lang.Math.PI;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.RenderableElement;
import constellation.math.CxIntersectionUtil;
import constellation.math.MatrixWCStoSCS;
import constellation.math.CxIntersectionUtil.Intersection;
import constellation.model.GeometricModel;

/**
 * Represents an Angular Dimension
 * @author lawrence.daniels@gmail.com
 */
public class DimensionXY implements EntityRepresentation {
	private static final Logger logger = Logger.getLogger( DimensionXY.class );
	private final Collection<EntityRepresentation> elements;
	private final DimensionType dimensionType;
	private RectangleXY bounds;
	
	/** 
	 * Creates a new angular dimension
	 * @param elements the given collection of {@link EntityRepresentation elements}
	 * @param dimensionType the given {@link DimensionType dimension type}
	 */
	private DimensionXY( final Collection<EntityRepresentation> elements, 
						 final DimensionType dimensionType ) {
		this.elements 		= elements;
		this.dimensionType	= dimensionType;
	}
	
	/** 
	 * Creates a new linear dimension
	 * @param lineA the given host {@link LineXY line A}
	 * @param lineB the given host {@link LineXY line B}
	 * @return a new {@link DimensionXY dimension}
	 */
	public static DimensionXY createLinearDimension( final LineXY lineA, final LineXY lineB ) {
		// create the entity container
		final Collection<EntityRepresentation> elements = new LinkedList<EntityRepresentation>();
		
		/////////////////////////////////////////////////////
		// 1. determine the lengths of the extension lines
		
		// get the start and end points of Line A
		//final PointXY pA1 = lineA.p1;
		final PointXY pA2 = lineA.p2;
		
		// get the start and end points of Line B
		//final PointXY pB1 = lineB.p1;
		final PointXY pB2 = PointXY.getClosestPoint( pA2, lineB.p1, lineB.p2 );
		
		// determine how long to make each line
		double lengthA = 50d;
		double lengthB = 50d;
		final PointXY ppA2 = PointXY.getProjectedPoint( pA2, lineB );
		final PointXY ppB2 = PointXY.getProjectedPoint( pB2, lineA );
		
		// if line A is shorter, extend the length
		if( !lineA.contains( ppB2 ) ) {
			lengthA += PointXY.getDistance( pA2, ppB2 );
			logger.error( format( "Line A is shorter; A=%3.2f, B=%3.2f, length=%3.2f: pA2 = %s, ppB2 = %s", 
					lineA.length(), lineB.length(), lengthA, pA2, ppB2 ) );
		}
		
		// if line B is shorter, extend the length
		if( !lineB.contains( ppA2 ) ) {
			lengthB += PointXY.getDistance( pB2, ppA2 );
			logger.error( format( "Line B is shorter; A=%3.2f, B=%3.2f, length = %3.2f: pB2 = %s, ppA2 = %s", 
					lineA.length(), lineB.length(), lengthB, pB2, ppA2 ) );
		}
		
		/////////////////////////////////////////////////////
		// 2. create the extension line points

		// determine the extension line overhang
		final int overHang = 8;
		
		// create new points for extension lines A 
		final PointXY pEA1 = PointXY.getPointAlongLine( lineA, pA2, overHang );
		final PointXY pEA2 = PointXY.getPointAlongLine( lineA, pA2, lengthA );
		final PointXY pEA3 = PointXY.getPointAlongLine( lineA, pA2, lengthA + overHang );

		// create new points for extension lines B 
		final PointXY pEB1 = PointXY.getPointAlongLine( lineB, pB2, overHang );
		final PointXY pEB2 = PointXY.getPointAlongLine( lineB, pB2, lengthB );
		final PointXY pEB3 = PointXY.getPointAlongLine( lineB, pB2, lengthB + overHang );
				
		/////////////////////////////////////////////////////
		// 3. create extension lines
		
		// create new extension lines A and B 
		final LineXY extLA = new LineXY( pEA1, pEA3 );
		final LineXY extLB = new LineXY( pEB1, pEB3 );
		elements.addAll( asList( extLA, extLB ) );
		
		/////////////////////////////////////////////////////
		// 4. create dimension line (normal to extension lines)
		
		// create dimension line (normal to extension lines)
		final double distance	= PointXY.getDistance( pEA2, pEB2 );
		final LineXY dimLine	= LineXY.createNormalLine( extLA, pEA2, distance );
		elements.add( dimLine );
		
		// add the text note
		elements.add( new TextNoteXY( dimLine.getMidPoint(), format( "%3.2f", distance ) ) );

		
		// return the dimension
		return new DimensionXY( elements, DimensionType.LINEAR );
	}
	
	/** 
	 * Creates a new angular dimension
	 * @param lineA the given host {@link LineXY line A}
	 * @param lineB the given host {@link LineXY line B}
	 * @return a new {@link DimensionXY dimension}
	 */
	public static DimensionXY createAngularDimension( final LineXY lineA, final LineXY lineB ) {
		// create the entity container
		final Collection<EntityRepresentation> elements = new LinkedList<EntityRepresentation>();
						
		// compute the intersection between lines A and B
		final PointXY ipAB = CxIntersectionUtil.getIntersectionPoint( lineA, lineB );
		if( ipAB == null ) {
			logger.error( "No intersection found between line A and line B" );
			return null;
		}
		
		/////////////////////////////////////////////////////
		// 1. determine the lengths of the extension lines
	
		// get the start and end points of Line A
		//final PointXY pA1 = lineA.p1;
		final PointXY pA2 = lineA.p2;
		
		// get the start and end points of Line B
		//final PointXY pB1 = lineB.p1;
		final PointXY pB2 = PointXY.getClosestPoint( pA2, lineB.p1, lineB.p2 );
		
		// project the end point of each line onto the other line
		final PointXY ppAB = PointXY.getProjectedPoint( pA2, lineB );
		final PointXY ppBA = PointXY.getProjectedPoint( pB2, lineA );
			
		// determine the longest line
		final LineXY lineS; 
		final LineXY lineL;
		final double diff;

		// if line A is shorter, extend the length
		if( !lineA.contains( ppBA ) ) {
			diff = PointXY.getDistance( pA2, ppBA );
			logger.info( format( "Line A is shorter; diff = %3.2f", diff ) );
			
			// capture the short and long lines
			lineS	= lineA;
			lineL	= lineB;
		}
		
		// line B is either shorter, or the same length
		else {
			diff = PointXY.getDistance( pB2, ppAB );
			logger.info( format( "Line B: diff = %3.2f", diff ) );
			
			// capture the short and long lines
			lineS	= lineB;
			lineL	= lineA;
		}
		
		/////////////////////////////////////////////////////
		// 2. create the extension lines
		
		// get the start and end points of the lines
		final PointXY pS2 = lineS.getEndPoint();
		final PointXY pL2 = lineL.getEndPoint();
		
		// add extension line L (the longer line)
		final PointXY dpL1 = PointXY.getPointAlongLine( lineL, pL2, 5 );
		final PointXY dpL2 = PointXY.getPointAlongLine( lineL, pL2, 15d );
		elements.add( new LineXY( dpL1, dpL2 ) );
		
		/////////////////////////////////////////////////////
		// 3. create dimension arc 
		
		// compute the midpoint of extension line L
		final PointXY mpL = PointXY.getMidPoint( dpL1, dpL2 );
		
		// compute the radius
		final double radius = PointXY.getDistance( ipAB, mpL );
		
		// create the temporary circle
		final CircleXY circle = new CircleXY( ipAB, radius );
		
		// get the intersection point between the circle and line A
		final Intersection ix = CxIntersectionUtil.getIntersectionPoints( lineS, circle );
		final PointXY apS = getClosestPoint( ix, pS2 );
		
		// compute the angles
		double angleA = PointXY.getAngle( ipAB, pS2 );
		double angleB = PointXY.getAngle( ipAB, pL2 );
		
		// swap the angles if Angle A is greater
		if( angleA > angleB ) {
			final double tmp = angleA;
			angleA = angleB;
			angleB = tmp;
		}				
logger.info( format( "radius = %3.2f, angleA = %3.2f, angleB = %3.2f", radius, angleA, angleB ) );	
		
		// get the distance from the end point of line S to the intersection point of the circle
		final double distS = PointXY.getDistance( pS2, apS );
		
		// add extension line S (the shorter line)
		final PointXY dpS1 = PointXY.getPointAlongLine( lineS, pS2, 5 );
		final PointXY dpS2 = PointXY.getPointAlongLine( lineS, pS2, 5 + distS );
		elements.add( new LineXY( dpS1, dpS2 ) );
		
		// create the arc
		final ArcXY arc = ArcXY.createArc( circle, angleA, angleB );
		elements.add( arc );
				
		// create a line that bisects lines A and B
		final LineXY lineBS = LineXY.createBisectLine( lineA, lineB );
		
		// intersect the bisect line with the circle
		final Intersection ixBS = CxIntersectionUtil.getIntersectionPoints( arc, lineBS );
		
		// use the intersection point as the text note anchor
		final PointXY pBS = getClosestPoint( ixBS, lineA.getEndPoint() );
		
		// compute the angle (convert to degrees)
		final double degrees = Math.abs( convertRadiansToDegrees( lineA.getAcuteAngle( lineB ) ) );
		
		// add the angle text note
		elements.add( new TextNoteXY( pBS, format( "%3.2f deg", degrees ) ) );
	
		// return the angular dimension		
		return new DimensionXY( elements, DimensionType.ANGULAR );
	}
	
	/** 
	 * Creates a new radius dimension
	 * @param circle the given host {@link CircleXY circle}
	 * @return a new {@link DimensionXY dimension}
	 */
	public static DimensionXY createRadiusDimension( final CircleXY circle ) {
		// create the entity container
		final Collection<EntityRepresentation> elements = new LinkedList<EntityRepresentation>();
		
		// get the radius
		final double radius = circle.getRadius();
		
		// get the midpoint of the circle
		final PointXY mp = circle.getMidPoint();
		elements.add( mp );
		
		// create a line angled 45 degrees
		final LineXY lineA = LineXY.createAngledLine( LineXY.createHorizontalLine( mp, mp.getOffset( .1, 0 ) ), mp, PI/4d, radius * 1.25d );
		final LineXY lineB = new LineXY( lineA.p2, lineA.p2.getOffset( 50, 0 ) );
		final TextNoteXY noteR = new TextNoteXY( lineB.p2.getOffset( 6, 2 ), format( "%3.2f", radius ) );
		elements.addAll( asList( lineA, lineB, noteR ) );
		
		// return the dimension
		return new DimensionXY( elements, DimensionType.RADIUS );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityRepresentation duplicate( final double dx, final double dy) {
		return new DimensionXY( elements, dimensionType );
	}

	/**
	 * {@inheritDoc}
	 */
	public RectangleXY getBounds( final MatrixWCStoSCS matrix ) {
		// calculate the boundary once
		if( bounds == null ) {
			bounds = EntityRepresentationUtil.getBounds( elements, matrix );
		}
		
		// return the boundary
		return bounds;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityCategoryTypes getCategoryType() {
		return EntityCategoryTypes.DIMENSION;
	}
	
	/** 
	 * Returns the dimension type
	 * @return the {@link DimensionType dimension type}
	 */
	public DimensionType getDimensionType() {
		return dimensionType;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityTypes getType() {
		return EntityTypes.DIMENSION;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean intersects( final RectangleXY boundary, final MatrixWCStoSCS matrix ) {
		for( final EntityRepresentation element : elements ) {
			if( element.intersects( boundary, matrix ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityRepresentation mirror( final LineXY plane ) {
		final Collection<EntityRepresentation> mirrored = new ArrayList<EntityRepresentation>( elements.size() );
		for( final EntityRepresentation element : elements ) {
			mirrored.add( element.mirror( plane ) );
		}
		return new DimensionXY( mirrored, dimensionType );
	}

	/**
	 * {@inheritDoc}
	 */
	public void render( final ApplicationController controller,
						final GeometricModel model,
						final MatrixWCStoSCS matrix, 
						final Rectangle clipper, 
						final Graphics2D g, 
						final Color color ) {
		// render the elements
		for( final RenderableElement element : elements ) {
			element.render( controller, model, matrix, clipper, g, color );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() 
	throws CloneNotSupportedException {
		return super.clone();
	}

	/** 
	 * Returns the intersection point that is closest to the given point
	 * @param ix2 the given {@link Intersection intersection}
	 * @param p the given {@link PointXY point}
	 * @return the closest {@link PointXY point}
	 */
	private static PointXY getClosestPoint( final Intersection ix2, final PointXY p ) {
		final PointXY[] points = ix2.getPointArray();
		
		// is there only 1 point?
		if( points.length == 1 ) {
			return points[0];
		}
		
		// determine the closest point
		else {
			return PointXY.getClosestPoint( p, points ); 
		}
	}

	/** 
	 * Represents the enumeration of dimension types
	 * @author lawrence.daniels@gmail.com
	 */
	public static enum DimensionType {
		ANGULAR, LINEAR, RADIUS
	}
	
}
