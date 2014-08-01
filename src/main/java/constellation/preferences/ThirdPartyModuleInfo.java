package constellation.preferences;

/**
 * Model Format Reader Information
 * @author lawrence.daniels@gmail.com
 */
public class ThirdPartyModuleInfo {
	private ThirdPartyModuleTypes type;
	private String className;
	private boolean enabled;
	
	/**
	 * Default Constructor
	 */
	public ThirdPartyModuleInfo() {
		super();
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName( final String className ) {
		this.className = className;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the type
	 */
	public ThirdPartyModuleTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ThirdPartyModuleTypes type) {
		this.type = type;
	}
	
}
