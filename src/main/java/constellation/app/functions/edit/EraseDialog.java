package constellation.app.functions.edit;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxRadioButton;
import constellation.ui.components.fields.CxLabel;

/**
 * Erase Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class EraseDialog extends CxDialog {
	// singleton instance
	private static EraseDialog instance = null;
	
	// dialog icon declarations
	private final CxContentManager contentManager = CxContentManager.getInstance();
	private final Icon PICKED_ICON		= contentManager.getIcon( "images/dialog/erase/picked.png" );
	private final Icon SELECTED_ICON	= contentManager.getIcon( "images/dialog/erase/selected.png" );
	private final Icon POINTS_ICON 		= contentManager.getIcon( "images/dialog/erase/points.png" );
	private final Icon WINDOWED_ICON 	= contentManager.getIcon( "images/dialog/erase/windowed.png" );

	// internal fields
	private final EraseFunction function;
	private JRadioButton pickedBox;
	private JRadioButton selectedBox;
	private JRadioButton pointsBox;
	private JRadioButton windowedBox;
	
	/** 
	 * Creates a new line dialog instance
	 * @param controller the given {@link ApplicationController function controller}
	 * @param function the given {@link EraseFunction erase function}
	 */
	private EraseDialog( final ApplicationController controller, final EraseFunction function ) {
		super( controller, "Erase" );
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
		
		// capture the function
		this.function = function;
	}
	
	/**
	 * Returns the singleton dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 * @param function the given {@link EraseFunction erase function}
	 * @return the singleton dialog instance
	 */
	public static EraseDialog getInstance( final ApplicationController controller, final EraseFunction function ) {
		if( instance == null ) {
			instance = new EraseDialog( controller, function );
		}
		return instance;
	}
	
	/**
	 * Returns the erase method
	 * @return the {@link EraseMethods erase method}
	 */
	public EraseMethods getEraseMethod() {
		// is it Picked?
		if( pickedBox.isSelected() ) {
			return EraseMethods.PICKED;
		}
		
		// is it Selected?
		else if( selectedBox.isSelected() ) {
			return EraseMethods.SELECTED;
		}
		
		// is it Windowed?
		else if( windowedBox.isSelected() ) {
			return EraseMethods.WINDOWED;
		}
		
		// is it Points?
		else if( pointsBox.isSelected() ) {
			return EraseMethods.POINTS;
		}
		
		// unknown
		else {
			throw new IllegalStateException( "No erase method was selected" );
		}
	}
	
	/**
	 * Returns the content pane
	 * @return the content pane
	 */
	private JComponent createContentPane() {
		final CxPanel cp = new CxPanel();
		int row = -1;
		
		// put it all together
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, new CxLabel( "Choose Method" ) );
		cp.gbc.gridwidth = 1;
		
		// create the erase method change listener
		final EraseMethodChangeListener listener = new EraseMethodChangeListener();
		
		// row #1
		cp.attach( 0, ++row, pickedBox = new CxRadioButton( "Picked", listener ) );
		cp.attach( 1,   row, new JLabel( PICKED_ICON ) );
		
		// row #2
		cp.attach( 0, ++row, selectedBox = new CxRadioButton( "Selected", listener ) );
		cp.attach( 1,   row, new JLabel( SELECTED_ICON ) );
		
		// row #3
		cp.attach( 0, ++row, windowedBox = new CxRadioButton( "Windowed", listener ) );
		cp.attach( 1,   row, new JLabel( WINDOWED_ICON ) );
		
		// row #4
		cp.attach( 0, ++row, pointsBox = new CxRadioButton( "Points", listener ) );
		cp.attach( 1,   row, new JLabel( POINTS_ICON ), GridBagConstraints.NORTHWEST );
		
		// create the radio button group
		final ButtonGroup group = new ButtonGroup();
		group.add( pickedBox );
		group.add( selectedBox );
		group.add( windowedBox );
		group.add( pointsBox );
		
		// select the default radio button
		pickedBox.setSelected( true );
		return cp;
	}
	
	/** 
	 * Erase Method Change Listener
	 * @author lawrence.daniels@gmail.com
	 */
	private class EraseMethodChangeListener implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the activated button
			final EraseMethods method = getEraseMethod();
			
			// notify the function of the change
			function.methodChanged( controller, method );
		}
		
	}
	
}
