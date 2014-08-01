package constellation.commands.builtin;

import static constellation.commands.CxCommandManager.WAIT;
import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/** 
 * Time Delay Command
 * @author lawrence.daniels@gmail.com
 */
public class WaitCommand extends AbstractCommand {
	private final long delay;

	/**
	 * Creates a new time delay command
	 * @param delay the delay time in milliseconds
	 */
	WaitCommand( final long delay ) {
		this.delay = delay;
	}
	
	/** 
	 * Creates a new Time Delay command
	 * @param delay the delay time in milliseconds
	 * @return the {@link WaitCommand command}
	 */
	public static WaitCommand create( final long delay ) {
		return new WaitCommand( delay );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void encode( final DataOutputStream buf ) 
	throws IOException {
		buf.writeInt( WAIT );						
		buf.writeLong( delay );
	}

	/** 
	 * {@inheritDoc}
	 */
	public void evaluate( final GeometricModel model ) {
		try {
			Thread.sleep( delay );
		}
		catch( final InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString() {
		return format( "[%04X] WAIT '%d msec'", WAIT, delay );
	}

}
