package constellation.tools.sdk.math.geometric.expressions;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.ldaniels528.tokenizer.Token;
import com.ldaniels528.tokenizer.TokenIterator;
import com.ldaniels528.tokenizer.Tokenizer;
import com.ldaniels528.tokenizer.TokenizerContext;

/**
 * Constellation Expression Parser
 * @author lawrence.daniels@gmail.com
 */
public class ExpressionParser {
	private static final ExpressionParser instance = new ExpressionParser();
	private final Logger logger = Logger.getLogger( getClass() );
	private final Tokenizer tokenizer;
	
	/**
	 * Default constructor
	 */
	private ExpressionParser() {
		this.tokenizer = new Tokenizer();
	}
	
	/**
	 * Returns the singleton instance of the class
	 * @return the singleton instance of the class
	 */
	public static ExpressionParser getInstance() {
		return instance;
	}
	
	/**
	 * Parses the given data string
	 * @param dataString the given data string
	 * @return the the collection of limit expressions
	 */
	public Collection<LimitExpression> parse( final String dataString ) {
		final Collection<LimitExpression> expressions = new LinkedList<LimitExpression>();
		
		// parse the data string
		final TokenizerContext context = tokenizer.parse( dataString );
		
		// get the iteration of tokens
		final TokenIterator tokens = tokenizer.nextTokens( context );
		
		double start = 0;
		double end = 0;
		
		// evaluate each token
		while( tokens.hasNext() ) {
			// get the next token
			final String token = tokens.next();
			
			// is the token the start of a limit?
			if( token.equals( "[" ) ) {
				// get all elements of the range
				final TokenIterator it = tokens.upto( "]", true );
				if( it.elementsRemaining() != 3 )
					throw new IllegalArgumentException( String.format( "Invalid limit at %d", it.nextToken().getStart() ) );
				
				// get the start and end value
				start = Double.parseDouble( it.next() );
				mandate( it, ":" );
				end = Double.parseDouble( it.next() );
				
				logger.debug( String.format( "limit: %3.2f .. %3.2f", start, end ) );
			}
			
			// check for end of statement
			else if( token.equals( ";" ) ) {
				start = 0;
				end = 0;
			}
		}
		
		return expressions;
	}
	
	private void mandate( final TokenIterator it, final String expected ) {
		// there must be at least 1 more element
		if( !it.hasNext() ) {
			throw new IllegalArgumentException( "Unexpected end of line" );
		}
		
		// get the next token
		final Token token = it.nextToken();
		
		// must be the expected token
		if( !expected.equals( token.getContent() ) ) {
			throw new IllegalArgumentException( String.format( "Invalid limit at %d", token.getStart() ) );
		}
	}

}
