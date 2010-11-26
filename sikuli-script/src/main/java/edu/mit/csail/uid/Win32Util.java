package edu.mit.csail.uid;

import java.io.*;
import com.wapmx.nativeutils.jniloader.NativeLoader;

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
} 



