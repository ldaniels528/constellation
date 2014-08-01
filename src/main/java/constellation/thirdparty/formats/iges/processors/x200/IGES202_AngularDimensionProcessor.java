package constellation.thirdparty.formats.iges.processors.x200;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESCopiousData;
import constellation.thirdparty.formats.iges.entities.x200.IGESAngularDimension;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.entities.x200.IGESLeaderArrow;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;
import constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory;

/** 
 * <h2>Represents an IGES Angular Dimension (Type 202)</h2>
 * <p>An Angular Dimension Entity consists of a general note; zero, one, or two witness lines; two leaders;
 * and an angle vertex point. Figure 48 indicates the construction used. Refer to Figure 49 for examples
 * of angular dimensions. If two witness lines are used, each is contained in its own Copious Data Entity.
 * </p>
 * <p>Each leader consists of at least one circular arc segment with an arrowhead at one end.   The
 * leader pointers are ordered such that the first circular arc segment of the first leader is defined
 * in a counterclockwise manner from arrowhead to terminate point, and the first circular arc segment
 * of the second leader is defined in a clockwise manner. (Refer to Section 3.2.4 for information relating
 * to the use of the term counterclockwise).
 * </p>
 * <p>Section 4.60 contains a discussion of multi-segment leaders. For those leaders in Angular Dimension
 * Entities consisting of more than one segment, the first two segments are circular arcs with a center
 * at the vertex point.  The second circular arc segment is defined in the opposite direction from the
 * first circular arc segment.  Remaining segments, if any, are straight lines.  Any leader segment in
 * which the start point is the same as the terminate point is to be ignored. This convention arises to
 * facilitate the definition of the second circular arc segment such as in the bottom leader in Figure 48.
 * The first example in Figure 49 illustrates a leader with three segments.</p>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1           DENOTE      Pointer   Pointer to the DE of the General Note Entity
 * 2           DEWIT1      Pointer   Pointer to the DE of the first Witness Line Entity or zero
 * 3           DEWIT2      Pointer   Pointer to the DE of the second Witness Line Entity or zero
 * 4           XT          Real      Coordinates of vertex point
 * 5           YT          Real
 * 6           R           Real      Radius of Leader arcs
 * 7           DEARRW1     Pointer   Pointer to the DE of the first Leader Entity
 * 8           DEARRW2     Pointer   Pointer to the DE of the second Leader Entity
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES202_AngularDimensionProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the directory element pointers
		final int ptr1 	= params.getIntegerParameter( 1 );
		final int ptr2 	= params.getIntegerParameter( 2 );
		final int ptr3 	= params.getIntegerParameter( 3 );
		final double xt = params.getDoubleParameter( 4 );
		final double yt = params.getDoubleParameter( 5 );
		final double r 	= params.getDoubleParameter( 6 );
		final int ptr7 	= params.getIntegerParameter( 7 );
		final int ptr8 	= params.getIntegerParameter( 8 );
		
		// lookup the note 
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( ptr1 );
		final IGES_P noteParms = igesModel.lookupParameters( noteEntry.getParameterIndex() );
		final IGESGeneralNote note = (IGESGeneralNote)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, noteEntry, noteParms ); 
			
		// lookup the witness line #1 
		final IGES_D witness1Entry = igesModel.lookupDirectoryEntry( ptr2 );
		final IGES_P witness1Parms = igesModel.lookupParameters( witness1Entry.getParameterIndex() );
		final IGESCopiousData witness1 = (IGESCopiousData)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, witness1Entry, witness1Parms ); 
		
		// lookup the witness line #2
		final IGES_D witness2Entry = igesModel.lookupDirectoryEntry( ptr3 );
		final IGES_P witness2Parms = igesModel.lookupParameters( witness2Entry.getParameterIndex() );
		final IGESCopiousData witness2 = (IGESCopiousData)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, witness2Entry, witness2Parms ); 
		
		// lookup the leader arrow #1
		final IGES_D arrow1Entry = igesModel.lookupDirectoryEntry( ptr7 );
		final IGES_P arrow1Parms = igesModel.lookupParameters( arrow1Entry.getParameterIndex() );
		final IGESLeaderArrow arrow1 = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrow1Entry, arrow1Parms ); 
		
		// lookup the leader arrow #2
		final IGES_D arrow2Entry = igesModel.lookupDirectoryEntry( ptr8 );
		final IGES_P arrow2Parms = igesModel.lookupParameters( arrow2Entry.getParameterIndex() );
		final IGESLeaderArrow arrow2 = (IGESLeaderArrow)IGESEntityProcessorFactory.evaluate( cxModel, igesModel, arrow2Entry, arrow2Parms ); 
		
		// return the linear dimension
		return new IGESAngularDimension( note, witness1, witness2, xt, yt, r, arrow1, arrow2 );
	}

}
