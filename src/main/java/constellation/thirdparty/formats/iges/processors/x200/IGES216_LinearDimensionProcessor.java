package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.entities.x200.IGESLeaderArrow;
import constellation.thirdparty.formats.iges.entities.x200.IGESLinearDimension;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory;

/**
 * <h2>Linear Dimension Entity (Type 216)</h2>
 * <p>A Linear Dimension Entity consists of a general note; 
 * two leaders; and zero, one or two witness lines.
 * Refer to Figure 65 for examples of linear dimensions.</p>
 * 
 * <p>Field 15 of the Directory Entry Section accomodates a form number.  For this entity, the form
 * number is used to maintain the nature of the dimension on the originating system.  The definition
 * of the form numbers can be found in Appendix G (see Section G.18).</p>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1           DENOTE      Pointer   Pointer to the DE of the General Note Entity
 * 2           DEARRW1     Pointer   Pointer to the DE of the first Leader Entity
 * 3           DEARRW2     Pointer   Pointer to the DE of the second Leader Entity
 * 4           DEWIT1      Pointer   Pointer to the DE of the first Witness Line Entity, 
 *                                   or zero if not defined
 * 5           DEWIT2      Pointer   Pointer to the DE of the second Witness Line Entity, 
 *                                   or zero if not defined
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES216_LinearDimensionProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the directory element pointers
		final int ptr1 = params.getIntegerParameter( 1 );
		final int ptr2 = params.getIntegerParameter( 2 );
		final int ptr3 = params.getIntegerParameter( 3 );
		final int ptr4 = params.getIntegerParameter( 4 );
		final int ptr5 = params.getIntegerParameter( 5 );
		
		// lookup the note 
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( ptr1 );
		final IGES_P noteParms = igesModel.lookupParameters( noteEntry.getParameterIndex() );
		final IGESGeneralNote note = (IGESGeneralNote)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, noteEntry, noteParms ); 
		
		// lookup the leader arrow #1
		final IGES_D arrow1Entry = igesModel.lookupDirectoryEntry( ptr2 );
		final IGES_P arrow1Parms = igesModel.lookupParameters( arrow1Entry.getParameterIndex() );
		final IGESLeaderArrow arrow1 = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrow1Entry, arrow1Parms ); 
		
		// lookup the leader arrow #2
		final IGES_D arrow2Entry = igesModel.lookupDirectoryEntry( ptr3 );
		final IGES_P arrow2Parms = igesModel.lookupParameters( arrow2Entry.getParameterIndex() );
		final IGESLeaderArrow arrow2 = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrow2Entry, arrow2Parms ); 
		
		// lookup the witness line #1 
		final IGES_D witness1Entry = igesModel.lookupDirectoryEntry( ptr4 );
		final IGES_P witness1Parms = igesModel.lookupParameters( witness1Entry.getParameterIndex() );
		final IGESCopiousData witness1 = (IGESCopiousData)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, witness1Entry, witness1Parms ); 
		
		// lookup the witness line #2
		final IGES_D witness2Entry = igesModel.lookupDirectoryEntry( ptr5 );
		final IGES_P witness2Parms = igesModel.lookupParameters( witness2Entry.getParameterIndex() );
		final IGESCopiousData witness2 = (IGESCopiousData)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, witness2Entry, witness2Parms ); 
		
		// return the linear dimension
		return new IGESLinearDimension( note, arrow1, arrow2, witness1, witness2 );
	}

}
