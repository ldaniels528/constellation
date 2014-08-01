package constellation.tools.sdk.cxscript;

import java.nio.ByteBuffer;

import constellation.tools.sdk.cxscript.value.ConstantValue;
import constellation.tools.sdk.cxscript.value.ValueReference;

/**
 * Constellation Script OpCode Utility
 * @author lawrence.daniels@gmail.com
 */
public class CxScriptUtil {
	
	/** 
	 * Decodes a string from the buffer
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return a string
	 */
	public static String decodeString( final ByteBuffer buffer ) {
		// get the length of the string
		final short length = buffer.getShort();
		
		// get the bytes to from a new string
		final byte[] data = new byte[ length ];
		buffer.get( data );
		
		// return the string
		return new String( data );
	}
	
	/** 
	 * Returns the method arguments
	 * @param buffer the given {@link ByteBuffer buffer}
	 * @return the array of {@link ValueReference method arguments}
	 */
	public static ValueReference[] decodeArguments( final ByteBuffer buffer  ) {
		// get the length of the string
		final byte count = buffer.get();
		
		// get the bytes to from a new string
		final ValueReference[] args = new ValueReference[ count ];
		for( int n = 0; n < count; n++ ) {
			args[n] = new ConstantValue( String.format( "%d", n+1 ) );
		}
		// return the string
		return args;
	}
	
	/**
	 * Evaluates the values of the given array of value references
	 * @param context the given {@link CxScriptRuntimeContext context}
	 * @param args the given array of {@link ValueReference value references}
	 * @return the object values
	 */
	public static Object[] getValues( final CxScriptRuntimeContext context,
									  final ValueReference... args ) {
		final Object[] values = new Object[ args.length ];
		for( int n = 0; n < args.length; n++ ) {
			values[n] = args[n].getValue( context );
		}
		return values;
	}

}
