package constellation.tools.pdm.client.commands;

import static constellation.commands.CxCommandManager.decodeDate;
import static constellation.commands.CxCommandManager.decodeString;
import static constellation.commands.CxCommandManager.encodeDate;
import static constellation.commands.CxCommandManager.encodeString;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import constellation.commands.CxCommandManager;
import constellation.tools.pdm.client.PDMModelFile;
import constellation.tools.pdm.client.PDMModelFileStatus;

/**
 * Constellation Product Data Management (PDM) Command Factory
 * @author lawrence.daniels@gmail.com
 */
public class PDMCommandFactory {
	// define the custom opCodes
	public static final Integer PDM_VERSION	= 0x1FFE;
	public static final Integer SEND_MODELS	= 0x1FFF;
	
	/**
	 * Register the Collaborative command decoders
	 */
	public static void init() {
		final CxCommandManager ccm = CxCommandManager.getInstance();
		ccm.register( PDM_VERSION,	new PDMVersionCommandDecoder() );
		ccm.register( SEND_MODELS,	new SendModelsCommandDecoder() );
	}

	/**
	 * Decodes the character string from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the character string
	 */
	public static List<PDMModelFile> decodeModelList( final ByteBuffer buffer ) {
		// create a container for the model files
		final List<PDMModelFile> modelFiles = new LinkedList<PDMModelFile>();
		
		// get the number of model files
		final short count = buffer.getShort();
		
		// retrieve each model file
		for( int n = 0; n < count; n++ ) {
			// retrieve the model file
			final PDMModelFile modelFile = new PDMModelFile();
			modelFile.setName( decodeString( buffer ) );
			modelFile.setStatus( decodeStatus( buffer ) );
			modelFile.setCreatedBy( decodeString( buffer ) );
			modelFile.setCreatedTime( decodeDate( buffer ) );
			modelFile.setLastModifiedBy( decodeString( buffer ) );
			modelFile.setLastModifiedTime( decodeDate( buffer ) );
			
			// add the model file to the list
			modelFiles.add( modelFile );
		}
		
		// return the model files
		return modelFiles;
	}

	/**
	 * Encodes the given model list to the given buffer
	 * @param buf the given {@link ByteBuffer buffer}
	 * @param modelFiles the given collection of {@link PDMModelFile PDM model files}
	 * @throws IOException 
	 */
	public static void encodeModelList( final DataOutputStream buf, final List<PDMModelFile> modelFiles ) 
	throws IOException {
		// get the number of model files
		final short count = (short)modelFiles.size();
		
		// append the number of model files to the stream
		buf.writeShort( count );
		
		// write each model file
		for( final PDMModelFile modelFile : modelFiles ) {
			encodeString( buf, modelFile.getName() );
			PDMCommandFactory.encodeStatus( buf, modelFile.getStatus() );
			encodeString( buf, modelFile.getCreatedBy() );
			encodeDate( buf, modelFile.getCreatedTime() );
			encodeString( buf, modelFile.getLastModifiedBy() );
			encodeDate( buf, modelFile.getLastModifiedTime() );
		}
	}

	/**
	 * Decodes the model file status from the given buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the {@link PDMModelFileStatus model file status}
	 */
	public static PDMModelFileStatus decodeStatus( final ByteBuffer buffer ) {
		final byte index = buffer.get();
		return PDMModelFileStatus.values()[ index ];
	}

	/**
	 * Encodes the given model file status to the given buffer
	 * @param stream the given {@link ByteBuffer buffer}
	 * @param status the {@link PDMModelFileStatus model file status}
	 * @throws IOException 
	 */
	public static void encodeStatus( final DataOutputStream stream, final PDMModelFileStatus status ) 
	throws IOException {
		stream.write( status.ordinal() );
	}

}
