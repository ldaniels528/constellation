package constellation.tools.pdm.client;

/**
 * Represents an observer of the Product Data Management Client
 * @author lawrence.daniels@gmail.com
 */
public interface PDMClientObserver {
	
	/**
	 * Called when the PDM model list has been updated
	 * @param client the given {@link PDMClient PDM Client}
	 */
	void update( PDMClient client );

}
