/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.internal.hotkey;

public class HotkeyManager {
   protected static HotkeyManager _instance = null;

   public static HotkeyManager getInstance(){
      if(_instance==null){
            _instance = new MacHotkeyManager();
            //FIXME
         /*
         if(Env.isWindows())
            _instance = new WindowsHotkeyManager();
         else if(Env.isMac())
            _instance = new MacHotkeyManager();
         else if(Env.isLinux())
            _instance = new LinuxHotkeyManager();
         else{
            Debug.error("Hotkey manager doesn't support your OS.");
         }
         */
      }
      return _instance;
   }
   
   /**
    *  install a hotkey listener.
    *
    *  @return true if success. false otherwise.
    */
   public boolean addHotkey(int keyCode, int modifiers, HotkeyListener listener){
      return _instance.addHotkey(keyCode, modifiers, listener);
   }

   /**
    *  uninstall a hotkey listener.
    *
    *  @return true if success. false otherwise.
    */
   public boolean removeHotkey(int keyCode, int modifiers){
      return _instance.removeHotkey(keyCode, modifiers);
   }
}
