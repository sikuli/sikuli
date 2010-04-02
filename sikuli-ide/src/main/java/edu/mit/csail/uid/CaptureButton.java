package edu.mit.csail.uid;


import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;


class CaptureButton extends JButton implements ActionListener, Cloneable, Observer{
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
      URL imageURL = SikuliIDE.class.getResource("/icons/capture.png");
      setIcon(new ImageIcon(imageURL));
      setToolTipText("Take a screenshot");
      setBorderPainted(false);
      setMaximumSize(new Dimension(26,26));
      addActionListener(this);
      _line = null;
   }

   public CaptureButton(SikuliPane codePane, Element elmLine){
      this();
      _line = elmLine;
      _codePane = codePane;
      setBorderPainted(true);
      setCursor(new Cursor (Cursor.HAND_CURSOR));
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

   protected void insertAtCursor(JTextPane pane, String imgFilename){
      ImageButton icon = new ImageButton(pane, imgFilename);
      pane.insertComponent(icon);
   }

   public void captureCompleted(String imgFullPath){
      _isCapturing = false;
      if(imgFullPath == null) return;
      Debug.log("captureCompleted: " + imgFullPath);
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
         Debug.log(text);
         for(int i=start;i<end;i++){
            Element elm = doc.getCharacterElement(i);
            if(elm.getName().equals(StyleConstants.ComponentElementName)){
               AttributeSet attr=elm.getAttributes();
               Component com=StyleConstants.getComponent(attr);
               if( com instanceof CaptureButton ){
                  Debug.log("button is at " + i);
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
   }

   public void update(Subject s){
      if(s instanceof CapturePrompt){
         CapturePrompt cp = (CapturePrompt)s;
         ScreenImage simg = cp.getSelection();
         cp.close();
         SikuliIDE ide = SikuliIDE.getInstance();
         SikuliPane pane = ide.getCurrentCodePane();
         String filename = 
            Utils.saveImage(simg.getImage(), pane.getSrcBundle());
         if(filename != null){
            String fullpath = pane.getFileInBundle(filename).getAbsolutePath();
            captureCompleted(Utils.slashify(fullpath,false));
         }
      }
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
            if(delay!=0) ide.setVisible(true);
         }
      };
      t.start();
   }

   public void actionPerformed(ActionEvent e) {
      Debug.log("capture!");
      UserPreferences pref = UserPreferences.getInstance();
      int delay = (int)(pref.getCaptureDelay() * 1000.0) +1;
      capture(delay);
   }
}
