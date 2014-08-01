package constellation.tools.collaboration.commands;

import static constellation.commands.CxCommandManager.encodeString;
import static constellation.tools.collaboration.commands.CollaborativeCommandFactory.SEND_HOST_INFO;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.commands.builtin.AbstractCommand;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.RemoteGeometricModel;

/**
 * Send Host Information Command
 * @author lawrence.daniels@gmail.com
 */
@VirtualCommand(true)
public class SendHostInfoCommand extends AbstractCommand {
	private final String modelName;
	
	/** 
	 * Creates a new "SEND HOSTINFO" Command
	 * @param modelName the given model name
	 */
	SendHostInfoCommand( final String modelName ) {
		this.modelName = modelName;
	}
	
	/** 
	 * Creates a new "SEND HOSTINFO" Command
	 * @param model the given {@link GeometricModel model}
	 * @return a new {@link SendHostInfoCommand "SEND HOSTINFO" Command}
	 */
	public static SendHostInfoCommand create( final GeometricModel model ) {
		return new SendHostInfoCommand( model.getName() );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream buf ) 
	throws IOException {		
		// append the OpCode
		buf.writeInt( SEND_HOST_INFO );		
		
		// append the model name
		encodeString( buf, modelName );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		// the model must be connected
		if( model.isVirtual() ) {
			final RemoteGeometricModel remoteModel = (RemoteGeometricModel)model;
			remoteModel.setName( modelName );
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%02X] SEND HOSTINFO '%s'", SEND_HOST_INFO, modelName );
	}
	
}