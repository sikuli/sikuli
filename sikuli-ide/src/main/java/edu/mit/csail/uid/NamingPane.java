package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.imageio.*;

public class NamingPane extends JPanel {
   final static int TXT_FILE_EXT_LENGTH = 4;
   final static int TXT_FILENAME_LENGTH = 20;
   final static int MAX_OCR_TEXT_LENGTH = 12;
   final static int THUMB_MAX_HEIGHT = 200;

   ImageButton _imgBtn;
   JTextField _txtPath, _txtFileExt;
   JComboBox _txtFilename;
   String _oldFilename;

   static String _I(String key, Object... args){ 
      return I18N._I(key, args);
   }

   public NamingPane(ImageButton imgBtn){
      super(new GridBagLayout());
      _imgBtn = imgBtn;
      init();
   }

   private String getFilenameWithoutExt(File f){
      String name = f.getName();
      int pos = name.lastIndexOf('.');
      return name.substring(0, pos);
   }

   private String getFileExt(File f){
      String name = f.getName();
      int pos = name.lastIndexOf('.');
      return name.substring(pos);
   }

   public static String getFilenameFromImage(BufferedImage img){
      TextRecognizer tr = TextRecognizer.getInstance();
      String text = tr.recognize(img);
      text = text.replaceAll("\\W","");
      if( text.length() > MAX_OCR_TEXT_LENGTH )
         return text.substring(0, MAX_OCR_TEXT_LENGTH);
      return text;
   }

   private void init(){
      JLabel lblPath = new JLabel(_I("lblPath"));
      JLabel lblFilename = new JLabel(_I("lblFilename"));

      String filename = _imgBtn.getImageFilename();
      File f = new File(filename);
      String fullpath = f.getParent();
      filename = getFilenameWithoutExt(f);
      _oldFilename = filename;

      BufferedImage thumb = _imgBtn.createThumbnailImage(THUMB_MAX_HEIGHT);
      Border border = LineBorder.createGrayLineBorder();
      JLabel lblThumb = new JLabel(new ImageIcon(thumb));
      lblThumb.setBorder(border);

      _txtPath = new JTextField(fullpath, TXT_FILENAME_LENGTH);
      _txtPath.setEditable(false);
      _txtPath.setEnabled(false);

      String[] candidates = new String[] {filename};
      String ocrText = getFilenameFromImage(thumb);
      if(ocrText.length()>0)
         candidates = new String[] {filename, ocrText};

      _txtFilename = new AutoCompleteCombo(candidates);

      _txtFileExt = new JTextField(getFileExt(f), TXT_FILE_EXT_LENGTH);
      _txtFileExt.setEditable(false);
      _txtFileExt.setEnabled(false);

      GridBagConstraints c = new GridBagConstraints();
      c.fill = 0;
      c.gridy = 0;
      c.gridwidth = 3;
      c.insets = new Insets(0,10,20,10);
      this.add(lblThumb, c);

      c = new GridBagConstraints();
      c.fill = 1;
      c.gridy = 1;
      this.add( lblPath, c );
      c.gridx = 1;
      c.gridwidth = 2;
      this.add( _txtPath, c );

      c = new GridBagConstraints();
      c.gridy = 2;
      c.fill = 0;
      this.add( lblFilename, c );
      this.add( _txtFilename, c );
      this.add( _txtFileExt, c );
   }

   public String getAbsolutePath(){
      return _txtPath.getText() + File.separatorChar + 
             _txtFilename.getSelectedItem() + _txtFileExt.getText(); 
   }

   public boolean isDirty(){
      return _oldFilename != _txtFilename.getSelectedItem();
   }
}

class AutoCompleteCombo extends JComboBox {
   final static int TXT_FILENAME_LENGTH = 20;
   public int caretPos = 0;
   public JTextField editor = null;

   public AutoCompleteCombo(final Object items[]) {
      super(items);
      this.setEditable(true);
      setHook();
      //hideDropDownButton();
   }

   private void hideDropDownButton(){
      for (Component component : this.getComponents()) 
         if (component instanceof AbstractButton && component.isVisible()){
            component.setVisible(false);
            this.revalidate();
         }
   }

   public void setSelectedIndex(int ind) {
      super.setSelectedIndex(ind);
      editor.setText(getItemAt(ind).toString());
      editor.setSelectionEnd(caretPos + editor.getText().length());
      editor.moveCaretPosition(caretPos);
   }

   public void setHook(){
      ComboBoxEditor anEditor = this.getEditor();
      if (anEditor.getEditorComponent() instanceof JTextField) {
         editor = (JTextField) anEditor.getEditorComponent();
         editor.setColumns(TXT_FILENAME_LENGTH);
         editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ev) {
               char key = ev.getKeyChar();
               if (!(Character.isLetterOrDigit(key) || Character
                     .isSpaceChar(key)))
                  return;
               caretPos = editor.getCaretPosition();
               String text = "";
               try {
                  text = editor.getText(0, caretPos);
               } catch (Exception ex) {
                  ex.printStackTrace();
               }
               int n = getItemCount();
               for (int i = 0; i < n; i++) {
                  int ind = ((String) getItemAt(i)).indexOf(text);
                  if (ind == 0) {
                     setSelectedIndex(i);
                     return;
                  }
               }
            }
         });
      }
   }

}

