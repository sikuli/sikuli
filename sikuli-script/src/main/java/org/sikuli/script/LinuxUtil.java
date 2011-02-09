package org.sikuli.script;

import java.io.*;
import javax.swing.JWindow;
//import com.sun.awt.AWTUtilities;

public class LinuxUtil implements OSUtil {

   public int switchApp(String appName, int winNum){
      try{
         String cmd[] = {"wmctrl", "-a", appName};
         Debug.history("switchApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }

   public int switchApp(String appName){
      return switchApp(appName, 0);
   }

   public int openApp(String appName){
      try{
         Debug.history("openApp: " + appName);
         String cmd[] = {"sh", "-c", appName + " &"};
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }


   public int closeApp(String appName){
      try{
         String cmd[] = {"killall", appName};
         Debug.history("closeApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }

   public Region getFocusedWindow(){
      //FIXME
      return null;
   }
   public Region getWindow(String appName){
      return getWindow(appName, 0);
   }

   public Region getWindow(String appName, int winNum){
      //FIXME
      return null;
   }

   public Region getWindow(int pid){
      return null;
   }

   public Region getWindow(int pid, int winNum){
      return null;
   }

   public int closeApp(int pid){
      return -1;
   }

   public int switchApp(int pid, int num){
      return -1;
   }

   public void setWindowOpacity(JWindow win, float alpha){
      //AWTUtilities.setWindowOpacity(win, alpha);
   }

   public void setWindowOpaque(JWindow win, boolean opaque){
      //AWTUtilities.setWindowOpaque(win, opaque);
   }


   public void bringWindowToFront(JWindow win, boolean ignoreMouse){}
} 


