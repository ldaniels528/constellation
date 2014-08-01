package constellation.thirdparty.formats.iges.entities.x100;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;

import java.util.Collection;
import java.util.LinkedList;

import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/** 
 * <h2>Copious Data Entity (Type 106)</h2>
 * <pre>
 * This entity stores data points in the form of pairs, triples, or sextuples. An interpretation flag value
 * signifies which of these forms is being used.  This value is one of the parameter data entries.  The
 * interpretation flag is abbreviated below by the letters IP.
 * 
 * Data points within definition space which lie within a single plane are specified in the form of XT,
 * YT coordinate pairs.  In this case, the common ZT value is also needed.  Data points arbitrarily
 * located within definition space are specified in the form of XT, YT, ZT coordinate triples.  Data
 * points within definition space which have an associated vector are specified in the form of sextuples;
 * the XT, YT, ZT coordinates are specified first, followed by the i, j, k coordinates of the vector
 * associated with the point. (Note that, for an associated vector, no special meaning is implicit.)
 * 
 * Field 15 of the Directory Entry accommodates a Form Number. For this entity, the options are as
 * follows:
 * 
 * ________________________________________________________________________________________________________
 * |__Form____|__________________________________________Meaning__________________________________________|
 * |    1     |Data points in the form of coordinate pairs. All data points lie in a plane ZT= constant.  |
 * |          |(IP=1)                                                                                     |
 * |          |                                                                                           |
 * |    2     |Data points in the form of coordinate triples (IP=2)                                       |
 * |          |                                                                                           |
 * |    3     |Data points in the form of sextuples (IP=3)                                                |
 * |          |                                                                                           |
 * |    11    |Data points in the form of coordinate pairs which represent the vertices of a planar,      |
 * |          |                                                                                           |
 * |          |piecewise linear curve (piecewise linear string is sometimes used). All data points lie in |
 * |          |a plane ZT=constant. (IP=1)                                                                |
 * |          |                                                                                           |
 * |    12    |Data points in the form of coordinate triples which represent the vertices of a piecewise  |
 * |          |linear curve (piecewise linear string is sometimes used) (IP=2)                            |
 * |          |                                                                                           |
 * |    13    |Data points in the form of sextuples.  The first triple of each sextuple represents the    |
 * |          |                                                                                           |
 * |          |vertices of a piecewise linear curve (piecewise linear string is sometimes used).  The     |
 * |          |second triple is an associated vector. (IP=3)                                              |
 * |          |                                                                                           |
 * |    20    |Centerline Entity through points (IP=1)                                                    |
 * |          |                                                                                           |
 * |    21    |Centerline Entity through circle centers (IP=1)                                            |
 * |          |                                                                                           |
 * |    31    |Section Entity Form 31 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    32    |Section Entity Form 32 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    33    |Section Entity Form 33 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    34    |Section Entity Form 34 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    35    |Section Entity Form 35 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    36    |Section Entity Form 36 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    37    |Section Entity Form 37 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    38    |Section Entity Form 38 (IP=1)                                                              |
 * |          |                                                                                           |
 * |    40    |Witness Line Entity (IP=1)                                                                 |
 * |          |                                                                                           |
 * |    63    |Simple Closed Planar Curve Entity (IP=1)                                                   |
 * |__________|___________________________________________________________________________________________|
 * 
 * The linear path is an ordered set of points in either 2- or 3-dimensional space. These points define
 * a series of linear segments along the consecutive points of the path. The segments may cross or be
 * coincident with each other. Paths may close, i.e., the first path point may be identical to the last.
 * 
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            IP           Integer   Interpretation Flag
 *                                       1 = x,y pairs, common z
 *                                       2 = x,y,z coordinates
 *                                       3 = x,y,z coordinates and i,j,k vectors
 * 2            N            Integer   Number of n-tuples
 * 
 * 
 * For IP=1 (x,y pairs, common z), i.e., for Form 1:
 * 
 * 
 * 3            ZT           Real      Common z displacement
 * 4            X1           Real      First data point abscissa
 * 5            Y1           Real      First data point ordinate
 * ..           .            .
 * .            ..           ..
 * 3+2*N        YN           Real      Last data point ordinate
 * 
 * 
 * For IP=2 (x,y,z triples), i.e., for Form 2:
 * 
 * 
 * 3            X1           Real      First data point x value
 * 4            Y1           Real      First data point y value
 * 5            Z1           Real      First data point z value
 * ..           .            .
 * .            ..           ..
 * 2+3*N        ZN           Real      Last data point z value
 * 
 * 
 * For IP=3 (x,y,z,i,j,k sextuples), i.e., for Form 3:
 * 
 * 
 * 3            X1           Real      First data point x value
 * 4            Y1           Real      First data point y value
 * 5            Z1           Real      First data point z value
 * 6            I1           Real      First data point i value
 * 7            J1           Real      First data point j value
 * 8            K1           Real      First data point k value
 * ..           .            .
 * .            ..           ..
 * 2+6*N        KN           Real      Last data point k value
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGESCopiousData implements IGESEntity {
	private final Collection<SetXYZ> dataSet;

	/**
	 * Default constructor
	 */
	public IGESCopiousData() {
		this.dataSet = new LinkedList<SetXYZ>();
	}
	
	/**
	 * Adds the data set to this collection
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public void addSet( final double x, final double y, final double z ) {
		dataSet.add( new SetXYZ( x, y, z ) );
	}
	
	/**
	 * Adds the data set to this collection
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @param i the i-coordinate
	 * @param j the j-coordinate
	 * @param k the k-coordinate
	 */
	public void addSet( final double x, 
						final double y, 
						final double z, 
						final double i, 
						final double j, 
						final double k ) {
		dataSet.add( new SetXYZ_IJK( x, y, z, i, j, k ) );
	}
	
	/**
	 * Returns the data sets associated to this
	 * copious data container
	 * @return the {@link SetXYZ data sets}
	 */
	public SetXYZ[] getDataSets() {
		return dataSet.toArray( new SetXYZ[ dataSet.size() ] );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		return null;
	}
	
	/** 
	 * Returns the data set as a collection of lines
	 * @return a {@link Collection collection} of {@link ModelElement lines}
	 */
	public Collection<ModelElement> toWitnessLines() {
		final Collection<ModelElement> lines = new LinkedList<ModelElement>();
		PointXY lastPt = null;

		// create the lines
		for( SetXYZ data : dataSet ) {
			if( lastPt == null ) {
				lastPt = EntityRepresentationUtil.getPoint( data.toGeometry() );
			}
			else {
				final PointXY currentPt = EntityRepresentationUtil.getPoint( data.toGeometry() );
				final ModelElement line = new CxModelElement( new LineXY( lastPt, currentPt ) );
				line.setColor( BLUE );
				lines.add( line );		
				lastPt = currentPt;
			}
		}
		
		return lines;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return dataSet.toString();
	}
	
	/**
	 * Represents an X,Y,Z data set
	 * @author lawrence.daniels@gmail.com
	 */
	public static class SetXYZ {
		public static final int DATA_SET_TYPE_1 = 0;
		public static final int DATA_SET_TYPE_2 = 1;
		protected final double x; 
		protected final double y; 
		protected final double z;
		protected final int type;
		
		/**
		 * Creates a new data set
		 * @param x the x-coordinate
		 * @param y the y-coordinate
		 * @param z the z-coordinate
		 */
		public SetXYZ( final double x, final double y, final double z ) {
			this( x, y, z, DATA_SET_TYPE_1 );
		}
		
		/**
		 * Creates a new data set
		 * @param x the x-coordinate
		 * @param y the y-coordinate
		 * @param z the z-coordinate
		 */
		protected SetXYZ( final double x, final double y, final double z, final int type ) {
			this.x 		= x;
			this.y 		= y;
			this.z 		= z;
			this.type	= type;
		}
		
		/** 
		 * Returns the drawing element represented by the data set
		 * @return the {@link ModelElement drawing element}
		 */
		public ModelElement toGeometry() {
			final ModelElement point = new CxModelElement( new PointXY( x, y ) );
			point.setColor( GREEN );
			return point;
		}
		
		/** 
		 * {@inheritDoc}
		 */
		public String toString() {
			return String.format( "[%.2f,%.2f,%.2f]", x, y, z );
		}
	}
	
	/**
	 * Represents an X,Y,Z and I, J, K data sets
	 * @author lawrence.daniels@gmail.com
	 */
	public static class SetXYZ_IJK extends SetXYZ {
		protected final double i; 
		protected final double j; 
		protected final double k;
		
		/**
		 * Creates a new data set
		 * @param x the x-coordinate
		 * @param y the y-coordinate
		 * @param z the z-coordinate
		 * @param i the i-coordinate
		 * @param j the j-coordinate
		 * @param k the k-coordinate
		 */
		public SetXYZ_IJK( final double x, 
					   	   final double y, 
					   	   final double z, 
					   	   final double i, 
					   	   final double j, 
					   	   final double k ) {
			super( x, y, z, DATA_SET_TYPE_2 );
			this.i = i;
			this.j = j;
			this.k = k;
		}
		
		/** 
		 * {@inheritDoc}
		 */
		public ModelElement toGeometry() {
			return new CxModelElement( new LineXY( x, y, i, j ) );
		}
		
		/** 
		 * {@inheritDoc}
		 */
		public String toString() {
			return String.format( "[%.2f,%.2f,%.2f-%.2f,%.2f,%.2f]", x, y, z, i, j, k );
		}
	}

}
