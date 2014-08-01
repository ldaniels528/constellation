package constellation.ui.components;

import java.awt.GridLayout;

import javax.swing.JPanel;

import constellation.CxFontManager;

/**
 * Constellation Grid Panel
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxGridPanel extends JPanel {
	
	/**
	 * Default constructor
	 */
	public CxGridPanel() {
		super( new GridLayout( 1, 1 ), true );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}

}
