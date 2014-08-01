package constellation.functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import constellation.ApplicationController;

/** 
 * Function Invocation Action
 * @author lawrence.daniels@gmail.com
 */
public class FunctionAction implements ActionListener {
	private final ApplicationController controller;
	private final Function function;
	
	/**
	 * Creates a new function action
	 * @param controller the given {@link ApplicationController controller}
	 * @param function the given {@link Function function}
	 */
	public FunctionAction( final ApplicationController controller, final Class<? extends Function> functionClass ) {
		this.controller	= controller;
		this.function 	= FunctionManager.getFunctionByClass( functionClass );
	}
	
	/**
	 * Creates a new function action
	 * @param controller the given {@link ApplicationController controller}
	 * @param function the given {@link Function function}
	 */
	public FunctionAction( final ApplicationController controller, final Function function ) {
		this.controller	= controller;
		this.function 	= function;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed( final ActionEvent event ) {
		controller.setActiveFunction( function );
	}
}