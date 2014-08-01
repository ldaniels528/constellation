package constellation.app.functions.line;

import static constellation.functions.MouseClick.BUTTON_SELECT;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityCategoryTypes;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.CxIntersectionUtil;
import constellation.model.GeometricModel;
import constellation.ui.components.CxPanel;
import constellation.ui.components.fields.CxDecimalField;

/**
 * The LINE::JOIN function
 * @author lawrence.daniels@gmail.com
 */
public class LineJoinFunction extends StructuredSelectionFunction {
	private static enum JoinType { BEVEL, MITER, ROUND };
	private static final Steps STEPS = new Steps(
		"Select the first #line to join",
		"Select the second #line to join",
		"Select the solution"
	);
	
	// internal fields
	private final MyParameters parameters;
	
	/**
	 * Default constructor
	 */
	public LineJoinFunction() {
		super( 
			"LINE", "JOIN", 
			"images/commands/line/line-fillet.png", 
			"docs/functions/line/fillet.html", 
			STEPS
		);
		this.parameters = new MyParameters();
	}	
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handleStep_LineSelection( controller, element );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.AbstractFunction#onStart(constellation.functions.ApplicationController)
	 */
	public void onStart( final ApplicationController controller ) {
		// allow parent to initialize
		super.onStart( controller );
		
		// initialize the editor dialog
		dialog.resetIdentity( EntityCategoryTypes.LINEAR );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// if it's a 'Select' click
		if( mouseClick.getButton() == BUTTON_SELECT ) {
			// determine the line to select
			final ModelElement line = 
				ElementDetectionUtil.lookupLineByRegion( controller, mouseClick );
			
			// handle the line
			handleStep_LineSelection( controller, line );
		}
	}
	
	/**
	 * Select the Line for joining
	 * @param controller the given {@link ApplicationController controller}
	 * @param line the given {@link ModelElement line}
	 */
	private void handleStep_LineSelection( final ApplicationController controller, final ModelElement line ) {
		if( line != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// select the line
			model.selectGeometry( line );
			
			// if two lines have been selected, perform the join
			if( model.getSelectedElementCount() >= 2 ) {
				performJoin( controller, model );
			}
		}
	}
	
	/** 
	 * Performs the actual join between the lines
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel model}
	 */
	private void performJoin( final ApplicationController controller, 
							  final GeometricModel model ) {
		// get the selected elements
		final ModelElement[] lines = new ModelElement[2];
		model.getSelectedGeometry( lines );
		
		// get the individual lines
		final LineXY lineA = EntityRepresentationUtil.getLine( lines[0] );
		final LineXY lineB = EntityRepresentationUtil.getLine( lines[1] );
		
		// get the join type
		final JoinType joinType = parameters.getJoinType();
		
		// lines must intersect
		final PointXY ip = CxIntersectionUtil.getIntersectionPoint( lineA, lineB );
		if( ip == null ) {
			controller.setStatusMessage( "Lines do not intersect" );
			return;
		}
		
		// is it an orthogonal join?
		if( lineA.isOrthogonal() && lineA.isOrthogonal() ) {
			// compute the Pythagorean Theorem: c = sqrt(a^2 + b^2)
			final double a = lineA.length();
			final double b = lineB.length();
			final double c = sqrt( pow( a, 2 ) + pow( b, 2 ) );
			
			// TODO figure out how to do an orthogonal join
			controller.setStatusMessage( format( "a = %3.2f, b = %3.2f, c = %3.2f", a, b, c ) );
		}

		// is only one of the lines orthogonal?
		else if( lineA.isOrthogonal() || lineA.isOrthogonal() ) {
			// TODO figure out how to do an semi-orthogonal join
			
			controller.setStatusMessage( format( "lineA %s orthoganol, and lineB %s orthoganol", 
					lineA.isOrthogonal() ? "is" : "is not",
					lineB.isOrthogonal() ? "is" : "is not" ) );
		}
		
		// both lines are non-orthogonal
		else {
			// get the slopes of the lines
			final double a = lineA.getSlope();
			final double b = lineB.getSlope();
			
			// get the radius
			final double r = parameters.getValue();
			
			// compute the distance vectors
			// dv_a = sqrt(1+a^2)
			// dv_b = sqrt(1+b^2) 
			double dv_a = sqrt(1.0d+a*a);
			double dv_b = sqrt(1.0d+b*b);

			// create the solutions
	        for( int i = 0; i < 2; i++ ) {
	            for( int j = 0; j < 2; j++ ) {
	    	        // compute the distance from intersection point
	    			//	x = r * ( sqrt(1+a^2) - sqrt(1+b^2) ) / (a-b)
	    			//  y = a * x - r * sqrt(1+a^2)
	            	final double x = r * ( dv_a - dv_b ) / (a - b);
	    	        final double y = a * x - r * dv_a;
	            	
	            	model.addPhysicalElement( new CxModelElement( new CircleXY( ip.x + x, ip.y + y, r ) ) );
	            	dv_a = -dv_a;
	            }
	            dv_b = -dv_b;
	        }
	        
			// advance to the next step
			advanceToNextStep( controller );
		}
        
        // request a redraw
        controller.requestRedraw();
	}
	
	/**
	 * Line Join Parameters
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyParameters extends CxPanel {
		// dialog icon declarations
		private final CxContentManager contentManager = CxContentManager.getInstance();
		private final Icon BEVEL_ICON 	= contentManager.getIcon( "images/dialog/line/JoinBevel24.png" );
		private final Icon MITER_ICON	= contentManager.getIcon( "images/dialog/line/JoinMiter24.png" );
		private final Icon ROUND_ICON 	= contentManager.getIcon( "images/dialog/line/JoinRound24.png" );
		
		// internal fields
		private CxDecimalField valueF;
		private JRadioButton bevelBox;
		private JRadioButton miterBox;
		private JRadioButton roundBox;
		
		/**
		 * Default Constructor
		 */
		public MyParameters() {
			super();
			
			// create the "radius" panel
			final CxPanel rp = new CxPanel();
			rp.attach( 0, 0, new JLabel( "Radius:" ) );
			rp.attach( 1, 0, valueF = new CxDecimalField( 1.0d ) );
			
			// put it all together
			int row = -1;
			super.gbc.gridwidth = 2;
			super.attach( 0, ++row, new JLabel( "Join Type:" ) );
			super.gbc.gridwidth = 1;
			super.attach( 0, ++row, bevelBox = new JRadioButton( "Bevel" ) );
			super.attach( 1,   row, new JLabel( BEVEL_ICON ) );
			super.attach( 0, ++row, miterBox = new JRadioButton( "Miter" ) );
			super.attach( 1,   row, new JLabel( MITER_ICON ) );
			super.attach( 0, ++row, roundBox = new JRadioButton( "Round", true ) );
			super.attach( 1,   row, new JLabel( ROUND_ICON ), GridBagConstraints.WEST );
			super.gbc.gridwidth = 2;
			super.attach( 0, ++row, rp );
			super.gbc.gridwidth = 1;
			
			// create the radio button group
			final ButtonGroup group = new ButtonGroup();
			group.add( bevelBox );
			group.add( miterBox );
			group.add( roundBox );
		}
		
		/**
		 * Returns the join type
		 * @return the {@link JoinType join type}
		 */
		public JoinType getJoinType() {
			// is it Beveled?
			if( bevelBox.isSelected() ) {
				return JoinType.BEVEL;
			}
			
			// is it Mitered?
			else if( miterBox.isSelected() ) {
				return JoinType.MITER;
			}
			
			// is it Round?
			else if( roundBox.isSelected() ) {
				return JoinType.ROUND;
			}
			
			// unknown
			else {
				throw new IllegalStateException( "No join type was selected" );
			}
		}
		
		/**
		 * Returns the radius value
		 * @return the {@link Double value}
		 */
		public Double getValue() {
			return valueF.getDecimal();
		}
		
	}
	
}