package constellation.thirdparty.formats.iges;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Represents a collection of lines from a text file
 * @author lawrence.daniels@gmail.com
 */
public class LineCollection {
	private LinkedList<String> lines;
	
	/**
	 * Default constructor
	 */
	public LineCollection() {
		this.lines = new LinkedList<String>();
	}
	
	/** 
	 * Reads the lines from the given reader
	 * @param reader the given {@link BufferedReader reader}
	 * @return the {@link LineCollection collection} of {@link String lines}
	 * @throws IOException
	 */
	public static LineCollection readLines( final File textFile ) 
	throws IOException {
		BufferedReader reader = null;
		try {
			// open the file
			reader = new BufferedReader( new FileReader( textFile ) );
			
			// create a container for the lines
			final LineCollection lines = new LineCollection();
			
			// parse each line
			String line;
			while( ( line = reader.readLine() ) != null ) {
				lines.add( line );
			}
			
			return lines;
		}
		finally {
			if( reader != null ) {
				try { reader.close(); } catch( Exception e ) { }
			}
		}
	}

	/** 
	 * Adds the given line to the collection
	 * @param line the given text line
	 */
	public void add( final String line ) {
		lines.add( line );
	}
	
	/** 
	 * Indicates whether at least one more line exists in the collection
	 * @return true, if at least one more line exists in the collection
	 */
	public boolean hasNext() {
		return !lines.isEmpty();
	}
	
	/** 
	 * Returns the next line in the list
	 * @return the next line in the list
	 */
	public String next() {
		return lines.removeFirst();
	}

	/** 
	 * Returns the next line in the list without removing it
	 * @return the next line in the list
	 */
	public String peek() {
		return lines.getFirst();
	}
	
}
