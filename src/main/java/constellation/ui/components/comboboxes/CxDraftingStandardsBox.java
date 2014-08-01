package constellation.ui.components.comboboxes;

import javax.swing.JComboBox;

import constellation.CxFontManager;

/**
 * Constellation Drafting Standards ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxDraftingStandardsBox extends JComboBox {
	private static final String[] DRAFTING_STANDARDS = {
		"(default)", "ISO", "AFNOR", "ANSI", 
		"BSI", "CSA", "DIN", "JIS"
	};
	
	/**
	 * Default constructor
	 */
	public CxDraftingStandardsBox() {
		super( DRAFTING_STANDARDS );
		
		// setup the appropriate font
		CxFontManager.setDefaultFont( this );
	}

}
