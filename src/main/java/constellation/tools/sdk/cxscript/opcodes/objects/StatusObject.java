package constellation.tools.sdk.cxscript.opcodes.objects;

import static constellation.tools.sdk.cxscript.CxScriptUtil.getValues;
import constellation.ApplicationController;
import constellation.tools.sdk.cxscript.AbstractCxScriptFunction;
import constellation.tools.sdk.cxscript.AbstractCxScriptObject;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;
import constellation.tools.sdk.cxscript.value.ValueReference;

/** 
 * Constellation STATUS Object
 * @author lawrence.daniels@gmail.com
 */
public class StatusObject extends AbstractCxScriptObject {
	
	/**
	 * Default Constructor
	 */
	public StatusObject() {
		super( "STATUS" );
		super.addMethod( new SetMethod() );
	}
	
	/** 
	 * DOCUMENT::set Method
	 * @author lawrence.daniels@gmail.com
	 */
	private class SetMethod extends AbstractCxScriptFunction {
		
		/**
		 * Creates a new method
		 */
		public SetMethod() {
			super( "set" );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptFunction#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
		 */
		public void execute( final CxScriptRuntimeContext context, 
							 final ValueReference[] args ) {
			// get the values
			final Object[] values = getValues( context, args );
			
			// the first argument should be a string
			final String message = (String)values[0];
			
			// set the status message
			final ApplicationController controller = context.getApplicationController();
			controller.setStatusMessage( message );
		}
		
	}

}
