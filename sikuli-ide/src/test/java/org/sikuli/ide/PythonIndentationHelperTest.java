package org.sikuli.ide;

import junit.framework.TestCase;

public class PythonIndentationHelperTest extends TestCase {

   private PythonIndentationHelper indentation;
   private int tabsize;

   public void setUp(){
      indentation = new PythonIndentationHelper();
      tabsize = indentation.getTabWidth();
   }

   public void testEndsWithColonEmpty(){
      assertFalse(indentation.endsWithColon(""));
   }

   public void testEndsWithColonEmptyLine(){
      assertFalse(indentation.endsWithColon("\n"));
   }

   public void testEndsWithColon(){
      assertTrue(indentation.endsWithColon("line:\n"));
   }

   public void testEndsWithColonSpace(){
      assertTrue(indentation.endsWithColon("line: \n"));
   }

   public void testEndsWithColonComment(){
      assertTrue(indentation.endsWithColon("line: #comment\n"));
   }

   public void testEndsWithColonNoColon(){
      assertFalse(indentation.endsWithColon("line\n"));
   }

   public void testEndsWithColonCommented(){
      assertFalse(indentation.endsWithColon("line#:\n"));
   }

   public void testEndsWithColonString(){
      assertFalse(indentation.endsWithColon("'line:'\n"));
   }

   public void testIsUnindentStatementEmpty(){
      assertFalse(indentation.isUnindentStatement(""));
   }

   public void testIsUnindentStatementEmptyLine(){
      assertFalse(indentation.isUnindentStatement("\n"));
   }

   public void testIsUnindentStatementBreak(){
      assertTrue(indentation.isUnindentStatement("\tbreak\n"));
   }

   public void testIsUnindentStatementContinue(){
      assertTrue(indentation.isUnindentStatement("\tcontinue\n"));
   }

   public void testIsUnindentStatementPass(){
      assertTrue(indentation.isUnindentStatement("\tpass\n"));
   }

   public void testIsUnindentStatementRaise(){
      assertTrue(indentation.isUnindentStatement("\traise\n"));
   }

   public void testIsUnindentStatementRaiseException(){
      assertTrue(indentation.isUnindentStatement("\traise Exception()\n"));
   }

   public void testIsUnindentStatementReturn(){
      assertTrue(indentation.isUnindentStatement("\treturn\n"));
   }

   public void testIsUnindentStatementReturnValue(){
      assertTrue(indentation.isUnindentStatement("\treturn 0\n"));
   }

   public void testIsUnindentStatementBreakComment(){
      assertTrue(indentation.isUnindentStatement("\tbreak #comment\n"));
   }

   public void testIsUnindentStatementPrint(){
      assertFalse(indentation.isUnindentStatement("\tprint\n"));
   }

   public void testIsUnindentStatementString(){
      assertFalse(indentation.isUnindentStatement("\t'break'\n"));
   }

   public void testIsUnindentStatementComment(){
      assertFalse(indentation.isUnindentStatement("\t#break\n"));
   }

   public void testDefaultTabWidth(){
      assertTrue(indentation.getTabWidth() > 0);
   }

   public void testSetTabWidth(){
      indentation.setTabWidth(7);
      assertEquals(7, indentation.getTabWidth());
   }

   public void testShouldChangeNextLineIndentationNoText(){
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChange(){
      indentation.addText("print\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChangeIndented(){
      indentation.addText("if True:\n\tprint\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIf(){
      indentation.addText("if True:\n");
      assertEquals(tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfIndented(){
      indentation.addText("try:\n\tif True:\n");
      assertEquals(tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationBreak(){
      indentation.addText("while True:\n\tbreak\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationContinue(){
      indentation.addText("while True:\n\tcontinue\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationPass(){
      indentation.addText("def f():\n\tpass\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationRaise(){
      indentation.addText("if True:\n\traise Exception()\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturn(){
      indentation.addText("def f():\n\treturn\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnIndented(){
      indentation.addText("def f():\n\tif True:\n\t\treturn\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnUnindented(){
      indentation.addText("return\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoining(){
      indentation.addText("print (0,\n\t\t1)\n");
      assertEquals(-2 * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoiningIndented(){
      indentation.addText("\tprint (0,\n\t\t1)\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfImplicitLineJoining(){
      indentation.addText("if (\n\t\tTrue):\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfMultipleImplicitLineJoining(){
      indentation
            .addText("if (a == 0 &&\n\t\tb == 2 &&\n\tc == 1 &&\n\t\t\td == 3):\n");
      assertEquals(-2 * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnImplicitLineJoining(){
      indentation.addText("def f():\n\treturn (0,\n\t\t\t1)\n");
      assertEquals(-3 * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoining(){
      indentation.addText("print\\\n\t0\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningIndented(){
      indentation.addText("\tprint\\\n\t0\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfExplicitLineJoining(){
      indentation.addText("if \\\n\t\tTrue:\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnExplicitLineJoining(){
      indentation.addText("def f():\n\treturn\\\n0\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesis(){
      indentation.addText("print (0,\n");
      assertEquals(PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS
            * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesis(){
      indentation.addText("print (0,\n\t\t(1,\n");
      assertEquals(
            PythonIndentationHelper.NESTED_PARENTHESIS_INDENTATION_TABSTOPS
                  * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesisSameLine(){
      indentation.addText("print (0, (1,\n");
      assertEquals(
            (PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS + PythonIndentationHelper.NESTED_PARENTHESIS_INDENTATION_TABSTOPS)
                  * tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevel(){
      indentation.addText("print (0,\n\t\t1,\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevelFixIndentation(){
      indentation.addText("print (0,\n\t\t\t1,\n");
      assertEquals(-tabsize, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringOpened(){
      indentation.addText("\"\"\"long\n");
      assertEquals(PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringContinued(){
      indentation.addText("\"\"\"long\n   string\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringClosed(){
      indentation.addText("\"\"\"long\n   string\"\"\"\n");
      assertEquals(-PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringSingleLine(){
      indentation.addText("\t\"\"\"long string\"\"\"\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringExplicitLineJoining(){
      indentation.addText("\"\"\"long\\\n");
      assertEquals(PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisLongString(){
      indentation.addText("print (\"\"\"\n");
      assertEquals(PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS
            * indentation.getTabWidth(),
            indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCompactIf(){
      indentation.addText("if True: print 0\n");
      assertEquals(0, indentation.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIncreaseTabWidth(){
      indentation.setTabWidth(7);
      indentation.addText("if True:\n");
      assertEquals(7, indentation.shouldChangeNextLineIndentation());
   }

   private void addFunctionDefs(int num){
      for( int i = 0; i < num; i++ ){
         indentation.addText("def foo(arg1, arg2):\n");
         indentation.addText("\t\"\"\"foo function\"\"\"\n");
         indentation.addText("\tprint '%d,%d\\n' % (\n");
         indentation.addText("\t\t\targ1, arg2)\n");
         indentation.addText("\n");
      }
   }

   public void testPerformance(){
      System.out.println("Starting performance test");
      for( int n = 1; n <= 10000; n *= 10 ){
         indentation.reset();
         System.out.print("adding " + n + " function definitions...");
         long start = System.currentTimeMillis();
         addFunctionDefs(n);
         long end = System.currentTimeMillis();
         System.out.println();
         System.out.println("added " + (indentation.getLastLineNumber() + 1)
               + " lines");
         System.out.println("indentation change = "
               + indentation.shouldChangeNextLineIndentation());
         System.out.println(((end - start) / 1000.0) + " seconds");
      }
      System.out.println("Completed performance test");
   }
}
