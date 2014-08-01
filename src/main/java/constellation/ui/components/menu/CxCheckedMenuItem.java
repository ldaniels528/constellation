package constellation.ui.components.menu;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import constellation.CxFontManager;

/**
 * Constellation Checked Menu Item
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxCheckedMenuItem extends JCheckBoxMenuItem {
	
	/**
	 * Creates a new checked menu item
	 * @param name the name/label of the menu item
	 * @param icon the graphical icon for the menu item
	 * @param keyStroke the activation key stroke
	 * @param listener the {@link ActionListener action listener}
	 */
	public CxCheckedMenuItem( final String name, 
		   	 				  final Icon icon,
		   	 				  final KeyStroke keyStroke, 
		   	 				  final ActionListener listener ) {
		super( name );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
		
		// setup the menu item
		super.setIcon( icon );
		super.setAccelerator( keyStroke );
		super.addActionListener( listener );
	}
}