package org.sikuli.ide;

import java.util.prefs.*;
import java.util.Date;
import java.awt.Event;

import org.sikuli.script.Debug;

public class UserPreferences {
   final static int AUTO_NAMING_TIMESTAMP = 0;
   final static int AUTO_NAMING_OCR = 1;
   final static int AUTO_NAMING_OFF = 2;

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
         mod = Event.SHIFT_MASK + Event.CTRL_MASK; 
      return pref.getInt("CAPTURE_HOTKEY_MODIFIERS", mod);
   }

   public double getCaptureDelay(){
      return pref.getDouble("CAPTURE_DELAY", 1.0);
   }

   public void setCaptureDelay(double v){
      pref.putDouble("CAPTURE_DELAY", v);
   }

   public void setAutoNamingMethod(int m){
      pref.putInt("AUTO_NAMING", m);
   }

   public int getAutoNamingMethod(){
      return pref.getInt("AUTO_NAMING", AUTO_NAMING_OCR);
   }

   public int getDefaultThumbHeight(){
      return pref.getInt("DEFAULT_THUMB_HEIGHT", 50);
   }

   public void setDefaultThumbHeight(int h){
      pref.putInt("DEFAULT_THUMB_HEIGHT", h);
   }

   public boolean getAutoCaptureForCmdButtons(){
      return pref.getInt("AUTO_CAPTURE_FOR_CMD_BUTTONS",1)!=0;
   }

   public void setAutoCaptureForCmdButtons(boolean flag){
      pref.putInt("AUTO_CAPTURE_FOR_CMD_BUTTONS", flag?1:0);
   }

   public void setCheckUpdateTime(){
      pref.putLong("LAST_CHECK_UPDATE", (new Date()).getTime());
   }

   public long getCheckUpdateTime(){
      return pref.getLong("LAST_CHECK_UPDATE", (new Date()).getTime());
   }


   public void setLastSeenUpdate(String ver){
      pref.put("LAST_SEEN_UPDATE", ver);
   }

   public String getLastSeenUpdate(){
      return pref.get("LAST_SEEN_UPDATE", "0.0");
   }

   public boolean getCheckUpdate(){
      return pref.getBoolean("CHECK_UPDATE", true);
   }

   public void setCheckUpdate(boolean flag){
      pref.putBoolean("CHECK_UPDATE", flag);
   }

   public void setConsoleCSS(String css){
      pref.put("CONSOLE_CSS", css);
   }

   public String getConsoleCSS(){
      return pref.get("CONSOLE_CSS", 
            "body   { font-family:serif; font-size: 12px; }" +
            ".normal{ color: black; }" +
            ".debug { color:#505000; }" +
            ".info  { color: blue; }" + 
            ".log   { color: #09806A; }" + 
            ".error { color: red; }"
      );
   }


   public void put(String key, String val){
      pref.put(key, val);
   }

   public String get(String key, String default_){
      return pref.get(key, default_);
   }

}
