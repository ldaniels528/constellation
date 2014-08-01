package constellation.commands.builtin;

import constellation.commands.CxCommand;

/** 
 * Represents a generic Constellation Command
 * @author lawrence.daniels@gmail.com
 */
public abstract class AbstractCommand implements CxCommand {
	private int length;
	
	/**
	 * Default constructor
	 */
	protected AbstractCommand() {
		super();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommand#getLength()
	 */
	public int getLength() {
		return length;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.CxCommand#setLength(int)
	 */
	public void setLength( final int length ) {
		this.length = length;
	}
	
}
