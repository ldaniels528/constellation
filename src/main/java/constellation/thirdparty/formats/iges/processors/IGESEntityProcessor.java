package constellation.thirdparty.formats.iges.processors;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;

/**
 * Represents an IGES Entity
 * @author lawrence.daniels@gmail.com
 */
public interface IGESEntityProcessor {
	// 100-Series Entity Types
	int IGES_100_CIRCULAR_ARC			= 100;
	int IGES_102_COMPOSITE_CURVE		= 102;
	int IGES_104_CONIC_ARC				= 104;
	int IGES_106_COPIOUS_DATA			= 106; 
	int IGES_108_PLANE					= 108;
	int IGES_110_LINE					= 110;
	int IGES_112_PARAMETRIC_SPLINE_CRV 	= 112;
	int IGES_114_PARAMETRIC_SPLINE_SURF	= 114;
	int IGES_116_POINT 					= 116;
	int IGES_118_RULED_SURFACE			= 118;
	int IGES_120_SURFACE_OF_REVOLUTION	= 120;
	int IGES_122_TABULATED_CYLINDER		= 122;
	int IGES_124_TRANSFORMATION_MATRIX	= 124;
	int IGES_126_RATION_B_SPLINE_CRV 	= 126;
	int IGES_128_RATION_B_SPLINE_SURF	= 128;
	int IGES_130_OFFSET_CURVE			= 130;
	int IGES_132_CONNECT_POINT			= 132;
	int IGES_134_NODE					= 134;
	int IGES_136_FINITE_ELEMENT			= 136;
	int IGES_140_OFFSET_SURFACE			= 140;
	int IGES_146_NODAL_RESULTS			= 146;
	
	// 200-Series Entity Types
	int IGES_202_ANGULAR_DIMENSION		= 202;
	int IGES_204_CURVE_DIMENSION		= 204;
	int IGES_206_DIAMETER_DIMENSION		= 206;
	int IGES_208_FLAG_NOTE				= 208;
	int IGES_210_GENERAL_LABEL			= 210;
	int IGES_212_GENERAL_NOTE			= 212;
	int IGES_214_LEADER_ARROW			= 214;
	int IGES_216_LINEAR_DIMENSION		= 216;
	int IGES_218_ORDINATE_DIMENSION		= 218;
	int IGES_220_POINT_DIMENSION		= 220;
	int IGES_222_RADIUS_DIMENSION		= 222;
	
	// 300-Series Entity Types
	int IGES_308_SUBFIGURE_DEF			= 308;
	int IGES_320_NETWORK_SUBFIGURE_DEF	= 320;
	
	// 400-Series Entity Types
	int IGES_404_DRAWING_ENTITY			= 404;
	int IGES_406_PROPERTY_ENTITY		= 406;
	int IGES_408_SINGULAR_SUBFIGURE_DEF	= 408;
	int IGES_410_VIEW_ENTITY			= 410;
	int IGES_412_RECTANGULAR_ARRAY		= 412;
	
	/**
	 * Evaluates the given directory entry and parameters
	 * @param cxModel the given {@link GeometricModel model}
	 * @param igesModel the given {@link IGESModel IGES model}
	 * @param entry the given {@link IGES_D directory entry}
	 * @param params the given {@link IGES_P parameters}
	 * @return true, if the entity was successfully evaluated
	 * @throws ModelFormatException
	 */
	IGESEntity evaluate( GeometricModel cxModel, IGESModel igesModel, IGES_D entry, IGES_P params )
	throws ModelFormatException;

}
