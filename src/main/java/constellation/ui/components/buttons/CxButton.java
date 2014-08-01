package constellation.ui.components.buttons;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Constellation Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxButton extends JButton {
	
	/**
	 * Creates a new button with an icon
	 * @param icon the given {@link Icon icon}
	 */
	public CxButton( final Icon icon ) {
		super( icon );
		super.setCursor( getPredefinedCursor( HAND_CURSOR ) );
	}
	
	/**
	 * Creates a new button with a text label
	 * @param label the given text label
	 */
	public CxButton( final String label ) {
		super( label );
		super.setCursor( getPredefinedCursor( HAND_CURSOR ) );
	}
	
	/**
	 * Creates a new button with a text label
	 * @param label the given text label
	 * @param listener the given {@link ActionListener listener}
	 */
	public CxButton( final String label, final ActionListener listener ) {
		super( label );
		super.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		super.addActionListener( listener );
	}
	
	/**
	 * Creates a new button
	 * @param icon the given image resource key
	 * @param listener the given action listener
	 * @param toolTip the given tool tip 
	 * @return the toggle button
	 */
	public static CxButton createButton( final Icon icon, 
										 final ActionListener listener, 
										 final String toolTip ) {
		final CxButton button = new CxButton( icon );
		button.setActionCommand( toolTip );
		button.addActionListener( listener );
		button.setToolTipText( toolTip );
		return button;
	}

	/**
	 * Creates a new border-less button
	 * @param icon the given image resource key
	 * @param listener the given action listener
	 * @param command the given command 
	 * @return the toggle button
	 */
	public static CxButton createBorderlessButton( final Icon icon, 
										  		   final ActionListener listener, 
										  		   final String command ) {
		final CxButton button = new CxButton( icon );
		button.setActionCommand( command );
		button.addActionListener( listener );
		button.setToolTipText( command );
		button.setBorderPainted( false );
		button.setContentAreaFilled( false );
		button.setPreferredSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );
		return button;
	}

}
