package constellation.thirdparty.formats.iges.processors.x400;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x400.IGESViewEntity;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/**
 * <h2>View Entity (Type 410)</h2>
 * <pre>
 * The View Entity defines a framework for specifying a viewing orientation of an object in three
 * dimensional model space (X,Y,Z). The framework is also used to support the projection of all or
 * part of model space onto a view plane. One type of projection, an orthographic parallel projection,
 * can be specified. A second type of projection supporting perspective views is defined in Appendix G
 * (see Section G.44).
 * 
 * Orthographic Parallel Projection. An orthographic parallel projection onto a view plane of an
 * object in model space is formed by passing rays normal to the view plane through each point of the
 * object and finding the intersection with the view plane as shown in Figure 88.
 * 
 * View Coordinate System. The view plane can be described by introducing a right-handed view
 * coordinate system, (XV, YV, ZV) into model space. The view plane is the XV, YV plane, i.e., the
 * plane ZV=0.  The view direction is along the positive ZV axis toward the view plane, i.e., in the
 * direction of the vector (0,0,-1).  The positive YV axis points in the "up" direction in the resulting
 * view. The point (0,0,0) in the view coordinate system (see Figure 89) is called the view origin. Thus,
 * a complete viewing orientation is specified by a view coordinate system.
 * 
 * View Coordinates Obtained from Model Coordinates. View coordinates are obtained from
 * model coordinates through translation and rotation.  There are several ways that systems specify
 * the data required to transfer from model to view coordinates. However, in each case, the data can
 * be recorded using Form 0 of the Transformation Matrix Entity such that the model coordinates are
 * taken as input and the view coordinates are produced as output, as follows, where R denotes the
 * rotation matrix and T the translation vector (see Section 4.19):
 * 
 *                          2       3   2                   3 2     3   2     3
 *                            XV           R11   R12   R13       X         T1
 *                          4  Y V  5 = 4  R21   R22   R23  5 4  Y  5 + 4  T2 5
 * 
 *                             ZV          R31   R32   R33       Z         T3
 * 
 * 
 * 
 * In this situation, R is called the view matrix.
 * 
 * The View Entity specifies the view matrix and the translation vector by use of a pointer to a Trans-
 * formation Matrix Entity in DE Field 7.  In the special case when the view matrix is the identity
 * matrix and there is zero translation, a zero value in DE Field 7 may be used.
 * 
 * Example  1:  (View coordinates obtained from model coordinates by a translation and then a
 * rotation.)
 * 
 * The system defines a viewing orientation by specifying a view origin (XO, YO, ZO) in model space
 * and a rotation matrix so that:
 * 
 *                          2       3    2            3 0 2     3   2       31
 *                             XV            3 x 3           X         XO
 *                          4  Y V  5  = 4  Rotation  5 @ 4  Y  5 - 4  Y O  5A
 * 
 *                             ZV            Matrix          Z         ZO
 * 
 * 
 * or:
 * 
 *                    2       3    2            3 2     3   2            3 2       3
 *                       XV            3 x 3         X          3 x 3         XO
 *                    4  Y V  5  = 4  Rotation  5 4  Y  5 - 4  Rotation  5 4  Y O  5 :
 * 
 *                       ZV            Matrix        Z          Matrix        ZO
 *                    
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            VNO         Integer   View number
 * 2            SCALE       Real      Scale factor (Default = 1.0)
 * 3            XVMINP      Pointer   Pointer to left side of view volume (XVMIN plane), or zero
 * 4            YVMAXP      Pointer   Pointer to top of view volume (YVMAX plane) or zero
 * 5            XVMAXP      Pointer   Pointer to right side of view volume (XVMAX plane), or zero
 * 6            YVMINP      Pointer   Pointer to bottom of view volume (YVMIN plane), or zero
 * 7            ZVMINP      Pointer   Pointer to back of view volume (ZVMIN plane), or zero
 * 8            ZVMAXP      Pointer   Pointer to front of view volume (ZVMAX plane), or zero   
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES410_ViewEntityProcessor extends IGESAbstractEntityProcessor {

	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		logger.info( String.format( "View Entity: params = %s", params.toString() ) );
		// TODO Auto-generated method stub
		return new IGESViewEntity();
	}

}
