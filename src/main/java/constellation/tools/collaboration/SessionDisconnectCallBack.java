package constellation.tools.collaboration;

/**
 * This interface is implemented by classes wishing to
 * track when a collaborative session is closed.
 * @author lawrence.daniels@gmail.com
 */
public interface SessionDisconnectCallBack {
	
	/**
	 * Called when the session is closed.
	 */
	void disconnected();

}
