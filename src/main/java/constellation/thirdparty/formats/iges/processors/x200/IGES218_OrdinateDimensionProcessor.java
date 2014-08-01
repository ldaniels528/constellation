package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.entities.x200.IGESOrdinateDimension;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory;

/** 
 * <h2>Ordinate Dimension Entity (Type 218)</h2>
 * <p>The Ordinate Dimension Entity is used to indicate dimensions from a common base line.  
 * Dimensioning is only permitted along the XT or YT axis.</p>
 * 
 * <p>An Ordinate Dimension Entity consists of a general note and a witness line or leader.  
 * The values stored are pointers to the Directory Entry for the associated General Note and 
 * Witness Line or Leader Entities.  A second form of the Ordinate Dimension Entity is defined 
 * in Appendix G (see Section G.19).</p>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1           DENOTE      Pointer   Pointer to the DE of the General Note Entity
 * 2           DEWIT       Pointer   Pointer to the DE of the Witness Line Entity or Leader Entity
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES218_OrdinateDimensionProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {		
		// get the directory element indices
		final int ptr1 = params.getIntegerParameter( 1 );
		final int ptr2 = params.getIntegerParameter( 2 );
		
		// lookup the note 
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( ptr1 );
		final IGES_P noteParms = igesModel.lookupParameters( noteEntry.getParameterIndex() );
		final IGESGeneralNote note = (IGESGeneralNote)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, noteEntry, noteParms ); 
		
		// lookup the witness line 
		final IGES_D witness2Entry = igesModel.lookupDirectoryEntry( ptr2 );
		final IGES_P witness2Parms = igesModel.lookupParameters( witness2Entry.getParameterIndex() );
		final IGESCopiousData witness = (IGESCopiousData)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, witness2Entry, witness2Parms ); 
		
		// return the entity
		return new IGESOrdinateDimension( note, witness );
	}

}