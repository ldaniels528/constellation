package constellation.app;

import static java.lang.Math.abs;
import static java.lang.String.format;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.PluginManager;
import constellation.SelectionMode;
import constellation.app.drawing.DefaultCamera;
import constellation.app.functions.layout.FilterManagementDialog;
import constellation.app.ui.CxContentPane;
import constellation.app.ui.CxMenuBar;
import constellation.app.ui.DrawingPane;
import constellation.app.ui.UserInterfaceUtil;
import constellation.app.ui.statusbar.EventManager;
import constellation.drawing.Camera;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.functions.Function;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;
import constellation.model.DefaultGeometricModel;
import constellation.model.Filter;
import constellation.model.GeometricModel;
import constellation.preferences.SystemPreferences;

/**
 * Constellation Application Controller Implementation
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class CxApplicationController implements ApplicationController {
	// get the application version
	private static final String VERSION = Constellation.class.getAnnotation(CxVersion.class).value();
	
	// logger instance
	private final Logger logger = Logger.getLogger( getClass() );
	
	// immutable fields
	private final SystemPreferences systemPreferences;
	private final CxThreadPool threadPool;
	private final CxContentPane contentPane;
	private final DrawingPane drawingPane;
	private final MatrixWCStoSCS matrix;
	private final CxFrame frame;
	private final Camera camera;
	
	// mutable fields
	private SelectionMode selectionMode;
	private boolean showHighlights;
	private GeometricModel model; 
	
	/////////////////////////////////////////////////////////////////////
	//		Constructor(s)
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new application frame
	 * @param systemPreferences the given {@link SystemPreferences system preferences}
	 */
	protected CxApplicationController( final SystemPreferences systemPreferences ) {		
		// create utility instances
		this.systemPreferences	= systemPreferences;
		this.threadPool			= new CxThreadPool();
		this.matrix				= new MyMatrixWCStoSCS();
		this.camera				= new DefaultCamera( matrix );
		this.selectionMode		= SelectionMode.PHYSICAL_ELEMENTS;
		
		// initialize the model instance
		this.model 				= DefaultGeometricModel.newModel();
		
		// update the matrix with the model units
		matrix.setScale( 0.5d );
		matrix.setUnitScale( model.getUnit().getModelScale() );
		
		// create the content panes	
		this.frame				= new CxFrame();
		this.contentPane 		= new CxContentPane( this, frame );
		this.drawingPane		= contentPane.getDrawingPane();
		
		// setup the frame
		frame.setJMenuBar( new CxMenuBar( this, frame ) );
		frame.setContentPane( new JScrollPane( contentPane ) );
		frame.setVisible( true );
		frame.requestFocus();
		
		// initialize the system preferences
		systemPreferences.init( this );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Life Cycle & Threading Methods
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Launches a new application window
	 */
	public static CxApplicationController launch( final SystemPreferences systemPreferences ) {
		final CxApplicationController frame = new CxApplicationController( systemPreferences );
		frame.init();
		return frame;
	}
	
	/**
	 * Initializes the frame
	 */
	protected void init() {		
		// initial the content pane
		contentPane.init( this );
		
		// update title bar
		updateTitle( model );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public PluginManager getPluginManager() {
		return contentPane.getPluginManager();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public CxThreadPool getThreadPool() {
		return threadPool;
	}	
	
	/** 
	 * {@inheritDoc}
	 */
	public void shutdown() {
		threadPool.shutdown();
	}
	
	////////////////////////////////////////////////////////////////////
	//		Display Preference-related Methods
	////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean showHighlights() {
		return showHighlights;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void showHighlights( final boolean enabled ) {
		showHighlights = enabled;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public SystemPreferences getSystemPreferences() {
		return systemPreferences;
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Screen Position & Boundary Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Point getCenterAnchorPoint( final JDialog dialog ) {
		return contentPane.getCenterAnchorPoint( dialog );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Point getLowerLeftAnchorPoint( final JDialog dialog ) {
		return contentPane.getLowerLeftAnchorPoint( dialog );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Point getLowerRightAnchorPoint( final JDialog dialog ) {
		return contentPane.getLowerRightAnchorPoint( dialog );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Point getUpperLeftAnchorPoint() {
		return contentPane.getUpperLeftAnchorPoint();
	}

	/** 
	 * {@inheritDoc}
	 */
	public Point getUpperRightAnchorPoint( final JDialog dialog ) {
		return contentPane.getUpperRightAnchorPoint( dialog );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Dimension getDrawingDimensions() {
		return drawingPane.getSize();
	}

	/** 
	 * {@inheritDoc}
	 */
	public Dimension getFrameDimensions( final double pctWidth, final double pctHeight ) {
		final int width = (int)( pctWidth * frame.getWidth() );
		final int height = (int)( pctHeight * frame.getHeight() );
		return new Dimension( width, height );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Selection Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setSelectionMode( final SelectionMode mode ) {
		this.selectionMode = mode;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Rectangle getSelectionBoundary( final Point p ) {
		return contentPane.getSelectionBoundary( p );
	}

	/** 
	 * {@inheritDoc}
	 */
	public Rectangle getSelectionBoundary() {
		return contentPane.getSelectionBoundary();
	}

	/////////////////////////////////////////////////////////////////////
	//		Function Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public Function getActiveFunction() {
		return contentPane.getActiveFunction();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setActiveFunction( final Function function ) {
		contentPane.setActiveFunction( function );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void showMessageDialog( final String message, final String title ) {
		JOptionPane.showMessageDialog( frame, message, title, JOptionPane.INFORMATION_MESSAGE );
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void showErrorDialog( final String message, final String title ) {
		JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void showErrorDialog( final String message, final String title, final Throwable cause ) {
		JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
		logger.error( message, cause );
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void showErrorDialog( final String title, final Throwable cause ) {
		JOptionPane.showMessageDialog( frame, cause.getMessage(), title, JOptionPane.ERROR_MESSAGE );
		logger.error( cause.getMessage(), cause );
		cause.printStackTrace();
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setStatusMessage( final String message ) {
		contentPane.setStatusMessage( message );
		
		// attach the message to the event manager
		final EventManager eventManager = EventManager.getInstance( this );
		eventManager.append( message );
		
		// request a redraw
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setInstructionalSteps( final Steps steps ) {
		contentPane.setInstructionalSteps( steps );
		requestRedraw();
	}

	/////////////////////////////////////////////////////////////////////
	//		Camera Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public Camera getCamera() {
		return camera;
	}

	/** 
	 * {@inheritDoc}
	 */
	public MatrixWCStoSCS getMatrix() {
		return matrix;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void repaint() {
		// repaint the scene
		contentPane.render();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void requestRedraw() {
		// render the scene
		repaint();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public PointXY untransform( final Point point ) {
		return matrix.untransform( point );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public RectangleXY untransform( final Rectangle boundary ) {
		return matrix.untransform( boundary );
				
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void zoomToFit( final RectangleXY boundary ) {
		// get the parameter values
		final double minX = boundary.getX();
		final double minY = boundary.getY();
		final double maxX = minX + boundary.getWidth();
		final double maxY = minY + boundary.getHeight();
		
		//  perform the fit
		zoomToFit( minX, minY, maxX, maxY );
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void zoomToFit( final double x1, 
						   final double y1, 
						   final double x2, 
						   final double y2 ) {
		// get the X- and Y-axis widths
		final double widthX = abs( x2 - x1 );
		final double widthY = abs( y2 - y1 );
		
		// get the clipper
		final Rectangle clipper = camera.getClippingPlane();
		
		// determine the X- and Y-scale factors
		final double scaleX 	= clipper.getWidth() / widthX;
		final double scaleY 	= clipper.getHeight() / widthY;
		final double zoomFactor	= ( scaleY > scaleX ) ? scaleX : scaleY; 
		
		//logger.info( format( "zoomToFit: bounds = ( %3.2f, %3.2f, %3.2f, %3.2f), scaleX = %3.2f, scaleY = %3.2f, zoomFactor = %3.2f", 
		//		x1, y1, widthX, widthY, scaleX, scaleY, zoomFactor ) ); // TODO remove this after testing
		
		// get the anchor points
		final double posX = -x1 * zoomFactor;
		final double posY = -y1 * zoomFactor;
			
		// reset the matrix
		matrix.reset();
		
	 	// move the camera into position
	 	camera.panTo( posX, posY );
	 	camera.setZoomFactor( zoomFactor / matrix.getUnitScale() );
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Model Methods
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * {@inheritDoc}
	 */
	public GeometricModel getModel() {
		return model;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void mergeModel( final GeometricModel mergeModel ) {		
		// add filters to the model
		final Collection<Filter> filters = mergeModel.getFilters();
		for( final Filter filter : filters ) {
			model.addFilter( filter );
		}
		
		// merge the geometry from the model
		model.addPhysicalElements( mergeModel.getPhysicalElements() );
		
		// redraw needed
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void setModel( final GeometricModel model ) {
		// point to the new model
		this.model = model;
		
		// update the title bar
		updateTitle( model );
		
		// adjust the screen matrix scale
		final double unitScale = model.getUnit().getModelScale();
		matrix.setUnitScale( unitScale );
		
		// update the filters
		FilterManagementDialog.getInstance( this ).update( model.getFilters() );
		
		// redraw needed
		requestRedraw();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void updateTitle( final GeometricModel model ) {
		frame.setTitle( format( "Constellation v%s - %s", VERSION, model.getName() ) );			
	}
	
	
	/////////////////////////////////////////////////////////////////////
	//		Constellation Frame (Inner Class)
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Constellation Frame
	 * @author lawrence.daniels@gmail.com
	 */
	private class CxFrame extends JFrame {
		
		/**
		 * Default Constructor
		 */
		public CxFrame() {
			super( format( "Constellation v%s", VERSION ) );
			super.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
			
			// setup the panel
			super.setAlwaysOnTop( false );
			super.pack();
			super.setSize( UserInterfaceUtil.getViewableDimensions( this ) ); 
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//		Internal SCS to MCS Matrix (Inner Class)
	/////////////////////////////////////////////////////////////////////
	
	/** 
	 * Internal WCS to SCS Matrix Implementation
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyMatrixWCStoSCS extends MatrixWCStoSCS {
		
		/** 
		 * {@inheritDoc}
		 */
		@Override
		protected boolean updateMatrices() {
			// allow the parent class to update the matrices
			final boolean updated = super.updateMatrices();
			
			// if the matrix required an update...
			if( updated ) {
				// request a redraw
				requestRedraw();
				
				// update the information bar
				contentPane.setScale( getScale() );
			}
			return updated;
		}
	}
	
}
