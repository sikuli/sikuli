package org.sikuli.ide.indentation;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sikuli.script.Debug;

/**
 * A python state is an object that you feed with chunks of text from a python
 * document, in order to determine its logical line structure.
 * <p>
 * The text chunks must be fed in the same order in which they appear in the
 * document, and there must not be any gaps between two chunks. Text chunks can
 * be any number of lines and can also be incomplete lines.
 * <p>
 * After each chunk, you can get the current state of the python document seen
 * so far. The state includes the nesting level of string literals and
 * parenthesis, the last physical and logical line seen, whether the last
 * physical and logical line is complete, and the indentation of the last
 * physical and logical line.
 * <p>
 * See <a href=
 * "http://docs.python.org/reference/lexical_analysis.html#line-structure">line
 * structure</a> in the <a href="http://docs.python.org/reference/">python
 * language reference</a> for information about physical and logical lines.
 */
public class PythonState {

   public static final int DEFAULT_TABSIZE = 4;

   private static final Pattern START_DELIMITER = Pattern.compile(
         "(\"\"\"|\"(?!\")|['(\\[{#]|\\\\?(?:\r|\n|\r\n)|\\\\.)",
         Pattern.MULTILINE);
   private static final Pattern DELIMITER = Pattern
         .compile("(\"\"\"|\"(?!\")|['()\\[\\]{}#]|\\\\?(?:\r|\n|\r\n)|\\\\.)");
   private static final Pattern SINGLE_QUOTE_DELIMITER = Pattern
         .compile("('|\\\\?(?:\r|\n|\r\n)|\\\\.)");
   private static final Pattern DOUBLE_QUOTE_DELIMITER = Pattern
         .compile("(\"|\\\\?(?:\r|\n|\r\n)|\\\\.)");
   private static final Pattern LONG_STRING_DELIMITER = Pattern
         .compile("(\"\"\"|\\\\?(?:\r|\n|\r\n)|\\\\.)");
   private static final Pattern END_OF_LINE = Pattern.compile("(?:\n|\r\n?)");

   public static enum State{
      DEFAULT, IN_SINGLE_QUOTED_STRING, IN_DOUBLE_QUOTED_STRING, IN_LONG_STRING, IN_PARENTHESIS, IN_COMMENT
   };

   private int tabsize = DEFAULT_TABSIZE;

   private Stack<PythonState.State> state;

   private Matcher startDelimiterMatcher = START_DELIMITER.matcher("")
         .useAnchoringBounds(true);
   private Matcher delimiterMatcher = DELIMITER.matcher("").useAnchoringBounds(
         true);
   private Matcher singleQuoteMatcher = SINGLE_QUOTE_DELIMITER.matcher("")
         .useAnchoringBounds(true);
   private Matcher doubleQuoteMatcher = DOUBLE_QUOTE_DELIMITER.matcher("")
         .useAnchoringBounds(true);
   private Matcher longStringMatcher = LONG_STRING_DELIMITER.matcher("")
         .useAnchoringBounds(true);
   private Matcher endOfLineMatcher = END_OF_LINE.matcher("")
         .useAnchoringBounds(true);

   private StringBuilder physicalLine;
   private StringBuilder logicalLine;
   private StringBuilder unmatchedChunk;

   private boolean completePhysicalLine;
   private boolean completeLogicalLine;
   private boolean explicitJoining;
   private int explicitJoinOffset;

   private int physicalLineNumber;
   private int logicalLineNumber;
   private int logicalLinePhysicalStartLineNumber;

   public PythonState(){
      state = new Stack<PythonState.State>();
      state.push(State.DEFAULT);
      physicalLine = new StringBuilder();
      logicalLine = new StringBuilder();
      unmatchedChunk = new StringBuilder();
      reset();
   }

   /**
    * Sets the number of whitespace columns that equals a single tab. This is
    * used to calculate the indentation of lines.
    * 
    * @param tabsize
    *           the number of whitespace columns that equals a single tab
    */
   public void setTabSize(int tabsize){
      this.tabsize = tabsize;
   }

   /**
    * Returns the number of whitespace columns equalling a single tab that is
    * used to calculate the indentation of lines.
    * 
    * @return the number of whitespace columns that equals a single tab
    */
   public int getTabSize(){
      return tabsize;
   }

   /**
    * Resets the state of this object. The new state is equivalent to an empty
    * document.
    */
   public void reset(){
      state.setSize(1);
      physicalLine.setLength(0);
      logicalLine.setLength(0);
      unmatchedChunk.setLength(0);
      completePhysicalLine = false;
      completeLogicalLine = false;
      explicitJoining = false;
      explicitJoinOffset = 0;
      physicalLineNumber = 0;
      logicalLineNumber = 0;
      logicalLinePhysicalStartLineNumber = 0;
   }

   private boolean isEOL(String s){
      return s.equals("\r") || s.equals("\n") || s.equals("\r\n");
   }

   private boolean isEscapedEOL(String s){
      return s.length() >= 2 && s.charAt(0) == '\\' && isEOL(s.substring(1));
   }

   private boolean isEscapedChar(String s){
      return s.length() == 2 && s.charAt(0) == '\\';
   }

   /**
    * Feeds a chunk of text to this object. The text will be (virtually)
    * appended to any text that was fed to this object earlier since the last
    * reset.
    * 
    * @param newChunk
    *           a new chunk of text
    */
   public void update(String newChunk){
      unmatchedChunk.append(newChunk);
      delimiterMatcher.reset(unmatchedChunk);
      startDelimiterMatcher.reset(unmatchedChunk);
      singleQuoteMatcher.reset(unmatchedChunk);
      doubleQuoteMatcher.reset(unmatchedChunk);
      longStringMatcher.reset(unmatchedChunk);
      endOfLineMatcher.reset(unmatchedChunk);
      int i = 0;
      int j = 0;
      String m = null;
      SCAN: while( i < unmatchedChunk.length() ){
         Debug.log(9, "%s: [%s]", state.peek().name(),
               unmatchedChunk.substring(i));
         // more input to match
         if( completePhysicalLine ){
            physicalLine.setLength(0);
            completePhysicalLine = false;
            physicalLineNumber++;
         }
         if( completeLogicalLine ){
            logicalLine.setLength(0);
            completeLogicalLine = false;
            logicalLineNumber++;
            logicalLinePhysicalStartLineNumber = physicalLineNumber;
         }
         explicitJoining = false;
         switch( state.peek() ){
         case DEFAULT:
            startDelimiterMatcher.region(i, unmatchedChunk.length());
            if( startDelimiterMatcher.find() ){
               m = startDelimiterMatcher.group(1);
               if( m.equals("'") ){
                  state.push(State.IN_SINGLE_QUOTED_STRING);
               }else if( m.equals("\"") ){
                  state.push(State.IN_DOUBLE_QUOTED_STRING);
               }else if( m.equals("\"\"\"") ){
                  state.push(State.IN_LONG_STRING);
               }else if( m.equals("(") || m.equals("[") || m.equals("{") ){
                  state.push(State.IN_PARENTHESIS);
               }else if( m.equals("#") ){
                  state.push(State.IN_COMMENT);
               }else if( isEOL(m) ){
                  completePhysicalLine = true;
               }else if( isEscapedEOL(m) ){
                  completePhysicalLine = true;
                  explicitJoining = true;
               }else if( isEscapedChar(m) ){
                  // skip
               }else{
                  throw new Error("unexpected match \"" + m + "\"");
               }
               j = i;
               i = startDelimiterMatcher.end(1);
            }else{
               break SCAN;
            }
            break;
         case IN_PARENTHESIS:
            delimiterMatcher.region(i, unmatchedChunk.length());
            if( delimiterMatcher.find() ){
               m = delimiterMatcher.group(1);
               if( m.equals("'") ){
                  state.push(State.IN_SINGLE_QUOTED_STRING);
               }else if( m.equals("\"") ){
                  state.push(State.IN_DOUBLE_QUOTED_STRING);
               }else if( m.equals("\"\"\"") ){
                  state.push(State.IN_LONG_STRING);
               }else if( m.equals("(") || m.equals("[") || m.equals("{") ){
                  state.push(State.IN_PARENTHESIS);
               }else if( m.equals(")") || m.equals("]") || m.equals("}") ){
                  state.pop();
               }else if( m.equals("#") ){
                  state.push(State.IN_COMMENT);
               }else if( isEOL(m) ){
                  completePhysicalLine = true;
               }else if( isEscapedEOL(m) ){
                  completePhysicalLine = true;
                  explicitJoining = true;
               }else if( isEscapedChar(m) ){
                  // skip
               }else{
                  throw new Error("unexpected match");
               }
               j = i;
               i = delimiterMatcher.end(1);
            }else{
               break SCAN;
            }
            break;
         case IN_SINGLE_QUOTED_STRING:
            singleQuoteMatcher.region(i, unmatchedChunk.length());
            if( singleQuoteMatcher.find() ){
               m = singleQuoteMatcher.group(1);
               if( m.equals("'") ){
                  state.pop();
               }else if( isEOL(m) ){
                  completePhysicalLine = true;
               }else if( isEscapedEOL(m) ){
                  completePhysicalLine = true;
                  explicitJoining = true;
               }else if( isEscapedChar(m) ){
                  // skip
               }else{
                  throw new Error("unexpected match");
               }
               j = i;
               i = singleQuoteMatcher.end();
            }else{
               break SCAN;
            }
            break;
         case IN_DOUBLE_QUOTED_STRING:
            doubleQuoteMatcher.region(i, unmatchedChunk.length());
            if( doubleQuoteMatcher.find() ){
               m = doubleQuoteMatcher.group(1);
               if( m.equals("\"") ){
                  state.pop();
               }else if( isEOL(m) ){
                  completePhysicalLine = true;
               }else if( isEscapedEOL(m) ){
                  completePhysicalLine = true;
                  explicitJoining = true;
               }else if( isEscapedChar(m) ){
                  // skip
               }else{
                  throw new Error("unexpected match");
               }
               j = i;
               i = doubleQuoteMatcher.end();
            }else{
               break SCAN;
            }
            break;
         case IN_LONG_STRING:
            longStringMatcher.region(i, unmatchedChunk.length());
            if( longStringMatcher.find() ){
               m = longStringMatcher.group(1);
               if( m.equals("\"\"\"") ){
                  state.pop();
               }else if( isEOL(m) ){
                  completePhysicalLine = true;
               }else if( isEscapedEOL(m) ){
                  completePhysicalLine = true;
                  explicitJoining = true;
               }else if( isEscapedChar(m) ){
                  // skip
               }else{
                  throw new Error("unexpected match");
               }
               j = i;
               i = longStringMatcher.end();
            }else{
               break SCAN;
            }
            break;
         case IN_COMMENT:
            endOfLineMatcher.region(i, unmatchedChunk.length());
            if( endOfLineMatcher.find() ){
               m = endOfLineMatcher.group();
               state.pop();
               completePhysicalLine = true;
               j = i;
               i = endOfLineMatcher.end();
            }else{
               break SCAN;
            }
            break;
         }
         Debug.log(9, "matcher=[%s]", m);
         physicalLine.append(unmatchedChunk
               .substring(j + explicitJoinOffset, i));
         if( explicitJoining ){
            // delete backslash-EOL, leave text after previous match in buffer
            // and wait for more input
            unmatchedChunk.delete(i - m.length(), i);
            logicalLine.append(unmatchedChunk.substring(j + explicitJoinOffset,
                  i - m.length()));
            explicitJoinOffset = i - j - m.length();
            completeLogicalLine = false;
            if( i - m.length() == unmatchedChunk.length() ){
               // no further match is possible until there is new input
               i = j;
               break SCAN;
            }
            i = j;
         }else{
            logicalLine.append(unmatchedChunk.substring(j + explicitJoinOffset,
                  i));
            completeLogicalLine = completePhysicalLine && inDefaultState();
            explicitJoinOffset = 0;
         }
      }
      unmatchedChunk.delete(0, i);
      Debug.log(9, "%s: unmatched: [%s]", state.peek().name(), unmatchedChunk);
   }

   /**
    * Returns the state of the python document seen so far.
    * 
    * @return the current state
    */
   public PythonState.State getState(){
      return state.peek();
   }

   /**
    * Returns true if the state of the document seen by this object is not
    * inside any parenthesis, string or comment.
    * 
    * @return true if the current state is the default state
    */
   public boolean inDefaultState(){
      return state.peek() == State.DEFAULT;
   }

   /**
    * Returns true if the state of the document seen by this object is inside a
    * parenthesis (including square brackets and curly braces).
    * 
    * @return true if the current state is inside a parenthesis
    */
   public boolean inParenthesis(){
      return state.peek() == State.IN_PARENTHESIS;
   }

   /**
    * Returns true if the state of the document seen by this object is inside a
    * string (short string or long string).
    * 
    * @return true if the current state is inside a string
    */
   public boolean inString(){
      switch( state.peek() ){
      case IN_DOUBLE_QUOTED_STRING:
      case IN_SINGLE_QUOTED_STRING:
      case IN_LONG_STRING:
         return true;
      }
      return false;
   }

   /**
    * Returns true if the state of the document seen by this object is inside a
    * long string.
    * 
    * @return true if the current state is inside a long string
    */
   public boolean inLongString(){
      return state.peek() == State.IN_LONG_STRING;
   }

   /**
    * Returns true if the state of the document seen by this object is inside a
    * comment.
    * 
    * @return true if the current state is inside a comment
    */
   public boolean inComment(){
      return state.peek() == State.IN_COMMENT;
   }

   /**
    * Returns the nesting level of parentheses and strings that the state of the
    * document seen by this object is in. The nesting level in the default state
    * is 0.
    * <p>
    * Note that parentheses can be nested at any depth, but only one level of
    * string can be nested inside the innermost parentheses because anything
    * inside a string is not interpreted.
    * 
    * @return the nesting level of parentheses and strings of the current state
    */
   public int getDepth(){
      return state.size() - 1;
   }

   /**
    * Returns the last physical line seen by this object, including the
    * terminating end-of-line sequence. If the last line seen by this object is
    * not a complete physical line, the return value is undefined.
    * 
    * @return the last complete physical line seen by this object
    */
   public String getLastPhysicalLine(){
      return physicalLine.toString();
   }

   /**
    * Returns the last logical line seen by this object, including the
    * terminating end-of-line sequence. If the input seen by this object does
    * not end with a complete logical line, the return value is guaranteed to
    * include all complete physical lines seen of which the logical line is
    * comprised. If explicit line joining has occurred, any escaped end-of-line
    * sequence is not included in the logical line.
    * 
    * @return the last complete logical line seen seen by this instance
    */
   public String getLastLogicalLine(){
      return logicalLine.toString();
   }

   /**
    * Returns the physical line number of the last physical line seen by this
    * object.
    * 
    * @return the physical line number of the line returned by
    *         {@link #getLastPhysicalLine()} (0-based)
    */
   public int getPhysicalLineNumber(){
      return physicalLineNumber;
   }

   /**
    * Returns the logical line number of the last logical line seen by this
    * object.
    * 
    * @return the logical line number of the line returned by
    *         {@link #getLastLogicalLine()} (0-based)
    */
   public int getLogicalLineNumber(){
      return logicalLineNumber;
   }

   /**
    * Returns the physical line number of the first physical line in the last
    * logical line seen by this object.
    * 
    * @return the physical line number of the first physical line in the logical
    *         line returned by {@link #getLastLogicalLine()} (0-based)
    */
   public int getLogicalLinePhysicalStartLineNumber(){
      return logicalLinePhysicalStartLineNumber;
   }

   /**
    * Returns whether the last physical line seen by this object is complete. A
    * physical line is complete if it is terminated by an end-of-line sequence.
    * 
    * @return true if the line returned by {@link #getLastPhysicalLine()} is
    *         complete
    */
   public boolean isPhysicalLineComplete(){
      return completePhysicalLine;
   }

   /**
    * Returns whether the last logical line seen by this object is complete. A
    * logical line is complete if all of the following are true:
    * <ul>
    * <li>the physical lines that it is comprised of are complete (i.e. it is
    * terminated by an end-of-line sequence)
    * <li>it does not end with a physical line that is explicitly joined with
    * the following line (i.e. the final end-of-line sequence is not preceded by
    * a backslash, unless the backslash is part of a comment)
    * <li>it does not contain any open parenthesis or string delimiter without
    * the matching closing parenthesis or string delimiter
    * </ul>
    * 
    * @return true if the line returned by {@link #getLastLogicalLine()} is
    *         complete
    */
   public boolean isLogicalLineComplete(){
      return completeLogicalLine;
   }

   /**
    * Returns whether the last physical line seen by this object is explicitly
    * joined with the following line, i.e. whether its end-of-line sequence is
    * escaped with a backslash and the backslash is not inside a comment. If the
    * last physical line seen is not complete, the return value is undefined.
    * 
    * @return true if the last complete physical line is explicitly joined with
    *         the following line
    */
   public boolean isExplicitLineJoining(){
      return explicitJoining;
   }

   private int getLineIndentation(CharSequence line){
      int indentation = 0;
      for( int i = 0; i < line.length(); i++ ){
         char c = line.charAt(i);
         if( c == ' ' ){
            indentation++;
         }else if( c == '\t' ){
            indentation += tabsize;
         }else{
            break;
         }
      }
      return indentation;
   }

   /**
    * Returns the indentation (in columns of whitespace) of the last physical
    * line seen by this object.
    * <p>
    * Any tab characters in the leading whitespace of the line are counted as
    * the equivalent number of blank characters.
    * 
    * @return the indentation of the last physical line
    */
   public int getLastPhysicalLineIndentation(){
      return getLineIndentation(physicalLine);
   }

   /**
    * Returns the indentation (in columns of whitespace) of the last logical
    * line seen by this object. This is the indentation of the physical line
    * which is the first line in the logical line.
    * <p>
    * Any tab characters in the leading whitespace of the line are counted as
    * the equivalent number of blank characters.
    * 
    * @return the indentation of the last logical line
    */
   public int getLastLogicalLineIndentation(){
      return getLineIndentation(logicalLine);
   }
}
