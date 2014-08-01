package constellation.app.functions.curve;

import static java.lang.String.format;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.SpiralXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.fields.CxIntegerField;

/** 
 * This function is responsible for generating spiral geometry.
 * @author lawrence.daniels@gmail.com
 */
public class SpiralFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the center #point of the spiral",
		"Select the outer #point of the spiral or Key the radius"
	);
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;
	private PointXY anchorPoint;
	
	/**
	 * Default constructor
	 */
	public SpiralFunction() {
		super( 
			"CURVE", "SPIRAL", 
			"images/commands/curve/spiral.png", 
			"docs/functions/curve/spiral.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
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
	 * @see constellation.functions.StructuredSelectionFunction#onStart(constellation.functions.ApplicationController)
	 */
	@Override
	public void onStart( final ApplicationController controller ) {
		// allow the parent to handle the event
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
				// determine the click point
				final PointXY vertex = controller.untransform( mouseClick );
				
				// create the clicked point
				final ModelElement point = new CxModelElement( vertex );
				
				// add the point to the model
				final GeometricModel model = controller.getModel();
				model.addPhysicalElement( point );
				
				// handle the click
				handleClickPoint( controller, point );
				advanceToNextStep( controller );
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
		
		// if we're at Step #2
		if( steps.currentIndex() == Steps.STEP_2 ) {
			// capture the end point
			final PointXY point = controller.untransform( mousePos );

			// create the temporary geometry
			final GeometricModel model = controller.getModel();
			model.setTemporaryElement( createSpiral( anchorPoint, point ) );
			
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
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// process based on the entity type
			switch( element.getType() ) {
				case SPIRAL:
					// remove the current curve
					final SpiralXY spiral = EntityRepresentationUtil.getSpiral( element );
					model.erase( element );
						
					// get the center point of the curve
					final ModelElement point = new CxModelElement( spiral.getMidPoint() );
					handleClickPoint( controller, point );
					advanceToNextStep( controller );
					break;
					
				case POINT:
					handleClickPoint( controller, element );
					advanceToNextStep( controller );
					break;
			}
		}
	}
	
	/** 
	 * Handle the click point
	 * @param model the given {@link GeometricModel model}
	 * @param pickedPt the given {@link ModelElement click point}
	 */
	private void handleClickPoint( final ApplicationController controller, final ModelElement clickPoint ){
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the temporary geometry
		final RenderableElement temporaryGeometry = model.getTemporaryElement();
		
		// capture the end point
		final PointXY point = EntityRepresentationUtil.getPoint( clickPoint );
		
		// has a line been started?
 		if( temporaryGeometry == null ) { 
 			// set the anchor point
 			anchorPoint = point;
 			
 			// set the coordinates
 			parameters.setCoordinates( point );
 			
 			// create a temporary circle
			model.setTemporaryElement( createSpiral( anchorPoint, point ) );
		}
 		
 		// otherwise ...
 		else {
 			// add the spiral
 			addSpiralToModel( controller, createSpiral( anchorPoint, point ) );
 		}
	}
	
	/** 
	 * Adds the spiral to the model 
	 * @param controller the given {@link ApplicationController controller}
	 * @param spiral the given {@link SpiralXY spiral}
	 */
	private void addSpiralToModel( final ApplicationController controller, 
								   final SpiralXY spiral ) {
		// create a new element using the spiral
		final ModelElement element = new CxModelElement( spiral );
		
		// update the element with the base attributes
		dialog.exportAttributes( element );
		
		// add the element to the model
		final GeometricModel model = controller.getModel();
		model.addPhysicalElement( element );
		
		// notify the user
		controller.setStatusMessage( format( "Created spiral '%s' at %s", element.getLabel(), spiral.getMidPoint() ) );
		
		// restart the function
		onStart( controller );
	}
	
	/** 
	 * Creates a new spiral
	 * @param startPt the given start {@link PointXY point}
	 * @param endPt the given end {@link PointXY point}
	 * @return the {@link SpiralXY spiral}
	 */
	private SpiralXY createSpiral( final PointXY startPt, final PointXY endPt ) {
		final double radius 	= parameters.getRadius();
		final double increment 	= parameters.getIncrement();
		final int revolutions 	= parameters.getRevolutions();
		return new SpiralXY( startPt, radius, increment, revolutions );
	}
	
	/**
	 * Spiral Function Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		private final CxDecimalField xF;
		private final CxDecimalField yF;
		private final CxDecimalField radiusF;
		private final CxDecimalField incrementF;
		private final CxIntegerField revolutionsF;
		
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
			super.attach( 1,   row, radiusF = new CxDecimalField( 10.0d ) );
			
			// row #4
			super.attach( 0, ++row, new JLabel( "Increment:")  );
			super.attach( 1,   row, incrementF = new CxDecimalField( 0.1d ) );
			
			// row #5
			super.attach( 0, ++row, new JLabel( "Revolutions:")  );
			super.attach( 1,   row, revolutionsF = new CxIntegerField( 5 ), GridBagConstraints.NORTHWEST );
		}
		
		public SpiralXY createSpiral() {
			if( isComplete() ) {
				final PointXY p 			= getCoordinates();
				final Double radius 		= getRadius();
				final Double increment 		= getIncrement();
				final Integer revolutions 	= getRevolutions();
				return new SpiralXY(p, radius, increment, revolutions );
			}
			else {
				return null;
			}
		}
		
		/**
		 * Indicates whether the parameters are complete enough to create a spiral
		 * @return true, if the parameters are complete enough to create a spiral
		 */
		public boolean isComplete() {
			return getCoordinates() != null &&
					getRadius() != null && 
					getIncrement() != null &&
					getRevolutions() != null;
		}
		
		/** 
		 * Returns the coordinates of the given point within the dialog 
		 * @return the {@link PointXY coordinates}
		 */
		public PointXY getCoordinates() {
			final Double x = xF.getDecimal();
			final Double y = yF.getDecimal();
			return ( x != null & y != null ) ? new PointXY( x, y ) : null;
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
		
		/**
		 * Returns the spiral increment value
		 * @return the spiral increment value
		 */
		public Double getIncrement() {
			return incrementF.getDecimal();
		}
		
		/**
		 * Sets the spiral increment value
		 * @param increment the spiral increment value
		 */
		public void setIncrement( final double increment ) {
			incrementF.setDecimal( increment );
		}
		
		/**
		 * Returns the number of spiral revolutions
		 * @return the number of spiral revolutions
		 */
		public Integer getRevolutions() {
			return revolutionsF.getInteger();
		}
		
		/** 
		 * Sets the number of spiral revolutions
		 * @param revolutions the number of spiral revolutions
		 */
		public void setRevolutions( final int revolutions ) {
			revolutionsF.setInteger( revolutions );
		}
	}
	
	/**
	 * Circle: Radius Function Parameter Plug-in
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
			final SpiralXY spiral = parameters.createSpiral();
			if( spiral != null ) {
				addSpiralToModel( controller, spiral );
			}
			else {
				controller.showErrorDialog( "Required elements are missing", "Input Error" );
			}
		}
	}
	
}
