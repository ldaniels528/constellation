package constellation.ui.components.comboboxes;

import static constellation.model.Units.*;

import javax.swing.JComboBox;

/**
 * Constellation Units ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxUnitsBox extends JComboBox {
	
	/**
	 * Default constructor
	 */
	public CxUnitsBox() {
		super( UNITS );
	}

}
