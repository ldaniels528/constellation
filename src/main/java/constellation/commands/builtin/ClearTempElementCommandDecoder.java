package constellation.commands.builtin;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * Clear Temporary Element Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class ClearTempElementCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		return ClearTempElementCommand.create();
	}

}
