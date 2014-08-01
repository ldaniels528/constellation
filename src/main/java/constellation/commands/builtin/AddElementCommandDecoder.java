package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.drawing.EntityRepresentation;

/**
 * Add Model Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class AddElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// extract the attributes of the representation
		final String label	= decodeString( buffer );
		final Color color	= decodeColor( buffer ); 
		final int pattern	= buffer.get();
		final int layer		= buffer.get();
		
		// decode the geometric representation
		final EntityRepresentation entity = 
			decodeRepresentation( buffer );
		
		// return the command
		return new AddElementCommand( label, color, pattern, layer, entity );
	}

}