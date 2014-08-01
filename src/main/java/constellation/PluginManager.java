package constellation;

import javax.swing.JComponent;

/** 
 * Constellation Plug-in Manager
 * @author lawrence.daniels@gmail.com
 */
public interface PluginManager {
	
	/**
	 * Sets the plug-in component
	 * @param plugin the given {@link JComponent plug-in component}
	 */
	void setPlugin( final JComponent plugin );
	
	/**
	 * Unloads the current plug-in
	 */
	void unloadPlugin();

}
