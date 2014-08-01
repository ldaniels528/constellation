package constellation.thirdparty.formats.iges.entities.x100;

import static constellation.thirdparty.formats.iges.IGESConstants.IGES_COLORS;
import constellation.drawing.LinePatterns;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.UserDefinedConicXY;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/** 
 * <h2>Conic ArcXY Entity (Type 104)</h2>
 * <p>A conic arc is a bounded connected portion of a parent conic curve which consists of more than one
 * point. The parent conic curve is either an ellipse, a parabola, or a hyperbola. The definition space
 * coordinate system is always chosen so that the conic arc lies in a plane either coincident with or
 * parallel to the XT , YT plane. Within such a plane, a conic is defined by the six coefficients in the
 * following equation.</p>
 * 
 * <div>A * XT^2 + B * XT * YT + C * YT^2 + D * XT + E * YT + F = 0</div>
 * 
 * <p>Each coefficient is a real number.  The definitions of ellipse, parabola, and hyperbola in terms of
 * these six coefficients are given below.</p>
 * 
 * <p>A conic arc determines unique arc end-points. A conic arc is defined within definition space by the six
 * coefficients above and the two end-points. By considering the conic arc end-points to be enumerated
 * and listed in an ordered manner, start point followed by terminate point, a direction with respect
 * to definition space can be associated with the arc.  In order for the desired elliptical arc to be
 * distinguished from its complementary elliptical arc, the direction of the desired elliptical arc must
 * be counterclockwise. In the case of a parabola or hyperbola, the parameters given in the parameter
 * data section uniquely define a portion of the parabola or a portion of a branch of the hyperbola;
 * therefore, the concept of a counterclockwise direction is not applied.  (Refer to Section 3.2.4 for
 * information concerning use of the term "counterclockwise.")</p>
 * 
 * <p>The direction of the conic arc with respect to model space is determined by the original direction of
 * the arc within definition space, in conjunction with the action of the transformation matrix on the
 * arc.</p>
 * 
 * <p>The definitions of the terms ellipse, parabola, and hyperbola are given in terms of the quantities Q1,
 * Q2, and Q3. These quantities are:</p>
 * <pre> 
 *                                                      2                    3
 *                                                         A     B=2   D=2
 * 
 *                          Q1    =    determinant of   64 B=2   C     E=2   75
 *
 *                                                         D=2   E=2   F
 * 
 *                                                         "                  #
 *                                                         A     B=2
 *                          Q2    =    determinant of
 *                                                         B=2   C
 * 
 *                          Q3    =    A + C
 * </pre>
 * A parent conic curve is:
 * 
 *                   An ellipse  if Q2 > 0 and Q1 * Q3 < 0.
 *                   A hyperbola if Q2 < 0 and Q1 6= 0.
 *                   A parabola  if Q2 = 0 and Q1 6= 0.
 * 
 * <p>An example of each type of conic arc is shown in Figure 18.</p>
 * 
 * <p>Those entities which can be represented as various degenerate forms of a conic equation (Point and
 * Line) must not be put into the Entity Type 104, more appropriate entity types exist for these forms.</p>
 * 
 * <p>Because of the numerical sensitivity of the implicit form of the conic description, a receiving system
 * not using that form as its internal representation for conics need not be expected to correctly process
 * conics in this form unless they are put into a standard position in definition space.  A Conic ArcXY
 * Entity is said to be in a standard position in definition space provided each of its axes is parallel
 * to either the XT axis or Y T axis and provided it is centered about the ZT axis.  For a parabola,
 * use the vertex as the origin. The conic is moved from this position in definition space to the desired
 * position in space with a transformation matrix (Entity Type 124).</p>
 * 
 * <p>The form number is regarded as purely informational by such a postprocessor.</p>
 * 
 * <p>Further details may be found in Appendix C.</p>
 * <pre>
 * Parameter Data
 * ----------------------------------------------------------------------------
 * Index       Name        Type      Description 
 * ----------------------------------------------------------------------------
 * 1           A           Real      Conic Coefficient
 * 2           B           Real      Conic Coefficient
 * 3           C           Real      Conic Coefficient
 * 4           D           Real      Conic Coefficient
 * 5           E           Real      Conic Coefficient
 * 6           F           Real      Conic Coefficient
 * 7           ZT          Real      ZT Coordinate of plane of definition
 * 8           X1          Real      Start Point Abscissa
 * 9           Y1          Real      Start Point Ordinate
 * 10          X2          Real      Terminate Point Abscissa
 * 11          Y2          Real      Terminate Point Ordinate
 * __________________________________________________________________________________________
 * | Form    |                              Meaning                                         |
 * |_________|______________________________________________________________________________|
 * |    0    |Form of parent conic curve must be determined from the general equation       |
 * |    1    |Parent conic curve is an ellipse (See Figure 18)                              |
 * |    2    |Parent conic curve is a hyperbola (See Figure 18)                             |
 * |____3____|Parent_conic_curve_is_a_parabola_(See_Figure_18)______________________________|
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGESConicArc implements IGESEntity {
	// define the conic form types
	public static final int FORM_EQUATION 	= 0;
	public static final int FORM_ELLIPSE	= 1;
	public static final int FORM_HYPERBOLA	= 2;
	public static final int FORM_PARABOLA	= 3;
	
	// internal fields
	private final IGESTransformationMatrix matrix;
	private final IGES_D entry;
	private final double a;
	private final double b;
	private final double c;
	private final double d;
	private final double e;
	private final double f;
	private final double zt;
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	/** 
	 * Creates a new conic arc
	 * @param entry the given {@link IGES_D directory entry}
	 * @param matrix the given {@link IGESTransformationMatrix transformation matrix}
	 * @param a the conic coefficient 1 of 6
	 * @param b the conic coefficient 2 of 6
	 * @param c the conic coefficient 3 of 6
	 * @param d the conic coefficient 4 of 6
	 * @param e the conic coefficient 5 of 6
	 * @param f the conic coefficient 6 of 6
	 * @param zt the given Z-axis plane
	 * @param x1 the given x-coordinate of point #1
	 * @param y1 the given y-coordinate of point #1
	 * @param x2 the given x-coordinate of point #2
	 * @param y2 the given y-coordinate of point #2
	 */
	public IGESConicArc( final IGES_D entry, 
						 final IGESTransformationMatrix matrix, 
						 final double a, 
						 final double b, 
						 final double c, 
						 final double d, 
						 final double e, 
						 final double f, 
						 final double zt, 
						 final double x1, 
						 final double y1, 
						 final double x2, 
						 final double y2 ) {
		this.entry 	= entry;
		this.matrix	= matrix;
		this.a 		= a;
		this.b 		= b;
		this.c 		= c;
		this.d 		= d;
		this.e 		= e;
		this.f 		= f;
		this.zt 	= zt;
		this.x1 	= x1;
		this.y1 	= y1;
		this.x2 	= x2;
		this.y2 	= y2;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public ModelElement[] toDrawingElements() {
		// generate the appropriate geometry
		final ModelElement drawingElement;
		switch( entry.getFormNumber() ) {
			case FORM_EQUATION: 	drawingElement = generateConicEquation(); break;
			case FORM_ELLIPSE: 		drawingElement = generateConicEllipse(); break;
			case FORM_HYPERBOLA: 	drawingElement = generateConicHyperbola(); break;
			case FORM_PARABOLA: 	drawingElement = generateConicParabola(); break;
			default:
				throw new IllegalArgumentException( String.format( "Form type %d is not supported", entry.getFormNumber() ) );
		}
		
		// set element's attributes
		drawingElement.setColor( IGES_COLORS[ entry.getColorNumber() ] );
		drawingElement.setPattern( LinePatterns.values()[ entry.getLineFontPattern() ] );
		
		// use the matrix to position the geometry
		// TODO add this functionality
		//matrix.hashCode();
		
		// return the geometry
		return new ModelElement[] { drawingElement };
	}
	
	/**
	 * Generates a curve via via the given parameters and 
	 * a function or equation
	 * @return a {@link ModelElement curve}
	 */
	private ModelElement generateConicEquation() {
		return new CxModelElement( new UserDefinedConicXY( x1, y1, x2, y2, a, b, c, d, e, f ) );
	}
	
	/**
	 * Generates an ellipse via the given parameters
	 * @return a {@link ModelElement curve}
	 */
	private ModelElement generateConicEllipse() {
		return new CxModelElement( new EllipseXY( x1, y1, x2, y2 ) );
	}
	
	/**
	 * Generates a hyperbola via the given parameters
	 * @return a {@link ModelElement curve}
	 */
	private ModelElement generateConicHyperbola() {
		throw new IllegalArgumentException( "Hyperbolic curves have not yet been implemented" );
	}
	
	/**
	 * Generates a parabola via the given parameters
	 * @return a {@link ModelElement curve}
	 */
	private ModelElement generateConicParabola() {
		throw new IllegalArgumentException( "Parabolic curves have not yet been implemented" );
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format( "A = %3.2f, B = %3.2f, C = %3.2f, D = %3.2f, E = %3.2f, F = %3.2f, " +
							  "ZT = %3.2f, X1 = %3.2f, Y1 = %3.2f, X2 = %3.2f, Y2 = %3.2f", 
							  	a, b, c, d, e, f, zt, x1, y1, x2, y2 );
	}
	
}
