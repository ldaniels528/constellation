package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESTransformationMatrix;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/**
 * <h2>Transformation Matrix Entity (Type 124)</h2>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 *  1           R11         Real      Top Row
 *  2           R12         Real      .
 *  3           R13         Real      .
 *  4           T1          Real      .
 *  5           R21         Real      Second Row
 *  6           R22         Real      .
 *  7           R23         Real      .
 *  8           T2          Real      .
 *  9           R31         Real      Third Row
 * 10           R32         Real      .
 * 11           R33         Real      .
 * 12           T3          Real      .
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES124_TransformationMatrixProcessor implements IGESEntityProcessor {
	private int sequenceNumber = 0;
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// create containers for the data
		final double[] r = new double[9];
		final Double[] t = new Double[3];
		
		// get the parameters
		int a = 0;
		int b = 0;
		int n = 0;
		for( int loop = 0; loop < 3; loop++ ) {
			r[a++] = params.getDoubleParameter( n++ );
			r[a++] = params.getDoubleParameter( n++ );
			r[a++] = params.getDoubleParameter( n++ );
			t[b++] = params.getDoubleParameter( n++ );
		}
		
		// create the transformation matrix instance
		final IGESTransformationMatrix matrix = new IGESTransformationMatrix( r, t );

		// add it to the IGES model
		igesModel.add( ++sequenceNumber, matrix );
		
		// return the matrix
		return matrix;
	}

}
