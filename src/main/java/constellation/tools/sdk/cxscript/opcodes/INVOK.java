package constellation.tools.sdk.cxscript.opcodes;

import static constellation.tools.sdk.cxscript.CxScriptUtil.decodeArguments;
import static constellation.tools.sdk.cxscript.CxScriptUtil.decodeString;
import static java.lang.String.format;

import java.nio.ByteBuffer;

import constellation.tools.sdk.cxscript.CxScriptFunction;
import constellation.tools.sdk.cxscript.CxScriptObject;
import constellation.tools.sdk.cxscript.CxScriptRuntimeContext;
import constellation.tools.sdk.cxscript.value.ValueReference;

/**
 * Constellation Script Method Invocation (INVOK) OpCode
 * <br>Syntax: INVOK object::method(arg1,arg2,..,argN)
 * @author lawrence.daniels@gmail.com
 */
public class INVOK implements CxScriptOpCode {
	private final ValueReference[] args;
	private final String objectName;
	private final String methodName;
	
	/**
	 * Creates a new "INVOK" opCode
	 * @param objectName the given object name
	 * @param methodName the given method name
	 * @param args the given array of {@link ValueReference arguments}
	 */
	public INVOK( final String objectName,
				  final String methodName, 
				  final ValueReference[] args ) {
		this.objectName = objectName;
		this.methodName = methodName;
		this.args		= args;
	}
	
	/**
	 * Creates a new "INVOK" opCode
	 * @param buffer the given {@link ByteBuffer buffer}
	 */
	public INVOK( final ByteBuffer buffer ) {
		this.objectName = decodeString( buffer );
		this.methodName = decodeString( buffer );
		this.args		= decodeArguments( buffer );
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptOpCode#execute(constellation.tools.sdk.cxscript.CxScriptRuntimeContext)
	 */
	public void execute( final CxScriptRuntimeContext context ) {
		// lookup the object by name
		final CxScriptObject object = context.lookupObject( objectName );
		if( object != null ) {
			// lookup the method by name
			final CxScriptFunction method = object.lookupMethod( methodName );
			if( method != null ) {
				method.execute( context, args );
			}
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// create the arguments string
		final StringBuilder sb = new StringBuilder(200);
		int n = 0;
		for( final ValueReference arg : args ) {
			if( n++ > 0 ) {
				sb.append( ", " );
			}
			sb.append( arg );
		}
		
		// return the entire string
		return format( "INVOK %s::%s(%s)", objectName, methodName, sb );
	}

}
