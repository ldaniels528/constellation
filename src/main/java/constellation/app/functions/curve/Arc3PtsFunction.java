package constellation.app.functions.curve;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.menu.CxMenuItem;

/** 
 * The ARC::3-PTS function
 * @author lawrence.daniels@gmail.com
 */
public class Arc3PtsFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the start #point of the arc",
		"Select or Indicate the center #point of the arc",
		"Select or Indicate the end #point of the arc"
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// internal fields
	private FunctionDialogPlugIn plugIn;
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	
	/**
	 * Default constructor
	 */
	public Arc3PtsFunction() {
		super( 
			"ARC", "3-PTS", 
			"images/commands/curve/arc-3pts.png", 
			"docs/functions/curve/arc_3pts.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handlePointSelection( controller, element, false );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.AbstractFunction#getParameterPlugin()
	 */
	@Override
	public FunctionDialogPlugIn getParameterPlugin() {
		return plugIn;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu( final ApplicationController controller ) {
		if( popupMenu == null ) {
			popupMenu = new MyPopupMenu( controller );
		}
		return popupMenu;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// initialize the plug-in
		if( plugIn == null ) {
			plugIn = new FunctionDialogPlugIn( null, new MyPlugInAction( controller ) );
		}
		
		// initialize the dialog
		dialog.resetIdentity( EntityCategoryTypes.CURVE );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// get the mouse click point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt =
					ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				
				// handle the point selection
				handlePointSelection( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				handlePointSelection( controller, new CxModelElement( vertex ), true );
				break;		
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		// allow the parent function to perform highlights
		super.processMouseMovement( controller, mousePos );
		
		// get the current space position
		currentPos = controller.untransform( mousePos );
	}
	
	/** 
	 * Creates a new arc
	 * @param controller the given {@link ApplicationController controller}
	 */
	private void createArc( final ApplicationController controller ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the selected elements
		final RenderableElement[] elements = new RenderableElement[3];
		model.getSelectedGeometry( elements );
		
		// get the creation points		
		final PointXY p1	= EntityRepresentationUtil.getPoint( elements[0] );
		final PointXY p2 	= EntityRepresentationUtil.getPoint( elements[1] );
		final PointXY p3	= EntityRepresentationUtil.getPoint( elements[2] );
		
		// create the arc
		final ModelElement arc = new CxModelElement( ArcXY.createArcThru3Points( p1, p2, p3 ) );
		dialog.exportSettings( arc );
		
		// add the arc to the model
		model.addPhysicalElement( arc );
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickedPt the given clicked {@link ModelElement point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handlePointSelection( final ApplicationController controller, 
									   final ModelElement clickedPt, 
									   final boolean addToModel ) {
		if( clickedPt != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
						
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( clickedPt );
			}
			
			// select the clicked point
			model.selectGeometry( clickedPt );
			
			// if there are 3 points
			if( model.getSelectedElementCount() >= 3 ) {
				createArc( controller );
			}
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Arc 3-Points Function Parameter Plug-in
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyPlugInAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new plug-in action listener
		 * @param controller the given {@link ApplicationController controller}
		 */
		public MyPlugInAction( final ApplicationController controller ) {
			this.controller = controller;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			createArc( controller );
		}
	}
	
	/** 
	 * Arc: 3-Points Pop-up Menu
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super( "Arc: 3-Points" );
			super.add( new CxMenuItem( "New point", NEW_PT_ICON, new NewPointAction( controller ) ) );
		}
	}
	
	/** 
	 * "New Point" Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class NewPointAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a "New Point" action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public NewPointAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			handlePointSelection( controller, new CxModelElement( currentPos ), true );
		}
	}
		
}