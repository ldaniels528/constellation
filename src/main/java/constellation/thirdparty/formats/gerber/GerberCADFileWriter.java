package constellation.thirdparty.formats.gerber;

import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatWriter;

/**
 * GERBER CAD File Writer
 * @author lawrence.daniels@gmail.com
 */
public class GerberCADFileWriter implements ModelFormatWriter {
	private final GerberCADFileFilter fileFilter;
	
	/**
	 * Default Constructor
	 */
	public GerberCADFileWriter() {
		this.fileFilter = new GerberCADFileFilter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public FileFilter getFileFilter() {
		return fileFilter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeFile( final GeometricModel model ) 
	throws IOException {
		
		// TODO handle model persistence logic
		
	}

}
