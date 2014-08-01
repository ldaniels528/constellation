package constellation.ui.components.choosers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import constellation.model.formats.ModelFormatManager;

/**
 * Constellation File Chooser
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxFileChooser extends JFileChooser {
	private static final ModelFormatManager formatManager = ModelFormatManager.getInstance();
	
	/**
	 * Default Constructor
	 */
	public CxFileChooser( final File parentDirectory ) {
		super( parentDirectory );
		super.setFileSelectionMode( FILES_AND_DIRECTORIES );
	}
	
	/**
	 * Creates an "Open" file chooser instance
	 * @param parentDirectory the given {@link File parent directory}
	 * @return the {@link CxFileChooser file chooser}
	 */
	public static CxFileChooser createOpenFileChooser( final File parentDirectory ) {
		// create a file chooser instance
		final CxFileChooser chooser = new CxFileChooser( parentDirectory );
		
		// add the file filters for the supported formats
		final FileFilter[] fileFilters = formatManager.getReaderFilters();
		for( final FileFilter fileFilter : fileFilters ) {
			chooser.addChoosableFileFilter( fileFilter );
		}
		
		// preselect the Model file filter
		chooser.setFileFilter( formatManager.getDefaultReader().getFileFilter() );
		return chooser;
	}
	
	/**
	 * Creates a "Save As" file chooser instance
	 * @param parentDirectory the given {@link File parent directory}
	 * @return the {@link JFileChooser file chooser}
	 */
	public static CxFileChooser createSaveAsFileChooser( final File parentDirectory ) {
		// create a file chooser instance
		final CxFileChooser chooser = new CxFileChooser( parentDirectory );
		
		// add the file filters for the supported formats
		final FileFilter[] fileFilters = formatManager.getWriterFilters();
		for( final FileFilter fileFilter : fileFilters ) {
			chooser.addChoosableFileFilter( fileFilter );
		}
		
		// preselect the Model file filter
		chooser.setFileFilter( formatManager.getDefaultWriter().getFileFilter() );
		return chooser;
	}
	

}
