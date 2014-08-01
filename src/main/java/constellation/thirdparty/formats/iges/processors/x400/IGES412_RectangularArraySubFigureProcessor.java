package constellation.thirdparty.formats.iges.processors.x400;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x400.IGESRectangularArraySubFigure;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/** 
 * Represents an IGES Rectangular Array SubFigure
 * <pre>
 * ----------------------------------------------------------------------------
 * Index       Name        Type      Description 
 * ----------------------------------------------------------------------------
 * 1            DE           Pointer   Pointer to the DE of the base entity
 * 2            S            Real      Scale factor (default = 1.0)
 * 3            X            Real      Coordinates of point to be used as lower left corner of array
 * 4            Y            Real
 * 5            Z            Real
 * 6            NC           Integer   Number of columns
 * 7            NR           Integer   Number of rows
 * 8            DX           Real      Horizontal distance between columns
 * 9            DY           Real      Vertical distance between rows
 * 10           AX           Real      Rotation angle in radians
 * 11           LC           Integer   DO-DON'T list count (LC=0 indicates all to be displayed.)
 * 12           DDF          Integer   DO-DON'T flag:
 *                                       0 = DO
 *                                       1 = DON'T
 * 13           N1           Integer   Number of first position to be processed (DO), or not to be
 *                                     processed (DON'T)
 * ..           .            .
 * .            ..           ..
 * 12+LC        NLC          Integer   Number of last position
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES412_RectangularArraySubFigureProcessor extends IGESAbstractEntityProcessor {
	
	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		logger.info( String.format( "Rectangular Array: label '%s' params = %s", entry.getEntityLabel(), params.toString() ) );
		
		// get the parameters as their actual values 
		int index = 1;
		final int pointer 	= params.getIntegerParameter( index++ );
		final double scale	= params.getDoubleParameter( index++ );
		final double x		= params.getDoubleParameter( index++ );
		final double y		= params.getDoubleParameter( index++ );
		final double z		= params.getDoubleParameter( index++ );
		final int columns 	= params.getIntegerParameter( index++ );
		final int rows	 	= params.getIntegerParameter( index++ );
		final double dx		= params.getDoubleParameter( index++ );
		final double dy		= params.getDoubleParameter( index++ );
		final double angle	= params.getDoubleParameter( index++ );
		final int count		= params.getIntegerParameter( index++ );	
		
		// get the list of positions
		final int[] positions = new int[count];
		for( int n = 0; n < count; n++ ) {
			positions[n] = params.getIntegerParameter( index++ );
		}
		
		// return the IGES object
		return new IGESRectangularArraySubFigure( pointer, scale, x, y, z, columns, rows, dx, dy, angle, positions );
	}

}