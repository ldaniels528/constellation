package constellation.app.ui.informationbar;

import static constellation.ui.components.buttons.CxButton.createBorderlessButton;
import static java.lang.String.format;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.CxFontManager;
import constellation.app.ui.HelpDialog;
import constellation.functions.Function;
import constellation.functions.FunctionManager;
import constellation.functions.FunctionSet;
import constellation.ui.components.CxPanel;

/**
 * Constellation Function Selection Pane
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class FunctionSelectionPane extends CxPanel {
	// logger instance
	private final Logger logger = Logger.getLogger( getClass() );
	
	// icon declarations
	private final CxContentManager cxm = CxContentManager.getInstance();
	private final Icon DIALOG_ICON  = cxm.getIcon( "images/informationbar/dialog.gif" );
	private final Icon HELP_ICON	= cxm.getIcon( "images/informationbar/help16.png" );
	
	// internal fields
	private final ApplicationController controller;
	private final FunctionComboBox functionBox;
	private final FunctionManager functionMgr;
	private final JLabel functionLabel;
	
	/**
	 * Creates a new function information panel
	 * @param controller the given {@link ApplicationController controller}
	 */
	public FunctionSelectionPane( final ApplicationController controller ) {
		// create the status panel
		super.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		//super.setPreferredSize( new Dimension( 224, 24 ) );
		super.gbc.anchor = GridBagConstraints.WEST;
		super.gbc.insets = new Insets( 0, 1, 0, 1 );
		
		// capture the controller instance
		this.controller	= controller;
		this.functionMgr= FunctionManager.getInstance();
		
		// attach the components
		// column #1: Hide/Show Input Dialog Button
		super.gbc.fill = GridBagConstraints.VERTICAL;
		super.attach( 0, 0, createBorderlessButton( DIALOG_ICON, new DialogTriggerAction( controller ), "Hide/show input dialog" ) );
		
		// column #2: Function Family Label
		super.gbc.fill = GridBagConstraints.VERTICAL;
		super.attach( 1, 0, functionLabel = new JLabel( "CIRCLE" ) );
		
		// column #3: Function List Box
		super.gbc.fill = GridBagConstraints.HORIZONTAL;
		super.attach( 2, 0, functionBox	= new FunctionComboBox(),  GridBagConstraints.WEST );
		
		// column #4: Help Button
		super.gbc.anchor = GridBagConstraints.EAST;
		super.gbc.fill = GridBagConstraints.VERTICAL;
		super.attach( 3, 0, createBorderlessButton( HELP_ICON, new LaunchHelpAction( controller ), "Get help on this function" ) );
		
		// setup the appropriate font
		CxFontManager.setSmallFont( functionLabel );
	}
	
	/**
	 * Sets the function information with the given text
	 * @param text the given function label text
	 */
	public void setFunctionInformation( final Function function ) {
		functionLabel.setText( format( "%-6s", function.getFamilyName() ) );
		functionBox.update( function );
	}
	
	/** 
	 * Function ComboBox
	 * @author lawrence.daniels@gmail.com
	 */
	private class FunctionComboBox extends JComboBox {
		
		/**
		 * Default Constructor
		 */
		public FunctionComboBox() {
			super( new FunctionComboBoxModel( FunctionManager.getInstance().getFunctions() ) );
			
			// setup the appropriate font
			CxFontManager.setSmallFont( this );
			
			// attach the listener
			super.addActionListener( new FunctionChangeAction() );
		}
		
		/** 
		 * Returns the selected function
		 * @return the selected {@link Function function}
		 */
		public Function getSelectedFunction() {
			return (Function)super.getSelectedItem();
		}
		
		/**
		 * Updates the comboBox with the functions based on the
		 * given function's family.
		 * @param function the given {@link Function function}
		 */
		public void update( final Function function ) {
			// lookup the function set
			final FunctionSet functionSet = functionMgr.getFunctionSet( function.getFamilyName() );
			
			// if the function set was found, proceed
			if( functionSet != null ) {
			 	super.setModel( new FunctionComboBoxModel( functionSet.getFunctions() ) );
			 	super.setSelectedItem( function );
			}
			else {
				logger.info( format( "No function set found for function family '%s'", function.getFamilyName() ) );
			}
		} 
	}
	
	/** 
	 * Function ComboBox Model
	 * @author lawrence.daniels@gmail.com
	 */
	private class FunctionComboBoxModel extends DefaultComboBoxModel {
		
		/**
		 * Default Constructor
		 */
		public FunctionComboBoxModel( final Collection<Function> functions ) {
			super( functions.toArray( new Function[ functions.size() ] ) );
		}
	}
	
	/** 
	 * Function Change Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class FunctionChangeAction implements ActionListener {

		/* 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the function comboBox
			final FunctionComboBox comboBox = (FunctionComboBox)event.getSource();
			
			// get the selected function
			final Function function = comboBox.getSelectedFunction();
			
			// set the active function
			controller.setActiveFunction( function );
		}
	}

	/** 
	 * Dialog Trigger Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class DialogTriggerAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new dialog trigger action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public DialogTriggerAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the active function
			final Function function = controller.getActiveFunction();
			
			// hide/show the input dialog
			function.toggleDialogVisibility();
		}
	}
	
	/** 
	 * Launch Help Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class LaunchHelpAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a launch action for the Help dialog
		 * @param controller the given {@link ApplicationController controller}
		 */
		public LaunchHelpAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the active function
			final Function activeFunction = controller.getActiveFunction();
			
			// launch the dialog
			final HelpDialog dialog = HelpDialog.getInstance( controller );
			dialog.makeVisible();
			
			// load the help page for the active function
			dialog.loadPage( activeFunction );
		}
	}
	
}
