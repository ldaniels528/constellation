package constellation.tools.sdk.cxscript;

/**
 * Generic CxScript Function
 * @author lawrence.daniels@gmail.com
 */
public abstract class AbstractCxScriptFunction implements CxScriptFunction {
	private final String name;
	
	/** 
	 * Creates a new generic CxScript function
	 * @param name the given function name
	 */
	public AbstractCxScriptFunction( final String name ) {
		this.name = name;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.tools.sdk.cxscript.opcodes.CxScriptFunction#getName()
	 */
	public String getName() {
		return name;
	}

}
