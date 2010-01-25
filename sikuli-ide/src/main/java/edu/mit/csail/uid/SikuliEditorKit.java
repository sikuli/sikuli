package edu.mit.csail.uid;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

public class SikuliEditorKit extends StyledEditorKit {

   private ViewFactory _viewFactory;;

   public SikuliEditorKit() {
      _viewFactory = new SikuliViewFactory();
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

}
