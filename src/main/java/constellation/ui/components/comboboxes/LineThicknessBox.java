package constellation.ui.components.comboboxes;

import static java.lang.String.format;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import constellation.CxContentManager;

/**
 * Line Thickness ComboBox
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class LineThicknessBox extends JComboBox {
	private final CxContentManager cxm = CxContentManager.getInstance();
	private final Icon[] icons = {
			cxm.getIcon( "images/attributes/thickness/size_1.png" ),
			cxm.getIcon( "images/attributes/thickness/size_2.png" ),
			cxm.getIcon( "images/attributes/thickness/size_3.png" ),
			cxm.getIcon( "images/attributes/thickness/size_4.png" ),
			cxm.getIcon( "images/attributes/thickness/size_5.png" ),
			cxm.getIcon( "images/attributes/thickness/size_6.png" ),
			cxm.getIcon( "images/attributes/thickness/size_7.png" ),
			cxm.getIcon( "images/attributes/thickness/size_8.png" ),
			cxm.getIcon( "images/attributes/thickness/size_9.png" ),
			cxm.getIcon( "images/attributes/thickness/size_10.png" )
	};
	
	/**
	 * Default constructor
	 */
	public LineThicknessBox() {
		super( new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } );
		super.setRenderer( new MyCellRenderer( super.getRenderer() ) );
	}
	
	/**
	 * Resets the component to it's initial state
	 */
	public void reset() {
		super.setSelectedIndex( 0 );
	}

	/** 
	 * Returns the selected thickness
	 * @return the given thickness
	 */
	public int getSelectedThickness() {
		// get the selected index
		final int index = super.getSelectedIndex();
		
		// set the appropriate pattern reference
		return index;
	}
	
	/**
	 * Sets the selected thickness
	 * @param thickness the given thickness value
	 */
	public void setSelectedThickness( final int thickness ) {
		setSelectedIndex( thickness );
	}
	
	/** 
	 * Line Thickness Cell Renderer
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyCellRenderer implements ListCellRenderer {
		private final ListCellRenderer renderer;
		
		/** 
		 * Creates a new Line Thickness Cell Renderer
		 * @param renderer the given {@link ListCellRenderer
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
			// get the component as returned from the system cell renderer
			final Component comp = 
				renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			
			// if the component is a label, attach the icon to it
			if( comp instanceof JLabel ) {
				// lookup the thickness icon
				final int thickness = (Integer)value;
				final Icon icon = icons[ thickness ];	
				
				// set the icon
				final JLabel label = (JLabel)comp;
				label.setIcon( icon );
				label.setText( format( "%d", thickness + 1 ) );
			}
			
			return comp;
		}
		
	}

}
