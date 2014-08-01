package constellation.math;

import java.awt.Point;

import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.VerticesXY;

/**
 * Represents a 3x3 Matrix used to perform various transformations 
 * (translation, rotation, and scaling) on a two-dimensional plane.
 * @author lawrence.daniels@gmail.com
 */
public class Matrix2D implements Cloneable {
	private static final int ROWS 	= 3;
	private static final int COLS 	= 3;
	private final double[][] nm;

	////////////////////////////////////////////////////////////////
	//		Constructor(s)
	////////////////////////////////////////////////////////////////

	/**
	 * Default constructor
	 */
	public Matrix2D() {
		this.nm = new double[ROWS][COLS];
	}

	/**
	 * Creates an instance of this matrix class
	 * @param values the given array of values
	 */
	public Matrix2D( final double... values ) {
		this();
		set( values );
	}

	////////////////////////////////////////////////////////////////
	//		Overridden Method(s)
	////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * @see java.lang.Cloneable#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} 
		catch( final CloneNotSupportedException e ) {
			return new Matrix2D(
				nm[0][0], nm[0][1], nm[0][2], 
				nm[1][0], nm[1][1], nm[1][2],
				nm[2][0], nm[2][1], nm[2][2]
			);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format(
			"| %5.2f %5.2f %5.2f |\n| %5.2f %5.2f %5.2f |\n| %5.2f %5.2f %5.2f |",
			nm[0][0], nm[0][1], nm[0][2], 
			nm[1][0], nm[1][1], nm[1][2],
			nm[2][0], nm[2][1], nm[2][2]
		);
	}

	////////////////////////////////////////////////////////////////
	// 		Accessor & Mutator Method(s)
	////////////////////////////////////////////////////////////////

	public static void copy( final double[] destRow, final double[] srcRow ) {
		System.arraycopy( srcRow, 0, destRow, 0, COLS );
	}
	
	public double[] getRow( final int rowIndex ) {
		return nm[rowIndex];
	}

	public void setRow( final int rowIndex, final double[] row ) {
		nm[rowIndex] = row;
	}

	/**
	 * Sets the values within the matrix
	 * @param values the array of values
	 */
	public Matrix2D set( final double... values ) {
		int index = 0;
		for( final double value : values ) {
			nm[index / ROWS][index % COLS] = value;
			index++;
		}
		return this;
	}

	/**
	 * Sets the matrix in an identity state
	 */
	public Matrix2D setIdentity() {
		set(
			1, 0, 0, 
			0, 1, 0, 
			0, 0, 1
		);
		return this;
	}

	/**
	 * Scalar multiplication of the entire matrix 
	 * @param factor the given scalar factor
	 */
	public void multiply( final double factor ) {
		for( int row = 0; row < ROWS; row++ ) {
			for (int col = 0; col < COLS; col++) {
				nm[row][col] *= factor;
			}
		}
	}

	/**
	 * Multiplies the given row of the matrix by the given constant value 
	 * @param constant the given constant value
	 */
	public double[] multiply( final int rowIndex, final double constant ) {
		for( int col = 0; col < COLS; col++ ) {
			nm[rowIndex][col] *= constant;
		}
		return nm[rowIndex];
	}

	/**
	 * Multiplies the given row of the matrix by the given constant value
	 * @param constant the given constant value
	 */
	public static double[] multiply( final double[] row, final double constant ) {
		for( int col = 0; col < COLS; col++ ) {
			row[col] *= constant;
		}
		return row;
	}

	/**
	 * Multiples matrix A by matrix B
	 * @param a the given {@link Matrix2D matrix A}
	 * @param b the given {@link Matrix2D matrix B}
	 * @return the resultant {@link Matrix matrix}
	 */
	public static Matrix2D multiply( final Matrix2D a, final Matrix2D b ) {
		// create the multiplied matrix
		final double[] ab = new double[ROWS*COLS];
		int index = 0;
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				ab[index] += a.nm[row][col] * b.nm[row][col];
				System.err.printf("%.0f * %.0f %s ", a.nm[row][col], b.nm[row][col], (col < 2) ? "+" : "");
				System.err.printf("( a%d%d * b%d%d %s ) ", row + 1, col + 1, row + 1, col + 1, (col < 2) ? "+" : "");
			}
			System.err.println();
			index++;
		}

		// create the new matrix
		return new Matrix2D(ab);
	}

	/**
	 * Rolls the matrix up (n < 1) or down (n > 1)
	 * @param n the given row delta
	 */
	public void roll( final int n ) {
		// copy the source matrix
		final double[][] a = new double[ROWS][COLS];
		System.arraycopy(nm, 0, a, 0, 3);

		// compute the index of the first row
		// n < 1 : 1%3 = 1, 2%3 = 2, 3%3 = 0
		// n > 1 : 2%3 = 2, 3%3 = 0, 4%3 = 1
		final int i = (n < 1) ? (n * -1) : (n + 1);

		// move the data
		nm[0] = a[(i + 0) % 3];
		nm[1] = a[(i + 1) % 3];
		nm[2] = a[(i + 2) % 3];
	}

	/**
	 * Rotates the given set of points and stores the result in the given
	 * destination points
	 * @param src the given source points
	 * @param dest the given destination points
	 */
	public void rotate( final PointXY[] src, final PointXY[] dest ) {
		double x,y;
		for( int n = 0; n < src.length; n++ ) {
			// compute the (x,y) coordinate
			x = src[n].x;
			y = src[n].y;
			
			// perform the rotation
			dest[n].x = x * nm[0][0] + y * nm[0][1];
			dest[n].y = x * nm[1][0] + y * nm[1][1];
		}
	}

	/**
	 * Scales the matrix by the given x and y factors respectively
	 * @param factor the given scale factor
	 */
	public void scale( final double factor ) {
		for( int col = 0; col < COLS; col++ ) {
			nm[0][col] *= factor;
			nm[1][col] *= factor;
		}
	}

	/**
	 * Adds the given matrix to the existing matrix
	 * @param matrix the given {@link Matrix2D matrix}
	 */
	public void sum( final Matrix2D matrix ) {
		for( int row = 0; row < ROWS; row++ ) {
			for( int col = 0; col < COLS; col++) {
				nm[row][col] += matrix.nm[row][col];
			}
		}
	}

	/**
	 * Sums the given source rows and places the result values in the given
	 * destination row
	 * @param dest the given destination row
	 * @param srcA the given source row A
	 * @param srcB the given source row B
	 */
	public static void sum( final double[] dest, 
					 		final double[] srcA,
					 		final double[] srcB) {
		for( int col = 0; col < COLS; col++ ) {
			dest[col] = srcA[col] + srcB[col];
		}
	}

	/**
	 * Translates the matrix by given x and y distances respectively
	 * @param dx the given x-coordinate distance
	 * @param dy the given y-coordinate distance
	 */
	public void translate( final double dx, final double dy ) {
		nm[0][2] += dx;
		nm[1][2] += dy;
	}

	////////////////////////////////////////////////////////////////
	//		Transformation Method(s)
	////////////////////////////////////////////////////////////////

	/**
	 * Transforms the given source points, and stores the result in the given
	 * destination points
	 * @param srcPt the given array of source {@link PointXY points}
	 * @param destPt the given array of destination {@link Point points}
	 */
	public void transform( final VerticesXY src, final Point[] dest) {
		for( int n = 0; n < src.length(); n++ ) {
			dest[n].x = (int)( src.x[n] * nm[0][0] + src.y[n] * nm[0][1] + nm[0][2] );
			dest[n].y = (int)( src.x[n] * nm[1][0] + src.y[n] * nm[1][1] + nm[1][2] );
		}
	}
	
	/**
	 * Transforms the given source point, and stores the result in the given
	 * destination point
	 * @param src the given source {@link PointXY point}
	 * @param dest the given destination {@link PointXY point}
	 */
	public void transform( final PointXY src, final PointXY dest ) {
		// cache the source coordinates
		final double x = src.x;
		final double y = src.y;

		// transform the coordinates
		final double tx = x * nm[0][0] + y * nm[0][1] + nm[0][2];
		final double ty = x * nm[1][0] + y * nm[1][1] + nm[1][2];

		// place the coordinates into the destination
		dest.setLocation( tx, ty );
	}

	/**
	 * Transforms the given source point, and stores the result in the given
	 * destination point
	 * @param src the given source {@link PointXY point}
	 * @param dest the given destination {@link Point point}
	 */
	public void transform( final PointXY src, final Point dest ) {
		// get the model coordinates
		final double x = src.x;
		final double y = src.y;

		// get the screen coordinates
		final int sx = (int)( x * nm[0][0] + y * nm[0][1] + nm[0][2] );
		final int sy = (int)( x * nm[1][0] + y * nm[1][1] + nm[1][2] );

		// populate the screen point
		dest.setLocation( sx, sy );
	}

	/**
	 * Transforms the given screen point to a model point
	 * @param src the given source {@link Point point}
	 * @param dest the given destination {@link PointXY point}
	 */
	public void transform( final Point src, final PointXY dest ) {
		// get the screen coordinates
		final double sx = src.x;
		final double sy = src.y;

		// get the screen coordinates
		final double x = ( sx * nm[0][0] + sy * nm[0][1] + nm[0][2] );
		final double y = ( sx * nm[1][0] + sy * nm[1][1] + nm[1][2] );

		// populate the screen point
		dest.setLocation( x, y );
	}

	/**
	 * Simultaneously transforms and translates the given source points, and
	 * stores the result in the given destination points
	 * @param src the given source points
	 * @param dest the given destination points
	 * @param transPt the given translation point
	 */
	public void transformAndTranslate( final PointXY[] src,
									   final PointXY[] dest, 
									   final PointXY transPt ) {
		double x,y;
		for( int n = 0; n < src.length; n++ ) {
			// compute the (x,y) coordinate
			x = src[n].x + transPt.x;
			y = src[n].y + transPt.y;
			
			// perform the transformation
			dest[n].x = x * nm[0][0] + y * nm[0][1] + nm[0][2];
			dest[n].y = x * nm[1][0] + y * nm[1][1] + nm[1][2];
		}
	}

}
