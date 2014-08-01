package constellation.ui.components.icons;

import static java.awt.Color.WHITE;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Represents a blank icon
 * @author lawrence.daniels@gmail.com
 */
public class CxBlankIcon implements Icon {
	private int width;
	private int height;
	
	/**
	 * Creates a new blank icon with a white background
	 * @param width the width of the icon
	 * @param height the height of the icon
	 */
	public CxBlankIcon( final int width, final int height ) {
		this.width	= width;
		this.height	= height;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return height;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return width;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon( final Component c, final Graphics g, final int x, final int y) {
		g.setColor( WHITE );
		g.fillRect( x, y, width, height );
	}
	
}