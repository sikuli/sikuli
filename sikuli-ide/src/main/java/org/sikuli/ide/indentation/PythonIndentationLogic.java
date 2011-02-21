package org.sikuli.ide.indentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements the logic of how indentation <i>should</i> change from one line to
 * the next in Python code.
 */
public class PythonIndentationLogic implements IndentationLogic {

   public static final int PARENTHESIS_INDENTATION_TABSTOPS = 2;
   public static final int NESTED_PARENTHESIS_INDENTATION_TABSTOPS = 1;
   public static final int LONG_STRING_INDENTATION_COLUMNS = 3;
   public static final int EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS = 2;

   private static final Pattern ENDS_WITH_COLON = Pattern.compile(
         "^[^#]*:\\s*(?:#.*)?$", Pattern.DOTALL);

   private static final Pattern UNINDENT_NEXT_LINE_STATEMENT = Pattern
         .compile("^\\s*(?:break|continue|pass|raise|return)\\b");

   private static final Pattern UNINDENT_LAST_LINE_STATEMENT = Pattern
         .compile("^\\s*(?:elif|else|except|finally)\\b");

   private PythonState pythonState;
   private Matcher endsWithColonMatcher = ENDS_WITH_COLON.matcher("");
   private Matcher unindentNextLineStatementMatcher = UNINDENT_NEXT_LINE_STATEMENT
         .matcher("");
   private Matcher unindentLastLineStatementMatcher = UNINDENT_LAST_LINE_STATEMENT
         .matcher("");

   public boolean endsWithColon(String logicalLine){
      // not thread safe!
      return endsWithColonMatcher.reset(logicalLine).matches();
   }

   public boolean isUnindentNextLineStatement(String logicalLine){
      // not thread safe!
      return unindentNextLineStatementMatcher.reset(logicalLine).find();
   }

   public boolean isUnindentLastLineStatement(String logicalLine){
      // not thread safe!
      return unindentLastLineStatementMatcher.reset(logicalLine).find();
   }

   public PythonIndentationLogic(){
      pythonState = new PythonState();
   }

   @Override
   public void setTabWidth(int tabwidth){
      pythonState.setTabSize(tabwidth);
   }

   @Override
   public int getTabWidth(){
      return pythonState.getTabSize();
   }

   @Override
   public void reset(){
      pythonState.reset();
   }

   @Override
   public void addText(String text){
      pythonState.update(text);
   }

   @Override
   public int getLastLineNumber(){
      return pythonState.getPhysicalLineNumber();
   }

   @Override
   public int shouldChangeLastLineIndentation(){
      // only change indentation of the first physical line of a logical line
      if( pythonState.getPhysicalLineNumber() > pythonState
            .getLogicalLinePhysicalStartLineNumber() )
         return 0;
      int change;
      if( isUnindentLastLineStatement(pythonState.getLastPhysicalLine()) ){
         change = -pythonState.getTabSize();
      }else{
         change = 0;
      }
      // avoid negative indentation
      int physicalIndentation = pythonState.getLastPhysicalLineIndentation();
      if( physicalIndentation + change < 0 ){
         change = -physicalIndentation;
      }
      return change;
   }

   @Override
   public int shouldChangeNextLineIndentation(){
      int logicalIndentation = pythonState.getLastLogicalLineIndentation();
      int physicalIndentation = pythonState.getLastPhysicalLineIndentation();
      int change = logicalIndentation - physicalIndentation;
      if( pythonState.isLogicalLineComplete() ){
         String logicalLine = pythonState.getLastLogicalLine();
         if( endsWithColon(logicalLine) ){
            change += pythonState.getTabSize();
         }else if( isUnindentNextLineStatement(logicalLine) ){
            change -= pythonState.getTabSize();
         }
      }else if( pythonState.inLongString() ){
         if( pythonState.getDepth() > 1 ){
            // long string inside parenthesis
            change += (PARENTHESIS_INDENTATION_TABSTOPS + (pythonState
                  .getDepth() - 2) * NESTED_PARENTHESIS_INDENTATION_TABSTOPS)
                  * pythonState.getTabSize();
         }else{
            if( pythonState.getPhysicalLineNumber() == pythonState
                  .getLogicalLinePhysicalStartLineNumber() ){
               change = LONG_STRING_INDENTATION_COLUMNS;
            }else{
               change = 0;
            }
         }
      }else if( pythonState.getDepth() > 0 ){
         // only parenthesis, no string
         change += (PARENTHESIS_INDENTATION_TABSTOPS + (pythonState.getDepth() - 1)
               * NESTED_PARENTHESIS_INDENTATION_TABSTOPS)
               * pythonState.getTabSize();
      }else if( pythonState.isExplicitLineJoining() ){
         if( pythonState.getPhysicalLineNumber() == pythonState
               .getLogicalLinePhysicalStartLineNumber() ){
            change = EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
                  * pythonState.getTabSize();
         }else{
            change = 0;
         }
      }else{
         change = 0;
      }

      // avoid negative indentation
      if( physicalIndentation + change < 0 ){
         change = -physicalIndentation;
      }

      return change;
   }
}
