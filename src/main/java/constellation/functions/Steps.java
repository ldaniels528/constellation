package constellation.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the collection of steps required to complete a function
 * @author lawrence.daniels@gmail.com
 */
public class Steps {
	// step constants
	public static final int STEP_1 = 0;
	public static final int STEP_2 = 1;
	public static final int STEP_3 = 2;
	public static final int STEP_4 = 3;	
	public static final int STEP_5 = 4;
	public static final int STEP_6 = 5;
	public static final int STEP_7 = 6;
	public static final int STEP_8 = 7;
	
	// internal fields
	private final List<Step> steps;
	private int position;
	
	/**
	 * Default Constructor
	 */
	public Steps() {
		this.steps = new ArrayList<Step>();
		this.position = 0;
	}
	
	/**
	 * Creates a new collection of steps (all defaulting to an 
	 * {@link InputType input type} of {@link InputType#MOUSE mouse})
	 * @param instructions the given instructions
	 */
	public Steps( final String ... instructions ) {
		this.steps = new ArrayList<Step>( instructions.length );
		this.position = 0;
		
		// add the steps
		for( final String instructionText : instructions ) {
			steps.add( new Step( instructionText ) );
		}
	}
	
	/**
	 * Returns the current step
	 * @return the current step
	 */
	public Step current() {
		return steps.get( position );
	}
	
	/**
	 * Returns the index of the current step
	 * @return the index of the current step
	 */
	public int currentIndex() {
		return position;
	}
	
	/**
	 * Appends a step to the collection of steps
	 * @param step the given {@link Step step}
	 * @return a {@link Steps reference} to the calling instance
	 */
	public Steps append( final Step step ) {
		steps.add( step );
		return this;
	}
	
	/**
	 * Appends a step to the collection of steps
	 * @param instructionText the given instruction text
	 * @return a {@link Steps reference} to the calling instance
	 */
	public Steps append( final String instructionText ) {
		steps.add( new Step( instructionText ) );
		return this;
	}
	
	/**
	 * Returns a step found at the given index
	 * @param index the given index
	 * @return the {@link Step step}
	 */
	public Step get( final int index ) {
		return steps.get( index );
	}
	
	/**
	 * Advances to the next step
	 * @return true, if the last step has been reached
	 */
	public boolean next() {
		final boolean last = ( ++position >= steps.size() );
		if( last ) {
			reset();
		}
		return last;
	}

	/**
	 * Resets the pointer to reference the
	 * {@link #STEP_1 initial step}
	 */
	public void reset() {
		position = STEP_1;
	}
	
	/**
	 * Sets the position of the current index
	 * @param index the given index
	 */
	public void setPosition( final int index ) {
		this.position = index;
	}
	
	/**
	 * Returns the number of steps
	 * @return the number of steps
	 */
	public int size() {
		return steps.size();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "Step %d of %d: %s", (position+1), size(), current().getInstructionText() );
	}
	
	/**
	 * Represents a single step of a function
	 * @author lawrence.daniels@gmail.com
	 *
	 */
	public static class Step {
		private final String instructionText;

		/**
		 * Creates a new step with a default {@link InputType input type} 
		 * of {@link InputType#MOUSE mouse}.
		 * @param instructionText the given instructions for the step
		 */
		public Step( final String instructionText ) {
			this.instructionText	= instructionText;
		}

		/**
		 * @return the instructionText
		 */
		public String getInstructionText() {
			return instructionText;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return instructionText;
		}
	}
}
