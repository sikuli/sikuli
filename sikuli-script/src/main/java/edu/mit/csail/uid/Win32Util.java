package edu.mit.csail.uid;

import java.io.*;
import java.awt.Window;
import java.awt.Rectangle;
import com.wapmx.nativeutils.jniloader.NativeLoader;
import com.sun.awt.AWTUtilities;

public class Win32Util implements OSUtil {

   static {
      try{
         NativeLoader.loadLibrary("Win32Util");
         System.out.println("Windows utilities loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public native int switchApp(String appName);
   public native int openApp(String appName);
   public native int closeApp(String appName);
   public Region getWindow(String appName){
      return getWindow(appName, 0);
   }

   public Region getWindow(String appName, int winNum){
      long pid = getPID(appName);
      Rectangle rect = getRegion(pid, winNum);
      Debug.log("getWindow: " + rect);
      if(rect != null)
         return new Region(rect);
      return null;
   }

   public Region getFocusedWindow(){
      Rectangle rect = getFocusedRegion();
      if(rect != null)
         return new Region(rect);
      return null;
   }

   public static native void bringWindowToFront(Window win, boolean ignoreMouse);
   public static native long getPID(String appName);
   public static native Rectangle getRegion(long pid, int winNum);
   public static native Rectangle getFocusedRegion();

   public static void setWindowOpacity(Window win, float alpha){
      AWTUtilities.setWindowOpacity(win, alpha);
   }




} 
