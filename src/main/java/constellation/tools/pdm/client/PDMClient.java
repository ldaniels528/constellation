package constellation.tools.pdm.client;

import java.util.ArrayList;
import java.util.List;

import constellation.tools.pdm.client.commands.PDMCommandFactory;

/**
 * Represents a Product Data Management Client
 * @author lawrence.daniels@gmail.com
 */
public class PDMClient {
	private static final PDMClient instance = new PDMClient();
	private List<PDMModelFile> modelFiles;
	
	/**
	 * Register the PDM remote commands
	 */
	static {
		PDMCommandFactory.init();
	}
	
	/**
	 * Default Constructor
	 */
	private PDMClient() {
		this.modelFiles = new ArrayList<PDMModelFile>(0);
	}
	
	/**
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static PDMClient getInstance() {
		return instance;
	}

	/**
	 * @return the modelFiles
	 */
	public List<PDMModelFile> getModelFiles() {
		return modelFiles;
	}

	/**
	 * @param modelFiles the modelFiles to set
	 */
	public void setModelFiles( final List<PDMModelFile> modelFiles ) {
		this.modelFiles = modelFiles;
		
		System.err.printf( "%d model(s) read\n", modelFiles.size() );
		for( final PDMModelFile file : modelFiles ) {
			System.err.printf( "PDM: %s\n", file.getName() );
		}
		
	}
	
}
