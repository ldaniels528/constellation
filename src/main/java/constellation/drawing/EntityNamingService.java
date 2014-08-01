package constellation.drawing;

import static constellation.drawing.EntityCategoryTypes.*;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import constellation.drawing.elements.ModelElement;

/**
 * This class acts as a service provider to geometry
 * classes to provide unique names for each entity
 * @author lawrence.daniels@gamil.com
 */
public class EntityNamingService {
	private final Map<EntityCategoryTypes,Set<String>> nameSets;
	private final Map<EntityCategoryTypes,String> classMapping;;
	
	/**
	 * Default constructor
	 */
	public EntityNamingService() {
		this.nameSets 		= new HashMap<EntityCategoryTypes,Set<String>>();
		this.classMapping	= createNameToTypeMapping();
	}
	
	/**
	 * Adds a automatically generated label for the element based
	 * on its category type
	 * @param element the given {@link ModelElement element}
	 */
	public void addLabel( final ModelElement element ) {
		final Set<String> nameSet = lookupNameSet( element.getCategoryType() );
		nameSet.add( element.getLabel() );
	}
	
	/**
	 * Retrieves the next unique name from the service
	 * @return a unique entity name
	 */
	public String getEntityName( final EntityCategoryTypes type ) {
		// get the appropriate name set
		final Set<String> nameSet = lookupNameSet( type );
		
		// setup the id seed
		int namingSeed = nameSet.size();
		
		// find unique name
		String name;
		do {
			name = format( "%s%02d", getPrefixCode( type ), ++namingSeed );
		} while( nameSet.contains( name ) );
		
		// return the name
		return name;
	}
	
	private Set<String> lookupNameSet( final EntityCategoryTypes type ) {
		// if there is already a name set for the type, use it
		if( nameSets.containsKey( type ) ) {
			return nameSets.get( type );
		}
		
		// otherwise, create a new name set for the type
		else {
			final Set<String> names = new HashSet<String>( 100, .95f );
			nameSets.put( type, names );
			return names;
		}
	}
	
	/**
	 * Returns the prefix code for the given type
	 * @param type the given class type
	 * @return the prefix code
	 */
	private String getPrefixCode( final EntityCategoryTypes type ) {
		String prefix = classMapping.get( type );
		if( prefix == null ) {
			prefix = "*ukn";
		}
		return prefix;
	}
	
	/** 
	 * Creates a name to type mapping
	 * @return a name to type mapping
	 */
	private Map<EntityCategoryTypes,String> createNameToTypeMapping() {
		final Map<EntityCategoryTypes,String> map = new HashMap<EntityCategoryTypes, String>();
		map.put( COMPOUND, 	"*cmp" );
		map.put( CURVE, 	"*crv" );
		map.put( DIMENSION,	"*dim" );
		map.put( IMAGE, 	"*img" );
		map.put( LINEAR, 	"*ln" );
		map.put( VERTEX, 	"*pt" );
		map.put( TEXT, 		"*txt" );
		return map;
	}
	
}
