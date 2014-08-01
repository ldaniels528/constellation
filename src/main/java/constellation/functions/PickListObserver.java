package constellation.functions;

import constellation.ApplicationController;
import constellation.drawing.elements.ModelElement;

/**
 * This interface should be implemented by classes who want to be
 * notified of pick list events
 * @author lawrence.daniels@gmail.com
 */
public interface PickListObserver {
	
	/**
	 * This call back method is invoked upon the selection
	 * of a geometric element  
	 * @param controller the given {@link ApplicationController controller}
	 * @param selectedElement the selected {@link ModelElement drawing element}
	 */
	void elementSelected( ApplicationController controller, ModelElement selectedElement );

}
