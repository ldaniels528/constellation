package constellation.tools.pdm.client.commands;

import static constellation.tools.pdm.client.commands.PDMCommandFactory.PDM_VERSION;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.commands.builtin.AbstractCommand;
import constellation.model.GeometricModel;

/**
 * Constellation Server PDM Version Command
 * @author lawrence.daniels@gmail.com
 */
public class PDMVersionCommand extends AbstractCommand {
	private final int majorVersion;
	private final int minorVersion;
	
	/**
	 * Creates a new version command instance
	 */
	PDMVersionCommand( final int majorVersion, final int minorVersion ) {
		this.majorVersion	= majorVersion;
		this.minorVersion	= minorVersion;
	}
	
	/**
	 * Creates a new version command instance
	 */
	public static PDMVersionCommand create( final int majorVersion,
						   	  				final int minorVersion ) {
		return new PDMVersionCommand( majorVersion, minorVersion );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.pdm.server.io.PDMCommand#encode()
	 */
	public void encode( final DataOutputStream buffer ) 
	throws IOException {
		// write the opCode
		buffer.writeInt( PDM_VERSION );
		
		// write the major & minor version
		buffer.writeShort( (short)majorVersion );
		buffer.writeShort( (short)minorVersion );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommand#evaluate(constellation.model.GeometricModel)
	 */
	public void evaluate( final GeometricModel model ) {
		// this method is unused
		throw new IllegalStateException( format( "%s does not support evaluation", getClass().getSimpleName() ) );
	}

	/**
	 * Returns the major version
	 * @return the major version
	 */
	public int getMajorVersion() {
		return majorVersion;
	}

	/**
	 * Returns the minor version
	 * @return the minor version
	 */
	public int getMinorVersion() {
		return minorVersion;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return format( "[%04X] VERSION %d.%d", PDM_VERSION, majorVersion, minorVersion );
	}
	
}