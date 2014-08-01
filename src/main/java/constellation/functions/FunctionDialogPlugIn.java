package constellation.functions;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

/**
 * Function Dialog Plug-in
 * @author lawrence.daniels@gmail.com
 */
public class FunctionDialogPlugIn {
	private final JComponent component; 
	private final ActionListener listener;
	
	/** 
	 * Creates a new function dialog plug-in
	 * @param component the given {@link JComponent component}
	 * @param listener the given {@link ActionListener action listener}
	 */
	public FunctionDialogPlugIn( final JComponent component, final ActionListener listener ) {
		this.component	= component;
		this.listener	= listener;
	}

	/**
	 * @return the component
	 */
	public JComponent getComponent() {
		return component;
	}

	/**
	 * @return the listener
	 */
	public ActionListener getListener() {
		return listener;
	}

}
