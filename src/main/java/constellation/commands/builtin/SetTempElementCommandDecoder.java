package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.decodeRepresentation;

import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.drawing.EntityRepresentation;

/** 
 * Set Temporary Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SetTempElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// decode the element
		final EntityRepresentation entity =
			decodeRepresentation( buffer );		
		
		// return the command
		return new SetTempElementCommand( entity );
	}

}
