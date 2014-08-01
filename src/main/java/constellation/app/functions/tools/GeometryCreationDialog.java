package constellation.app.functions.tools;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.curve.Arc3PtsFunction;
import constellation.app.functions.curve.Circle3PtsFunction;
import constellation.app.functions.curve.CircleRadiusFunction;
import constellation.app.functions.curve.BezierSplineFunction;
import constellation.app.functions.edit.EraseFunction;
import static constellation.app.functions.layout.FilterManagementDialog.*;
import constellation.app.functions.layout.FoldFunction;
import constellation.app.functions.layout.MeasureFunction;
import constellation.app.functions.layout.MirrorFunction;
import constellation.app.functions.layout.TextNoteFunction;
import constellation.app.functions.line.LinePtToPtInfiniteFunction;
import constellation.app.functions.line.LineJoinFunction;
import constellation.app.functions.line.LinePtToPtFunction;
import constellation.app.functions.line.LineRelimitFunction;
import constellation.app.functions.point.PointCoodinatesFunction;
import constellation.app.functions.shape.RectangleFunction;
import constellation.app.functions.tools.CxCalculator.CalculatorAction;
import constellation.app.ui.CxMenuBar.Edit_RedoAction;
import constellation.app.ui.CxMenuBar.Edit_UndoAction;
import constellation.app.ui.CxMenuBar.FileSaveAction;
import constellation.functions.Function;
import constellation.functions.FunctionAction;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxIconPanel;
import constellation.ui.components.buttons.CxButton;

/**
 * Constellation Geometry Creation Dialog
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class GeometryCreationDialog extends CxDialog {
	// singleton instance
	private static GeometryCreationDialog instance = null;
	
	// internal fields
	private final CxContentManager contentManager = CxContentManager.getInstance();
	
	/** 
	 * Default constructor
	 */
	private GeometryCreationDialog( final ApplicationController controller ) {
		super( controller, "Creation" );
		
		// setup the dialog box
		super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		super.setContentPane( createContentPane() );
		super.pack();
		super.setLocation( controller.getLowerRightAnchorPoint( this ) );
		
		// attach the mouse listener
		this.addMouseListener( new MouseEventHandler() );
	}
	
	/** 
	 * Returns the single instance of the class
	 * @param controller the given {@link ApplicationController function controller}
	 * @return the {@link GeometryCreationDialog dialog} instance
	 */
	public static GeometryCreationDialog getInstance( final ApplicationController controller ) {
		// if the dialog instance has not already been instantiated
		if( instance == null ) {
			// instantiate an instance of the dialog
			instance = new GeometryCreationDialog( controller );
		}
		return instance;
	}

	/** 
	 * Constructs the content pane
	 * @return the content pane
	 */
	private JComponent createContentPane() {
		final CxIconPanel cp = new CxIconPanel( 3 );
		cp.gbc.insets = new Insets( 0, 0, 0, 0 );
		
		// Drawing Functions #1
		cp.attach( button( contentManager.getIcon( "images/creation/Point.png" ), PointCoodinatesFunction.class, "Point" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Line.png" ), LinePtToPtFunction.class, "Line point-to-point" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Line-Infinite.png" ), LinePtToPtInfiniteFunction.class, "Infinite line point-to-point" ) );
		
		// Drawing Functions #2
		cp.attach( button( contentManager.getIcon( "images/creation/Rectangle.png" ), RectangleFunction.class, "Rectangle" ) );	
		cp.attach( button( contentManager.getIcon( "images/creation/Circle-Radius.png" ), CircleRadiusFunction.class, "Circle by radius" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Circle-3pt.png" ), Circle3PtsFunction.class, "Circle through 3-points" ) );
		
		// Drawing Functions #3
		cp.attach( button( contentManager.getIcon( "images/creation/Arc-3pt.png" ), Arc3PtsFunction.class, "Arc through 3-points" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Spline-Points.png" ), BezierSplineFunction.class, "Spline thru points" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Spline.jpg" ), BezierSplineFunction.class, "Spline (free hand)" ) );
		
		// Modification Functions
		cp.attach( button( contentManager.getIcon( "images/creation/Extend.png" ), LineRelimitFunction.class, "Trim/Extend" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Fillet.png" ), LineJoinFunction.class, "Fillet" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Fold.png" ), FoldFunction.class, "Fold" ) );
		
		// Copy/Text
		cp.attach( button( contentManager.getIcon( "images/creation/Copy.png" ), MirrorFunction.class, "Copy" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Mirror.png" ), MirrorFunction.class, "Mirror" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/TextNote.png" ), TextNoteFunction.class, "Text note" ) );
		
		// Utility functions #1
		cp.attach( button( FILTER_ICON, new FilterManagementAction( controller ), "Filter Management" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Calc.png" ), new CalculatorAction(), "Calculator" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Measure.png" ), MeasureFunction.class, "Measure" ) );

		// Undo/Re-do/Erase
		cp.attach( button( contentManager.getIcon( "images/creation/Undo.png" ), new Edit_UndoAction( controller ), "Undo" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Redo.png" ), new Edit_RedoAction( controller ), "Re-do" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Erase.png" ), EraseFunction.class, "Erase" ) );
		
		// Save/Print/Snapshot
		cp.attach( button( contentManager.getIcon( "images/creation/File-Save.png" ), new FileSaveAction( controller ), "Save" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Print.png" ), new CxPrintingUtil.PrintAction( controller ), "Print" ) );
		cp.attach( button( contentManager.getIcon( "images/creation/Snapshot.png" ), SnapshotFunction.class, "Snapshot" ) );
		return cp;
	}
	
	/**
	 * Creates a new button
	 * @param icon the given image {@link Icon icon}
	 * @param functionClass the given function class
	 * @param toolTip the given tool tip to describe the button  
	 * @return the {@link CxButton button}
	 */
	private JButton button( final Icon icon, 
							final Class<? extends Function> functionClass, 
							final String toolTip ) {
		// create the button
		final JButton button = new JButton( icon );
		button.addActionListener( new FunctionAction( controller, functionClass ) );
		button.setToolTipText( toolTip );
		button.setBorderPainted( false );
		button.setPreferredSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );

		// return the button
		return button;
	}
	
	/**
	 * Creates a new button
	 * @param icon the given image {@link Icon icon}
	 * @param functionClass the given function class
	 * @param toolTip the given tool tip to describe the button  
	 * @return the {@link CxButton button}
	 */
	private CxButton button( final Icon icon, 
							 final ActionListener listener, 
							 final String toolTip ) {
		// create the button
		final CxButton button = new CxButton( icon );
		button.addActionListener( listener );
		button.setToolTipText( toolTip );
		button.setBorderPainted( false );
		button.setPreferredSize( new Dimension( 42, 42 ) );
		
		// return the button
		return button;
	}
	
	/**
	 * Mouse Event Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class MouseEventHandler implements MouseListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked( final MouseEvent event ) {
			// do nothing
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered( final MouseEvent event ) {
			logger.info( "Entered" );
			GeometryCreationDialog.this.requestFocus();
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited( final MouseEvent event ) {
			logger.info( "Exited" );
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed( final MouseEvent event ) {
			// do nothing
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased( final MouseEvent event ) {
			// do nothing
		}
		
	}
	
}
