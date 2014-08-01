package constellation.thirdparty.formats.iges.entities.x100;

import static constellation.thirdparty.formats.iges.IGESConstants.IGES_COLORS;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * Represents an IGES Point
 * @author lawrence.daniels@gmail.com
 */
public class IGESPoint implements IGESEntity {
	private final double x, y, z;
	private final int colorNumber;
	
	/** 
	 * Creates a new point at the given (x,y,z) coordinates
	 * @param x the given x-coordinate
	 * @param y the given y-coordinate
	 * @param z the given z-coordinate
	 * @param colorNumber the given color number
	 */
	public IGESPoint( final double x, 
					  final double y, 
					  final double z, 
					  final int colorNumber ) {
		this.x 				= x;
		this.y 				= y;
		this.z 				= z;
		this.colorNumber	= colorNumber;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// create the point
		final ModelElement point = new CxModelElement( new PointXY( x, y ) );
		point.setColor( IGES_COLORS[ colorNumber ] );	
		return new ModelElement[] { point };
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "(%.2f,%.2f,%.2f)", x, y, z );
	}

}
