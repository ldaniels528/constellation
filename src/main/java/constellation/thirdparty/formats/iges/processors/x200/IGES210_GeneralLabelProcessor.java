package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralLabel;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.entities.x200.IGESLeaderArrow;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessor;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory;

/** 
 * <h2>General Label Entity (Type 210)</h2>
 * <p>A General Label Entity consists of a general note with one 
 * or more associated leaders. Examples of general labels are shown 
 * in Figure 53.</p>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            DENOTE       Pointer   Pointer to the DE of the associated General Note Entity
 * 2            n            Integer   Number of Leaders
 * 3            DEARRW1      Pointer   Pointer to the DE of the first associated Leader Entity
 * .            .            .
 * ..           ..           ..
 * 2+n          DEARRWn      Pointer   Pointer to the DE of the last associated Leader Entity
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES210_GeneralLabelProcessor implements IGESEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the note
		final int notePtr = params.getIntegerParameter( 1 );
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( notePtr );
		final IGES_P noteParms = igesModel.lookupParameters( noteEntry.getParameterIndex() );
		final IGESGeneralNote note = (IGESGeneralNote)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, noteEntry, noteParms ); 
		
		// create the general label
		final IGESGeneralLabel label = new IGESGeneralLabel( note );
		
		// get the number of leader arrows
		final int count = params.getIntegerParameter( 2 );
		
		// attach the leaders
		for( int n = 0; n < count; n++ ) {
			// get the pointer to the leader
			final int ptr = params.getIntegerParameter( n + 3 );
			
			// lookup the leader
			final IGES_D arrowEntry = igesModel.lookupDirectoryEntry( ptr );
			final IGES_P arrowParms = igesModel.lookupParameters( arrowEntry.getParameterIndex() );
			final IGESLeaderArrow arrow = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrowEntry, arrowParms ); 
			
			// attach the leader to the label
			label.add( arrow );
		}
		
		return label;
	}

}