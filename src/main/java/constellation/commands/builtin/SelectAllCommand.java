package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.*;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/** 
 * "Select All" Command
 * @author lawrence.daniels@gmail.com
 */
public class SelectAllCommand extends AbstractCommand {
	private static SelectAllCommand instance = new SelectAllCommand();
	
	/**
	 * Creates a new "Select All" command
	 */
	private SelectAllCommand() {
		super();
	}
	
	/**
	 * Creates a new "Select All" command
	 * @return a new {@link SelectAllCommand "Select All" command}
	 */
	public static SelectAllCommand create() {
		return instance;
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream stream ) 
	throws IOException {
		stream.writeInt( SEL_ALL );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		model.selectAll();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] SELECT ALL", SEL_ALL );
	}

}