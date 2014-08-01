package constellation.tools.pdm.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import constellation.tools.pdm.client.PDMModelFile;
import constellation.tools.pdm.client.PDMModelFileStatus;

/**
 * Constellation Product Data Management (PDM) Database 
 * @author lawrence.daniels@gmail.com
 */
public class PDMDatabase {
	private static PDMDatabase instance = new PDMDatabase();
	
	/**
	 * Private constructor
	 */
	private PDMDatabase() {
		super();
	}
	
	/**
	 * Returns the singleton instance of the PDM database
	 * @return the singleton instance
	 */
	public static PDMDatabase getInstance() {
		return instance;
	}
	
	/**
	 * Returns the list of available PDM files
	 * @return a collection of {@link PDMModelFile PDM Model files}
	 */
	@SuppressWarnings("deprecation")
	public List<PDMModelFile> getFileList() {
		final List<PDMModelFile> list = new LinkedList<PDMModelFile>();
		
		PDMModelFile file;
		
		// model #1
		file = new PDMModelFile();
		file.setPdmFileId( 1 );
		file.setName( "Cube" );
		file.setCreatedBy( "slovett" );
		file.setCreatedTime( new Date( "03/01/2009" ) );
		file.setLastModifiedBy( "ldaniels" );
		file.setLastModifiedTime( new Date( "05/24/2009" ) );
		file.setStatus( PDMModelFileStatus.AVAILABLE );
		list.add( file );
		
		// model #2
		file = new PDMModelFile();
		file.setPdmFileId( 2 );
		file.setName( "Circles" );
		file.setCreatedBy( "ldaniels" );
		file.setCreatedTime( new Date( "03/11/2008" ) );
		file.setLastModifiedBy( "slovett" );
		file.setLastModifiedTime( new Date( "05/24/2009" ) );
		file.setStatus( PDMModelFileStatus.CHECKED_OUT );
		list.add( file );
		return list;
	}

}
