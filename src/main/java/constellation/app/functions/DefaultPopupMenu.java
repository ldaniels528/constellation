package constellation.app.functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.edit.EntityEditorFunction;
import constellation.functions.FunctionManager;
import constellation.ui.components.menu.CxMenuItem;

/** 
 * Constellation Default Pop-up Menu
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class DefaultPopupMenu extends JPopupMenu {
	// icon declarations
	private static final CxContentManager cxm	= CxContentManager.getInstance();
	public static final Icon PROPERTIES_ICON 	= cxm.getIcon( "images/commands/common/popup/properties.gif" );
	
	// internal fields
	@SuppressWarnings("unused")
	private final ApplicationController controller;
	
	/**
	 * Default Constructor
	 */
	public DefaultPopupMenu( final ApplicationController controller ) {
		this.controller = controller;
		super.add( new CxMenuItem( "Properties", PROPERTIES_ICON, new PropertiesAction( controller ) ) );
	}
	
	/**
	 * Properties Editor Launch Action
	 * @author lawrence.daniels@gmail.com
	 */
	public static class PropertiesAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a new properties action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public PropertiesAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// switch to 'Editor' function
			controller.setActiveFunction( FunctionManager.getFunctionByClass( EntityEditorFunction.class ) );
		}
		
	}
	

}
