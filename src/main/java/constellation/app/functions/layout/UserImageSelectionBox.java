package constellation.app.functions.layout;

import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

import constellation.ApplicationController;
import constellation.drawing.entities.UserImage;

/**
 * Constellation Image Selection ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class UserImageSelectionBox extends JComboBox {
	private final ApplicationController controller;
	
	/**
	 * Default Constructor
	 */
	public UserImageSelectionBox( final ApplicationController controller, final ActionListener listener ) {
		super();
		this.controller = controller;
		super.setModel( new MyModel() );
		super.addActionListener( listener );
	}
	
	/** 
	 * Returns the selected image
	 * @return the selected {@link UserImage image}
	 */
	public UserImage getSelectedUserImage() {
		return (UserImage)super.getSelectedItem();
	}
	
	/**
	 * Resets the selection box
	 */
	public void reset() {
		super.setSelectedItem( null );
		super.updateUI();
	}
	
	/**
	 * Represents an internal-use only ComboBox model
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyModel implements ComboBoxModel {
		private Object selectedItem;

		/**
		 * Default constructor
		 */
		public MyModel() {
			super();
		}
		
		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ComboBoxModel#getSelectedItem()
		 */
		public Object getSelectedItem() {
			return selectedItem;
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
		 */
		public void setSelectedItem( final Object anItem ) {
			this.selectedItem = anItem;
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
		 */
		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		public Object getElementAt( final int index ) {
			final Object[] objects = controller.getModel().getUserImages().toArray();
			return objects[ index ];
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		public int getSize() {
			return controller.getModel().getUserImages().size();
		}

		/* 
		 * (non-Javadoc)
		 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
		 */
		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
		}
		
	}

}
