package constellation.thirdparty.formats.gerber;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import constellation.model.DefaultGeometricModel;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.model.formats.ModelFormatReader;

/**
 * GERBER CAD File Reader
 * @author lawrence.daniels@gmail.com
 */
public class GerberCADFileReader implements ModelFormatReader {
	private final GerberCADFileFilter fileFilter;
	
	/**
	 * Default Constructor
	 */
	public GerberCADFileReader() {
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
	public boolean isCompatible( final File file ) {
		return fileFilter.accept( file );
	}

	/**
	 * {@inheritDoc}
	 */
	public GeometricModel readFile( final File file ) 
	throws ModelFormatException {
		// create a new model
		final DefaultGeometricModel model = DefaultGeometricModel.newModel();
		
		// TODO add translation logic here
		
		// return the model
		return model;
	}

}
