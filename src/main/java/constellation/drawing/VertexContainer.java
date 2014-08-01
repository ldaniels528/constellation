package constellation.drawing;

import constellation.drawing.entities.PointXY;

/**
 * Represents a drawing element that is comprised of vertex points
 * @author lawrence.daniels@gmail.com
 */
public interface VertexContainer extends EntityRepresentation {

	/** 
	 * Appends a vertex array to the spline
	 * @param segment the given array of {@link PointXY vertices}
	 */
	void append( PointXY ... points );
	
}
