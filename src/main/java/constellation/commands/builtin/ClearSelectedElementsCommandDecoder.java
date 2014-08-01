package constellation.commands.builtin;

import java.nio.ByteBuffer;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;

/** 
 * Clear Selected Elements Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public class ClearSelectedElementsCommandDecoder implements CxCommandDecoder {
	
	/** 
	 * {@inheritDoc}
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		return ClearSelectedElementsCommand.create();
	}

}
