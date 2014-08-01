package constellation.tools.sdk.cxscript;

/**
 * Constellation Script Object
 * @author lawrence.daniels@gmail.com
 */
public interface CxScriptObject {
	
	/**
	 * Returns the name of the object
	 * @return the name
	 */
	String getName();
	
	/**
	 * Looks up a method via name
	 * @param methodName the given method name
	 * @return the {@link CxScriptFunction method}
	 */
	CxScriptFunction lookupMethod( String methodName );

}
