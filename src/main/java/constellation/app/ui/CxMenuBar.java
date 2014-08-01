package constellation.app.ui;

import static constellation.app.functions.layout.FilterManagementDialog.FILTER_ICON;
import static constellation.app.math.CxZoomUtil.ZOOM_1TO1_ICON;
import static constellation.app.math.CxZoomUtil.ZOOM_1TO2_ICON;
import static constellation.app.math.CxZoomUtil.ZOOM_2TO1_ICON;
import static constellation.app.math.CxZoomUtil.ZOOM_AUTOFIT_ICON;
import static constellation.app.math.CxZoomUtil.ZOOM_OUT_ICON;
import static java.awt.event.InputEvent.ALT_MASK;
import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static java.lang.String.format;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.CxFontManager;
import constellation.app.Constellation;
import constellation.app.CxVersion;
import constellation.app.functions.curve.Arc3PtsFunction;
import constellation.app.functions.curve.ArcRadiusFunction;
import constellation.app.functions.curve.BezierSplineFunction;
import constellation.app.functions.curve.Circle3PtsFunction;
import constellation.app.functions.curve.CircleRadiusFunction;
import constellation.app.functions.curve.CircleTangentFunction;
import constellation.app.functions.curve.CurveParallelFunction;
import constellation.app.functions.curve.Ellipse2PtsFunction;
import constellation.app.functions.curve.Ellipse3PtsFunction;
import constellation.app.functions.curve.SpiralFunction;
import constellation.app.functions.edit.CopyFunction;
import constellation.app.functions.edit.EntityEditorFunction;
import constellation.app.functions.edit.EraseFunction;
import constellation.app.functions.edit.RestoreFunction;
import constellation.app.functions.layout.CommentFunction;
import constellation.app.functions.layout.DimensionFunction;
import constellation.app.functions.layout.FoldFunction;
import constellation.app.functions.layout.InspectFunction;
import constellation.app.functions.layout.MeasureFunction;
import constellation.app.functions.layout.MirrorFunction;
import constellation.app.functions.layout.PictureManagementFunction;
import constellation.app.functions.layout.RotateFunction;
import constellation.app.functions.layout.ScaleFunction;
import constellation.app.functions.layout.TextNoteFunction;
import constellation.app.functions.layout.TranslateFunction;
import constellation.app.functions.layout.FilterManagementDialog.FilterManagementAction;
import constellation.app.functions.line.LineAngleFunction;
import constellation.app.functions.line.LineHorizontalFunction;
import constellation.app.functions.line.LineJoinFunction;
import constellation.app.functions.line.LineNormalFunction;
import constellation.app.functions.line.LineParallelFunction;
import constellation.app.functions.line.LinePtToPtFunction;
import constellation.app.functions.line.LineRelimitFunction;
import constellation.app.functions.line.LineSplitFunction;
import constellation.app.functions.line.LineTangentFunction;
import constellation.app.functions.line.LineVerticalFunction;
import constellation.app.functions.point.MidPointFunction;
import constellation.app.functions.point.PointCoodinatesFunction;
import constellation.app.functions.point.PointIntersectionFunction;
import constellation.app.functions.point.PointLimitsFunction;
import constellation.app.functions.point.PointOffsetFunction;
import constellation.app.functions.point.PointProjectFunction;
import constellation.app.functions.shape.PieFunction;
import constellation.app.functions.shape.RectangleFunction;
import constellation.app.functions.shape.SketchFunction;
import constellation.app.functions.tools.CxCalculator;
import constellation.app.functions.tools.GeometryCreationDialog;
import constellation.app.functions.tools.PreferencesDialog;
import constellation.app.functions.tools.SnapshotFunction;
import constellation.app.functions.tools.SystemInformationDialog;
import constellation.app.functions.tools.CxPrintingUtil.PrintAction;
import constellation.app.functions.view.ZoomInFunction;
import constellation.app.math.CxZoomUtil;
import constellation.app.math.CxZoomUtil.AutoFitAction;
import constellation.app.math.CxZoomUtil.Zoom1to1Action;
import constellation.app.math.CxZoomUtil.Zoom1to2Action;
import constellation.app.math.CxZoomUtil.Zoom2to1Action;
import constellation.app.math.CxZoomUtil.ZoomOutAction;
import constellation.functions.Function;
import constellation.functions.FunctionAction;
import constellation.functions.FunctionManager;
import constellation.model.DefaultGeometricModel;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatManager;
import constellation.model.formats.ModelFormatReader;
import constellation.model.formats.ModelFormatWriter;
import constellation.preferences.SystemPreferences;
import constellation.ui.components.choosers.CxFileChooser;
import constellation.ui.components.choosers.CxImageFileChooser;
import constellation.ui.components.choosers.CxImageFileChooser.ImageFileFilter;
import constellation.ui.components.menu.CxCheckedMenuItem;
import constellation.ui.components.menu.CxMenu;
import constellation.ui.components.menu.CxMenuItem;
import constellation.ui.components.menu.MenuModes;
import constellation.util.OSPlatformUtil;

/**
 * Constellation Application Menu Bar
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxMenuBar extends JMenuBar {
	// get the meta key for the host Operating System
	private static final int META_KEY = OSPlatformUtil.getMetaKey();

	// static fields
	private static final ModelFormatManager formatManager = ModelFormatManager.getInstance();
	
	// log4j instance
	private static final Logger logger = Logger.getLogger( CxMenuBar.class );
	
	// menu icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon BLANK_ICON 		= null; 
	
	// internal fields
	private final ApplicationController controller;
	private final SystemPreferences preferences;
	private final JFrame frame;
	
	/**
	 * Creates a new application menu bar
	 * @param controller the given {@link ApplicationController controller}
	 * @param frame the given {@link JFrame frame}
	 */
	public CxMenuBar( final ApplicationController controller, final JFrame frame ) {
		this.controller		= controller;
		this.preferences 	= controller.getSystemPreferences();
		this.frame 			= frame;
		
		// add the common-application menus
		add( new FileMenu() );
		add( new EditMenu() );
		add( new ViewMenu() );
		
		// add the geometry specific menus
		add( new PointMenu() );
		add( new LineMenu() );
		add( new CurveMenu() );
		add( new ShapeMenu() );
		add( new LayoutMenu() );
		
		// tool/option menus
		add( new ToolsMenu() );
		
		// setup the help menu
		add( new HelpMenu() );
	}
	
	/** 
	 * Opens the CAD model file
	 * @param modelFile the given {@link File model file}
	 */
	private static GeometricModel loadModel( final ApplicationController controller, final File modelFile ) {
		GeometricModel model = null;
    	try {
    		// lookup the appropriate format reader
    		final ModelFormatReader reader = formatManager.getFormatReader( modelFile );
    		
			// was a reader retrieved?
			if( reader != null ) {
				model = reader.readFile( modelFile );
			}
			
			// unsupported format
			else {
				controller.showErrorDialog( 
					"Could not find a suitable import mechanism for the selected file", 
					"File Open Error",
					null
				);
			}
    	}
    	catch( final Exception e ) {
    		controller.showErrorDialog( e.getMessage(), "File Open Error", e );
    		logger.error( "An error occurred", e );
    	}
    	
		return model;
	}
	
	////////////////////////////////////////////////////////////////////
	//		Dynamic Menu Item Classes
	////////////////////////////////////////////////////////////////////
	
	/**
	 * This is a function-based menu item
	 * @author lawrence.daniels@gmail.com
	 */
	private class FxMenuItem extends CxMenuItem {
		
		/**
		 * Creates a new function-based menu item
		 * @param name the name/label of the menu item
		 * @param keyStroke the activation {@link KeyStroke key stroke}
		 * @param listener the {@link ActionListener action listener}
		 * @param menuMode the {@link MenuModes compatible menu contexts}
		 */
		public FxMenuItem( final String name, 
						   	 final KeyStroke keyStroke,
						   	 final Class<? extends Function> functionClass ) {
			super( name );
			
			// setup the appropriate font
			CxFontManager.setDefaultFont( this );
			
			// lookup the function
			final Function function = FunctionManager.getFunctionByClass( functionClass );
			
			// setup the menu item
			super.addActionListener( new FunctionAction( controller, function ) );
			super.setAccelerator( keyStroke );
			super.setIcon( function.getIcon() );
		}
	}
	
	////////////////////////////////////////////////////////////////////
	//		Dynamic Menu Classes
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates the 'Edit' menu
	 * @return the 'Edit' {@link JMenu menu}
	 */
	private class EditMenu extends CxMenu {
		private final Icon CUT_ICON 		= cxm.getIcon( "images/commands/edit/cut.png" );
		private final Icon PASTE_ICON 		= cxm.getIcon( "images/commands/edit/paste.png" );
		private final Icon REDO_ICON 		= cxm.getIcon( "images/commands/edit/redo.png" );
		private final Icon UNDO_ICON 		= cxm.getIcon( "images/commands/edit/undo.png" );
		
		/**
		 * Default Constructor
		 */
		public EditMenu() {
			super( "Edit" );
			add( new CxMenuItem( "Undo", UNDO_ICON, getKeyStroke( VK_Z, META_KEY ), new Edit_UndoAction( controller ) ) );
			add( new CxMenuItem( "Redo", REDO_ICON, getKeyStroke( VK_Y, META_KEY ), new Edit_RedoAction( controller ) ) );
			add( new JSeparator() );
			add( new CxMenuItem( "Cut", CUT_ICON, getKeyStroke( VK_X, META_KEY ), new Edit_CutAction() ) );
			add( new FxMenuItem( "Copy", getKeyStroke( VK_C, META_KEY ), CopyFunction.class ) );
			add( new CxMenuItem( "Paste", PASTE_ICON, getKeyStroke( VK_V, META_KEY ), new Edit_PasteAction() ) );
			add( new FxMenuItem( "Modify", getKeyStroke( VK_E, META_KEY ), EntityEditorFunction.class ) );
			add( new JSeparator() );
			add( new FxMenuItem( "Erase", getKeyStroke( VK_BACK_SPACE, 0 ), EraseFunction.class ) );
			add( new FxMenuItem( "Restore", getKeyStroke( VK_BACK_SPACE, SHIFT_MASK ), RestoreFunction.class ) );	
		}
		
		/**
		 * Edit::Cut Action
		 * @author lawrence.daniels@gmail.com
		 */
		public class Edit_CutAction implements ActionListener {
			
			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// TODO Auto-generated method stub
				controller.showErrorDialog( "Function not yet implemented", "Cut" );
			}
		}
		
		/**
		 * Edit::Paste Action
		 * @author lawrence.daniels@gmail.com
		 */
		public class Edit_PasteAction implements ActionListener {
			
			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// TODO Auto-generated method stub
				controller.showErrorDialog( "Function not yet implemented", "Paste" );
			}
		}
		
	}
	
	/**
	 * Edit::Re-do Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Edit_RedoAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Re-do Action
		 * @param controller the given {@link ApplicationController function controller}
		 */
		public Edit_RedoAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// TODO Auto-generated method stub
			controller.showErrorDialog( "Function not yet implemented", "Redo" );
		}
		
	}
	
	/**
	 * Edit::Undo Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Edit_UndoAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Undo Action
		 * @param controller the given {@link ApplicationController function controller}
		 */
		public Edit_UndoAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// TODO Auto-generated method stub
			controller.showErrorDialog( "Function not yet implemented", "Undo" );
		}
	}
	
	/**
	 * Creates the 'File' menu
	 * @return the 'File' {@link JMenu menu}
	 */
	private class FileMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public FileMenu() {
			super( "File" );
			add( new CxMenuItem( "New", cxm.getIcon( "images/commands/file/new.png" ), getKeyStroke( VK_N, META_KEY ), new FileNewAction( controller ) ) );
			add( new CxMenuItem( "Open", cxm.getIcon( "images/commands/file/open.png" ), getKeyStroke( VK_O, META_KEY ), new FileOpenAction( controller ) ) );
			add( new CxMenuItem( "Reload", cxm.getIcon( "images/commands/file/reload.png" ), null, new FileReloadAction( controller ) ) );
			add( new CxMenuItem( "Merge", cxm.getIcon( "images/commands/file/merge.png" ), new FileMergeAction() ) );
			add( new JSeparator() );
			add( new CxMenuItem( "Save", cxm.getIcon( "images/commands/file/save.png" ), getKeyStroke( VK_S, META_KEY ), new FileSaveAction( controller ) ) );
			add( new CxMenuItem( "Save As ...", cxm.getIcon( "images/commands/file/saveAs.png" ), getKeyStroke( VK_S, SHIFT_MASK | META_KEY ), new FileSaveAsAction() ) );
			add( new JSeparator() );
			add( new CxMenuItem( "Print", cxm.getIcon( "images/commands/file/print.png" ), null, new PrintAction( controller ) ) );
			add( new JSeparator() );
			add( new CxMenuItem( "Close", BLANK_ICON, getKeyStroke( VK_W, META_KEY ), new CloseModelAction() ) );
		}
		
		/**
		 * Close Model Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class CloseModelAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// don't allow if the model is distributed
				if( controller.getModel().isVirtual() ) {
					controller.showErrorDialog(
							"Not allowed when operating in Collaborative Mode", 
							"Collaborative Model Error" 
					);
					return;
				}
				
				// close the application
				frame.dispose();
			}
		}
		
		/**
		 * File::Save As Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class FileSaveAsAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// get the model
				final GeometricModel model = controller.getModel();
				
				// get the parent directory
				final File parentDirectory = model.getModelFile().getParentFile();
				
				// create a file chooser instance
				final JFileChooser chooser = CxFileChooser.createSaveAsFileChooser( parentDirectory );
				
				// open the file dialog
				final int returnVal = chooser.showSaveDialog( (Component)event.getSource() );
			    if( returnVal == JFileChooser.APPROVE_OPTION ) {
			       saveModelAs( model, chooser.getSelectedFile() );
			    }
			}
			
			/**
			 * Saves the given model file
			 * @param model the given {@link GeometricModel model}
			 * @param file the given model {@link File file}
			 */
			private void saveModelAs( final GeometricModel model, final File file ) {
				try {			
					// set the model file
					model.setModelFile( file );
					
					// save the model
					final ModelFormatWriter writer = formatManager.getDefaultWriter();
					writer.writeFile( model );
					
					// alert the user
					controller.setStatusMessage( "Model saved." );
				}
				catch( final Exception cause ) {
					controller.showErrorDialog( "Model File Save Error", cause );
				}
				finally {
					// update the model
					model.setModelFile( file );
					controller.setModel( model );	
				}
			}	
		}
		
		/**
		 * File Merge Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class FileMergeAction implements ActionListener {
			
			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// get the parent directory
				final File parentDirectory = controller.getModel().getModelFile().getParentFile();
				
				// create a file chooser instance
				final JFileChooser chooser = CxFileChooser.createOpenFileChooser( parentDirectory );
				
				// open the file dialog
				final int returnVal = chooser.showOpenDialog( (Component)event.getSource() );
			    if( returnVal == JFileChooser.APPROVE_OPTION ) {
			    	final File chosenFile = chooser.getSelectedFile();
			    	final GeometricModel model = loadModel( controller, chosenFile );
			    	if( model != null ) {
			    		controller.mergeModel( model );
			    	}
			    }
			}
		}
	}
	
	/**
	 * File Open Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class FileOpenAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new File Save Action
		 * @param controller the given {@link ApplicationController function controller}
		 */
		public FileOpenAction( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// don't allow if the model is distributed
			if( controller.getModel().isVirtual() ) {
				controller.showErrorDialog(  
						"Not allowed when operating in Collaborative Mode", 
						"Collaborative Model Error" 
				);
				return;
			}
			
			// get the parent directory
			final File parentDirectory = controller.getModel().getModelFile().getParentFile();
			
			// create a file chooser instance
			final JFileChooser chooser = CxFileChooser.createOpenFileChooser( parentDirectory );
			
			// open the file dialog
			final int returnVal = chooser.showOpenDialog( (Component)event.getSource() );
		    if( returnVal == JFileChooser.APPROVE_OPTION ) {
		    	final File chosenFile = chooser.getSelectedFile();
		    	final GeometricModel model = loadModel( controller, chosenFile );
		    	if( model != null ) {
		    		// set the new model
		    		controller.setModel( model );
		    		
		    		// auto-fit the elements on to the screen
		    		CxZoomUtil.autoFit( controller );
		    	}
		    }
		}
	}
	
	/**
	 * File Reload Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class FileReloadAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new File Save Action
		 * @param controller the given {@link ApplicationController function controller}
		 */
		public FileReloadAction( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// don't allow if the model is distributed
			if( controller.getModel().isVirtual() ) {
				controller.showErrorDialog(  
						"Not allowed when operating in Collaborative Mode", 
						"Collaborative Model Error" 
				);
				return;
			}
			
			// get the currently loaded model
			final GeometricModel loadedModel = controller.getModel();
			
			// reload the model
	    	final GeometricModel reloadedModel = loadModel( controller, loadedModel.getModelFile() );
	    	if( reloadedModel != null ) {
	    		// set the new model
	    		controller.setModel( reloadedModel );
	    		
	    		// auto-fit the elements on to the screen
	    		CxZoomUtil.autoFit( controller );
	    	}
		}
	}
	
	/**
	 * File::New Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class FileNewAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new "File::New" Action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public FileNewAction( final ApplicationController controller ) {
			this.controller = controller;
		} 
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent e ) {
			// if the model has changed, warn the user
			final int outcome = JOptionPane.showConfirmDialog(
					controller.getFrame(), 
					"Discard current changes?", "Possible Data Loss", 
					JOptionPane.WARNING_MESSAGE 
			);
			
			// if okay to proceed...
			if( outcome == JOptionPane.OK_OPTION ) {
				// create a new model instance
				final GeometricModel newModel = DefaultGeometricModel.newModel();
				
				// set the model
				controller.setModel( newModel );
			}
		}
	}
	
	/**
	 * File::Save Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class FileSaveAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new "File::Save" Action
		 * @param controller the given {@link ApplicationController function controller}
		 */
		public FileSaveAction( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			saveModel( controller.getModel() );
		}
		
		/**
		 * Saves the current model
		 * @param model the given {@link GeometricModel model}
		 */
		private void saveModel( final GeometricModel model ) {
			try {
				// save the model
				final ModelFormatWriter writer = formatManager.getDefaultWriter();
				writer.writeFile( model );
				
				// alert the user
				controller.setStatusMessage( "Model saved." );
			}
			catch( final Exception cause ) {
				controller.showErrorDialog( "Model File Save Error", cause );
			}
		}
	}
	
	/**
	 * Creates the 'Help' menu
	 * @return the 'Help' {@link JMenu menu}
	 */
	private class HelpMenu extends CxMenu {
		private final Icon ABOUT_ICON 			= cxm.getIcon( "images/commands/help/about.gif" );
		private final Icon HELP_CONTENTS_ICON 	= cxm.getIcon( "images/commands/help/bookOpen.gif" );
		private final Icon HELP_ICON 			= cxm.getIcon( "images/commands/help/help.png" );
		private final Icon LOGO_ICON 			= cxm.getIcon( "images/logo/Logo.png" );
		private final Icon SYSTEM_INFO_ICON 	= cxm.getIcon( "images/commands/help/systemInfo.png" );
		
		/**
		 * Default Constructor
		 */
		public HelpMenu() {
			super( "Help" );
			super.add( new CxMenuItem( "About", ABOUT_ICON, null, new AboutAction() ) );
			super.add( new CxMenuItem( "Help", HELP_CONTENTS_ICON, null, new HelpContentsAction() ) );
			super.add( new CxMenuItem( "Help On Function", HELP_ICON, null, new HelpOnFunctionAction() ) );
			super.add( new CxMenuItem( "System Information", SYSTEM_INFO_ICON, null, new SystemInformationAction() ) );
		}
		
		/**
		 * About Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class AboutAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// get the version information
				final CxVersion version = Constellation.class.getAnnotation( CxVersion.class );
				
				// show the message dialog
				JOptionPane.showMessageDialog( 
						CxMenuBar.this, 
						format( "Constellation v%s\n\n" +
								"Copyright (c) 2007 Dizzie Bee\n" +
								"Author: Lawrence Daniels\n" +
								"lawrence.daniels@gmail.com", version.value() ), 
						"About Constellation", 
						JOptionPane.INFORMATION_MESSAGE, 
						LOGO_ICON 
				);
			}
		}
		
		/**
		 * Help Contents Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HelpContentsAction implements ActionListener {

			/**
			 * {@inheritDoc}
			 */
			public void actionPerformed( final ActionEvent event ) {
				final HelpDialog dialog = HelpDialog.getInstance( controller );
				dialog.makeVisible();
			}	
		}
		
		/**
		 * Help On Function Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HelpOnFunctionAction implements ActionListener {

			/**
			 * {@inheritDoc}
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
		
		/**
		 * System Information Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class SystemInformationAction implements ActionListener {

			/**
			 * {@inheritDoc}
			 */
			public void actionPerformed( final ActionEvent event ) {
				// get an instance of the dialog
				final SystemInformationDialog systemMonitorDialog = SystemInformationDialog.getInstance( controller );
				
				// move it to the appropriate location
				systemMonitorDialog.setLocation( controller.getLowerRightAnchorPoint( systemMonitorDialog ) );
				
				// make it visible
				systemMonitorDialog.makeVisible();
			}	
		}
	}
	
	/**
	 * Creates the 'Line' menu
	 * @return the 'Line' {@link JMenu menu}
	 */
	private class LineMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public LineMenu() {
			super( "Lines" );
			add( new FxMenuItem( "Point to Point", getKeyStroke( VK_L, META_KEY ), LinePtToPtFunction.class ) );
			add( new FxMenuItem( "Angled", null, LineAngleFunction.class ) );
			add( new FxMenuItem( "Horizontal", getKeyStroke( VK_H, SHIFT_MASK | META_KEY ), LineHorizontalFunction.class ) );
			add( new FxMenuItem( "Normal", null, LineNormalFunction.class ) );
			add( new FxMenuItem( "Parallel", null, LineParallelFunction.class ) );
			add( new FxMenuItem( "Tangent", null, LineTangentFunction.class ) );
			add( new FxMenuItem( "Vertical", getKeyStroke( VK_V, SHIFT_MASK | META_KEY ), LineVerticalFunction.class ) );
			add( new JSeparator() );
			
			add( new FxMenuItem( "Join/Fillet", getKeyStroke( VK_J, META_KEY ), LineJoinFunction.class ) );
			add( new FxMenuItem( "Trim/Extend", null, LineRelimitFunction.class ) );
			add( new FxMenuItem( "Split", getKeyStroke( VK_L, ALT_MASK | META_KEY ), LineSplitFunction.class ) );
		}
	}
	
	/**
	 * Creates the 'Curve' menu
	 * @return the 'Curve' {@link JMenu menu}
	 */
	private class CurveMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public CurveMenu() {
			super( "Curves" );
			add( new FxMenuItem( "Arc: Radius", getKeyStroke( VK_A, SHIFT_MASK | ALT_MASK | META_KEY ), ArcRadiusFunction.class ) );
			add( new FxMenuItem( "Arc: 3-Points", getKeyStroke( VK_A, ALT_MASK | META_KEY ), Arc3PtsFunction.class ) );
			add( new JSeparator() );
			
			add( new FxMenuItem( "Circle: Radius", getKeyStroke( VK_C, ALT_MASK | META_KEY ), CircleRadiusFunction.class ) );
			add( new FxMenuItem( "Circle: 3-Points", getKeyStroke( VK_C, SHIFT_MASK | ALT_MASK | META_KEY ), Circle3PtsFunction.class ) );
			add( new FxMenuItem( "Circle: Tangent", null, CircleTangentFunction.class ) );
			add( new JSeparator() );
			
			add( new FxMenuItem( "Ellipse: 2-Points", null, Ellipse2PtsFunction.class ) );
			add( new FxMenuItem( "Ellipse: 3-Points", null, Ellipse3PtsFunction.class ) );
			add( new JSeparator() );
			
			add( new FxMenuItem( "Spline: Bezier", getKeyStroke( VK_B, ALT_MASK ), BezierSplineFunction.class ) );
			add( new FxMenuItem( "Curve: Parallel", null, CurveParallelFunction.class ) );
		}
	}
	
	/**
	 * Creates the 'Layout' menu
	 * @return the 'Layout' {@link JMenu menu}
	 */
	private class LayoutMenu extends CxMenu {
		private final Icon BKGD_IMAGE_ICON 	= cxm.getIcon( "images/commands/layout/bkgImage.png" );
		private final Icon CANVAS_ICON 		= cxm.getIcon( "images/commands/layout/canvas.png" );
		
		/**
		 * Default Constructor
		 */
		public LayoutMenu() {
			super( "Layout" );

			// add function menu items
			add( new CxMenuItem( "Filter Management", FILTER_ICON, new FilterManagementAction( controller ) ) );
			add( new CxMenuItem( "Color Management", CANVAS_ICON, null, new ColorManagementAction( controller ) ) );
			add( new FxMenuItem( "Picture Management", null, PictureManagementFunction.class ) );
			add( new CxMenuItem( "Set Background", BKGD_IMAGE_ICON, new BackgroundImageAction(), MenuModes.DRAFTING ) );
			add( new JSeparator() );			

			add( new FxMenuItem( "Inspect", getKeyStroke( VK_I, SHIFT_MASK | META_KEY ), InspectFunction.class ) );
			add( new FxMenuItem( "Dimension", getKeyStroke( VK_D, META_KEY ), DimensionFunction.class ) );
			add( new FxMenuItem( "Measure", getKeyStroke( VK_M, ALT_MASK ), MeasureFunction.class ) );
			add( new JSeparator() );	
			
			add( new FxMenuItem( "Comment", null, CommentFunction.class ) );
			add( new FxMenuItem( "Text Note", getKeyStroke( VK_T, META_KEY ), TextNoteFunction.class ) );
			add( new JSeparator() );
			
			add( new FxMenuItem( "Fold", null, FoldFunction.class ) );
			add( new FxMenuItem( "Mirror", null, MirrorFunction.class ) );
			add( new FxMenuItem( "Rotate", null, RotateFunction.class ) );
			add( new FxMenuItem( "Scale", null, ScaleFunction.class ) );
			add( new FxMenuItem( "Translate", null, TranslateFunction.class ) );
		}
		
		/**
		 * Background Image Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class BackgroundImageAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// get the image
				final CxImageFileChooser chooser = CxImageFileChooser.getInstance();
				final int returnVal = chooser.showOpenDialog( frame );
				if( returnVal == APPROVE_OPTION ) {
					try {
						// get the image file
						final File imageFile = chooser.getSelectedFile();
						
						// read the image
						final ImageFileFilter fileFilter = (ImageFileFilter)chooser.getFileFilter();
						final BufferedImage image = fileFilter.readImage( imageFile );
						controller.setStatusMessage( format( "Loaded image '%s'", imageFile.getName() ) );
						
						// get the model instance
						final GeometricModel model = controller.getModel();
						model.setBackgroundImage( image );
					}
					catch( final Exception cause ) {
						controller.showErrorDialog( "Background Image", cause );
					}
				}
			}
		}
	}
		
	
	/**
	 * Color Management Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class ColorManagementAction implements ActionListener {
		private final ApplicationController controller;
		
		public ColorManagementAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			controller.showErrorDialog( "Not yet implemented", "Color Management" );
		}
	}
	
	/**
	 * Creates the 'Shapes' menu
	 * @return the 'Shapes' {@link JMenu menu}
	 */
	private class ShapeMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public ShapeMenu() {
			super( "Shapes" );
			add( new FxMenuItem( "Pie", null, PieFunction.class ) );
			add( new FxMenuItem( "Rectangle", null, RectangleFunction.class ) );
			add( new FxMenuItem( "Sketch", null, SketchFunction.class ) );
			add( new FxMenuItem( "Spiral", null, SpiralFunction.class ) );
		}
		
	}
		
	/**
	 * Creates the 'Tools' menu
	 * @return the 'Tools' {@link JMenu menu}
	 */
	private class ToolsMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public ToolsMenu() {
			super( "Tools" );
			
			// attach all other menu items
			add( new CxMenuItem( "Preferences", cxm.getIcon( "images/commands/tools/preferences.png" ), getKeyStroke( VK_COMMA, META_KEY ), new PreferencesEditAction() ) );
			add( new CxMenuItem( "Calculator", cxm.getIcon( "images/commands/tools/calculator.png" ), null, new CxCalculator.CalculatorAction() ) );
			add( new CxMenuItem( "Take Snapshot", cxm.getIcon( "images/commands/tools/snapshot.png" ), null, new FunctionAction( controller, SnapshotFunction.class ) ) );
			add( new CxMenuItem( "Geometry Creation", cxm.getIcon( "images/commands/tools/gear.png" ), null, new GeometryCreationAction() ) );
			
			// get the system preferences instance
			final SystemPreferences preferences = controller.getSystemPreferences();
			
			// load the third party tool menu items
			final Collection<JMenuItem> menuItems = preferences.getThirdPartyMenuItems( controller );
			if( !menuItems.isEmpty() ) {
				// add a separator
				add( new JSeparator() );
				
				// add the sub-menus to the 'Tools' menu
				for( final JMenuItem menuItem : menuItems ) {
					// setup the appropriate font
					CxFontManager.setDefaultFont( menuItem );
					
					// add the sub-menu to the tool menu
					add( menuItem );
				}
			}
		}
		
		/**
		 * Tools::GeometricElement Creation Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class GeometryCreationAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// make the dialog visible
				GeometryCreationDialog.getInstance( controller ).setVisible( true );
			}
		}
		
		/** 
		 * Preferences::Edit Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class PreferencesEditAction implements ActionListener {

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
	}
	
	/**
	 * Creates the 'View' menu
	 * @return the 'View' {@link JMenu menu}
	 */
	private class ViewMenu extends CxMenu {
		private final JCheckBoxMenuItem commentsMenuItem;
		private final JCheckBoxMenuItem gridMenuItem;
		private final JCheckBoxMenuItem phantomsMenuItem;
		private final JCheckBoxMenuItem pointsMenuItem;
		
		// icon definitions
		private final Icon COMMENT_ICON 	= cxm.getIcon( "images/commands/layout/comment.gif" );
		private final Icon EYE_OPEN_ICON 	= cxm.getIcon( "images/commands/view/eye_open.png" );
		private final Icon EYE_SHUT_ICON 	= cxm.getIcon( "images/commands/view/eye_shut.png" );
		private final Icon GRID_ICON 		= cxm.getIcon( "images/commands/view/grid.png" );
		private final Icon POINT_LABEL_ICON = cxm.getIcon( "images/commands/view/pointLabel.png" );
		
		/**
		 * Default Constructor
		 */
		public ViewMenu() {
			super( "View" );		
			
			// add hide/show menu items
			add( commentsMenuItem = new CxCheckedMenuItem( "Show Comments", COMMENT_ICON, null, new HideShowCommentsAction() ) );
			add( gridMenuItem = new CxCheckedMenuItem( "Show Grid", GRID_ICON, getKeyStroke( VK_G, META_KEY ), new HideShowGridAction() ) );
			add( phantomsMenuItem = new CxCheckedMenuItem( "Show Phantoms", EYE_OPEN_ICON, getKeyStroke( VK_P, SHIFT_MASK | META_KEY ), new HideShowPhantomsAction() ) );
			add( pointsMenuItem = new CxCheckedMenuItem( "Show Point Labels", POINT_LABEL_ICON, null, new HideShowPointLabelsAction() ) );
			add( new JSeparator() );
			
			add( new CxMenuItem( "Auto-fit", ZOOM_AUTOFIT_ICON, getKeyStroke( VK_A, SHIFT_MASK | META_KEY ), new AutoFitAction( controller ) ) );
			add( new FxMenuItem( "Zoom In", getKeyStroke( VK_Z, SHIFT_MASK | META_KEY ), ZoomInFunction.class ) );		
			add( new CxMenuItem( "Zoom Out", ZOOM_OUT_ICON, getKeyStroke( VK_R, SHIFT_MASK | META_KEY ), new ZoomOutAction( controller ) ) );
			add( new CxMenuItem( "Zoom 1:1", ZOOM_1TO1_ICON, null, new Zoom1to1Action( controller ) ) );
			add( new CxMenuItem( "Zoom 1:2", ZOOM_1TO2_ICON, null, new Zoom1to2Action( controller ) ) );
			add( new CxMenuItem( "Zoom 2:1", ZOOM_2TO1_ICON, null, new Zoom2to1Action( controller ) ) );
			
			// update the check-boxes
			final SystemPreferences preferences = controller.getSystemPreferences();
			commentsMenuItem.setSelected( preferences.showComments() );
			gridMenuItem.setSelected( preferences.showGrids() );
			phantomsMenuItem.setSelected( preferences.showPhantoms() );
			phantomsMenuItem.setIcon( preferences.showPhantoms() ? EYE_OPEN_ICON : EYE_SHUT_ICON );
			pointsMenuItem.setSelected( preferences.showPointLabels() );
		}
		
		/** 
		 * Hide/Show Comments Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HideShowCommentsAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// toggle the comments visible
				final boolean commentsOn = !preferences.showComments();
				
				// toggle the point label display
				preferences.showComments( commentsOn );
				
				// update the menu item check-box
				commentsMenuItem.setSelected( commentsOn );
				
				// request a draw of the screen
				controller.requestRedraw();
			}
		}
		
		/** 
		 * hide/Show Phantoms Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HideShowPhantomsAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {
				// toggle the phantom element display
				final boolean phantomsOn = !preferences.showPhantoms();
				
				// toggle the phantom element display
				preferences.showPhantoms( phantomsOn );
				
				// update the menu item check-box
				phantomsMenuItem.setSelected( phantomsOn );
				phantomsMenuItem.setIcon( preferences.showPhantoms() ? EYE_OPEN_ICON : EYE_SHUT_ICON );
				
				// request a draw of the screen
				controller.requestRedraw();
			}
		}
		
		/** 
		 * Hide/Show Point Labels Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HideShowPointLabelsAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {			
				// toggle the point label visible
				final boolean pointsOn = !preferences.showPointLabels();
				
				// toggle the point label display
				preferences.showPointLabels( pointsOn );
				
				// update the menu item check-box
				pointsMenuItem.setSelected( pointsOn );
				
				// request a draw of the screen
				controller.requestRedraw();
			}
		}
		
		/** 
		 * Hide/Show Grid Action
		 * @author lawrence.daniels@gmail.com
		 */
		private class HideShowGridAction implements ActionListener {

			/* 
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed( final ActionEvent event ) {	
				// toggle the grid display
				final boolean gridOn = !preferences.showGrids();
				
				// perform the action on the model
				preferences.showGrids( gridOn );
				
				// update the menu item check-box
				gridMenuItem.setSelected( gridOn );
				
				// request a draw of the screen
				controller.requestRedraw();
			}
		}
		
	}
	
	/**
	 * Creates the 'Point' menu
	 * @return the 'Point' {@link JMenu menu}
	 */
	private class PointMenu extends CxMenu {
		
		/**
		 * Default Constructor
		 */
		public PointMenu() {
			super( "Points" );
			add( new FxMenuItem( "Coordinates", getKeyStroke( VK_P, META_KEY ), PointCoodinatesFunction.class ) );
			add( new FxMenuItem( "Intersection", getKeyStroke( VK_I, META_KEY ), PointIntersectionFunction.class ) );
			add( new FxMenuItem( "Limits", null, PointLimitsFunction.class ) );
			add( new FxMenuItem( "Mid-point", null, MidPointFunction.class ) );
			add( new FxMenuItem( "Offset", null, PointOffsetFunction.class ) );
			add( new FxMenuItem( "Project", null, PointProjectFunction.class ) );
		}
	}
		
}
