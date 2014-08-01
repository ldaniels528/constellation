package constellation.thirdparty.formats.iges.processors.x400;

import static constellation.thirdparty.formats.iges.elements.IGESElement.parseTokens;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x400.IGESPropertyEntity;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/** 
 * Represents an IGES Property Entity 
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            NP          Integer   Number of property values (NP=3)
 * 2            EVR         Integer   Electrical vias restriction (EVR=0,1 or 2)
 * 3            ECPR        Integer   Electrical components restriction (ECPR=0,1 or 2)
 * 4            ECRR        Integer   Electrical circuitry restriction (ECRR=0,1 or 2)
 * </pre> 
 * @author lawrence.daniels@gmail.com
 */
public class IGES406_PropertyEntityProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the parameters
		final int count = params.getIntegerParameter( 1 );
		
		// create the property entity
		final IGESPropertyEntity entity = new IGESPropertyEntity();
		
		// loop thru the parameters
		int n = 2;
		for( int loop = 0; loop < count; loop++ ) {
			final String dataString = params.getStringParameter( n++ );
			entity.addProperties( parseTokens( dataString ) );
		}
		
		return entity;
	}

}
