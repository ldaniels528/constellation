package constellation.thirdparty.formats.iges.processors.x100;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCircularArc;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/** 
 * Represents a Circular Arc
 * @author lawrence.daniels@gmail.com
 */
public class IGES100_CircularArcProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the Z Axis for all points
		final double zt = params.getDoubleParameter( 1 );
		
		// get the center point coordinates
		final double cx = params.getDoubleParameter( 2 );
		final double cy = params.getDoubleParameter( 3 );
		
		// get the start point coordinates
		final double ax = params.getDoubleParameter( 4 );
		final double ay = params.getDoubleParameter( 5 );
		
		// get the end point coordinates
		final double bx = params.getDoubleParameter( 6 );
		final double by = params.getDoubleParameter( 7 );

		// add the curve to the model
		return new IGESCircularArc( cx, cy, ax, ay, bx, by, zt, entry.getColorNumber(), entry.getLineFontPattern() );
	}

}
