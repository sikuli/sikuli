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
} 


