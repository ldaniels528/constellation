package constellation.app.functions.curve;

import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;

import java.awt.GridBagConstraints;
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
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;
import constellation.ui.components.menu.CxMenuItem;

/** 
 * The CURVE::PARALLEL function
 * @author lawrence.daniels@gmail.com
 */
public class CurveParallelFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select the host #curve",
		"Select or Indicate offset #point to create the parallel curve"
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// internal fields
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	private CurveXY curve;
	private PointXY endPoint;
	
	/**
	 * Default constructor
	 */
	public CurveParallelFunction() {
		super( 
			"CURVE", "PARALLEL", 
			"images/commands/curve/curve-parallel.png", 
			"docs/functions/curve/curve_parallel.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1_CurveSelection( controller, element ); break;
			case STEP_2: handleStep2_ClickPoint( controller, element, true ); break;
		}
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
	 * @see constellation.functions.Function#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow the parent to update
		super.onStart( controller );
		
		// create the parameters instance
		if( parameters == null ) {
			parameters = new MyParameters();
		}
		
		// create the plug-in instance
		if( plugIn == null ) {
			plugIn = new FunctionDialogPlugIn( parameters, new MyPlugInAction( controller ) );
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
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: handleStep1( controller, mouseClick ); break;
			case Steps.STEP_2: handleStep2( controller, mouseClick ); break;
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
		
		// Step 2: if the curve is selected, perform dynamic offset
		if( steps.currentIndex() == Steps.STEP_2 ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// create the planar point
			endPoint = controller.untransform( mousePos );
			
			// get the selected elements
			final RenderableElement[] elements = new RenderableElement[1];
			model.getSelectedGeometry( elements );
			
			// get the selected curve
			final CurveXY curve = 
				EntityRepresentationUtil.getCurve( elements[0] );
			
			// compute the offset distance
			final double offset = PointXY.getDistance( curve.getMidPoint(), endPoint );
			parameters.setOffset( offset );
			
			// manipulate temporary geometry
			model.setTemporaryElement( curve.getParallelCurve( endPoint ) );
			
			// request a redraw
			controller.requestRedraw();
		}
	}
	
	/** 
	 * Creates a new parallel curve
	 * @param controller the given {@link ApplicationController controller}
	 */
	private void createParallelCurve( final ApplicationController controller ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// create the parallel curve
		final CurveXY offsetCurve = curve.getParallelCurve( endPoint );
		
		// create a new element
		final ModelElement curveElem = new CxModelElement( offsetCurve );
		
		// update the appearance
		dialog.exportSettings( curveElem );
		
		// add the curve to the model
		model.addPhysicalElement( curveElem );
	
		// set the status message
		controller.setStatusMessage( format( "Created parallel curve '%s'", curveElem ) );
	}
	
	/**
	 * Handles STEP 1: Select a curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a curve
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement curve = ElementDetectionUtil.lookupCurveByRegion( controller, mouseClick );
				handleStep1_CurveSelection( controller, curve );
				break;	
		}
	}
	
	/**
	 * Handles the selection of a curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param curveElem the given {@link ModelElement curve}
	 */
	private void handleStep1_CurveSelection( final ApplicationController controller, final ModelElement curveElem ) {
		if( curveElem != null && 
				( curve = EntityRepresentationUtil.getCurve( curveElem ) ) != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the curve
			model.selectGeometry( curveElem );
			
			// notify the user
			controller.setStatusMessage( format( "Selected %s '%s'", 
					EntityRepresentationUtil.getTypeName( curveElem.getType() ), curveElem.getLabel() ) );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}

	/**
	 * Handles STEP 2: Select or Indicate an offset point
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the click point
		final PointXY vertex = controller.untransform( mouseClick );
		
		// handle the mouse click
		switch( mouseClick.getButton() ) {
			// selected a curve
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement pickedPt = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handleStep2_ClickPoint( controller, pickedPt, true );
				break;
				
			// indicated a point
			case BUTTON_INDICATE:
				// handle the click
				handleStep2_ClickPoint( controller, new CxModelElement( vertex ), true );
				break;		
		}
	}
	
	/** 
	 * STEP 2: Handle the selected or indicated point
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement click point}
	 * @param addToModel indicates whether the point should be added to the model
	 */
	private void handleStep2_ClickPoint( final ApplicationController controller, 
								   		 final ModelElement point, 
								   		 final boolean addToModel ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the point to the model?
			if( addToModel ) {
				model.addPhysicalElement( point );
			}
			
			// capture the end point
			endPoint = (PointXY)point.getRepresentation();
			
			// set the offset
			final double offset = PointXY.getDistance( curve.getMidPoint(), endPoint );
			parameters.setOffset( offset );
			
			// create the parallel curve
 			createParallelCurve( controller );
			
	 		// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/**
	 * Curve Parallel Function Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		private final CxDecimalField offsetF;
		
		/**
		 * Default Constructor
		 */
		public MyParameters() {			
			// row #1
			int row = -1;
			super.attach( 0, ++row, new JLabel( "Offset:" )  );
			super.attach( 1,   row, offsetF = new CxDecimalField(), GridBagConstraints.WEST );
		}
		
		/**
		 * Returns the curve offset
		 * @return the curve offset
		 */
		public Double getOffset() {
			return offsetF.getDecimal();
		}
		
		/** 
		 * Sets the curve offset
		 * @param offset the curve offset
		 */
		public void setOffset( final double offset ) {
			offsetF.setDecimal( offset );
		}
	}
	
	/**
	 * Curve Parallel Function Parameter Plug-in
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
			// get the circle's offset
			final Double offset = parameters.getOffset();
			
			// if the offset is defined ...
			if( ( controller.getModel().getSelectedElementCount() > 0 ) && ( offset != null ) ) {
				// create the circle
				createParallelCurve( controller );
				
				// restart the function
				onStart( controller );
			}
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
			if( currentPos != null ) {
				// create a point element
				final ModelElement element = new CxModelElement( currentPos );
			
				// handle the point based on the current step
				switch( steps.currentIndex() ) {
					case STEP_1: handleStep1_CurveSelection( controller, element ); break;
					case STEP_2: handleStep2_ClickPoint( controller, element, true ); break;
				}
			}
		}
	}
	
}
