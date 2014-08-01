package constellation.thirdparty.formats.iges.processors;

import static constellation.thirdparty.formats.iges.processors.IGESEntityProcessor.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.processors.x100.IGES100_CircularArcProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES102_CompositeCurveProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES104_ConicArcProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES106_CopiousDataProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES108_PlaneProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES110_LineProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES112_ParametricSplineCurveProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES114_ParametricSplineSurfaceProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES116_PointProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES118_RuleSurfaceProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES120_SurfaceOfRevolutionProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES122_TabulatedCylinderProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES124_TransformationMatrixProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES126_RationalBSplineCurveProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES128_RationalBSplineSurfaceProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES130_OffsetCurveProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES132_ConnectPointProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES134_NodeProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES136_FiniteElementProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES140_OffsetSurfaceProcessor;
import constellation.thirdparty.formats.iges.processors.x100.IGES146_NodalResultsProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES202_AngularDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES204_CurveDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES206_DiameterDimension;
import constellation.thirdparty.formats.iges.processors.x200.IGES208_FlagNoteProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES210_GeneralLabelProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES212_GeneralNoteProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES214_LeaderArrowProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES216_LinearDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES218_OrdinateDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES220_PointDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x200.IGES222_RadiusDimensionProcessor;
import constellation.thirdparty.formats.iges.processors.x300.IGES308_SubFigureDefProcessor;
import constellation.thirdparty.formats.iges.processors.x300.IGES320_NetworkSubFigureDefProcessor;
import constellation.thirdparty.formats.iges.processors.x400.IGES404_DrawingEntityProcessor;
import constellation.thirdparty.formats.iges.processors.x400.IGES406_PropertyEntityProcessor;
import constellation.thirdparty.formats.iges.processors.x400.IGES408_SingularSubFigureDefProcessor;
import constellation.thirdparty.formats.iges.processors.x400.IGES410_ViewEntityProcessor;
import constellation.thirdparty.formats.iges.processors.x400.IGES412_RectangularArraySubFigureProcessor;

/**
 * IGES Entity Processor Factory
 * @author lawrence.daniels@gmail.com
 */
public class IGESEntityProcessorFactory {
	private static final Logger logger = Logger.getLogger( IGESEntityProcessorFactory.class );
	private static final Map<Integer,IGESEntityProcessor> processes = createProcessorMapping();
	
	/**
	 * Private constructor
	 */
	private IGESEntityProcessorFactory() {
		super();
	}
	
	/**
	 * Processes the given directory entry 
	 * @param cxModel the given {@link GeometricModel model}
	 * @param igesModel the given {@link IGESModel IGES model}
	 * @param entry the given {@link IGES_D directory entry}
	 * @param params the given {@link IGES_P parameters}
	 * @return true, if the entity was identified and processed
	 * @throws ModelFormatException 
	 */
	public static IGESEntity evaluate( final GeometricModel cxModel, 
									   final IGESModel igesModel, 
									   final IGES_D entry, 
									   final IGES_P params ) 
	throws ModelFormatException {
		// lookup the entity processor
		final IGESEntityProcessor processor = processes.get( entry.getEntityTypeNumber() );
		
		// if the processor wasn't found ...
		if( processor == null ) {
			logger.error( String.format( "Unrecognized directory entry '%s'", entry.getEntityTypeNumber() ) );
			return null;
		}
		
		// evaluate the entry using the parameters
		return processor.evaluate( cxModel, igesModel, entry, params );
	}
	
	/** 
	 * Returns the mapping of entity processors
	 * @return the mapping of {@link IGESEntityProcessor entity processors}
	 */
	private static Map<Integer,IGESEntityProcessor> createProcessorMapping() {
		final Map<Integer,IGESEntityProcessor> mapping = new HashMap<Integer, IGESEntityProcessor>();
		// IGES 100's
		mapping.put( IGES_100_CIRCULAR_ARC, 			new IGES100_CircularArcProcessor() );
		mapping.put( IGES_102_COMPOSITE_CURVE, 			new IGES102_CompositeCurveProcessor() );
		mapping.put( IGES_104_CONIC_ARC, 				new IGES104_ConicArcProcessor() );
		mapping.put( IGES_106_COPIOUS_DATA, 			new IGES106_CopiousDataProcessor() );
		mapping.put( IGES_108_PLANE, 					new IGES108_PlaneProcessor() );
		mapping.put( IGES_110_LINE, 					new IGES110_LineProcessor() );
		mapping.put( IGES_112_PARAMETRIC_SPLINE_CRV, 	new IGES112_ParametricSplineCurveProcessor() );
		mapping.put( IGES_114_PARAMETRIC_SPLINE_SURF, 	new IGES114_ParametricSplineSurfaceProcessor() );
		mapping.put( IGES_116_POINT, 					new IGES116_PointProcessor() );
		mapping.put( IGES_118_RULED_SURFACE, 			new IGES118_RuleSurfaceProcessor() );
		mapping.put( IGES_120_SURFACE_OF_REVOLUTION, 	new IGES120_SurfaceOfRevolutionProcessor() );
		mapping.put( IGES_122_TABULATED_CYLINDER, 		new IGES122_TabulatedCylinderProcessor() );
		mapping.put( IGES_124_TRANSFORMATION_MATRIX, 	new IGES124_TransformationMatrixProcessor() );
		mapping.put( IGES_126_RATION_B_SPLINE_CRV, 		new IGES126_RationalBSplineCurveProcessor() );
		mapping.put( IGES_128_RATION_B_SPLINE_SURF, 	new IGES128_RationalBSplineSurfaceProcessor() );
		mapping.put( IGES_130_OFFSET_CURVE, 			new IGES130_OffsetCurveProcessor() );
		mapping.put( IGES_132_CONNECT_POINT, 			new IGES132_ConnectPointProcessor() );
		mapping.put( IGES_134_NODE, 					new IGES134_NodeProcessor() );
		mapping.put( IGES_136_FINITE_ELEMENT, 			new IGES136_FiniteElementProcessor() );
		mapping.put( IGES_140_OFFSET_SURFACE, 			new IGES140_OffsetSurfaceProcessor() );
		mapping.put( IGES_146_NODAL_RESULTS, 			new IGES146_NodalResultsProcessor() );
		
		// IGES 200's
		mapping.put( IGES_202_ANGULAR_DIMENSION, 		new IGES202_AngularDimensionProcessor() );
		mapping.put( IGES_204_CURVE_DIMENSION, 			new IGES204_CurveDimensionProcessor() );
		mapping.put( IGES_206_DIAMETER_DIMENSION, 		new IGES206_DiameterDimension() );
		mapping.put( IGES_208_FLAG_NOTE, 				new IGES208_FlagNoteProcessor() );
		mapping.put( IGES_210_GENERAL_LABEL,			new IGES210_GeneralLabelProcessor() );
		mapping.put( IGES_212_GENERAL_NOTE, 			new IGES212_GeneralNoteProcessor() );
		mapping.put( IGES_214_LEADER_ARROW, 			new IGES214_LeaderArrowProcessor() );
		mapping.put( IGES_216_LINEAR_DIMENSION, 		new IGES216_LinearDimensionProcessor() );
		mapping.put( IGES_218_ORDINATE_DIMENSION, 		new IGES218_OrdinateDimensionProcessor() );
		mapping.put( IGES_220_POINT_DIMENSION, 			new IGES220_PointDimensionProcessor() );
		mapping.put( IGES_222_RADIUS_DIMENSION, 		new IGES222_RadiusDimensionProcessor() );
		
		// IGES 300's
		mapping.put( IGES_308_SUBFIGURE_DEF, 			new IGES308_SubFigureDefProcessor() );
		mapping.put( IGES_320_NETWORK_SUBFIGURE_DEF, 	new IGES320_NetworkSubFigureDefProcessor() );
		
		// IGES 400's
		mapping.put( IGES_404_DRAWING_ENTITY, 			new IGES404_DrawingEntityProcessor() );
		mapping.put( IGES_406_PROPERTY_ENTITY, 			new IGES406_PropertyEntityProcessor() );
		mapping.put( IGES_408_SINGULAR_SUBFIGURE_DEF, 	new IGES408_SingularSubFigureDefProcessor() );
		mapping.put( IGES_410_VIEW_ENTITY, 				new IGES410_ViewEntityProcessor() );
		mapping.put( IGES_412_RECTANGULAR_ARRAY, 		new IGES412_RectangularArraySubFigureProcessor() );
		return mapping;
	}
	
}
