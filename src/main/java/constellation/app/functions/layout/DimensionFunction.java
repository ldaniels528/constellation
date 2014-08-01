package constellation.app.functions.layout;

import static constellation.drawing.DefaultDrawingColors.DIMENSION_COLOR;
import static constellation.drawing.EntityRepresentationUtil.getTypeName;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.DimensionXY;
import constellation.drawing.entities.LineXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The LAYOUT::DIMENSION Function
 * @author lawrence.daniels@gmail.com
 */
public class DimensionFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Select a #line",
		"Select a second #line"
		//"Select or Indicate the starting #point of the dimension"
	);
	private ModelElement elementA;
	private ModelElement elementB;

	/** 
	 * Default constructor
	 */
	public DimensionFunction() {
		super( 
			"LAYOUT", "DIMENSION", 
			"images/commands/layout/dimension.png", 
			"docs/functions/layout/dimension.html", 
			STEPS 
		);
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	public void elementSelected( final ApplicationController controller, 
								 final ModelElement element ) {
		if( element != null ) {
			handleElementSelection( controller, element ); 	
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseClick(constellation.functions.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, 
								   final MouseClick mouseClick ) {
		// handle the current step
		switch( steps.currentIndex() ) {
			case Steps.STEP_1: pickEntityA( controller, mouseClick ); break;
			case Steps.STEP_2: pickEntityB( controller, mouseClick ); break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void handleElementSelection( final ApplicationController controller, 
										 final ModelElement element ) {
		if( element != null ) {
			// record the entity based on the current step
			switch( steps.currentIndex() ) {
				case STEP_1: 
					elementA = element; 
					break;	
					
				case STEP_2: 
					elementB = element; 
					break;
			}
			
			// perform the entity selection
			final GeometricModel model = controller.getModel();			
			model.selectGeometry( element );
			controller.setStatusMessage( format( "Selected %s '%s'", getTypeName( element.getType() ), element.getLabel() ) );
			
			// handle the logic based on the selected entity
			if( elementA.getType() == EntityTypes.CIRCLE || 
					steps.currentIndex() == STEP_2 ) {
				createDimension( controller );
			}
			else {
				advanceToNextStep( controller );
			}
		}
	}

	/**
	 * Step 1: Pick entity A: point, line or curve
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void pickEntityA( final ApplicationController controller, 
							  final MouseClick mouseClick ) {		
		// handle the 'Select' mouse click
		if( mouseClick.getButton() == BUTTON_SELECT ) { 
			// lookup the elements
			final ModelElement element = 
				ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
			
			// handle the selection of the element
			handleElementSelection( controller, element ); 
		}
	}
	
	/**
	 * Step 2: Pick entity A: point or line
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 */
	private void pickEntityB( final ApplicationController controller, 
							  final MouseClick mouseClick ) {
		// handle the 'Select' mouse click
		if( mouseClick.getButton() == BUTTON_SELECT ) { 
			// lookup the elements
			final ModelElement element = 
				ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
			
			// handle the selection of the element
			handleElementSelection( controller, element ); 
		}
	}
	
	/**
	 * Creates the dimension
	 * @param controller the given {@link ApplicationController controller}
	 */
	private void createDimension( final ApplicationController controller  ) {
		DimensionXY dimension = null;

		// handle the elements
		switch( elementA.getType() ) {
			// is entity "A" a circle?
			case CIRCLE:
				final CircleXY circle = EntityRepresentationUtil.getCircle( elementA );
				dimension = DimensionXY.createRadiusDimension( circle );
				break;
				
			// is entity "A" a line?
			case LINE: 
				final LineXY lineA = EntityRepresentationUtil.getLine( elementA );
				switch( elementB.getType() ) {
					// is entity "B" a line?
					case LINE: 
						final LineXY lineB = EntityRepresentationUtil.getLine( elementB );
						dimension = lineA.isParallelTo( lineB ) 
								? DimensionXY.createLinearDimension( lineA, lineB )
								: DimensionXY.createAngularDimension( lineA, lineB );
						break;
						
				}
				break;	
		}
		
		// was the dimension created?
		if( dimension != null ) {
			// create the dimension element
			final ModelElement dimElem = new CxModelElement( dimension );
			dimElem.setColor( DIMENSION_COLOR );
			
			// add the dimension to the model
			final GeometricModel model = controller.getModel();	
			model.addPhysicalElement( dimElem );
			
			// restart the function
			onStart( controller );
		}
		else {
			controller.setStatusMessage( format( "The dimension could not be created between '%s' and '%s'", elementA, elementB ) );
		}
	}

}