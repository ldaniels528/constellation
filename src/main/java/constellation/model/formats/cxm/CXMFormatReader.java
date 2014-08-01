package constellation.model.formats.cxm;

import static java.lang.String.format;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.model.formats.ModelFormatReader;

/**
 * This class is responsible for reading Constellation geometry models (XML) from disk
 * @author lawrence.daniels@gmail.com
 */
public class CXMFormatReader implements ModelFormatReader {
	public static final String EXTENSION = "cxm";
	private final CXMFileFilter fileFilter;
	
	/**
	 * Default constructor
	 */
	public CXMFormatReader() {
		this.fileFilter = new CXMFileFilter();
	}

	/**
	 * Repairs the given file by adding the file extension (if needed)
	 * @param file the given {@link File model file} 
	 * @return the repaired {@link File file} reference
	 */
	public static File fixFilePath( final File file ) {
		return file.getName().contains( "." )
					? file
					: new File( format( "%s.%s", file.getAbsolutePath(), EXTENSION ) );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.CxModelFormatReader#getFileFilter()
	 */
	public FileFilter getFileFilter() {
		return fileFilter;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.CxModelFormatReader#isCompatible(java.io.File)
	 */
	public boolean isCompatible( final File file ) {
		return file.getName().toLowerCase().endsWith( format( ".%s", EXTENSION ) );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.geometry.model.formats.CxModelFormatReader#readFile(java.io.File)
	 */
	public GeometricModel readFile( final File modelFile )
	throws ModelFormatException {
		try {
			return CxNativeXMLModelReader.readFile( modelFile );
		} 
		catch( final Exception e ) {
			e.printStackTrace();
			throw new ModelFormatException( e );
		}
	}
	
}
