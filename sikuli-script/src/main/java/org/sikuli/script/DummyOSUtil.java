package org.sikuli.script;

import java.io.*;
import javax.swing.JWindow;

public class DummyOSUtil implements OSUtil {

   public int switchApp(String appName){
      System.err.println("Your OS doesn't support switchApp");
      return -1;
   }

   public int switchApp(String appName, int winNum){
      System.err.println("Your OS doesn't support switchApp");
      return -1;
   }

   public int openApp(String appName){
      System.err.println("Your OS doesn't support openApp");
      return -1;
   }


   public int closeApp(String appName){
      System.err.println("Your OS doesn't support closeApp");
      return -1;
   }

   public Region getWindow(String appName){
      System.err.println("Your OS doesn't support getWindow");
      return null;
   }

   public Region getWindow(String appName, int winNum){
      System.err.println("Your OS doesn't support getWindow");
      return null;
   }
   
   public Region getWindow(int pid){
      System.err.println("Your OS doesn't support getWindow");
      return null;
   }

   public Region getWindow(int pid, int winNum){
      System.err.println("Your OS doesn't support getWindow");
      return null;
   }


   public Region getFocusedWindow(){
      System.err.println("Your OS doesn't support getFocusedWindow");
      return null;
   }
   public void setWindowOpacity(JWindow win, float alpha){
   }
   public void setWindowOpaque(JWindow win, boolean opaque){
   }
   public void bringWindowToFront(JWindow win, boolean ignoreMouse){}
} 


