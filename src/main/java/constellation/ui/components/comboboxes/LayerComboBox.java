package constellation.ui.components.comboboxes;

import static constellation.model.Filter.TOTAL_LAYERS;

import javax.swing.JComboBox;

/**
 * Constellation Layer ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class LayerComboBox extends JComboBox {
	private static String[] LAYERS = createLayerIds();
	
	/**
	 * Default constructor
	 */
	public LayerComboBox() {
		super( LAYERS );
	}
	
	/**
	 * Resets the component
	 */
	public void reset() {
		this.setSelectedIndex( 0 );
	}
	
	/**
	 * Creates the array of layer identifiers
	 * @return the array of layer identifiers
	 */
	private static String[] createLayerIds() {
		final String[] layers = new String[ TOTAL_LAYERS ];
		for( int n = 0; n < layers.length; n++ ) {
			layers[n] = String.format( "%03d", n+1 );
		}
		return layers;
	}
	
}
