package constellation.drawing;

import static constellation.drawing.EntityTypes.LINE;
import static constellation.drawing.LinePatternDefs.CENTER_LINE_STROKE;
import static constellation.drawing.LinePatternDefs.DASHED_STROKE;
import static constellation.drawing.LinePatternDefs.DOTTED_STROKE;
import static constellation.drawing.LinePatternDefs.PHANTOM_STROKE;
import static constellation.drawing.LinePatternDefs.SOLID_STROKE;
import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Collection;

import constellation.ApplicationController;
import constellation.drawing.elements.CxModelElement;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.ArcXY;
import constellation.drawing.entities.CircleXY;
import constellation.drawing.entities.CompositionXY;
import constellation.drawing.entities.CurveXY;
import constellation.drawing.entities.EllipseXY;
import constellation.drawing.entities.EllipticArcXY;
import constellation.drawing.entities.LineXY;
import constellation.drawing.entities.PointXY;
import constellation.drawing.entities.PolyLineXY;
import constellation.drawing.entities.RectangleXY;
import constellation.drawing.entities.SpiralXY;
import constellation.drawing.entities.SplineXY;
import constellation.drawing.entities.UserDefinedConicXY;
import constellation.drawing.entities.VerticesXY;
import constellation.math.MatrixWCStoSCS;
import constellation.model.GeometricModel;

/** 
 * Constellation Entity Representation Utility
 * @author lawrence.daniels@gmail.com
 */
public class EntityRepresentationUtil {
	
	/**
	 * Returns the boundary that surrounds the given drawing entities
	 * @param entities the given {@link EntityRepresentation drawing entities}
	 * @param matrix the given {@link MatrixWCStoSCS transformation matrix}
	 * @return the {@link RectangleXY boundary}
	 */
	public static RectangleXY getBounds( final Collection<EntityRepresentation> entities, 
										 final MatrixWCStoSCS matrix ) {
		RectangleXY rect2D = null;
		
		// determine the expanse needed
		for( final EntityRepresentation entity : entities ) {
			// if the element is a line ...
			if( ( entity.getType() != LINE ) || 
					 !EntityRepresentationUtil.getLine( entity ).isInfinite() ) {
				// initialize the boundary
				if( rect2D == null ) {
					rect2D = entity.getBounds( matrix );
				}
				
				// expand the boundary
				else {
					rect2D.add( entity.getBounds( matrix ) );
				}
			}
		}
		
		// return the boundary
		return rect2D;
	}
	
	/**
	 * Returns the appropriate category type for the given entity type
	 * @param entityType the given {@link EntityTypes entity type}
	 * @return the {@link EntityCategoryTypes category type}
	 */
	public static EntityCategoryTypes getCategoryType( final EntityTypes entityType ) {
		switch( entityType ) {
			case ARC:;
			case CIRCLE:;
			case ELLIPSE:;
			case ELLIPTIC_ARC:;
			case SPLINE:;
			case SPIRAL:;
				return EntityCategoryTypes.CURVE;
				
			case COMMENT:;
			case TEXTNOTE:
				return EntityCategoryTypes.TEXT;
				
			case COMPOSITION:;
			case USER_DEFINED:
				return EntityCategoryTypes.COMPOUND;
			
			case LINE:
				return EntityCategoryTypes.LINEAR;
				
			case PICTURE:
				return EntityCategoryTypes.IMAGE;
				
			case POINT:
				return EntityCategoryTypes.VERTEX;
	
			default:
				throw new IllegalArgumentException( format( "No mapping found for type '%s'", entityType ) );
		}
	}
	
	/** 
	 * Returns the circle or <tt>null</tt> if the entity is not a circle
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link CircleXY circle}
	 */
	public static CircleXY getCircle( final ModelElement element ) {
		// is the element a circle already?
		if( element instanceof CircleXY ) {
			return (CircleXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a circle type?
			if( mdlElem.getType() == EntityTypes.CIRCLE ) {
				return (CircleXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the arc or <tt>null</tt> if the entity is not an arc
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link ArcXY arc}
	 */
	public static ArcXY getCircularArc( final ModelElement element ) {
		// is the element an arc already?
		if( element instanceof ArcXY ) {
			return (ArcXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it an arc type?
			if( mdlElem.getType() == EntityTypes.ARC ) {
				return (ArcXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	} 
	
	/** 
	 * Returns the composition or <tt>null</tt> if the entity is not an composition
	 * @param element the given {@link ModelElement model element}
	 * @return the {@link CompositionXY composition}
	 */
	public static CompositionXY getComposition( final ModelElement element ) {
		// is the element an composition already?
		if( element instanceof CompositionXY ) {
			return (CompositionXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it an arc type?
			if( mdlElem.getType() == EntityTypes.COMPOSITION ) {
				return (CompositionXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the curve or <tt>null</tt> if the entity is not a curve
	 * @param element the given {@link RenderableElement drawing element}
	 * @return the {@link CurveXY curve}
	 */
	public static CurveXY getCurve( final RenderableElement element ) {
		// is the element a curve already?
		if( element instanceof CurveXY ) {
			return (CurveXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// get the internal representation
			final EntityRepresentation rep = mdlElem.getRepresentation();
			
			// is it a curve?
			if( rep instanceof CurveXY ) {
				return (CurveXY)rep;
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the ellipse or <tt>null</tt> if the entity is not an ellipse
	 * @param element the given {@link RenderableElement drawing element}
	 * @return the {@link EllipseXY ellipse}
	 */
	public static EllipseXY getEllipse( final RenderableElement element ) {
		// is the element an ellipse already?
		if( element instanceof EllipseXY ) {
			return (EllipseXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it an ellipse?
			if( mdlElem.getType() == EntityTypes.ELLIPSE ) {
				return (EllipseXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the elliptic arc or <tt>null</tt> if the entity is not an elliptic arc
	 * @param element the given {@link RenderableElement drawing element}
	 * @return the {@link EllipticArcXY elliptic arc}
	 */
	public static EllipticArcXY getEllipticArc( final RenderableElement element ) {
		// is the element an elliptic arc already?
		if( element instanceof EllipticArcXY ) {
			return (EllipticArcXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it an elliptic arc?
			if( mdlElem.getType() == EntityTypes.ELLIPTIC_ARC ) {
				return (EllipticArcXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the line or <tt>null</tt> if the entity is not a line
	 * @param element the given {@link RenderableElement drawing element}
	 * @return the {@link LineXY line}
	 */
	public static LineXY getLine( final RenderableElement element ) {
		// is the element a line already?
		if( element instanceof LineXY ) {
			return (LineXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a line?
			if( mdlElem.getType() == EntityTypes.LINE ) {
				return (LineXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the point or <tt>null</tt> if the entity is not a point
	 * @param element the given {@link RenderableElement drawing element}
	 * @return the {@link PointXY point}
	 */
	public static PointXY getPoint( final RenderableElement element ) {
		// is the element a point already?
		if( element instanceof PointXY ) {
			return (PointXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a point?
			if( mdlElem.getType() == EntityTypes.POINT ) {
				return (PointXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the polyLine or <tt>null</tt> if the entity is not a polyLine
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link PolyLineXY polyLine}
	 */
	public static PolyLineXY getPolyLine( final ModelElement element ) {
		// is the element a polyLine already?
		if( element instanceof PolyLineXY ) {
			return (PolyLineXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a polyLine?
			if( mdlElem.getType() == EntityTypes.POLYLINE ) {
				return (PolyLineXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the spiral or <tt>null</tt> if the entity is not a spiral
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link SpiralXY spiral}
	 */
	public static SpiralXY getSpiral( final ModelElement element ) {
		// is the element a spiral already?
		if( element instanceof SpiralXY ) {
			return (SpiralXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a spiral?
			if( mdlElem.getType() == EntityTypes.SPIRAL ) {
				return (SpiralXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the spline or <tt>null</tt> if the entity is not a spline
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link SplineXY spline}
	 */
	public static SplineXY getSpline( final ModelElement element ) {
		// is the element a spline already?
		if( element instanceof SplineXY ) {
			return (SplineXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it a spline?
			if( mdlElem.getType() == EntityTypes.SPLINE ) {
				return (SplineXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the user-defined conic or <tt>null</tt> if the entity is not a user-defined conic
	 * @param element the given {@link ModelElement drawing element}
	 * @return the {@link UserDefinedConicXY user-defined conic}
	 */
	public static UserDefinedConicXY getUserDefinedConic( final ModelElement element ) {
		// is the element an user-defined conic already?
		if( element instanceof UserDefinedConicXY ) {
			return (UserDefinedConicXY)element;
		}
		
		// is the element a model element?
		if( element instanceof ModelElement ) {
			final ModelElement mdlElem = (ModelElement)element;
			
			// is it an user-defined conic?
			if( mdlElem.getType() == EntityTypes.USER_DEFINED ) {
				return (UserDefinedConicXY)mdlElem.getRepresentation();
			}
		}
		
		return null;
	}
	
	/** 
	 * Returns the drawing element equivalent of the given geometric representation
	 * @param rep the given {@link EntityRepresentation geometric representation}
	 * @return the {@link ModelElement drawing element}
	 */
	public static ModelElement toDrawingElement( final EntityRepresentation rep ) {
		return new CxModelElement( rep );
	}
	
	/**
	 * Renders the geometry onto the given graphics context
	 * @param controller the given {@link ApplicationController controller}
	 * @param model the given {@link GeometricModel geometric model}
	 * @param matrix the given {@link MatrixWCStoSCS WCS to SCS matrix}
	 * @param clipper the given {@link Rectangle clipping boundary}
	 * @param g the given {@link Graphics2D graphics context}
	 * @param geometry the given {@link ComplexInternalRepresentation complex geometric representation}
	 * @param color the rendering {@link Color color} or <tt>null</tt> for the element's color
	 */
	public static void render( final ApplicationController controller,
							   final GeometricModel model,
							   final MatrixWCStoSCS matrix,
							   final Rectangle clipper, 
							   final Graphics2D g, 
							   final ComplexInternalRepresentation geometry,
							   final Color color ) {
		// get the composition points
		final VerticesXY vertices = geometry.getVertices( matrix );
		
		// get the projecting points
		final Point[] projectedPoints = ScratchPad.getProjectionPoints( vertices );
		
		// project the points
		matrix.transform( vertices, projectedPoints );
		
		// set the color
		g.setColor( color ); 
		
		// draw the lines
		final int count = vertices.length() - 1;
		for( int n = 0; n < count; n++ ) {
			// cache the points of the line
			final Point p1 = projectedPoints[n];
			final Point p2 = projectedPoints[n+1];
				
			// draw the line
			if( clipper.intersectsLine( p1.x, p1.y, p2.x, p2.y ) ) {
				// is the line completely within the view port?
				if( !clipper.contains( p1 ) || !clipper.contains( p2 ) ) {
					LineXY.clipLine( clipper, p1, p2 );
				}
				
				// cache the line's coordinates
				final int x1 = p1.x;
				final int y1 = p1.y;
				final int x2 = p2.x;
				final int y2 = p2.y;
				
				// draw the line
				g.drawLine( x1, y1, x2, y2 );
			}
		}
	}

	/**
	 * Converts the given geometric limits into an array of points 
	 * @param limits the given array of {@link VerticesXY limits}
	 * @return the array of {@link PointXY points}
	 */
	public static PointXY[] fromLimits( final VerticesXY limits ) {
		final PointXY[] points = new PointXY[ limits.length() ];
		for( int n = 0; n < points.length; n++ ) {
			points[n] = new PointXY( limits.x[n], limits.y[n] );
		}
		return points;
	}

	/**
	 * Converts the given array of points into a geometric shape
	 * @param points the given array of {@link PointXY points}
	 * @return the {@link VerticesXY geometric shape}
	 */
	public static VerticesXY toLimits( final PointXY ... points ) {
		final VerticesXY limits = new VerticesXY( points.length );
		for( final PointXY p : points ) {
			limits.add( p.x, p.y );
		}
		return limits;
	}

	/**
	 * Converts the given collection of points into a geometric shape
	 * @param points the given collection of {@link PointXY points}
	 * @return the {@link VerticesXY geometric shape}
	 */
	public static VerticesXY toLimits( final Collection<PointXY> points ) {
		final VerticesXY limits = new VerticesXY( points.size() );
		int n = 0;
		for( final PointXY p : points ) {
			limits.add( p.x, p.y );
			n++;
		}
		return limits;
	}
	
	/**
	 * Returns the heading for the given type ('Points')
	 * @param type the given {@link EntityTypes type}
	 * @return the type heading
	 */
	public static String getTypeHeading( final EntityTypes type ) {
		// get the type name
		final String name = getTypeNameCap( type );
		
		// build the heading
		final StringBuilder heading = new StringBuilder( name.length() + 1 );
		heading.append( name );
		heading.append( 's' );
		
		// return the heading
		return heading.toString();
	}

	/**
	 * Returns the name of the given type (e.g. 'point')
	 * @param type the given {@link EntityTypes type}
	 * @return the type name
	 */
	public static String getTypeName( final EntityTypes type ) {
		return type.toString().toLowerCase();
	}
	
	/**
	 * Returns the heading for the given type ('Point')
	 * @param type the given {@link EntityTypes type}
	 * @return the type heading
	 */
	public static String getTypeNameCap( final EntityTypes type ) {
		// get the type name
		final String name = getTypeName( type );
		
		// build the heading
		final StringBuilder heading = new StringBuilder( name.length() + 1 );
		heading.append( name.substring( 0, 1 ).toUpperCase() );
		heading.append( name.substring( 1, name.length() ) );
		
		// return the heading
		return heading.toString();
	}

	/** 
	 * Sets the rendering stroke
	 * @param element the given {@link ModelElement model element}
	 */
	public static Stroke getStroke( final ModelElement element ) {
		return getStroke( element.getPattern() );
	}

	/** 
	 * Sets the rendering stroke
	 * @param element the given {@link ModelElement model element}
	 */
	public static Stroke getStroke( final LinePatterns patten ) {
		switch( patten ) {
			case PATTERN_SOLID: 
				return SOLID_STROKE;
				
			case PATTERN_DASHED:
				return DASHED_STROKE;
			
			case PATTERN_DOTTED:
				return DOTTED_STROKE;
			
			case PATTERN_CENTERLINE:
				return CENTER_LINE_STROKE;
	
			case PATTERN_PHANTOM:
				return PHANTOM_STROKE;
				
			default:
				return SOLID_STROKE;
		}
	}

}
