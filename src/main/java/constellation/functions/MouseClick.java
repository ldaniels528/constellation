package constellation.functions;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Represents a mouse click action
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class MouseClick extends Point {
	// mouse button definitions
	public static final int BUTTON_SELECT 	= MouseEvent.BUTTON1;
	public static final int BUTTON_POPUP 	= MouseEvent.BUTTON3;
	public static final int BUTTON_INDICATE = MouseEvent.BUTTON2;
	
	// internal fields
	private final Rectangle rectangle;
	private final int button;
	
	/**
	 * Represents a mouse click
	 * @param p the given mouse {@link Point position}
	 * @param width the given selection width
	 * @param height the given selection height
	 * @param button the button that was clicked
	 */
	public MouseClick( final Point p, 
					   final int width, 
					   final int height, 
					   final int button ) {
		super( p );
		this.rectangle	= new Rectangle( x - ( width / 2 ), y - ( height / 2 ), width, height );
		this.button 	= button;
	}
	
	/** 
	 * Returns the click bounds
	 * @return the {@link Rectangle click bounds}
	 */
	public Rectangle getClickBounds() {
		return rectangle;
	}

	/**
	 * Returns the code of the button that was clicked
	 * @return the code of the button that was clicked
	 */
	public int getButton() {
		return button;
	}
	
}
