package constellation.functions;

import static java.lang.String.format;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/** 
 * Constellation Function Manager
 * @author lawrence.daniels@gmail.com
 */
public class FunctionManager {
	private static final Logger logger = Logger.getLogger( FunctionManager.class );
	private static final FunctionManager instance = new FunctionManager();
	private static long lastUpdateTime 	= System.currentTimeMillis();
	private final Map<Class<? extends Function>, Function> functions;
	private final Map<String,FunctionSet> functionSets;
	
	/** 
	 * Default constructor
	 */
	public FunctionManager() {
		this.functions		= new LinkedHashMap<Class<? extends Function>, Function>();
		this.functionSets	= new LinkedHashMap<String,FunctionSet>();
	}
	
	/** 
	 * Returns the singleton instance 
	 * @return the singleton instance 
	 */
	public static FunctionManager getInstance() {
		return instance;
	}

	/**
	 * Searches for and retrieve the function by its class definition
	 * @param functionClass the given {@link Class function class}
	 * @return the desired {@link Function function} instance or <tt>null</tt> if not found
	 */
	public static Function getFunctionByClass( final Class<? extends Function> functionClass ) {
		// localize the function mapping
		final Map<Class<? extends Function>, Function> functions = instance.functions;
		
		// if the function is not already contained within the mapping, create it
		synchronized( functions ) {
			if( !functions.containsKey( functionClass ) ) {
				try {
					// add the function class and instance to the mapping
					functions.put( functionClass, functionClass.newInstance() );
					
					// show how many functions are loaded
					if( System.currentTimeMillis() - lastUpdateTime >= 5000 ) {
						logger.info( format( "%d function(s) loaded", functions.size() ) );
						lastUpdateTime = System.currentTimeMillis();
					}
				} 
				catch ( final Exception e ) {
					logger.error( format( "Function class '%s' could not be instantiated", functionClass.getName() ), e );
				}
			}
			
			// return the function
			return functions.get( functionClass );
		}
	}
	
	/** 
	 * Returns the complete collection of functions
	 * @return an array of {@link Function functions}
	 */
	public Set<Function> getFunctions() {
		synchronized( functions ) {
			return new HashSet<Function>( functions.values() );
		}
	}
	
	/**
	 * Returns the function set by name
	 * @param name the name of the desired function set
	 * @return the desired {@link FunctionSet function set}, 
	 * or <tt>null</tt> if not found
	 */
	public FunctionSet getFunctionSet( final String name ) {
		// update the function sets
		updateFunctionSets();
		
		// return the function set by name
		synchronized( functionSets ) {
			return functionSets.get( name );
		}
	}
	
	/**
	 * Returns the collection of function set
	 * @return the collection of {@link FunctionSet function sets}
	 */
	public Set<FunctionSet> getFunctionSets() {
		// update the function sets
		updateFunctionSets();
		
		synchronized( functionSets ) {
			return new HashSet<FunctionSet>( functionSets.values() );
		}
	}
	
	/**
	 * Updates the function set mapping with all available functions 
	 */
	private void updateFunctionSets() {
		// refresh the function set mapping
		synchronized( functions ) {
			synchronized( functionSets ) {
				// is an update required
				if( functions.size() != functionSets.size() ) {
					// erect the data structure
					for( final Function function : functions.values() ) {
						// get the function family name
						final String familyName = function.getFamilyName();
						
						// get the command for the given family
						final FunctionSet functionSet;
						if( functionSets.containsKey( familyName ) ) {
							functionSet = functionSets.get( familyName ); 
						}
						else {
							functionSet = new FunctionSet( familyName );
							functionSets.put( familyName, functionSet );
						}
							
						// add the function to the command
						functionSet.add( function );
					}
				}
			}
		}
	}
	
}
