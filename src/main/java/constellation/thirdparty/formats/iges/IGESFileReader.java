package constellation.thirdparty.formats.iges;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.model.formats.ModelFormatReader;
import constellation.thirdparty.formats.iges.elements.IGESElement;

/**
 * <h4>IGES File Reader</h4>
 * An IGES file is composed of 80-character ASCII records, a record length 
 * derived from the punch card era. Text strings are represented in "Hollerith" 
 * format, the number of characters in the string, followed by the letter "H", 
 * followed by the text string, e.g., "4HSLOT" (this is the text string format 
 * used in early versions of the Fortran language). Early IGES translators had 
 * problems with IBM mainframe computers because the mainframes used EBCDIC 
 * encoding for text, and some EBCDIC-ASCII translators would either substitute 
 * the wrong character, or improperly set the Parity bit, causing a misread.
 * <br><a href="http://en.wikipedia.org/wiki/IGES">IGES Reference</a>
 * @author lawrence.daniels@gmail.com
 */
public class IGESFileReader implements ModelFormatReader {
	private final FileFilter fileFilter;
	
	/**
	 * Default constructor
	 */
	public IGESFileReader() {
		this.fileFilter = new IGESFileFilter();
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
	public GeometricModel readFile( final File igesFile ) 
	throws ModelFormatException {
		try {			
			// create a new IGES Model instance
			final IGESModel igesModel = new IGESModel();
			
			// read the lines from the file
			final LineCollection lines = LineCollection.readLines( igesFile );
			
			// parse each line
			while( lines.hasNext() ) {
				// parse the IGES element
				final IGESElement element = IGESElement.parse( lines );
				
				// add the element to the model
				igesModel.add( element );
			}
			
			// return the CAD Model
			return igesModel.toModel();
		}
		catch( final IOException e ) {
			throw new ModelFormatException( e );
		}
	}
	
}
