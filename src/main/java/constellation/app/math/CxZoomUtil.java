package constellation.app.math;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.Icon;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.drawing.Camera;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.RectangleXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * Constellation Zoom Utilities
 * @author lawrence.daniels@gmail.com
 */
public class CxZoomUtil {
	// zoom icon declarations
	private static final CxContentManager imgMgr	= CxContentManager.getInstance();
	public  static final Icon ZOOM_AUTOFIT_ICON 	= imgMgr.getIcon( "images/zoom/zoom-fit.png" );
	public  static final Icon ZOOM_1TO1_ICON 		= imgMgr.getIcon( "images/zoom/zoom-1to1.png" );
	public  static final Icon ZOOM_1TO2_ICON 		= imgMgr.getIcon( "images/zoom/zoom-1to2.png" );
	public  static final Icon ZOOM_2TO1_ICON 		= imgMgr.getIcon( "images/zoom/zoom-2to1.png" );
	public  static final Icon ZOOM_IN_ICON 			= imgMgr.getIcon( "images/zoom/zoom-in.png" );
	public  static final Icon ZOOM_OUT_ICON 		= imgMgr.getIcon( "images/zoom/zoom-out.png" );
	public  static final Icon ZOOM_WINDOW_ICON 		= imgMgr.getIcon( "images/zoom/zoom-window.png" );
	
	// miniature zoom icon declarations
	public  static final Icon MINI_ZOOM_1TO1_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-1to1.png" );
	public  static final Icon MINI_ZOOM_1TO2_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-1to2.png" );
	public  static final Icon MINI_ZOOM_2TO1_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-2to1.png" );
	public  static final Icon MINI_ZOOM_PREV_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-previous.png" );
	public  static final Icon MINI_ZOOM_PAGE_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-page.png" );
	public  static final Icon MINI_ZOOM_WIDTH_ICON 	= imgMgr.getIcon( "images/zoom/mini/zoom-pagewidth.png" );
	
	/**
	 * Auto-fits all geometry into the current view
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void autoFit( final ApplicationController controller ) {		
		// get the geometry
		final GeometricModel model = controller.getModel();
		
		// zoom to fit the geometry
		final Collection<ModelElement> elements = new LinkedList<ModelElement>(); 
		model.getVisibleElements( elements );
		
		// get the boundary
		final RectangleXY boundary = 
			( !elements.isEmpty() ) ? ElementDetectionUtil.getBounds( elements, controller.getMatrix() ) : null;
		
		// if boundary was computed, use it
		if( boundary != null ) {
			//  perform the fit
			controller.zoomToFit(boundary );
		}
		
		// otherwise, just re-center the view
		else {
			final MatrixWCStoSCS matrix = controller.getMatrix();
			matrix.reset();
			matrix.setScale( 0.50d );
		}
		
		// request a draw of the screen
		controller.requestRedraw();
	}
	
	/**
	 * Sets the Zoom Ratio to 1:1.
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void zoom1To1( final ApplicationController controller ) {		
		// reset the camera
		final Camera camera = controller.getCamera();
		camera.setZoomFactor( 1d );
		
		// request redraw
		controller.requestRedraw();
	}
	
	/**
	 * Sets the Zoom Ratio to 1:2.
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void zoom1To2( final ApplicationController controller ) {		
		// reset the camera
		final Camera camera = controller.getCamera();
		camera.setZoomFactor( 0.5d );
		
		// request redraw
		controller.requestRedraw();
	}
	
	/**
	 * Sets the Zoom Ratio to 1:2.
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void zoom2To1( final ApplicationController controller ) {		
		// reset the camera
		final Camera camera = controller.getCamera();
		camera.setZoomFactor( 2d );
		
		// request redraw
		controller.requestRedraw();
	}
	
	/**
	 * Zooms in/out resetting the view to its original state.
	 * @param controller the given {@link ApplicationController controller}
	 */
	public static void zoomOut( final ApplicationController controller ) {		
		// reset the camera
		final Camera camera = controller.getCamera();
		camera.reset();
		
		// request redraw
		controller.requestRedraw();
	}
	
	/** 
	 * Auto-Fit Action: This action listener is intended for use
	 * by any module that requires the use of the auto-fit feature.
	 * @author lawrence.daniels@gmail.com
	 */
	public static class AutoFitAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Auto-fit action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public AutoFitAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// perform the auto-fit
			autoFit( controller );
		}
	}
	
	/** 
	 * Zoom 1:1 Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Zoom1to1Action implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Zoom 1:1 action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public Zoom1to1Action( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// perform the zoom
			zoom1To1( controller );
		}
	}
	
	/** 
	 * Zoom 1:2 Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Zoom1to2Action implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Zoom 1:2 action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public Zoom1to2Action( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// perform the zoom
			zoom1To2( controller );
		}
	}
	
	/** 
	 * Zoom 2:1 Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class Zoom2to1Action implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Zoom 1:2 action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public Zoom2to1Action( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// perform the zoom
			zoom2To1( controller );
		}
	}
	
	/** 
	 * Zoom Out Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class ZoomOutAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new Zoom Out action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public ZoomOutAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// perform the zoom
			zoomOut( controller );
		}
	}

}
