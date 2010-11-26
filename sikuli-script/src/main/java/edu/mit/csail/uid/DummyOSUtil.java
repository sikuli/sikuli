package edu.mit.csail.uid;

import java.io.*;

public class DummyOSUtil implements OSUtil {

   public int switchApp(String appName){
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
   public Region getFocusedWindow(){
      System.err.println("Your OS doesn't support getFocusedWindow");
      return null;
   }
} 


