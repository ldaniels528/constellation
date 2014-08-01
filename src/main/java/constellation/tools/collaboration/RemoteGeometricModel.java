package constellation.tools.collaboration;

import constellation.commands.VirtualModel;
import constellation.tools.collaboration.commands.RemoteMessagingDialog;

/**
 * Represents a remote model; a model that is being
 * hosted by another peer or server.
 * @author lawrence.daniels@gmail.com
 */
public interface RemoteGeometricModel extends VirtualModel {
	
	/**
	 * Returns the client ID
	 * @return the client ID
	 */
	String getClientID();
	
	/**
	 * Returns the remote ID
	 * @return the remote ID
	 */
	String getRemoteID();
	
	/**
	 * Sets the model name
	 * @param name the model name
	 */
	void setName( String name );
	
	/** 
	 * Returns the collaborative messaging dialog
	 * @return the {@link RemoteMessagingDialog remote messaging dialog}
	 */
	RemoteMessagingDialog getMessagingDialog();
	
}
