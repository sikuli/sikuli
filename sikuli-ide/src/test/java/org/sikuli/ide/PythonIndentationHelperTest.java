package org.sikuli.ide;

import junit.framework.TestCase;

public class PythonIndentationHelperTest extends TestCase {

   private PythonIndentationHelper indHelper;
   private int tabsize;

   public void setUp(){
      indHelper = new PythonIndentationHelper();
      tabsize = indHelper.getTabWidth();
   }

   public void testEndsWithColonEmpty(){
      assertFalse(indHelper.endsWithColon(""));
   }

   public void testEndsWithColonEmptyLine(){
      assertFalse(indHelper.endsWithColon("\n"));
   }

   public void testEndsWithColon(){
      assertTrue(indHelper.endsWithColon("line:\n"));
   }

   public void testEndsWithColonSpace(){
      assertTrue(indHelper.endsWithColon("line: \n"));
   }

   public void testEndsWithColonComment(){
      assertTrue(indHelper.endsWithColon("line: #comment\n"));
   }

   public void testEndsWithColonNoColon(){
      assertFalse(indHelper.endsWithColon("line\n"));
   }

   public void testEndsWithColonCommented(){
      assertFalse(indHelper.endsWithColon("line#:\n"));
   }

   public void testEndsWithColonString(){
      assertFalse(indHelper.endsWithColon("'line:'\n"));
   }

   public void testIsUnindentNextLineStatementEmpty(){
      assertFalse(indHelper.isUnindentNextLineStatement(""));
   }

   public void testIsUnindentNextLineStatementEmptyLine(){
      assertFalse(indHelper.isUnindentNextLineStatement("\n"));
   }

   public void testIsUnindentNextLineStatementBreak(){
      assertTrue(indHelper.isUnindentNextLineStatement("\tbreak\n"));
   }

   public void testIsUnindentNextLineStatementContinue(){
      assertTrue(indHelper.isUnindentNextLineStatement("\tcontinue\n"));
   }

   public void testIsUnindentNextLineStatementPass(){
      assertTrue(indHelper.isUnindentNextLineStatement("\tpass\n"));
   }

   public void testIsUnindentNextLineStatementRaise(){
      assertTrue(indHelper.isUnindentNextLineStatement("\traise\n"));
   }

   public void testIsUnindentNextLineStatementRaiseException(){
      assertTrue(indHelper.isUnindentNextLineStatement("\traise Exception()\n"));
   }

   public void testIsUnindentNextLineStatementReturn(){
      assertTrue(indHelper.isUnindentNextLineStatement("\treturn\n"));
   }

   public void testIsUnindentNextLineStatementReturnValue(){
      assertTrue(indHelper.isUnindentNextLineStatement("\treturn 0\n"));
   }

   public void testIsUnindentNextLineStatementBreakComment(){
      assertTrue(indHelper.isUnindentNextLineStatement("\tbreak #comment\n"));
   }

   public void testIsUnindentNextLineStatementPrint(){
      assertFalse(indHelper.isUnindentNextLineStatement("\tprint\n"));
   }

   public void testIsUnindentNextLineStatementString(){
      assertFalse(indHelper.isUnindentNextLineStatement("\t'break'\n"));
   }

   public void testIsUnindentNextLineStatementComment(){
      assertFalse(indHelper.isUnindentNextLineStatement("\t#break\n"));
   }

   public void testIsUnindentNextLineStatementBreakIdentifier(){
      assertFalse(indHelper.isUnindentNextLineStatement("\tbreakid=0\n"));
   }

   public void testIsUnindentNextLineStatementReturnIdentifier(){
      assertFalse(indHelper.isUnindentNextLineStatement("\treturnid=0\n"));
   }

   public void testIsUnindentLastLineStatementEmpty(){
      assertFalse(indHelper.isUnindentLastLineStatement(""));
   }

   public void testIsUnindentLastLineStatementEmptyLine(){
      assertFalse(indHelper.isUnindentLastLineStatement("\n"));
   }

   public void testIsUnindentLastLineStatementElif(){
      assertTrue(indHelper.isUnindentLastLineStatement("\telif a=0:\n"));
   }

   public void testIsUnindentLastLineStatementElse(){
      assertTrue(indHelper.isUnindentLastLineStatement("\telse:\n"));
   }

   public void testIsUnindentLastLineStatementExcept(){
      assertTrue(indHelper
            .isUnindentLastLineStatement("\texcept Exception, e:\n"));
   }

   public void testIsUnindentLastLineStatementFinally(){
      assertTrue(indHelper.isUnindentLastLineStatement("\tfinally:\n"));
   }

   public void testIsUnindentLastLineStatementElseComment(){
      assertTrue(indHelper.isUnindentLastLineStatement("\telse: #comment\n"));
   }

   public void testIsUnindentLastLineStatementPrint(){
      assertFalse(indHelper.isUnindentLastLineStatement("\tprint\n"));
   }

   public void testIsUnindentLastLineStatementString(){
      assertFalse(indHelper.isUnindentLastLineStatement("\t'else:'\n"));
   }

   public void testIsUnindentLastLineStatementLongString(){
      assertFalse(indHelper
            .isUnindentLastLineStatement("\t\"\"\"else:\"\"\"\n"));
   }

   public void testIsUnindentLastLineStatementComment(){
      assertFalse(indHelper.isUnindentLastLineStatement("\t# else:\n"));
   }

   public void testIsUnindentLastLineStatementElseIdentifier(){
      assertFalse(indHelper.isUnindentLastLineStatement("\telseid=0\n"));
   }

   public void testIsUnindentLastLineStatementElifOpenParenthesis(){
      assertTrue(indHelper.isUnindentLastLineStatement("\telif (\n"));
   }

   public void testIsUnindentLastLineStatement(){
      assertFalse(indHelper.isUnindentLastLineStatement(""));
   }

   public void testDefaultTabWidth(){
      assertTrue(indHelper.getTabWidth() > 0);
   }

   public void testSetTabWidth(){
      indHelper.setTabWidth(7);
      assertEquals(7, indHelper.getTabWidth());
   }

   public void testShouldChangeNextLineIndentationNoText(){
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChange(){
      indHelper.addText("print\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChangeIndented(){
      indHelper.addText("if True:\n\tprint\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIf(){
      indHelper.addText("if True:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfIndented(){
      indHelper.addText("try:\n\tif True:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationElif(){
      indHelper.addText("if a:\n\tprint\nelif b:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationElse(){
      indHelper.addText("if True:\n\tprint\nelse:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationWhile(){
      indHelper.addText("while True:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationWith(){
      indHelper.addText("with f:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationTry(){
      indHelper.addText("try:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExcept(){
      indHelper.addText("try:\n\tprint\nexcept Exception, e:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationFinally(){
      indHelper.addText("try:\n\tprint\nfinally:\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationDef(){
      indHelper.addText("def f():\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationClass(){
      indHelper.addText("class C(B):\n");
      assertEquals(tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationBreak(){
      indHelper.addText("while True:\n\tbreak\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationContinue(){
      indHelper.addText("while True:\n\tcontinue\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationPass(){
      indHelper.addText("def f():\n\tpass\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationRaise(){
      indHelper.addText("if True:\n\traise Exception()\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturn(){
      indHelper.addText("def f():\n\treturn\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnIndented(){
      indHelper.addText("def f():\n\tif True:\n\t\treturn\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnUnindented(){
      indHelper.addText("return\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoining(){
      indHelper.addText("print (0,\n\t\t1)\n");
      assertEquals(-2 * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoiningIndented(){
      indHelper.addText("\tprint (0,\n\t\t1)\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfImplicitLineJoining(){
      indHelper.addText("if (\n\t\tTrue):\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfMultipleImplicitLineJoining(){
      indHelper
            .addText("if (a == 0 &&\n\t\tb == 2 &&\n\tc == 1 &&\n\t\t\td == 3):\n");
      assertEquals(-2 * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnImplicitLineJoining(){
      indHelper.addText("def f():\n\treturn (0,\n\t\t\t1)\n");
      assertEquals(-3 * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoining(){
      indHelper.addText("print\\\n");
      assertEquals(
            PythonIndentationHelper.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
                  * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningRepeated(){
      indHelper.addText("print\\\n\t\t0,\\\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningPhysicalLineOffset(){
      indHelper.addText("\"\"\"doc\n\"\"\"\nprint\\\n");
      assertEquals(
            PythonIndentationHelper.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
                  * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningPhysicalLineOffsetRepeated(){
      indHelper.addText("\"\"\"doc\n\"\"\"\nprint\\\n\t\t0,\\\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningEnd(){
      indHelper.addText("print\\\n\t0\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningEndIndented(){
      indHelper.addText("\tprint\\\n\t0\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfExplicitLineJoining(){
      indHelper.addText("if \\\n\t\tTrue:\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnExplicitLineJoining(){
      indHelper.addText("def f():\n\treturn\\\n\t0\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesis(){
      indHelper.addText("print (0,\n");
      assertEquals(PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS
            * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesis(){
      indHelper.addText("print (0,\n\t\t(1,\n");
      assertEquals(
            PythonIndentationHelper.NESTED_PARENTHESIS_INDENTATION_TABSTOPS
                  * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesisSameLine(){
      indHelper.addText("print (0, (1,\n");
      assertEquals(
            (PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS + PythonIndentationHelper.NESTED_PARENTHESIS_INDENTATION_TABSTOPS)
                  * tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevel(){
      indHelper.addText("print (0,\n\t\t1,\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevelFixIndentation(){
      indHelper.addText("print (0,\n\t\t\t1,\n");
      assertEquals(-tabsize, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringOpened(){
      indHelper.addText("\"\"\"long\n");
      assertEquals(PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringContinued(){
      indHelper.addText("\"\"\"long\n   string\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringClosed(){
      indHelper.addText("\"\"\"long\n   string\"\"\"\n");
      assertEquals(-PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringSingleLine(){
      indHelper.addText("\t\"\"\"long string\"\"\"\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringOpenedPhysicalLineOffset(){
      indHelper.addText("a=[0,\n\t\t1]\n\"\"\"long\n");
      assertEquals(PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringContinuedPhysicalLineOffset(){
      indHelper.addText("a=[0,\n\t\t1]\n\"\"\"long\n   string\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringClosedPhysicalLineOffset(){
      indHelper.addText("a=[0,\n\t\t1]\n\"\"\"long\n   string\"\"\"\n");
      assertEquals(-PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongStringExplicitLineJoining(){
      indHelper.addText("\"\"\"long\\\n");
      assertEquals(PythonIndentationHelper.LONG_STRING_INDENTATION_COLUMNS,
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationComment(){
      indHelper.addText("#comment\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentIndented(){
      indHelper.addText("\t#comment\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentIf(){
      indHelper.addText("#if True:\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentBreak(){
      indHelper.addText("while True:\n\t#break\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentString(){
      indHelper.addText("#s=\"\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentLongString(){
      indHelper.addText("#\"\"\"\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentBackslashEOL(){
      indHelper.addText("#comment\\\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisLongString(){
      indHelper.addText("print (\"\"\"\n");
      assertEquals(PythonIndentationHelper.PARENTHESIS_INDENTATION_TABSTOPS
            * indHelper.getTabWidth(),
            indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCompactIf(){
      indHelper.addText("if True: print 0\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationBreakIdentifier(){
      indHelper.addText("while True:\n\tbreakid=0\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnIdentifier(){
      indHelper.addText("def f():\n\treturnid=0\n");
      assertEquals(0, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIncreaseTabWidth(){
      indHelper.setTabWidth(7);
      indHelper.addText("if True:\n");
      assertEquals(7, indHelper.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrint(){
      indHelper.addText("print\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintIndented(){
      indHelper.addText("\tprint\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElif(){
      indHelper.addText("if a:\n\tprint\n\telif b:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElse(){
      indHelper.addText("if a:\n\tprint\n\telse:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseAfterIf(){
      indHelper.addText("if a:\n\telse:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseAfterBlock(){
      indHelper.addText("if a:\n\tprint\tprint\n\telse:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNestedIf(){
      indHelper.addText("if a:\n\tif b:\n\t\tprint\n\t\telse:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNestedIfToplevel(){
      indHelper.addText("if a:\n\tif b:\n\t\tprint\n\telse:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNoIndentation(){
      indHelper.addText("if a:\n\tprint\nelse:\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationExcept(){
      indHelper.addText("try:\n\tprint\n\texcept Exception, e:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationFinally(){
      indHelper.addText("try:\n\tprint\n\tfinally:\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintInIf(){
      indHelper.addText("if a:\n\tprint\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintInTry(){
      indHelper.addText("try:\n\tprint\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenParenthesis(){
      indHelper.addText("if a:\n\tprint\n\telif (b\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenString(){
      indHelper.addText("if a:\n\tprint\n\telif s=='\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenLongString(){
      indHelper.addText("if a:\n\tprint\n\telif s==\"\"\"\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifExplicitLineJoining(){
      indHelper.addText("if a:\n\tprint\n\telif \\\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationCommentElse(){
      indHelper.addText("if a:\n\tprint\n\t#else:\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationStringElse(){
      indHelper.addText("if a:\n\tprint\n\t'else:'\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationLongStringElse(){
      indHelper.addText("if a:\n\tprint\n\t\"\"\"else:\"\"\"\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseIdentifier(){
      indHelper.addText("if a:\n\tprint\n\telseid=0\n");
      assertEquals(0, indHelper.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationCompactElse(){
      indHelper.addText("if a:\n\tprint\n\telse: print\n");
      assertEquals(-tabsize, indHelper.shouldChangeLastLineIndentation());
   }

   private void addFunctionDefs(int num){
      for( int i = 0; i < num; i++ ){
         indHelper.addText("def foo(arg1, arg2):\n");
         indHelper.addText("\t\"\"\"foo function\"\"\"\n");
         indHelper.addText("\tprint '%d,%d\\n' % (\n");
         indHelper.addText("\t\t\targ1, arg2)\n");
         indHelper.addText("\n");
      }
   }

   public void testPerformance(){
      System.out.println("Starting performance test");
      for( int n = 1; n <= 10000; n *= 10 ){
         indHelper.reset();
         System.out.print("adding " + n + " function definitions...");
         long start = System.currentTimeMillis();
         addFunctionDefs(n);
         long end = System.currentTimeMillis();
         System.out.println();
         System.out.println("added " + (indHelper.getLastLineNumber() + 1)
               + " lines");
         System.out.println("indentation change = "
               + indHelper.shouldChangeNextLineIndentation());
         System.out.println(((end - start) / 1000.0) + " seconds");
      }
      System.out.println("Completed performance test");
   }
}
