package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/**
 * This class is responsible for parsing the parameters of a directory entry
 * to create the resultant {@link IGES106_CopiousDataProcessor IGES Copious Data Entity}.
 * @author lawrence.daniels@gmail.com
 */
public class IGES106_CopiousDataProcessor extends IGESAbstractEntityProcessor {
	private static final int TYPE1 = 1;
	private static final int TYPE2 = 2;
	private static final int TYPE3 = 3;
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the interpretation flag
		final int interpretationFlag = params.getIntegerParameter( 1 );
		
		// get the count of data tuples
		final int count = params.getIntegerParameter( 2 );
		
		// determine the interpretation of the parameters
		switch( interpretationFlag ) {
			case TYPE1: return parseType1( params, count );
			case TYPE2: return parseType2( params, count );
			case TYPE3: return parseType3( params, count );
			default:
				throw new ModelFormatException( String.format( "Unrecognized form type (%d)", interpretationFlag ) );
		}
	}
	
	/**
	 * Parses a Type 1 copious data element
	 * @param params the given {@link IGES_P parameters}
	 * @param count the number of data sets
	 * @return the resultant {@link IGESCopiousData copious data}
	 */
	private IGESCopiousData parseType1( final IGES_P params, final int count ) {
		final IGESCopiousData data = new IGESCopiousData();
		int n = 3;
		
		// get the common Z coordinate (ZT)
		final double zt = params.getDoubleParameter( n++ );
		
		// get the X and Y coordinate tuples
		for( int loop = 0; loop < count; loop++ ) {
			final double x = params.getDoubleParameter( n++ );
			final double y = params.getDoubleParameter( n++ );
			data.addSet( x, y, zt );
		}
		return data;
	}
	
	/**
	 * Parses a Type 2 copious data element
	 * @param params the given {@link IGES_P parameters}
	 * @param count the number of data sets
	 * @return the resultant {@link IGESCopiousData copious data}
	 */
	private IGESCopiousData parseType2( final IGES_P params, final int count ) {
		final IGESCopiousData data = new IGESCopiousData();
		int n = 3;
		
		// get the X, Y, and Z coordinate tuples
		for( int loop = 0; loop < count; loop++ ) {
			final double x = params.getDoubleParameter( n++ );
			final double y = params.getDoubleParameter( n++ );
			final double z = params.getDoubleParameter( n++ );
			data.addSet( x, y, z );
		}
		return data;
	}
	
	/**
	 * Parses a Type 3 copious data element
	 * @param params the given {@link IGES_P parameters}
	 * @param count the number of data sets
	 * @return the resultant {@link IGESCopiousData copious data}
	 */
	private IGESCopiousData parseType3( final IGES_P params, final int count ) {
		final IGESCopiousData data = new IGESCopiousData();
		int n = 3;
		
		// get the X, Y, Z and I, J, K coordinate tuples
		for( int loop = 0; loop < count; loop++ ) {
			final double x = params.getDoubleParameter( n++ );
			final double y = params.getDoubleParameter( n++ );
			final double z = params.getDoubleParameter( n++ );
			final double i = params.getDoubleParameter( n++ );
			final double j = params.getDoubleParameter( n++ );
			final double k = params.getDoubleParameter( n++ );
			data.addSet( x, y, z, i, j, k );
		}
		return data;
	}

}

