package constellation.commands.builtin;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * Clear Picked Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class ClearPickedElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		return ClearPickedElementCommand.create();
	}

}