package constellation.app.preferences;

import static constellation.drawing.DefaultDrawingColors.AXIS_COLOR;
import static constellation.drawing.DefaultDrawingColors.BACKGROUND_COLOR;
import static constellation.drawing.DefaultDrawingColors.GRID_COLOR;
import static constellation.drawing.DefaultDrawingColors.HIGHLIGHT_COLOR;
import static constellation.drawing.DefaultDrawingColors.PHANTOM_ELEMENT_COLOR;
import static constellation.drawing.DefaultDrawingColors.PICKLIST_COLOR;
import static constellation.drawing.DefaultDrawingColors.SELECT_COLOR;
import static constellation.drawing.DefaultDrawingColors.TEMP_GEOM_COLOR;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.MenuItem;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenuItem;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import constellation.ApplicationController;
import constellation.CxClassLoader;
import constellation.CxConfigurationUtil;
import constellation.CxContentManager;
import constellation.model.formats.ModelFormatManager;
import constellation.model.formats.ModelFormatReader;
import constellation.model.formats.ModelFormatWriter;
import constellation.preferences.SystemPreferences;
import constellation.preferences.ThirdPartyModuleInfo;

/**
 * Constellation System Preferences
 * @author lawrence.daniels@gmail.com
 */
public class CxSystemPreferences implements SystemPreferences {
	// singleton instance
	private static final CxSystemPreferences instance = new CxSystemPreferences();
	
	// miscellaneous fields
	private static final Logger logger = Logger.getLogger( CxSystemPreferences.class );
	private final Collection<ThirdPartyModuleInfo> thirdPartyModules;
	private final CxClassLoader myClassLoader;
	
	// frame fields
	private int width;
	private int height;
	
	// display preference fields
	private boolean antiAliasing;
	private boolean showComments;
	private boolean showGrids;
	private boolean showPhantoms;
	private boolean showPointLabels;
	
	// color preference fields
	private Color axisColor;
	private Color backgroundColor;
	private Color ghostGeometryColor;
	private Color gridColor;
	private Color highlightedGeometryColor;
	private Color pickedGeometryColor;
	private Color selectedGeometryColor;
	private Color temporaryGeometryColor;
	
	// miscellaneous fields
	private long lastFileUpdated;
	private long lastModified;
	private long initializedTime;
	private boolean debugMode;
	private boolean initialized;
	
	/** 
	 * Default Constructor
	 */
	private CxSystemPreferences() {
		this.myClassLoader 				= CxClassLoader.getInstance( );
		this.thirdPartyModules			= new LinkedList<ThirdPartyModuleInfo>();
		this.initialized				= false;
		this.debugMode					= false;
		
		// set display hints
		this.antiAliasing				= false;
		this.showComments				= true;
		this.showGrids					= true;
		this.showPhantoms				= true;
		this.showPointLabels			= false;
		
		// set the color preferences
		this.axisColor					= AXIS_COLOR;
		this.backgroundColor			= BACKGROUND_COLOR;
		this.ghostGeometryColor			= PHANTOM_ELEMENT_COLOR;
		this.gridColor					= GRID_COLOR;
		this.highlightedGeometryColor	= HIGHLIGHT_COLOR;
		this.pickedGeometryColor		= PICKLIST_COLOR;
		this.selectedGeometryColor		= SELECT_COLOR;
		this.temporaryGeometryColor		= TEMP_GEOM_COLOR;
	}
	
	/** 
	 * Returns the singleton instance of the class
	 * @return the singleton instance of the class
	 */
	public static CxSystemPreferences getInstance() {
		return instance;
	}
	
	/**
	 * Loads the system preferences from disk
	 * @param configFile the given system preferences configuration {@link File file}
	 * @return the {@link CxSystemPreferences system preferences}
	 * @throws IOException 
	 */
	public static CxSystemPreferences load( final File configFile ) 
	throws IOException {
		try {
			// load the system preferences
			final CxSystemPreferences preferences = CxSystemPreferencesReader.readFile( configFile );
			
			// capture the last saved time of the file
			preferences.lastFileUpdated = configFile.lastModified();
			
			// return the system preferences
			return preferences;
		} 
		catch( final ParserConfigurationException cause ) {
			logger.error( "Error loading the configuration file", cause );
			throw new IOException( "Error loading the configuration file" );
		} 
		catch( final SAXException cause ) {
			logger.error( "Error loading the configuration file", cause );
			throw new IOException( "Error loading the configuration file" );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#save()
	 */
	public void save() throws IOException {
		// get the configuration file path
		final File configFile = 
			CxConfigurationUtil.getXMLConfigurationFile();
		
		// check the last modified time
		final long fileLastModified = configFile.lastModified();
		
		// if the file hasn't been modified since loaded,
		// update it
		if( ( lastFileUpdated >= fileLastModified ) && ( lastModified > initializedTime ) ) {
			// save the XML file
			CxSystemPrferencesWriter.writeFile( configFile, this );
			
			// record the saved time
			lastFileUpdated = configFile.lastModified();
		}
		else {
			logger.warn( "System configuration file was not saved" );
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Initialization Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Initializes the system preferences
	 * @param controller the given {@link ApplicationController controller}
	 */
	public void init( final ApplicationController controller ) {
		if( !initialized ) {
			// set the flag
			initialized = true;
			initializedTime = System.currentTimeMillis();
			
			// get an instance of the format manager
			final ModelFormatManager formatManager = ModelFormatManager.getInstance();
			
			// look for format reader definitions
			for( final ThirdPartyModuleInfo module : thirdPartyModules ) {
				// if the module is enabled, proceed...
				if( module.isEnabled() ) {
					// cache the module's class name
					final String className = module.getClassName();
					
					// process the module
					switch( module.getType() ) {
						case FORMAT_READER:
							final ModelFormatReader reader = loadModelFormatReader( className );
							if( reader != null ) {
								formatManager.add( reader );
							}
							break;
							
						case FORMAT_WRITER:
							final ModelFormatWriter writer = loadModelFormatWriter( className );
							if( writer != null ) {
								formatManager.add( writer );
							}
							break;
					}
				}
			}
		}
	}
	
	/** 
	 * Loads the third party menu item identified by the given class name
	 * @param controller the given {@link ApplicationController controller}
	 * @param className the class name of the third party menu item
	 * @return the {@link JMenuItem menu item}
	 */
	private JMenuItem loadThirdPartyToolMenuItem( final ApplicationController controller, final String className ) {
		try {
			// load the menu class
			logger.info( format( "Loading menu class '%s'...", className ) );
			final Class<?> menuClass = Class.forName( className, true, myClassLoader );
			
			// get the constructor
			final Constructor<?> constructor = menuClass.getConstructor( new Class[] { ApplicationController.class } );
			
			// return the new instance
			return (JMenuItem)constructor.newInstance( new Object[] { controller } );
		} 
		catch( final Exception e ) {
			logger.error( format( "Error loading class '%s'", className ), e );
			
			// failed menu item
			final JMenuItem menuItem = new JMenuItem();
			menuItem.setText( getShortClassName( className ) );
			menuItem.setIcon( CxContentManager.getInstance().getIcon( "images/menu/important.png" ) );
			menuItem.setToolTipText( "This third party menu could not be loaded" );
			return menuItem;
		}
	}
	
	/** 
	 * Returns the "short" name of the class name  
	 * @param className the given class name
	 * @return the "short" class name
	 */
	private String getShortClassName( final String className ) {
		final int index = className.lastIndexOf( '.' ) + 1;
		return ( index > 0 && index < className.length() ) 
					? className.substring( index ) 
					: className;
	}
	
	/** 
	 * Loads the model format reader represented by the given class name
	 * @param className the given class name
	 * @return the {@link ModelFormatReader model format reader}, 
	 * or <tt>null</tt> if the class could not be loaded.
	 */
	private ModelFormatReader loadModelFormatReader( final String className ) {
		try {
			// create an instance of the format reader
			return (ModelFormatReader)Class.forName( className, true, myClassLoader ).newInstance();
		}
		catch( final Throwable e ) {
			logger.error( format( "Error loading model format reader '%s'", className ), e );
			return null;
		}
	}
	
	/** 
	 * Loads the model format writer represented by the given class name
	 * @param className the given class name
	 * @return the {@link ModelFormatWriter model format writer}, 
	 * or <tt>null</tt> if the class could not be loaded.
	 */
	private ModelFormatWriter loadModelFormatWriter( final String className ) {
		try {
			// create an instance of the format writer
			return (ModelFormatWriter)Class.forName( className, true, myClassLoader ).newInstance();
		}
		catch( final Throwable e ) {
			logger.error( format( "Error loading model format writer '%s'", className ), e );
			return null;
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Third Party Module Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Returns the collection of third party menu items
	 * @return the collection of third party {@link MenuItem menu items}
	 */
	public Collection<JMenuItem> getThirdPartyMenuItems( final ApplicationController controller ) {
		// create a container for the menus
		final Map<String,JMenuItem> menus = new TreeMap<String,JMenuItem>();
		
		// look for format reader definitions
		for( final ThirdPartyModuleInfo module : thirdPartyModules ) {
			// if the module is enabled, proceed...
			if( module.isEnabled() ) {
				// cache the module's class name
				final String className = module.getClassName();
				
				// process the module
				switch( module.getType() ) {
					case MENU_ITEM:
						final JMenuItem menuItem = loadThirdPartyToolMenuItem( controller, className );
						menus.put( menuItem.getText(), menuItem );
						break;
				}
			}
		}
		
		return menus.values();
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Frame Dimension Methods
	/////////////////////////////////////////////////////////////////////
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#getWidth()
	 */
	public int getWidth() {
		return width;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#setWidth(int)
	 */
	public void setWidth( final int width ) {
		this.width = width;
		this.lastModified = System.currentTimeMillis();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#setHeight(int)
	 */
	public void setHeight( final int height ) {
		this.height = height;
		this.lastModified = System.currentTimeMillis();
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Third-Party Module Methods
	/////////////////////////////////////////////////////////////////////

	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#addThirdPartyModule(constellation.preferences.ThirdPartyModuleInfo)
	 */
	public void addThirdPartyModule( final ThirdPartyModuleInfo thirdPartyModule ) {
		thirdPartyModules.add( thirdPartyModule );
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#getThirdPartyModules()
	 */
	public Collection<ThirdPartyModuleInfo> getThirdPartyModules() {
		return thirdPartyModules;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Color/Dislay Preference Methods
	/////////////////////////////////////////////////////////////////////
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getAxisColor()
	 */
	public Color getAxisColor() {
		return axisColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setAxisColor(java.awt.Color)
	 */
	public void setAxisColor( final Color color ) {
		this.axisColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getGridColor()
	 */
	public Color getGridColor() {
		return gridColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setGridColor(java.awt.Color)
	 */
	public void setGridColor( final Color color ) {
		this.gridColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getBackgroundColor()
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setBackgroundColor(java.awt.Color)
	 */
	public void setBackgroundColor( final Color color ) {
		this.backgroundColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#isAntiAliasing()
	 */
	public boolean isAntiAliasing() {
		return antiAliasing;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#setAntiAliasing(boolean)
	 */
	public void setAntiAliasing( final boolean antiAliasing ) {
		this.antiAliasing = antiAliasing;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showComments()
	 */
	public boolean showComments() {
		return showComments;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showComments(boolean)
	 */
	public void showComments( final boolean enabled ) {
		this.showComments = enabled;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showGrids()
	 */
	public boolean showGrids() {
		return showGrids;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showGrids(boolean)
	 */
	public void showGrids( final boolean enabled ) {
		this.showGrids = enabled;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getHighlightedGeometryColor()
	 */
	public Color getHighlightedGeometryColor() {
		return highlightedGeometryColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setHighlightedGeometryColor(java.awt.Color)
	 */
	public void setHighlightedGeometryColor( final Color color ) {
		this.highlightedGeometryColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getGhostGeometryColor()
	 */
	public Color getPhantomColor() {
		return ghostGeometryColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setGhostGeometryColor(java.awt.Color)
	 */
	public void setPhantomColor( final Color color ) {
		this.ghostGeometryColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showPhantoms()
	 */
	public boolean showPhantoms() {
		return showPhantoms;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showPhantoms(boolean)
	 */
	public void showPhantoms( final boolean enabled ) {
		this.showPhantoms = enabled;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getPickedGeometryColor()
	 */
	public Color getPickedElementColor() {
		return pickedGeometryColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setPickedGeometryColor(java.awt.Color)
	 */
	public void setPickedElementColor( final Color color ) {
		this.pickedGeometryColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showPointLabels()
	 */
	public boolean showPointLabels() {
		return showPointLabels;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#showPointLabels(boolean)
	 */
	public void showPointLabels( final boolean enabled ) {
		this.showPointLabels = enabled;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getSelectedGeometryColor()
	 */
	public Color getSelectedGeometryColor() {
		return selectedGeometryColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setSelectedGeometryColor(java.awt.Color)
	 */
	public void setSelectedGeometryColor( final Color color ) {
		this.selectedGeometryColor = color;
		this.lastModified = System.currentTimeMillis();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#getTemporaryElementColor()
	 */
	public Color getTemporaryElementColor() {
		return temporaryGeometryColor;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.SystemPreferences#setTemporaryElementColor(java.awt.Color)
	 */
	public void setTemporaryElementColor( final Color color ) {
		this.temporaryGeometryColor = color;
		this.lastModified = System.currentTimeMillis();
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#isDebugMode()
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.preferences.SystemPreferences#setDebugMode(boolean)
	 */
	public void setDebugMode( final boolean enabled ) {
		this.debugMode = enabled;
		this.lastModified = System.currentTimeMillis();
	}
	
}
