package constellation.tools.sdk.cxscript.opcodes.objects;

import constellation.ApplicationController;
import constellation.tools.sdk.cxscript.AbstractCxScriptFunction;
import constellation.tools.sdk.cxscript.AbstractCxScriptObject;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;
import constellation.tools.sdk.cxscript.value.ValueReference;

/** 
 * Constellation MOUSE Object
 * @author lawrence.daniels@gmail.com
 */
public class MouseObject extends AbstractCxScriptObject {
	
	/**
	 * Default Constructor
	 */
	public MouseObject() {
		super( "MOUSE" );
		super.addMethod( new GetXMethod() );
	}
	
	/** 
	 * MOUSE::getX() Method
	 * @author lawrence.daniels@gmail.com
	 */
	private class GetXMethod extends AbstractCxScriptFunction {
		
		/**
		 * Default Constructor
		 */
		public GetXMethod() {
			super( "getX" );
		}
		
		/* 
		 * (non-Javadoc)
		 * @see constellation.tools.sdk.cxscript.CxScriptFunction#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
		 */
		public void execute( final CxScriptRuntimeContext context, 
							 final ValueReference[] args ) {
			// get the controller instance
			final ApplicationController controller = context.getApplicationController();
			
			// 
			
		}
	}


}
