package constellation.app.ui.informationbar;

import static constellation.app.functions.layout.FilterManagementDialog.FILTER_ICON;
import static constellation.ui.components.buttons.CxButton.createBorderlessButton;
import static java.lang.String.format;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;

import constellation.ApplicationController;
import constellation.app.functions.layout.FilterManagementDialog.FilterManagementAction;
import constellation.model.GeometricModel;
import constellation.model.Layer;
import constellation.ui.components.CxPanel;
import constellation.ui.components.comboboxes.LayerComboBox;

/**
 * Constellation Layer Panel
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
class LayerPane extends CxPanel {
	private static final String DEFAULT_LAYER = "001";
	
	// internal fields
	private final ApplicationController controller;
	private final LayerComboBox layerComboBox;
	
	/**
	 * Creates a new layer choosing pane
	 * @param controller the given {@link ApplicationController application frame}
	 */
	public LayerPane( final ApplicationController controller ) {
		this.controller = controller;
		
		// create the comboBox
		layerComboBox = new LayerComboBox();
		layerComboBox.setEditable( true );
		layerComboBox.addActionListener( new LayerSelectionHandler() );
		layerComboBox.setToolTipText( "Change the current layer" );
		
		// create the status panel
		super.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		super.gbc.anchor = GridBagConstraints.WEST;
		super.attach( 0, 0, createBorderlessButton( FILTER_ICON, new FilterManagementAction( controller ), "Filter management" ) );
		super.attach( 1, 0, layerComboBox, GridBagConstraints.WEST );
	}
	
	/**
	 * Updates the collection of layers
	 * @param layers the given collection of {@link Layer layers}
	 */
	public void update( final Collection<Layer> layers ) {
		//layerComboBox.update( layers );
	}
	
	/**
	 * Layer Selection Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class LayerSelectionHandler implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// check the selected element
			final String value1 = (String)layerComboBox.getSelectedItem();
			final String value2 = getLayerValue();
			if( !value2.equals( value1 ) ) {
				layerComboBox.setSelectedItem( value2 );
			}
			
			// set the current layer
			final GeometricModel model = controller.getModel();
			model.setDefaultLayer( layerComboBox.getSelectedIndex() );
			
			// notify operator
			controller.setStatusMessage( format( "Layer changed to %s", layerComboBox.getSelectedItem() ) );
			
			// request a redraw
			controller.requestRedraw();
		}
		
		/**
		 * Retrieves a validated value for the current layer
		 * @return a validated value for the current layer
		 */
		private String getLayerValue() {
			// check the selected element
			String value = (String)layerComboBox.getSelectedItem();
			
			// if it's null
			if( value == null ) {
				value = DEFAULT_LAYER;
			}
			
			// is it less than 3 digits?
			while( value.length() < 3 ) {
				value = "0" + value;
			}
			
			// is it over 3 digits
			if( value.length() > 3 ) {
				value = DEFAULT_LAYER;
			}
			
			// test numeric value
			try {
				final int number = Integer.parseInt( value );
				if( number < 1 ) {
					value = "001";
				}
				else if( number > 256 ) {
					value = "256";
				}	
			}
			catch( final NumberFormatException e ) {
				value = DEFAULT_LAYER;
			}
			
			return value;
		}
		
	}

}
