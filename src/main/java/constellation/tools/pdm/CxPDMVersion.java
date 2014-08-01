package constellation.tools.pdm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a PDM Server/Client Application Version
 * @author lawrence.daniels@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CxPDMVersion {
	String value();
}
