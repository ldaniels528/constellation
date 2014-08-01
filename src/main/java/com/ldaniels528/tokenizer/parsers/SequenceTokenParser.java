package com.ldaniels528.tokenizer.parsers;

import java.util.Collection;
import java.util.LinkedList;

import com.ldaniels528.tokenizer.SimpleToken;
import com.ldaniels528.tokenizer.Token;
import com.ldaniels528.tokenizer.TokenParser;
import com.ldaniels528.tokenizer.TokenizerContext;

/**
 * Represents a Token Parser that recognizes specific
 * sequences of tokens (e.g. "[" ... "]")
 * @author lawrence.daniels@gmail.com
 */
public class SequenceTokenParser implements TokenParser {
	private final Collection<TokenSequence> sequences;
	
	/**
	 * Default Constructor
	 */
	public SequenceTokenParser() {
		this.sequences = new LinkedList<TokenSequence>();
	}
	
	/**
	 * Adds the given token sequence to this token parser
	 * @param sequence the given {@link TokenSequence token sequence}
	 */
	public void add( TokenSequence sequence ) {
		sequences.add( sequence );
	}

	/**
	 * Checks for token sequence i.e.  <% ... %>
	 * @param ctx the given {@link TokenizerContext tokenizer context}
	 * @return a {@link Token token} representing the {@link TokenSequence token sequence}
	 */
	public Token getToken( final TokenizerContext ctx ) {
	    for( final TokenSequence sequence : sequences ) {
	    		// get the length of the start and end sequences
	        final int slen = sequence.getStart().length();
	        final int elen = sequence.getEnd().length();

	        // look for starting sequence
	        if( ( ctx.position + slen - 1 < ctx.exprCh.length ) && ctx.expr.substring( ctx.position, ctx.position + slen ).equals( sequence.getStart() ) ) {
	          final int start  = ctx.position;
	          while( ( ctx.position + elen < ctx.exprCh.length ) && !ctx.expr.substring( ctx.position, ctx.position + elen ).equals( sequence.getEnd() ) ) ctx.position++;
	          ctx.position += slen;
	          
	          // make sure the current position does not exceed the length of the data
	          if( ctx.position > ctx.exprCh.length ) ctx.position = ctx.exprCh.length;
	          
	          // return the token
	          return new SimpleToken(
	            ctx.expr.substring( start, ctx.position ),
	            sequence.getTokenType(),
	            start,
	            ctx.position,
	            ctx.lineNo
	          );
	        }
	      }
	      return null;
	}

}
