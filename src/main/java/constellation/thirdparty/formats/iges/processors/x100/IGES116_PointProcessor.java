package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESPoint;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/**
 * Represents an IGES Point
 * @author lawrence.daniels@gmail.com
 */
public class IGES116_PointProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the x,y,z coordinates
		final double x = params.getDoubleParameter( 1 );
		final double y = params.getDoubleParameter( 2 );
		final double z = params.getDoubleParameter( 3 );
		
		// create & return the point
		return new IGESPoint( x, y, z, entry.getColorNumber() );
	}

}
