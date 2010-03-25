package edu.mit.csail.uid;

import java.awt.event.*;

enum OS {
   MAC, WINDOWS, LINUX,
   NOT_SUPPORTED
}

public class Env {
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

   public static int getHotkeyModifier(){
      if(getOS() == OS.MAC)
         return KeyEvent.VK_META;
      else
         return KeyEvent.VK_CONTROL;
   }
}
