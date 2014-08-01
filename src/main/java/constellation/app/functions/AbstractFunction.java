package constellation.app.functions;

import static constellation.app.math.ElementDetectionUtil.lookupElementsByRegion;
import static constellation.app.math.ElementDetectionUtil.lookupPhantomElementsByRegion;
import static constellation.drawing.EntityRepresentationUtil.getTypeName;
import static java.lang.String.format;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.RectangleXY;
import constellation.functions.Function;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.model.GeometricModel;
import constellation.ui.components.CxDialog;

/**
 * Represents a drawing function with optional input dialog
 * @author lawrence.daniels@gmail.com
 */
public abstract class AbstractFunction implements Function {
	// constants
	private static final Collection<ModelElement> NO_GEOMETRY = new ArrayList<ModelElement>(0);
	private static final CxContentManager contentManager = CxContentManager.getInstance();
	
	// logger instance
	protected final Logger logger = Logger.getLogger( getClass() );
	
	// internal fields
	private final Class<? extends InputDialog> dialogClass;
	private final String familyName;
	private final String commandName;
	private final Icon icon;
	private final String helpPath;
	
	// mutable fields
	private boolean showDialog;
	protected InputDialog dialog;
	
	/** 
	 * Creates a function instance
	 * @param familyName the name of the function's family
	 * @param commandName the name of the command (sub-function)
	 * @param iconPath the given icon path
	 * @param helpPath the given help path
	 */
	public AbstractFunction( final String familyName, 
							 final String commandName, 
							 final String iconPath, 
							 final String helpPath ) {
		this( familyName, commandName, iconPath, helpPath, null );
	}
	
	/** 
	 * Creates a function instance
	 * @param familyName the name of the function's family
	 * @param commandName the name of the command (sub-function)
	 * @param iconPath the given icon path
	 * @param helpPath the given help path
	 */
	public AbstractFunction( final String familyName, 
							 final String commandName, 
							 final String iconPath,
							 final String helpPath, 
							 final Class<? extends InputDialog> dialogClass ) {
		this.familyName		= familyName;
		this.commandName 	= commandName;
		this.icon			= ( iconPath != null ) ? contentManager.getIcon( iconPath ) : null;	
		this.helpPath		= helpPath;
		this.dialogClass	= dialogClass;
		this.showDialog		= ( dialogClass != null );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		// this method may be overridden
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getFamilyName()
	 */
	public String getFamilyName() {
		return familyName;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getIcon()
	 */
	public Icon getIcon() {
		return icon;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getHelpPath()
	 */
	public String getHelpPath() {
		return helpPath;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getName()
	 */
	public String getName() {
		return commandName;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getParameterPlugin()
	 */
	public FunctionDialogPlugIn getParameterPlugin() {
		return null;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getPopupMenu()
	 */
	public JPopupMenu getPopupMenu( final ApplicationController controller ) {
		return new DefaultPopupMenu( controller );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#onKeyPressed(constellation.ApplicationController)
	 */
	public void onKeyPressed( final ApplicationController controller, KeyEvent event, final Point mousePos ) {
		showDialog( mousePos );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		if( dialogClass != null ) {
			// initialize the dialog class
			if( dialog == null ) {
				dialog = createDialogInstance( controller );
			}		
			
			// reset the dialog
			dialog.reset( controller );
			
			// show the dialog?
			if( showDialog ) {
				dialog.makeVisible();
			}
		}
		
		// clear selections
		final GeometricModel model = controller.getModel();
		model.clearPickedElement();
		model.clearTemporaryElement();
		model.clearSelectedElements();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onFinish(constellation.functions.ApplicationController)
	 */
	public void onFinish( final ApplicationController controller ) {
		final JDialog dialog = getInputDialog();
		if( dialog != null ) {
			dialog.setVisible( false );
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	public void processMouseClick( ApplicationController controller, MouseClick mouseClick  ) {
		// may be overridden
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// get the model
		final GeometricModel model = controller.getModel();
		
		// determine if any geometry should be highlighted
		final Collection<ModelElement> highlightedGeometry = getHighlightedGeometry( controller );
		if( !highlightedGeometry.isEmpty() || 
				( model.getHighlightedGeometry() != null && 
						!model.getHighlightedGeometry().isEmpty() ) ) {
			// set the highlighted geometry
			model.setHighlightedGeometry( highlightedGeometry );
			
			// a redraw will be necessary
			controller.requestRedraw();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseDrag(constellation.functions.ApplicationController, double, double)
	 */
	public boolean processMouseDrag( ApplicationController controller, MouseClick oldMousePos, MouseClick newMousePos ) {
		return false;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#supportsDrag()
	 */
	public boolean supportsDrag() {
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#supportsSelection()
	 */
	public boolean supportsSelection() {
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getInputDialog()
	 */
	public CxDialog getInputDialog() {
		return dialog;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#toggleVisibility()
	 */
	public void toggleDialogVisibility() {
		if( dialog != null ) {
			dialog.setVisible( !dialog.isShowing() );
		}
	}
	 
	/**
	 * Shows the dialog, positioning it at the given location
	 * @param p the given {@link Point location}
	 */
	public void showDialog( final Point p ) {
		final CxDialog dialog = getInputDialog();
		if( dialog != null ) {
			dialog.setLocation( p ); 
			dialog.makeVisible();
		}
		
		// update the "show dialog" flag
		showDialog = true;
	}
	
	/**
	 * Shows the dialog
	 */
	public void showDialog() {
		final CxDialog dialog = getInputDialog();
		if( dialog != null ) {
			dialog.makeVisible();
		}
		
		// update the "show dialog" flag
		showDialog = true;
	}
	
	/**
	 * Hides the dialog
	 */
	public void hideDialog() {
		final CxDialog dialog = getInputDialog();
		if( dialog != null ) {
			dialog.setVisible( false );
		}
		
		// update the "show dialog" flag
		showDialog = false;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Selects the given element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link RenderableElement element}
	 */
	protected void select( final ApplicationController controller, final RenderableElement element ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mElement = (ModelElement)element;
			controller.setStatusMessage( format( "Selected %s '%s'", getTypeName( mElement.getType() ), mElement ) );
		}
		
		// select the element
		model.selectGeometry( element );
	}
	
	/** 
	 * Creates a new dialog instance
	 * @param controller the given {@link ApplicationController controller}
	 * @return the {@link InputDialog dialog} instance
	 */
	private InputDialog createDialogInstance( final ApplicationController controller ) {
		try {
			// lookup the singleton "getter" method
			final Method method = dialogClass.getMethod( "getInstance", new Class[] { ApplicationController.class } );
			
			// invoke the method
			return (InputDialog)method.invoke( null, new Object[] { controller } );
		}
		catch( final Exception cause ) {
			logger.error( format( "Error initializing dialog class '%s'", dialogClass.getName() ), cause );
		}
		return null;
	}

	/** 
	 * Returns the highlighted geometric elements
	 * @return the highlighted {@link ModelElement model elements}
	 */
	private Collection<ModelElement> getHighlightedGeometry( final ApplicationController controller ) {
		if( supportsSelection() ) {
			// get the on-screen selection boundary
			final Rectangle bounds = controller.getSelectionBoundary();
			
			// create a collection for returning the elements
			final List<ModelElement> returnSet = new LinkedList<ModelElement>();	
			
			// get the two-dimensional boundary
			final RectangleXY boundary = controller.untransform( bounds );
			
			// find the elements within the boundary
			switch( controller.getSelectionMode() ) {
				case PHYSICAL_ELEMENTS:
					lookupElementsByRegion( controller, boundary, returnSet );
					break;
					
				case PHANTOM_ELEMENTS:
					lookupPhantomElementsByRegion( controller, boundary, returnSet );
					break;
			}
			
 			return returnSet;
		}
		return NO_GEOMETRY;
	}
	
}
