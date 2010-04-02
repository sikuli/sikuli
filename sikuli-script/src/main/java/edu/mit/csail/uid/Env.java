package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
import java.awt.MouseInfo;


public class Env {
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

   static OSUtil createOSUtil(){
      switch(getOS()){
         case MAC:       return new MacUtil();
         case WINDOWS:   return new Win32Util();
         case LINUX:     return new LinuxUtil();
         default:
            System.err.println("Warning: Sikuli doesn't fully support your OS");
            return new DummyOSUtil();
      }
   }

   static int getHotkeyModifier(){
      if(getOS() == OS.MAC)
         return KeyEvent.VK_META;
      else
         return KeyEvent.VK_CONTROL;
   }
}
