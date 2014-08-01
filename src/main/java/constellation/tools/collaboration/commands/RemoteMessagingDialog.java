package constellation.tools.collaboration.commands;

/**
 * Represents an agent that facilitates messaging 
 * between two or more remote peers.
 * @author lawrence.daniels@gmail.com
 */
public interface RemoteMessagingDialog {

	/** 
	 * Appends the given message to the output pane of the dialog
	 * @param message the given message
	 */
	void appendMessage( String message );
	
	/**
	 * Insures that the messaging agent is visible
	 */
	void makeVisible();
	
}
