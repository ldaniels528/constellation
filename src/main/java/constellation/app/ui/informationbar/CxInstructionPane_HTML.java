package constellation.app.ui.informationbar;

import static java.lang.String.format;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.CxFontManager;
import constellation.ThreadPool;

/**
 * Constellation Instruction Pane (HTML Version)
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxInstructionPane_HTML extends JEditorPane {
	// image URL definitions
	private static final CxContentManager contentMgr = CxContentManager.getInstance();
	private static final URL KEYBOARD_URL	= contentMgr.getResourceURL( "images/informationbar/input-keyboard.gif" );
	private static final URL MOUSE_URL		= contentMgr.getResourceURL( "images/informationbar/input-mouse.gif" );
	
	// define the special tags
	private static final Map<String,String> KEYWORDS = new LinkedHashMap<String, String>();
	static {
		KEYWORDS.put( "Drag", 		format( "<font color='#D00000'>Drag</font>&nbsp;<img src='%s'>", MOUSE_URL ) ); 
		KEYWORDS.put( "Select", 	format( "<font color='#D00000'>Select</font>&nbsp;<img src='%s'>", MOUSE_URL ) ); 
		KEYWORDS.put( "Indicate",	format( "<font color='#D00000'>Indicate</font>&nbsp;<img src='%s'>", MOUSE_URL )  );
		KEYWORDS.put( "Key", 		format( "<font color='#D00000'>Key</font>&nbsp;<img src='%s'>", KEYBOARD_URL ) );
		KEYWORDS.put( "#angle", 	format( "<font color='blue'>angle</font>" ) );
		
		// geometry
		KEYWORDS.put( "#circle", 	format( "<font color='#0000FF'>circle</font>" ) );
		KEYWORDS.put( "#curve", 	format( "<font color='#0000FF'>curve</font>" ) );
		KEYWORDS.put( "#elements", 	format( "<font color='#0000FF'>elements</font>" ) );
		KEYWORDS.put( "#element", 	format( "<font color='#0000FF'>element</font>" ) );
		KEYWORDS.put( "#line", 		format( "<font color='#0000FF'>line</font>" ) );
		KEYWORDS.put( "#point", 	format( "<font color='#0000FF'>point</font>" ) );
		KEYWORDS.put( "#polyline", 	format( "<font color='#0000FF'>polyline</font>" ) );
	}
	
	// logger instance
	private final Logger logger = Logger.getLogger( getClass() );
	
	// internal fields
	private final ApplicationController controller;
	private final RepaintTask repaintTask;
	private final Map<String,String> cache;
	private int length = 512;
	
	/**
	 * Creates the HTML instruction pane
	 * @param controller the given {@link ApplicationController controller}
	 */
	public CxInstructionPane_HTML( final ApplicationController controller ) {
		this.controller 	= controller;
		this.cache 			= new HashMap<String, String>( 100 );
		this.repaintTask	= new RepaintTask();

		// setup the pane
		super.setEditorKit( new HTMLEditorKit() ); 
		super.setEditable( false );
		super.setDoubleBuffered( true );
		
		// setup the appropriate font
		CxFontManager.setBoldFont( this );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JEditorPane#setText(java.lang.String)
	 */
	public void setText( final String text ) {
		// set the text
		super.setText( toHTML( text ) );
		
		// create a paint task
		final ThreadPool pool = controller.getThreadPool();
		pool.queue( repaintTask );
	}
	
	/**
	 * Formats the given step as HTML
	 * @param text the given text string
	 * @return the resultant HTML
	 */
	private String toHTML( final String text ) {		
		// is the text cached?
		if( !cache.containsKey( text ) ) {	
			// convert it to HTML
			final String htmltext = toTextWithImageTags( text );
			
			// put the HTML into the cache
			cache.put( text, htmltext );
			
			// return the HTML
			return htmltext;
		}
		else {
			return cache.get( text );
		}
	}
	
	/**
	 * Embeds images next to key words found in the given text
	 * @param text the given text
	 * @return the text with embedded HTML image tags
	 */
	private String toTextWithImageTags( final String text ) {
		final StringBuilder sb = new StringBuilder( length );
		
		// attach the original step text
		sb.append( "<font face='arial' size='5'>" );
		sb.append( text );
		
		// replace all keywords
		for( final String keyword : KEYWORDS.keySet() ) {
			int lastIndex = 0;
			int index;
			
			// replace all instances of the current keyword
			while( ( index = sb.indexOf( keyword, lastIndex ) ) != -1 ) {
				// create the replacement string
				final String replaceString = KEYWORDS.get( keyword );
				
				// replace the section
				sb.replace( index, index + keyword.length(), replaceString );
				
				// make sure to move the cursor beyond the replaced text
				lastIndex = index + replaceString.length();
			}
		}
		sb.append( "</font>" );
		
		// adjust the preallocated sum
		if( sb.length() > length ) {
			length = sb.length();
			logger.info( format( "Expanded the buffer to %d", length ) );
		}
		
		// return the HTML string
		return sb.toString();
	}
	
	/**
	 * Repaint Task
	 * @author lawrence.daniels@gmail.com
	 */
	private class RepaintTask implements Runnable {

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// sleep first
			try {
				Thread.sleep( 200 );
			} 
			catch( final InterruptedException cause ) {
				cause.printStackTrace();
			}
			
			// repaint the component
			CxInstructionPane_HTML.this.repaint();
		}
		
	}

}
