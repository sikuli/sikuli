package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

public class NamingPane extends JPanel {
   final static int TXT_FILENAME_LENGTH = 20;
   final static int TXT_FILE_EXT_LENGTH = 4;
   final static int THUMB_MAX_HEIGHT = 200;

   ImageButton _imgBtn;
   JTextField _txtPath, _txtFilename, _txtFileExt;
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
      _txtFilename = new JTextField(filename, TXT_FILENAME_LENGTH);
      _txtFileExt = new JTextField(getFileExt(f), TXT_FILE_EXT_LENGTH);
      _txtFileExt.setEditable(false);
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
      c.fill = 1;
      this.add( lblFilename, c );
      this.add( _txtFilename, c );
      this.add( _txtFileExt, c );
   }

   public String getAbsolutePath(){
      return _txtPath.getText() + File.separatorChar + 
             _txtFilename.getText() + _txtFileExt.getText(); 
   }

   public boolean isDirty(){
      return _oldFilename != _txtFilename.getText();
   }
}

