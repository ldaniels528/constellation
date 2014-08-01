package constellation.tools.demopro.app;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a Constellation DemoPro Version
 * @author lawrence.daniels@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DemoProVersion {
	String value();
}
