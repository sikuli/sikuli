package org.sikuli.ide;

import java.util.regex.Pattern;

public class SimplePythonFormatter implements CodeFormatter {

   private static final String[] END_BLOCK_STATEMENTS = { "break", "continue",
         "pass", "raise", "return" };

   private static final Pattern endBlockPattern;

   static{
      StringBuilder re = new StringBuilder();
      re.append("\\s+(?:").append(END_BLOCK_STATEMENTS[0]);
      for( int i = 1; i < END_BLOCK_STATEMENTS.length; i++ ){
         re.append("|").append(END_BLOCK_STATEMENTS[i]);
      }
      re.append(")\\b.*");
      endBlockPattern = Pattern.compile(re.toString(), Pattern.DOTALL);
   }

   private DocumentAccessor documentAccessor;
   private int tabwidth;

   public SimplePythonFormatter(DocumentAccessor documentAccessor)
         throws IllegalArgumentException{
      if( documentAccessor == null ){
         throw new IllegalArgumentException("null argument");
      }
      this.documentAccessor = documentAccessor;
   }

   @Override
   public void setTabWidth(int tabwidth){
      this.tabwidth = tabwidth;
   }

   @Override
   public int getTabWidth(){
      return tabwidth;
   }

   private boolean endsWithColon(String line){
      for( int i = line.length() - 1; i >= 0; i-- ){
         if( line.charAt(i) == ':' )
            return true;
         else if( !Character.isWhitespace(line.charAt(i)) )
            return false;
      }
      return false;
   }

   private boolean isEndBlockStatement(String line){
      return endBlockPattern.matcher(line).matches();
   }

   @Override
   public int shouldChangeNextLineIndentation(int linenum){
      String line = documentAccessor.getLine(linenum);
      if( endsWithColon(line) )
         return 1;
      if( isEndBlockStatement(line) )
         return -1;
      return 0;
   }

}
