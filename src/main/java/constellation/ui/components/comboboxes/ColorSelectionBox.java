package constellation.ui.components.comboboxes;

import static java.awt.Color.*;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Constellation Color Selection ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class ColorSelectionBox extends JComboBox {
	private static final Map<Color, String> COLOR_NAMES = getColoredIconMapping();
	private final DefaultColorCubeIcon DEFAULT_ICON = new DefaultColorCubeIcon();
	
	/**
	 * Default constructor
	 */
	public ColorSelectionBox() {
		super( new Color[] { 
				null, BLACK, RED, GREEN, BLUE, YELLOW, PINK,
				MAGENTA, CYAN, WHITE, GRAY, ORANGE 
		} );
		
		// setup the custom renderer
		super.setRenderer( new ColorSelectionCellRenderer( super.getRenderer() ) );
	} 
	
	/**
	 * Resets the component to it's initial state
	 */
	public void reset() {
		super.setSelectedIndex( 0 );
	}
	
	/**
	 * Returns the selected color
	 * @return the selected {@link Color color}
	 */
	public Color getSelectedColor() {
		return (Color)super.getSelectedItem();
	}
	
	/**
	 * Sets the selected color
	 * @param color the given {@link Color color}
	 */
	public void setSelectedColor( final Color color ) {
		super.setSelectedItem( color );
	}
	
	/**
	 * Creates a mapping of color names to colored icon
	 * @return the mapping of color names to colored icon
	 */
	private static Map<Color, String> getColoredIconMapping() {
		final Map<Color, String> map = new HashMap<Color, String>();
		map.put( BLACK, 	"Black" );
		map.put( BLUE, 		"Blue" );
		map.put( CYAN,		"Cyan" );
		map.put( GRAY,		"Gray" );
		map.put( GREEN,		"Green" );
		map.put( MAGENTA,	"Magenta" );
		map.put( ORANGE,	"Orange" );
		map.put( PINK,		"Pink" );
		map.put( RED,		"Red" );
		map.put( WHITE,		"White" );
		map.put( YELLOW,	"Yellow" );
		return map;
	}
	
	/**
	 * Color Selection Cell Renderer
	 * @author lawrence.daniels@gmail.com
	 */
	private class ColorSelectionCellRenderer implements ListCellRenderer {
		private final ListCellRenderer renderer;
		
		/** 
		 * Creates a new color selection cell renderer
		 * @param renderer the given {@link ListCellRenderer list cell renderer}
		 */
		public ColorSelectionCellRenderer( final ListCellRenderer renderer ) {
			this.renderer = renderer;
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent( final JList list, 
													   final Object value,
													   final int index, 
													   final boolean isSelected, 
													   final boolean cellHasFocus) {
			// get the component as returned from the system cell renderer
			final Component comp = 
				renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			
			// cast the value to a color
			final Color color = (Color)value;
			
			// lookup the appropriate icon
			final Icon icon = ( color == null ) ? DEFAULT_ICON : new ColorCubeIcon( color );
			
			// if it's a label, set the icon
			if( comp instanceof JLabel ) {
				final JLabel label = (JLabel)comp;
				label.setIcon( icon );
				label.setText( COLOR_NAMES.containsKey( color ) 
								? COLOR_NAMES.get( color ) 
								: ( icon.equals( DEFAULT_ICON ) ? "Default" : "Custom" ) );
			}
			
			return comp;
		}
	}
	
	/**
	 * Default Color Cube Icon
	 * @author lawrence.daniels@gmail.com
	 */
	private static class DefaultColorCubeIcon extends ImageIcon {
		
		/**
		 * Creates a new Cube icon
		 * @param color the given cube {@link Color color}
		 */
		public DefaultColorCubeIcon() {
			final BufferedImage image = new BufferedImage( 12, 12, BufferedImage.TYPE_4BYTE_ABGR );
			final Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor( BLACK );
			g2d.drawRect( 0, 0, image.getWidth() - 1, image.getHeight() - 1 );
			super.setImage( image );
		}	
	}
	
	/**
	 * Color Cube Icon
	 * @author lawrence.daniels@gmail.com
	 */
	private static class ColorCubeIcon extends ImageIcon {
		
		/**
		 * Creates a new Cube icon
		 * @param color the given cube {@link Color color}
		 */
		public ColorCubeIcon( final Color color ) {
			final BufferedImage image = new BufferedImage( 12, 12, BufferedImage.TYPE_4BYTE_ABGR );
			final Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor( color );
			g2d.fillRect( 0, 0, image.getWidth(), image.getHeight() );
			super.setImage( image );
		}	
	}
	
}
