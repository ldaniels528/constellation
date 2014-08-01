package constellation.app.functions.shape;

import static java.lang.Math.PI;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;
import static constellation.functions.Steps.STEP_4;
import static constellation.math.CxMathUtil.convertRadiansToDegrees;
import static java.lang.String.format;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
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
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.menu.CxMenuItem;

/** 
 * The SHAPE::PIE function
 * @author lawrence.daniels@gmail.com
 */
public class PieFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select or Indicate the center #point of the arc",
		"Select or Indicate the end #point or Key the radius of the arc",
		"Select or Indicate the start #point or Key the start #angle of the arc",
		"Select or Indicate the end #point or Key the end #angle of the arc"
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// function fields
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	
	// internal fields
	private PointXY centerPoint;
	private PointXY endPoint;
	private PointXY anglePointA;
	private PointXY anglePointB;
	private double angleA;
	private double angleB;
	
	/**
	 * Default constructor
	 */
	public PieFunction() {
		super( 
			"SHAPE", "PIE", 
			"images/commands/shape/pie.png", 
			"docs/functions/shape/pie.html", 
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
		
		// create the parameters instance
		if( parameters == null ) {
			parameters = new MyParameters();
		}
		
		// create the plug-in instance
		if( plugIn == null ) {
			plugIn = new FunctionDialogPlugIn( parameters, new MyPlugInAction( controller ) );
		}
		
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
				
		// if we're at (or beyond) Step #2 
		if( steps.currentIndex() >= STEP_2 ) {
			// create the planar point
			final PointXY clickPoint = controller.untransform( mousePos );
			
			// at Step 3?
			switch( steps.currentIndex() ) {
				case STEP_2: 
					endPoint = clickPoint; 
					parameters.setRadius( PointXY.getDistance( centerPoint, endPoint ) );
					break;
					
				case STEP_3: 
					anglePointA = getPointOnCircle( clickPoint ); 
					angleA = PointXY.getAngle( centerPoint, anglePointA );
					parameters.setAngleA( angleA );
					break;
					
				case STEP_4: 
					anglePointB = getPointOnCircle( clickPoint ); 
					angleB = PointXY.getAngle( centerPoint, anglePointB );
					parameters.setAngleB( angleB );
					break;
			}
			
			// create the temporary geometry
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( createTemporaryArcByRadius( centerPoint, endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
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
				case ARC:
					// get the arc's representation
					final ArcXY arc = EntityRepresentationUtil.getCircularArc( element );
					
					// remove the current element
					final GeometricModel model = controller.getModel();
					model.erase( element );
						
					// get the center point of the arc
					handleClickPoint( controller, arc.getMidPoint() );
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
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// handle each step
		switch( steps.currentIndex() ) {
			// select the center-point
			case STEP_1: 
	 			centerPoint = clickPoint;
	 			model.selectGeometry( clickPoint );
	 			parameters.setCoordinates( clickPoint );
	 			break;
	 			
	 		// select the radius
			case STEP_2:
				endPoint = clickPoint;
				model.selectGeometry( clickPoint );
				parameters.setRadius( PointXY.getDistance( centerPoint, endPoint ) );
				break;
				
			// select angle point A
			case STEP_3:
				anglePointA = getPointOnCircle( clickPoint );
				model.selectGeometry( anglePointA );
				angleA = PointXY.getAngle( centerPoint, anglePointA );
				parameters.setAngleA( angleA );
				break;
	 			
			case STEP_4:
				createPie( controller );
	 			break;
		}
	
 		// advance to the next step
		advanceToNextStep( controller );
	}
	
	/** 
	 * Creates a new circular pie
	 * @param controller the given {@link ApplicationController controller}
	 */
	private void createPie( final ApplicationController controller ) {
		// recompute the angles
		computeAngles();
		logger.info( format( "angleA = %3.2f, angleB = %3.2f", angleA, angleB ) );
		
		// create the arc
		final ArcXY arc = new ArcXY( 
				centerPoint.getX(), 
				centerPoint.getY(), 
				PointXY.getDistance( centerPoint, endPoint ),
				angleB, angleA + 2d * PI
			);
		
		// create line A
		final LineXY lineA = new LineXY( centerPoint, anglePointA );
		
		// create line B
		final LineXY lineB = new LineXY( centerPoint, anglePointB );
		
		// create the composition
		final CompositionXY comp = new CompositionXY();
		comp.addAll( arc, lineA, lineB );
		
		// create the wrapping element
		final ModelElement element = new CxModelElement( comp );
		
		// update the arc's attributes
		dialog.exportSettings( element );
		
		// add the arc to the model
		final GeometricModel model = controller.getModel();
		model.addPhysicalElement( element );
		
		// set the status message
		controller.setStatusMessage( format( "Created pie '%s'", element ) );
	}
	
	/**
	 * Computes the start and end angles
	 */
	private void computeAngles() {
		// get the current step number
		final int stepNo = steps.currentIndex();
		
		// compute angle A?
		if( stepNo >= STEP_3 ) {
			angleA = PointXY.getAngle( centerPoint, anglePointA );
		}
		
		// compute angle B?
		if( stepNo >= STEP_4 ) {
			angleB = PointXY.getAngle( centerPoint, anglePointB );
		}
	}
	
	/**
	 * Creates a temporary circle (by radius) using the given start and end points
	 * @param cp the center point of the circle
	 * @param ep the end point of the circle
	 * @return a new {@link RenderableElement rendering element}
	 */
	private RenderableElement createTemporaryArcByRadius( final PointXY cp, final PointXY ep ) {
		// get the current step number
		final int stepNo = steps.currentIndex();
		
		// compute all of the necessary values
		computeAngles();
		
		// create a container for all of the geometry
		final CompositionXY composition = new CompositionXY();
		
		////////////////////////////////////////////////////////////////////
		// Create the circle/arc indicating the radius of the arc
		// NOTE: Steps #1 through #4
		if( stepNo >= STEP_2 ) {
			// create the circle
			final CircleXY circle = CircleXY.createCircleByRadius( cp, ep );
			
			// get the distance between the points
			final double radius = PointXY.getDistance( cp, ep );
			
			// create a note with the radius
			final TextNoteXY noteR = new TextNoteXY( cp, format( "r = %3.2f", radius ) );
			
			// finally, add the circle, radius note, and center-point to composition
			composition.addAll( circle, noteR, cp );
		}
		
		if( stepNo >= STEP_4 ) {
			// get the distance between the points
			final double radius = PointXY.getDistance( cp, ep );
			
			// create a note with the radius
			final TextNoteXY noteR = new TextNoteXY( cp, format( "r = %3.2f", radius ) );
			
			// finally, add the circle, radius note, and center-point to composition
			composition.addAll( noteR, cp );
		}
		
		////////////////////////////////////////////////////////////////////
		// Create the line indicating the radius length
		// NOTE: Step #2 only
		if( stepNo == STEP_2 ) {
			// create the line from the center-point through the end-point 
			final LineXY line = new LineXY( cp, ep );
					
			// add the line & note to composition
			composition.addAll( line, ep );
		}
		
		////////////////////////////////////////////////////////////////////
		// Create line describing the starting angle
		// NOTE: Steps #3 and #4
		if( stepNo >= STEP_3 ) {
			// create the line from the center-point through the end-point 
			final LineXY lineA	= new LineXY( cp, anglePointA );
			
			// create a note with the angle
			final TextNoteXY noteA = 
				new TextNoteXY( lineA.getMidPoint(), format( "A = %3.2f°", convertRadiansToDegrees( angleA ) ) );
			
			// add the line & note to composition
			composition.addAll( lineA, noteA, anglePointA );
		}
		
		////////////////////////////////////////////////////////////////////
		// Create line describing the ending angle
		// NOTE: Step #4 only
		if( stepNo >= STEP_4 ) {
			// create the line from the center-point through the end-point 
			final LineXY lineB = new LineXY( cp, anglePointB );
			
			// create a note with the angle
			final TextNoteXY noteB = 
				new TextNoteXY( lineB.getMidPoint(), format( "B = %3.2f°", convertRadiansToDegrees( angleB ) ) );
			
			// add the line & note to composition
			composition.addAll( lineB, noteB, anglePointB );
		}
		
		// return the composition
		return composition;
	}
	
	/** 
	 * Projects the given point onto the circle, and returns the new projected point.
	 * @param p the given {@link PointXY point} in two-dimensional space
	 * @return the new projected {@link PointXY point}
	 */
	private PointXY getPointOnCircle( final PointXY p ) {
		// create the circle
		final CircleXY circle = CircleXY.createCircleByRadius( centerPoint, endPoint );
		
		// return the projected point
		return PointXY.getPointOnCircle( circle, p );
	}
	
	/**
	 * Arc 3-Points Function Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		private final CxDecimalField xF;
		private final CxDecimalField yF;
		private final CxDecimalField angleAF;
		private final CxDecimalField angleBF;
		private final CxDecimalField radiusF;
		
		/**
		 * Default Constructor
		 */
		public MyParameters() {
			// row #1
			int row = -1;
			super.attach( 0, ++row, new JLabel( "X-axis:")  );
			super.attach( 1,   row, xF = new CxDecimalField() );
			
			// row #2
			super.attach( 0, ++row, new JLabel( "Y-axis:")  );
			super.attach( 1,   row, yF = new CxDecimalField() );
			
			// row #3
			super.attach( 0, ++row, new JLabel( "Radius:")  );
			super.attach( 1,   row, radiusF = new CxDecimalField( 1.0d ) );
			
			// row #4
			super.attach( 0, ++row, new JLabel( "Start angle:")  );
			super.attach( 1,   row, angleAF = new CxDecimalField() );
			
			// row #5
			super.attach( 0, ++row, new JLabel( "End angle:")  );
			super.attach( 1,   row, angleBF = new CxDecimalField() );
		}
		
		/** 
		 * Sets the coordinates of the given point within the dialog
		 * @param vertex the given {@link PointXY vertex}
		 */
		public void setCoordinates( final PointXY vertex ) {
			// set the coordinates
			xF.setDecimal( vertex.getX() );
			yF.setDecimal( vertex.getY() );
		}
		
		/**
		 * Returns arc angle "A"
		 * @return the given angle in radians
		 */
		public Double getAngleA() {
			return angleAF.getDecimal();
		}
		
		/**
		 * Sets arc angle "A"
		 * @param angle the given angle in radians
		 */
		public void setAngleA( final double angle ) {
			angleAF.setDecimal( angle );
		}
		
		/**
		 * Returns arc angle "B"
		 * @return the given angle in radians
		 */
		public Double getAngleB() {
			return angleBF.getDecimal();
		}
		
		/**
		 * Sets arc angle "B"
		 * @param angle the given angle in radians
		 */
		public void setAngleB( final double angle ) {
			angleBF.setDecimal( angle );
		}
		
		/**
		 * Returns the circle/arc radius
		 * @return the circle/arc radius
		 */
		public Double getRadius() {
			return radiusF.getDecimal();
		}
		
		/** 
		 * Sets the circle/arc radius
		 * @param radius the circle/arc radius
		 */
		public void setRadius( final double radius ) {
			radiusF.setDecimal( radius );
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
			createPie( controller );
		}
	}
	
	/** 
	 * Arc: Radius Pop-up Menu
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super( "Arc: Radius" );
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