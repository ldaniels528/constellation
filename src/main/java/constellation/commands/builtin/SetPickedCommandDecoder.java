package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.decodeString;

import java.io.IOException;
import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * Set Picked Elements Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class SetPickedCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) 
	throws IOException {
		// decode the element's label
		final String label =
			decodeString( buffer );		
		
		// return the command
		return new SetPickedCommand( label );
	}

}
