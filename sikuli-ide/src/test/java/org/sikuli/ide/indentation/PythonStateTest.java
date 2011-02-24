package org.sikuli.ide.indentation;

import junit.framework.TestCase;

public class PythonStateTest extends TestCase {

   private PythonState state;

   @Override
   public void setUp(){
      state = new PythonState();
   }

   public void testUpdateDefault(){
      state.update("print");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateSingleQuote(){
      state.update("'print (\"#'");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateSingleQuoteEmpty(){
      state.update("''");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateSingleQuoteOpen(){
      state.update("'print (\"#");
      assertEquals(PythonState.State.IN_SINGLE_QUOTED_STRING, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateSingleQuoteEscaped(){
      state.update("'print (\"#\\'");
      assertEquals(PythonState.State.IN_SINGLE_QUOTED_STRING, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateSingleQuoteEscapedBackslash(){
      state.update("'print (\"#\\\\'");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateDoubleQuote(){
      state.update("\"print ('#\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateDoubleQuoteEmpty(){
      state.update("\"\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateDoubleQuoteOpen(){
      state.update("\"print ('#");
      assertEquals(PythonState.State.IN_DOUBLE_QUOTED_STRING, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateDoubleQuoteEscaped(){
      state.update("\"print ('#\\\"");
      assertEquals(PythonState.State.IN_DOUBLE_QUOTED_STRING, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateDoubleQuoteEscapedBackslash(){
      state.update("\"print ('#\\\\\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateLongSingleQuote(){
      state.update("'''print ('#\" '' '''");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateLongSingleQuoteOpen(){
      state.update("'''print ('#\" ''");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped1(){
      state.update("'''print ('#\" \\'''");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped2(){
      state.update("'''print ('#\" '\\''");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped3(){
      state.update("'''print ('#\" ''\\'");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped12(){
      state.update("'''print ('#\" \\'\\''");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped23(){
      state.update("'''print ('#\" '\\'\\'");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscaped123(){
      state.update("'''print ('#\" \\'\\'\\'");
      assertEquals(PythonState.State.IN_LONG_SINGLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteEscapedBackslash(){
      state.update("'''print ('#\" \\\\'''");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateLongDoubleQuote(){
      state.update("\"\"\"print ('#\" \"\" \"\"\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteOpen(){
      state.update("\"\"\"print ('#\" \"\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped1(){
      state.update("\"\"\"print ('#\" \\\"\"\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped2(){
      state.update("\"\"\"print ('#\" \"\\\"\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped3(){
      state.update("\"\"\"print ('#\" \"\"\\\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped12(){
      state.update("\"\"\"print ('#\" \\\"\\\"\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped23(){
      state.update("\"\"\"print ('#\" \"\\\"\\\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscaped123(){
      state.update("\"\"\"print ('#\" \\\"\\\"\\\"");
      assertEquals(PythonState.State.IN_LONG_DOUBLE_QUOTED_STRING,
            state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteEscapedBackslash(){
      state.update("\"\"\"print ('#\" \\\\\"\"\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateParenthesis(){
      state.update("{ a : f(x[0]) }");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateParenthesisOpen(){
      state.update("{ a : f(x[0])");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateParenthesisOpen2(){
      state.update("{ a : f(x[0]");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(2, state.getDepth());
   }

   public void testUpdateParenthesisOpen3(){
      state.update("{ a : f(x[0");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(3, state.getDepth());
   }

   public void testUpdateParenthesisInvalid(){
      state.update("{ a : f(x[0]) }}");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateComment(){
      state.update("# \"comment (\"\"\"");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateCommentWhitespace(){
      state.update("  # '");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLFComment(){
      state.update("\n#");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateCRLFComment(){
      state.update("\r\n#");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateCRComment(){
      state.update("\r#");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateCommentEOL(){
      state.update("# \"comment (\"\"\"\n");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateParenthesisComment(){
      state.update("(#[");
      assertEquals(PythonState.State.IN_COMMENT, state.getState());
      assertEquals(2, state.getDepth());
   }

   public void testUpdateParenthesisCommentEOL(){
      state.update("(#[\n");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateLongSingleQuoteExplicitJoining(){
      state.update("'''long \\\n");
      state.update("string'''");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateLongDoubleQuoteExplicitJoining(){
      state.update("\"\"\"long \\\n");
      state.update("string\"\"\"");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testUpdateParenthesisExplicitJoining(){
      state.update("(0,\\\n");
      state.update("1");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateMixed(){
      state.update("def f():\n\t\"\"\"doc f()\"\"\"\n\t# comment\n\treturn '%d,%d' % (");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
   }

   public void testUpdateChunks(){
      state.update("{ a : ");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
      state.update("f(x");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(2, state.getDepth());
      state.update("['");
      assertEquals(PythonState.State.IN_SINGLE_QUOTED_STRING, state.getState());
      assertEquals(4, state.getDepth());
      state.update("0'");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(3, state.getDepth());
      state.update("]");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(2, state.getDepth());
      state.update(")");
      assertEquals(PythonState.State.IN_PARENTHESIS, state.getState());
      assertEquals(1, state.getDepth());
      state.update(" }");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      state.update("}");
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
   }

   public void testIsPhysicalLineCompleteNoInput(){
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineCompleteEmpty(){
      state.update("");
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineCompleteNoEOL(){
      state.update("print");
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineComplete1Line(){
      state.update("print\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLine1Line(){
      state.update("print\n");
      assertEquals("print\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteChunks(){
      state.update("print ");
      assertFalse(state.isPhysicalLineComplete());
      state.update("0");
      assertFalse(state.isPhysicalLineComplete());
      state.update("\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineChunks(){
      state.update("print ");
      state.update("0");
      state.update("\n");
      assertEquals("print 0\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteChunksLine2(){
      state.update("print\n");
      state.update("print");
      assertFalse(state.isPhysicalLineComplete());
      state.update(" 0\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineChunksLine2(){
      state.update("print\n");
      state.update("print");
      state.update(" 0\n");
      assertEquals("print 0\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteLine2Incomplete(){
      state.update("print\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("print");
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineCompleteBackslashEOL(){
      state.update("print\\\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineBackslashEOL(){
      state.update("print\\\n");
      assertEquals("print\\\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteEscapedBackslashEOL(){
      state.update("print\\\\\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineEscapedBackslashEOL(){
      state.update("print\\\\\n");
      assertEquals("print\\\\\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteCommentBackslashEOL(){
      state.update("#comment\\\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineCommentBackslashEOL(){
      state.update("#comment\\\n");
      assertEquals("#comment\\\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteExplicitJoining(){
      state.update("print \\\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("0\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineExplicitJoining(){
      state.update("print \\\n");
      assertEquals("print \\\n", state.getLastPhysicalLine());
      state.update("0\n");
      assertEquals("0\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteExplicitJoiningNoEOL(){
      state.update("print \\\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("0");
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineCompleteExplicitJoiningChunks(){
      state.update("print ");
      assertFalse(state.isPhysicalLineComplete());
      state.update("\\");
      assertFalse(state.isPhysicalLineComplete());
      state.update("\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("0");
      assertFalse(state.isPhysicalLineComplete());
      state.update("\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineExplicitJoiningChunks(){
      state.update("print ");
      state.update("\\");
      state.update("\n");
      assertEquals("print \\\n", state.getLastPhysicalLine());
      state.update("0");
      state.update("\n");
      assertEquals("0\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteExplicitJoiningOneChunk(){
      state.update("print \\\n0\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineExplicitJoiningOneChunk(){
      state.update("print \\\n0\n");
      assertEquals("0\n", state.getLastPhysicalLine());
   }

   public void testIsPhysicalLineCompleteExplicitJoiningOneChunkNoEOL(){
      state.update("print \\\n0");
      assertFalse(state.isPhysicalLineComplete());
   }

   public void testIsPhysicalLineCompleteRepeatedExplicitJoining(){
      state.update("print\\\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update(" \\\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("\\\n");
      assertTrue(state.isPhysicalLineComplete());
      state.update("0\n");
      assertTrue(state.isPhysicalLineComplete());
   }

   public void testGetLastPhysicalLineRepeatedExplicitJoining(){
      state.update("print\\\n");
      assertEquals("print\\\n", state.getLastPhysicalLine());
      state.update(" \\\n");
      assertEquals(" \\\n", state.getLastPhysicalLine());
      state.update("\\\n");
      assertEquals("\\\n", state.getLastPhysicalLine());
      state.update("0\n");
      assertEquals("0\n", state.getLastPhysicalLine());
   }

   public void testIsLogicalLineCompleteNoInput(){
      assertFalse(state.isLogicalLineComplete());
   }

   public void testIsLogicalLineCompleteEmpty(){
      state.update("");
      assertFalse(state.isLogicalLineComplete());
   }

   public void testIsLogicalLineCompleteNoEOL(){
      state.update("print");
      assertFalse(state.isLogicalLineComplete());
   }

   public void testIsLogicalLineCompleteSimpleStatement(){
      state.update("print\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineSimpleStatement(){
      state.update("print\n");
      assertEquals("print\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteParenthesis(){
      state.update("{ 'a' :\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("0 }\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineParenthesis(){
      state.update("{ 'a' :\n");
      assertEquals("{ 'a' :\n", state.getLastLogicalLine());
      state.update("0 }\n");
      assertEquals("{ 'a' :\n0 }\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteLongSingleQuote(){
      state.update("'''long\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("string\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("'''\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineLongSingleQuote(){
      state.update("'''long\n");
      assertEquals("'''long\n", state.getLastLogicalLine());
      state.update("string\n");
      assertEquals("'''long\nstring\n", state.getLastLogicalLine());
      state.update("'''\n");
      assertEquals("'''long\nstring\n'''\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteLongDoubleQuote(){
      state.update("\"\"\"long\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("string\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("\"\"\"\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineLongDoubleQuote(){
      state.update("\"\"\"long\n");
      assertEquals("\"\"\"long\n", state.getLastLogicalLine());
      state.update("string\n");
      assertEquals("\"\"\"long\nstring\n", state.getLastLogicalLine());
      state.update("\"\"\"\n");
      assertEquals("\"\"\"long\nstring\n\"\"\"\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteExplicitJoining(){
      state.update("print \\\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("0\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineExplicitJoining(){
      state.update("print \\\n");
      assertEquals("print ", state.getLastLogicalLine());
      state.update("0\n");
      assertEquals("print 0\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteRepeatedExplicitJoining(){
      state.update("print\\\n");
      assertFalse(state.isLogicalLineComplete());
      state.update(" \\\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("\\\n");
      assertFalse(state.isLogicalLineComplete());
      state.update("0\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineRepeatedExplicitJoining(){
      state.update("print\\\n");
      assertEquals("print", state.getLastLogicalLine());
      state.update(" \\\n");
      assertEquals("print ", state.getLastLogicalLine());
      state.update("\\\n");
      assertEquals("print ", state.getLastLogicalLine());
      state.update("0\n");
      assertEquals("print 0\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteCommentBackslashEOL(){
      state.update("#comment\\\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineCommentBackslashEOL(){
      state.update("#comment\\\n");
      assertEquals("#comment\\\n", state.getLastLogicalLine());
   }

   public void testIsLogicalLineCompleteEscapedBackslashEOL(){
      state.update("print\\\\\n");
      assertTrue(state.isLogicalLineComplete());
   }

   public void testGetLastLogicalLineEscapedBackslashEOL(){
      state.update("print\\\\\n");
      assertEquals("print\\\\\n", state.getLastLogicalLine());
   }

   public void testIsExplicitLineJoiningCompleteLine(){
      state.update("print\n");
      assertFalse(state.isExplicitLineJoining());
   }

   public void testIsExplicitLineJoining(){
      state.update("print\\\n");
      assertTrue(state.isExplicitLineJoining());
   }

   public void testIsExplicitLineJoiningJoined(){
      state.update("print\\\n");
      state.update(" 0\n");
      assertFalse(state.isExplicitLineJoining());
   }

   public void testCodeFragment(){
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());

      state.update("\"\"\"code\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\"\"\"code\n", state.getLastPhysicalLine());
      assertEquals("\"\"\"code\n", state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("   fragment\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(1, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("   fragment\n", state.getLastPhysicalLine());
      assertEquals("\"\"\"code\n   fragment\n", state.getLastLogicalLine());
      assertEquals(3, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\"\"\"\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(2, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\"\"\"\n", state.getLastPhysicalLine());
      assertEquals("\"\"\"code\n   fragment\n\"\"\"\n",
            state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(3, state.getPhysicalLineNumber());
      assertEquals(1, state.getLogicalLineNumber());
      assertEquals(3, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\n", state.getLastPhysicalLine());
      assertEquals("\n", state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("class C:\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(4, state.getPhysicalLineNumber());
      assertEquals(2, state.getLogicalLineNumber());
      assertEquals(4, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("class C:\n", state.getLastPhysicalLine());
      assertEquals("class C:\n", state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\"\"\"C docstring\"\"\"\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(5, state.getPhysicalLineNumber());
      assertEquals(3, state.getLogicalLineNumber());
      assertEquals(5, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\"\"\"C docstring\"\"\"\n", state.getLastPhysicalLine());
      assertEquals("\t\"\"\"C docstring\"\"\"\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize(), state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize(), state.getLastLogicalLineIndentation());

      state.update("\tdef __init__(self):\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(6, state.getPhysicalLineNumber());
      assertEquals(4, state.getLogicalLineNumber());
      assertEquals(6, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\tdef __init__(self):\n", state.getLastPhysicalLine());
      assertEquals("\tdef __init__(self):\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize(), state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize(), state.getLastLogicalLineIndentation());

      state.update("\t\tpass\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(7, state.getPhysicalLineNumber());
      assertEquals(5, state.getLogicalLineNumber());
      assertEquals(7, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\tpass\n", state.getLastPhysicalLine());
      assertEquals("\t\tpass\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 2,
            state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize() * 2,
            state.getLastLogicalLineIndentation());

      state.update("\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(8, state.getPhysicalLineNumber());
      assertEquals(6, state.getLogicalLineNumber());
      assertEquals(8, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\n", state.getLastPhysicalLine());
      assertEquals("\n", state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\tdef f(self, x):\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(9, state.getPhysicalLineNumber());
      assertEquals(7, state.getLogicalLineNumber());
      assertEquals(9, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\tdef f(self, x):\n", state.getLastPhysicalLine());
      assertEquals("\tdef f(self, x):\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize(), state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize(), state.getLastLogicalLineIndentation());

      state.update("\t\treturn '%d,%d' % (\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(10, state.getPhysicalLineNumber());
      assertEquals(8, state.getLogicalLineNumber());
      assertEquals(10, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\treturn '%d,%d' % (\n", state.getLastPhysicalLine());
      assertEquals("\t\treturn '%d,%d' % (\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 2,
            state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize() * 2,
            state.getLastLogicalLineIndentation());

      state.update("\t\t\t\tx, x)\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(11, state.getPhysicalLineNumber());
      assertEquals(8, state.getLogicalLineNumber());
      assertEquals(10, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t\t\tx, x)\n", state.getLastPhysicalLine());
      assertEquals("\t\treturn '%d,%d' % (\n\t\t\t\tx, x)\n",
            state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 4,
            state.getLastPhysicalLineIndentation());
      assertEquals(state.getTabSize() * 2,
            state.getLastLogicalLineIndentation());

      state.update("d = {\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(12, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("d = {\n", state.getLastPhysicalLine());
      assertEquals("d = {\n", state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t'a' :\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(13, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t'a' :\n", state.getLastPhysicalLine());
      assertEquals("d = {\n\t\t'a' :\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 2,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t\t(0,\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(14, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t\t(0,\n", state.getLastPhysicalLine());
      assertEquals("d = {\n\t\t'a' :\n\t\t\t(0,\n", state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 3,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t\t\t\"\"\"long\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(15, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t\t\t\"\"\"long\n", state.getLastPhysicalLine());
      assertEquals("d = {\n\t\t'a' :\n\t\t\t(0,\n\t\t\t\t\"\"\"long\n",
            state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 4,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t\t\t   string\"\"\"),\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(16, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t\t\t   string\"\"\"),\n", state.getLastPhysicalLine());
      assertEquals(
            "d = {\n\t\t'a' :\n\t\t\t(0,\n\t\t\t\t\"\"\"long\n\t\t\t\t   string\"\"\"),\n",
            state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 4 + 3,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t'b' : (1, 'explicit \\\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(17, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t'b' : (1, 'explicit \\\n", state.getLastPhysicalLine());
      assertEquals(
            "d = {\n\t\t'a' :\n\t\t\t(0,\n\t\t\t\t\"\"\"long\n\t\t\t\t   string\"\"\"),\n\t\t'b' : (1, 'explicit ",
            state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 2,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("\t\t\t\tjoining')\n");
      assertTrue(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertEquals(18, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals("\t\t\t\tjoining')\n", state.getLastPhysicalLine());
      assertEquals(
            "d = {\n\t\t'a' :\n\t\t\t(0,\n\t\t\t\t\"\"\"long\n\t\t\t\t   string\"\"\"),\n\t\t'b' : (1, 'explicit \t\t\t\tjoining')\n",
            state.getLastLogicalLine());
      assertEquals(state.getTabSize() * 4,
            state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());

      state.update("}\n");
      assertTrue(state.isPhysicalLineComplete());
      assertTrue(state.isLogicalLineComplete());
      assertEquals(19, state.getPhysicalLineNumber());
      assertEquals(9, state.getLogicalLineNumber());
      assertEquals(12, state.getLogicalLinePhysicalStartLineNumber());
      assertEquals(
            "d = {\n\t\t'a' :\n\t\t\t(0,\n\t\t\t\t\"\"\"long\n\t\t\t\t   string\"\"\"),\n\t\t'b' : (1, 'explicit \t\t\t\tjoining')\n}\n",
            state.getLastLogicalLine());
      assertEquals(0, state.getLastPhysicalLineIndentation());
      assertEquals(0, state.getLastLogicalLineIndentation());
   }

   public void testGetPrevPhysicalLineIndentation(){
      state.update("def f():\n\tpass\n");
      assertEquals(0, state.getPrevPhysicalLineIndentation());
   }

   public void testGetPrevLogicalLineIndentation(){
      state.update("def f():\n\tpass\n");
      assertEquals(0, state.getPrevLogicalLineIndentation());
   }

   public void testGetPrevPhysicalLineIndentationIndented(){
      state.update("if x:\n\tprint\nelse:\n");
      assertEquals(state.getTabSize(), state.getPrevPhysicalLineIndentation());
   }

   public void testGetPrevLogicalLineIndentationIndented(){
      state.update("if x:\n\tprint\nelse:\n");
      assertEquals(state.getTabSize(), state.getPrevLogicalLineIndentation());
   }

   public void testGetLastPhysicalLineIndentationIllegalState(){
      state.update("print\n");
      state.update("print");
      boolean illegalState = false;
      try{
         state.getLastPhysicalLineIndentation();
      }catch( IllegalStateException e ){
         illegalState = true;
      }
      assertTrue(illegalState);
   }

   public void testGetLastLogicalLineIndentationIllegalState(){
      state.update("print\n");
      state.update("print");
      boolean illegalState = false;
      try{
         state.getLastLogicalLineIndentation();
      }catch( IllegalStateException e ){
         illegalState = true;
      }
      assertTrue(illegalState);
   }

   public void testGetPrevPhysicalLineIndentationIllegalState(){
      state.update("print\n");
      boolean illegalState = false;
      try{
         state.getPrevPhysicalLineIndentation();
      }catch( IllegalStateException e ){
         illegalState = true;
      }
      assertTrue(illegalState);
   }

   public void testGetPrevLogicalLineIndentationIllegalState(){
      state.update("print\n");
      boolean illegalState = false;
      try{
         state.getPrevLogicalLineIndentation();
      }catch( IllegalStateException e ){
         illegalState = true;
      }
      assertTrue(illegalState);
   }

   public void testResetNoInput(){
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }

   public void testResetCompletePhysicalLine(){
      state.update("f(x)\n");
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }

   public void testResetInCompletePhysicalLine(){
      state.update("print");
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }

   public void testResetInParenthesis(){
      state.update("f(x\n");
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }

   public void testResetInString(){
      state.update("f('x\n");
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }

   public void testResetExplicitLineJoining(){
      state.update("print\nf(x,\\\n");
      state.reset();
      assertEquals(PythonState.State.DEFAULT, state.getState());
      assertEquals(0, state.getDepth());
      assertFalse(state.isPhysicalLineComplete());
      assertFalse(state.isLogicalLineComplete());
      assertFalse(state.isExplicitLineJoining());
      assertEquals("", state.getLastPhysicalLine());
      assertEquals("", state.getLastLogicalLine());
      assertEquals(0, state.getPhysicalLineNumber());
      assertEquals(0, state.getLogicalLineNumber());
      assertEquals(0, state.getLogicalLinePhysicalStartLineNumber());
   }
}
