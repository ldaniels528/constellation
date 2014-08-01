package constellation.tools.sdk.cxscript;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic CxScript Object
 * @author lawrence.daniels@gmail.com
 */
public class AbstractCxScriptObject implements CxScriptObject {
	private final Map<String,CxScriptFunction> functions;
	private final String name;
	
	/** 
	 * Creates a new generic CxScript object
	 * @param name the given function name
	 */
	public AbstractCxScriptObject( final String name ) {
		this.name 		= name;
		this.functions	= new HashMap<String,CxScriptFunction>();
	}
	
	/** 
	 * Adds a new method to the object
	 * @param method the given {@link CxScriptFunction method}
	 */
	public void addMethod( final CxScriptFunction method ) {
		functions.put( method.getName(), method );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.CxScriptObject#lookupMethod(java.lang.String)
	 */
	public CxScriptFunction lookupMethod( final String methodName ) {
		return functions.get( methodName );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptFunction#getName()
	 */
	public String getName() {
		return name;
	}
}
