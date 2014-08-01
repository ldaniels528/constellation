package constellation.app.functions.point;

import constellation.ApplicationController;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentation;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.VerticesXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.math.MatrixWCStoSCS;

/**
 * The POINT::LIMITS function
 * @author lawrence.daniels@gmail.com
 */
public class PointLimitsFunction extends AbstractPointFunction {
	private static final Steps STEPS = new Steps(
		"Select a #line or #curve"
	);
	
	/**
	 * Default constructor
	 */
	public PointLimitsFunction() {
		super( 
			"LIMITS", 
			"images/commands/point/point-limits.gif", 
			"docs/functions/point/limits.html", 
			STEPS 
		); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		createLimitPoints( controller, element );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {		
		// lookup all complex elements within the click boundary
		final ModelElement element = 
			ElementDetectionUtil.lookupComplexElementByRegion( controller, mouseClick );
		
		// if an element was retrieved, select it
		// NOTE: the "call-back" listener may pick it up
		createLimitPoints( controller, element );
	}
	
	/**
	 * Creates the limit points of the given geometry
	 * @param controller the given {@link ApplicationController controller}
	 * @param element the given {@link ModelElement element}
	 */
	private void createLimitPoints( final ApplicationController controller,  
									final ModelElement element ) {
		if( element != null ) {
			int pointsCreated = 0;
			
			// get the transformation matrix
			final MatrixWCStoSCS matrix = controller.getMatrix();
			
			// get the internal entity
			final EntityRepresentation entity = element.getRepresentation();
				
			// handle the entity by type
			switch( entity.getType() ) {
				// non-geometric entity?
				case COMPOSITION:;
				case PICTURE:;
				case TEXTNOTE:
					final RectangleXY bounds = element.getBounds( matrix );
					final PointXY mp = bounds.getMidPoint();
					addPointToModel( controller, mp );
					pointsCreated = 1;
					break;
					
				case LINE:
					final LineXY line = EntityRepresentationUtil.getLine( element );
					final VerticesXY limits1 = line.getLimits();
					addPointsToModel( controller, limits1.explode() );
					pointsCreated = limits1.length();
					break;
					
				default:
					// is it a curve?
					if( entity instanceof CurveXY ) {
						final CurveXY curve = (CurveXY)entity;
						final VerticesXY limits = curve.getLimits();
						addPointsToModel( controller, limits.explode() );
						pointsCreated = limits.length();
					}		
			}
			
			// if points were created ...
			if( pointsCreated == 0 ) {
				controller.setStatusMessage( "No points found" );
			}
		}
	}
	
}