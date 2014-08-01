package constellation.ui.components.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/** 
 * Color Cube Icon
 * @author lawrence.daniels@gmail.com
 */
public class CxColorCubeIcon implements Icon {
	private static final int WIDTH	= 50;
	private static final int HEIGHT = 5;
	private Color color;
	
	public CxColorCubeIcon( final Color color ) {
		this.color = color;
	}
	
	/** 
	 * Returns the currently selected color
	 * @return the currently selected {@link Color color}
	 */
	public Color getColor() {
		return color;
	}

	/** 
	 * Set the currently selected color
	 * @param color the currently selected {@link Color color}
	 */
	public void setColor( final Color color ) {
		this.color = color;
	}



	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return HEIGHT;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return WIDTH;
	}

	/* 
	 * (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon( final Component comp, Graphics g, final int x, final int y ) {
		g.setColor( color );
		g.fillRect( x, y, WIDTH, HEIGHT );
	}
}