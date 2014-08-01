package constellation.commands;

import java.util.Collection;

import constellation.model.GeometricModel;

/**
 * Represents a Virtual Model
 * @author lawrence.daniels@gmail.com
 */
public interface VirtualModel extends GeometricModel {
	
	/** 
	 * Closes the virtual connection
	 */
	void close();
	
	/** 
	 * Returns the host model
	 * @return the {@link GeometricModel model}
	 */
	GeometricModel getHostModel();
	
	/** 
	 * Queues the given commands for consumption by the host model
	 * @param commands the given collection of {@link CxCommand commands}
	 */
	void queue( Collection<CxCommand> commands );
	
	/** 
	 * Queues the given array of command(s) for consumption by the host model
	 * @param commands the the given {@link CxCommand command(s)}
	 */
	void queue( CxCommand ... commands );

}
