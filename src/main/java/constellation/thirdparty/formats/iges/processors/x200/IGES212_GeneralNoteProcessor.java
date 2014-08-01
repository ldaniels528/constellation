package constellation.thirdparty.formats.iges.processors.x200;

import static constellation.thirdparty.formats.iges.elements.IGESElement.parseTokens;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.IGESModel;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x200.IGESGeneralNote;
import constellation.thirdparty.formats.iges.processors.IGESAbstractEntityProcessor;

/**
 * Represents an IGES General Note
 * <pre>
 * Parameter Data
 * --------------------------------------------------
 * Index        Name         Type      Description 
 * --------------------------------------------------
 *  1           NS           Integer   Number of text strings in General Note
 *  2           NC1          Integer   Number of characters in first string (TEXT1) or zero. The number
 *                                     of characters (NCn) must always be equal to the character
 *                                     count of its corresponding text string (TEXTn)
 *  3           WT1          Real      Box width
 *  4           HT1          Real      Box height
 *  5           FC1          Integer   Font code (default = 1)
 *                           or
 *                           Pointer   Pointer to the DE of the Text Font Definition Entity if negative
 *  6           SL1          Real      Slant angle of TEXT1 in radians (ss=2 is the value for no slant
 *                                     angle and is the default value)
 *  7           A1           Real      Rotation angle in radians for TEXT1
 *  8           M1           Integer   Mirror flag:
 *                                       0 = no mirroring
 *                                       1 = mirror axis is perpendicular to text base line
 *                                       2 = mirror axis is text base line
 *  9           VH1          Integer   Rotate internal text flag:
 *                                       0 = text horizontal
 *                                       1 = text vertical
 * 10           XS1          Real      First text start point
 * 11           YS1          Real
 * 12           ZS1          Real      Z depth from XT, YT plane
 * 13           TEXT1        String    First text string
 * 14           NC2          Integer   Number of characters in second text string
 * ..           .            .
 * .            ..           ..
 * -10+12*NS    NCNS        Integer   Number of characters in last text string
 * ..           .            .
 * .            ..           ..
 * 1+12*NS    TEXTNS      String    Last text string
 * </pre>
 * @author lawrence.daniels@gmail.com
 */
public class IGES212_GeneralNoteProcessor extends IGESAbstractEntityProcessor {

	/** 
	 * {@inheritDoc}
	 */
	public IGESEntity evaluate( final GeometricModel cxModel, IGESModel igesModel, final IGES_D entry, final IGES_P params ) 
	throws ModelFormatException {
		// get the number of text lines
		final int count = params.getIntegerParameter( 1 );
		
		// create an array of 2D Text objects
		final IGESGeneralNote notes = new IGESGeneralNote( );
		
		// get the text strings
		for( int n = 0; n < count; n++ ) {
			final int index = ( 13 * ( n + 1 ) ) - ( n == 0 ? 0 : 1 );
			 
 			// get the (x,y,z) coordinates
			final double x = params.getDoubleParameter( index - 3 );
			final double y = params.getDoubleParameter( index - 2 );
			final double z = params.getDoubleParameter( index - 1 );
		
			// get the text string
			final String textString = parseTokens( params.getStringParameter( index + 0 ) ).getFirst();
			
			// create the note
			notes.addNote( x, y, z, textString );
		}
				
		// return the notes
		return notes;
	}

}
