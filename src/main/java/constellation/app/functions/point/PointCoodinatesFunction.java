package constellation.app.functions.point;

import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import constellation.ApplicationController;
import constellation.drawing.entities.PointXY;
import constellation.functions.FunctionDialogPlugIn;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;

/**
 * The POINT::COORDS function
 * @author lawrence.daniels@gmail.com
 */
public class PointCoodinatesFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Key the coordinates or Indicate to create a #point"
	);
	
	// internal fields
	private FunctionDialogPlugIn plugIn;
	private MyParameters parameters;

	/**
	 * Default constructor
	 */
	public PointCoodinatesFunction() {
		super( 
			"COORDS", 
			"images/commands/point/point.png", 
			"docs/functions/point/coords.html", 
			STEPS 
		);
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
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {	
		// if it's a primary click
		if( mouseClick.getButton() == BUTTON_INDICATE ) {
			// get the click point in 2D Space
			final PointXY vertex = controller.untransform( mouseClick );
			
			// set the coordinates
			parameters.setCoordinates( vertex );
			
			// get the model instance
			addPointToModel( controller, vertex );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see constellation.commands.StructuredSelectionFunction#supportsSelection()
	 */
	@Override
	public boolean supportsSelection() {
		return false;
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
			// create a new vertex
			final PointXY vertex = parameters.getCoordinates();
			
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
		private final CxDecimalField xF;
		private final CxDecimalField yF;
		
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
			super.attach( 1,   row, yF = new CxDecimalField(), GridBagConstraints.WEST );
		}
		
		/**
		 * Returns the coordinates
		 * @return the coordinates
		 */
		public PointXY getCoordinates() {
			final Double x = xF.getDecimal();
			final Double y = yF.getDecimal();
			return new PointXY( x, y );
		}
		
		/** 
		 * Sets the coordinates of the given point within the dialog
		 * @param vertex the given {@link PointXY vertex}
		 */
		public void setCoordinates( final PointXY vertex ) {
			xF.setDecimal( vertex.getX() );
			yF.setDecimal( vertex.getY() );
		}
	}

}