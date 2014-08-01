package constellation.app.functions;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityNamingService;
import constellation.drawing.elements.ModelElement;
import constellation.functions.FunctionDialogPlugIn;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.AcceptButton;
import constellation.ui.components.comboboxes.ColorSelectionBox;
import constellation.ui.components.comboboxes.LayerComboBox;
import constellation.ui.components.comboboxes.LinePatternBox;
import constellation.ui.components.comboboxes.LineThicknessBox;
import constellation.ui.components.fields.CxStringField;

/**
 * Constellation Function Input Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class InputDialog extends CxDialog {
	// singleton instance
	private static InputDialog instance = null;
	
	// internal fields
	protected CxStringField labelF;
	protected LayerComboBox layerBox;
	protected ColorSelectionBox colorBox;
	protected LinePatternBox patternBox;
	protected LineThicknessBox thicknessBox;
	
	/** 
	 * Creates a new generic creation dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 * @param title the title of the dialog
	 */
	private InputDialog( final ApplicationController controller ) {
		super( controller, "Input Dialog" );
		
		// initialize the fields
		labelF 			= new CxStringField( 10 );
		layerBox 		= new LayerComboBox();
		patternBox 		= new LinePatternBox();
		colorBox 		= new ColorSelectionBox();
		thicknessBox	= new LineThicknessBox();
		
		// setup the dialog
		super.setDefaultCloseOperation( HIDE_ON_CLOSE );
		super.setContentPane( createContentPane( null ) );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
	}
	
	/** 
	 * Creates a new generic creation dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 * @param title the title of the dialog
	 */
	protected InputDialog( final ApplicationController controller, final String title ) {
		super( controller, title );
		
		// initialize the fields
		labelF 			= new CxStringField( 10 );
		layerBox 		= new LayerComboBox();
		patternBox 		= new LinePatternBox();
		colorBox 		= new ColorSelectionBox();
		thicknessBox	= new LineThicknessBox();
		
		// setup the dialog
		super.setContentPane( createContentPane( null ) );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
	}
	
	/**
	 * Returns the singleton instance of the class
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link InputDialog singleton instance}
	 */
	public static InputDialog getInstance( final ApplicationController controller ) {
		if( instance == null ) {
			instance = new InputDialog( controller );
		}
		return instance;
	}
	
	/**
	 * Imports the settings of the given element
	 * @param element the given {@link ModelElement element}
	 */
	public void importSettings( final ModelElement element ) {
		// set the label
		labelF.setText( element.getLabel() );
		
		// import the attributes
		colorBox.setSelectedColor( element.getColor() );
		layerBox.setSelectedIndex( element.getLayer() );
		patternBox.setSelectedPattern( element.getPattern() );
	}

	/**
	 * Updates the appearance attributes of the element
	 * @param element the given {@link ModelElement element}
	 */
	public void exportSettings( final ModelElement element ) {
		// set the label (if it's not blank)
		if( !labelF.isBlank() ) {
			element.setLabel( labelF.getText() );
		}
		
		// set the attributes
		element.setColor( colorBox.getSelectedColor() );
		element.setLayer( layerBox.getSelectedIndex() );
		element.setPattern( patternBox.getSelectedPattern() );
	}
	
	/**
	 * Updates the appearance attributes of the element
	 * @param element the given {@link ModelElement element}
	 */
	public void exportAttributes( final ModelElement element ) {
		// set the attributes
		element.setColor( colorBox.getSelectedColor() );
		element.setLayer( layerBox.getSelectedIndex() );
		element.setPattern( patternBox.getSelectedPattern() );
	}
	
	/**
	 * Resets the dialog
	 * @param controller the given {@link ApplicationController function controller}
	 */
	public void reset( final ApplicationController controller) {
		// TODO remove this method after re-factoring of EntityEditorDialog
	}

	/**
	 * Resets the default entity name 
	 * @param type the given {@link EntityCategoryTypes category type}
	 */
	public void resetIdentity( final EntityCategoryTypes type ) {
		final GeometricModel model = controller.getModel();
		final EntityNamingService namingService = model.getNamingService();
		labelF.setText( namingService.getEntityName( type ) );
	}

	/**
	 * Installs the parameter plug-in
	 * @param plugIn the given {@link FunctionDialogPlugIn plug-in}
	 */
	public void setParameterPlugin( final FunctionDialogPlugIn plugIn ) {
		super.setContentPane( createContentPane( plugIn ) );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) ); 
	}

	/**
	 * Returns the content pane
	 * @param plugIn the given {@link FunctionDialogPlugIn dialog plug-in}
	 * @return the content pane
	 */
	protected JComponent createContentPane( final FunctionDialogPlugIn plugIn ) {
		final CxPanel cp = new CxPanel();
		cp.gbc.fill = GridBagConstraints.BOTH;
		cp.gbc.insets = new Insets( 2, 2, 2, 2 );
		
		JComponent comp;
		int row = 0;
		
		// attach the "Identification" section
		comp = createIdentificationPanel();
		if( comp != null ) {
			cp.attach( 0, row++, comp );
		}
		
		// attach the "Location" section
		comp = createLocationPanel();
		if( comp != null ) {
			cp.attach( 0, row++, comp );
		}
		
		// attach the "Parameter" section
		comp = createParameterPanel();
		if( comp != null ) {
			cp.attach( 0, row++, comp );
		}
		
		// attach the "Parameter" section #2
		if( plugIn != null ) {
			final JComponent paramComp = plugIn.getComponent();
			if( paramComp != null ) {
				paramComp.setBorder(  BorderFactory.createCompoundBorder( 
						BorderFactory.createTitledBorder( "Parameters" ),
						BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
				) );
				cp.attach( 0, row++, paramComp );
			}
		}
		
		// attach the "Appearance" section
		comp = createAppearancePanel();
		if( comp != null ) {
			cp.attach( 0, row++, comp  );
		}
		
		// attach the "Accept" button
		cp.gbc.fill = GridBagConstraints.NONE;
		final ActionListener listener = ( plugIn != null ) ? plugIn.getListener() : null;
		cp.attach( 0, row++, new AcceptButton( listener ), GridBagConstraints.NORTHEAST  );
		return cp;
	}
	
	/**
	 * Builds the "Identification" section of the dialog 
	 * @return the "Identification" section of the dialog 
	 */
	protected JComponent createIdentificationPanel() {
		final CxPanel cpA = new CxPanel();
		cpA.gbc.insets = new Insets( 2, 2, 2, 2 );
		cpA.gbc.anchor = GridBagConstraints.WEST;
		cpA.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( "Identification" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
		) );
		cpA.attach( 0, 0, new JLabel( "Label:" ) );
		cpA.attach( 1, 0, labelF );
		return cpA;
	}
	
	/**
	 * Builds the "Location" section of the dialog 
	 * @return the "Location" section of the dialog 
	 */
	protected JComponent createLocationPanel() {
		return null;
	}
	
	/**
	 * Builds the "Parameter" section of the dialog 
	 * @return the "Parameter" section of the dialog 
	 */
	protected JComponent createParameterPanel() {
		return null;
	}

	/**
	 * Builds the "Appearance" section of the dialog 
	 * @return the "Appearance" section of the dialog 
	 */
	protected JComponent createAppearancePanel() {
		final CxPanel cpB = new CxPanel();
		cpB.gbc.insets = new Insets( 2, 2, 2, 2 );
		cpB.gbc.anchor = GridBagConstraints.WEST;
		cpB.setBorder(  BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder( "Appearance" ),
				BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) 
		) );
		
		int row = -1;
		cpB.attach( 0, ++row, new JLabel( "Color:")  );
		cpB.attach( 1,   row, colorBox );
		cpB.attach( 0, ++row, new JLabel( "Pattern:")  );
		cpB.attach( 1,   row, patternBox );
		cpB.attach( 0, ++row, new JLabel( "Layer:")  );
		cpB.attach( 1,   row, layerBox );
		cpB.attach( 0, ++row, new JLabel( "Thickness:")  );
		cpB.attach( 1,   row, thicknessBox );
		return cpB;
	}
	
}
