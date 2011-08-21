/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.MouseInfo;
import java.lang.reflect.Constructor;


import org.sikuli.script.internal.hotkey.HotkeyManager;


public class Env {
   final static String SikuliVersion = "X-1.0rc3";

   public static Location getMouseLocation() throws HeadlessException{
      Point loc = MouseInfo.getPointerInfo().getLocation();
      return new Location(loc.x, loc.y);
   }

   public static String getOSVersion(){
      return System.getProperty("os.version");
   }

   public static OS getOS(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("mac os x") )
         return OS.MAC;
      else if( os.startsWith("windows"))
         return OS.WINDOWS;
      else if( os.startsWith("linux"))
         return OS.LINUX;
      return OS.NOT_SUPPORTED;
   }

   public static boolean isWindows(){
      return getOS() == OS.WINDOWS;
   }

   public static boolean isLinux(){
      return getOS() == OS.LINUX;
   }

   public static boolean isMac(){
      return getOS() == OS.MAC;
   }

   public static String getSeparator(){
      if(isWindows())
         return ";";
      return ":";
   }

   public static String getClipboard(){
      Transferable content = Clipboard.getSystemClipboard().getContents(null);
      try{
         if(content.isDataFlavorSupported(DataFlavor.stringFlavor))
            return content.getTransferData(DataFlavor.stringFlavor).toString();
      }
      catch(UnsupportedFlavorException e){
         Debug.error("UnsupportedFlavorException: " + content);
      }
      catch(IOException e){
         e.printStackTrace();
      }
      return "";
   }
   
   static String getOSUtilClass(){
      String pkg = "org.sikuli.script.";
      switch(getOS()){
         case MAC:       return pkg+"MacUtil";
         case WINDOWS:   return pkg+"Win32Util";
         case LINUX:     return pkg+"LinuxUtil";
         default:
            Debug.error("Warning: Sikuli doesn't fully support your OS.");
            return pkg+"DummyUtil";
      }
   }

   static OSUtil _osUtil = null;
   public static OSUtil getOSUtil(){
      if(_osUtil == null){
         try{
            Class c = Class.forName(getOSUtilClass());
            Constructor constr = c.getConstructor();
            _osUtil = (OSUtil)constr.newInstance();
         }
         catch(Exception e){
            Debug.error("Can't create OS Util: " + e.getMessage());
         }
      }
      return _osUtil;
   }

   public static boolean isLockOn(char key){
      Toolkit tk = Toolkit.getDefaultToolkit();
      switch(key){
         case '\ue025': return tk.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);
         case '\ue027': return tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
         case '\ue03B': return tk.getLockingKeyState(KeyEvent.VK_NUM_LOCK);
         default:
            return false;
      }
   }

   static int getHotkeyModifier(){
      if(getOS() == OS.MAC)
         return KeyEvent.VK_META;
      else
         return KeyEvent.VK_CONTROL;
   }

   static String getSikuliDataPath(){
      String home, sikuliPath;
      if(isWindows()){
         home = System.getenv("APPDATA");  
         sikuliPath = "Sikuli";
      }
      else if(isMac()){
         home = System.getProperty("user.home") + 
                "/Library/Application Support";
         sikuliPath = "Sikuli";
      }
      else{
         home = System.getProperty("user.home");
         sikuliPath = ".sikuli";
      }
      File fHome = new File(home, sikuliPath);
      return fHome.getAbsolutePath();
   }

   public static String getSikuliVersion(){
      return SikuliVersion;
   }

   public static boolean addHotkey(char key, int modifiers, HotkeyListener listener){
      return HotkeyManager.getInstance().addHotkey(key, modifiers, listener);
   }

   public static boolean removeHotkey(char key, int modifiers){
      return HotkeyManager.getInstance().removeHotkey(key, modifiers);
   }

   public static void cleanUp(){
      HotkeyManager.getInstance().cleanUp();
   }
}
