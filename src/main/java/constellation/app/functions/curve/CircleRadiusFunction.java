package constellation.app.functions.curve;

import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

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
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.menu.CxMenuItem;

/** 
 * The CIRCLE::RADIUS function
 * @author lawrence.daniels@gmail.com
 */
public class CircleRadiusFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select #curve or the center #point of the circle",
		"Drag to shrink/enlarge or Select the outer #point of the circle"
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// internal fields
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	private PointXY startPoint;
	private PointXY endPoint;
	
	/**
	 * Default constructor
	 */
	public CircleRadiusFunction() {
		super( 
			"CIRCLE", "RADIUS", 
			"images/commands/curve/circle-radius.png", 
			"docs/functions/curve/circle_radius.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handleSelectElement( controller, element );
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
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event first
		super.onStart( controller );
		
		// initialize the editor dialog
		dialog.resetIdentity( EntityCategoryTypes.CURVE );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {		
		// determine which geometry was click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement element = 
					ElementDetectionUtil.lookupGeometricElementByRegion( controller, mouseClick );
				
				// handle the selection of the point
				handleSelectElement( controller, element );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// determine the indicated point
				final PointXY vertex = controller.untransform( mouseClick );
				
				// add the point to the model
				final GeometricModel model = controller.getModel();
				model.addPhysicalElement( vertex );
				
				// handle the click
				handleClickPoint( controller, vertex );
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
		
		// if we're at Step #2 
		if( steps.currentIndex() == STEP_2 ) {
			// capture the new end point
			endPoint = currentPos;
			
			// create the temporary geometry
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( createTemporaryCircleByRadius( startPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}

	/**
	 * Creates the circle, and adds it to the model
	 * @param controller the given {@link ApplicationController controller}
	 */
	private void createCircle( final ApplicationController controller ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// add the element to the model
		model.addPhysicalElement( CircleXY.createCircleByRadius( startPoint, endPoint ) );
	}
	
	/**
	 * Handle the element selection
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleSelectElement( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			// process based on the entity type
			switch( element.getType() ) {
				case CIRCLE:
					// get the circle's representation
					final CircleXY circle = EntityRepresentationUtil.getCircle( element );
					
					// remove the current element
					final GeometricModel model = controller.getModel();
					model.erase( element );
						
					// get the center point of the curve
					handleClickPoint( controller, circle.getMidPoint() );
					break;
						
				case POINT:
					handleClickPoint( controller, EntityRepresentationUtil.getPoint( element ) );
					break;
			}
		}
	}
	
	/** 
	 * Handle the click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link PointXY click point}
	 */
	private void handleClickPoint( final ApplicationController controller, final PointXY clickPoint ) {
		// select the point
		final GeometricModel model = controller.getModel();
		model.selectGeometry( clickPoint );
		
		// handle the step
		switch( steps.currentIndex() ) {
			case STEP_1: 
				// set the anchor point
	 			startPoint = clickPoint;	 			
	 			break;
	 			
			case STEP_2:
				endPoint = clickPoint;
				
				// create the circle
				createCircle( controller );
	 			break;
		}
	
 		// advance to the next step
		advanceToNextStep( controller );;
	}
	
	/**
	 * Creates a temporary circle (by radius) using the given start and end points
	 * @param centerPoint the center point of the circle
	 * @param endPoint the end point of the circle
	 * @return a new {@link RenderableElement rendering element}
	 */
	private static RenderableElement createTemporaryCircleByRadius( final PointXY centerPoint, final PointXY endPoint ) {
		// create the circle
		final CircleXY circle = CircleXY.createCircleByRadius( centerPoint, endPoint );
		
		// create the line from the center-point through the end-point 
		final LineXY line = new LineXY( centerPoint, endPoint );
		
		// get the distance between the points
		final double distance = PointXY.getDistance( centerPoint, endPoint );
		
		// create a note with the distance
		final TextNoteXY note = new TextNoteXY( centerPoint, format( "r = %3.2f", distance ) );
		
		// attach the circle, line & note to the HUD
		final HUDXY composition = new HUDXY( circle, line, note, endPoint );
		composition.addAll( circle.getLimits().explode() );
		return composition;
	}
	
	/** 
	 * Circle: Radius Pop-up Menu
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super( "Circle: Radius" );
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
			handleClickPoint( controller, currentPos );
		}
	}
	
}
