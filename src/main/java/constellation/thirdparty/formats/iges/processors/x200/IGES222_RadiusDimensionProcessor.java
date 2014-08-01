package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.entities.x200.IGESLeaderArrow;
import constellation.thirdparty.formats.iges.entities.x200.IGESRadiusDimension;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory;

/** 
 * This class is responsible for parsing an IGES Radius Dimension (Type 222)
 * @author lawrence.daniels@gmail.com
 */
public class IGES222_RadiusDimensionProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the directory element pointers
		final int ptr1 	= params.getIntegerParameter( 1 );
		final int ptr2 	= params.getIntegerParameter( 2 );
		final double xt = params.getDoubleParameter( 3 );
		final double yt = params.getDoubleParameter( 4 );
		
		// lookup the note 
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( ptr1 );
		final IGES_P noteParms = igesModel.lookupParameters( noteEntry.getParameterIndex() );
		final IGESGeneralNote note = (IGESGeneralNote)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, noteEntry, noteParms ); 
		
		// lookup the leader arrow 
		final IGES_D arrowEntry = igesModel.lookupDirectoryEntry( ptr2 );
		final IGES_P arrowParms = igesModel.lookupParameters( arrowEntry.getParameterIndex() );
		final IGESLeaderArrow arrow = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrowEntry, arrowParms ); 
		
		// return the radius dimension
		return new IGESRadiusDimension( note, arrow, xt, yt );
	}

}