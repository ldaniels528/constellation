package constellation.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Constellation Command Writer
 * @author lawrence.daniels@gmail.com
 */
public class CxCommandWriter {
	private final ByteArrayOutputStream baos;
	private final DataOutputStream buffer;
	private final DataOutputStream out;
	
	/**
	 * Creates a new command writer instance
	 * @param out the given {@link OutputStream output stream}
	 */
	public CxCommandWriter( final OutputStream out ) {
		this.out 	= new DataOutputStream( out ); 
		this.baos	= new ByteArrayOutputStream( 65535 );
		this.buffer	= new DataOutputStream( baos );
	}
	
	/**
	 * Closes the writer
	 * @throws IOException
	 */
	public void close() 
	throws IOException {
		out.close();
	}

	/**
	 * Appends the given command to the stream
	 * @return the {@link CxCommand command}
	 * @throws IOException
	 */
	public void write( final CxCommand command ) 
	throws IOException {
		// reset the buffer
		baos.reset();
		
		// encode the command
		command.encode( buffer );
		
		// flush the buffer
		buffer.flush();
		
		// get the data
		final byte[] data = baos.toByteArray();
		
		// set the length of the command
		command.setLength( data.length );
			
		// write the bytes to the stream
		out.writeInt( data.length );
		out.write( data );
		out.flush();
	}
	
}
