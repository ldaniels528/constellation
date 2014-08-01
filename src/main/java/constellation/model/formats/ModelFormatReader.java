package constellation.model.formats;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;

/**
 * Constellation Model Format Reader
 * @author lawrence.daniels@gmail.com
 */
public interface ModelFormatReader {
	
	/**
	 * Returns the file filter for the format(s) understood
	 * by this format reader.
	 * @return the {@link FileFilter file filter}
	 */
	FileFilter getFileFilter();

	/** 
	 * Determines whether the given file is compatible with this reader
	 * @param file the given {@link File file}
	 * @return true, if the file's extension is '.igs' or '.iges'
	 */
	boolean isCompatible( File file );
	
	/**
	 * Loads the model file from disk
	 * @param modelFile the given project {@link File file}
	 * @return the {@link GeometricModel model} 
	 * @throws ModelFormatException 
	 */
	GeometricModel readFile( File modelFile ) 
	throws ModelFormatException;

}
