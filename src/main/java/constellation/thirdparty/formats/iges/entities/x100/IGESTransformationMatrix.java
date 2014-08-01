package constellation.thirdparty.formats.iges.entities.x100;

import java.util.Arrays;

import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.math.Matrix2D;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * Represents an IGES Transformation Matrix Entity (see line 6796)
 * @author lawrence.daniels@gmail.com
 */
public class IGESTransformationMatrix implements IGESEntity {
	private final Matrix2D matrix;
	private final Double[] vector;
	
	/**
	 * Default constructor
	 */
	public IGESTransformationMatrix( final double[] matrixData, final Double[] vector ) {
		this.matrix = new Matrix2D( matrixData );
		this.vector	= vector;
	}
	
	/**
	 * Transforms the given source point, and stores the result in the given
	 * destination point
	 * @param src the given source {@link PointXY point}
	 * @param dest the given destination {@link PointXY point}
	 */
	public void transform( final PointXY src, final PointXY dest ) {
		matrix.transform( src, dest );
	}

	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.format( "matrix=%s, vector=%s", matrix, Arrays.asList( vector ) );
	}

}
