/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.internal.hotkey;

import java.lang.reflect.*;
import java.awt.Event;
import java.awt.event.*;
import java.util.*;
import java.io.IOException;

import org.sikuli.script.Debug;
import org.sikuli.script.HotkeyListener;
import org.sikuli.script.HotkeyEvent;

import com.wapmx.nativeutils.jniloader.NativeLoader;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.JXGrabKey;

public class LinuxHotkeyManager extends HotkeyManager {
   static{
      try{
         NativeLoader.loadLibrary("JXGrabKey");
      }
      catch(IOException e){
         Debug.error("Can't load native lib JXGrabKey");
         e.printStackTrace();
      }
   }

   class HotkeyData {
      int key, modifiers;
      HotkeyListener listener;

      public HotkeyData(int key_, int mod_, HotkeyListener l_){
         key = key_;
         modifiers = mod_;
         listener = l_;
      }
   };

   class MyHotkeyHandler implements jxgrabkey.HotkeyListener{
      public void onHotkey(int id){
         Debug.log(4, "Hotkey pressed");
         HotkeyData data = _idCallbackMap.get(id);
         HotkeyEvent e = new HotkeyEvent(data.key, data.modifiers);
         data.listener.hotkeyPressed(e);
      }
   };

   private Map<Integer, HotkeyData> _idCallbackMap = new HashMap<Integer,HotkeyData >();
   private int _gHotkeyId = 1;

   public boolean addHotkey(int keyCode, int modifiers, HotkeyListener listener){
      JXGrabKey grabKey = JXGrabKey.getInstance();
      String txtMod = getKeyModifierText(modifiers);
      String txtCode = getKeyCodeText(keyCode);
      Debug.info("add hotkey: " + txtMod + " " + txtCode);

      if(_gHotkeyId == 1){
         grabKey.addHotkeyListener(new MyHotkeyHandler());
      }

      removeHotkey(keyCode, modifiers);
      int id = _gHotkeyId++;
      HotkeyData data = new HotkeyData(keyCode, modifiers, listener);
      _idCallbackMap.put(id, data);

      try{
         //JXGrabKey.setDebugOutput(true);
         grabKey.registerAwtHotkey(id, modifiers, keyCode);
      }catch(HotkeyConflictException e){
         Debug.error("Hot key conflicts: " + txtMod + "+" + txtCode);
         return false;
      }
      return true;
   }

   private boolean _removeHotkey(int keyCode, int modifiers){
      for( Map.Entry<Integer, HotkeyData> entry : _idCallbackMap.entrySet() ){
         HotkeyData data = entry.getValue();
         if(data.key == keyCode && data.modifiers == modifiers){
            JXGrabKey grabKey = JXGrabKey.getInstance();
            int id = entry.getKey();
            grabKey.unregisterHotKey(id); 
            _idCallbackMap.remove(id);
            return true;
         }
      }
      return false;
   }

   public boolean removeHotkey(int keyCode, int modifiers){
      String txtMod = getKeyModifierText(modifiers);
      String txtCode = getKeyCodeText(keyCode);
      Debug.info("remove hotkey: " + txtMod + " " + txtCode);
      return _removeHotkey(keyCode, modifiers);
   }


   public void cleanUp(){
      JXGrabKey.getInstance().cleanUp(); 
   }

}


