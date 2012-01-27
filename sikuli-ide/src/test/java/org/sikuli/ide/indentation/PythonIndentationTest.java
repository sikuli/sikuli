package org.sikuli.ide.indentation;

import junit.framework.TestCase;

public class PythonIndentationTest extends TestCase {

   private PythonIndentation indentlogic;
   private int tabsize;

   public void setUp(){
      indentlogic = new PythonIndentation();
      tabsize = indentlogic.getTabWidth();
   }

   public void testEndsLastLogicalLineWithColonEmpty(){
      indentlogic.addText("");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonEmptyLine(){
      indentlogic.addText("\n");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColon(){
      indentlogic.addText("line:\n");
      assertTrue(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonSpace(){
      indentlogic.addText("line: \n");
      assertTrue(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonComment(){
      indentlogic.addText("line: #comment\n");
      assertTrue(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonNoColon(){
      indentlogic.addText("line\n");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonCommented(){
      indentlogic.addText("line#:\n");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonString(){
      indentlogic.addText("'line:'\n");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testEndsLastLogicalLineWithColonCompactIfPass(){
      indentlogic.addText("if x: pass\n");
      assertFalse(indentlogic.endsLastLogicalLineWithColon());
   }

   public void testShouldAddColonNoText(){
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonEmptyLine(){
      indentlogic.addText("\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonPrint(){
      indentlogic.addText("print 0\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIf(){
      indentlogic.addText("if\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfSpace(){
      indentlogic.addText("if \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfComment(){
      indentlogic.addText("if # comment\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfX(){
      indentlogic.addText("if x\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXSpace(){
      indentlogic.addText("if x \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXComment(){
      indentlogic.addText("if x # comment\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfString(){
      indentlogic.addText("if 'x'\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfStringSpace(){
      indentlogic.addText("if 'x' \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfStringWithColon(){
      indentlogic.addText("if 'x:'\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfFunctionCall(){
      indentlogic.addText("if f(x)\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfFunctionCallSpace(){
      indentlogic.addText("if f(x) \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfTuple(){
      indentlogic.addText("if (x)\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfNoSpaceTuple(){
      indentlogic.addText("if(x)\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfTupleSpace(){
      indentlogic.addText("if (x) \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfList(){
      indentlogic.addText("if [x]\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfNoSpaceList(){
      indentlogic.addText("if[x]\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfListSpace(){
      indentlogic.addText("if [x] \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfDict(){
      indentlogic.addText("if { 'a' : 0 }\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfNoSpaceDict(){
      indentlogic.addText("if{ 'a' : 0 }\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfDictSpace(){
      indentlogic.addText("if { 'a' : 0 } \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXColon(){
      indentlogic.addText("if x:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXColonSpace(){
      indentlogic.addText("if x: \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXColonComment(){
      indentlogic.addText("if x: #comment\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXColonPass(){
      indentlogic.addText("if x: pass\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXComma(){
      indentlogic.addText("if x,\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXCommaSpace(){
      indentlogic.addText("if x, \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXQuestionMark(){
      indentlogic.addText("if x ?\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXQuestionMarkSpace(){
      indentlogic.addText("if x ? \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfStringPercent(){
      indentlogic.addText("if '%s' %\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfStringPercentSpace(){
      indentlogic.addText("if '%s' % \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfFParenthesis(){
      indentlogic.addText("if f(\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfParenthesisX(){
      indentlogic.addText("if (x\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfOpenSingleQuote(){
      indentlogic.addText("if 'x\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfOpenDoubleQuote(){
      indentlogic.addText("if \"x\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonStringWithIfX(){
      indentlogic.addText("'if x'\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonIfXExplicitLineJoining(){
      indentlogic.addText("if x\\\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElif(){
      indentlogic.addText("elif\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElifX(){
      indentlogic.addText("elif x\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElifXColon(){
      indentlogic.addText("elif x:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElse(){
      indentlogic.addText("else\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElseSpace(){
      indentlogic.addText("else \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElseColon(){
      indentlogic.addText("else:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonElseColonSpace(){
      indentlogic.addText("else: \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonWhile(){
      indentlogic.addText("while\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonWhileX(){
      indentlogic.addText("while x\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonWhileXColon(){
      indentlogic.addText("while x:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonClass(){
      indentlogic.addText("class\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonClassX(){
      indentlogic.addText("class X\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonClassXColon(){
      indentlogic.addText("class X:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonClassXInheritY(){
      indentlogic.addText("class X(Y)\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonClassXInheritYColon(){
      indentlogic.addText("class X(Y):\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonDef(){
      indentlogic.addText("def\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonDefX(){
      indentlogic.addText("def x\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonDefFParentheses(){
      indentlogic.addText("def f()\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonDefFParenthesesColon(){
      indentlogic.addText("def f():\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonTry(){
      indentlogic.addText("try\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonTryColon(){
      indentlogic.addText("try:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   // except is special because it can be used bare or with arguments
   public void testShouldAddColonExcept(){
      indentlogic.addText("except\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   // except is special because it can be used bare or with arguments
   public void testShouldAddColonExceptSpace(){
      indentlogic.addText("except \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptColon(){
      indentlogic.addText("except:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptColonSpace(){
      indentlogic.addText("except: \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptX(){
      indentlogic.addText("except x\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXSpace(){
      indentlogic.addText("except x \n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXColon(){
      indentlogic.addText("except x:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXColonSpace(){
      indentlogic.addText("except x: \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXCommaY(){
      indentlogic.addText("except x, y\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXCommaYColon(){
      indentlogic.addText("except x, y:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXComma(){
      indentlogic.addText("except x,\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonExceptXCommaSpace(){
      indentlogic.addText("except x, \n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonFinally(){
      indentlogic.addText("finally\n");
      assertTrue(indentlogic.shouldAddColon());
   }

   public void testShouldAddColonFinallyColon(){
      indentlogic.addText("finally:\n");
      assertFalse(indentlogic.shouldAddColon());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementEmpty(){
      indentlogic.addText("");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementEmptyLine(){
      indentlogic.addText("\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementBreak(){
      indentlogic.addText("\tbreak\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementContinue(){
      indentlogic.addText("\tcontinue\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementPass(){
      indentlogic.addText("\tpass\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementRaise(){
      indentlogic.addText("\traise\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementRaiseException(){
      indentlogic.addText("\traise Exception()\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementReturn(){
      indentlogic.addText("\treturn\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementReturnValue(){
      indentlogic.addText("\treturn 0\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementBreakComment(){
      indentlogic.addText("\tbreak #comment\n");
      assertTrue(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementPrint(){
      indentlogic.addText("\tprint\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementString(){
      indentlogic.addText("\t'break'\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementComment(){
      indentlogic.addText("\t#break\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementBreakIdentifier(){
      indentlogic.addText("\tbreakid=0\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementReturnIdentifier(){
      indentlogic.addText("\treturnid=0\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsLastLogicalLineUnindentNextLineStatementCompactIfPass(){
      indentlogic.addText("\tif x: pass\n");
      assertFalse(indentlogic.isLastLogicalLineUnindentNextLineStatement());
   }

   public void testIsUnindentLastLineStatementEmpty(){
      indentlogic.addText("");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementEmptyLine(){
      indentlogic.addText("\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementElif(){
      indentlogic.addText("\telif a=0:\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementElse(){
      indentlogic.addText("\telse:\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementExcept(){
      indentlogic.addText("\texcept Exception, e:\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementFinally(){
      indentlogic.addText("\tfinally:\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementElseComment(){
      indentlogic.addText("\telse: #comment\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementPrint(){
      indentlogic.addText("\tprint\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementString(){
      indentlogic.addText("\t'else:'\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementLongString(){
      indentlogic.addText("\t\"\"\"else:\"\"\"\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementComment(){
      indentlogic.addText("\t# else:\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementElseIdentifier(){
      indentlogic.addText("\telseid=0\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementElifOpenParenthesis(){
      indentlogic.addText("\telif (\n");
      assertTrue(indentlogic.isUnindentLastLineStatement());
   }

   public void testIsUnindentLastLineStatementCompactIfPass(){
      indentlogic.addText("\tif x: pass\n");
      assertFalse(indentlogic.isUnindentLastLineStatement());
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
      assertEquals(PythonIndentation.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
            * tabsize, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningRepeated(){
      indentlogic.addText("print\\\n\t\t0,\\\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationExplicitLineJoiningPhysicalLineOffset(){
      indentlogic.addText("\"\"\"doc\n\"\"\"\nprint\\\n");
      assertEquals(PythonIndentation.EXPLICIT_LINE_JOINING_INDENTATION_TABSTOPS
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
      assertEquals(
            PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS * tabsize,
            indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationNestedParenthesis(){
      indentlogic.addText("print (0,\n\t\t(1,\n");
      assertEquals(PythonIndentation.NESTED_PARENTHESIS_INDENTATION_TABSTOPS
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
            * indentlogic.getTabWidth(), indentlogic
            .shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationParenthesisLongDoubleQuoteString(){
      indentlogic.addText("print (\"\"\"\n");
      assertEquals(PythonIndentation.PARENTHESIS_INDENTATION_TABSTOPS
            * indentlogic.getTabWidth(), indentlogic
            .shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCompactIf(){
      indentlogic.addText("if True: print 0\n");
      assertEquals(0, indentlogic.shouldChangeNextLineIndentation());
   }

   public void testShouldChangeNextLineIndentationCompactIfPass(){
      indentlogic.addText("\tif True: pass\n");
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
