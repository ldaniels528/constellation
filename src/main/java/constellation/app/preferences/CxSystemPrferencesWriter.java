package constellation.app.preferences;

import static java.lang.String.format;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import constellation.preferences.ThirdPartyModuleInfo;

/**
 * Constellation System Preferences XML Writer
 * @author lawrence.daniels@gmail.com
 */
public class CxSystemPrferencesWriter {
	// singleton instance
	private static final CxSystemPrferencesWriter instance = new CxSystemPrferencesWriter();
	
	/**
	 * Private Constructor 
	 */
	private CxSystemPrferencesWriter() {
		super();
	}
	
	/** 
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static CxSystemPrferencesWriter getInstance() {
		return instance;
	}
	
	/**
	 * Saves the given system preferences to disk
	 * @param configFile the given configuration {@link File file}
	 * @param preferences the given {@link CxSystemPreferences preferences}
	 * @throws IOException
	 */
	public static void writeFile( final File configFile, final CxSystemPreferences preferences ) 
	throws IOException {
		PrintWriter out = null;
		try {
			// open the file for writing
			out = new PrintWriter( configFile );
			
			// write the system preferences
			instance.write( out, preferences );
			
			// flush the buffer
			out.flush();
		}
		finally {
			if( out != null ) {
				try { out.close(); } catch( Exception e ) { } 
			}
		}
	}
	
	/**
	 * Saves the given project to disk
	 * @param out the given {@link PrintWriter output stream}
	 * @param preferences the given {@link CxSystemPreferences preferences}
	 */
	public void write( final PrintWriter out, final CxSystemPreferences preferences ) {
		out.println( "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" );
		out.println( "<ConstellationConfig>" );
		
		// write the frame information
		out.printf( "\t<FrameSize width='%d' height='%d' />\n", preferences.getWidth(), preferences.getHeight() );
		
		// write the color preferences
		out.printf( "\t<ColorPreferences background='%s' phantom='%s' grid='%s' highlighted='%s' picked='%s' temporary='%s' />\n", 
				encode( preferences.getBackgroundColor() ), 
				encode( preferences.getPhantomColor() ),
				encode( preferences.getGridColor() ), 
				encode( preferences.getHighlightedGeometryColor() ), 
				encode( preferences.getPickedElementColor() ),
				encode( preferences.getTemporaryElementColor() ) );
		
		// write the developer preferences
		out.printf( "\t<DeveloperPreferences debugMode='%s' />\n", 
				preferences.isDebugMode() );
		
		// write the visibility preferences
		out.printf( "\t<VisibilityPreferences antialiasing='%s' comments='%s' grids='%s' phantoms='%s' pointLabels='%s' />\n", 
				preferences.isAntiAliasing(),
				preferences.showComments(), 
				preferences.showGrids(), 
				preferences.showPhantoms(),
				preferences.showPointLabels() );
		
		// write the third party module information
		writeThirdPartyModules( out, preferences.getThirdPartyModules() );
		
		// end the file
		out.println( "</ConstellationConfig>" );
	}
	
	/** 
	 * Writes the third party modules
	 * @param out the given {@link PrintWriter output stream}
	 * @param modules the given collection of {@link ThirdPartyModuleInfo third party modules}
	 */
	public void writeThirdPartyModules( final PrintWriter out, final Collection<ThirdPartyModuleInfo> modules ) {
		for( final ThirdPartyModuleInfo module : modules ) {
			out.printf( "\t<ThirdPartyModule type='%s' class='%s' enabled='%s' />\n", 
					module.getType(), module.getClassName(), module.isEnabled() );
		}
	}
	
	/**
	 * Encodes the given color into an RGB string expression
	 * @param color the given {@link Color color}
	 * @return the string expression (e.g. WHITE => '255,255,255')
	 */
	private String encode( final Color color ) {
		return format( "%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue() );
	}

}
