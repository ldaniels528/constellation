package constellation.tools.collaboration.commands;

import static constellation.commands.CxCommandManager.encodeString;
import static constellation.tools.collaboration.commands.CollaborativeCommandFactory.SEND_MSG;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.commands.builtin.AbstractCommand;
import constellation.model.GeometricModel;
import constellation.tools.collaboration.RemoteGeometricModel;

/**
 * Send Message Command
 * @author lawrence.daniels@gmail.com
 */
@VirtualCommand(true)
public class SendMessageCommand extends AbstractCommand {
	private final String message;
	
	/** 
	 * Creates a new "Send Message" Command
	 * @param message the given message
	 */
	SendMessageCommand( final String message ) {
		this.message = message;
	}

	/** 
	 * Creates a new "Send Message" Command
	 * @param message the given message
	 * @return a new {@link SendMessageCommand "Send Message" Command}
	 */
	public static SendMessageCommand create( final String message ) {
		return new SendMessageCommand( message );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		// append the OpCode
		stream.writeInt( SEND_MSG );			
		
		// append the model name
		encodeString( stream, message );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		// if the model is virtual ...
		if( model.isVirtual() ) {
			// get the remote model instance
			final RemoteGeometricModel remoteModel = (RemoteGeometricModel)model;
			
			// append the message to the messaging agent
			final RemoteMessagingDialog messagingAgent = remoteModel.getMessagingDialog();
			messagingAgent.appendMessage( format( "[%s] %s", remoteModel.getRemoteID(), message ) );
			messagingAgent.makeVisible();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[0%4X] SEND MESSAGE '%s'", SEND_MSG, message );
	}

}
