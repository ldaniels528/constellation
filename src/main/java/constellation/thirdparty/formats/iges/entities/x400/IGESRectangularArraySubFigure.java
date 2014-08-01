package constellation.thirdparty.formats.iges.entities.x400;

import constellation.drawing.elements.ModelElement;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * <h2>Rectangular Array Subfigure Instance Entity (Type 412)</h2>
 * <p>The Rectangular Array Subfigure Instance Entity produces copies of an object called the base entity,
 * arranging them in equally spaced rows and columns.  The following types of entities are valid for
 * use as a base entity: Group Associativity, Point, Line, Circular ArcXY, Conic ArcXY, Parametric Spline
 * Curve, Rational B-spline Curve, any annotation entity, Rectangular Array Instance, Circular Array
 * Instance, or Subfigure Definition. The number of columns and rows of the rectangular array together
 * with their respective horizontal and vertical displacements are given.  Also, the coordinates of the
 * lower left hand corner for the entire array are given. This is where the first entity in the reproduction
 * process is placed and is called position Number 1.  The successive positions are counted vertically
 * up the first column, then vertically up the second column to the right, and so on.</p>
 * 
 * <p>The array of instance locations for the base entity is rotated about the line through the point (X,Y),
 * parallel to the ZT-axis.  The angle of rotation is specified in radians counterclockwise from the
 * positive XT-axis. The instances of the base entity are not rotated from their original orientation.</p>
 * 
 * <p>A DO-DON'T flag controls which portion of the array is displayed. If the DO value is chosen, half
 * or fewer of the elements of the rectangular array are to be defined.  If the DON'T value is chosen,
 * half or more of the elements of the rectangular array are to be defined.</p>
 * 
 * <pre>
 * ----------------------------------------------------------------------------
 * Index       Name        Type      Description 
 * ----------------------------------------------------------------------------
 * 1            DE           Pointer   Pointer to the DE of the base entity
 * 2            S            Real      Scale factor (default = 1.0)
 * 3            X            Real      Coordinates of point to be used as lower left corner of array
 * 4            Y            Real
 * 5            Z            Real
 * 6            NC           Integer   Number of columns
 * 7            NR           Integer   Number of rows
 * 8            DX           Real      Horizontal distance between columns
 * 9            DY           Real      Vertical distance between rows
 * 10           AX           Real      Rotation angle in radians
 * 11           LC           Integer   DO-DON'T list count (LC=0 indicates all to be displayed.)
 * 12           DDF          Integer   DO-DON'T flag:
 *                                       0 = DO
 *                                       1 = DON'T
 * 13           N1           Integer   Number of first position to be processed (DO), or not to be
 *                                     processed (DON'T)
 * ..           .            .
 * .            ..           ..
 * 12+LC        NLC          Integer   Number of last position
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGESRectangularArraySubFigure implements IGESEntity {
	private final int[] positions;	
	private final int pointer;
	private final double scale;
	private final double x;
	private final double y;
	private final double z;
	private final int columns;
	private final int rows;
	private final double dx;
	private final double dy;
	private final double angle;

	/**
	 * Default constructor
	 */
	public IGESRectangularArraySubFigure( final int pointer,
										  final double scale,
										  final double x,
										  final double y,
										  final double z,
										  final int columns,
										  final int rows,
										  final double dx,
										  final double dy,
										  final double angle,
										  final int[] positions ) {
		this.pointer	= pointer;
		this.scale		= scale;
		this.x			= x;
		this.y			= y;
		this.z			= z;
		this.columns	= columns;
		this.rows		= rows;
		this.dx			= dx;
		this.dy			= dy;
		this.angle		= angle;
		this.positions	= positions;
		
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
		return "<No Rectangular Array>";
	}

}
