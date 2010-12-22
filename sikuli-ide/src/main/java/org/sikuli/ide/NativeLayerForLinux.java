package org.sikuli.ide;

import java.lang.reflect.*;
import java.awt.Event;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import com.wapmx.nativeutils.jniloader.NativeLoader;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;


import org.sikuli.script.Debug;

public class NativeLayerForLinux implements NativeLayer {
   private Map<String, Integer> _callbackIdMap = new HashMap<String,Integer>();
   private Map<Integer, String> _idCallbackMap = new HashMap<Integer,String>();

   public void initApp(){}

   public void initIDE(SikuliIDE ide){
      try{
         NativeLoader.loadLibrary("JXGrabKey");      
      }
      catch(IOException e){
         Debug.error("Can't load native lib JXGrabKey");
         e.printStackTrace();
      }
   }

   public void installHotkey(int key, int mod, 
                              final SikuliIDE ide, 
			      final String callbackMethod, String callbackType){ 

      String txtMod = KeyEvent.getKeyModifiersText(mod).toUpperCase();
      txtMod = txtMod.replace("META","WIN");
      txtMod = txtMod.replace("WINDOWS","WIN");
      String txtCode = KeyEvent.getKeyText(key).toUpperCase();
      Debug.log(1, "[Linux] install hotkey: " + txtMod + "+" + txtCode + 
                   " for " + callbackMethod);

      JXGrabKey grabKey = JXGrabKey.getInstance();
      int id;
      if( _callbackIdMap.containsKey(callbackMethod) ){
         id = _callbackIdMap.get(callbackMethod);
         grabKey.unregisterHotKey(id); 
      }
      else{
         id = _callbackIdMap.size()+1;
         _callbackIdMap.put(callbackMethod, id);
         _idCallbackMap.put(id, callbackMethod);
      }
      try{
         //JXGrabKey.setDebugOutput(true);
         grabKey.registerAwtHotkey(id, mod, key);
      }catch(HotkeyConflictException e){
         Debug.error("Hot key conflicts");
         grabKey.cleanUp(); 
         return;
      }
				
		//Implement HotkeyListener

      if(_callbackIdMap.size()==1){
         HotkeyListener hotkeyListener = new jxgrabkey.HotkeyListener(){
            public void onHotkey(int id) {
               Debug.log(1, "onHotkey: " + id);
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
         };
         JXGrabKey.getInstance().addHotkeyListener(hotkeyListener);
      }
		
   }
}


