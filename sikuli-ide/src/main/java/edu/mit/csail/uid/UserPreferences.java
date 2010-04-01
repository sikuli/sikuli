package edu.mit.csail.uid;

import java.util.prefs.*;
import java.awt.Event;

public class UserPreferences {
   static UserPreferences _instance = null;
   Preferences pref = Preferences.userNodeForPackage(SikuliIDE.class);

   public static UserPreferences getInstance(){
      if(_instance == null)
         _instance = new UserPreferences();
      return _instance;
   }

   protected UserPreferences(){
      Debug.log(1, "init user preferences");
   }

   public void setCaptureHotkey(int hkey){
      pref.putInt("CAPTURE_HOTKEY", hkey);
   }

   public void setCaptureHotkeyModifiers(int mod){
      pref.putInt("CAPTURE_HOTKEY_MODIFIERS", mod);
   }

   public void setStopHotkey(int hkey){
      pref.putInt("STOP_HOTKEY", hkey);
   }

   public void setStopHotkeyModifiers(int mod){
      pref.putInt("STOP_HOTKEY_MODIFIERS", mod);
   }

   public int getStopHotkey(){
      return pref.getInt("STOP_HOTKEY", 67); // default: 'c'
   }

   public int getStopHotkeyModifiers(){
      String os = System.getProperty("os.name").toLowerCase();
      int mod = Event.SHIFT_MASK + Event.META_MASK; // mac default
      if( os.startsWith("windows") || os.startsWith("linux"))
         mod = Event.SHIFT_MASK + Event.ALT_MASK; 
      return pref.getInt("GET_HOTKEY_MODIFIERS", mod);
   }


   public int getCaptureHotkey(){
      return pref.getInt("CAPTURE_HOTKEY", 50); // default: '2'
   }

   public int getCaptureHotkeyModifiers(){
      String os = System.getProperty("os.name").toLowerCase();
      int mod = Event.SHIFT_MASK + Event.META_MASK; // mac default
      if( os.startsWith("windows") || os.startsWith("linux"))
         mod = Event.SHIFT_MASK + Event.ALT_MASK; 
      return pref.getInt("CAPTURE_HOTKEY_MODIFIERS", mod);
   }

   public double getCaptureDelay(){
      return pref.getDouble("CAPTURE_DELAY", 1.0);
   }

   public void setCaptureDelay(double v){
      pref.putDouble("CAPTURE_DELAY", v);
   }

   public int getDefaultThumbHeight(){
      return pref.getInt("DEFAULT_THUMB_HEIGHT", 50);
   }

   public void setDefaultThumbHeight(int h){
      pref.putInt("DEFAULT_THUMB_HEIGHT", h);
   }

   public void put(String key, String val){
      pref.put(key, val);
   }

   public String get(String key, String default_){
      return pref.get(key, default_);
   }

}
