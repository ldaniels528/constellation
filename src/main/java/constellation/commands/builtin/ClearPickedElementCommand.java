package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/** 
 * Clear Picked Elements Command
 * @author lawrence.daniels@gmail.com
 */
public class ClearPickedElementCommand extends AbstractCommand {
	private static ClearPickedElementCommand instance = new ClearPickedElementCommand();

	/**
	 * Creates a new "Clear Temporary Element" command
	 */
	private ClearPickedElementCommand() {
		super();
	}
	
	/**
	 * Creates a new "Clear Picked Elements" command
	 * @return a new {@link ClearPickedElementCommand "Clear Picked Elements" command}
	 */
	public static ClearPickedElementCommand create() {
		return instance;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		stream.writeInt( CLR_PICK );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.clearPickedElement();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] CLEAR PICKED", CLR_PICK );
	}

}