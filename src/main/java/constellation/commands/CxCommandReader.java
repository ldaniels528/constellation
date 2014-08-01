package constellation.commands;

import static java.lang.String.format;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * Constellation Command Reader
 * @author lawrence.daniels@gmail.com
 */
public class CxCommandReader {
	private final Logger logger = Logger.getLogger( getClass() );
	private final CxCommandManager ccm;
	private final DataInputStream in;
	
	/**
	 * Creates a new command reader instance
	 * @param in the given {@link InputStream input stream}
	 */
	public CxCommandReader( final InputStream in ) {
		this.ccm	= CxCommandManager.getInstance();
		this.in 	= new DataInputStream( new BufferedInputStream( in, 1024 ) );
	}
	
	/**
	 * Closes the reader
	 * @throws IOException
	 */
	public void close() 
	throws IOException {
		in.close();
	}
	
	/**
	 * Reads the next command from the stream
	 * @return the {@link CxCommand command}
	 * @throws IOException
	 */
	public CxCommand read() 
	throws IOException {		
		// if no bytes are available, return null
		if( in.available() == 0 ) {
			return null;
		}
		
		// read the length of the next instruction
		final int length = in.readInt();
		
		// wait until at least 'n' bytes are in the stream
		int ticker = 0;
		while( in.available() < length ) {
			// every 30 seconds
			if( ticker++ % 1200 == 0 ) {
				logger.info( format( "%d (of %d) bytes available...", in.available(), length ) );
			}
			
			// sleep 25 msec
			try { Thread.sleep( 25 ); } 
			catch( final InterruptedException e ) {
				logger.error( format( "Interrupted while awaiting %d bytes from stream", length ), e );
				return null;
			}
		}
		
		// is there enough data to read the next command?
		final byte[] block = new byte[ length ];
		in.read( block );
		
		// wrap the block in a byte buffer
		final ByteBuffer buffer = ByteBuffer.allocate( length );
		buffer.put( block );
		buffer.rewind();
		
		// get the decoder
		final CxCommandDecoder decoder = ccm.getDecoder( buffer );
		
		// decode the opCode into a command
		final CxCommand command = decoder.decode( buffer );
		command.setLength( length );
		
		// return the command 
		return command;
	}
		
}
