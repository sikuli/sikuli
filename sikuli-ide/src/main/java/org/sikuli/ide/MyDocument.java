package org.sikuli.ide;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.sikuli.script.Debug;

class MyDocument extends DefaultStyledDocument {
   @Override
      public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
         str = str.replaceAll("\t", "  ");
         super.insertString(offs, str, a);
         Debug.log(5, "insertString: " + str); 
      }
}

