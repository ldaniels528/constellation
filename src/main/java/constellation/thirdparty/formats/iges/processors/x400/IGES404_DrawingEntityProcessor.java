package constellation.thirdparty.formats.iges.processors.x400;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.IGESFauxEntity;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/** 
 * <h2>Drawing Entity (Type 404)</h2>
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 * 1            N            Integer   Number of View pointers
 * 2            VPTR1        Pointer   Pointer to the DE of the first View Entity
 * 3            XORIGIN1     Real      Drawing space coordinate of the origin of the first transformed
 *                                     View
 * 4            YORIGIN1     Real      Drawing space coordinate of the origin of the first transformed
 *                                     View
 * 5            VPTR2        Pointer   Pointer to the DE of the second View Entity
 * ..           .            .
 * .            ..           ..
 * 2+3*N        M            Integer   Number of Annotation Entities (may be zero)
 * 3+3*N        DPTR1        Pointer   Pointer to the DE of the first Annotation Entity in this Drawing
 * ..           .            .
 * .            ..           ..
 * 2+M+3*N      DPTRM        Pointer   Pointer to the DE of the last Annotation Entity in this Drawing
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES404_DrawingEntityProcessor extends IGESAbstractEntityProcessor {

	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		logger.info( String.format( "Drawing Entity: params = %s", params.toString() ) );
		// TODO Auto-generated method stub
		return new IGESFauxEntity();
	}

}
