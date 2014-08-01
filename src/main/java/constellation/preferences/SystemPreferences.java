package constellation.preferences;

import java.awt.Color;
import java.awt.MenuItem;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JMenuItem;

import constellation.ApplicationController;

/**
 * Constellation System Preferences
 * @author lawrence.daniels@gmail.com
 */
public interface SystemPreferences {
	
	/** 
	 * Initializes the system preferences
	 * @param controller the given {@link ApplicationController controller}
	 */
	void init( ApplicationController controller );
	
	/** 
	 * Persist the preferences to disk
	 * @throws IOException
	 */
	void save() throws IOException;
	
	/////////////////////////////////////////////////////////////////////
	//		Frame Dimension Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the width of the application frame
	 * @return the width
	 */
	int getWidth();
	
	/**
	 * Sets the width of the application frame
	 * @param width the width to set
	 */
	void setWidth( int width ) ;

	/**
	 * Returns the height of the application frame
	 * @return the height
	 */
	int getHeight();

	/**
	 * Sets the height of the application frame
	 * @param height the height to set
	 */
	void setHeight( int height );
	
	/////////////////////////////////////////////////////////////////////
	//		Third-Party Module Methods
	/////////////////////////////////////////////////////////////////////

	/** 
	 * Adds a third party module to the preferences
	 * @param thirdPartyModule the collection of {@link ThirdPartyModuleInfo third party modules}
	 */
	void addThirdPartyModule( ThirdPartyModuleInfo thirdPartyModule );
	
	/** 
	 * Returns the collection of third party modules
	 * @return the collection of {@link ThirdPartyModuleInfo third party modules}
	 */
	Collection<ThirdPartyModuleInfo> getThirdPartyModules();
	
	/** 
	 * Returns the collection of third party menu items
	 * @return the collection of third party {@link MenuItem menu items}
	 */
	Collection<JMenuItem> getThirdPartyMenuItems( ApplicationController controller ); 
	
	////////////////////////////////////////////////////////////////////
	//		Display Preference-related Methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Indicates whether anti-aliasing is turned on
	 * @return true, if anti-aliasing is turned on
	 */
	boolean isAntiAliasing();
	
	/** 
	 * Turns anti-aliasing on/off
	 * @param on if true, anti-aliasing is turned off, otherwise turned off
	 */
	void setAntiAliasing( boolean on );
	
	/** 
	 * Returns the axis color
	 * @return the axis {@link Color color}
	 */
	Color getAxisColor();
	
	/** 
	 * Sets the axis color
	 * @param color the axis {@link Color color}
	 */
	void setAxisColor( Color color );
	
	/**
	 * Returns the background color
	 * @return the {@link Color color}
	 */
	Color getBackgroundColor();
	
	/** 
	 * Sets the background color of the drawing panel
	 * @param color the given {@link Color background color}
	 */
	void setBackgroundColor( Color color );
	
	/**
	 * Enables/disables the display of the comments
	 * @param enabled indicates whether to enable/disable
	 */
	boolean showComments();
	
	/**
	 * Enables/disables the display of the grid
	 * @param enabled indicates whether to enable/disable
	 */
	void showComments( boolean enabled );
	
	/**
	 * Returns the grid color
	 * @return the {@link Color color}
	 */
	Color getGridColor();
	
	/** 
	 * Sets the grid color of the drawing panel
	 * @param color the given {@link Color color}
	 */
	void setGridColor( Color color );
	
	/**
	 * Enables/disables the display of the comments
	 * @param enabled indicates whether to enable/disable
	 */
	boolean showGrids();
	
	/**
	 * Enables/disables the display of the grid
	 * @param enabled indicates whether to enable/disable
	 */
	void showGrids( boolean enabled );
	
	/**
	 * Returns the highlighted geometry color
	 * @return the {@link Color color}
	 */
	Color getHighlightedGeometryColor();
	
	/** 
	 * Sets the highlighted geometry color 
	 * @param color the given {@link Color color}
	 */
	void setHighlightedGeometryColor( Color color );
	
	/**
	 * Returns the ghost geometry color
	 * @return the {@link Color color}
	 */
	Color getPhantomColor();
	
	/** 
	 * Sets the ghost geometry color 
	 * @param color the given {@link Color color}
	 */
	void setPhantomColor( Color color );
	
	/**
	 * Enables/disables the display of phantom geometry
	 * @param enabled indicates whether to enable/disable
	 */
	boolean showPhantoms();
	
	/**
	 * Enables/disables the display of phantom geometry
	 * @param enabled indicates whether to enable/disable
	 */
	void showPhantoms( boolean enabled );
	
	/**
	 * Returns the picked drawing element color
	 * @return the {@link Color color}
	 */
	Color getPickedElementColor();
	
	/** 
	 * Sets the picked drawing element color 
	 * @param color the given {@link Color color}
	 */
	void setPickedElementColor( Color color );
	
	/**
	 * Enables/disables the display of point labels
	 * @param enabled indicates whether to enable/disable
	 */
	boolean showPointLabels();
	
	/**
	 * Enables/disables the display of point labels
	 * @param enabled indicates whether to enable/disable
	 */
	void showPointLabels( boolean enabled );
	
	/**
	 * Returns the selected geometry color
	 * @return the {@link Color color}
	 */
	Color getSelectedGeometryColor();
	
	/** 
	 * Sets the selected geometry color 
	 * @param color the given {@link Color color}
	 */
	void setSelectedGeometryColor( Color color );
	
	/**
	 * Returns the temporary geometry color
	 * @return the {@link Color color}
	 */
	Color getTemporaryElementColor();
	
	/** 
	 * Sets the temporary geometry color 
	 * @param color the given {@link Color color}
	 */
	void setTemporaryElementColor( Color color );
	
	/** 
	 * Indicates whether the camera is in debug mode
	 * @return true, if the camera is in debug mode
	 */
	boolean isDebugMode();
	
	/** 
	 * Sets the application in debug mode
	 * @param enabled the debug mode indicator
	 */
	void setDebugMode( boolean enabled );

}
