package constellation.app.functions;

import constellation.ApplicationController;


/**
 * This interface is implemented by all classes
 * that are interested in receiving input from
 * the user.
 * @param DADATA_TYPE the expected data type of the user input
 * @author lawrence.daniels@gmail.com
 */
public interface UserInputObserver<DATA_TYPE> {

	/**
	 * This method is called when the {@link UserInputDialog User Input Dialog}
	 * receives input from the user.
	 * @param controller the given {@link ApplicationController controller}
	 * @param inputData the given user inputed {@link Object data}
	 */
	void update( ApplicationController controller, DATA_TYPE inputData );
	
}
