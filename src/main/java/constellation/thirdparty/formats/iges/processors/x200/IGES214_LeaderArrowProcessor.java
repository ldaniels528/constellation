package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x200.IGESLeaderArrow;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;

/** 
 * Represents a Leader Arrow
 * <pre>
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            N            Integer   Number of segments
 * 2            AD1          Real      Arrowhead height
 * 3            AD2          Real      Arrowhead width
 * 4            ZT           Real      Z depth
 * 5            XH           Real      Arrowhead coordinates
 * 6            YH           Real
 * 7            X1           Real      First segment tail coordinate pair
 * 8            Y1           Real
 * ..           .            .
 * .            ..           ..
 * 5+2*N       XN            Real      Last segment tail coordinate pair
 * 6+2*N       YN            Real
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES214_LeaderArrowProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {		
		// get the arrow count
		final int count = params.getIntegerParameter( 1 );
		
		// get the arrow head dimensions
		final double height	= params.getDoubleParameter( 2 );
		final double width 	= params.getDoubleParameter( 3 );
		
		// get the arrow head coordinates
		final double zt = params.getDoubleParameter( 4 );
		final double xh = params.getDoubleParameter( 5 );
		final double yh = params.getDoubleParameter( 6 );
		
		// create the leader arrow
		final IGESLeaderArrow arrow = new IGESLeaderArrow( width, height, xh, yh, zt );
		
		// gather the tail sections
		int index = 7;
		for( int n = 0; n < count; n++ ) {
			// get the arrow tail coordinates
			final double xt = params.getDoubleParameter( index++ );
			final double yt = params.getDoubleParameter( index++ );
			
			// add the tail section
			arrow.addTail( xt, yt );
		}
		
		// return the arrow
		return arrow;
	}

}
