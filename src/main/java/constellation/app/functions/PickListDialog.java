package constellation.app.functions;

import static constellation.app.functions.IndexedModelElement.createIndexedSet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.drawing.elements.ModelElement;
import constellation.functions.PickListObserver;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.AcceptButton;
import constellation.ui.components.buttons.CancelButton;

/** 
 * Pick List Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PickListDialog extends CxDialog {
	// singleton instance
	private static PickListDialog instance = null;
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon[] NUMBERS		= {
			cxm.getIcon( "images/dialog/picklist/black/1.png" ),
			cxm.getIcon( "images/dialog/picklist/black/2.png" ),
			cxm.getIcon( "images/dialog/picklist/black/3.png" ),
			cxm.getIcon( "images/dialog/picklist/black/4.png" ),
			cxm.getIcon( "images/dialog/picklist/black/5.png" ),
			cxm.getIcon( "images/dialog/picklist/black/6.png" ),
			cxm.getIcon( "images/dialog/picklist/black/7.png" ),
			cxm.getIcon( "images/dialog/picklist/black/8.png" ),
			cxm.getIcon( "images/dialog/picklist/black/9.png" ),
			cxm.getIcon( "images/dialog/picklist/black/10.png" )
	};
	
	// internal fields
	private final PickListObserver listener;
	private List<ModelElement> elements;
	private JList selectionList;
	
	/**
	 * Creates a new pick list dialog
	 * @param controller the given {@link ApplicationController controller}
	 * @param listener the given {@link PickListObserver listener}
	 */
	private PickListDialog( final ApplicationController controller, 
						    final PickListObserver listener ) {
		super( controller, "Pick List" );			
		this.listener = listener;
		
		// construct the dialog
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Initializes the dialog
	 * @param controller the given {@link ApplicationController controller}
	 * @param listener the given {@link PickListObserver listener}
	 */
	public static PickListDialog init( final ApplicationController controller, 
			  				 		   final PickListObserver listener ) {
		if( instance == null ) {
			instance = new PickListDialog( controller, listener );
		}
		return instance;
	}
	
	/** 
	 * Returns the singleton instance of the class
	 * @return the singleton instance of the class
	 */
	public static PickListDialog getInstance() {
		if( instance == null ) {
			throw new IllegalStateException( "The pick list dialog must first be initialized" );
		}
		return instance;
	}
	
	/**
	 * Opens the pick list to allow the choosing of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link Point click point}
	 * @param elements the given {@link ModelElement drawing elements}
	 */
	public void pickElement( final ApplicationController controller, 
						 	 final Point clickPoint, 
						 	 final ModelElement[] elements ) {
		// update the list
		this.elements = Arrays.asList( elements );
		this.selectionList.setListData( createIndexedSet( elements ) );
		
		// position the dialog
		super.setLocation( clickPoint.x + 100, clickPoint.y );
		super.pack();
		super.setVisible( true );
	}
	
	/**
	 * Opens the pick list to allow the choosing of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link Point click point}
	 * @param elements the given {@link ModelElement drawing elements}
	 */
	public void pickElement( final ApplicationController controller, 
						 	 final Point clickPoint, 
						 	 final Collection<ModelElement> elements ) {
		// update the list
		this.elements = new ArrayList<ModelElement>( elements );
		this.selectionList.setListData( createIndexedSet( elements ) );
		
		// determine where to position the dialog
		if( clickPoint != null ) {
			super.setLocation( clickPoint.x + 100, clickPoint.y );
		}
		
		// make the dialog visible
		super.pack();
		super.setVisible( true );
	}
	
	/**
	 * Selects a specific element by its index
	 * @param index the given element index 
	 */
	public boolean selectIndex( final int index ) {
		if( elements != null ) {
			// invoke the "call-back" method
			listener.elementSelected( controller, elements.get( index ) );
			
			// clear the picked element
			final GeometricModel model = controller.getModel();
			model.clearPickedElement();
			
			// dispose of the pick list dialog
			PickListDialog.this.setVisible( false );
			
			// request a redraw
			controller.requestRedraw();
			return true;
		}
		return false;
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link JComponent pane}
	 */
	private JComponent createContentPane() {
		// create a list component
		selectionList = new JList();
		selectionList.setCellRenderer( new MyCellRenderer( selectionList.getCellRenderer() ) );
		selectionList.setPreferredSize( new Dimension( 250, 150 ) );
		selectionList.addListSelectionListener( new ClickElementAction() );
		
		// create the main panel
		final CxPanel cp = new CxPanel();
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		int row = -1;
		
		// row #1
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, selectionList, GridBagConstraints.NORTHWEST );
		cp.gbc.gridwidth = 1;
		
		// row #2
		cp.gbc.weightx = 1;
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.attach( 0, ++row, new CancelButton( new CancelAction() ) );
		cp.gbc.anchor = GridBagConstraints.NORTHEAST;
		cp.attach( 1,   row, new AcceptButton( new PickElementAction() ) );
		return cp;
	}
	
	/** 
	 * Internal List Cell Renderer
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyCellRenderer implements ListCellRenderer {
		private final ListCellRenderer renderer;
		
		/**
		 * Creates a new internal list cell renderer
		 * @param renderer the system {@link ListCellRenderer list cell renderer}
		 */
		public MyCellRenderer( final ListCellRenderer renderer ) {
			this.renderer = renderer;
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent( final JList list, 
													   final Object value,
													   final int index, 
													   final boolean isSelected, 
													   final boolean cellHasFocus ) {
			// get the component
			final Component comp = 
				renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			
			// get the index of the element
			int itemNo = -1;
			if( value instanceof IndexedModelElement ) {
				final IndexedModelElement element = (IndexedModelElement)value;
				itemNo = element.getIndex();
			}
			
			// is the component a label?
			if( ( itemNo >= 0 && itemNo <= 9 ) && ( comp instanceof JLabel ) ) {
				final JLabel label = (JLabel)comp;
				label.setIcon( NUMBERS[ itemNo ] );
			}
			
			return comp;
		}
		
	}
	
	/** 
	 * Cancel Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class CancelAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// dispose of the pick list dialog
			PickListDialog.this.setVisible( false );
			
			// clear the "picked" element
			controller.getModel().clearPickedElement();
			
			// request a redraw
			controller.requestRedraw();
		}
		
	}
	
	/**
	 * Click Element Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class ClickElementAction implements ListSelectionListener {

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged( final ListSelectionEvent event ) {
			final ModelElement selectedElement = (ModelElement)selectionList.getSelectedValue();
			if( selectedElement != null ) {
				// get the model instance
				final GeometricModel model = controller.getModel();
				
				// clear the old picked element
				model.clearPickedElement();
				
				// pick the new element
				model.setPickedElement( selectedElement );
				
				// request a redraw
				controller.requestRedraw();
			}
		}
	}
	
	/**
	 * Pick Element Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class PickElementAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			final ModelElement selectedElement = (ModelElement)selectionList.getSelectedValue();
			if( selectedElement != null ) {
				// invoke the "call-back" method
				listener.elementSelected( controller, selectedElement );
				
				// clear the picked element
				final GeometricModel model = controller.getModel();
				model.clearPickedElement();
				
				// dispose of the pick list dialog
				PickListDialog.this.setVisible( false );
				
				// request a redraw
				controller.requestRedraw();
			}
		}
	}

}
