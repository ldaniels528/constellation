package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.IGESFauxEntity;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/** 
 * Represents an IGES Diameter Dimension
 * @author lawrence.daniels@gmail.com
 */
public class IGES206_DiameterDimension extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		logger.info( String.format( "Diameter Dimension: params = %s", params.toString() ) );
		// TODO implement this function
		return new IGESFauxEntity();
	}

}

