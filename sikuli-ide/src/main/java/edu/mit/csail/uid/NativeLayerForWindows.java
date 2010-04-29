package edu.mit.csail.uid;

import java.lang.reflect.*;
import java.awt.Event;
import java.awt.event.*;
import java.util.*;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

public class NativeLayerForWindows implements NativeLayer {
   private Map<String, Integer> _callbackIdMap = new HashMap<String,Integer>();
   private Map<Integer, String> _idCallbackMap = new HashMap<Integer,String>();

   public void initApp(){
   }
   public void initIDE(SikuliIDE ide){
   }

   public void installHotkey(int key, int mod, 
                              final SikuliIDE ide, 
                              final String callbackMethod, String callbackType){
      JIntellitype itype = JIntellitype.getInstance();
      String txtMod = KeyEvent.getKeyModifiersText(mod).toUpperCase();
      txtMod = txtMod.replace("META","WIN");
      txtMod = txtMod.replace("WINDOWS","WIN");
      String txtCode = KeyEvent.getKeyText(key).toUpperCase();
      Debug.log(1, "[WIN] install hotkey: " + txtMod + "+" + txtCode + 
                   " for " + callbackMethod);

      int id;
      if( _callbackIdMap.containsKey(callbackMethod) ){
         id = _callbackIdMap.get(callbackMethod);
         itype.unregisterHotKey(id);
      }
      else{
         id = _callbackIdMap.size()+1;
         _callbackIdMap.put(callbackMethod, id);
         _idCallbackMap.put(id, callbackMethod);
      }

      itype.registerHotKey(id, txtMod + "+" + txtCode);
      Debug.log(1, "[WIN] " + callbackMethod + " " + id);
      if(_callbackIdMap.size()==1){
         itype.addHotKeyListener(new HotkeyListener(){
            public void onHotKey(int id){
               Debug.log(1, "Hotkey pressed");
               String callbackFunc = _idCallbackMap.get(id);
               Class params[] = {};
               Object paramsObj[] = {};
               Class cls = ide.getClass();
               try{
                  Method callback = cls.getDeclaredMethod(callbackFunc, params);
                  callback.invoke(ide, paramsObj);
               }
               catch(Exception e){
                  e.printStackTrace();
               }
            }
         });
      }
   }
}


