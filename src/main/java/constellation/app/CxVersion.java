package constellation.app;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a Constellation Application Version
 * @author lawrence.daniels@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CxVersion {
	String value();
}
