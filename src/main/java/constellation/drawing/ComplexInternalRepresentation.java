package constellation.drawing;

import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.VerticesXY;
import constellation.math.MatrixWCStoSCS;

/**
 * Represents a two-dimensional complex internal representation
 * @author lawrence.daniels@gmail.com
 */
public interface ComplexInternalRepresentation extends EntityRepresentation {
	
	/**
	 * Returns the control points that form the boundary of the representation
	 * @return the array of {@link VerticesXY vertices}
	 */
	VerticesXY getLimits();
	
	/** 
	 * Returns the midpoint of the representation
	 * @return the {@link PointXY midpoint} of the representation
	 */
	PointXY getMidPoint();
	
	/**
	 * Returns the control points (vertices) of the representation
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return the {@link VerticesXY control points}
	 */
	VerticesXY getVertices( MatrixWCStoSCS matrix );

	/**
	 * Returns the length of the representation
	 * @return the length of the representation
	 */
	double length();

}
