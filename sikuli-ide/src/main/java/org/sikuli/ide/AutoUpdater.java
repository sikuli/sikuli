/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.sikuli.script.Debug;

public class AutoUpdater {
   static String ServerList[] = {
      "http://sikuli.org/latest-update",
      "http://groups.csail.mit.edu/uid/sikuli/latest-update",
      "http://sikuli.csail.mit.edu/latest-update"
   };

   protected String version, details;


   public AutoUpdater(){
   }

   public AutoUpdater(String[] servers){
      ServerList = servers;
   }

   // is v1 newer than v2?
   // TODO: consider rxxx, RCx, X
   private boolean isNewer(String v1, String v2){
      return v1.compareTo(v2) > 0;
   }

   public boolean checkUpdate(){
      for(String s : ServerList){
         try{
            if(checkUpdate(s)){
               if(isNewer(version, IDESettings.SikuliVersion))
                  return true;
            }
         }
         catch(Exception e){
            Debug.log("Can't get version info from " + s + "\n" + e);
         }
      }
      return false;
   }

   public String getVersion(){   return version;   }
   public String getDetails(){   return details;   }

   boolean checkUpdate(String s) throws IOException, MalformedURLException{
      URL url = new URL(s);
      url.openConnection();
      URLConnection conn = url.openConnection();
      BufferedReader in = new BufferedReader( 
            new InputStreamReader( conn.getInputStream()));
      String line;
      if ((line = in.readLine()) != null){
         version = line.trim();
         details = "";
         while( (line = in.readLine()) != null )
            details += line;
         return true;
      }
      return false;
   }
}

 
class UpdateFrame extends JFrame {
   public UpdateFrame(String title, String text) {
      setTitle(title);
      setSize(300, 200);
      setLocationRelativeTo(getRootPane()); 
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      final JEditorPane p = new JEditorPane("text/html", text);
      p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      p.setEditable(false);
      p.addHyperlinkListener( new HyperlinkListener() {
         public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
               try{
                  SikuliIDE.openURL(e.getURL().toString());
               }
               catch(Exception ex){
                  ex.printStackTrace();
               }
            }
         }
      });
      cp.add(new JScrollPane(p) , BorderLayout.CENTER);
      JPanel buttonPane = new JPanel();
      JButton btnOK = new JButton( SikuliIDE._I("ok") );
      btnOK.addActionListener( new ActionListener(){
         public void actionPerformed(ActionEvent ae){
            UpdateFrame.this.dispose();
         }
      });
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
      buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
      buttonPane.add(Box.createHorizontalGlue());
      buttonPane.add(btnOK);
      buttonPane.add(Box.createHorizontalGlue());
      getRootPane().setDefaultButton(btnOK);

      cp.add(buttonPane, BorderLayout.PAGE_END);
      cp.doLayout();
      pack();

      setVisible(true);
      btnOK.requestFocus();
   }
}

