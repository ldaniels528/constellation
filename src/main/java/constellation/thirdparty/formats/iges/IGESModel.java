package constellation.thirdparty.formats.iges;

import static constellation.model.Units.UNITS_CENTIMETERS;
import static constellation.model.Units.UNITS_FEET;
import static constellation.model.Units.UNITS_INCHES;
import static constellation.model.Units.UNITS_KILOMETERS;
import static constellation.model.Units.UNITS_METERS;
import static constellation.model.Units.UNITS_MICROINCHES;
import static constellation.model.Units.UNITS_MICRONS;
import static constellation.model.Units.UNITS_MILES;
import static constellation.model.Units.UNITS_MILLIMETERS;
import static constellation.model.Units.UNITS_MILS;
import static constellation.model.formats.cxm.CXMFormatReader.EXTENSION;
import static constellation.thirdparty.formats.iges.elements.IGESElement.TYPE_D;
import static constellation.thirdparty.formats.iges.elements.IGESElement.TYPE_G;
import static constellation.thirdparty.formats.iges.elements.IGESElement.TYPE_P;
import static constellation.thirdparty.formats.iges.elements.IGESElement.TYPE_S;
import static constellation.thirdparty.formats.iges.elements.IGESElement.TYPE_T;
import static constellation.thirdparty.formats.iges.processors.IGESEntityProcessorFactory.evaluate;
import static java.lang.String.format;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import constellation.drawing.elements.ModelElement;
import constellation.model.DefaultGeometricModel;
import constellation.model.DraftingStandards;
import constellation.model.GeometricModel;
import constellation.model.Unit;
import constellation.model.formats.ModelFormatException;
import constellation.thirdparty.formats.iges.elements.IGESElement;
import constellation.thirdparty.formats.iges.elements.IGES_D;
import constellation.thirdparty.formats.iges.elements.IGES_G;
import constellation.thirdparty.formats.iges.elements.IGES_P;
import constellation.thirdparty.formats.iges.elements.IGES_S;
import constellation.thirdparty.formats.iges.elements.IGES_T;
import constellation.thirdparty.formats.iges.entities.IGESEntity;
import constellation.thirdparty.formats.iges.entities.x100.IGESTransformationMatrix;
import constellation.util.StringUtil;

/**
 * Represents an IGES Model
 * @author lawrence.daniels@gmail.com
 */
public class IGESModel {
	private final Logger logger = Logger.getLogger( getClass() );
	private final Map<Integer,IGESTransformationMatrix> matrices;
	private final Map<Integer,IGES_D> directoryEntries;
	private final Map<Integer,IGES_G> globalEntries;
	private final Map<Integer,IGES_S> startEntries;
	private final Map<Integer,IGES_P> parameters;
	private final Map<Integer,IGES_T> terminators;
	
	/**
	 * Default constructor
	 */
	public IGESModel() {
		this.directoryEntries	= new LinkedHashMap<Integer,IGES_D>();
		this.globalEntries		= new LinkedHashMap<Integer,IGES_G>();
		this.parameters			= new LinkedHashMap<Integer,IGES_P>();
		this.startEntries		= new LinkedHashMap<Integer,IGES_S>();
		this.terminators		= new LinkedHashMap<Integer,IGES_T>();
		this.matrices			= new LinkedHashMap<Integer,IGESTransformationMatrix>();
	}
	
	/**
	 * Adds the given IGES element to the model
	 * @param element the given {@link IGESElement IGES element}
	 */
	public void add( final IGESElement element ) {
		// get the record type
		final String type = element.getType();
		final int sequenceNumber = element.getSequenceNumber();
		
		// directory entry?
		if( TYPE_D.equals( type ) ) {
			directoryEntries.put( sequenceNumber, (IGES_D)element );
		}
		
		// global entry?
		else if( TYPE_G.equals( type ) ) {
			globalEntries.put( sequenceNumber, (IGES_G)element );
		}
		
		// parameter entry?
		else if( TYPE_P.equals( type ) ) {
			parameters.put( sequenceNumber, (IGES_P)element );
		}
		
		// start entry?
		else if( TYPE_S.equals( type ) ) {
			startEntries.put( sequenceNumber, (IGES_S)element );
		}
		
		// terminator entry?
		else if( TYPE_T.equals( type ) ) {
			terminators.put( sequenceNumber, (IGES_T)element );
		}
		
		// unhandled type
		else {
			logger.error( format( "Unhandled type '%s'", type ) );
		}
	}
	
	/** 
	 * Adds the given matrix to the model addressable by the given line number
	 * @param sequenceNumber the given sequence number
	 * @param matrix the given {@link IGESTransformationMatrix matrix}
	 */
	public void add( final int sequenceNumber, final IGESTransformationMatrix matrix ) {
		matrices.put( sequenceNumber, matrix );
	}
	
	/** 
	 * Retrieves the directory entry based on the given sequence number
	 * @param sequenceNumber the given sequence number
	 * @return the {@link IGES_D directory entry}
	 */
	public IGES_D lookupDirectoryEntry( final int sequenceNumber ) {
		// get the directory entries
		final IGES_D directoryEntry = directoryEntries.get( sequenceNumber );
		
		// return the entry
		return directoryEntry;
	}

	/** 
	 * Retrieves the matrix based on the given sequence number
	 * @param sequenceNumber the given sequence number
	 * @return the {@link IGESTransformationMatrix matrix}
	 */
	public IGESTransformationMatrix lookupMatrix( final int sequenceNumber ) {
		return matrices.get( sequenceNumber );
	}
	
	/**
	 * Returns the parameter by sequence number
	 * @param sequenceNumber the given sequence number
	 * @return the {@link IGES_P parameter}
	 * @throws ModelFormatException  
	 */
	public IGES_P lookupParameters( final int sequenceNumber ) 
	throws ModelFormatException {
		// get the set of parameters
		final IGES_P param = parameters.get( sequenceNumber );
		
		// if not found, throw an exception
		if( param == null ) {
			throw new ModelFormatException( format( "Parameter '%d' not found", sequenceNumber ) );
		}
		
		return param;
	}

	/**
	 * Returns the Constellation Model
	 * @return the {@link GeometricModel Constellation Model}
	 * @throws ModelFormatException 
	 */
	public GeometricModel toModel() 
	throws ModelFormatException {
		// set the model author information
		final IGES_G global = extractGlobalSegment();
		
		// create a new Constellation model
		final GeometricModel model = createModel( global );
		
		// start with the directory entries
		final Collection<IGES_D> entries = directoryEntries.values();
		for( final IGES_D entry : entries ) {
			// get the parameters
			final IGES_P params = lookupParameters( entry.getParameterIndex() );
			
			// process the entity
			final IGESEntity entity = evaluate( model, this, entry, params );
			if( entity != null ) {
				// display the entity
				if( logger.isDebugEnabled() ) {
					logger.debug( format( "[%s] %s", entity.getClass().getSimpleName(), entity ) );
				}
				
				// convert the IGES entity into geometry
				final ModelElement[] drawingElements = entity.toDrawingElements();
				if( ( drawingElements != null ) && ( drawingElements.length > 0 ) ) {
					model.addPhysicalElement( drawingElements );
				}
			} 
			else {
				logger.error( format( "Directory entry '%s' was not processed", entry.getEntityTypeNumber() ) );	
			}
		}
		
		return model;
	}
	
	/** 
	 * Returns the Constellation Model
	 * @return the {@link GeometricModel model}
	 */
	private GeometricModel createModel( final IGES_G global ) {
		// get the model units
		final Unit unit = lookupUnit( global.getUnitFlag() );
		
		// create the Constellation Model
		final GeometricModel model = DefaultGeometricModel.newModel( getModelFile( global ), unit );
		model.setAuthorName( global.getAuthorName() );
		model.setAuthorOrganization( global.getAuthorOrganization() );
		model.setDescription( extractIGESDescription() );
		model.setDraftingStandard( DraftingStandards.values()[ global.getDraftingStandardCode() ] );
		model.setScale( global.getModelSpaceScale() );
		model.setUnit( unit );
		return model;
	}
	
	/** 
	 * Returns the model file
	 * @param global the given {@link IGES_G IGES Global Segment}
	 * @return the model {@link File file}
	 */
	private File getModelFile( final IGES_G global ) {
		final String igesFileName = global.getFileName();
		if( !StringUtil.isBlank( igesFileName ) ) {
			if( igesFileName.contains( "." ) ) {
				final int index = igesFileName.indexOf( '.' );
				return new File( format( "%s.%s", igesFileName.substring( 0, index ), EXTENSION ) );
			}
		}
		return new File( format( "Untitled.%s", EXTENSION ) );
	}
	
	/**
	 * Retrieves the IGES global segment
	 * @return the {@link IGES_G IGES global segment}
	 */
	private IGES_G extractGlobalSegment() {
		return globalEntries.values().iterator().next();
	}
	
	/**
	 * Retrieves the IGES model description
	 * @return the IGES model description
	 */
	private String extractIGESDescription() {
		final StringBuilder sb = new StringBuilder( 320 );
		final Collection<IGES_S> entries = startEntries.values();
		for( final IGES_S entry : entries ) {
			// append the description
			sb.append( entry.getDescription() );
			sb.append( '\n' );
		}
		
		return sb.toString();
	}

	/**
	 * Returns the appropriate unit per the IGES specification
	 * @param index the given unit index
	 * @return the resultant {@link Unit unit}
	 */
	private Unit lookupUnit( final int index ) {
		switch( index ) {
			case 1: return UNITS_INCHES; 
			case 2: return UNITS_MILLIMETERS; 
			case 3: return UNITS_FEET; 
			case 4: return UNITS_MILES;
			case 5: return UNITS_METERS; 
			case 6: return UNITS_KILOMETERS; 
			case 7: return UNITS_MILS;
			case 8: return UNITS_MICRONS;
			case 9: return UNITS_CENTIMETERS; 
			case 10: return UNITS_MICROINCHES;
			default: return UNITS_MILLIMETERS;
		}
	}
	
}
