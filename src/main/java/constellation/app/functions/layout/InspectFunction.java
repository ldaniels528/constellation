package constellation.app.functions.layout;

import static constellation.drawing.EntityRepresentationUtil.getTypeNameCap;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.math.CxMathUtil.convertRadiansToDegrees;
import static java.lang.Math.PI;
import static java.lang.String.format;
import constellation.ApplicationController;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.RenderableElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.HUDXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.SpiralXY;
import constellation.drawing.entities.SplineXY;
import constellation.drawing.entities.TextNoteXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.model.Unit;

/**
 * The LAYOUT::INSPECT function
 * @author lawrence.daniels@gmail.com
 */
public class InspectFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps( 
			"Select a #point, #line, or #curve"
		);
	
	/**
	 * Default constructor
	 */
	public InspectFunction() {
		super( 
			"LAYOUT", "INSPECT", 
			"images/commands/layout/inspect.png", 
			"docs/functions/layout/inspect.html", 
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
	 * @see constellation.app.functions.AbstractFunction#processMouseClick(constellation.ApplicationController, constellation.functions.MouseClick)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// determine which geometry was click
		switch( mouseClick.getButton() ) {
			// selected a point
			case BUTTON_SELECT:
				// determine the selected point
				final ModelElement element = 
					ElementDetectionUtil.lookupElementByRegion( controller, mouseClick );
				
				// handle the selection of the point
				handleSelectElement( controller, element );
				break;
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

			// get the unit of measurement
			final Unit unit = model.getUnit();
			
			// create a temporary element reference
			RenderableElement temporaryElement = null;
			
			// process based on the entity type
			switch( element.getType() ) {
				case ARC:		temporaryElement = analyzeArc( element, unit ); break;
				case CIRCLE:	temporaryElement = analyzeCircle( element, unit ); break;
				case ELLIPSE:	temporaryElement = analyzeEllipse( element, unit ); break;
				case LINE:		temporaryElement = analyzeLine( element, unit ); break;			
				case POINT:		temporaryElement = analyzePoint( element ); break;
				case SPIRAL:	temporaryElement = analyzeSpiral( element, unit ); break;
				case SPLINE:	temporaryElement = analyzeSpline( element, unit ); break;
			}
			
			// set the temporary element
			model.setTemporaryElement( temporaryElement );
		}
	}
	
	/**
	 * Performs the analysis on an arc
	 * @param element the given {@link ModelElement arc element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeArc( final ModelElement element, final Unit unit ) {
		// create a HUD
		final HUDXY hud = new HUDXY();
		
		// add the arc entity
		final ArcXY arc = EntityRepresentationUtil.getCircularArc( element );
		final PointXY mp = arc.getMidPoint();
		
		hud.addAll( arc, mp );
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		hud.append( format( "Area: %3.2f %s^2", arc.getArea(), unit.getShortName() ) );
		hud.append( format( "Length: %3.4f %s", arc.length(), unit.getShortName() ) );	
		hud.append( format( "Radius: %3.4f %s", arc.getRadius(), unit.getShortName() ) );
		hud.append( format( "Angle A: %3.2fº", convertRadiansToDegrees( arc.getAngleStart() ) ) );
		hud.append( format( "Angle B: %3.2fº", convertRadiansToDegrees( arc.getAngleEnd() ) ) );
		hud.appendSeparator();
			
		// add the radius & circumference
		hud.addAll( mp, new TextNoteXY( mp, "P1" ) );
		hud.append( format( "P1: %s", mp ) );
		
		// create a note with the angle A
		final double angleA = arc.getAngleStart();
		final PointXY anglePointA = arc.getArcEndPoint( angleA );
		final LineXY lineA = new LineXY( mp, anglePointA );
		hud.addAll( lineA, new TextNoteXY( lineA.getMidPoint(), "A" ) );
		hud.addAll( anglePointA, new TextNoteXY( anglePointA, "P2" ) );
		hud.append( format( "P2: %s", anglePointA ) );
		
		// create a note with the angle B
		final double angleB = arc.getAngleEnd();
		final PointXY anglePointB = arc.getArcEndPoint( angleB );
		final LineXY lineB = new LineXY( mp, anglePointB );
		hud.addAll( lineB, new TextNoteXY( lineB.getMidPoint(), "B" ) );
		hud.addAll( anglePointB, new TextNoteXY( anglePointB, "P3" ) );
		hud.append( format( "P3: %s", anglePointB ) );
		
		return hud;
	}
	
	/**
	 * Performs the analysis on a circle
	 * @param element the given {@link ModelElement circle element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeCircle( final ModelElement element, final Unit unit ) {
		// create a HUD
		final HUDXY hud = new HUDXY();
		
		// add the circle entity & midpoint
		final CircleXY circle = EntityRepresentationUtil.getCircle( element );
		final PointXY mp = circle.getMidPoint();
		hud.addAll( circle, mp );
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		hud.append( format( "Area: %3.2f %s^2", circle.getArea(), unit.getShortName() ) );
		hud.append( format( "Length: %3.4f %s", circle.length(), unit.getShortName() ) );	
		hud.append( format( "Radius: %3.4f %s", circle.getRadius(), unit.getShortName() ) );
		hud.appendSeparator();
		
		// add the radius line & note
		final LineXY lineR = new LineXY( mp, PointXY.getPointOnCircle( circle, PI*0.75 ) );
		final PointXY ep = lineR.getEndPoint();
		final TextNoteXY noteR1 = new TextNoteXY( mp, "P1" );
		final TextNoteXY noteR2 = new TextNoteXY( ep, "P2" );
		
		hud.addAll( lineR, noteR1, noteR2, ep );
		hud.append( format( "P1: %s", mp ) );
		hud.append( format( "P2: %s", ep ) );
	
		// return the HUD
		return hud;
	}
	
	/**
	 * Performs the analysis on a ellipse
	 * @param element the given {@link ModelElement ellipse element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeEllipse( final ModelElement element, final Unit unit ) {
		// create a HUD
		final HUDXY hud = new HUDXY();
		
		// add the ellipse entity & midpoint
		final EllipseXY ellipse = EntityRepresentationUtil.getEllipse( element );
		final PointXY mp = ellipse.getMidPoint();
		
		hud.addAll( ellipse, mp, new TextNoteXY( mp, "P1" ) );
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		hud.append( format( "Area: %3.2f %s^2", ellipse.getArea(), unit.getShortName() ) );
		hud.append( format( "Length: %3.4f %s", ellipse.length(), unit.getShortName() ) );
		hud.append( format( "Width:  %3.4f %s", ellipse.getWidth(), unit.getShortName() ) );
		hud.append( format( "Height: %3.4f %s", ellipse.getHeight(), unit.getShortName() ) );
		hud.appendSeparator();
		
		// add the midpoint
		hud.append( format( "P1: %s",  mp ) );
		
		// add the line A & note
		final LineXY lineA = new LineXY( mp, PointXY.getPointOnEllipse( ellipse, PI/2d ) );
		final PointXY pAe = lineA.getEndPoint(); 
		hud.addAll( lineA, pAe, new TextNoteXY( pAe, "P2" ) );
		hud.append( format( "P2: %s",  pAe ) );
		
		// add the line B & note
		final LineXY lineB = new LineXY( mp, PointXY.getPointOnEllipse( ellipse, PI ) );
		final PointXY pBe = lineB.getEndPoint(); 
		hud.addAll( lineB, pBe, new TextNoteXY( pBe, "P3" ) );
		hud.append( format( "P3: %s",  pBe ) );
		
		// return the HUD
		return hud;
	}
	
	/**
	 * Performs the analysis on a line
	 * @param element the given {@link ModelElement line element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeLine( final ModelElement element, final Unit unit ) {
		// get the line entity
		final LineXY line = EntityRepresentationUtil.getLine( element );
		
		// create a HUD
		final HUDXY hud = new HUDXY();
		hud.add( line );
		
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		if( !line.isInfinite() ) {
			hud.append( format( "Length: %3.4f %s", line.length(), unit.getShortName() ) );	
		}
		else {
			hud.append( "Length: Infinite" );
		}
		hud.append( format( "Angle: %3.2fº", convertRadiansToDegrees( line.getAngle() ) ) );	
		hud.appendSeparator();
		
		// if it's not an infinite line
		if( !line.isInfinite() ) {
			// add end points
			final PointXY p1 = line.getBeginPoint();
			hud.addAll( p1, new TextNoteXY( p1, "P1" ) );
			hud.append( format( "P1: %s", p1 ) );
			
			// add end points
			final PointXY p2 = line.getEndPoint();
			hud.addAll( p2, new TextNoteXY( p2, "P2" ) );
			hud.append( format( "P2: %s", p2 ) );
		}
		
		return hud;
	}
	
	/**
	 * Performs the analysis on a point
	 * @param element the given {@link ModelElement point element}
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzePoint( final ModelElement element ) {
		// get the point entity
		final PointXY point = EntityRepresentationUtil.getPoint( element );
		
		// create a HUD
		final HUDXY hud = new HUDXY();
		
		// add end points
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		hud.addAll( point, new TextNoteXY( point, "P1" ) );
		hud.append( format( "P1: %s", point ) );
		return hud;
	}
	
	/**
	 * Performs the analysis on a spiral
	 * @param element the given {@link ModelElement spiral element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeSpiral( final ModelElement element, final Unit unit ) {
		// create a HUD
		final HUDXY hud = new HUDXY();

		// add the spiral entity
		final SpiralXY spiral = EntityRepresentationUtil.getSpiral( element );
		hud.add( spiral );
		
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		hud.append( format( "Radius: %3.4f %s", spiral.getRadius(), unit.getShortName() ) );
		hud.append( format( "Length: %3.4f %s", spiral.length(), unit.getShortName() ) );
		hud.append( format( "Increment: %3.4f", spiral.length() ) );
		hud.append( format( "Revolutions: %d", spiral.getRevolutions() ) );
		hud.append( format( "Direction: %s", spiral.isClockWise() ? "CW" : "CCW" ) );
		hud.appendSeparator();
		
		// add point labels 'P1' and 'P2'
		final PointXY p1 = spiral.getMidPoint();
		final PointXY p2 = spiral.getEndPoint();
		hud.addAll( p1, new TextNoteXY( p1, "P1" ) );
		hud.addAll( p2, new TextNoteXY( p2, "P2" ) );
		
		// append the text to the log
		hud.append( format( "P1: %s", p1 ) );
		hud.append( format( "P2: %s", p2 ) );
		return hud;
	}
	
	/**
	 * Performs the analysis on a spline
	 * @param element the given {@link ModelElement spline element}
	 * @param unit the given {@link Unit unit} of measurement
	 * @return the {@link RenderableElement graphical representation} of the analysis
	 */
	private RenderableElement analyzeSpline( final ModelElement element, final Unit unit ) {
		// create a HUD
		final HUDXY hud = new HUDXY();

		// add the spline entity
		final SplineXY spline = EntityRepresentationUtil.getSpline( element );
		hud.add( spline );
		
		hud.append( format( "Type: %s", getTypeNameCap( element.getType() ) ) );
		hud.append( format( "Label: %s", element.getLabel() ) );
		hud.append( format( "Layer: %03d", element.getLayer() + 1 ) );
		hud.appendSeparator();
		
		hud.append( format( "Length: %3.4f %s", spline.length(), unit.getShortName() ) );
		hud.appendSeparator();
		
		// add the limit points
		final PointXY[] limits = spline.getLimits().explode();

		int n = 0;
		for( final PointXY p : limits ) {
			hud.addAll( p, new TextNoteXY( p, format( "P%d", ++n ) ) );
			hud.append( format( "P%d: %s", n, p ) );
		}
		
		return hud;
	}

}