package constellation.app.functions.point;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;

/**
 * The POINT::OFFSET function
 * @author lawrence.daniels@gmail.com
 */
public class PointOffsetFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Select the host #point",
		"Key the offset values"
	);
	
	// internal fields
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;
	
	/**
	 * Default constructor
	 */
	public PointOffsetFunction() {
		super( 
			"OFFSET", 
			"images/commands/point/point-offset.gif", 
			"docs/functions/point/offset.html", 
			STEPS 
		);
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.functions.ApplicationController, constellation.drawing.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		if( element != null ) {
			switch( steps.currentIndex() ) {
				case STEP_1: handleStep1_SelectPoint( controller, element ); break;
				case STEP_2: break;
			}
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
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
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
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Function#process(constellation.FunctionController, constellation.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		switch( steps.currentIndex() ) {
			case STEP_1: handleStep1( controller, mouseClick ); break;
			case STEP_2: handleStep2( controller, mouseClick ); break;
		}
	}
	
	/**
	 * Handles step #1: Select the host point
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep1( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a Select
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// determine the selected point
			final ModelElement point = 
				ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
			
			// handle the clicked point
			handleStep1_SelectPoint( controller, point );
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleStep1_SelectPoint( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the point
			model.selectGeometry( point );
			
			// advance to the next step
			advanceToNextStep( controller );	
		}
	}
	
	/**
	 * Handles step #2: Enter the offset
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void handleStep2( final ApplicationController controller, final MouseClick mouseClick ) {
		// TODO figure out how to accept input
	}	
	
	/**
	 * Point Coordinates Function Parameter Plug-in
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
			// retrieved the select elements
			final RenderableElement[] selected = new RenderableElement[1];
			final GeometricModel model = controller.getModel();
			model.getSelectedGeometry( selected );
			
			// get the select point
			final PointXY selectedPt = EntityRepresentationUtil.getPoint( selected[0] );
			
			// get the DX value
			final Double dx = parameters.getDeltaX();
			if( dx == null ) {
				controller.showErrorDialog( "Required value DX is missing", "Error" );
			}
			
			// get the DY value
			final Double dy = parameters.getDeltaY();
			if( dy == null ) {
				controller.showErrorDialog( "Required value DY is missing", "Error" );
			}
			
			// create the new offset vertex
			final PointXY vertex = new PointXY( selectedPt.x + dx, selectedPt.y + dy );
			
			// add the vertex to the model
			addPointToModel( controller, vertex );
		}
	}
	
	/**
	 * Point Coordinates Function Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		private final CxDecimalField dxF;
		private final CxDecimalField dyF;
		
		/**
		 * Default Constructor
		 */
		public MyParameters() {
			// row #1
			int row = -1;
			super.attach( 0, ++row, new JLabel( "DX:")  );
			super.attach( 1,   row, dxF = new CxDecimalField() );
			
			// row #2
			super.attach( 0, ++row, new JLabel( "DY:")  );
			super.attach( 1,   row, dyF = new CxDecimalField(), GridBagConstraints.WEST );
		}
		
		/** 
		 * Returns the delta-X value
		 * @return the delta-X value
		 */
		public Double getDeltaX() {
			return dxF.getDecimal();
		}
		
		/** 
		 * Returns the delta-Y value
		 * @return the delta-Y value
		 */
		public Double getDeltaY() {
			return dyF.getDecimal();
		}
	}
	
} 