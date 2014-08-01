package constellation.tools.sdk.cxscript.opcodes.objects;

import constellation.tools.sdk.cxscript.AbstractCxScriptObject;
import constellation.tools.sdk.cxscript.CxScriptFunction;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;
import constellation.tools.sdk.cxscript.value.ValueReference;

/** 
 * Constellation INSTRUCTION Object
 * @author lawrence.daniels@gmail.com
 */
public class InstructionObject extends AbstractCxScriptObject {
	
	/**
	 * Default Constructor
	 */
	public InstructionObject() {
		super( "INSTRUCTION" );
		super.addMethod( new SetMethod() );
	}
	
	/** 
	 * DOCUMENT::select Method
	 * @author lawrence.daniels@gmail.com
	 */
	private class SetMethod implements CxScriptFunction {
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
		 */
		public void execute( final CxScriptRuntimeContext runtime, ValueReference[] args ) {
			// TODO Auto-generated method stub
		}

		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#getName()
		 */
		public String getName() {
			return "set";
		}
	}

}
