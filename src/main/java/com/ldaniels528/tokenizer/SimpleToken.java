package com.ldaniels528.tokenizer;

/**
 * Represents a simple {@link Token token} implementation
 */
public class SimpleToken implements Token {  
  private final String content;
  private final int type;
  private final int lineNo;
  private final int start;
  private final int end;

  public SimpleToken( final String content ) {
	  this( content, TEXT, 0, content.length(), 1 );
  }
  
  /**
   * Creates this token instance
   * @param content the content of the token
   * @param type the type of token
   * @param start the starting position of the token within the string
   * @param end the ending position of the token within the string
   * @param lineNo the line number that the token occurred within
   */
  public SimpleToken( String content, int type, int start, int end, int lineNo ) {
    this.content  = content;
    this.type     = type;
    this.start    = start;
    this.end      = end;
    this.lineNo   = lineNo;
  }
  
  /* 
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( Object o ) {
	  // if the object is a string ...
	  if( o instanceof String ) {
		  return content.equals( (String)o );
	  }
	  // if the object is a token ...
	  else if( o instanceof Token ) {
		  final Token token = (Token)o;
		  return content.equals( token.getContent() ) && 
		  		( type == token.getType() ) &&
		  		( start == token.getStart() ) &&
		  		( end == token.getEnd() ) &&
		  		( lineNo == token.lineNumber() );
	  }
	  // dunno what it is ...
	  else return false;
  }
  
  /* 
   * (non-Javadoc)
   * @see java.lang.String#equalsIgnoreCase(java.lang.String)
   */
  public boolean equalsIgnoreCase( String s ) {
	  return content.equalsIgnoreCase( s );
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#getContent()
   */
  public String getContent() {
    return this.content;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#getEnd()
   */
  public int getEnd() {
    return this.end;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#getLength()
   */
  public int getLength() {
    return content.length();
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#getStart()
   */
  public int getStart() {
    return start;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#getType()
   */
  public int getType() {
    return type;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isComment()
   */
  public boolean isComment() {
    return type == COMMENTS;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isDelimiter()
   */
  public boolean isDelimiter() {
    return type == DELIMITER;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isEOL()
   */
  public boolean isEOL() {
    return type == EOL;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isSequence()
   */
  public boolean isSequence() {
    return type == SEQUENCE;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isNumeric()
   */
  public boolean isNumeric() {
    return type == NUMERIC;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isOperator()
   */
  public boolean isOperator() {
    return type == OPERATOR;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isQuotedText()
   */
  public boolean isQuotedText() {
    return type == QUOTED_TEXT;
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#isText()
   */
  public boolean isText() {
    return ( type == TEXT ) || ( type == QUOTED_TEXT );
  }

  /* 
   * (non-Javadoc)
   * @see jbasic.util.tokenizer.Token#lineNo()
   */
  public int lineNumber() {
    return lineNo;
  }

  /* 
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return content;
  }

}
