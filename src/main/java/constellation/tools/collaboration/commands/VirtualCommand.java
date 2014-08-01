package constellation.tools.collaboration.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a command this is virtual; requires a virtual
 * model implementation as opposed to a "host" model.
 * @author lawrence.daniels@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VirtualCommand {
	boolean value();
}
