/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.internal.hotkey;

import java.lang.reflect.Constructor;

import java.awt.event.KeyEvent;

import org.sikuli.script.HotkeyListener;
import org.sikuli.script.Env;
import org.sikuli.script.Debug;

public class HotkeyManager {
   protected static HotkeyManager _instance = null;

   private static String getOSHotkeyManagerClass(){
      String pkg = "org.sikuli.script.internal.hotkey.";
      switch(Env.getOS()){
         case MAC:       return pkg+"MacHotkeyManager";
         case WINDOWS:   return pkg+"WindowsHotkeyManager";
         case LINUX:     return pkg+"LinuxHotkeyManager";
         default:
                         Debug.error("Error: Hotkey registration is not supported on your OS.");
      }
      return null;
   }

   protected String getKeyCodeText(int key){
      return KeyEvent.getKeyText(key).toUpperCase();
   }

   protected String getKeyModifierText(int modifiers){
      String txtMod = KeyEvent.getKeyModifiersText(modifiers).toUpperCase();
      if(Env.isMac()){
         txtMod = txtMod.replace("META","CMD");
         txtMod = txtMod.replace("WINDOWS","CMD");
      }
      else{
         txtMod = txtMod.replace("META","WIN");
         txtMod = txtMod.replace("WINDOWS","WIN");
      }
      return txtMod;
   }

   public static HotkeyManager getInstance(){
      if(_instance==null){
         String cls = getOSHotkeyManagerClass();
         if(cls != null){
            try{
               Class c = Class.forName(cls);
               Constructor constr = c.getConstructor();
               _instance = (HotkeyManager)constr.newInstance();
            }
            catch(Exception e){
               Debug.error("Can't create " + cls + ": " + e.getMessage());
            }
         }
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

   public void cleanUp(){
      _instance.cleanUp();
   }
}
