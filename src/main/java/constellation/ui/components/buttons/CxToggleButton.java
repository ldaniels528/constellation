package constellation.ui.components.buttons;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

import java.awt.Cursor;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * Constellation Toggle Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxToggleButton extends JToggleButton {
	
	/**
	 * Creates a new toggle button with a text label
	 * @param label the given text label
	 */
	public CxToggleButton( final String label ) {
		super( label );
		super.setCursor( getPredefinedCursor( HAND_CURSOR ) );
	}
	
	/**
	 * Creates a new toggle button with an icon
	 * @param icon the given {@link Icon icon}
	 */
	public CxToggleButton( final Icon icon ) {
		super( icon );
		super.setCursor( getPredefinedCursor( HAND_CURSOR ) );
	}
	
	/**
	 * Creates a new toggle button with a text label
	 * @param label the given text label
	 * @param listener the given {@link ActionListener listener}
	 */
	public CxToggleButton( final String label, final ActionListener listener ) {
		super( label );
		super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		super.addActionListener( listener );
	}
	
	/**
	 * Creates a new toggle button
	 * @param icon the given image resource key
	 * @param listener the given action listener
	 * @param command the given command 
	 * @param enabled indicates whether the button should be enabled
	 * @return the toggle button
	 */
	public static JToggleButton createToggleButton( final Icon icon, 
											  		final ActionListener listener, 
											  		final String command, 
											  		final boolean enabled ) {
		final CxToggleButton button = new CxToggleButton( icon );
		button.setActionCommand( command );
		button.addActionListener( listener );
		button.setToolTipText( command );
		button.setSelected( enabled );
		button.setBorderPainted( false );
		return button;
	}

}
