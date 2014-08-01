package constellation.app.ui.informationbar;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.text.JTextComponent;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.curve.Arc3PtsFunction;
import constellation.app.functions.curve.ArcRadiusFunction;
import constellation.app.functions.curve.BezierSplineFunction;
import constellation.app.functions.curve.Circle3PtsFunction;
import constellation.app.functions.curve.CircleRadiusFunction;
import constellation.app.functions.curve.Ellipse2PtsFunction;
import constellation.app.functions.edit.EraseFunction;
import constellation.app.functions.edit.RestoreFunction;
import constellation.app.functions.layout.FoldFunction;
import constellation.app.functions.layout.InspectFunction;
import constellation.app.functions.layout.MirrorFunction;
import constellation.app.functions.layout.RotateFunction;
import constellation.app.functions.layout.ScaleFunction;
import constellation.app.functions.layout.TranslateFunction;
import constellation.app.functions.line.LineJoinFunction;
import constellation.app.functions.line.LinePtToPtFunction;
import constellation.app.functions.point.PointCoodinatesFunction;
import constellation.app.functions.point.PointIntersectionFunction;
import constellation.app.functions.shape.SketchFunction;
import constellation.app.functions.tools.PreferencesDialog;
import constellation.app.functions.tools.CxPrintingUtil.PrintAction;
import constellation.app.ui.CxMenuBar.FileNewAction;
import constellation.app.ui.CxMenuBar.FileOpenAction;
import constellation.app.ui.CxMenuBar.FileReloadAction;
import constellation.app.ui.CxMenuBar.FileSaveAction;
import constellation.functions.Function;
import constellation.functions.FunctionAction;
import constellation.functions.FunctionManager;
import constellation.functions.Steps;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxIconToggleButton;

/**
 * Constellation Information Pane
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class InformationPane extends CxPanel {	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon SELECT_MODE_ICON = cxm.getIcon( "images/commands/mode/select.png" );
	private final Icon PAN_MODE_ICON 	= cxm.getIcon( "images/commands/mode/pan.gif" );
	
	// internal fields
	private final ApplicationController controller;
	private final FunctionSelectionPane funcPane;
	private final JTextComponent instructionPane;
	private final ZoomPane zoomPane;
	
	/**
	 * Creates the information pane
	 * @param controller the given {@link ApplicationController controller}
	 */
	public InformationPane( final ApplicationController controller ) {
		super.gbc.insets = new Insets( 1, 1, 1, 1 );

		// capture the controller instance
		this.controller = controller;
		
		// create the command tool bar
		final JPanel toolbarPane = new JPanel( new GridLayout( 1, 1 ) );
		toolbarPane.add( new CommandToolBar() );
		
		// attach the components
		// row #1
		int col = 0;
		super.gbc.anchor = GridBagConstraints.WEST;		
		super.gbc.fill = GridBagConstraints.BOTH;
		super.gbc.insets = new Insets( 0, 0, 0, 0 );
		super.gbc.gridwidth = 2;
		super.attach( 0, 0,   funcPane = new FunctionSelectionPane( controller ) );
		super.gbc.gridwidth = 3;
		super.attach( 2, 0, instructionPane	= createIstructionPane( controller, this ), GridBagConstraints.WEST );
		super.gbc.gridwidth = 1;
		
		// row #2: attach the components: file tool bar, layer tool bar & zoom tool bar
		col = 0;
		super.gbc.fill = GridBagConstraints.BOTH;
		super.attach( col++, 1, new FileToolBar() );
		super.attach( col++, 1, new LayerPane( controller ) );
		super.attach( col++, 1, zoomPane = new ZoomPane( controller )  );
		
		// attach the utility tool bar
		super.gbc.weightx = 0.01d;
		super.gbc.fill = GridBagConstraints.VERTICAL;
		super.attach( col++, 1, new UtilityToolBar() );
		
		// attach the tool bar
		super.gbc.anchor = GridBagConstraints.EAST;
		super.gbc.fill = GridBagConstraints.BOTH;
		super.gbc.weightx = 2d;
		super.attach( col++, 1, toolbarPane );
	}
	
	/**
	 * Sets the function information with the given text
	 * @param text the given function label text
	 */
	public void setFunctionInformation( final Function function ) {
		funcPane.setFunctionInformation( function );
	}

	/**
	 * Sets the message with the given text
	 * @param steps the given message text
	 */
	public void setInstruction( final Steps steps ) {
		instructionPane.setText( steps.toString() );
		instructionPane.requestFocus();
	}
	
	/**
	 * Sets the zoom/scale
	 * @param scale the given scale value
	 */
	public void setScale( final double scale ) {
		zoomPane.setZoom( scale );
	}
	
	/** 
	 * Creates the HTML-based instruction pane
	 * @param controller the given {@link ApplicationController controller}
	 * @param hostPanel the given {@link JComponent host panel}
	 * @return the {@link CxInstructionPane_HTML HTML-based instruction pane}
	 */
	private CxInstructionPane_HTML createIstructionPane( final ApplicationController controller, 
														 final JComponent hostPanel ) {
		final CxInstructionPane_HTML htmlPane = new CxInstructionPane_HTML( controller );
		htmlPane.setBackground( hostPanel.getBackground() );
		htmlPane.setPreferredSize( new Dimension( 400, 28 ) );
		htmlPane.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
			) );
		return htmlPane;
	}
	
	/**
	 * Creates a new button
	 * @param functionClass the given function class
	 * @param toolTip the given tool tip to describe the button  
	 * @param icon the given image {@link Icon icon}
	 * @return the {@link FxButton button}
	 */
	private JButton button( final Class<? extends Function> functionClass, 
							 final String toolTip ) {
		// lookup the function instance for the function class
		final Function function = FunctionManager.getFunctionByClass( functionClass );
		
		// create the function's icon
		final Icon icon = function.getIcon(); 
		
		// create the button
		final JButton button = new JButton( icon );
		button.addActionListener( new FunctionAction( controller, functionClass ) );
		button.setToolTipText( toolTip );
		button.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		button.setPreferredSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );
		button.setContentAreaFilled( false );
		button.setBorderPainted( false );
		button.setFocusPainted( false );
		
		// return the button
		return button;
	}
	
	/**
	 * Creates a new button
	 * @param icon the given image {@link Icon icon}
	 * @param functionClass the given function class
	 * @param toolTip the given tool tip to describe the button  
	 * @return the {@link JButton button}
	 */
	private JButton button( final String iconPath, 
						 	final ActionListener listener, 
						 	final String toolTip ) {
		// create the icon
		final Icon icon = cxm.getIcon( iconPath );
		
		// create the button
		final JButton button = new JButton( icon );
		button.addActionListener( listener );
		button.setToolTipText( toolTip );
		button.setBorderPainted( false );
		button.setFocusPainted( false );
		button.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		button.setPreferredSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );
		button.setContentAreaFilled( false );
		
		// return the button
		return button;
	}
	
	/**
	 * Constellation Command Tool Bar
	 * @author lawrence.daniels@gmail.com
	 */
	private class CommandToolBar extends JToolBar {
		
		/**
		 * Default Constructor
		 */
		public CommandToolBar() {
			super( HORIZONTAL );
			super.setFloatable( true );
			
			// create the tool bar panel
			final CxPanel cp = new CxPanel();
			cp.setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(),
					BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
				) );
			cp.gbc = new GridBagConstraints();
			cp.gbc.anchor = GridBagConstraints.WEST;
			cp.gbc.fill = GridBagConstraints.BOTH;
			cp.gbc.weightx = 2d; //0.1;
			
			// creation set 
			int col = 0;
			cp.attach( col++, 0, button( PointCoodinatesFunction.class, "Point" ) );
			cp.attach( col++, 0, button( PointIntersectionFunction.class, "Point intersection" ) );
			cp.attach( col++, 0, button( LinePtToPtFunction.class, "Line point-to-point" ) );
			cp.attach( col++, 0, button( LineJoinFunction.class, "Line join/fillet" ) );
			cp.attach( col++, 0, button( ArcRadiusFunction.class, "Arc through 2-points" ) );
			cp.attach( col++, 0, button( Arc3PtsFunction.class, "Arc through 3-points" ) );
			cp.attach( col++, 0, button( CircleRadiusFunction.class, "Circle through 2-points" ) );
			cp.attach( col++, 0, button( Circle3PtsFunction.class, "Circle through 3-points" ) );
			cp.attach( col++, 0, button( Ellipse2PtsFunction.class, "Ellipse through 2-points" ) );
			cp.attach( col++, 0, button( BezierSplineFunction.class, "Spline through points" ) );
			cp.attach( col++, 0, button( SketchFunction.class, "Draw freeform" ) );
			
			// modification set
			cp.attach( col++, 0, button( MirrorFunction.class, "Mirror" ) );
			cp.attach( col++, 0, button( FoldFunction.class, "Fold" ) );
			cp.attach( col++, 0, button( TranslateFunction.class, "Move/translate elements" ) );
			cp.attach( col++, 0, button( RotateFunction.class, "Flip/rotate elements" ) );
			cp.attach( col++, 0, button( ScaleFunction.class, "Scale elements" ) );
			
			// add the components to the command bar
			super.add( cp );
		}		
	}
	
	/**
	 * Command Set #2: Utility Functions
	 * @author lawrence.daniels@gmail.com
	 */
	private class UtilityToolBar extends CxPanel {
		
		/**
		 * Default Constructor
		 */
		public UtilityToolBar() {
			super.setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(),
					BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
				) );
			super.gbc.anchor = GridBagConstraints.WEST;
			super.gbc.fill = GridBagConstraints.BOTH;
			
			// attach components
			int col = 0;
			super.attach( col++, 0, new CxIconToggleButton( SELECT_MODE_ICON, PAN_MODE_ICON, new ModeSelectionAction(), "Select mode", "Pan mode" ) );
			super.attach( col++, 0, button( "images/commands/tools/preferences.png", new PreferencesAction(), "Edit preferences" ) );
			super.attach( col++, 0, button( InspectFunction.class, "Inspect element" ) );
			super.attach( col++, 0, button( EraseFunction.class, "Erase element(s)" ) );
			super.attach( col++, 0, button( RestoreFunction.class, "Restore element(s)" ) );
		}
	}
	
	/**
	 * Command Set #3: File Tool Bar
	 * @author lawrence.daniels@gmail.com
	 */
	private class FileToolBar extends CxPanel {
		
		/**
		 * Default Constructor
		 */
		public FileToolBar() {
			super.setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(),
					BorderFactory.createEmptyBorder( 1, 1, 1, 1 )
				) );
			super.gbc.anchor = GridBagConstraints.WEST;
			super.gbc.fill = GridBagConstraints.BOTH;
			
			// attach components
			int col = 0;
			super.attach( col++, 0, button( "images/commands/file/new.png", new FileNewAction( controller ), "New document" ) );
			super.attach( col++, 0, button( "images/commands/file/open.png", new FileOpenAction( controller ), "Open document" ) );
			super.attach( col++, 0, button( "images/commands/file/reload.png", new FileReloadAction( controller ), "Reload the current document" ) );
			super.attach( col++, 0, button( "images/commands/file/save.png", new FileSaveAction( controller ), "Save document" ) );
			super.attach( col++, 0, button( "images/commands/file/print.png", new PrintAction( controller ), "Print" ) );
		}
	}
		
	/**
	 * Hide/Show Phantoms Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class PreferencesAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			final PreferencesDialog preferencesDialog = PreferencesDialog.getInstance( controller );
			preferencesDialog.makeVisible();
			preferencesDialog.load();
		}
	}
	
	/**
	 * Mode Selection Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class ModeSelectionAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the toggle button
			final CxIconToggleButton button = (CxIconToggleButton)event.getSource();
			
			// update the icon displayed
			button.updateIcon();
			
			// just print a message
			controller.setStatusMessage( "Select element is not yet implemented" );
		}
	}

}
