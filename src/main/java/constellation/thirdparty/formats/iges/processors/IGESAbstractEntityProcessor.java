package constellation.thirdparty.formats.iges.processors;

import org.apache.log4j.Logger;

/** 
 * Represents an Abstract IGES Entity
 * @author lawrence.daniels@gmail.com
 */
public abstract class IGESAbstractEntityProcessor implements IGESEntityProcessor {
	protected final Logger logger = Logger.getLogger( getClass() );
	
	/**
	 * Default constructor
	 */
	protected IGESAbstractEntityProcessor() {
		super();
	}

}
