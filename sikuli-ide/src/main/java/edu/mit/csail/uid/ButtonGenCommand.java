package edu.mit.csail.uid;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.net.URL;


public class ButtonGenCommand extends JButton implements ActionListener{
   String _cmd;
   String[] _params;

   public ButtonGenCommand(String cmd, String... params){
      super(getRichRepresentation(cmd, params, false));
      _cmd = cmd;
      _params = params;
      setToolTipText(getRichRepresentation(cmd, params, true));
      addActionListener(this);
   }

   static String getRichRepresentation(String cmd, String[] params, boolean showOptParam){
      URL imgPattern = SikuliIDE.class.getResource("/icons/capture-small.png");
      String ret = "<html><table><tr><td valign=\"middle\">" + cmd + "(";
      int count = 0;
      for(String p : params){
         String item = "";
         if(count++ != 0)
            item += ", ";
         if(p.equals("PATTERN"))
            item += "<td><img src=\"" + imgPattern + "\">";
         else{
            if(p.startsWith("[") && p.endsWith("]")){
               if(showOptParam)
                  item += "<td valign=\"middle\">" + p;
            }
            else
               item += "<td valign=\"middle\">" + p;
         }
         if(!item.equals(", "))
            ret += item;
      }
      ret += "<td>)";
      return ret;
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
