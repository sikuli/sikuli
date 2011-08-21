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
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

import org.sikuli.script.Debug;
import org.sikuli.script.HotkeyListener;
import org.sikuli.script.HotkeyEvent;

public class WindowsHotkeyManager extends HotkeyManager {
   class HotkeyData {
      int key, modifiers;
      HotkeyListener listener;

      public HotkeyData(int key_, int mod_, HotkeyListener l_){
         key = key_;
         modifiers = mod_;
         listener = l_;
      }
   };

   class JIntellitypeHandler implements 
                               com.melloware.jintellitype.HotkeyListener{
      public void onHotKey(int id){
         Debug.log(4, "Hotkey pressed");
         HotkeyData data = _idCallbackMap.get(id);
         HotkeyEvent e = new HotkeyEvent(data.key, data.modifiers);
         data.listener.hotkeyPressed(e);
      }
   };

   private Map<Integer, HotkeyData> _idCallbackMap = new HashMap<Integer,HotkeyData >();
   private int _gHotkeyId = 1;

   public boolean _addHotkey(int keyCode, int modifiers, HotkeyListener listener){
      JIntellitype itype = JIntellitype.getInstance();

      if(_gHotkeyId == 1){
         itype.addHotKeyListener(new JIntellitypeHandler());
      }

      _removeHotkey(keyCode, modifiers);
      int id = _gHotkeyId++;
      HotkeyData data = new HotkeyData(keyCode, modifiers, listener);
      _idCallbackMap.put(id, data);

      itype.registerSwingHotKey(id, modifiers, keyCode);
      return true;
   }
   
   public boolean _removeHotkey(int keyCode, int modifiers){
      for( Map.Entry<Integer, HotkeyData> entry : _idCallbackMap.entrySet() ){
         HotkeyData data = entry.getValue();
         if(data.key == keyCode && data.modifiers == modifiers){
            JIntellitype itype = JIntellitype.getInstance();
            int id = entry.getKey();
            itype.unregisterHotKey(id);
            _idCallbackMap.remove(id);
            return true;
         }
      }
      return false;
   }


   public void cleanUp(){
      JIntellitype itype = JIntellitype.getInstance();
      for( Map.Entry<Integer, HotkeyData> entry : _idCallbackMap.entrySet() ){
         int id = entry.getKey();
         itype.unregisterHotKey(id); 
      }
      _gHotkeyId = 1;
      _idCallbackMap.clear();
      itype.cleanUp();
   }

}


