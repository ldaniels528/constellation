package constellation.app.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Constellation Drawing Pane
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class DrawingPane extends JPanel {
	private Image buffer;
	
	/**
	 * Creates a new drawing pane instance
	 */
	public DrawingPane() {
		super( true );
		super.setBorder( BorderFactory.createRaisedBevelBorder() );
	}
	
	/**
	 * Initializes the drawing surface
	 */
	public void init() {
		// get the frame dimensions
		final Insets insets = this.getInsets();
		final int width 	= this.getWidth() - ( insets.left + insets.right );
		final int height	= this.getHeight() - ( insets.top + insets.bottom );
		this.buffer 		= super.createImage( width, height );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent( final Graphics g ) {
		render( g );
	}
	
	/**
	 * Renders the off-screen image
	 */
	public void render() {
		final Graphics g = super.getGraphics();
		render( g );
	}
	
	/**
	 * Renders the off-screen image on to the given graphics context
	 * @param g the given {@link Graphics graphics context}
	 */
	public void render( final Graphics g ) {
		g.drawImage( buffer, 0, 0, this );
	}
	
	/**
	 * Returns the off-screen rendering buffer
	 * @return the off-screen rendering buffer
	 */
	public Image getBuffer() {
		return buffer;
	}

}
