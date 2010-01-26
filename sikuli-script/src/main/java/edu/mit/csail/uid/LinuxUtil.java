package edu.mit.csail.uid;

import java.io.*;

public class LinuxUtil implements OSUtil {

   public int switchApp(String appName){
      try{
         String cmd[] = {"wmctrl", "-a", appName};
         System.out.println("switchApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }

   public int openApp(String appName){
      try{
         System.out.println("openApp: " + appName);
         Process p = Runtime.getRuntime().exec(appName);
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
         System.out.println("closeApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }
} 


