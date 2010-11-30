package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
import java.awt.Robot.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;


public class SikuliScript {

   public SikuliScript() throws AWTException{
   }

   public void setShowActions(boolean flag){
      Settings.ShowActions = flag;
      if(flag){
         if(Settings.MoveMouseDelay < 1f)
            Settings.MoveMouseDelay = 1f;
      }
   }

   public String input(String msg){
      return (String)JOptionPane.showInputDialog(msg);
   }
   
   public int switchApp(String appName){
      return App.focus(appName);
   }

   public int openApp(String appName){
      try{
         App.open(appName);
      }
      catch(AppNotFound e){
         Debug.error("Can't find app: " + e.getMessage());
         return -1;
      }
      return 0;
   }

   public int closeApp(String appName){
      return App.close(appName);
   }
   
   public void popup(String message){
      JOptionPane.showMessageDialog(null, message, 
                                    "Sikuli", JOptionPane.PLAIN_MESSAGE);
   }

   public String run(String cmdline){
      String lines="";

      try {
         String line;
         Process p = Runtime.getRuntime().exec(cmdline);
         BufferedReader input = 
            new BufferedReader
            (new InputStreamReader(p.getInputStream()));
         while ((line = input.readLine()) != null) {
            lines = lines + '\n' + line;
         }
      } 
      catch (Exception err) {
         err.printStackTrace();
      }
      return lines;

   }

   public static void main(String[] args){
      if(args.length == 0 ){
         System.out.println("Usage: sikuli-script [file.sikuli]");
         return;
      }
      for (int i = 0; i < args.length; i++) {
         try {
            ScriptRunner runner = new ScriptRunner(args);
            runner.runPython(args[i]);
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }

}

