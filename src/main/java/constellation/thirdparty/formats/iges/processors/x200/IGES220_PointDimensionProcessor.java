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
 * Represents an IGES Point Dimension
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            DENOTE      Pointer   Pointer to the DE of the General Note Entity
 * 2            DEARRW      Pointer   Pointer to the DE of the Leader Entity
 * 3            DEGEOM      Pointer   Pointer to the DE of the Circular ArcXY Entity, Composite Curve
 *                                    Entity, or Simple Closed Planar Curve Entity, or zero
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES220_PointDimensionProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		logger.info( String.format( "Point Dimension: params = %s", params.toString() ) );
		
		// get the directory element indices
		final int index1 = params.getIntegerParameter( 1 );
		final int index2 = params.getIntegerParameter( 2 );
		final int index3 = params.getIntegerParameter( 3 );
		
		// lookup the note 
		final IGES_D noteEntry = igesModel.lookupDirectoryEntry( index1 );
		logger.info( String.format( "Point Dimension: noteEntry = %s", noteEntry ) );
		
		// lookup the leader arrow
		final IGES_D arrowEntry = igesModel.lookupDirectoryEntry( index2 );
		logger.info( String.format( "Point Dimension: arrowEntry = %s", arrowEntry ) );
		
		// lookup the geometry (curve, etc.)
		final IGES_D geomEntry = igesModel.lookupDirectoryEntry( index3 );
		logger.info( String.format( "Point Dimension: geomEntry = %s", geomEntry ) );
		
		// TODO implement this function
		return new IGESFauxEntity();
	}

}