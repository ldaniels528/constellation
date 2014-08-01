package constellation.thirdparty.formats.iges.entities.x100;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * IGES Plane Entity (Type 108)
 * @author lawrence.daniels@gmail.com
 */
public class IGESPlane implements IGESEntity {
	private final double a;
	private final double b;
	private final double c;
	private final double d;
	private final double x;
	private final double y;
	private final double z;
	private final double size;
	
	/** 
	 * Creates a new plane
	 * @param a the A coefficient of the plane
	 * @param b the B coefficient of the plane
	 * @param c the C coefficient of the plane
	 * @param d the D coefficient of the plane
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @param size the size of the plane
	 */
	public IGESPlane( final double a, 
					  final double b, 
					  final double c, 
					  final double d, 
					  final double x,
					  final double y, 
					  final double z, 
					  final double size ) {
		this.a 		= a;
		this.b 		= b;
		this.c 		= c;
		this.d 		= d;
		this.x 		= x;
		this.y 		= y;
		this.z 		= z;
		this.size 	= size;
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
	@Override
	public String toString() {
		return String.format( "a = %.2f, b = %.2f, c = %.2f, d = %.2f, x = %.2f, y = %.2f, z = %.2f, size = %3.2f", 
								a, b, c, d, x, y, z, size );
	}

}
