package constellation.ui.components.buttons;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Icon;

/**
 * Constellation Icon Toggle Button
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxIconToggleButton extends CxToggleButton {
	private final Icon activeIcon;
	private final Icon inactiveIcon;
	private final String activeToolTipText;
	private final String inactiveToolTipText;
	
	/**
	 * Creates a new icon-based toggle button
	 * @param activeIcon the icon that represents the active (selected) state
	 * @param inactiveIcon the icon that represents the inactive (unselected) state
	 * @param listener the given {@link ActionListener action listener}
	 * @param activeToolTipText the given active (selected) tool tip text
	 * @param inactiveToolTipText the given inactive (unselected) tool tip text
	 * @param selected indicates whether the component should be in a selected state
	 */
	public CxIconToggleButton( final Icon activeIcon, 
								final Icon inactiveIcon,
								final ActionListener listener,
								final String activeToolTipText,
								final String inactiveToolTipText, 
								final boolean selected ) {
		super( inactiveIcon );
		this.activeIcon 			= activeIcon;
		this.inactiveIcon			= inactiveIcon;
		this.activeToolTipText		= activeToolTipText;
		this.inactiveToolTipText	= inactiveToolTipText;
		
		super.addActionListener( listener );
		super.setToolTipText( inactiveToolTipText );
		super.setPreferredSize( new Dimension( activeIcon.getIconWidth(), activeIcon.getIconHeight() ) );
		super.setBorderPainted( false );
		super.setContentAreaFilled( false );
		super.setSelected( selected );
		updateIcon();
	}
	
	/**
	 * Creates a new icon-based toggle button
	 * @param activeIcon the icon that represents the active (selected) state
	 * @param inactiveIcon the icon that represents the inactive (unselected) state
	 * @param listener the given {@link ActionListener action listener}
	 * @param toolTipText the given tool tip text
	 */
	public CxIconToggleButton( final Icon activeIcon, 
								final Icon inactiveIcon,
								final ActionListener listener,
								final String activeToolTipText,
								final String inactiveToolTipText ) {
		this( activeIcon, inactiveIcon, listener, activeToolTipText, inactiveToolTipText, false );
	}
	
	/**
	 * Updates the button
	 */
	public void updateIcon() {
		if( isSelected() ) {
			super.setIcon( activeIcon );
			super.setToolTipText( activeToolTipText );
		}
		else {
			super.setIcon( inactiveIcon );
			super.setToolTipText( inactiveToolTipText );
		}
	}
}