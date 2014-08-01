package constellation.tools.sdk.cxscript;

import java.util.HashMap;
import java.util.Map;

import constellation.ApplicationController;
import constellation.tools.sdk.cxscript.opcodes.objects.DocumentObject;
import constellation.tools.sdk.cxscript.opcodes.objects.InstructionObject;
import constellation.tools.sdk.cxscript.opcodes.objects.MouseObject;
import constellation.tools.sdk.cxscript.opcodes.objects.StatusObject;

/**
 * Constellation Script Runtime Context
 * @author lawrence.daniels@gmail.com
 */
public class CxScriptRuntimeContext {
	private final ApplicationController controller;
	private final Map<String,CxScriptFunction> functions;
	private final Map<String,CxScriptObject> objects;
	private boolean terminate;
	
	/**
	 * Default Constructor 
	 */
	public CxScriptRuntimeContext( final ApplicationController controller ) {
		this.controller	= controller;
		this.terminate 	= false;
		this.functions	= new HashMap<String, CxScriptFunction>();
		this.objects	= new HashMap<String, CxScriptObject>();
		
		// add the built-in "special" functions
		addObject( new DocumentObject() );
		addObject( new InstructionObject() );
		addObject( new MouseObject() );
		addObject( new StatusObject() );
	}
	
	/**
	 * Returns an instance of the application controller
	 * @return the {@link ApplicationController application controller}
	 */
	public ApplicationController getApplicationController() {
		return controller;
	}
	
	public void addFunction( final CxScriptFunction function ) {
		functions.put( function.getName(), function );
	}
	
	public void addObject( final CxScriptObject object ) {
		objects.put( object.getName(), object );
	}
	
	/** 
	 * Looks up an object by name
	 * @param name the given object name
	 * @return the {@link CxScriptObject object} or <tt>null</tt> if not found
	 */
	public CxScriptObject lookupObject( final String name ) {
		return objects.get( name );
	}

	/**
	 * Indicates whether the context is in a terminate state
	 * @return true, if the context is in a terminate state
	 */
	public boolean isTerminate() {
		return terminate;
	}

	/** 
	 * Sets the terminate indicator
	 * @param terminate the terminate indicator
	 */
	public void setTerminate( final boolean terminate ) {
		this.terminate = terminate;
	}
	
}
