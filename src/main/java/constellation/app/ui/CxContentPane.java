package constellation.app.ui;

import static constellation.app.functions.layout.PictureUtils.isImageFile;
import static constellation.app.math.ElementDetectionUtil.lookupElementsInclusionSet;
import static constellation.app.ui.statusbar.CxInputTypes.KEYBOARD;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_LEFT;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_MIDDLE;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_RIGHT;
import static constellation.app.ui.statusbar.CxInputTypes.MOUSE_WHEEL;
import static constellation.drawing.EntityTypes.PICTURE;
import static constellation.functions.MouseClick.*;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.MouseWheelEvent.WHEEL_BLOCK_SCROLL;
import static java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL;
import static java.lang.String.format;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxFontManager;
import constellation.PluginManager;
import constellation.app.CxApplicationController;
import constellation.app.functions.IndexedModelElement;
import constellation.app.functions.InputDialog;
import constellation.app.functions.PickListDialog;
import constellation.app.functions.line.LinePtToPtFunction;
import constellation.app.preferences.CxSystemPreferences;
import constellation.app.ui.informationbar.InformationPane;
import constellation.app.ui.statusbar.CxInputTypes;
import constellation.app.ui.statusbar.StatusBarPane;
import constellation.drawing.Camera;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PictureXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.UserImage;
import constellation.functions.Function;
import constellation.functions.FunctionManager;
import constellation.functions.MouseClick;
import constellation.functions.PickListObserver;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;
import constellation.model.formats.ModelFormatManager;
import constellation.model.formats.ModelFormatReader;
import constellation.ui.components.CxPanel;

/**
 * Constellation Content Pane
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxContentPane extends CxPanel {
	// window constants
	private static final int MIN_WIDTH	= 600;
	private static final int MIN_HEIGHT	= 400;
	
	// zoom constants
	private static final double MIN_ZOOM =  0.01d;
	private static final double MAX_ZOOM = 400.00d;
	
	// internal fields
	private final Logger logger = Logger.getLogger( getClass() );
	private final ApplicationController controller;
	private final InformationPane	informationBar;
	private final StatusBarPane statusBar;
	private final DrawingPane drawingPane;
	private final JFrame frame;
	
	// miscellaneous fields
	private PickListDialog pickListDialog;
	private Function activeFunction;
	private int selectionWidth;
	private int selectionHeight;
	private Point mouseMovePos;
	private Point oldMouseMovePos;
	
	/** 
	 * Creates a new content pane 
	 * @param controller the given {@link ApplicationController controller}
	 * @param frame the given {@link JFrame frame}
	 * @param informationBar the given {@link InformationPane information bar}
	 * @param drawingPane the given {@link DrawingPane drawing pane}
	 * @param statusBar the given {@link StatusBarPane status bar}
	 */
	public CxContentPane( final CxApplicationController controller, final JFrame frame ) {	
		super.gbc.anchor = GridBagConstraints.NORTHWEST;
		super.gbc.fill 	 = GridBagConstraints.HORIZONTAL;
		
		// internal stuff
		this.selectionWidth		= 12;
		this.selectionHeight	= 12;
		
		// initialize the components
		this.controller			= controller;
		this.frame				= frame;
		this.drawingPane 		= new DrawingPane();
		this.statusBar			= new StatusBarPane( controller );
		this.informationBar		= new InformationPane( controller );
		
		// put it all together
		// row 1
		int row = -1;
		super.attach( 0, ++row, informationBar );
		
		// row 3 
		super.gbc.fill = GridBagConstraints.BOTH;
		super.attach( 0, ++row, drawingPane, GridBagConstraints.SOUTHEAST );
		
		// row 4
		super.gbc.fill = GridBagConstraints.HORIZONTAL;
		super.gbc.anchor = GridBagConstraints.SOUTHEAST;
		super.attach( 0, ++row, statusBar );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Rendering Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes the component
	 * @param controller the given {@link ApplicationController controller}
	 */
	public void init( final ApplicationController controller ) {
		// initialize the drawing pane
		drawingPane.init();
		
		// initialize the font manager
		CxFontManager.init( drawingPane.getGraphics() );
		
		// initialize the pick list dialog
		pickListDialog = PickListDialog.init( controller, new PickListEventHandler() );
		pickListDialog.addKeyListener( new KeyEventHandler() );
		this.addKeyListener( new KeyEventHandler() );
		
		// initialize the camera
		final Camera camera = controller.getCamera();
		camera.init( drawingPane );
		
		// set the default "active" function
		setActiveFunction( FunctionManager.getFunctionByClass( LinePtToPtFunction.class ) );
		
		// attach the window listeners
		frame.addComponentListener( new ComponentResizeHandler() );
		frame.addWindowListener( new WindowEventHandler() );
		
		// attach the mouse listeners
		final MouseEventHandler mouseEventHandler = new MouseEventHandler();
		drawingPane.addMouseListener( mouseEventHandler );
		drawingPane.addMouseMotionListener( mouseEventHandler );
		drawingPane.addMouseWheelListener( mouseEventHandler );
	
		// attach the drag & drop listener
		final DropTarget dropTarget = new DropTarget( drawingPane, new DropTargetHandler() );
		dropTarget.setDefaultActions( DnDConstants.ACTION_COPY );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.ApplicationController#getActiveFunction()
	 */
	public Function getActiveFunction() {
		return activeFunction;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.ApplicationController#setActiveFunction(constellation.functions.Function)
	 */
	public void setActiveFunction( final Function function ) {
		// unload the currently "active" function
		if( activeFunction != null ) {
			activeFunction.onFinish( controller );
		}
		
		// if the pick list is shown, hide it
		if( pickListDialog.isVisible() ) {
			pickListDialog.setVisible( false );
		}
		
		// is it a selection function?
		final boolean supportsSelection = function.supportsSelection();
		
		// change the cursor based on the function type
		drawingPane.setCursor( supportsSelection 
								? Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) 
								: Cursor.getDefaultCursor() ); 
		
		// indicate to auto-highlight
		controller.showHighlights( supportsSelection );

		// clear the status area
		this.clearStatusMessage();
		
		// set the function information
		this.setFunctionInformation( function );
		
		// reset selections and temporary geometry
		final GeometricModel model = controller.getModel();
		model.clearPickedElement();
		model.clearSelectedElements();
		model.clearTemporaryElement();
		
		// request a redraw
		render();
		
		// call the "On Start" function
		function.onStart( controller );
		
		// install the function's parameter plug-in
		final InputDialog dialog = InputDialog.getInstance( controller );
		dialog.setParameterPlugin( function.getParameterPlugin() );
		
		// record the function
		this.activeFunction = function;
	}
	
	public void setFunctionInformation( final Function function ) {
		informationBar.setFunctionInformation( function );
	}

	/** 
	 * Sets the instructional steps
	 * @param steps the given {@link Steps steps}
	 */
	public void setInstructionalSteps( final Steps steps ) {
		informationBar.setInstruction( steps );
	}

	/**
	 * Clears the status message
	 */
	public void clearStatusMessage() {
		statusBar.clearStatus();
	}

	/** 
	 * Sets the current status message
	 * @param statusText the given status message text
	 */
	public void setStatusMessage( final String statusText ) {
		statusBar.setStatus( statusText );
	}

	/** 
	 * Returns the drawing pane
	 * @return the {@link DrawingPane drawing pane}
	 */
	public DrawingPane getDrawingPane() {
		return drawingPane;
	}

	/** 
	 * Returns the information bar
	 * @return the {@link InformationPane information bar}
	 */
	public InformationPane getInformationBar() {
		return informationBar;
	}

	/**
	 * Returns the plug-in manager
	 * @return the {@link PluginManager plug-in manager} instance
	 */
	public PluginManager getPluginManager() {
		return statusBar.getPluginManager();
	}

	/** 
	 * Returns the center anchor point in the middle of the window
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	public Point getCenterAnchorPoint( final JDialog dialog ) {
		// get the position and/or size of the various components
		final Dimension dlgSize = dialog.getSize();
		final Dimension frmSize = frame.getSize();
		
		// compute the new position
		final int x = ( frmSize.width - dlgSize.width ) / 2;
		final int y = ( frmSize.height - dlgSize.height ) / 2;

		// return the anchor point
		return new Point( x, y );
	}
	
	/** 
	 * Returns the left-most anchor point just above the navigation bar
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	public Point getLowerLeftAnchorPoint( final JDialog dialog ) {
		// get the position and/or size of the various components
		final Dimension dlgSize	= dialog.getSize();
		final Dimension frmSize = frame.getSize();
		final Dimension navSize = statusBar.getSize();
		
		// compute the new position
		final int x = frame.getX();
		final int y = frmSize.height - ( dlgSize.height + navSize.height );

		// return the anchor point
		return new Point( x, y );
	}
	
	/** 
	 * Returns the anchor point for the given dialog
	 * @param dialog the given {@link JDialog dialog}
	 * @return the anchor {@link Point point}
	 */
	public Point getLowerRightAnchorPoint( final JDialog dialog ) {
		// get the position and/or size of the various components
		final Dimension scnSize	= Toolkit.getDefaultToolkit().getScreenSize();
		final Insets frmInsets 	= frame.getInsets();
		final Dimension dlgSize = dialog.getSize();
		final Dimension frmSize = frame.getSize();
		final Dimension navSize = statusBar.getSize();
		
		// compute the new position
		final int eastOfFrameX = frame.getX() + frmSize.width;
		final int x = ( eastOfFrameX + dlgSize.width < scnSize.width ) ? eastOfFrameX : eastOfFrameX - dlgSize.width;
		final int y = frmSize.height - ( frame.getY() + frmInsets.top + frmInsets.bottom + dlgSize.height + navSize.height );
		
		// return the anchor point
		return new Point( x, y );
	}
	
	/** 
	 * Returns the left-most anchor point just below the information bar
	 * @return the anchor {@link Point point}
	 */
	public Point getUpperLeftAnchorPoint() {
		// get the position and/or size of the various components
		final Dimension ibSize = informationBar.getSize();
		
		// compute the new position
		final int x = frame.getX();
		final int y = frame.getInsets().top + 
						frame.getJMenuBar().getHeight() + 
						this.getY() + 
						informationBar.getY() + 
						ibSize.height;

		// return the anchor point
		return new Point( x, y );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.ApplicationController#getUpperRightAnchorPoint(javax.swing.JDialog)
	 */
	public Point getUpperRightAnchorPoint( final JDialog dialog ) {
		// get the position and/or size of the various components
		final Dimension scnSize	= Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension ibSize = informationBar.getSize();
		final Dimension frmSize = frame.getSize();
		final Insets frmInsets = frame.getInsets();
		final Dimension dlgSize = dialog.getSize();
		
		// compute the new position
		final int eastOfFrameX = frame.getX() + frmSize.width;
		final int x = ( eastOfFrameX + dlgSize.width < scnSize.width ) ? eastOfFrameX : eastOfFrameX - dlgSize.width;
		final int y = frmInsets.top + frame.getJMenuBar().getHeight() + this.getY() + informationBar.getY() + ibSize.height;

		// return the anchor point
		return new Point( x, y );
	}
	
	/**
	 * Returns the selection boundary at the current mouse position
	 * @return the selection {@link Rectangle boundary}
	 */
	public Rectangle getSelectionBoundary() {
		// compute the (x,y) coordinate
		final int x = mouseMovePos.x - ( selectionWidth / 2 );
		final int y = mouseMovePos.y - ( selectionHeight / 2 );
		
		// return the boundary instance
		return new Rectangle( x, y, selectionWidth, selectionHeight );
	}
	
	/** 
	 * Returns the selection boundary at the given (x,y) coordinate
	 * @param p the given (x,y) coordinate point
	 * @return the selection {@link Rectangle boundary}
	 */
	public Rectangle getSelectionBoundary( final Point p ) {
		// compute the (x,y) coordinate
		final int xa = p.x - ( selectionWidth / 2 );
		final int ya = p.y - ( selectionHeight / 2 );
		
		// return the boundary instance
		return new Rectangle( xa, ya, selectionWidth, selectionHeight );
	}
	
	/** 
	 * Sets the current zoom/scale
	 * @param scale the given scale
	 */
	public void setScale( final double scale ) {
		informationBar.setScale( scale );
	}
	
	/**
	 * Copies the off-screen graphics context to the 
	 * content pane (virtual screen)
	 */
	public void render() {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// if there's temporary geometry, redraw is required
		/*if( model.getTemporaryElement() != null ) {
			redrawNeeded = true; 
		}*/
		
		// get the camera instance
		final Camera camera = controller.getCamera();
		synchronized( camera ) {
			// allow the camera to re-draw the scene
			camera.render( controller, model, drawingPane.getBuffer() );	
			
			// render the drawing pane
			repaint();
		}
			
		// does this drawing pane have the focus?
		this.requestFocus();
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Rendering Methods
	/////////////////////////////////////////////////////////////////////
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	public void repaint() {
		// render the drawing pane
		if( drawingPane != null ) {
			drawingPane.render();
		}
		
		// allow parent to repaint
		super.repaint();
	}

	/**
	 * Sets the icon that corresponds to the given input type
	 * @param inputType the given {@link CxInputTypes input type}
	 */
	private void setInputType( final CxInputTypes inputType ) {
		statusBar.setInputType( inputType );
	}
	
	/**
	 * Sets the icon representing a mouse click
	 * @param button the given mouse button that was clicked
	 */
	private void setInputType( final int button ) {
		switch( button ) {
			case 1: statusBar.setInputType( MOUSE_LEFT ); break;
			case 2: statusBar.setInputType( MOUSE_MIDDLE ); break;
			case 3: statusBar.setInputType( MOUSE_RIGHT ); break;
		}
	}

	/////////////////////////////////////////////////////////////////////////
	//		Component Resize Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////////

	/** 
	 * Component Resize Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class ComponentResizeHandler implements ComponentListener {
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
		 */
		public void componentHidden( final ComponentEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
		 */
		public void componentMoved( final ComponentEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
		 */
		public void componentResized( final ComponentEvent event ) {	
			// maintain the minimum window size
			final Dimension proposedSize = frame.getSize();
			if( proposedSize.width < MIN_WIDTH || proposedSize.height < MIN_HEIGHT ) {
				final int newWidth	= ( proposedSize.width < MIN_WIDTH ) ? MIN_WIDTH : proposedSize.width;
				final int newHeight = ( proposedSize.height < MIN_HEIGHT ) ? MIN_HEIGHT : proposedSize.height;
				frame.setSize( newWidth, newHeight );
			}
			
			// re-initialize and re-paint the scene
			final Camera camera = controller.getCamera();
			drawingPane.init();
			camera.init( drawingPane );
		
			// repaint the panels
			frame.repaint();
			drawingPane.repaint();
			
			// redraw needed
			render(); // render()
			
			// update the system preferences
			final CxSystemPreferences systemPreferences = CxSystemPreferences.getInstance();
			systemPreferences.setWidth( frame.getWidth() );
			systemPreferences.setHeight( frame.getHeight() );
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
		 */
		public void componentShown( final ComponentEvent event ) {
			// do nothing
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//		Drag & Drop Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Drag & Drop Target Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class DropTargetHandler implements DropTargetListener {
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
		 */
		public void dragEnter( final DropTargetDragEvent event ) {
			frame.setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
		 */
		public void dragExit( final DropTargetEvent event ) {
			frame.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
		 */
		public void dragOver( final DropTargetDragEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
		 */
		public void drop( final DropTargetDropEvent event ) {
			handleDrop( event ); 
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
		 */
		public void dropActionChanged( final DropTargetDragEvent event ) {	
			logger.info( "CxApplicationController: dropActionChanged" );
		}
		
		/**
		 * Handles the drag drop event
		 * @param event the given {@link DropTargetDropEvent drop target drop event}
		 */
		private void handleDrop( final DropTargetDropEvent event ) {
			final Transferable transferable = event.getTransferable();
			if( transferable != null ) {
				// get the data flavors for the transferable object
				final DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
				
				// find the appropriate flavor
				for( final DataFlavor flavor : dataFlavors ) {
					// is it a file list?
					if( flavor.equals( DataFlavor.javaFileListFlavor ) ) {
						// accept the drop
						event.acceptDrop( DnDConstants.ACTION_COPY );
						
						try {
							// process each file in the list
							@SuppressWarnings("unchecked")
							final Iterable<File> fileList = (Iterable<File>)transferable.getTransferData( flavor );
							for( final File file : fileList ) {								
								// is it an image file?
								if( isImageFile( file ) ) {
									final Point p = event.getLocation();
									importPicture( file, p.x, p.y );
								}
								
								// is it a model file?
								else {
									final ModelFormatManager formatManager = ModelFormatManager.getInstance();
									final ModelFormatReader reader = formatManager.getFormatReader( file );
									if( reader != null ) {
										controller.setModel( reader.readFile( file ) );
									}
								}
							}
							
							// complete the drop event
							event.getDropTargetContext().dropComplete( true );
						} 
						catch( final Exception cause ) {
							controller.showErrorDialog( "Image import error", cause );
							event.rejectDrop();
						}
						
						// change back to the cross-hairs cursor
						frame.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
					}
				}
			}
		}
		
		/**
		 * Imports the given image file as a picture
		 * @param file the given image {@link File file}
		 * @throws IOException
		 */
		private void importPicture( final File file, final int px, final int py ) 
		throws IOException {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
 			// get the image name
			final String imageName = model.getNamingService().getEntityName( EntityCategoryTypes.IMAGE );
			
			// import the user image
			final UserImage image = UserImage.createUserImage( imageName, file );
			
			// add the user image to the model
			model.addUserImage( image );
			
			// get the matrix
			final MatrixWCStoSCS matrix = controller.getMatrix();
			
			// attach a picture in the model at the current mouse position
			final PointXY p = matrix.untransform( new Point( px, py ) );
			model.addPhysicalElement( new CxModelElement( new PictureXY( p, image ) ) );
			
			// alert the operator
			controller.setStatusMessage( format( "Imported picture '%s' at %s", file.getName(), p ) );
			
			// request a redraw
			render();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//		Keyboard Event Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Keyboard Event Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class KeyEventHandler implements KeyListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed( final KeyEvent event ) {
			// set the key pressed icon
			setInputType( KEYBOARD );
			
			// handle the event
			handleInputEvent( event );
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased( final KeyEvent event ) {
			// do nothing
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped( final KeyEvent event ) {
			// do nothing
		}
		
		/**
		 * Handles the input event
		 * @param event the given {@link KeyEvent key event}
		 */
		private void handleInputEvent( final KeyEvent event ) {
			// get the key code
			final int keyCode = event.getKeyCode();
			//logger.info( format( "keyCode = '%02X' [%c]", keyCode, (char)keyCode ) );
			
			// decipher the key press
			switch( keyCode ) {
				// reset the active function?
				case VK_ESCAPE: setActiveFunction( activeFunction ); break;
				case VK_UP:		panCamera(  0, -5 ); break;
				case VK_LEFT:	panCamera( -5,  0 ); break;
				case VK_RIGHT:	panCamera( +5,  0 ); break;
				case VK_DOWN:	panCamera(  0, +5 ); break;
					
				default:
					boolean consumed = false;
					
					// is it a numeric key?
					if( keyCode >= VK_0 && keyCode <= VK_9 ) {
						// is the pick list visible?
						if( pickListDialog.isVisible() ) {
							// select the element by index
							final int index = ( keyCode - VK_0 );
							logger.info( format( "Selecting index %d", index ) );
							consumed = pickListDialog.selectIndex( index );
						}
						
						// if the event hasn't been consumed
						if( !consumed ) {
							// notify the "active" function
							activeFunction.onKeyPressed( controller, event, mouseMovePos );
						}
					}
					break;
			}
		}
		
		/**
		 * Pans the camera from the old mouse-coordinates to the new mouse coordinates
		 * @param deltaX the delta X-axis coordinates
		 * @param deltaY the delta Y-axis coordinates
		 */
		private void panCamera( final double deltaX, final double deltaY ) {
			// pan the camera by the delta
			final Camera camera = controller.getCamera();
			camera.pan( deltaX, deltaY );
			render();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//		Mouse Event Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Mouse Event Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class MouseEventHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
	
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged( final MouseEvent event ) {
			try { 
				boolean consumed = false;
				
				// update the space position
				updateMouseXY( event );
				
				// determine the clicked button 
				int button = event.getButton();
				if( button == MouseEvent.NOBUTTON ) {
					button = BUTTON_SELECT;
				}
				
				// invoke the action function if it supports dragging
				if( activeFunction.supportsDrag() ) {
					// process the mouse drag
					final MouseClick oldMouseClick = new MouseClick( oldMouseMovePos, selectionWidth, selectionHeight, button );
					final MouseClick newMouseClick = new MouseClick( mouseMovePos, selectionWidth, selectionHeight, button );
					consumed = activeFunction.processMouseDrag( controller, oldMouseClick, newMouseClick );
				}
				
				// button #1 (left button drag)
				if( !consumed && button == BUTTON_SELECT ) {
					// move a picture?
					synchronized( controller ) {
						// move a picture?
						consumed = movePictures( oldMouseMovePos, mouseMovePos );
					}
					
					// pan the camera
					if( !consumed ) {
						panCamera( oldMouseMovePos, mouseMovePos ); 
					}			
				}
			}
			catch( final Exception e ) {
				controller.showErrorDialog( "Function Error", e );
			}
			
			// consume the event
			event.consume();
			
			// redraw needed
			render();
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved( final MouseEvent event ) {
			// update the space position
			updateMouseXY( event );
			
			// notify the active function of mouse movement
			try {
				activeFunction.processMouseMovement( controller, mouseMovePos );
			}
			catch( final Exception e ) {
				controller.showErrorDialog( "Function Error", e );
			}
		}
			
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked( final MouseEvent event ) {
			// update the space position
			updateMouseXY( event );
			
			// get the clicked mouse button
			int button = event.getButton();
			
			// get the mouse click count
			final int clickCount = event.getClickCount();
			
			// if double click, treat like an "Indicate"
			if( clickCount == 2 && button == BUTTON_SELECT ) {
				button = BUTTON_INDICATE;
			}
			
			// create the mouse click instance
			final MouseClick click = new MouseClick( mouseMovePos, selectionWidth, selectionHeight, button );
			
			// alert the active function of the mouse click
			try {
				activeFunction.processMouseClick( controller, click );
			}
			catch( final Exception e ) {
				controller.showErrorDialog( "Function Error", e );
			}
			
			// does this drawing pane have the focus?
			drawingPane.requestFocus();
			
			// redraw needed
			render();
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered( final MouseEvent event ) {
			// update the space position
			updateMouseXY( event );
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited( final MouseEvent event ) {
			// clear the highlights
			final GeometricModel model = controller.getModel();
			model.clearHighlightedGeometry();
			
			// update the space position
			updateMouseXY( event );
			
			// redraw needed
			render();
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed( final MouseEvent event ) {
			// set the mouse click icon
			setInputType( event.getButton() );
			
			// update the space position
			updateMouseXY( event );
			
			// is it a pop-up trigger?
			if( event.isPopupTrigger() ) {
				final JPopupMenu popupMenu = activeFunction.getPopupMenu( controller );
				if( popupMenu != null ) {
					popupMenu.show( event.getComponent(), event.getX(), event.getY() );
				}
			}
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased( final MouseEvent event ) {
			// update the space position
			updateMouseXY( event );
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		public void mouseWheelMoved( final MouseWheelEvent event ) {
			// set the input type
			statusBar.setInputType( MOUSE_WHEEL );
			
			// get the mouse wheel rotation & scroll amount
			final double scrollAmount = event.getScrollAmount();
			final double wheelRotation = event.getWheelRotation();
			final double unitsToScroll = event.getUnitsToScroll();
			
			// update the space position
			updateMouseXY( event );
			
			// handle the event
			switch( event.getScrollType() ) {
				case WHEEL_BLOCK_SCROLL: 
					//processMouseWheelScroll( wheelRotation, scrollAmount );
					break;
					
				case WHEEL_UNIT_SCROLL: 
					adjustZoom( scrollAmount, wheelRotation, unitsToScroll );
					break;
			}
			
			// consume the event
			event.consume();
		}
		 
		/**
		 * Adjust the current zoom setting according to the given
		 * input information that is as a result of a mouse scroll wheel
		 * invocation. 
		 * @param scrollAmount the mouse wheel scroll amount
		 * @param wheelRotation the mouse wheel rotation
		 * @param unitsToScroll the mouse wheel units to scroll
		 */
		private void adjustZoom( final double scrollAmount, 
								 final double wheelRotation, 
								 final double unitsToScroll ) {		
			// get the matrix instance
			final MatrixWCStoSCS matrix = controller.getMatrix();
			
			// get the current scale
			final double scale = matrix.getScale();
			
			// get the current unit scale
			final double unitScale = matrix.getUnitScale();
			
			// compute the scale delta
			final double scaleDX = ( unitsToScroll / 100d );
			
		//	logger.info( format( "unitsToScroll = %.0f, scale = %3.2f, unitScale = %3.2f, scaleDX = %3.5f", unitsToScroll, scale, unitScale, scaleDX ) );
			
			// compute the new effective scale
			final double newScale	= scale + scaleDX;
			final double newEffScale = newScale * unitScale;
			
			// insure that the zoom factor is within bounds
			if( ( newScale > 0 ) &&  ( newEffScale >= MIN_ZOOM && newEffScale <= MAX_ZOOM ) ) { 
				// adjust the camera's zoom
				final Camera camera = controller.getCamera();
				camera.setZoomFactor( scale + scaleDX );
				
				// request redraw
				render();
			}
		}	
		
		/**
		 * Pans the camera from the old mouse-coordinates to the new mouse coordinates
		 * @param oldMouseX the old mouse X-axis coordinates
		 * @param oldMouseY the old mouse Y-axis coordinates
		 * @param mouseX the new mouse X-axis coordinates
		 * @param mouseY the new mouse Y-axis coordinates
		 */
		private void panCamera( final Point oldMousePos, final Point mousePos ) {
			// get the delta between the old & new coordinates
			final double deltaX = mousePos.x - oldMousePos.x;
			final double deltaY = mousePos.y - oldMousePos.y;
			
			// pan the camera by the delta
			final Camera camera = controller.getCamera();
			camera.pan( deltaX, deltaY );
		}
		
		/**
		 * Drags the picture from the old mouse-coordinates to the new mouse coordinates
		 * @param oldMousePos the old mouse {@link Point position}
		 * @param mousePos the new mouse {@link Point position}
		 * @return true, if at least one picture was moved
		 */
		private boolean movePictures( final Point oldMousePos, final Point mousePos ) {
			// get the selection boundary
			final RectangleXY boundary = 
				controller.untransform( getSelectionBoundary( oldMousePos ) );
			
			// determine the intersecting elements
			final Collection<ModelElement> returnSet = new LinkedList<ModelElement>();
			lookupElementsInclusionSet( controller, boundary, returnSet, PICTURE );
			
			// get the pictures within the click boundary
			if( !returnSet.isEmpty() ) {
				// determine how much to move the objects
				final PointXY p0 = controller.untransform( oldMousePos );
				final PointXY p1 = controller.untransform( mousePos );
				
				// get the delta X and Y values
				final double dx = p1.x - p0.x;
				final double dy = p1.y - p0.y;
				
				// move the pictures
				for( final ModelElement element : returnSet ) {
					final PictureXY picture = (PictureXY)element;
					picture.moveRelative( dx, dy );
				}
			}

			return !returnSet.isEmpty();
		}

		/**
		 * Updates the current mouse position
		 * @param event the given {@link MouseEvent mouse event}
		 */
		private void updateMouseXY( final MouseEvent event ) {
			// record the old mouse positions
			oldMouseMovePos = mouseMovePos;
			
			//  get the mouse positions
			mouseMovePos = event.getPoint();
			
			// if the previous mouse position is null ...
			if( oldMouseMovePos == null ) {
				oldMouseMovePos = mouseMovePos;
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//		Pick List Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * Pick List Event Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class PickListEventHandler implements PickListObserver {

		/* 
		 * (non-Javadoc)
		 * @see constellation.functions.PickListObserver#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
		 */
		public void elementSelected( final ApplicationController controller,
									 final ModelElement element ) {
			// invoke the active function's pick list handler
			if( element instanceof IndexedModelElement ) {
				final IndexedModelElement indexedElement = (IndexedModelElement)element;
				activeFunction.elementSelected( controller, indexedElement.getHostElement() );
			}
			else {
				activeFunction.elementSelected( controller, element );
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Window Event Handler (Inner Class)
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Window Event Handler
	 * @author lawrence.daniels@gmail.com
	 */
	private class WindowEventHandler implements WindowListener {
		
		/**
		 * Default Constructor
		 */
		public WindowEventHandler() {
			super();
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
		 */
		public void windowActivated( final WindowEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
		 */
		public void windowClosed( final WindowEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing( final WindowEvent event ) {
			controller.shutdown();
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
		 */
		public void windowDeactivated( final WindowEvent event ) {
			// do nothing	
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
		 */
		public void windowDeiconified( final WindowEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
		 */
		public void windowIconified( final WindowEvent event ) {
			// do nothing
		}
	
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
		 */
		public void windowOpened( final WindowEvent event ) {
			// do nothing	
		}
	}
	
}
