package constellation.tools.sdk.cxscript.opcodes.objects;

import constellation.tools.sdk.cxscript.AbstractCxScriptObject;
import constellation.tools.sdk.cxscript.CxScriptFunction;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;
import constellation.tools.sdk.cxscript.value.ValueReference;

/** 
 * Constellation DOCUMENT Object
 * @author lawrence.daniels@gmail.com
 */
public class DocumentObject extends AbstractCxScriptObject {
	
	/**
	 * Default Constructor
	 */
	public DocumentObject() {
		super( "DOCUMENT" );
		super.addMethod( new AddMethod() );
		super.addMethod( new SelectMethod() );
	}
	
	/** 
	 * DOCUMENT::add Method
	 * @author lawrence.daniels@gmail.com
	 */
	private class AddMethod implements CxScriptFunction {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
		 */
		public void execute( final CxScriptRuntimeContext runtime, 
							 final ValueReference[] args ) {
			// TODO Auto-generated method stub
		}

		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#getName()
		 */
		public String getName() {
			return "add";
		}
	}

	/** 
	 * DOCUMENT::select Method
	 * @author lawrence.daniels@gmail.com
	 */
	private class SelectMethod implements CxScriptFunction {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
		 */
		public void execute( final CxScriptRuntimeContext runtime, 
							 final ValueReference[] args ) {
			// TODO Auto-generated method stub
		}

		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#getName()
		 */
		public String getName() {
			return "select";
		}
	}
	
}
