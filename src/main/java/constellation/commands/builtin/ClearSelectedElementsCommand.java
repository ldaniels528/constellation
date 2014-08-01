package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.CLR_SEL;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/** 
 * Clear Selected Elements Command
 * @author lawrence.daniels@gmail.com
 */
public class ClearSelectedElementsCommand extends AbstractCommand {
	private static ClearSelectedElementsCommand instance = new ClearSelectedElementsCommand();
	
	/**
	 * Creates a new "Clear Temporary Element" command
	 */
	private ClearSelectedElementsCommand() {
		super();
	}
	
	/**
	 * Creates a new "Clear Selected Elements" command
	 * @return a new {@link ClearSelectedElementsCommand "Clear Selected Elements" command}
	 */
	public static ClearSelectedElementsCommand create() {
		return instance;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		stream.writeInt( CLR_SEL );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.clearSelectedElements();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] CLEAR SELECTED", CLR_SEL );
	}

}