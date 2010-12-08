package edu.mit.csail.uid;

import java.io.*;
import java.awt.Window;
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
      //FIXME
      return null;
   }

   public Region getFocusedWindow(){
      //FIXME
      return null;
   }

   public static native void bringWindowToFront(Window win, boolean ignoreMouse);

   public static void setWindowOpacity(Window win, float alpha){
      AWTUtilities.setWindowOpacity(win, alpha);
   }




} 
