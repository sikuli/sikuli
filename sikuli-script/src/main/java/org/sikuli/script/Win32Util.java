package org.sikuli.script;

import java.io.*;
import java.awt.Window;
import javax.swing.JWindow;
import java.awt.Rectangle;
import com.wapmx.nativeutils.jniloader.NativeLoader;
import com.sun.awt.AWTUtilities;

public class Win32Util implements OSUtil {

   static {
      try{
         NativeLoader.loadLibrary("Win32Util");
         Debug.info("Windows utilities loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   // compatible to the old switchApp
   public int switchApp(String appName){
      return switchApp(appName, 0);
   }

   public native int switchApp(String appName, int num);
   public native int switchApp(int pid, int num);

   public native int openApp(String appName);

   public native int closeApp(String appName);
   public native int closeApp(int pid);

   public Region getWindow(String appName){
      return getWindow(appName, 0);
   }

   public Region getWindow(int pid){
      return getWindow(pid, 0);
   }

   public Region getWindow(String appName, int winNum){
      long hwnd = getHwnd(appName, winNum);
      return _getWindow(hwnd, winNum);
   }

   public Region getWindow(int pid, int winNum){
      long hwnd = getHwnd(pid, winNum);
      return _getWindow(hwnd, winNum);
   }

   protected Region _getWindow(long hwnd, int winNum){
      Rectangle rect = getRegion(hwnd, winNum);
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

   public native void bringWindowToFront(JWindow win, boolean ignoreMouse);
   public static native long getHwnd(String appName, int winNum);
   public static native long getHwnd(int pid, int winNum);
   public static native Rectangle getRegion(long hwnd, int winNum);
   public static native Rectangle getFocusedRegion();

   public void setWindowOpacity(JWindow win, float alpha){
      AWTUtilities.setWindowOpacity(win, alpha);
   }

   public void setWindowOpaque(JWindow win, boolean opaque){
      AWTUtilities.setWindowOpaque(win, opaque);
   }


} 
