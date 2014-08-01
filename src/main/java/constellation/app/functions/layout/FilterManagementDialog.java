package constellation.app.functions.layout;

import static constellation.model.Filter.TOTAL_LAYERS;
import static java.lang.String.format;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.model.Filter;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.AcceptButton;
import constellation.ui.components.buttons.CancelButton;

/**
 * Filter Management Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class FilterManagementDialog extends CxDialog {
	// singleton instance
	public static FilterManagementDialog instance = null;
	
	// new filter constant
	private static final Filter NEW_FILTER = new NewFilter();
	
	// icon definition
	public static final Icon FILTER_ICON = 
		CxContentManager.getInstance().getIcon( "images/commands/layout/filter.png" );
	
	// internal fields
	private final FilterComboBox filterBox;
	private final JCheckBox[] checkBoxes;
	private final JTextField filterNameField;
	private final AcceptButton acceptButton;
	private final ApplyButton applyButton;
	private final CancelButton cancelButton;
	private Filter selectedFilter;
	private boolean creationState;
	
	/**
	 * Creates a new filter management dialog
	 * @param controller the given {@link ApplicationController controller}
	 */
	private FilterManagementDialog( final ApplicationController controller ) {
		super( controller, "Filters" );
		super.setDefaultCloseOperation( HIDE_ON_CLOSE );
		
		// set the initial creation state
		creationState	= false;
		
		// create the filter components
		filterBox 		= new FilterComboBox( controller );
		filterNameField	= new JTextField( 24 );
		applyButton		= new ApplyButton();
		acceptButton	= new AcceptButton( new AcceptAction() );
		cancelButton	= new CancelButton( new CancelAction() );
		
		// create the check-boxes
		checkBoxes = new JCheckBox[ TOTAL_LAYERS ];
		for( int n = 0; n < TOTAL_LAYERS; n++ ) {
			checkBoxes[n] = new JCheckBox( format( "%03d", n+1 ) );
		}
		
		// setup the content pane
		super.setContentPane( constructContentPane( creationState ) );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the singleton instance of the filter management dialog
	 * @param controller the {@link ApplicationController controller}
	 * @return the {@link FilterManagementDialog filter management dialog} instance
	 */
	public static FilterManagementDialog getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new FilterManagementDialog( controller );
		}
		return instance;
	}
	
	/**
	 * Resets the dialog
	 */
	public void reset() {
		// clear the selected filter
		selectedFilter = null;
		
		// update the button states
		updateButtonStates();
		
		// refresh the filter box
		filterBox.update( controller.getModel().getFilters() );
		
		// reset the filter selection
		filterBox.setSelectedItem( null );
		
		// disable the layers
		enableLayers( false );
		
		// refresh the dialog
		if( creationState ) {
			refresh( false );
		}
		
		// reset the check-boxes
		for( final JCheckBox checkBox : checkBoxes ) {
			checkBox.setSelected( false );
		}
	}	
	
	/**
	 * Updates the filter comboBox
	 * @param filters the given collection of {@link Filter filters}
	 */
	public void update( final Collection<Filter> filters ) {
		filterBox.update( filters );
	}
	
	/**
	 * Creates the filter based on the current settings
	 * @return the new {@link Filter filter}
	 */
	private Filter createFilter() {
		// get the filter
		final String name = filterNameField.getText().trim();
		
		// create the filter
		final Filter filter = new Filter( name );
		
		// turn the filters on/off
		saveFilterStates( filter );
		
		return filter;
	}
	
	/** 
	 * Loads the layer states into the check-box components
	 * @param filter the given {@link Filter filter}
	 */
	private void loadFilterStates( final Filter filter ) {
		for( int n = 0; n < TOTAL_LAYERS; n++ ) {
			checkBoxes[n].setSelected( filter.containsLayer( n ) );
		}
	}
	
	/**
	 * Persists the current layer settings to the filter
	 * @param filter the given {@link Filter filter}
	 */
	private void saveFilterStates( final Filter filter ) {
		for( int n = 0; n < TOTAL_LAYERS; n++ ) {
			if( checkBoxes[n].isSelected() ) {
				filter.setLayerState( n, true );
			}
		}
	}
	
	/**
	 * Enables/disables the layers
	 * @param enabled indicates whether the layers will be enabled
	 */
	private void enableLayers( final boolean enabled ) {
		for( final JCheckBox checkBox : checkBoxes ) {
			checkBox.setEnabled( enabled );
		}
	}

	/**
	 * Refreshes the filter management dialog
	 * @param newFilter indicates whether the dialog should be in the "create filter" state
	 */
	private void refresh( final boolean newFilter ) {
		// set the creation state
		creationState = newFilter;
		
		// update the button states
		updateButtonStates();
		
		// update the dialog
		super.setContentPane( constructContentPane( newFilter ) );
		super.pack();
	}

	/**
	 * Updates the button states to determine when certain
	 * buttons can be clicked.
	 */
	private void updateButtonStates() {
		// enable/disable the 'Apply' button
		applyButton.setEnabled( !creationState );
		
		// enable/disable the 'Accept' button
		acceptButton.setEnabled( creationState || ( selectedFilter != null ) );
		
		// enable/disable the 'Cancel' button
		cancelButton.setEnabled( creationState || ( selectedFilter != null ) );
	}
	
	/**
	 * Constructs the content pane
	 * @return the {@link JComponent content pane}
	 */
	private JComponent constructContentPane( final boolean newFilter ) {
		int row = -1;
		CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.WEST;
		
		// create a tab of layers
		final JTabbedPane layers = createLayerTabs();
		
		// create the action button panel
		final CxPanel buttons = new CxPanel();
		buttons.attach( 0, 0, cancelButton );
		buttons.attach( 1, 0, acceptButton );
		buttons.attach( 2, 0, applyButton );
		
		// row #1
		cp.attach( 0, ++row, new JLabel( "Filter:" ) );
		cp.attach( 1,   row, ( newFilter ? filterNameField : filterBox ) );	
		
		// row #2
		cp.gbc.gridwidth = 3;
		cp.gbc.fill = GridBagConstraints.HORIZONTAL;
		cp.attach( 0, ++row, layers );
		
		// row #3
		cp.attach( 0, ++row, buttons, GridBagConstraints.NORTHWEST );
		return cp;
	}
	
	/**
	 * Create the layer tabs
	 * @return the {@link JTabbedPane tabs}
	 */
	private JTabbedPane createLayerTabs() {
		int layer = 0;
		
		// create the tabs
		final JTabbedPane tab = new JTabbedPane();
		
		// create four tabs ( 4 x 64 = 256 )
		for( int n = 0; n < 4; n++ ) {
			
			// create a panel for the current tab
			final CxPanel cp = new CxPanel();
			
			// create the check-boxes (8x8 grid)
			for( int row = 0; row < 8; row++ ) {
				for( int col = 0; col < 8; col++ ) {
					cp.attach( col, row, checkBoxes[layer++] );
				}
			}
			
			// compute the start and end values for the tab label 
			final int start = (n*64)+1;
			final int end   = (n+1)*64;
			
			// assign the tab
			tab.add( format( "%03d-%03d", start, end ), cp );
		}
		return tab;
	}
	
	/**
	 * Filter Management Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class FilterManagementAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new filter management action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public FilterManagementAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			final FilterManagementDialog dialog = FilterManagementDialog.getInstance( controller );
			dialog.reset();
			dialog.makeVisible();
		}
		
	}
	
	/**
	 * Represents a special filter for triggering the creation
	 * of new filters
	 * @author lawrence.daniels@gmail.com
	 */
	private static class NewFilter extends Filter {

		/**
		 * Default constructor
		 */
		public NewFilter() {
			super( "<New>" );
		}	
	}
	
	/**
	 * Constellation Filter ComboBox
	 * @author lawrence.daniels@gmail.com
	 */
	private class FilterComboBox extends JComboBox {
		
		/**
		 * Creates a new filter comboBox
		 * @param filters the given collection of {@link Filter filters}
		 */
		public FilterComboBox( final ApplicationController controller ) {
			super( new FilterComboBoxModel( ) );
			super.addActionListener( new FilterSelectionAction() );
		}
		
		/**
		 * Returns the selected filter
		 * @return the selected {@link Filter filter}
		 */
		public Filter getSelectedFilter() {
			return (Filter)super.getSelectedItem();
		}
		
		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
		 */
		public void setSelectedItem( final Object anItem ) {
			super.setSelectedItem( anItem );
			FilterComboBox.this.updateUI();
		}
		
		/**
		 * Updates the filter comboBox
		 * @param filters the given collection of {@link Filter filters}
		 */
		public void update( final Collection<Filter> filters ) {
			final FilterComboBoxModel comboBoxModel = (FilterComboBoxModel)super.getModel();
			comboBoxModel.setFilters( filters );
			super.updateUI();
		}
	}
	
	/** 
	 * Constellation Filter ComboBox Model
	 * @author lawrence.daniels@gmail.com
	 */
	private class FilterComboBoxModel implements ComboBoxModel {
		private List<Filter> filters;
		private int selectedIndex;
		
		/**
		 * Creates a new Filter ComboBox Model instance
		 */
		public FilterComboBoxModel() {
			this.filters = new LinkedList<Filter>();
		}
		
		/**
		 * Adds a new filter to the model
		 * @param filter the given {@link Filter filter}
		 */
		public void addFilter( final Filter filter ) {
			synchronized( filters ) {
				filters.add( filter );
			}
		}
		
		/**
		 * Updates the collection of filters contained by this comboBox
		 * @param filterSet the given collection of {@link Filter filters}
		 */
		public void setFilters( final Collection<Filter> filterSet ) {
			synchronized( filters ) {
				// clear out the old filters
				filters.clear();
				filters.add( null );
				filters.add( NEW_FILTER );
				
				// if the filter set is not null, add the filter
				if( filterSet != null ) {
					filters.addAll( filterSet );
				}
				
				// adjust the selected index
				if( selectedIndex >= filters.size() ) {
					selectedIndex = filters.size() - 1;
				}
			}
 		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ComboBoxModel#getSelectedItem()
		 */
		public Object getSelectedItem() {
			synchronized( filters ) {
				return selectedIndex < filters.size() ? filters.get( selectedIndex ) : null;
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
		 */
		public void setSelectedItem( final Object anItem ) {
			synchronized( filters ) {
				this.selectedIndex = filters.indexOf( anItem );
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
		 */
		public void addListDataListener( final ListDataListener listener ) {
			// TODO Auto-generated method stub
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		public Object getElementAt( final int index ) {
			synchronized( filters ) {
				return filters.get( index );
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		public int getSize() {
			synchronized( filters ) {
				return filters.size();
			}
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
		 */
		public void removeListDataListener( final ListDataListener listener ) {
			// TODO Auto-generated method stub
		}
	}
	
	/**
	 * Filter Selection Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class FilterSelectionAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {			
			// get the selected filter
			final Filter filter = filterBox.getSelectedFilter();

			// enable/disable the layer input fields
			enableLayers( filter != null );
			
			// is it a new filter trigger?
			if( NEW_FILTER.equals( filter ) ) {
				refresh( true );
			}
			
			// otherwise, select the filter
			else {
				// select the filter
				selectedFilter = !NEW_FILTER.equals( filter ) ? filter : null;
				
				// if a filter was selected, refresh the layer states
				if( selectedFilter != null ) {
					loadFilterStates( selectedFilter );
				}
			}
			
			// update the button states
			updateButtonStates();
		}
	}
	
	/** 
	 * Apply Button
	 * @author lawrence.daniels@gmail.com
	 */
	private class ApplyButton extends JButton implements ActionListener {

		/**
		 * Default constructor
		 */
		public ApplyButton() {
			super( "Apply" );
			super.addActionListener( this );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			controller.getModel().setFilter( selectedFilter );
			controller.requestRedraw();
		}
	}
	
	/**
	 * Accept Button Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class AcceptAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// was a filter selected?
			if( selectedFilter != null ) {
				saveFilterStates( selectedFilter );
			}
			
			else {
				// create a new filter?
				if( creationState ) {
					// create the filter
					selectedFilter = createFilter();
					
					// add the filter to the comboBox model
					final FilterComboBoxModel comboBoxModel = (FilterComboBoxModel)filterBox.getModel();
					comboBoxModel.addFilter( selectedFilter );
					
					// add the filter to the geometric model
					controller.getModel().addFilter( selectedFilter );
					
					// refresh the dialog
					refresh( false );
					filterBox.setSelectedItem( selectedFilter );
				}
			}
		}
	}
	
	/**
	 * Cancel Button Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class CancelAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			JOptionPane.showConfirmDialog( FilterManagementDialog.this, "Are you sure?", "Cancel Operation", JOptionPane.YES_NO_OPTION );
			reset();
		}
	}

}
