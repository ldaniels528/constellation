package constellation.app.functions;

import static java.lang.String.format;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import constellation.ApplicationController;
import constellation.ui.components.CxDialog;
import constellation.ui.components.CxPanel;
import constellation.ui.components.buttons.CxButton;

/**
 * This dialog handles the input of data for all functions 
 * that require it.
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class UserInputDialog<DATA_TYPE> extends CxDialog {
	private static final Class<?>[] STRING_CLASS_ARGS = new Class[] { String.class };
	private final UserInputObserver<DATA_TYPE> observer;
	private final Class<DATA_TYPE> returnType;
	private final DATA_TYPE initialValue;
	private JTextComponent textComp;
	
	/** 
	 * Creates a new input dialog
	 * @param controller the given {@link ApplicationController controller}
	 * @param labelText the given input label text
	 * @param columns the given size of the input field
	 * @param observer the given {@link UserInputObserver user input observer}
	 * @param initialValue the initial value
	 */
	public UserInputDialog( final ApplicationController controller, 
							final String labelText, 
							final int columns,
							final UserInputObserver<DATA_TYPE> observer, 
							final DATA_TYPE initialValue ) {
		this( controller, labelText, 0, columns, observer, initialValue );
	}
	
	/** 
	 * Creates a new input dialog
	 * @param controller the given {@link ApplicationController controller}
	 * @param labelText the given input label text
	 * @param columns the given size of the input field
	 * @param observer the given {@link UserInputObserver user input observer}
	 * @param initialValue the initial value
	 */
	@SuppressWarnings("unchecked")
	public UserInputDialog( final ApplicationController controller, 
							final String labelText, 
							final int rows,
							final int columns,
							final UserInputObserver<DATA_TYPE> observer, 
							final DATA_TYPE initialValue ) {
		super( controller, "Input" );
		
		// capture the observer
		this.observer		= observer;
		this.initialValue	= initialValue;
		this.returnType 	= (Class<DATA_TYPE>)initialValue.getClass();
		
		// setup the dialog box
		super.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		super.setContentPane( createContentPane( labelText, rows, columns ) );
		super.pack();
		super.setLocation( controller.getLowerLeftAnchorPoint( this ) );
		
		// set the initial value
		textComp.setText( initialValue.toString() );
	}
	
	/** 
	 * Returns the current value
	 * @return the current value
	 */
	public DATA_TYPE getCurrentValue() {
		// get the text string
		final String text = textComp.getText();
		
		try {			
			// lookup the constructor for the return type
			final Constructor<DATA_TYPE> constructor = returnType.getConstructor( STRING_CLASS_ARGS );
			
			// create a new instance
			return constructor.newInstance( new Object[] { text } );
		} 
		catch( final Exception cause ) {
			return initialValue;
		}
	}
	
	/** 
	 * Constructs the content pane
	 * @param labelText the given input label text
	 * @param rows the number of rows to display
	 * @param columns the number of columns to display
	 * @return the content pane
	 */
	private JComponent createContentPane( final String labelText, int rows, final int columns ) {
		final CxPanel cp = new CxPanel();
		cp.gbc.fill   = GridBagConstraints.NONE;
		
		// if the row count is zero, use a text field.
		if ( rows < 1 ) {
			cp.gbc.insets = new Insets( 0, 0, 0, 0 );
			cp.attach( 0, 0, new JLabel( labelText ) );
			cp.attach( 1, 0, textComp = new JTextField( columns ) );		
			cp.attach( 2, 0, new CxButton( "Accept", new UserInputSubmitAction() ), GridBagConstraints.SOUTHWEST );	
		}
		
		// otherwise, use a text area
		else {
			cp.gbc.insets = new Insets( 2, 5, 2, 5 );
			cp.attach( 0, 0, new JLabel( labelText ) );
			cp.attach( 0, 1, new JScrollPane( textComp = new JTextArea( rows, columns ) ) );		
			cp.attach( 0, 2, new CxButton( "Accept", new UserInputSubmitAction() ), GridBagConstraints.SOUTHWEST );
		}
		return cp;
	}
	
	/**
	 * Converts the input text to the declared type
	 * @param inputText the given input text
	 * @return the {@link Object typed data}
	 */
	private DATA_TYPE convertData( final String inputText ) {
		try {			
			// lookup the constructor for the return type
			final Constructor<DATA_TYPE> constructor = returnType.getConstructor( STRING_CLASS_ARGS );
			
			// create a new instance
			return constructor.newInstance( new Object[] { inputText } );
		} 
		catch( final Exception cause ) {
			throw new IllegalArgumentException( format( "Unsupported return type - %s", returnType.getName() ), cause );
		}
	}
	
	/**
	 * User Input Submit Action
	 * @author lawrence.daniels@gmail.com
	 */
	private class UserInputSubmitAction implements ActionListener {

		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed( final ActionEvent event ) {
			// get the input text
			final DATA_TYPE typedData = convertData( textComp.getText() );
			
			// notify the observer
			observer.update( controller, typedData );
		}
	}

}
