package org.sikuli.ide.indentation;

import junit.framework.TestCase;

public class PythonIndentationTest extends TestCase {

   private PythonIndentation indentlogic;
   private int tabsize;

   public void setUp(){
      indentlogic = new PythonIndentation();
      tabsize = indentlogic.getTabWidth();
   }

   public void testEndsWithColonEmpty(){
      assertFalse(indentlogic.endsWithColon(""));
   }

   public void testEndsWithColonEmptyLine(){
      assertFalse(indentlogic.endsWithColon("\n"));
   }

   public void testEndsWithColon(){
      assertTrue(indentlogic.endsWithColon("line:\n"));
   }

   public void testEndsWithColonSpace(){
      assertTrue(indentlogic.endsWithColon("line: \n"));
   }

   public void testEndsWithColonComment(){
      assertTrue(indentlogic.endsWithColon("line: #comment\n"));
   }

   public void testEndsWithColonNoColon(){
      assertFalse(indentlogic.endsWithColon("line\n"));
   }

   public void testEndsWithColonCommented(){
      assertFalse(indentlogic.endsWithColon("line#:\n"));
   }

   public void testEndsWithColonString(){
      assertFalse(indentlogic.endsWithColon("'line:'\n"));
   }

   public void testIsUnindentNextLineStatementEmpty(){
      assertFalse(indentlogic.isUnindentNextLineStatement(""));
   }

   public void testIsUnindentNextLineStatementEmptyLine(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\n"));
   }

   public void testIsUnindentNextLineStatementBreak(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\tbreak\n"));
   }

   public void testIsUnindentNextLineStatementContinue(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\tcontinue\n"));
   }

   public void testIsUnindentNextLineStatementPass(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\tpass\n"));
   }

   public void testIsUnindentNextLineStatementRaise(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\traise\n"));
   }

   public void testIsUnindentNextLineStatementRaiseException(){
      assertTrue(indentlogic
            .isUnindentNextLineStatement("\traise Exception()\n"));
   }

   public void testIsUnindentNextLineStatementReturn(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\treturn\n"));
   }

   public void testIsUnindentNextLineStatementReturnValue(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\treturn 0\n"));
   }

   public void testIsUnindentNextLineStatementBreakComment(){
      assertTrue(indentlogic.isUnindentNextLineStatement("\tbreak #comment\n"));
   }

   public void testIsUnindentNextLineStatementPrint(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\tprint\n"));
   }

   public void testIsUnindentNextLineStatementString(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\t'break'\n"));
   }

   public void testIsUnindentNextLineStatementComment(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\t#break\n"));
   }

   public void testIsUnindentNextLineStatementBreakIdentifier(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\tbreakid=0\n"));
   }

   public void testIsUnindentNextLineStatementReturnIdentifier(){
      assertFalse(indentlogic.isUnindentNextLineStatement("\treturnid=0\n"));
   }

   public void testIsUnindentLastLineStatementEmpty(){
      assertFalse(indentlogic.isUnindentLastLineStatement(""));
   }

   public void testIsUnindentLastLineStatementEmptyLine(){
      assertFalse(indentlogic.isUnindentLastLineStatement("\n"));
   }

   public void testIsUnindentLastLineStatementElif(){
      assertTrue(indentlogic.isUnindentLastLineStatement("\telif a=0:\n"));
   }

   public void testIsUnindentLastLineStatementElse(){
      assertTrue(indentlogic.isUnindentLastLineStatement("\telse:\n"));
   }

   public void testIsUnindentLastLineStatementExcept(){
      assertTrue(indentlogic
            .isUnindentLastLineStatement("\texcept Exception, e:\n"));
   }

   public void testIsUnindentLastLineStatementFinally(){
      assertTrue(indentlogic.isUnindentLastLineStatement("\tfinally:\n"));
   }

   public void testIsUnindentLastLineStatementElseComment(){
      assertTrue(indentlogic.isUnindentLastLineStatement("\telse: #comment\n"));
   }

   public void testIsUnindentLastLineStatementPrint(){
      assertFalse(indentlogic.isUnindentLastLineStatement("\tprint\n"));
   }

   public void testIsUnindentLastLineStatementString(){
      assertFalse(indentlogic.isUnindentLastLineStatement("\t'else:'\n"));
   }

   public void testIsUnindentLastLineStatementLongString(){
      assertFalse(indentlogic
            .isUnindentLastLineStatement("\t\"\"\"else:\"\"\"\n"));
   }

   public void testIsUnindentLastLineStatementComment(){
      assertFalse(indentlogic.isUnindentLastLineStatement("\t# else:\n"));
   }

   public void testIsUnindentLastLineStatementElseIdentifier(){
      assertFalse(indentlogic.isUnindentLastLineStatement("\telseid=0\n"));
   }

   public void testIsUnindentLastLineStatementElifOpenParenthesis(){
      assertTrue(indentlogic.isUnindentLastLineStatement("\telif (\n"));
   }

   public void testIsUnindentLastLineStatement(){
      assertFalse(indentlogic.isUnindentLastLineStatement(""));
   }

   public void testDefaultTabWidth(){
      assertTrue(indentlogic.getTabWidth() > 0);
   }

   public void testSetTabWidth(){
      indentlogic.setTabWidth(7);
      assertEquals(7, indentlogic.getTabWidth());
   }

   public void testShouldChangeNextLineIndentationNoText(){
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChange(){
      indentlogic.addText("print\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNoChangeIndented(){
      indentlogic.addText("if True:\n\tprint\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIf(){
      indentlogic.addText("if True:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfIndented(){
      indentlogic.addText("try:\n\tif True:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationElif(){
      indentlogic.addText("if a:\n\tprint\nelif b:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationElse(){
      indentlogic.addText("if True:\n\tprint\nelse:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationWhile(){
      indentlogic.addText("while True:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationWith(){
      indentlogic.addText("with f:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationTry(){
      indentlogic.addText("try:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExcept(){
      indentlogic.addText("try:\n\tprint\nexcept Exception, e:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationFinally(){
      indentlogic.addText("try:\n\tprint\nfinally:\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationDef(){
      indentlogic.addText("def f():\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationClass(){
      indentlogic.addText("class C(B):\n");
      assertEquals(tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationBreak(){
      indentlogic.addText("while True:\n\tbreak\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationContinue(){
      indentlogic.addText("while True:\n\tcontinue\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationPass(){
      indentlogic.addText("def f():\n\tpass\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationRaise(){
      indentlogic.addText("if True:\n\traise Exception()\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturn(){
      indentlogic.addText("def f():\n\treturn\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnIndented(){
      indentlogic.addText("def f():\n\tif True:\n\t\treturn\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnUnindented(){
      indentlogic.addText("return\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoining(){
      indentlogic.addText("print (0,\n\t\t1)\n");
      assertEquals(-2 * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationImplicitLineJoiningIndented(){
      indentlogic.addText("\tprint (0,\n\t\t1)\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfImplicitLineJoining(){
      indentlogic.addText("if (\n\t\tTrue):\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfMultipleImplicitLineJoining(){
      indentlogic
            .addText("if (a == 0 &&\n\t\tb == 2 &&\n\tc == 1 &&\n\t\t\td == 3):\n");
      assertEquals(-2 * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnImplicitLineJoining(){
      indentlogic.addText("def f():\n\treturn (0,\n\t\t\t1)\n");
      assertEquals(-3 * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoining(){
      indentlogic.addText("print\\\n");
      assertEquals(
            PythonIndentation.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
                  * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningRepeated(){
      indentlogic.addText("print\\\n\t\t0,\\\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningPhysicalLineOffset(){
      indentlogic.addText("\"\"\"doc\n\"\"\"\nprint\\\n");
      assertEquals(
            PythonIndentation.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
                  * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningPhysicalLineOffsetRepeated(){
      indentlogic.addText("\"\"\"doc\n\"\"\"\nprint\\\n\t\t0,\\\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningEnd(){
      indentlogic.addText("print\\\n\t0\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningEndIndented(){
      indentlogic.addText("\tprint\\\n\t0\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIfExplicitLineJoining(){
      indentlogic.addText("if \\\n\t\tTrue:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnExplicitLineJoining(){
      indentlogic.addText("def f():\n\treturn\\\n\t0\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesis(){
      indentlogic.addText("print (0,\n");
      assertEquals(PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS
            * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesis(){
      indentlogic.addText("print (0,\n\t\t(1,\n");
      assertEquals(
            PythonIndentation.NESTED_PARENTHESIS_INDENTATION_TABSTOPS
                  * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesisSameLine(){
      indentlogic.addText("print (0, (1,\n");
      assertEquals(
            (PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS + PythonIndentation.NESTED_PARENTHESIS_INDENTATION_TABSTOPS)
                  * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevel(){
      indentlogic.addText("print (0,\n\t\t1,\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisSameLevelFixIndentation(){
      indentlogic.addText("print (0,\n\t\t\t1,\n");
      assertEquals(-tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringOpened(){
      indentlogic.addText("'''long\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringContinued(){
      indentlogic.addText("'''long\n   string\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringClosed(){
      indentlogic.addText("'''long\n   string'''\n");
      assertEquals(-PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringSingleLine(){
      indentlogic.addText("\t'''long string'''\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringOpenedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n'''long\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringContinuedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n'''long\n   string\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringClosedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n'''long\n   string'''\n");
      assertEquals(-PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongSingleQuoteStringExplicitLineJoining(){
      indentlogic.addText("'''long\\\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringOpened(){
      indentlogic.addText("\"\"\"long\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringContinued(){
      indentlogic.addText("\"\"\"long\n   string\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringClosed(){
      indentlogic.addText("\"\"\"long\n   string\"\"\"\n");
      assertEquals(-PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringSingleLine(){
      indentlogic.addText("\t\"\"\"long string\"\"\"\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringOpenedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n\"\"\"long\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringContinuedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n\"\"\"long\n   string\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringClosedPhysicalLineOffset(){
      indentlogic.addText("a=[0,\n\t\t1]\n\"\"\"long\n   string\"\"\"\n");
      assertEquals(-PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationLongDoubleQuoteStringExplicitLineJoining(){
      indentlogic.addText("\"\"\"long\\\n");
      assertEquals(PythonIndentation.LONG_STRING_INDENTATION_COLUMNS,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationComment(){
      indentlogic.addText("#comment\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentIndented(){
      indentlogic.addText("\t#comment\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentIf(){
      indentlogic.addText("#if True:\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentBreak(){
      indentlogic.addText("while True:\n\t#break\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentString(){
      indentlogic.addText("#s=\"\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentLongSingleQuoteString(){
      indentlogic.addText("#'''\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentLongDoubleQuoteString(){
      indentlogic.addText("#\"\"\"\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCommentBackslashEOL(){
      indentlogic.addText("#comment\\\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisLongSingleQuoteString(){
      indentlogic.addText("print ('''\n");
      assertEquals(PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS
            * indentlogic.getTabWidth(),
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisLongDoubleQuoteString(){
      indentlogic.addText("print (\"\"\"\n");
      assertEquals(PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS
            * indentlogic.getTabWidth(),
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCompactIf(){
      indentlogic.addText("if True: print 0\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationBreakIdentifier(){
      indentlogic.addText("while True:\n\tbreakid=0\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationReturnIdentifier(){
      indentlogic.addText("def f():\n\treturnid=0\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationIncreaseTabWidth(){
      indentlogic.setTabWidth(7);
      indentlogic.addText("if True:\n");
      assertEquals(7, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrint(){
      indentlogic.addText("print\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintIndented(){
      indentlogic.addText("\tprint\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElif(){
      indentlogic.addText("if a:\n\tprint\n\telif b:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElse(){
      indentlogic.addText("if a:\n\tprint\n\telse:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseAfterIf(){
      indentlogic.addText("if a:\n\telse:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseAfterBlock(){
      indentlogic.addText("if a:\n\tprint\tprint\n\telse:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNestedIf(){
      indentlogic.addText("if a:\n\tif b:\n\t\tprint\n\t\telse:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNestedIfAlreadyUnindented(){
      indentlogic.addText("if a:\n\tif b:\n\t\tprint\n\telse:\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseNoIndentation(){
      indentlogic.addText("if a:\n\tprint\nelse:\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationExcept(){
      indentlogic.addText("try:\n\tprint\n\texcept Exception, e:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationFinally(){
      indentlogic.addText("try:\n\tprint\n\tfinally:\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintInIf(){
      indentlogic.addText("if a:\n\tprint\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationPrintInTry(){
      indentlogic.addText("try:\n\tprint\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenParenthesis(){
      indentlogic.addText("if a:\n\tprint\n\telif (b\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenString(){
      indentlogic.addText("if a:\n\tprint\n\telif s=='\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifOpenLongString(){
      indentlogic.addText("if a:\n\tprint\n\telif s==\"\"\"\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElifExplicitLineJoining(){
      indentlogic.addText("if a:\n\tprint\n\telif \\\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationCommentElse(){
      indentlogic.addText("if a:\n\tprint\n\t#else:\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationStringElse(){
      indentlogic.addText("if a:\n\tprint\n\t'else:'\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationLongStringElse(){
      indentlogic.addText("if a:\n\tprint\n\t\"\"\"else:\"\"\"\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationElseIdentifier(){
      indentlogic.addText("if a:\n\tprint\n\telseid=0\n");
      assertEquals(0, indentlogic.shouldChangeLastLineIndentation());
   }

   public void testShouldChangeLastLineIndentationCompactElse(){
      indentlogic.addText("if a:\n\tprint\n\telse: print\n");
      assertEquals(-tabsize, indentlogic.shouldChangeLastLineIndentation());
   }

   private void addFunctionDefs(int num){
      for( int i = 0; i < num; i++ ){
         indentlogic.addText("def foo(arg1, arg2):\n");
         indentlogic.addText("\t\"\"\"foo function\"\"\"\n");
         indentlogic.addText("\tprint '%d,%d\\n' % (\n");
         indentlogic.addText("\t\t\targ1, arg2)\n");
         indentlogic.addText("\n");
      }
   }

   public void testPerformance(){
      System.out.println("Starting performance test");
      for( int n = 1; n <= 10000; n *= 10 ){
         indentlogic.reset();
         System.out.print("adding " + n + " function definitions...");
         long start = System.currentTimeMillis();
         addFunctionDefs(n);
         long end = System.currentTimeMillis();
         System.out.println();
         System.out.println("added " + (indentlogic.getLastLineNumber() + 1)
               + " lines");
         System.out.println("indentation change = "
               + indentlogic.shouldChangeNextLineIndentation());
         System.out.println(((end - start) / 1000.0) + " seconds");
      }
      System.out.println("Completed performance test");
   }
}
