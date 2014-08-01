package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESLine;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/**
 * Represents an IGES Line
 * @author lawrence.daniels@gmail.com
 */
public class IGES110_LineProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the Point 1's x,y,z coordinates
		final double x1 = params.getDoubleParameter( 1 );
		final double y1 = params.getDoubleParameter( 2 );
		final double z1 = params.getDoubleParameter( 3 );
		
		// get the Point 2's x,y,z coordinates
		final double x2 = params.getDoubleParameter( 4 );
		final double y2 = params.getDoubleParameter( 5 );
		final double z2 = params.getDoubleParameter( 6 );
		
		// add the line to the model
		return new IGESLine( x1, y1, z1, x2, y2, z2, entry.getColorNumber(), entry.getLineFontPattern() );
	}

}
