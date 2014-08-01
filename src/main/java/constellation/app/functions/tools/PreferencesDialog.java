package constellation.app.functions.tools;

import static constellation.drawing.DefaultDrawingColors.BACKGROUND_COLOR;
import static constellation.drawing.DefaultDrawingColors.GRID_COLOR;
import static constellation.drawing.DefaultDrawingColors.HIGHLIGHT_COLOR;
import static constellation.drawing.DefaultDrawingColors.PHANTOM_ELEMENT_COLOR;
import static constellation.drawing.DefaultDrawingColors.SELECT_COLOR;
import static constellation.drawing.DefaultDrawingColors.TEMP_GEOM_COLOR;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import constellation.ApplicationController;
import constellation.drawing.LinePatterns;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.AcceptButton;
import constellation.ui.components.buttons.CancelButton;
import constellation.ui.components.buttons.CxCustomColorButton;
import constellation.ui.components.comboboxes.ColorSelectionBox;
import constellation.ui.components.comboboxes.CxDraftingStandardsBox;
import constellation.ui.components.comboboxes.CxUnitsBox;
import constellation.ui.components.comboboxes.LinePatternBox;
import constellation.ui.components.comboboxes.LineThicknessBox;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.fields.CxStringField;
import constellation.ui.components.fields.CxTextArea;

/**
 * Constellation Preferences Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends CxDialog {
	// singleton instance
	private static PreferencesDialog instance = null;
	
	// internal fields
	private CxCustomColorButton bkgColorButton;
	private CxCustomColorButton gridColorButton;
	private CxCustomColorButton ghostGeomColorButton;
	private CxCustomColorButton hiliteColorButton;
	private CxCustomColorButton selectColorButton;
	private CxCustomColorButton tempGeomColorButton;
	private CxTextArea descriptionField;
	private CxStringField authorNameField;
	private CxStringField authorOrganizationField;
	private CxDraftingStandardsBox draftingStandardsBox;
	private CxDecimalField scaleField;
	private CxUnitsBox unitsBox;
	private LinePatternBox patternBox;
	private ColorSelectionBox colorBox;
	private LineThicknessBox thicknessBox;
	private JCheckBox antiAliasingBox;
	private JCheckBox debugModeBox;
	
	/** 
	 * Creates a new preferences dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 */
	private PreferencesDialog( final ApplicationController controller ) {
		super( controller, "Model Preferences" );
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getUpperRightAnchorPoint( this ) );
	}
	
	/** 
	 * Returns the single instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the {@link PreferencesDialog dialog} instance
	 */
	public static PreferencesDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {
			// instantiate an instance of the dialog
			instance = new PreferencesDialog( controller );
		}
		return instance;
	}
	
	/** 
	 * Updates the dialog with the current model settings
	 */
	public void load() {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the preferences instance
		final SystemPreferences preferences = controller.getSystemPreferences();
		
		// import the general information
		descriptionField.setText( model.getDescription() );
		descriptionField.setCaretPosition( 0 );
		authorNameField.setText( model.getAuthorName() );
		authorOrganizationField.setText( model.getAuthorOrganization() );
		
		// import the model units
		draftingStandardsBox.setSelectedIndex( model.getDraftingStandard().ordinal() );
		scaleField.setDecimal( model.getScale() );
		unitsBox.setSelectedItem( model.getUnit() );
		
		// import the defaults
		colorBox.setSelectedColor( model.getDefaultColor() );
		patternBox.setSelectedPattern( model.getDefaultPattern() );
		thicknessBox.setSelectedIndex( model.getDefaultThickness() );
		
		// import the rending hints
		antiAliasingBox.setSelected( preferences.isAntiAliasing() );
		
		// import the debug mode setting
		final SystemPreferences systemPreferences = controller.getSystemPreferences();
		debugModeBox.setSelected( systemPreferences.isDebugMode() );
	}
	
	/** 
	 * Commits the current selections
	 */
	public void commit() {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the preferences instance
		final SystemPreferences preferences = controller.getSystemPreferences();
		
		// update the model description
		model.setDescription( descriptionField.getText() );
		
		// update the defaults
		model.setDefaultColor( colorBox.getSelectedColor() );
		model.setDefaultPattern( LinePatterns.values()[ patternBox.getSelectedIndex() ] );
		model.setDefaultThickness( thicknessBox.getSelectedIndex() );
		
		// update the colors
		preferences.setBackgroundColor( bkgColorButton.getSelectedColor() );
		preferences.setPhantomColor( ghostGeomColorButton.getSelectedColor() );
		preferences.setGridColor( gridColorButton.getSelectedColor() );
		preferences.setHighlightedGeometryColor( hiliteColorButton.getSelectedColor() );
		preferences.setSelectedGeometryColor( selectColorButton.getSelectedColor() );
		preferences.setTemporaryElementColor( tempGeomColorButton.getSelectedColor() );
		
		// update the rending hints
		preferences.setAntiAliasing( antiAliasingBox.isSelected() );
		
		// update the debug mode setting
		final SystemPreferences systemPreferences = controller.getSystemPreferences();
		systemPreferences.setDebugMode( debugModeBox.isSelected() );
		
		// request a redraw
		controller.requestRedraw();
	}
	
	/**
	 * Creates the content pane
	 * @return the content {@link CxPanel pane}
	 */
	private JComponent createContentPane() {
		// create a tab pane
		final JTabbedPane tabs = new JTabbedPane();
		tabs.add( "General", createGeneralTab() );
		tabs.add( "Model", createModelTab() );
		tabs.add( "Colors", createColorsTab() );
		
		// row #1
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		int row = -1;
		
		// row #1
		cp.gbc.fill	= GridBagConstraints.BOTH;
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, tabs );
		cp.gbc.gridwidth = 1;
		
		// row #2
		cp.gbc.fill	= GridBagConstraints.NONE;
		cp.attach( 0, ++row, new CancelButton( new CancelAction() ) );
		cp.attach( 1,   row, new AcceptButton( new AcceptAction() ), GridBagConstraints.NORTHEAST );
		return cp;
	}
	
	/**
	 * Creates the 'General' tab
	 * @return the 'General' tab
	 */
	private JComponent createGeneralTab() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		int row = -1;
		
		// create the text area
		descriptionField = new CxTextArea( 8, 40, true );
		
		// row #1
		cp.gbc.fill = GridBagConstraints.NONE;
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, new JLabel( "Description") );
		
		// row #2
		cp.gbc.fill	= GridBagConstraints.BOTH;
		cp.gbc.gridwidth = 2;
		cp.attach( 0, ++row, new JScrollPane( descriptionField ), GridBagConstraints.NORTHWEST );
		cp.gbc.gridwidth = 1;
		
		// row #3
		cp.attach( 0, ++row, new JLabel( "Author Name:") );
		cp.attach( 1,   row, authorNameField = new CxStringField( 40 ) );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Author Organization:") );
		cp.attach( 1,   row, authorOrganizationField = new CxStringField( 40 ) );
		return cp;
	}
	
	/**
	 * Creates the 'Model' tab
	 * @return the 'Model' tab
	 */
	private JComponent createModelTab() {
		final CxPanel cp = new CxPanel();
		cp.gbc.fill   = GridBagConstraints.NONE;
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		int row = -1;
		
		// row #1
		cp.attach( 0, ++row, new JLabel( "Drafting Standards:") );
		cp.attach( 1,   row, draftingStandardsBox = new CxDraftingStandardsBox() );
		cp.attach( 2,   row, new JLabel( "Scale:") );
		cp.attach( 3,   row, scaleField = new CxDecimalField() );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Units:") );
		cp.attach( 1,   row, unitsBox = new CxUnitsBox() );
		cp.attach( 2,   row, new JLabel( "Anti-Aliasing:") );
		cp.attach( 3,   row, antiAliasingBox = new JCheckBox() );
		
		// row #3
		cp.attach( 0, ++row, new JLabel( "Line Color:")  );
		cp.attach( 1,   row, colorBox = new ColorSelectionBox() );
		cp.attach( 2,   row, new JLabel( "Debug Mode:") );
		cp.attach( 3,   row, debugModeBox = new JCheckBox() );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Line Pattern:")  );
		cp.attach( 1,   row, patternBox = new LinePatternBox() );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Line Thickness:")  );
		cp.attach( 1,   row, thicknessBox = new LineThicknessBox(), GridBagConstraints.NORTHWEST );
		return cp;
	}
	
	/**
	 * Creates the 'Colors' tab
	 * @return the 'Colors' tab
	 */
	private JComponent createColorsTab() {
		final CxPanel cp = new CxPanel();
		cp.gbc.anchor = GridBagConstraints.NORTHWEST;
		cp.gbc.insets = new Insets( 5, 5, 5, 5 );
		cp.gbc.fill	= GridBagConstraints.NONE;
		int row = -1;
		
		// row #1
		cp.attach( 0, ++row, new JLabel( "Background Color:") );
		cp.attach( 1,   row, bkgColorButton = new CxCustomColorButton( BACKGROUND_COLOR ) );
		
		// row #2
		cp.attach( 0, ++row, new JLabel( "Grid Color:") );
		cp.attach( 1,   row, gridColorButton = new CxCustomColorButton( GRID_COLOR ) );
		
		// row #3
		cp.attach( 0, ++row, new JLabel( "Highlighted GeometricElement Color:") );
		cp.attach( 1,   row, hiliteColorButton = new CxCustomColorButton( HIGHLIGHT_COLOR ) );
		
		// row #4
		cp.attach( 0, ++row, new JLabel( "Ghost GeometricElement Color:") );
		cp.attach( 1,   row, ghostGeomColorButton = new CxCustomColorButton( PHANTOM_ELEMENT_COLOR ) );
		
		// row #5
		cp.attach( 0, ++row, new JLabel( "Selected GeometricElement Color:") );
		cp.attach( 1,   row, selectColorButton = new CxCustomColorButton( SELECT_COLOR ) );
		
		// row #6
		cp.attach( 0, ++row, new JLabel( "Temporary GeometricElement Color:") );
		cp.attach( 1,   row, tempGeomColorButton = new CxCustomColorButton( TEMP_GEOM_COLOR ), GridBagConstraints.NORTHWEST );
		return cp;
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
			PreferencesDialog.this.setVisible( false );
		}
	}
	
	/** 
	 * Accept Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class AcceptAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			commit();
			
			// hide the dialog
			PreferencesDialog.this.setVisible( false );
		}
	}

}
