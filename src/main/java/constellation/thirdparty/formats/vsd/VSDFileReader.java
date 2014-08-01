package constellation.thirdparty.formats.vsd;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatException;
import constellation.model.formats.ModelFormatReader;

/**
 * Microsoft VISIO (VSD) File Reader
 * @author lawrence.daniels@gmail.com
 */
public class VSDFileReader implements ModelFormatReader, VSDConstants {
	private static final int DISPLAY_WIDTH 		= 32;
	private static final String DISPLAY_FORMAT	= format( "[%%04X] %%%ds[%%%ds]", -DISPLAY_WIDTH*3, -DISPLAY_WIDTH );
	
	// internal fields
	private final Logger logger = Logger.getLogger( getClass() );
	private final FileFilter fileFilter;
	
	/**
	 * Default constructor
	 */
	public VSDFileReader() {
		this.fileFilter = new VSDFileFilter();
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
	public GeometricModel readFile( final File vsdFile ) 
	throws ModelFormatException {
		RandomAccessFile in = null;
		try {			
			// create a new VSD Model instance
			final VSDModel model = new VSDModel();
			
			// open the VSD file
			in = new RandomAccessFile( vsdFile, "r" );
			
			// notify the administrator
			logger.info( format( "Importing '%s' (length = 0x%04X)...", vsdFile.getName(), in.length() ) );
			
			// is the header of the file valid
			if( !isValidVSD( in ) ) {
				throw new ModelFormatException( format( "File '%s' is not a valid Viso/VSD file", vsdFile.getName() ) );
			}
			
			// get the positions of all of the anchors
			final long rootEntry01 		= scan( in, VSD_ROOT_ENTRY );
			final long rootEntry02 		= scan( in, VSD_ROOT_ENTRY );
			final long visioDoc 		= scan( in, VSD_VISIO_DOCUMENT );
			final long summaryInfo 		= scan( in, VSD_SUMMARY_INFO );
			final long docSmmaryIfno	= scan( in, VSD_DOC_SUMMARY_INFO );
			final long visioInfo 		= scan( in, VSD_VISIO_INFO );
			final long visioDrawing		= scan( in, VSD_VISIO_DRAWING );
			
			// extract information from 'VisoInformation'
			extractVisioInformation( model, in ); 
			
			// extract information from 'VISIO Drawing'
			extractVisioDrawing( model, in ); 
			
			// show data after 'VisioInformation'
			logger.info( "" );
			//displayBinaryData( in, rootEntry01, rootEntry02 );
			//displayBinaryData( in, rootEntry02, visioDoc );
			//displayBinaryData( in, visioDoc, summaryInfo );
			displayBinaryData( in, visioInfo, visioDrawing  );
			//displayBinaryData( in, visioDrawing, in.length() );
			
			logger.info( "model = " + model );
			
			// return the Constellation Model
			return model.toModel();
		}
		catch( final IOException e ) {
			throw new ModelFormatException( e );
		}
		finally {
			if( in != null ) {
				try { in.close(); } catch( Exception e ) { }
			}
		}
	}
	
	/**
	 * Extracts all entries from the 'VisioInformation' section of the file,
	 * and attaches them to the given model.
	 * @param model the given {@link VSDModel model}
	 * @param in the given {@link RandomAccessFile stream}
	 * @throws IOException
	 */
	private void extractVisioInformation( final VSDModel model, final RandomAccessFile in ) 
	throws IOException {		
		// extract the company name
		model.setCompanyName( extractString( extractBinaryData( in, VSD_POS_COMPANY_NAME ) ) );		
		
		// extract the work sheets (48 bytes after Company Name)
		extractElements( model, in, in.getFilePointer() + VSD_OFFSET_WORK_SHEETS );
		
		// Extract the creator name
		model.setAuthorName( extractString( extractBinaryData( in, VSD_POS_USER_NAME ) ) );	
		
		// Extract the .VST file path
		model.setVstPath( extractString( extractBinaryData( in, VSD_POS_VST_PATH ) ) );
	}

	/** 
	 * Extract the information from the 'VISIO Drawing' section
	 * @param model the given {@link VSDModel model}
	 * @param in the given {@link RandomAccessFile file device}
	 */
	private void extractVisioDrawing( final VSDModel model, final RandomAccessFile in) {
		// TODO Auto-generated method stub
		
	}
	
	private void extractElements( final VSDModel model, final RandomAccessFile in, final long position ) 
	throws IOException {
		// go to the start position of the work sheets
		in.seek( position );
		
		boolean done = false;
		do {
			// extract the binary data
			final byte[] bindata = extractBinaryData( in );
			
			// extract the work sheet's name
			final String label = extractString( bindata );
			
			// was there a code present?
			final byte code = bindata[ bindata.length - 1 ];
			
			// is it a work sheet?
			if( code != 0 ) {
				final VSDWorkSheet workSheet = new VSDWorkSheet( label );
				workSheet.setCode( code );
				model.add( workSheet );
			}
			
			// must be an element type
			else {
				//final VSDElementType type = new VSDElementType( label );
				//model.add( type );
				done = true;
			}
			
		} while( !done );
		
	}
	
	/**
	 * Extracts a Work Sheet element from the stream,
	 * and appends it to the given model
	 * @param in the given {@link RandomAccessFile stream}
	 * @return the {@link VSDWorkSheet work sheet}
	 * @throws IOException
	 */
	private VSDWorkSheet extractWorkSheet( final RandomAccessFile in ) 
	throws IOException {
		// extract the binary data
		final byte[] bindata = extractBinaryData( in );
		
		// extract the work sheet's name
		final String label = extractString( bindata );
		logger.info( format( "Worksheet '%s'", label ) );
		
		// create the work sheet
		final VSDWorkSheet workSheet = new VSDWorkSheet( label );
		
		// was there a code present?
		final byte code = bindata[ bindata.length - 1 ];
		if( code != 0 ) {
			workSheet.setCode( code );
		}
		
		// return the work sheet
		return workSheet;
	}

	/**
	 * Retrieves a block of binary data from the stream at the current file position.
	 * @param in the given {@link RandomAccessFile file device}
	 * @return the block of binary data
	 * @throws IOException
	 */
	private byte[] extractBinaryData( final RandomAccessFile in ) 
	throws IOException {
		final long position = in.getFilePointer();
		
		// read the length of the block
		final int length = readLowOrderInt( in );
		
		// read the entire block
		final byte[] block = new byte[length];
		in.read( block );
		
		// display the block
		displayBinaryData( position, block );
		
		// return the block
		return block;
	}
	
	/**
	 * Reads an integer (4-bytes) from the file device with
	 * the first byte as the low order byte.
	 * @param in the given {@link RandomAccessFile file device}
	 * @return an integer
	 * @throws IOException
	 */
	private int readLowOrderInt( final RandomAccessFile in ) 
	throws IOException {
		final int a = in.read();
		final int b = in.read();
		final int c = in.read();
		final int d = in.read();
		return ( d << 24 ) | ( c << 16 ) | ( b << 8 ) | a;
	}
	
	/**
	 * Retrieves a block of binary data from the stream at the current file position.
	 * @param in the given {@link RandomAccessFile input stream}
	 * @return the block of binary data
	 * @throws IOException
	 */
	private byte[] extractBinaryData( final RandomAccessFile in, final long position ) 
	throws IOException {
		// seek the given position
		in.seek( position );
		
		// return the block
		return extractBinaryData( in );
	} 
	
	private byte[] extractBinaryData( final RandomAccessFile in, final long position, final int size ) 
	throws IOException {
		// seek the given position
		in.seek( position );
		
		// create a buffer 
		final byte[] buf = new byte[ size ];
		
		// read the the data into the buffer
		int count = in.read( buf );
		logger.info( format( "Retrieved %d of %d bytes from [%04X:%05d]", count, size, position, position ) );
		
		// return the buffer
		return buf;
	}

	/**
	 * Retrieves a string from the binary block of data
	 * @param in the given binary block of data
	 * @throws IOException
	 */
	private String extractString( final byte[] block ) 
	throws IOException {
		// fail-safe
		if( block.length == 0 ) {
			return null;
		}
		
		// extract the string
		final StringBuilder sb = new StringBuilder();
		int n = 0;
		byte b;		
		while( ( b = block[n++] ) != 0x00 ) {
			sb.append( (char)b );
		}
		return sb.toString();
	}

	/**
	 * Validates the first 8 bytes of the file to validate
	 * whether the file is a VSD file. 
	 * @param in the given {@link RandomAccessFile input stream}
	 * @return true, if the first 8 bytes are "D0 CF 11 E0 A1 B1 1A E1"
	 * @throws IOException
	 */
	private boolean isValidVSD( final RandomAccessFile in ) 
	throws IOException {
		// create the header buffer
		final byte[] header = new byte[8];
		
		// read the head bytes
		in.read( header );
		
		// check each byte
		for( int n = 0; n < header.length; n++ ) {
			if( header[n] != VSD_HEADER[n] ) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Scans the file for the given sequence
	 * @param in the given {@link RandomAccessFile file device}
	 * @param sequence the given sequence
	 * @return the position of the sequence if found, or <code>-1</code> if not found.
	 * @throws IOException
	 */
	private long scan( final RandomAccessFile in, final String sequence ) 
	throws IOException {
		boolean found = false;
		boolean eof = false;
		long position = 0;
		
		// get the length of the file
		final long length = in.length();
	
		// encode the sequence
		final byte[] encodedSeq = zeroEncodeString( sequence );
		
		do {
			// continue reading until the first byte of the sequence is found...
			while( ( in.getFilePointer() + 1 < length ) && ( in.readByte() != encodedSeq[0] ) ) { }
			
			// then reach for the request of the sequence
			int n = 1;
			while( ( in.getFilePointer() + 1 < length ) && ( n < encodedSeq.length ) && ( ( in.readByte() == encodedSeq[n++] ) ) ) { }
			
			// are we at the end of file
			eof = ( in.getFilePointer() + 1 >= length );
			if( eof ) {
				logger.info( format( "Sequence '%s' not found before EOF", sequence ) );
			}
			
			// was the complete sequence found?
			found = ( n == encodedSeq.length );
			if( found ) {
				position = in.getFilePointer() - encodedSeq.length;
				logger.info( format( "Sequence '%s' found at 0x%04X", sequence, position ) );
			}
			
		} while( !eof && !found );
		
		return !eof ? position : length;
	}

	private void displayBinaryData( final RandomAccessFile in, final long startpos, final long endpos ) 
	throws IOException {
		// create buffer
		final byte[] buf = new byte[DISPLAY_WIDTH];
		
		// seek the start position
		in.seek( startpos );
		
		// read the file
		long position = in.getFilePointer();
		int block = 0;
		int total = 0;
		int count;
		while( ( in.getFilePointer() < endpos )  && ( count = in.read( buf ) ) != -1 ) {
			block++;
			total += count;
			displayBinaryData( position, buf );
			position = in.getFilePointer();
			
			if( buf.length != count ) {
				logger.warn( format( "Only retrieved %d out of %d bytes", count, buf.length ) );
			}
			
			if( in.getFilePointer() >= endpos ) {
				return;
			}
		}
		
		logger.info( format( "Blocks read: %d, Bytes read: %d", block, total ) );
	}

	private void displayBinaryData( final long filePos, final byte[] data ) {
		final StringBuilder sb1 = new StringBuilder( data.length * 3 );
		final StringBuilder sb2 = new StringBuilder( data.length );
	
		int count = 0;
		for( int n = 0; n < data.length; n++ ) {
			// only display the maximum width at a time
			if( ++count > DISPLAY_WIDTH ) {
				logger.info( format( DISPLAY_FORMAT, filePos, sb1, sb2 ) );
				sb1.delete( 0, sb1.length() );
				sb2.delete( 0, sb2.length() );
				count = 1;
			}
			
			// append the next byte
			sb1.append( format( "%02X ", data[n] ) );
			sb2.append( format( "%c", data[n] >= 32 && data[n] <= 127 ? (char)data[n] : '.' ) );
		}
		logger.info( format( DISPLAY_FORMAT, filePos, sb1, sb2 ) );
	}

	private int skipZeroes( final RandomAccessFile in ) 
	throws IOException {
		int count = 0;
		while( in.read() == 0 ) { count++; }
		in.seek( in.getFilePointer() - 1 );
		return count;
	}
	
	/**
	 * Zero encodes a text string
	 * @param string the given text string
	 * @return the zero encoded byte array
	 */
	private static byte[] zeroEncodeString( final String string ) {
		final char[] chars = string.toCharArray();
		final byte[] bytes = new byte[ chars.length ];
		for( int n = 0; n < chars.length; n++ ) {
			bytes[n] = ( chars[n] == '.' ) ? 0 : (byte)chars[n];
		}
		return bytes;
	}
		

}
