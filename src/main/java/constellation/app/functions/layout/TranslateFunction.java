package constellation.app.functions.layout;

import static constellation.app.functions.DefaultPopupMenu.PROPERTIES_ICON;
import static constellation.drawing.EntityTypes.POINT;
import static constellation.functions.MouseClick.BUTTON_SELECT;
import static constellation.functions.MouseClick.BUTTON_INDICATE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import constellation.ApplicationController;
import constellation.CxContentManager;
import constellation.app.functions.StructuredSelectionFunction;
import constellation.app.functions.DefaultPopupMenu.PropertiesAction;

import constellation.app.math.ElementDetectionUtil;
import constellation.drawing.EntityRepresentationUtil;
import constellation.drawing.elements.ModelElement;
import constellation.drawing.entities.PointXY;
import constellation.functions.MouseClick;
import constellation.functions.Steps;
import constellation.model.GeometricModel;
import constellation.ui.components.menu.CxMenuItem;

/**
 * The LAYOUT::TRANSLATE function
 * @author lawrence.daniels@gmail.com
 */
public class TranslateFunction extends StructuredSelectionFunction {
	private static final Steps STEPS = new Steps(
		"Define the translation: select the source #point",
		"Define the translation: select the target #point",
		"Select #elements to translate or END to quit" 
	);
	
	// icon declarations
	private final CxContentManager cxm	= CxContentManager.getInstance();
	private final Icon SEGMENT_ICON 	= cxm.getIcon( "images/commands/line/popup/segment.png" );
	private final Icon NEW_PT_ICON 		= cxm.getIcon( "images/commands/common/popup/new-point.png" );
	
	// internal fields
	private JPopupMenu popupMenu;
	private PointXY currentPos;
	
	/**
	 * Default constructor
	 */
	public TranslateFunction() {
		super( 
			"LAYOUT", "TRANSLATE", 
			"images/commands/layout/translate.png", 
			"docs/functions/layout/translate.html", 
			STEPS 
		);
	}	
		
	/* 
	 * (non-Javadoc)
	 * @see constellation.app.functions.PickListObserver#elementSelected(constellation.math.geometric.GeometricElement)
	 */
	@Override
	public void elementSelected( final ApplicationController controller, final ModelElement element ) {
		handlePointSelection( controller, element );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see constellation.functions.Function#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu( final ApplicationController controller ) {
		if( popupMenu == null ) {
			popupMenu = new MyPopupMenu( controller );
		}
		return popupMenu;
	}

	/* 
	 * (non-Javadoc)
	 * @see constellation.commands.Command#process(constellation.math.geometric.Point)
	 */
	@Override
	public void processMouseClick( final ApplicationController controller, final MouseClick mouseClick ) {
		// get the click point
		final PointXY point = controller.untransform( mouseClick );
		
		// get the model instance
		final GeometricModel model = controller.getModel();
		
		// process the click
		switch( mouseClick.getButton() ) {
			// selecting a point ...
			case BUTTON_SELECT:
				// determine the selected point
				ModelElement pickedPt = ElementDetectionUtil.lookupPointByRegion( controller, mouseClick );
				handlePointSelection( controller, pickedPt );
				break;
		
			// move the selected points ...
			case BUTTON_INDICATE:		
				// get the selected geometry
				int count;
				if( ( count = model.getSelectedElementCount() ) > 0 ) {
					// get the selected elements
					final ModelElement[] elements = new ModelElement[count];
					model.getSelectedGeometry( elements );
					
					// set up the deltas for movement
					boolean once = true;
					double deltaX = 0;
					double deltaY = 0;
					
					// move the points
					for( final ModelElement element : elements ) {
						if( element.getType() == POINT ) {
							// cast the selected point
							final ModelElement selectedPoint = element;
							final PointXY p = EntityRepresentationUtil.getPoint( selectedPoint );
							
							// if deltas have not been computed
							if( once  ) {
								once = !once;
								deltaX = point.getX() - p.getX();
								deltaY = point.getY() - p.getY();
							}
							
							// move the point
							p.translate( new PointXY( deltaX, deltaY ) );
						}
					}
					advanceToNextStep( controller );
				}
				break;
		}
	}
	
	/**
	 * Handles the selection of an element
	 * @param controller the given {@link ApplicationController controller}
	 * @param point the given {@link ModelElement point}
	 */
	private void handlePointSelection( final ApplicationController controller, final ModelElement point ) {
		if( point != null ) {
			// get the model instance
			final GeometricModel model = controller.getModel();
			
			// handle the clicked point
			model.selectGeometry( point );
			
			// advance to the next step
			advanceToNextStep( controller );
		}
	}
	
	/** 
	 * Line: Point-to-Point Pop-up Menu
	 * @author lawrence.daniels@gmail.com
	 */
	@SuppressWarnings("serial")
	private class MyPopupMenu extends JPopupMenu {

		/**
		 * Default Constructor
		 */
		public MyPopupMenu( final ApplicationController controller ) {
			super.add( new CxMenuItem( "New point", NEW_PT_ICON, new NewPointAction( controller ) ) );
			super.add( new CxMenuItem( "End", SEGMENT_ICON, new QuitAction( controller ) ) );
			super.add( new JSeparator() );
			super.add( new CxMenuItem( "Properties", PROPERTIES_ICON, new PropertiesAction( controller ) ) );
		}
	}
	
	/** 
	 * "New Point" Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class NewPointAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a "New Point" action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public NewPointAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// add the point to the model
			final GeometricModel model = controller.getModel();
			model.addPhysicalElement( currentPos );
			
			// treat it like a clicked point
			//TODO handlePointSelection( controller, currentPos );
		}
	}
	
	/**
	 * Quit Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class QuitAction implements ActionListener {
		private final ApplicationController controller;
		
		/**
		 * Creates a "New Point" action
		 * @param controller the given {@link ApplicationController controller}
		 */
		public QuitAction( final ApplicationController controller ) {
			this.controller = controller;
		}

		/** 
		 * {@inheritDoc}
		 */
		public void actionPerformed( final ActionEvent event ) {
			// TODO Auto-generated method stub
		}
	}
	
}