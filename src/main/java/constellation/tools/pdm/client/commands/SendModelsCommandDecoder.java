package constellation.tools.pdm.client.commands;

import static constellation.tools.pdm.client.commands.PDMCommandFactory.*;
import java.nio.ByteBuffer;
import java.util.List;

import constellation.commands.CxCommand;
import constellation.commands.CxCommandDecoder;
import constellation.tools.pdm.client.PDMModelFile;

/**
 * Send Models Command
 * @author lawrence.daniels@gmail.com
 */
public class SendModelsCommandDecoder implements CxCommandDecoder {

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommandDecoder#decode(java.nio.ByteBuffer)
	 */
	public CxCommand decode( final ByteBuffer buffer ) {
		// extract the attributes of the command
		final List<PDMModelFile> modelFiles = decodeModelList( buffer );
		
		// return the command
		return new SendModelsCommand( modelFiles );
	}

}
