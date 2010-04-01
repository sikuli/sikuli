package edu.mit.csail.uid;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;


public class ButtonGenCommand extends JButton implements ActionListener{
   String _cmd;
   String[] _params;

   public ButtonGenCommand(String cmd, String... params){
      super(getTextRepresentation(cmd, params));
      _cmd = cmd;
      _params = params;
      addActionListener(this);
   }

   static String getTextRepresentation(String cmd, String[] params){
      String ret = "" + cmd + "(";
      int count = 0;
      for(String p : params){
         ret += p;
         if(++count < params.length)
            ret += ", ";
      }
      ret += ")";
      return ret;
   }

   public String toString(){
      return getTextRepresentation(_cmd, _params);
   }

   public void actionPerformed(ActionEvent ae){
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane pane = ide.getCurrentCodePane();
      UserPreferences pref = UserPreferences.getInstance();
      pane.insertString(_cmd + "(");
      for(String p : _params){
         if( p.equals("PATTERN") ){
            Document doc = pane.getDocument();
            Element root = doc.getDefaultRootElement();
            int pos = pane.getCaretPosition();
            int lineIdx = root.getElementIndex(pos);
            Element line = root.getElement(lineIdx);
            CaptureButton btnCapture = new CaptureButton(pane, line);
            pane.insertComponent(btnCapture);
            if( pref.getAutoCaptureForCmdButtons() ){
               btnCapture.capture(300);
            }
         }
      }
      pane.insertString(")");
      (new SikuliEditorKit.InsertBreakAction()).insertBreak(pane);
   }

}
