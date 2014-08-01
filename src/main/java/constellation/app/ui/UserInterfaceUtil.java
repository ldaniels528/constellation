package constellation.app.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;

import constellation.app.preferences.CxSystemPreferences;

/** 
 * Constellation User Interface Utility
 * @author lawrence.daniels@gmail.com
 */
public class UserInterfaceUtil {
	
	/**
	 * Returns the dimensions of the full screen
	 * @return the {@link Dimension dimensions} of the full screen
	 */
	public static Dimension getViewableDimensions( final Component comp ) {
		// get the components needed to determine the display-able area
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final GraphicsConfiguration gc = comp.getGraphicsConfiguration();
		
		final Dimension screenSize = toolkit.getScreenSize();
		final Insets insets = toolkit.getScreenInsets( gc );
		
		// get the system preferences
		final CxSystemPreferences systemPreferences = CxSystemPreferences.getInstance();
		
		// get the desired panel width
		int width = systemPreferences.getWidth();
		if( width == 0 ) {
			width = screenSize.width - ( insets.left + insets.right );
		}
		
		// get the desired panel height
		int height = systemPreferences.getHeight();
		if( height == 0 ) {
			height = screenSize.height - ( insets.top + insets.bottom );
		}
	
		// return the dimensions
		return new Dimension( width, height );
	}

}
