package constellation.commands;

import java.io.DataOutputStream;
import java.io.IOException;

import constellation.model.GeometricModel;

/**
 * Constellation Command
 * @author lawrence.daniels@gmail.com
 */
public interface CxCommand {
	
	/**
	 * Writes the command to the given data stream
	 * @param stream the given {@link DataOutputStream data stream}
	 * @throws IOException 
	 */
	void encode( DataOutputStream stream ) throws IOException;
	
	/** 
	 * Evaluate the command
	 * @param model the given {@link GeometricModel model}
	 */
	void evaluate( GeometricModel model );
	
	/**
	 * Returns the length of the command
	 * @return the length of the command
	 */
	int getLength();
	
	/**
	 * Sets the length of the command
	 * @param length the length of the command
	 */
	void setLength( int length );

}
