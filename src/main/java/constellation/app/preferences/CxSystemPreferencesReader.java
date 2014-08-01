package constellation.app.preferences;

import static java.lang.String.format;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import constellation.preferences.ThirdPartyModuleInfo;
import constellation.preferences.ThirdPartyModuleTypes;

/**
 * Constellation System Preferences XML Reader
 * @author lawrence.daniels@gmail.com
 */
public class CxSystemPreferencesReader extends DefaultHandler {
	private static enum TAG_ENUMS { 
		COLOR_PREFS, CONST_CONFIG, DEVELOPER_PREFS, 
		FRAME_SIZE, THIRD_PARTY_MODULE, VISIBILITY_PREFS
	};
	private static final Map<String, ThirdPartyModuleTypes> MODULE_TYPES = createThirdPartyModuleType();
	private static final Map<String,TAG_ENUMS> TAGS;
	private final Logger logger = Logger.getLogger( getClass() );
	private final LinkedList<Properties> stackProps;
	private final LinkedList<TAG_ENUMS> stackTags;
	private final StringBuilder characters;
	private final CxSystemPreferences preferences;
	
	static {
		// Create a mapping of the tag names to enumeration
		TAGS = new HashMap<String, TAG_ENUMS>();
		TAGS.put( "ColorPreferences",		TAG_ENUMS.COLOR_PREFS );
		TAGS.put( "ConstellationConfig",	TAG_ENUMS.CONST_CONFIG );
		TAGS.put( "DeveloperPreferences",	TAG_ENUMS.DEVELOPER_PREFS );
		TAGS.put( "FrameSize",				TAG_ENUMS.FRAME_SIZE );
		TAGS.put( "ThirdPartyModule",		TAG_ENUMS.THIRD_PARTY_MODULE );
		TAGS.put( "VisibilityPreferences",	TAG_ENUMS.VISIBILITY_PREFS );
	}
	
	/**
	 * Default constructor
	 */
	private CxSystemPreferencesReader() {
		this.characters		= new StringBuilder( 1024 );
		this.preferences	= CxSystemPreferences.getInstance();
		this.stackProps		= new LinkedList<Properties>();
		this.stackTags		= new LinkedList<TAG_ENUMS>();
	}
	
	/**
	 * Loads the system preferences from disk
	 * @param configFile the given system preferences configuration {@link File file}
	 * @return the {@link CxSystemPreferences system preferences}
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static CxSystemPreferences readFile( final File configFile ) 
	throws ParserConfigurationException, SAXException, IOException {
		// create a handler instance
		final CxSystemPreferencesReader handler = new CxSystemPreferencesReader();
		
		// get a SAX Parser Factory instance
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		
		// get a SAX Parser instance
		final SAXParser parser = parserFactory.newSAXParser();
		
		// parse the file
		parser.parse( configFile, handler );
		
		// return the preferences
		return handler.preferences;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters( final char[] chars, final int start, final int length )
	throws SAXException {
		characters.append( String.copyValueOf( chars, start, length ) );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement( final String uri, 
						  	final String localName, 
						  	final String name )
	throws SAXException {
		// get the text string
		final String text = characters.toString();
		
		// pop the current tag 
		stackTags.removeLast();
		
		// get the parent tag
		final TAG_ENUMS parentTag = !stackTags.isEmpty() ? stackTags.getLast() : null;
		
		// get the current depth of the stack
		final int depth = stackProps.size();
		
		// determine which tag handler to invoke
		final TAG_ENUMS tag = TAGS.get( name );
		if( tag != null ) {
			switch( tag ) {

			}
		}
		else {
			logger.warn( format( "Element '%s' not recognized", name ) );
		}
		
		// pop the last set of attributes
		// off the stack (if it hasn't been done already)
		if( depth == stackProps.size() ) {
			stackProps.removeLast();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement( final String uri, 
							  final String localName, 
							  final String name,
							  final Attributes attributes ) 
	throws SAXException {
		// clear the old text
		characters.delete( 0, characters.length() );
		
		// get the map of attributes
		final Properties attribs = toProperties( attributes );
		
		// get the parent tag
		final TAG_ENUMS parentTag = !stackTags.isEmpty() ? stackTags.getLast() : null;
		
		// push the attributes onto the stack
		stackProps.add( attribs );
		
		// identify the tag
		final TAG_ENUMS tag = TAGS.get( name );
		stackTags.add( tag );
		
		if( tag != null ) {
			switch( tag ) {
				case CONST_CONFIG: break;
				case COLOR_PREFS:			colorPreferencesStart( attribs ); break;
				case DEVELOPER_PREFS:		developerPreferencesStart( attribs ); break;
				case FRAME_SIZE:			frameSizeStart( attribs ); break;
				case THIRD_PARTY_MODULE:	thirdPartyModuleStart( attribs ); break;
				case VISIBILITY_PREFS:		visibilityPreferencesStart( attribs ); break;
			}
		}
		else {
			logger.warn( format( "Element '%s' not recognized", name ) );
		}
	}
	
	/** 
	 * Handles the 'ColorPreferences' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void colorPreferencesStart( final Properties attribs ) 
	throws SAXException {
		// get the parameter
		final Color backgroundColor	= getParamColorRGB( attribs, "background", false );
		final Color gridColor 		= getParamColorRGB( attribs, "grid", false );
		final Color highlightedColor= getParamColorRGB( attribs, "highlighted", false );
		final Color phantomColor 	= getParamColorRGB( attribs, "phantom", false );
		final Color pickedColor 	= getParamColorRGB( attribs, "picked", false );
		final Color temporaryColor	= getParamColorRGB( attribs, "temporary", false );
		
		// is the background color present?
		if( backgroundColor != null ) {
			preferences.setBackgroundColor( backgroundColor );
		}
		
		// is the ghost geometry color present?
		if( phantomColor != null ) {
			preferences.setPhantomColor( phantomColor );
		}
		
		// is the grid color present?
		if( gridColor != null ) {
			preferences.setGridColor( gridColor );
		}
		
		// is the highlighted color present?
		if( highlightedColor != null ) {
			preferences.setHighlightedGeometryColor( highlightedColor );
		}
		
		// is the picked geometry color present?
		if( pickedColor != null ) {
			preferences.setPickedElementColor( pickedColor );
		}
		
		// is the temporary geometry color present?
		if( temporaryColor != null ) {
			preferences.setTemporaryElementColor( temporaryColor );
		}
	}

	/** 
	 * Handles the 'DeveloperPreferences' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void developerPreferencesStart( final Properties attribs ) 
	throws SAXException {
		// get the frame width and height
		final boolean isDebugMode = getParamBoolean( attribs, "debugMode" );
		
		// set the developer preferences
		preferences.setDebugMode( isDebugMode );
	}
	
	/** 
	 * Handles the 'FrameSize' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void frameSizeStart( final Properties attribs ) 
	throws SAXException {
		// get the frame width and height
		final int width = getParamInt( attribs, "width", true );
		final int height = getParamInt( attribs, "height", true );
		
		// capture the frame width and height
		preferences.setWidth( width );
		preferences.setHeight( height );
	}

	/** 
	 * Handles the 'ThirdPartyModule' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void thirdPartyModuleStart( final Properties attribs ) 
	throws SAXException {
		// create a third party module
		final ThirdPartyModuleInfo module = new ThirdPartyModuleInfo();
		module.setClassName( getParamString( attribs, "class" ) );
		module.setType( getThirdPartyModuleType( attribs, "type" ) );
		module.setEnabled( getParamBoolean( attribs, "enabled" ) );
		
		// add the module to the preferences
		preferences.addThirdPartyModule( module );
	}
	
	/** 
	 * Handles the 'VisibilityPreferences' tag
	 * @param attribs the given {@link Properties attributes}
	 * @throws SAXException 
	 */
	private void visibilityPreferencesStart( final Properties attribs ) 
	throws SAXException {
		preferences.setAntiAliasing( getParamBoolean( attribs, "antialiasing" ) );
		preferences.showComments( getParamBoolean( attribs, "comments" ) );
		preferences.showGrids( getParamBoolean( attribs, "grids" ) );
		preferences.showPhantoms( getParamBoolean( attribs, "phantoms" ) );
		preferences.showPointLabels( getParamBoolean( attribs, "pointLabels" ) );
	}

	/** 
	 * Creates a mapping of type labels to type objects
	 * @return a mapping of type labels to {@link ThirdPartyModuleTypes type objects}
	 */
	private static Map<String, ThirdPartyModuleTypes> createThirdPartyModuleType() {
		final Map<String, ThirdPartyModuleTypes> map = new HashMap<String, ThirdPartyModuleTypes>();
		map.put( "FORMAT_READER", ThirdPartyModuleTypes.FORMAT_READER );
		map.put( "FORMAT_WRITER", ThirdPartyModuleTypes.FORMAT_WRITER );
		map.put( "MENU_ITEM", ThirdPartyModuleTypes.MENU_ITEM );
		return map;
	}
	
	/** 
	 * Returns the module type of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the {@link ThirdPartyModuleTypes module type}
	 * @throws SAXException
	 */
	private static ThirdPartyModuleTypes getThirdPartyModuleType( final Properties attributes, 
																  final String paramName ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// get the string value
		final String value = getParamString( attributes, paramName );
		
		// check the module type
		final ThirdPartyModuleTypes type = MODULE_TYPES.get( value );
		if( type == null ) {
			throw new SAXException( format( "Third Party Module type '%s' is not recognized", value ) );
		}
		
		return type;
	}
	
	/** 
	 * Returns the color value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @return the color value
	 * @throws SAXException
	 */
	private static boolean getParamBoolean( final Properties attributes, 
										  	final String paramName ) 
	throws SAXException {
		// get the string value
		final String value = getParamString( attributes, paramName ).toLowerCase();
		
		// return the color value
		return "true".equals( value ) || "yes".equals( value );
	}

	/** 
	 * Returns the color value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the color value
	 * @throws SAXException
	 */
	private static Color getParamColorRGB( final Properties attributes, 
										   final String paramName, 
										   final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// get the index
		final String rgbString = getParamString( attributes, paramName );
		final String[] rgb = rgbString.split( "[,]" ); 
		
		// convert them to integers
		final int r = Integer.parseInt( rgb[0] );
		final int g = Integer.parseInt( rgb[1] );
		final int b = Integer.parseInt( rgb[2] );
		
		// return the color value
		return new Color( r, g, b );
	}
	
	/** 
	 * Returns the integer value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @param required indicates whether the parameter is required
	 * @return the integer value
	 * @throws SAXException
	 */
	private static int getParamInt( final Properties attributes, 
									final String paramName, 
									final boolean required ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			if( required ) {
				throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
			}
			return 0;
		}
		
		// get the parameter value
		final String value = attributes.getProperty( paramName );
		
		// convert the value to a double
		try {
			return Integer.parseInt( value ); 
		}
		catch( final NumberFormatException e ) {
			throw new SAXException( format( "Invalid value '%s' for parameter '%s'", value, paramName ), e );
		}
	}
	
	/** 
	 * Returns the string value of parameter name
	 * @param attributes the given {@link Properties parameter mapping}
	 * @param paramName the given parameter name
	 * @return the string value
	 * @throws SAXException
	 */
	private static String getParamString( final Properties attributes, final String paramName ) 
	throws SAXException {
		// does the parameter exist?
		if( !attributes.containsKey( paramName ) ) {
			throw new SAXException( format( "Required parameter '%s' not found", paramName ) );
		}
		
		// return the parameter value
		return attributes.getProperty( paramName );
	}
	
	/**
	 * Returns the contents of the attributes as a properties object
	 * @param attributes the given {@link Attributes attributes}
	 * @return a {@link Properties attributes}
	 */
	private static Properties toProperties( final Attributes attributes ) {
		final Properties map = new Properties();
		for( int n = 0; n < attributes.getLength(); n++ ) {
			map.put( attributes.getQName( n ), attributes.getValue( n ) );
		}
		return map;
	}

}
