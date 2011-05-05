/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;


import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;

import org.sikuli.script.Debug;
import org.sikuli.script.CapturePrompt;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Observer;
import org.sikuli.script.Subject;

class CaptureButton extends ToolbarButton implements ActionListener, Cloneable, Observer{
   protected Element _line;
   protected SikuliPane _codePane;
   protected boolean _isCapturing;

   /*
   public String toString(){
      return " \"CAPTURE-BUTTON\" ";
   }
   */

   public CaptureButton(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/camera-icon.png");
      setIcon(new ImageIcon(imageURL));
      UserPreferences pref = UserPreferences.getInstance();
      String strHotkey = Utils.convertKeyToText(
            pref.getCaptureHotkey(), pref.getCaptureHotkeyModifiers() );
      setToolTipText(SikuliIDE._I("btnCaptureHint", strHotkey));
      setText(SikuliIDE._I("btnCaptureLabel"));
      //setBorderPainted(false);
      //setMaximumSize(new Dimension(26,26));
      addActionListener(this);
      _line = null;
   }

   public CaptureButton(SikuliPane codePane, Element elmLine){
      this();
      _line = elmLine;
      _codePane = codePane;
      setUI(UIManager.getUI(this));
      setBorderPainted(true);
      setCursor(new Cursor (Cursor.HAND_CURSOR));
      setText(null);
      URL imageURL = SikuliIDE.class.getResource("/icons/capture.png");
      setIcon(new ImageIcon(imageURL));
   }

   public boolean hasNext(){  return false;  }
   public CaptureButton getNextDiffButton(){ return null; }
   public void setParentPane(SikuliPane parent){
      _codePane = parent;
   }

   public void setDiffMode(boolean flag){}
   
   public void setSrcElement(Element elmLine){
      _line = elmLine;
   }

   public Element getSrcElement(){  return _line;  }

   protected void insertAtCursor(SikuliPane pane, String imgFilename){
      ImageButton icon = new ImageButton(pane, imgFilename);
      pane.insertComponent(icon);
      pane.requestFocus();
   }

   public void captureCompleted(String imgFullPath){
      _isCapturing = false;
      if(imgFullPath == null) return;
      Debug.log(2, "captureCompleted: " + imgFullPath);
      Element src = getSrcElement();
      if( src == null ){
         if(_codePane == null)
            insertAtCursor(SikuliIDE.getInstance().getCurrentCodePane(), imgFullPath);
         else
            insertAtCursor(_codePane, imgFullPath);
         return;
      }

      int start = src.getStartOffset();
      int end = src.getEndOffset();
      int old_sel_start = _codePane.getSelectionStart(),
          old_sel_end = _codePane.getSelectionEnd();
      try{
         StyledDocument doc = (StyledDocument)src.getDocument();
         String text = doc.getText(start, end-start);
         Debug.log(3, text);
         for(int i=start;i<end;i++){
            Element elm = doc.getCharacterElement(i);
            if(elm.getName().equals(StyleConstants.ComponentElementName)){
               AttributeSet attr=elm.getAttributes();
               Component com=StyleConstants.getComponent(attr);
               if( com instanceof CaptureButton ){
                  Debug.log(5, "button is at " + i);
                  int oldCaretPos = _codePane.getCaretPosition();
                  _codePane.select(i, i+1);
                  ImageButton icon = new ImageButton(_codePane, imgFullPath);
                  _codePane.insertComponent(icon);
                  _codePane.setCaretPosition(oldCaretPos);
                  break;
               }
            }
         }
      }
      catch(BadLocationException ble){
         ble.printStackTrace();
      }
      _codePane.select(old_sel_start, old_sel_end);
      _codePane.requestFocus();
   }

   private String getFilenameFromUser(String hint){
      return (String)JOptionPane.showInputDialog(
            _codePane,
            I18N._I("msgEnterScreenshotFilename"),
            I18N._I("dlgEnterScreenshotFilename"),
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            hint);
   }

   public void update(Subject s){
      if(s instanceof CapturePrompt){
         CapturePrompt cp = (CapturePrompt)s;
         ScreenImage simg = cp.getSelection();
         if(simg==null){
            captureCompleted(null);
            return;
         }
         cp.close();
         SikuliIDE ide = SikuliIDE.getInstance();
         SikuliPane pane = ide.getCurrentCodePane();
         int naming = UserPreferences.getInstance().getAutoNamingMethod();
         String filename;
         if(naming == UserPreferences.AUTO_NAMING_TIMESTAMP)
            filename = Utils.getTimestamp();
         else if(naming == UserPreferences.AUTO_NAMING_OCR){
            filename = NamingPane.getFilenameFromImage(simg.getImage());
            if(filename == null || filename.length() == 0)
               filename = Utils.getTimestamp();
         }
         else{
            String hint = NamingPane.getFilenameFromImage(simg.getImage());
            filename = getFilenameFromUser(hint);
            if(filename == null){
               captureCompleted(null);
               return;
            }

         }
         String fullpath = 
            Utils.saveImage(simg.getImage(), filename, pane.getSrcBundle());
         if(fullpath != null){
            //String fullpath = pane.getFileInBundle(filename).getAbsolutePath();
            captureCompleted(Utils.slashify(fullpath,false));
         }
      }
   }

   public void captureWithAutoDelay(){
      UserPreferences pref = UserPreferences.getInstance();
      int delay = (int)(pref.getCaptureDelay() * 1000.0) +1;
      capture(delay);
   }

   public void capture(final int delay){
      if(_isCapturing)
         return;
      _isCapturing = true;
      Thread t = new Thread("capture"){
         public void run(){
            SikuliIDE ide = SikuliIDE.getInstance();
            if(delay!=0) ide.setVisible(false);
            try{
               Thread.sleep(delay);
            }
            catch(Exception e){}
            (new CapturePrompt(null, CaptureButton.this)).prompt();
            try{
               Thread.sleep(500);
            }
            catch(Exception e){}
            if(delay!=0) ide.setVisible(true);
         }
      };
      t.start();
   }

   public void actionPerformed(ActionEvent e) {
      Debug.log(2, "capture!");
      captureWithAutoDelay();
   }
}
