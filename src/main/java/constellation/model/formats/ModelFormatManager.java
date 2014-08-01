package constellation.model.formats;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.filechooser.FileFilter;

import constellation.model.formats.cxm.CXMFormatReader;
import constellation.model.formats.cxm.CXMFormatWriter;

/**
 * Constellation Model Format Manager
 * @author lawrence.daniels@gmail.com
 */
public class ModelFormatManager {
	private static final ModelFormatManager instance = new ModelFormatManager();
	private final ModelFormatReader defaultReader = new CXMFormatReader();
	private final ModelFormatWriter defaultWriter = new CXMFormatWriter();
	private final Collection<ModelFormatReader> readers;
	private final Collection<ModelFormatWriter> writers;
	
	/**
	 * Default constructor
	 */
	private ModelFormatManager() {
		this.readers = new LinkedHashSet<ModelFormatReader>();
		this.writers = new LinkedHashSet<ModelFormatWriter>();
		
		// add the default format reader
		readers.add( defaultReader );
		
		// add the default format writer
		writers.add( defaultWriter );
	}
	
	/**
	 * Returns the singleton instance of this class
	 * @return the singleton instance of this class
	 */
	public static ModelFormatManager getInstance() {
		return instance;
	}
	
	/** 
	 * Adds a new model format reader to this manager
	 * @param reader the given {@link ModelFormatReader format reader}
	 */
	public void add( final ModelFormatReader reader ) {
		readers.add( reader );
	}
	
	/** 
	 * Adds a new model format writer to this manager
	 * @param reader the given {@link ModelFormatWriter format writer}
	 */
	public void add( final ModelFormatWriter writer ) {
		writers.add( writer );
	}
	
	/**
	 * Returns the default model format reader
	 * @return the default {@link ModelFormatReader model format reader}
	 */
	public ModelFormatReader getDefaultReader() {
		return defaultReader;
	}
	
	/**
	 * Returns the default model format writer
	 * @return the default {@link ModelFormatWriter model format writer}
	 */
	public ModelFormatWriter getDefaultWriter() {
		return defaultWriter;
	}
	
	/**
	 * Attempts to find the appropriate format reader
	 * for the given model file.
	 * @param modelFile the given {@link File model file}
	 * @return the {@link ModelFormatReader format reader}
	 */
	public ModelFormatReader getFormatReader( final File modelFile ) {
		// look for the appropriate format reader
		for( final ModelFormatReader format : readers ) {
			// is the format compatible with the file?
			if( format.isCompatible( modelFile ) ) {
				return format;
			}
		}
		
		// no suitable reader found
		return null;
	}
	
	/**
	 * Returns the file filters for the associated format readers
	 * @return the {@link FileFilter file filters}
	 */
	public FileFilter[] getReaderFilters() {
		final FileFilter[] filters = new FileFilter[ readers.size() ]; 
		int n = 0;
		for( final ModelFormatReader reader : readers ) {
			filters[n++] = reader.getFileFilter();
		}
		return filters;
	}
	
	/**
	 * Returns the file filters for the associated format writers
	 * @return the {@link FileFilter file filters}
	 */
	public FileFilter[] getWriterFilters() {
		final FileFilter[] filters = new FileFilter[ writers.size() ]; 
		int n = 0;
		for( final ModelFormatWriter writer : writers ) {
			filters[n++] = writer.getFileFilter();
		}
		return filters;
	}

}
