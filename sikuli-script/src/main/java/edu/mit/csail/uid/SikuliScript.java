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
   private OverlayWindow _overlay;
   private OSUtil _osUtil;
   private int _waitBeforeAction = 3000;
   private boolean _stopIfWaitingFailed = true;
   private boolean _showActions = false;

   public SikuliScript() throws AWTException{
      _overlay = new OverlayWindow();
      _osUtil = Env.createOSUtil();
   }

   public void setShowActions(boolean flag){
      _showActions = flag;
   }

   public String input(String msg){
      return (String)JOptionPane.showInputDialog(msg);
   }
   
   public int switchApp(String appName){
      return _osUtil.switchApp(appName);
   }

   public int openApp(String appName){
      return _osUtil.openApp(appName);
   }

   public int closeApp(String appName){
      return _osUtil.closeApp(appName);
   }
   
   public void popup(String message){
      JOptionPane.showMessageDialog(null, message, 
                                    "Sikuli", JOptionPane.PLAIN_MESSAGE);
   }

   private void showClick(int x, int y, int w, int h){
      if(_showActions){
         _overlay.showTarget(x,y,w,h);
      }
   }

   private void showDragDrop(int x, int y, int x2, int y2){
      if(_showActions){
         _overlay.showDragDrop(x,y, x2, y2);
      }
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
      try{
         ScriptRunner runner = new ScriptRunner();
         runner.runPython(args[0]);
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

}

