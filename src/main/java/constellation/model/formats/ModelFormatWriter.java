package constellation.model.formats;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;

/**
 * Constellation Model Format Writer
 * @author lawrence.daniels@gmail.com
 */
public interface ModelFormatWriter {
	
	/**
	 * Returns the file filter for the format(s) understood
	 * by this format reader.
	 * @return the {@link FileFilter file filter}
	 */
	FileFilter getFileFilter();
	
	/**
	 * Writes the model file to disk
	 * @param modelFile the given project {@link File file}
	 * @return the {@link GeometricModel model} 
	 * @throws IOException 
	 */
	void writeFile( GeometricModel model ) 
	throws IOException;

}
