/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.sikuli.script.internal.hotkey.HotkeyManager;
import org.sikuli.script.HotkeyListener;
import org.sikuli.script.HotkeyEvent;
import org.sikuli.script.Debug;

public class HotkeyTest 
{
   public boolean pressed = false;


   private void sleep(double secs){
       int count = 0;
       while(count < secs*10){
          try{
             Thread.sleep(100);
             count++;
          }
          catch(InterruptedException e){}
       }
   }

    @Ignore("ignore this in automated test")
    @Test
    public void test_hotkey_install()
    {
       JFrame f = new JFrame("hello"); // need this to hook in the event loop
       boolean ret = Env.addHotkey(KeyEvent.VK_F6, 0,
         new HotkeyListener(){
            public void hotkeyPressed(HotkeyEvent e){
               HotkeyTest.this.pressed = true;
               Debug.log("hotkey pressed!" + e.keyCode + ", " + e.modifiers);
            }
       });
       Debug.log("install hotkey F6: " + ret);
       ret = Env.addHotkey(KeyEvent.VK_2, InputEvent.ALT_MASK,
         new HotkeyListener(){
            public void hotkeyPressed(HotkeyEvent e){
               HotkeyTest.this.pressed = true;
               Debug.log("hotkey 2 pressed!");
            }
       });
       Debug.log("install hotkey ALT-2: " + ret);
       Debug.log("press the hot key now.");
       sleep(5);
       ret = Env.removeHotkey(KeyEvent.VK_F6, 0);
       Debug.log("remove hotkey F6: " + ret);
       Debug.log("press the hot key again.");
       sleep(2);
       ret = Env.addHotkey(KeyEvent.VK_F7, 0,
         new HotkeyListener(){
            public void hotkeyPressed(HotkeyEvent e){
               HotkeyTest.this.pressed = true;
               Debug.log("hotkey 3 pressed!" + e.keyCode + ", " + e.modifiers);
            }
       });
       Debug.log("install hotkey F7: " + ret);
       Debug.log("press the hot key now.");
       sleep(3);
       //hkm.cleanUp();
    }

    public static void main(String args[]){
      (new HotkeyTest()).test_hotkey_install();
    }
}

