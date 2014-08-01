package constellation.ui.components.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import constellation.CxFontManager;

/**
 * This is the base class for all dynamically updating menus
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxMenu extends JMenu {
	
	/**
	 * Creates a new menu 
	 * @param label the given menu label
	 */
	public CxMenu( final String label ) {
		super( label );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}
	
	/**
	 * Attaches a new menu item to the menu
	 * @param menuItem the given {@link JMenuItem menu item}
	 */
	public JMenuItem add( final JMenuItem menuItem ) {
		// add the component
		super.add( menuItem );
			
		// setup the appropriate font
		CxFontManager.setDefaultFont( menuItem );
		
		// return the menu item
		return menuItem;
	}
	
}