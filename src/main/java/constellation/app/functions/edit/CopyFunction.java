package constellation.app.functions.edit;

import static constellation.app.math.ElementDetectionUtil.lookupPointByRegion;
import static constellation.functions.MouseClick.BUTTON_INDICATE;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.Steps.STEP_1;
import static constellation.functions.Steps.STEP_2;
import static constellation.functions.Steps.STEP_3;

import java.awt.Point;

import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;

/**
 * The EDIT::COPY function
 * @author lawrence.daniels@gmail.com
 */
public class CopyFunction extends StructuredSelectionFunction  {
	private static final Steps STEPS = new Steps(
		"Select the source #point of the copy vector",
		"Select the destination #point of the copy vector",
		"Select an #element to copy or Indicate to quit"
	);
	private PointXY sourcePt;
	private PointXY destinationPt;
	
	/**
	 * Default constructor
	 */
	public CopyFunction() {
		super( 
			"EDIT", "COPY", 
			"images/commands/edit/copy.png", 
			"docs/functions/edit/copy.html", 
			STEPS 
		);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.PickListObserver#elementSelected(constellation.ApplicationController, constellation.model.ModelElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		switch( steps.currentIndex() ) {
			case STEP_1:;
			case STEP_2: handleClickedPoint( controller, element, false ); break;
			case STEP_3: handleSelectedElement( controller, element ); break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick  ) {
		// if Step #1 or #2
		switch( steps.currentIndex() ) {
			case STEP_1:;
			case STEP_2:
				switch( mouseClick.getButton() ) {
					case BUTTON_SELECT:
						// determine the selected point
						final ModelElement pickedPt = 
							lookupPointByRegion( controller, mouseClick );
						
						// handle the "selected" point
						handleClickedPoint( controller, pickedPt, false );
						break;
						
					case BUTTON_INDICATE:
						// get the click point
						final PointXY point = controller.untransform( mouseClick );
						
						// handle the "indicated" point
						handleClickedPoint( controller, new CxModelElement( point ), true );
						break;
				}
				break;
				
			case STEP_3:
				// determine the selected element
				final ModelElement element = 
					lookupPointByRegion( controller, mouseClick );
				
				// handle the "selected" element
				handleSelectedElement( controller, element );
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#processMouseMovement(constellation.functions.ApplicationController, int, int)
	 */
	public void processMouseMovement( final ApplicationController controller, final Point mousePos ) {
		if( steps.currentIndex() >= STEP_2 ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// set the temporary elements
			model.setTemporaryElement( createTemporaryGeometry() );
		}
	}
	
	/**
	 * Handles the given click point
	 * @param controller the given {@link ApplicationController controller}
	 * @param pointElem the given {@link ModelElement point element}
	 * @param addToModel indicates whether the point {@link ModelElement element} should be added to the model
	 */
	private void handleClickedPoint( final ApplicationController controller, 
								   	 final ModelElement pointElem, 
								   	 final boolean addToModel ) {
		if( pointElem != null ) {
			// handle each step
			switch( steps.currentIndex() ) {
				case STEP_1: 
					sourcePt = EntityRepresentationUtil.getPoint( pointElem );
					break;
					
				case STEP_2: 
					destinationPt = EntityRepresentationUtil.getPoint( pointElem );
					break;
			}
			
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// add the element to the model?
			if( addToModel ) {
				model.addPhysicalElement( pointElem );
			}
			
			// select the point element
			model.selectGeometry( pointElem );
			
			// set the temporary geometry
			model.setTemporaryElement( createTemporaryGeometry() );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	private RenderableElement createTemporaryGeometry() {
		// get the current step #
		final int stepNo = steps.currentIndex();
		
		final HUDXY hud = new HUDXY();
		
		switch( stepNo) {
			default:;
			
			case STEP_2:
				hud.add( new TextNoteXY( destinationPt, "B" ) );
				// purposeful fall-through
				
			case STEP_1:
				hud.add( new TextNoteXY( sourcePt, "A" ) );
				break;
		}
		
		return hud;
	}
	
	/**
	 * Handles the given element
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement point element}
	 */
	private void handleSelectedElement( final ApplicationController controller, 
										final ModelElement element ) {
		if( element != null ) {
			// compute the offset
			final double dx = sourcePt.getX() - destinationPt.getX();
			final double dy = sourcePt.getY() - destinationPt.getY();
			
			// move the element
			final EntityRepresentation representation = element.getRepresentation();
			representation.duplicate( dx, dy );
		}
	}

	
}