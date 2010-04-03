package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

public class SikuliEditorKit extends StyledEditorKit {

   public static final String sklDeindentAction = "SKL.DeindentAction";

   private ViewFactory _viewFactory;;

   public SikuliEditorKit() {
      _viewFactory = new SikuliViewFactory();
   }

   private static final TextAction[] defaultActions = {
      new InsertTabAction(),
      new DeindentAction(),
      new InsertBreakAction(),
      /*
      new NextVisualPositionAction(forwardAction, false, SwingConstants.EAST),
      new NextVisualPositionAction(backwardAction, false, SwingConstants.WEST),
      new NextVisualPositionAction(selectionForwardAction, true, SwingConstants.EAST),
      new NextVisualPositionAction(selectionBackwardAction, true, SwingConstants.WEST),
      new NextVisualPositionAction(upAction, false, SwingConstants.NORTH),
      new NextVisualPositionAction(downAction, false, SwingConstants.SOUTH),
      new NextVisualPositionAction(selectionUpAction, true, SwingConstants.NORTH),   
      new NextVisualPositionAction(selectionDownAction, true, SwingConstants.SOUTH),
      */

   };

   public Action[] getActions() {
      return TextAction.augmentList(super.getActions(),
            SikuliEditorKit.defaultActions);
   }  

   @Override
   public ViewFactory getViewFactory() {
      return _viewFactory;
   }

   @Override
   public String getContentType() {
      return "text/python";
   }

   @Override
   public void read(Reader in, Document doc, int pos) 
                           throws IOException, BadLocationException{
      Debug.log("SikuliEditorKit.read");
      super.read(in,doc,pos);

   }

   @Override
   public void write(Writer out, Document doc, int pos, int len) 
                           throws IOException, BadLocationException{
      Debug.log(9,"SikuliEditorKit.write %d %d", pos, len);
      DefaultStyledDocument sdoc = (DefaultStyledDocument)doc;
      int i=pos;

      while(i<pos+len){
         Element e = sdoc.getCharacterElement(i);
         int start = e.getStartOffset(), end = e.getEndOffset();
         if(e.getName().equals(StyleConstants.ComponentElementName)){
            // A image argument to be filled
            AttributeSet attr=e.getAttributes();
            Component com=StyleConstants.getComponent(attr);
            out.write( com.toString() );
         }
         else{
            if( start < pos ) start = pos;
            if( end > pos+len ) end = pos+len;
            out.write(doc.getText(start, end - start));
         }

         i = end;
      }
      out.close();
   }

   public static class NextVisualPositionAction extends TextAction {

      private boolean select;
      private int direction;

      public NextVisualPositionAction(String nm, boolean select, int dir) {
         super(nm);
         this.select = select;
         this.direction = dir;
      }

      public void actionPerformed(ActionEvent e) {
         JTextComponent textArea = (JTextComponent)e.getSource();

         Caret caret = textArea.getCaret();
         int dot = caret.getDot();

         /*
          * Move to the beginning/end of selection on a "non-shifted"
          * left- or right-keypress.  We shouldn't have to worry about
          * navigation filters as, if one is being used, it let us get
          * to that position before.
          */
         if (!select) {
            switch (direction) {
               case SwingConstants.EAST:
                  int mark = caret.getMark();
                  if (dot!=mark) {
                     caret.setDot(Math.max(dot, mark));
                     return;
                  }
                  break;
               case SwingConstants.WEST:
                  mark = caret.getMark();
                  if (dot!=mark) {
                     caret.setDot(Math.min(dot, mark));
                     return;
                  }
                  break;
               default:
            }
         }

         Position.Bias[] bias = new Position.Bias[1];
         Point magicPosition = caret.getMagicCaretPosition();

         try {

            if(magicPosition == null &&
                  (direction == SwingConstants.NORTH ||
                   direction == SwingConstants.SOUTH)) {
               Rectangle r = textArea.modelToView(dot);
               magicPosition = new Point(r.x, r.y);
            }

            NavigationFilter filter = textArea.getNavigationFilter();

            if (filter != null) {
               dot = filter.getNextVisualPositionFrom(textArea, dot,
                     Position.Bias.Forward, direction, bias);
            }
            else {
               dot = textArea.getUI().getNextVisualPositionFrom(
                     textArea, dot,
                     Position.Bias.Forward, direction, bias);
            }
            if (select)
               caret.moveDot(dot);
            else
               caret.setDot(dot);

            if(magicPosition != null &&
                  (direction == SwingConstants.NORTH ||
                   direction == SwingConstants.SOUTH)) {
               caret.setMagicCaretPosition(magicPosition);
                   }

         } catch (BadLocationException ble) {
            ble.printStackTrace();
         }

      }

   }


   public static class InsertTabAction extends TextAction {
         
      public InsertTabAction() {
         super(insertTabAction);
      }  
         
      public InsertTabAction(String name) {
         super(name);
      }  
         
      public void actionPerformed(ActionEvent e) {
         Debug.log(5, "InsertTabAction " + e);
         JTextComponent textArea = (JTextComponent)e.getSource();

         Document document = textArea.getDocument();
         Element map = document.getDefaultRootElement();
         Caret c = textArea.getCaret();
         int dot = c.getDot();
         int mark = c.getMark();
         int dotLine = map.getElementIndex(dot);
         int markLine = map.getElementIndex(mark);

         if (dotLine!=markLine) {
            int first = Math.min(dotLine, markLine);
            int last = Math.max(dotLine, markLine);
            Element elem; 
            int start;
            try {
               for (int i=first; i<last; i++) {
                  elem = map.getElement(i);
                  start = elem.getStartOffset();
                  document.insertString(start, "\t", null);
               }
               elem = map.getElement(last);
               start = elem.getStartOffset();
               if (Math.max(c.getDot(), c.getMark())!=start) {
                  document.insertString(start, "\t", null);
               }
            } catch (BadLocationException ble) {
               ble.printStackTrace();
               UIManager.getLookAndFeel().
                  provideErrorFeedback(textArea);
            }
         }
         else {
            textArea.replaceSelection("\t");
         }
      }

   }

   public static class DeindentAction extends TextAction {
      private Segment s;

      public DeindentAction() {
         this(sklDeindentAction);
      }

      public DeindentAction(String name) {
         super(name);
         s = new Segment();
      }

      public void actionPerformed(ActionEvent e) {
         JTextComponent textArea = (JTextComponent)e.getSource();

         Document document = textArea.getDocument();
         Element map = document.getDefaultRootElement();
         Caret c = textArea.getCaret();
         int dot = c.getDot();
         int mark = c.getMark();
         int line1 = map.getElementIndex(dot);
         int tabSize = 3; //FIXME: replace me with the real tab size

         if (dot!=mark) {
            int line2 = map.getElementIndex(mark);
            int begin = Math.min(line1, line2);
            int end = Math.max(line1, line2);
            Element elem;
            try {
               for (line1=begin; line1<end; line1++) {
                  elem = map.getElement(line1);
                  handleDecreaseIndent(elem, document, tabSize);
               }
               elem = map.getElement(end);
               int start = elem.getStartOffset();
               if (Math.max(c.getDot(),c.getMark())!=start) {
                  handleDecreaseIndent(elem, document, tabSize);
               }
            } catch (BadLocationException ble) {
               ble.printStackTrace();
               UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            }
         }
         else {
            Element elem = map.getElement(line1);
            try {
               handleDecreaseIndent(elem, document, tabSize);
            } catch (BadLocationException ble) {
               ble.printStackTrace();
               UIManager.getLookAndFeel().provideErrorFeedback(textArea);
            }
         }

      }

      private final void handleDecreaseIndent(Element elem, Document doc,
                                 int tabSize) throws BadLocationException {
         int start = elem.getStartOffset();
         int end = elem.getEndOffset() - 1; 
         doc.getText(start,end-start, s);
         int i = s.offset;
         end = i+s.count;
         if (end>i) {
            if (s.array[i]=='\t') {
               doc.remove(start, 1);
            }
            else if (s.array[i]==' ') {
               i++;
               int toRemove = 1;
               while (i<end && s.array[i]==' ' && toRemove<tabSize) {
                  i++;
                  toRemove++;
               }
               doc.remove(start, toRemove);
            }
         }
      }
   }

   public static class InsertBreakAction extends TextAction {

      public InsertBreakAction() {
         super(insertBreakAction);
      }

      public InsertBreakAction(String name) {
         super(name);
      }

      public void insertBreak(JTextComponent textArea){
         boolean noSelection = textArea.getSelectionStart()==textArea.getSelectionEnd();

         if (noSelection ) {
            insertNewlineWithAutoIndent(textArea);
         }
         else {
            textArea.replaceSelection("\n");
         }
      }

      public void actionPerformed(ActionEvent e) {
         JTextComponent textArea = (JTextComponent)e.getSource();
         insertBreak(textArea);
      }

      static boolean isWhitespace(char ch){
         return ch==' ' || ch=='\t';
      }

      static String getLeadingWhitespace(StyledDocument doc, int head, int len)
                                                   throws BadLocationException{
         String ret = "";

         int pos = head;
         while(pos<head+len){
            Element e = doc.getCharacterElement(pos);
            int eStart = e.getStartOffset(), eEnd = e.getEndOffset();
            if(e.getName().equals(StyleConstants.ComponentElementName))
               break;
            String space=getLeadingWhitespace(doc.getText(eStart, eEnd-eStart));
            ret += space;
            if(space.length() < eEnd - eStart)
               break;

            pos = eEnd;
         }
         return ret;
      }
      
      static String getLeadingWhitespace(String text){
         int len = text.length();
         int count = 0;
         while (count<len && isWhitespace(text.charAt(count))) 
            count++;
         return text.substring(0, count);
      }

      private static final int atEndOfLine(int pos, String s, int sLen) {
         for (int i=pos; i<sLen; i++) {
            if (!isWhitespace(s.charAt(i)))
               return i;
         }
         return -1;
      }

      private void insertNewlineWithAutoIndent(JTextComponent txt) {

         try {
            int caretPos = txt.getCaretPosition();
            StyledDocument doc = (StyledDocument)txt.getDocument();
            Element map = doc.getDefaultRootElement();
            int lineNum = map.getElementIndex(caretPos);
            Element line = map.getElement(lineNum);
            int start = line.getStartOffset();
            int end = line.getEndOffset()-1; 
            int len = end-start;
            String s = doc.getText(start, len);

            String leadingWS = getLeadingWhitespace(doc, start, caretPos-start);
            StringBuffer sb = new StringBuffer("\n");
            sb.append(leadingWS);

            // If there is only whitespace between the caret and
            // the EOL, pressing Enter auto-indents the new line to
            // the same place as the previous line.
            int nonWhitespacePos = atEndOfLine(caretPos-start, s, len);
            if (nonWhitespacePos==-1) {
               if (leadingWS.length()==len) {
                  // If the line was nothing but whitespace, select it
                  // so its contents get removed.
                  txt.setSelectionStart(start);
                  txt.setSelectionEnd(end);
               }
               txt.replaceSelection(sb.toString());
            }

            // If there is non-whitespace between the caret and the
            // EOL, pressing Enter takes that text to the next line
            // and auto-indents it to the same place as the last
            // line.
            else {
               /*
               sb.append(s.substring(nonWhitespacePos));
               ((AbstractDocument)doc).replace(caretPos, end - caretPos, sb.toString(), null);
               txt.setCaretPosition(caretPos + leadingWS.length()+1);
               */
               doc.insertString(caretPos, sb.toString(), null);
            }

            // FIXME: examine the previous line and do auto-indent if 
            // 'if', 'while', 'for', or 'with' is seen.
            /*
            if (txt.getShouldIndentNextLine(lineNum)) {
               txt.replaceSelection("\t");
            }
            */

         } catch (BadLocationException ble) { 
            txt.replaceSelection("\n");
            ble.printStackTrace();
         }

      }


   }

}
