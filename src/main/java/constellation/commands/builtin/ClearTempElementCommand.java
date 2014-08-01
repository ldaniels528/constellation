package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.CLR_TEMP;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/** 
 * Clear Temporary Element Command
 * @author lawrence.daniels@gmail.com
 */
public class ClearTempElementCommand extends AbstractCommand {
	private static ClearTempElementCommand instance = new ClearTempElementCommand();

	/**
	 * Creates a new "Clear Temporary Element" command
	 */
	private ClearTempElementCommand() {
		super();
	}
	
	/**
	 * Creates a new "Clear Temporary Element" command
	 * @return a new {@link ClearTempElementCommand "Clear Temporary Element" command}
	 */
	public static ClearTempElementCommand create() {
		return instance;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		stream.writeInt( CLR_TEMP );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.clearTemporaryElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] CLEAR TEMP", CLR_TEMP );
	}

}
