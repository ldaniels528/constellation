package constellation.commands;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Constellation Command Decoder
 * @author lawrence.daniels@gmail.com
 */
public interface CxCommandDecoder {
	
	/**
	 * Decodes the given command from the given data buffer
	 * @param buffer the given {@link ByteBuffer data buffer}
	 * @return the decoded {@link CxCommand command}
	 * @throws IOException
	 */
	CxCommand decode( ByteBuffer buffer ) throws IOException;

}
