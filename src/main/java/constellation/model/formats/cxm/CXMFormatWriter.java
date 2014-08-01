package constellation.model.formats.cxm;

import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatWriter;

/**
 * This class is responsible for writing Constant Projects (XML) to disk
 * @author lawrence.daniels@gmail.com
 */
public class CXMFormatWriter implements ModelFormatWriter {
	private final FileFilter fileFilter;
	
	/**
	 * Private constructor
	 */
	public CXMFormatWriter() {
		this.fileFilter = new CXMFileFilter();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.CxModelFormatWriter#getFileFilter()
	 */
	public FileFilter getFileFilter() {
		return fileFilter;
	}
	
	/**
	 * Saves the given project to disk
	 * @param project the given {@link GeometricModel model}
	 * @throws IOException
	 */
	public void writeFile( final GeometricModel model ) 
	throws IOException {
		CxNativeXMLModelWriter.writeFile( model );
	}
	
}
