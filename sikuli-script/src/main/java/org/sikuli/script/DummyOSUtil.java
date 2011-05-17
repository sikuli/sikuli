/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.io.*;
import java.awt.Window;

public class DummyOSUtil implements OSUtil {

   public int switchApp(String appName){
      Debug.error("Your OS doesn't support switchApp");
      return -1;
   }

   public int switchApp(String appName, int winNum){
      Debug.error("Your OS doesn't support switchApp");
      return -1;
   }

   public int switchApp(int pid, int num){
      Debug.error("Your OS doesn't support switchApp");
      return -1;
   }

   public int openApp(String appName){
      Debug.error("Your OS doesn't support openApp");
      return -1;
   }


   public int closeApp(String appName){
      Debug.error("Your OS doesn't support closeApp");
      return -1;
   }

   public int closeApp(int pid){
      Debug.error("Your OS doesn't support closeApp");
      return -1;
   
   }

   public Region getWindow(String appName){
      Debug.error("Your OS doesn't support getWindow");
      return null;
   }

   public Region getWindow(String appName, int winNum){
      Debug.error("Your OS doesn't support getWindow");
      return null;
   }
   
   public Region getWindow(int pid){
      Debug.error("Your OS doesn't support getWindow");
      return null;
   }

   public Region getWindow(int pid, int winNum){
      Debug.error("Your OS doesn't support getWindow");
      return null;
   }


   public Region getFocusedWindow(){
      Debug.error("Your OS doesn't support getFocusedWindow");
      return null;
   }
   public void setWindowOpacity(Window win, float alpha){
   }
   public void setWindowOpaque(Window win, boolean opaque){
   }
   public void bringWindowToFront(Window win, boolean ignoreMouse){}
} 


