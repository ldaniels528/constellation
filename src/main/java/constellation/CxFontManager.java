package constellation;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComboBox;

/**
 * Constellation Font Manager
 * @author lawrence.daniels@gmail.com
 */
public class CxFontManager {
	private static final Font BOLD_FONT		= new Font( "Arial", Font.BOLD, 16 );
	private static final Font DEFAULT_FONT	= new Font( "Arial", Font.PLAIN, 16 );
	private static final Font ITALIC_FONT	= new Font( "Arial", Font.ITALIC, 16 );
	private static final Font MEDIUM_FONT 	= new Font( "Arial", Font.PLAIN, 14 );
	private static final Font SMALL_FONT 	= new Font( "Arial", Font.PLAIN, 12 );
	private static FontMetrics fontMetrics 	= null;
	
	/** 
	 * Initializes the font manager giving it the ability
	 * to determine the length of graphical text strings.
	 * @param g the given {@link Graphics graphics context}
	 */
	public static void init( final Graphics g ) {
		// get the font metrics
		fontMetrics = g.getFontMetrics();
	}
	
	public static int getTextWidth( final String textString ) {
		if( fontMetrics == null ) {
			throw new IllegalStateException( "Font manager has not been initialized" );
		}
		return fontMetrics.stringWidth( textString );
	}
	
	public static int getTextHeight() {
		if( fontMetrics == null ) {
			throw new IllegalStateException( "Font manager has not been initialized" );
		}
		return fontMetrics.getHeight();
	}
	
	/**
	 * Sets the default font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setDefaultFont( final Component comp ) {
		//final Font font = super.getFont();
		//logger.info( "font is " + font.getFamily() + " " + font.getFontName() + " " + font.getSize() );
		//super.setFont( new Font( "LucidaGrande", Font.PLAIN, 16 ) );
		comp.setFont( DEFAULT_FONT );
	}
	
	/**
	 * Sets the default font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setDefaultFont( final JComboBox comp ) {
		comp.setFont( MEDIUM_FONT );
	}
	
	/**
	 * Sets the default italic font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setItalicFont( final Component comp ) {
		comp.setFont( ITALIC_FONT );
	}
	
	/**
	 * Sets the default font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setBoldFont( final Component comp ) {
		comp.setFont( BOLD_FONT );
	}
	
	/**
	 * Sets the default font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setMediumFont( final Component comp ) {
		comp.setFont( MEDIUM_FONT );
	}
	
	/**
	 * Sets the default font for the given component 
	 * @param comp the given {@link Component component}
	 */
	public static void setSmallFont( final Component comp ) {
		comp.setFont( SMALL_FONT );
	}
	
}
