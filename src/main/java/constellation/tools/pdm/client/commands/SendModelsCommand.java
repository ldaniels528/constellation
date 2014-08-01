package constellation.tools.pdm.client.commands;

import static constellation.tools.pdm.client.commands.PDMCommandFactory.SEND_MODELS;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import constellation.commands.builtin.AbstractCommand;
import constellation.model.GeometricModel;
import constellation.tools.pdm.client.PDMClient;
import constellation.tools.pdm.client.PDMModelFile;

/**
 * Send Models Command
 * @author lawrence.daniels@gmail.com
 */
public class SendModelsCommand extends AbstractCommand {
	private final List<PDMModelFile> modelFiles;
	
	/** 
	 * Default constructor
	 */
	SendModelsCommand( final List<PDMModelFile> modelFiles ) {
		this.modelFiles = modelFiles;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.app.navigationbar.sharing.ops.Operation#createOperation(java.lang.Object)
	 */
	public static SendModelsCommand create( final List<PDMModelFile> modelFiles ) {
		return new SendModelsCommand( modelFiles );
	}

	/**
	 * Decodes the given bytes into a "Send Models" command
	 * @param bytes the given array of bytes
	 * @return the {@link SendModelsCommand command}
	 */
	public static SendModelsCommand decode( final byte[] bytes ) {
		// wrap the data
		final ByteBuffer buffer = ByteBuffer.wrap( bytes );
		
		 // skip the opCode
		buffer.getInt();
		
		// extract the attributes of the command
		final List<PDMModelFile> modelFiles = PDMCommandFactory.decodeModelList( buffer );
		
		// return the command
		return new SendModelsCommand( modelFiles );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.pdm.server.io.PDMCommand#encode()
	 */
	public void encode( final DataOutputStream buffer ) 
	throws IOException {
		// append the OpCode
		buffer.writeInt( SEND_MODELS );			
		
		// append the model name
		PDMCommandFactory.encodeModelList( buffer, modelFiles );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.navigationbar.sharing.ops.AbstractOperation#evaluate(constellation.geometry.model.CxModel, java.util.List)
	 */
	public void evaluate( final GeometricModel model ) {
		// update the PDM client with model list
		final PDMClient pdmClient = PDMClient.getInstance();
		pdmClient.setModelFiles( modelFiles );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.model.collaboration.objects.AbstractOperation#toString()
	 */
	public String toString() {
		return format( "[0%4X] SEND_MODELS [%02d]", SEND_MODELS, modelFiles.size() );
	}

}
