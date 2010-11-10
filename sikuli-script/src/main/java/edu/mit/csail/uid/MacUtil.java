package edu.mit.csail.uid;

import java.io.*;

public class MacUtil implements OSUtil {

   public int switchApp(String appName){
      return openApp(appName);
   }

   public int openApp(String appName){
      try{
         Debug.history("openApp: \"" + appName + "\"");
         String cmd[] = {"open","-a", appName};
         Process p = Runtime.getRuntime().exec(cmd);
         /*
         BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
         String line;
         while((line=br.readLine()) != null)
            Debug.log(line);
         br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
         while((line=br.readLine()) != null)
            Debug.error(line);
         */
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         Debug.error(e.getMessage());
         return -1;
      }
   }


   public int closeApp(String appName){
      Debug.history("closeApp: " + appName);
      try{
         String cmd[] = {"sh", "-c", 
            "ps aux |  grep " + appName + " | awk '{print $2}' | xargs kill"};
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

