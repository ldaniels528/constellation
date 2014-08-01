package constellation.ui.components.menu;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import constellation.CxFontManager;

/**
 * This is the base class for all dynamic menu items
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxMenuItem extends JMenuItem {
	
	/**
	 * Creates a new menu item
	 * @param name the name/label of the menu item
	 */
	public CxMenuItem( final String name ) {
		super( name );

		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Creates a new menu item
	 * @param name the name/label of the menu item
	 * @param icon the graphical icon for the menu item
	 * @param keyStroke the activation key stroke
	 * @param listener the {@link ActionListener action listener}
	 */
	public CxMenuItem( final String name, 
					   final Icon icon,
					   final KeyStroke keyStroke,
					   final ActionListener listener ) {
		super( name );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
		
		// setup the menu item
		super.addActionListener( listener );
		super.setAccelerator( keyStroke );
		super.setIcon( icon );
	}
	
	/**
	 * Creates a new menu item
	 * @param name the name/label of the menu item
	 * @param icon the graphical icon for the menu item
	 * @param keyStroke the activation key stroke
	 * @param listener the {@link ActionListener action listener}
	 * @param menuMode the {@link MenuModes compatible menu contexts}
	 */
	public CxMenuItem( final String name, 
					   final Icon icon,
					   final ActionListener listener,
					   final MenuModes menuMode ) {
		this( name, icon, null, listener );
	}
	
	/**
	 * Creates a new menu item
	 * @param name the name/label of the menu item
	 * @param icon the graphical icon for the menu item
	 * @param keyStroke the activation key stroke
	 * @param listener the {@link ActionListener action listener}
	 */
	public CxMenuItem( final String name, 
					   final Icon icon,
					   final ActionListener listener ) {
		this( name, icon, null, listener );
	}

}