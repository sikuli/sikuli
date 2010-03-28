package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
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

}
