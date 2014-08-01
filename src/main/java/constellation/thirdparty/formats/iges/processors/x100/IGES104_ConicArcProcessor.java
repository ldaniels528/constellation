package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESConicArc;
import constellation.thirdparty.formats.iges.entities.x100.IGESTransformationMatrix;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/** 
 * This class is responsible for parsing the parameters necessary to
 * create a conic arc entity.
 * @author lawrence.daniels@gmail.com
 */
public class IGES104_ConicArcProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {		
		// get the parameters for the conic arc
		final double a	= params.getDoubleParameter( 1 );
		final double b	= params.getDoubleParameter( 2 );
		final double c	= params.getDoubleParameter( 3 );
		final double d	= params.getDoubleParameter( 4 );
		final double e	= params.getDoubleParameter( 5 );
		final double f	= params.getDoubleParameter( 6 );
		final double zt = params.getDoubleParameter( 7 );
		final double x1 = params.getDoubleParameter( 8 );
		final double y1 = params.getDoubleParameter( 9 );
		final double x2 = params.getDoubleParameter( 10 );
		final double y2 = params.getDoubleParameter( 11 );
		
		// lookup the matrix
		final int matrixID	= entry.getTransformationMatrix();
		final IGESTransformationMatrix matrix = igesModel.lookupMatrix( matrixID );
		
		// return the conic entity
		return new IGESConicArc( entry, matrix, a, b, c, d, e, f, zt, x1, y1, x2, y2 );
	}

}
