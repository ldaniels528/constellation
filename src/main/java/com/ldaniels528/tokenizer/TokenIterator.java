package com.ldaniels528.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a Token Enumeration
 */
public class TokenIterator {
  private final List<String> stringList;
  private final List<Token> tokenList;
  private int position;

  /**
   * Creates a Token Iterator instance using the given collection
   * to iterate over.
   * @param collection the given {@link Collection collection}
   */
  public TokenIterator( final Collection<Token> collection ) {	  
	  this.tokenList	= new ArrayList<Token>( collection );
	  this.stringList	= toStringList( collection );	  
	  this.position 	= 0;
  }

  /**
   * Tests the existence of the given token in this iteration
   * @param token the given {@link String token}
   * @return true, if the given token exists in this iteration
   */
  public boolean contains( final String token ) {
	  final List subList = stringList.subList( position, stringList.size() );
	  return subList.contains( token );
  }
  
  /**
   * @return the number of elements remaining in this iterator
   */
  public int elementsRemaining() {
	  return stringList.size() - position;
  }

  /**
   * Returns <tt>true</tt> if the iteration has previous elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   * @return <tt>true</tt> if the iterator has previous elements.
   */
  public boolean hasPrevious() {
    return position > 0;
  }
  
  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
    return position < stringList.size();
  }
  
  /**
   * Returns the last element in the iteration.  Calling this method
   * repeatedly until the {@link #hasNext()} method returns false will
   * return each element in the underlying collection exactly once.
   * @return the next element in the iteration.
   * @exception java.util.NoSuchElementException iteration has no more elements.
   */
  public String previous() {
	  // if we're at the end of the stream, throw exception
	  if( !hasPrevious() ) throw new NoSuchElementException();
	  
	  // return the next element
	  return stringList.get( --position );
  }
  
  /**
   * Returns the next element in the iteration.  Calling this method
   * repeatedly until the {@link #hasNext()} method returns false will
   * return each element in the underlying collection exactly once.
   * @return the next element in the iteration.
   * @exception java.util.NoSuchElementException iteration has no more elements.
   */
  public Token previousToken() {
	  // if we're at the end of the stream, throw exception
	  if( !hasNext() ) throw new NoSuchElementException();
	  
	  // return the next element
	  return tokenList.get( --position );
  }

  /**
   * Returns the next element in the iteration.  Calling this method
   * repeatedly until the {@link #hasNext()} method returns false will
   * return each element in the underlying collection exactly once.
   * @return the next element in the iteration.
   * @exception java.util.NoSuchElementException iteration has no more elements.
   */
  public String next() {
	  // if we're at the end of the stream, throw exception
	  if( !hasNext() ) throw new NoSuchElementException();
	  
	  // return the next element
	  return stringList.get( position++ );
  }
  
  /**
   * Returns the next element in the iteration.  Calling this method
   * repeatedly until the {@link #hasNext()} method returns false will
   * return each element in the underlying collection exactly once.
   * @return the next element in the iteration.
   * @exception java.util.NoSuchElementException iteration has no more elements.
   */
  public Token nextToken() {
	  // if we're at the end of the stream, throw exception
	  if( !hasNext() ) throw new NoSuchElementException();
	  
	  // return the next element
	  return tokenList.get( position++ );
  }

  /**
   * Finds the next instance of the given token from the given position
   * in the iteration
   * @param token the given token
   * @return the index of the token within the collection or -1 if not found
   */
  public int nextIndexOf( final String token ) {
	  for( int i = position; i < stringList.size(); i++ ) {
		  if( stringList.get( i ).equals( token ) ) return i;
	  }
	  return -1;
  }

  /**
   * Allows the ability to look ahead without removing the next item from
   * this iterator.
   * @return the next element in the iteration.
   */
  public String peekAtNext() {
	  return peekAtNext( 0 );
  }

  /**
   * Allows the ability to look ahead without removing the next item from
   * this iterator.
   * @return the next element in the iteration.
   */
  public String peekAtNext( final int offset ) {
	  final int index = position + offset;
	  return ( index < stringList.size() ) ? stringList.get( index ) : null;
  }

  /**
   * Allows the ability to look ahead without removing the next item from
   * this iterator.
   * @return the next {@link Token token} in the iteration.
   */
  public Token peekAtNextToken() {
	  return peekAtNextToken( 0 );
  }

  /**
   * Allows the ability to look ahead without removing the next item from
   * this iterator.
   * @return the next {@link Token token} in the iteration.
   */
  public Token peekAtNextToken( final int offset ) {
	  final int index = position + offset;
	  return ( index < tokenList.size() ) ? tokenList.get( index ) : null;
  }
  
  /**
   * Removes from the underlying collection the last element returned by the iterator
   * @see java.util.Iterator#remove()
   */
  public void remove() {
	  remove( position );
  }
  
  public void remove( int index ) {
	  stringList.remove( index );
	  tokenList.remove( index );
  }

  /**
   * Removes the first element in this iterator
   */
  public void removeFist() {
	  if( !stringList.isEmpty() ) {
		  remove( 0 );
	  }
  }

  
  /**
   * Removes the last element in this iterator
   */
  public void removeLast() {
	  if( !stringList.isEmpty() ) {
		  remove( stringList.size() - 1 );
	  }
  }

  /**
   * Splits the token iterator into a collection of
   * token iterators tokenized by the given delimiter token
   * @param delimiterToken the given delimiter token
   * @return a {@link List collection} of {@link TokenIterator token iterators}
   */
  public List<TokenIterator> split( final String delimiterToken ) {
	  // create a container for returning the iterators
	  final List<TokenIterator> iteratorList = new LinkedList<TokenIterator>();
	  
	  // create a container for the stringList
	  List<Token> tokens = new LinkedList<Token>();
	  
	  // iterate from the current position, build new iterators
	  for( int i = position; i < tokenList.size(); i++ ) {
		  // get the token
		  final Token token = tokenList.get( i );
		  final String tokenString = token.getContent();
		  
		  // is the token a delimiter
		  if( tokenString.equals( delimiterToken ) ) {
			  iteratorList.add( new TokenIterator( tokens ) );
			  tokens = new LinkedList<Token>();
		  }
		  else tokens.add( token );
	  }
	  
	  // add the list fragment
	  if( !tokens.isEmpty() ) 
		  iteratorList.add( new TokenIterator( tokens ) );
	  
	  return iteratorList;
  }
  
  /**
   * Un-gets the last token 
   */
  public void unGet() {
	  if( position > 0 ) position--;
  }

  /**
   * Creates a sub-list containing all stringList upto the first instance
   * of the given token.
   * @param token the given limit token
   * @param movePointer indicates whether to move the iteration pointer
   * @return a {@link TokenIterator sub-list} containing all stringList upto the first instance
   * of the given token.
   */
  public TokenIterator upto( final String token, final boolean movePointer ) {	  
	  // determine the limit 
	  final int limit = contains( token ) ? nextIndexOf( token ) : stringList.size();
	  
	  // create a container for the sublist
	  final Collection<Token> collection = new ArrayList<Token>( limit - position );
	  
	  // gather all stringList from the current position to the limit
	  collection.addAll( tokenList.subList( position, limit ) );
	  
	  // move the current position pointer?
	  if( movePointer ) position = limit;
	  
	  // return a new token iterator
	  return new TokenIterator( collection );    
  }
  
  /**
   * Creates a sub-list containing all stringList upto the first instance
   * of the given token.
   * @param stringList the given limit token
   * @return a {@link TokenIterator sub-list} containing all stringList upto the first instance
   * of the given token.
   */
  public TokenIterator upto( final String ... token ) {
	  final Collection<Token> collection = new LinkedList<Token>();
	  final Set<String> tokenSet = new HashSet<String>( Arrays.asList( token ) );
	  
	  // while there is a next token ...
	  while( hasNext() ) {
		  // if the token is one of the limit stringList, return the new iterator
		  if( tokenSet.contains( peekAtNext() ) ) 
			  return new TokenIterator( collection );
		  
		  // otherwise add the token to the collection
		  collection.add( nextToken() );
	  }
	  
	  // return a new token iterator
	  return new TokenIterator( collection );    
  }

private List<String> toStringList( final Collection<Token> tokens ) {
	  final List<String> list = new ArrayList<String>( tokens.size() );
	  for( final Token token : tokens ) {
		  list.add( token.getContent() );
	  }
	  return list;
  }

/**
   * @return a string representation of the object.
   */
  public String toString() {
	  final StringBuffer sb = new StringBuffer( 100 );
	  int n = 0;
	  for( int p = position; p < stringList.size(); p++ ) {
		  if( n++ > 0 ) sb.append( ' ' );
		  sb.append( stringList.get( p ) );
	  }	  	  
	  return sb.toString();
  }

}
