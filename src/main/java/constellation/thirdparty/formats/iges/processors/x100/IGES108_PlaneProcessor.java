package constellation.thirdparty.formats.iges.processors.x100;

import org.apache.log4j.Logger;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESPlane;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/** 
 * <h2>Plane (Type 108)</h2>
 * <pre>
 * The plane entity can be used to represent an unbounded plane, as well as a bounded portion of a
 * plane.  In either of the above cases, the plane is defined within definition space by means of the
 * coefficients A, B, C, D, where at least one of A, B, and C is nonzero and
 * 
 *           A * XT + B * YT + C * ZT = D
 * 
 * for each point lying in the plane, and having definition space coordinates (XT; Y T; ZT ).
 * 
 * The definition space coordinates of a point, as well as a size parameter, can be specified in order
 * to assist in defining a system-dependent display symbol.  These values are parameter data entries
 * six through nine, respectively.  This information, together with the four coefficients defining the
 * plane, provides sufficient information relative to definition space in order to be able to position the
 * display symbol. (In the unbounded plane example of Figure 22, the curves and the crosshair together
 * constitute the display symbol.)  Setting the size parameter to zero indicates that a display symbol
 * is not intended.
 * 
 * The case of a bounded portion of a fixed plane requires the existence of a pointer to a closed curve
 * lying in the plane. This is parameter five. The only allowed coincident points for this curve are the
 * start point and the terminate point.  The case of an unbounded plane requires this pointer to be
 * zero.
 * 
 * Use of the Single Parent Associativity has been deprecated (see Appendix F).  This functionality
 * should be implemented using the Trimmed (Parametric) Surface Entity (Type 144) or the Bounded
 * Surface Entity (Type 143).
 * 
 * Field 15 of the Directory Entry accommodates a form number.  For this entity, the options are as
 * follows:
 *
 * ____________________________________________________________________
 * |__Form__|_______________________Meaning____________________________|
 * |  +1    | Bounded planar portion is considered positive.           |
 * |        | PTR must not be zero.                                    |
 * |        |                                                          |
 * |   0    | Plane is unbounded. PTR must be zero.                    |
 * |        |                                                          |
 * |  -1    | Bounded planar portion is considered negative (hole).    |
 * |        |  PTR must not be zero.                                   |
 * |________|__________________________________________________________|
 *                            
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            A            Real      Coefficients of Plane
 * 2            B            Real      Coefficients of Plane
 * 3            C            Real      Coefficients of Plane
 * 4            D            Real      Coefficients of Plane
 * 5            PTR          Pointer   Must be zero
 * 6            X            Real      XT coordinate of location point for display symbol
 * 7            Y            Real      YT coordinate of location point for display symbol
 * 8            Z            Real      ZT coordinate of location point for display symbol
 * 9            SIZE         Real      Size parameter for display symbol
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES108_PlaneProcessor implements IGESEntityProcessor {
	private final Logger logger = Logger.getLogger( getClass() );
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the parameters
		int n = 1;
		final double a 		= params.getDoubleParameter( n++ );
		final double b 		= params.getDoubleParameter( n++ );
		final double c 		= params.getDoubleParameter( n++ );
		final double d 		= params.getDoubleParameter( n++ );
		final int ptr		= params.getIntegerParameter( n++ );
		final double x		= params.getDoubleParameter( n++ );
		final double y		= params.getDoubleParameter( n++ );
		final double z		= params.getDoubleParameter( n++ );
		final double size	= params.getDoubleParameter( n++ );
		
		// the pointer value should be zero
		if( ( ptr != 0 ) && ( ptr != -1 ) && ( ptr != 1 ) ) {
			logger.warn( String.format( "The pointer value is invalid (ptr = %d)", ptr ) );
		}
		
		// return the plane
		return new IGESPlane( a, b, c, d, x, y, z, size );
	}

}
