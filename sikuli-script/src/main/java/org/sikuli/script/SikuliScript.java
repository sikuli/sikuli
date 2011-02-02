package org.sikuli.script;

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

import org.python.util.jython;

public class SikuliScript {
   public final static int DEFAULT_SERVER_PORT = 7458;

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
      if(App.focus(appName) != null)
         return 0;
      return -1;
   }

   public int openApp(String appName){
      if(App.open(appName) != null)
         return 0;
      return -1;
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

   private static void startServerMode(int port){
      /*
      SikuliServer serv = new SikuliServer(port);
      serv.start();
      */
   }

   private static void startInteractiveMode(String[] args){
      jython.main(args);
   }

   public static void main(String[] args){
      if(args.length == 0 ){
         System.out.println("Usage: sikuli-script [-i] [-s] [file.sikuli]");
         return;
      }
      for (int i = 0; i < args.length; i++) {
         if(args[i].equals("-s")){
            startServerMode(DEFAULT_SERVER_PORT);
         }
         else if(args[i].equals("-i")){
            startInteractiveMode(args);
         }
         else{
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

}

