package constellation.app.functions.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Represents a Panel for displaying a picture (image)
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PicturePanel extends JPanel {
	private Image image;
	
	/**
	 * Default Constructor
	 */
	public PicturePanel() {
		super.setPreferredSize( new Dimension( 400, 400 ) );
	}
	
	/**
	 * Resets the panel by clearing the current image
	 */
	public void reset() {
		this.image = null;
		super.updateUI();
	}

	/**
	 * Updates the pane with the given image
	 * @param image the given {@link Image image}
	 */
	public void setImage( final Image image ) {
		this.image = image;
		if( image != null ) {
			super.setPreferredSize( new Dimension( image.getWidth( this ), image.getHeight( this ) ) );
			final Graphics g = super.getGraphics();
			if( g != null ) {
				g.drawImage( image, 0, 0, this );
			}
		}
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint( final Graphics g ) {
		g.drawImage( image, 0, 0, this );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#update(java.awt.Graphics)
	 */
	public void update( final Graphics g ) {
		g.drawImage( image, 0, 0, this );
	}

}