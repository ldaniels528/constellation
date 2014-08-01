package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;
import static constellation.math.CxMathUtil.convertDegreesToRadians;
import static java.lang.String.format;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.DimensionXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;

/**
 * The LINE::ANGLE function
 * @author lawrence.daniels@gmail.com
 */
public class LineAngleFunction extends StructuredSelectionFunction {
	// define the steps
	private static final Steps STEPS = new Steps(
		"Select the host #line",
		"Select or Indicate the start #point",
		"Key the angle or Select or Indicate the end #point" 
	);
	
	// internal fields
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;
	private PointXY startPt;
	private LineXY hostLine;
	private Double lastAngle;
	private Double lastLength;
	
	/**
	 * Default constructor
	 */
	public LineAngleFunction() {
		super( 
			"LINE", "ANGLE", 
			"images/commands/line/line-angled.gif", 
			"docs/functions/line/angle.html", 
			STEPS 
		);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1_SelectHostLine( controller, element ); break;
			case STEP_2: handleStep2_SelectStartPoint( controller, element, false ); break;
			case STEP_3: handleStep3_SelectEndPoint( controller, element, false ); break;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FunctionDialogPlugIn getParameterPlugin() {
		return plugIn;
	}

	/**
	 * {@inheritDoc}
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
		dialog.resetIdentity( EntityCategoryTypes.LINEAR ); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: handleStep1( controller, mouseClick ); break;
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
			case Steps.STEP_3: handleStep3( controller, mouseClick ); break;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean processMouseDrag( final ApplicationController controller, 
									 final MouseClick oldMousePos, 
									 final MouseClick newMousePos ) {
		// only for Step #3
		if( parameters.isComplete() ) {
			handleStep3_HovingEndPoint( controller, newMousePos );
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		super.processMouseMovement( controller, mousePos );
		
		// get the current angle & length
		final Double angle = parameters.getAngle();
		final Double length = parameters.getLength();
		
		if( parameters.isComplete() && ( ( angle != lastAngle ) || ( length != lastLength ) ) ) {
			// record the angle
			lastAngle = angle;
			lastLength = length;
			
			// get the position in space
			final PointXY vertex = controller.untransform( mousePos );
			
			// create the angled line
			final LineXY angledLine = createAngleLine( controller, vertex );
			if( angledLine != null ) {
				final GeometricModel model = controller.getModel();
				model.setTemporaryElement( createTemporaryLine( angledLine ) );
				controller.requestRedraw();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsSelection() {
		return true;
	}

	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement line = ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
				handleStep1_SelectHostLine( controller, line );
				
				// remember this line
				hostLine = EntityRepresentationUtil.getLine( line );
				break;
		}
	}
	
	/**
	 * Handles the "picked" line
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the given {@link ModelElement line}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep1_SelectHostLine( final ApplicationController controller, final ModelElement line ) {
		if( line != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the line
			model.selectGeometry( line );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep2_SelectStartPoint( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// get the new point
				final PointXY newPoint = controller.untransform( mouseClick );
				handleStep2_SelectStartPoint( controller, new CxModelElement( newPoint ), true );
				break;
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link ModelElement click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep2_SelectStartPoint( final ApplicationController controller, 
											   final ModelElement clickPoint, 
											   final boolean addToModel ) {
		if( clickPoint != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( clickPoint );
			}
			
			// select the point
			model.selectGeometry( clickPoint );
			
			// remember this point
			startPt = EntityRepresentationUtil.getPoint( clickPoint );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	private void handleStep3( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep3_SelectEndPoint( controller, pickedPt, false );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// get the new point
				final PointXY newPoint = controller.untransform( mouseClick );
				handleStep3_SelectEndPoint( controller, new CxModelElement( newPoint ), true );
				break;
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param mousePos the given {@link Point mouse position}
	 */
	private void handleStep3_HovingEndPoint( final ApplicationController controller, final Point mousePos ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the space coordinates of the mouse position
		final PointXY hoverPt = controller.untransform( mousePos );
		
		// get the selected elements
		final RenderableElement[] elements = new RenderableElement[2];
		model.getSelectedGeometry( elements );
		
		// get the host line
		final LineXY hostLine 	= EntityRepresentationUtil.getLine( elements[0] );
		final PointXY startPt 	= EntityRepresentationUtil.getPoint( elements[1] );
		final double length		= PointXY.getDistance( startPt, hoverPt );
		final double angle		= parameters.getAngle();
		
		// convert the angle to radians
		double angle_radians = convertDegreesToRadians( angle );
		
		// flip the direction of the angle?
		if( hostLine.isAbove( hoverPt ) && ( angle_radians < 0 ) ) {
			angle_radians = -angle_radians;
		}
		else if(  hostLine.isBelow( hoverPt ) && ( angle_radians > 0 ) ) {
			angle_radians = -angle_radians;
		}
			
		// set the temporary geometry
		model.setTemporaryElement( createTemporaryLine( hostLine, startPt, angle_radians, length ) );
		
		// request a re-draw
		controller.requestRedraw();	
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param clickPoint the given {@link ModelElement click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep3_SelectEndPoint( final ApplicationController controller, 
											 final ModelElement clickPoint, 
											 final boolean addToModel ) {
		if( clickPoint != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( clickPoint );
			}
			
			// get the selected elements
			final RenderableElement[] elements = new RenderableElement[2];
			model.getSelectedGeometry( elements );
			
			// get the host line, start point and end point
			final LineXY hostLine 	= EntityRepresentationUtil.getLine( elements[0] );
			final PointXY startPt 	= EntityRepresentationUtil.getPoint( elements[1] );		
			final PointXY endPt		= EntityRepresentationUtil.getPoint( clickPoint );
			
			// if the selected elements are not null
			if( ( hostLine != null ) && ( startPt != null ) ) {
				final double length	= PointXY.getDistance( startPt, endPt );
				final double angle	= parameters.getAngle();
				
				// set the 'length' parameter
				parameters.setLength( length );
				
				// compute the angle in radians
				final double angle_radians = convertDegreesToRadians( angle );
				
				// create the angle line definition
				final LineXY angledLine = createAngledLine( hostLine, startPt, angle_radians, length );
				
				// add the angled line
			addLineToModel( controller, angledLine );
			}
		}
	}
	
	/** 
	 * Adds the given line to the model
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the given {@link LineXY line}
	 */
	private void addLineToModel( final ApplicationController controller,
								 final LineXY line ) {
		// create an element wrapper for the line
		final ModelElement element = new CxModelElement( line );
		
		// update the lines attributes
		dialog.exportAttributes( element );
		
		// add the element to the model
		final GeometricModel model = controller.getModel();
		model.addPhysicalElement( element );
		
		// restart the function
		onStart( controller );
	}
	
	private LineXY createAngleLine( final ApplicationController controller, final PointXY referencePt ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the selected elements
		final RenderableElement[] selected = new RenderableElement[2];
		model.getSelectedGeometry( selected );
		
		// get the host line
		final LineXY hostLine 	= EntityRepresentationUtil.getLine( selected[0] );
		final PointXY startPt 	= EntityRepresentationUtil.getPoint( selected[1] );
		
		// if the selected elements are not null
		if( ( hostLine != null ) && ( startPt != null ) ) {
			final double length		= parameters.getLength();
			final double angle		= parameters.getAngle();
			
			// convert the angle to radians
			double angle_radians = convertDegreesToRadians( angle );
			
			// flip the direction of the angle?
			if( hostLine.isAbove( referencePt ) && ( angle_radians < 0 ) ) {
				angle_radians = -angle_radians;
			}
			else if( hostLine.isBelow( referencePt ) && ( angle_radians > 0 ) ) {
				angle_radians = -angle_radians;
			}
				
			return createAngledLine( hostLine, startPt, angle_radians, length );
		}
		return null;
	}
	
	/** 
	 * Creates a new line having the specified length and angle
	 * @param hostLine the given {@link LineXY host line}
	 * @param startPt the given {@link PointXY starting point}
	 * @param angle_radians the given angle (in radians)
	 * @param length the given length of the line
	 * @return the {@link LineXY angled line}
	 */
	private LineXY createAngledLine( final LineXY hostLine, 
									 final PointXY startPt,
									 final double angle_radians,
									 final double length ) {
		// create the angle line definition
		final LineXY newLine = LineXY.createAngledLine( hostLine, startPt, angle_radians, length );
		
		// create the angled line
		return parameters.isInfinite() ? LineXY.createInfiniteLine( newLine ) : newLine;
	}
	
	/** 
	 * Creates a new temporary line having the specified length and angle
	 * @param hostLine the given {@link LineXY host line}
	 * @param startPt the given {@link PointXY starting point}
	 * @param angle_radians the given angle (in radians)
	 * @param length the given length of the line
	 * @return the {@link LineXY angled line}
	 */
	private RenderableElement createTemporaryLine( final LineXY hostLine, 
												   final PointXY startPt,
												   final double angle_radians,
												   final double length ) {
		// create the temporary line
		final LineXY line = this.createAngledLine( hostLine, startPt, angle_radians, length );
		
		// create the length note
		return createTemporaryLine( line );
	}
	
	/** 
	 * Creates a new temporary line having the specified length and angle
	 * @param hostLine the given {@link LineXY host line}
	 * @param startPt the given {@link PointXY starting point}
	 * @param angle_radians the given angle (in radians)
	 * @param length the given length of the line
	 * @return the {@link RenderableElement temporary elements}
	 */
	private RenderableElement createTemporaryLine( final LineXY line ) {	
		// get the end points of the line
		final PointXY p1 = line.getBeginPoint();
		final PointXY p2 = line.getEndPoint();
		final PointXY mp = line.getMidPoint();
		
		// add the line, points, length note and dimension
		final HUDXY hud = new HUDXY( 
			line, 
			p1, p2, 
			new TextNoteXY( mp, format( "L=%3.2f", line.length() ) ), 
			DimensionXY.createAngularDimension( line, hostLine ) 
		);
		
		// return the HUD
		return hud;
	}
	
	/**
	 * Line: Angle Function Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		private final JCheckBox infiniteBox;
		private final CxDecimalField angleF;
		private final CxDecimalField lengthF;
		
		/**
		 * Default Constructor
		 */
		public MyParameters() {
			// row #1
			int row = -1;
			super.attach( 0, ++row, new JLabel( "Length:")  );
			super.attach( 1,   row, lengthF = new CxDecimalField() );
			
			// row #2
			super.attach( 0, ++row, new JLabel( "Angle:")  );
			super.attach( 1,   row, angleF = new CxDecimalField( 0d ) );
			
			// row #3
			super.attach( 0, ++row, new JLabel( "Infinite Line" ) );
			super.attach( 1,   row, infiniteBox = new JCheckBox( "Yes" ), GridBagConstraints.WEST );
		}
		
		/**
		 * Indicates whether the dialog is complete enough to generate geometry
		 * @return true, if all fields are filled.
		 */
		public boolean isComplete() {
			return ( getAngle() != null ) && 
					( getLength() != null );
		}
		
		/** 
		 * Returns the angle
		 * @return the angle
		 */
		public Double getAngle() {
			return angleF.getDecimal();
		}
		
		/** 
		 * Returns the length
		 * @return the length
		 */
		public Double getLength() {
			return lengthF.getDecimal();
		}
		
		/** 
		 * Sets the length parameter
		 * @param length the given length
		 */
		public void setLength( final Double length ) {
			lengthF.setDecimal( length );
		}
		
		/**
		 * Indicates whether the line to be created is an infinite line
		 * @return true, if the line to be created is an infinite line 
		 */
		public boolean isInfinite() {
			return infiniteBox.isSelected();
		}
	}
	
	/**
	 * Line: Angle Function Parameter Plug-in
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
		
		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			final GeometricModel model = controller.getModel();
			if( model.getSelectedElementCount() >= 1 ) {
				// get the host line
				final RenderableElement[] selected = new RenderableElement[1];
				controller.getModel().getSelectedGeometry( selected );
				final LineXY hostLine = EntityRepresentationUtil.getLine( selected[0] );
				
				// get the line's parameters
				final Double angle		= parameters.getAngle();
				final Double length 	= parameters.getLength();
				
				// convert the angle to radians
				final double angle_radians = convertDegreesToRadians( angle );
				
				// create the angled line
				final LineXY line = createAngledLine( hostLine, startPt, angle_radians, length );
				
				// add the line to the model
				addLineToModel( controller, line );
			}
		}
	}
	
}