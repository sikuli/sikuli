package edu.mit.csail.uid;

import java.io.*;

public class MacUtil implements OSUtil {

   public int switchApp(String appName){
      try{
         String cmd[] = {"open", "-a", appName};
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
      return switchApp(appName);
   }


   public int closeApp(String appName){
      try{
         String cmd[] = {"sh", "-c", 
            "ps aux |  grep " + appName + " | awk '{print $2}' | xargs kill"};
         System.out.println("closeApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
/*
         DataOutputStream out = new DataOutputStream(p.getOutputStream());
         out.writeUTF(cmdKill);
         out.close();
         BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String line;
         while((line=in.readLine()) != null)
            System.out.println(line);
         in.close();
*/
         return p.exitValue();
      }
      catch(Exception e){
         return -1;
      }
   }
} 

