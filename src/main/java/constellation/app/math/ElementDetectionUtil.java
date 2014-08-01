package constellation.app.math;

import static constellation.drawing.EntityTypes.*;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constellation.ApplicationController;
import constellation.app.functions.PickListDialog;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.EntityTypes;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.RectangleXY;
import constellation.functions.MouseClick;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/**
 * This utility class offer many collision detection methods
 * for use with geometry selection.
 * @author lawrence.daniels@gmail.com
 */
public class ElementDetectionUtil {
	// define the curve types
	private static final Set<EntityTypes> CURVE_TYPES =
		new HashSet<EntityTypes>( asList( 
			CIRCLE, ARC, ELLIPSE, ELLIPTIC_ARC,
			SPIRAL, SPLINE
	) );
	
	private static final Set<EntityTypes> GEOMETRY_TYPES = 
		new HashSet<EntityTypes>( asList( 
			POINT, LINE, CIRCLE, ARC, 
			ELLIPSE, ELLIPTIC_ARC, SPIRAL, SPLINE
		) );
	
	private static final Set<EntityTypes> COMPLEX_TYPES = 
		new HashSet<EntityTypes>( asList( 
			LINE, CIRCLE, ARC, ELLIPSE, ELLIPTIC_ARC,
			SPIRAL, SPLINE, POLYLINE, COMPOSITION
		) );
	
	/**
	 * Returns the boundary that surrounds the given drawing elements
	 * @param elements the given {@link ModelElement drawing elements}
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return the {@link RectangleXY boundary}
	 */
	public static RectangleXY getBounds( final Collection<ModelElement> elements, 
										 final MatrixWCStoSCS matrix ) {
		RectangleXY rect2D = null;
		
		// determine the expanse needed
		for( final ModelElement element : elements ) {
			// if the element is a line ...
			if( ( element.getType() != LINE ) || 
					 !EntityRepresentationUtil.getLine( element ).isInfinite() ) {
				// initialize the boundary
				if( rect2D == null ) {
					rect2D = element.getBounds( matrix );
				}
				
				// expand the boundary
				else {
					rect2D.add( element.getBounds( matrix ) );
				}
			}
		}
		
		// return the boundary
		return rect2D;
	}

	/** 
	 * Retrieves a single complex geometric element from the boundary
	 * created at the given mouse click.
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement complex element} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupComplexElementByRegion( final ApplicationController controller, 
															 final MouseClick mouseClick ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the complex elements within the the boundary
		final List<ModelElement> returnSet = 
			lookupComplexElementsByRegion( controller, boundary );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}

	/** 
	 * Retrieves a collection of complex geometric element from the given boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement complex geometric elements} 
	 */
	public static List<ModelElement> lookupComplexElementsByRegion( final ApplicationController controller, 
																	final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> list = new LinkedList<ModelElement>();
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// filter the geometry
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// perform the lookup operation
		for( final ModelElement element : filtered ) {
			if( COMPLEX_TYPES.contains( element.getType() ) && element.intersects( boundary, matrix ) ) {
				list.add( element );
			}
		}
		return list;
	}
	
	/** 
	 * Retrieves a single model element from the boundary
	 * created at the given mouse click.
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement model element} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupGeometricElementByRegion( final ApplicationController controller, 
															   final MouseClick mouseClick ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the geometric elements within the the boundary
		final List<ModelElement> returnSet = 
			lookupGeometricElementsByRegion( controller, boundary );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance(); 
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}
	
	/** 
	 * Retrieves a collection of model element from the given boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement model elements} 
	 */
	public static List<ModelElement> lookupGeometricElementsByRegion( final ApplicationController controller, 
																 	  final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> list = new LinkedList<ModelElement>();
		
		// filter the geometry
		lookupGeometricElementsByRegion( controller, boundary, list );
		
		// return the set
		return list;
	}
	
	/**
	 * Retrieves all visible geometric elements found within the given two-dimensional boundary.
	 * @param controller the given {@link ApplicationController function controller}
	 * @param boundary the given {@link RectangleXY spatial boundary}
	 * @param returnSet the collection for the returning of the 
	 * {@link ModelElement model elements} that were found. 
	 */
	public static void lookupGeometricElementsByRegion( final ApplicationController controller, 
											   			final RectangleXY boundary,
											   			final Collection<ModelElement> returnSet ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();

		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// find the elements that intersect the bounds
		for( final ModelElement element : filtered ) {
			if( GEOMETRY_TYPES.contains( element.getType() ) && 
					element.intersects( boundary, matrix ) ) {
				returnSet.add( element );
			}
		}
	}

	/** 
	 * Retrieves a single drawing element from the given screen boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement element} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupElementByRegion( final ApplicationController controller, 
													  final MouseClick mouseClick ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the drawing elements within the the boundary
		final List<ModelElement> returnSet = 
			lookupElementsByRegion( controller, boundary );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}
	
	/** 
	 * Retrieves a collection of drawing geometric elements that exist within the given boundary.
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement drawing element} 
	 */
	public static List<ModelElement> lookupElementsByRegion( final ApplicationController controller, 
															 final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> list = new LinkedList<ModelElement>();
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// perform the lookup operation
		for( final ModelElement element : filtered ) {
			if( element.intersects( boundary, matrix ) ) {
				list.add( element );
			}
		}
		return list;
	}

	/**
	 * Retrieves all visible geometric elements found within the given two-dimensional boundary.
	 * @param controller the given {@link ApplicationController function controller}
	 * @param boundary the given {@link RectangleXY spatial boundary}
	 * @param returnSet the collection for the returning of the 
	 * {@link ModelElement model elements} that were found. 
	 */
	public static void lookupElementsByRegion( final ApplicationController controller, 
											   final RectangleXY boundary,
											   final Collection<ModelElement> returnSet ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();

		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// find the elements that intersect the bounds
		for( final ModelElement element : filtered ) {
			if( element.intersects( boundary, matrix ) ) {
				returnSet.add( element );
			}
		}
	}
	
	/**
	 * Returns the geometry found within the given bounds
	 * @param controller the given {@link ApplicationController controller}
	 * @param excludeTypes the given array of {@link EntityTypes geometry types} to exclude
	 * @return the {@link Collection collection} of {@link ModelElement geometry} or <tt>null</tt> if not found
	 */
	public static ModelElement lookupElementExclusionSet( final ApplicationController controller, 
														  final MouseClick mouseClick, 
														  final EntityTypes ... excludeTypes ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the desired elements within the the boundary
		final List<ModelElement> returnSet = new LinkedList<ModelElement>();
		lookupElementsExclusionSet( controller, boundary, returnSet, excludeTypes );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}

	/**
	 * Returns the geometry found within the given bounds
	 * @param boundary the given {@link RectangleXY selection boundary}
	 * @param types the given array of {@link EntityTypes geometry types}
	 * @return the {@link Collection collection} of {@link ModelElement geometry} or <tt>null</tt> if not found
	 */
	public static void lookupElementsExclusionSet( final ApplicationController controller, 
												   final RectangleXY boundary, 
												   final Collection<ModelElement> returnSet,
												   final EntityTypes ... excludeTypes ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// create a set of the types
		final Set<EntityTypes> typeSet = new HashSet<EntityTypes>( asList( excludeTypes ) );
				
		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// find the element that intersects the bounds
		for( final ModelElement element : filtered ) {
			if( !typeSet.contains( element.getType() ) ) {
				if( element.intersects( boundary, matrix ) ) {
					returnSet.add( element );
				}
			}
		}
	}
	
	/** 
	 * Retrieves a single drawing element from the given screen boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @param includeTypes the given {@link EntityTypes types to include}
	 * @return the resultant {@link ModelElement element} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupElementInclusionSet( final ApplicationController controller, 
													  	  final MouseClick mouseClick,
													  	  final EntityTypes ... includeTypes ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the drawing elements within the the boundary
		final List<ModelElement> returnSet = new LinkedList<ModelElement>();
		lookupElementsInclusionSet( controller, boundary, returnSet );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}

	/**
	 * Returns the elements found within the given selection boundary
	 * @param boundary the given {@link RectangleXY selection boundary}
	 * @param types the given array of {@link EntityTypes geometry types}
	 * @return the collection of {@link ModelElement geometry} or <tt>null</tt> if not found
	 */
	public static void lookupElementsInclusionSet( final ApplicationController controller, 
												   final RectangleXY boundary, 
												   final Collection<ModelElement> returnSet,
												   final EntityTypes ... includeTypes ) {
		// create a set of the types
		final Set<EntityTypes> typeSet = new HashSet<EntityTypes>( asList( includeTypes ) );
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// find the geometry that intersects the bounds
		for( final ModelElement element : filtered ) {
			if( typeSet.contains( element.getType() ) && 
				element.intersects( boundary, matrix ) ) {
				returnSet.add( element );
			}
		}
	}
	
	/** 
	 * Attempts to retrieve a single curve contained within the given boundary
	 * created at the given mouse click.
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement curve} or <tt>null</tt> if no curves were found
	 */
	public static ModelElement lookupCurveByRegion( final ApplicationController controller, 
													final MouseClick mouseClick ) {
		ModelElement curve = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the curve within the the boundary
		final List<ModelElement> returnSet = lookupCurvesByRegion( controller, boundary );
		
		// determine the selected line
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				curve = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the curve
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return curve;
	}
	
	/** 
	 * Retrieves the collection of lines contained within the given boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement curve} 
	 */
	public static List<ModelElement> lookupCurvesByRegion( final ApplicationController controller, 
													  	   final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> curves = new LinkedList<ModelElement>();
		
		// get the model instance
		final GeometricModel model = controller.getModel();
			
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();

		// filter the geometry
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// perform the lookup operation
		for( final ModelElement element : filtered ) {
			if( CURVE_TYPES.contains( element.getType() ) && element.intersects( boundary, matrix ) ) {
				curves.add( element );
			}
		}
		return curves;
	}
	
	/** 
	 * Retrieves a single line contained within the given boundary
	 * created at the given mouse click.
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement line} or <tt>null</tt> if no lines were found
	 */
	public static ModelElement lookupLineByRegion( final ApplicationController controller, 
											   	   final MouseClick mouseClick ) {
		ModelElement line = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the lines within the the boundary
		final List<ModelElement> returnSet = lookupLinesByRegion( controller, boundary );
		
		// determine the selected line
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				line = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the line
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return line;
	}
	
	/** 
	 * Retrieves the collection of lines contained within the given boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement lines} 
	 */
	public static List<ModelElement> lookupLinesByRegion( final ApplicationController controller, 
														  final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> lines = new LinkedList<ModelElement>();
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// perform the lookup operation
		for( final ModelElement element : filtered ) {
			if( element.getType().equals( LINE ) && element.intersects( boundary, matrix ) ) {
				lines.add( element );
			}
		}
		return lines;
	}
	
	/** 
	 * Retrieves a single phantom drawing element from the given screen boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement element} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupPhantomElementByRegion( final ApplicationController controller, 
													 		 final MouseClick mouseClick ) {
		ModelElement element = null;
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the drawing elements within the the boundary
		final List<ModelElement> returnSet = new LinkedList<ModelElement>();
		
		// capture the phantom elements
		lookupPhantomElementsByRegion( controller, boundary, returnSet );
		
		// determine the selected geometry
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the element
			case 1: 
				element = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the element
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return element;
	}
	
	/**
	 * Retrieves all phantom geometric elements found within the given two-dimensional boundary.
	 * @param controller the given {@link ApplicationController function controller}
	 * @param boundary the given {@link RectangleXY spatial boundary}
	 * @param returnSet the collection for the returning of the 
	 * {@link ModelElement model elements} that were found. 
	 */
	public static void lookupPhantomElementsByRegion( final ApplicationController controller, 
													  final RectangleXY boundary,
													  final Collection<ModelElement> returnSet ) {
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// create the filter container
		final LinkedList<ModelElement> container = new LinkedList<ModelElement>();
		model.getPhantomElements( container );
		
		// find the elements that intersect the bounds
		for( final ModelElement element : container ) {
			if( element.intersects( boundary, matrix ) ) {
				returnSet.add( element );
			}
		}
	}
	
	/** 
	 * Retrieves a single point from the given screen boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param elements the collection of elements to examine
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the resultant {@link ModelElement point} or <tt>null</tt> if no element was found
	 */
	public static ModelElement lookupPointByRegion( final ApplicationController controller, 
												 	final MouseClick mouseClick,
												 	final Collection<ModelElement> elements ) {
		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// create a container for matching points
		final List<ModelElement> returnSet = new LinkedList<ModelElement>();
		
		// perform the lookup operation
		for( final ModelElement element : elements ) {
			if( ( element.getType() == POINT ) && element.intersects( boundary, matrix ) ) {
				returnSet.add( element );
			}
		}
		
		// determine the selected point
		ModelElement point = null;
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the point
			case 1: 
				point = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the point
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return point;
	}
	
	/** 
	 * Retrieves a single point contained within the given boundary
	 * created at the given mouse click.
	 * @param controller the given {@link ApplicationController controller}
	 * @param mouseClick the given {@link MouseClick mouse click}
	 * @return the resultant {@link ModelElement point} or <tt>null</tt> if no points were found
	 */
	public static ModelElement lookupPointByRegion( final ApplicationController controller, 
													final MouseClick mouseClick ) {		
		// get the click boundary
		final RectangleXY boundary = controller.untransform( mouseClick.getClickBounds() );
		
		// lookup the lines within the the boundary
		final List<ModelElement> returnSet = lookupPointsByRegion( controller, boundary );
		
		// determine the selected point
		ModelElement point = null;
		switch( returnSet.size() ) {
			// do nothing
			case 0: break;
			
			// get the point
			case 1: 
				point = returnSet.get( 0 );
				break;
			
			// create the "pick list" for selecting the point
			default:
				// allow the user to "pick" the element
				final PickListDialog dialog = PickListDialog.getInstance();
				dialog.pickElement( controller, mouseClick, returnSet );
				break;
		}
		
		return point;
	}
	
	/** 
	 * Retrieves the collection of points contained within the given boundary
	 * @param controller the given {@link ApplicationController controller}
	 * @param boundary the given {@link RectangleXY screen boundary}
	 * @return the collection of {@link ModelElement points} 
	 */
	public static List<ModelElement> lookupPointsByRegion( final ApplicationController controller, 
													  	   final RectangleXY boundary ) {
		// create the container for the return list
		final List<ModelElement> points = new LinkedList<ModelElement>();
		
		// get the model instance
		final GeometricModel model = controller.getModel();

		// get the transformation matrix
		final MatrixWCStoSCS matrix = controller.getMatrix();
		
		// filter the geometry
		// get only the visible element
		final Collection<ModelElement> filtered = new LinkedList<ModelElement>();
		model.getVisibleElements( filtered );
		
		// perform the lookup operation
		for( final ModelElement element : filtered ) {
			if( ( element.getType() == POINT ) && element.intersects( boundary, matrix ) ) {
				points.add( element );
			}
		}
		return points;
	}

}
