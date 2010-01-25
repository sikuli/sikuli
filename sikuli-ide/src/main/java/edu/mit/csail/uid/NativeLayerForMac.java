package edu.mit.csail.uid;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.prefs.*;
import com.apple.eawt.*;
import com.wapmx.nativeutils.jniloader.NativeLoader;


// http://lists.apple.com/archives/mac-games-dev/2001/Sep/msg00113.html
// full key table: http://www.mactech.com/articles/mactech/Vol.04/04.12/Macinkeys/
// modifiers code: http://www.mactech.com/macintosh-c/chap02-1.html

public class NativeLayerForMac implements NativeLayer {
   static final int CARBON_MASK_CMD = 0x0100;
   static final int CARBON_MASK_SHIFT = 0x0200;
   static final int CARBON_MASK_OPT = 0x0800;
   static final int CARBON_MASK_CTRL = 0x1000;

   static {
      try{
         NativeLoader.loadLibrary("NativeLayerForMac");      
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public void initIDE(final SikuliIDE ide){
      Application app = Application.getApplication();
      app.addPreferencesMenuItem();
      app.setEnabledPreferencesMenu(true);
      app.addApplicationListener(
         new ApplicationAdapter() {
            public void handleOpenFile(ApplicationEvent evt) {
               Debug.log(1, "opening " + evt.getFilename());
               ide.loadFile(evt.getFilename());
            }

            public void handlePreferences(ApplicationEvent evt){
               Debug.log(1, "opening preferences setting");
               ide.showPreferencesWindow();
            }

            public void handleQuit(ApplicationEvent event){
               SikuliIDE.getInstance().quit();
            }
         }
      ); 
   }

   public void installHotkey(int key, int mod, 
                              SikuliIDE ide, 
                              String callbackMethod, String callbackType){
      int ckey = convertToCarbonKey(key);
      int cmod = convertToCarbonModifiers(mod);
      Debug.log(1, "register hotkey Java:" + key + "(" +mod+ 
                   ") Carbon: " + ckey + "(" + cmod + ")");
      installGlobalHotkey(ckey, cmod, ide, 
                          callbackMethod, callbackType);
   }

   private native void installGlobalHotkey(int keyCode, int modifiers, 
                              SikuliIDE ide, 
                              String callbackMethod, String callbackType);

   private int convertToCarbonModifiers(int mod){
      int cmod = 0;
      if((mod&InputEvent.SHIFT_MASK) != 0) cmod |= CARBON_MASK_SHIFT;
      if((mod&InputEvent.META_MASK) != 0) cmod |= CARBON_MASK_CMD;
      if((mod&InputEvent.ALT_MASK) != 0) cmod |= CARBON_MASK_OPT;
      if((mod&InputEvent.CTRL_MASK) != 0) cmod |= CARBON_MASK_CTRL;
      return cmod;
   }

   private int convertToCarbonKey(int keycode){
      switch(keycode){
         case KeyEvent.VK_BACK_SPACE: return 0x33;
         case KeyEvent.VK_TAB: return 0x30;
         case KeyEvent.VK_CLEAR: return 0x47;
         case KeyEvent.VK_ENTER: return 0x24;
         case KeyEvent.VK_SHIFT: return 0xF0;
         case KeyEvent.VK_CONTROL: return 0xF1;
         case KeyEvent.VK_META: return 0xF2; 
         case KeyEvent.VK_PAUSE: return 0x71; // = F15
         case KeyEvent.VK_ESCAPE: return 0x35;
         case KeyEvent.VK_SPACE: return 0x31;
         case KeyEvent.VK_OPEN_BRACKET: return 0x21;
         case KeyEvent.VK_BACK_SLASH: return 0x2A;
         case KeyEvent.VK_CLOSE_BRACKET: return 0x1E;
         case KeyEvent.VK_SLASH: return 0x2C;
         case KeyEvent.VK_PERIOD: return 0x2F;
         case KeyEvent.VK_COMMA: return 0x2B;
         case KeyEvent.VK_SEMICOLON: return 0x29;
         case KeyEvent.VK_END: return 0x77;
         case KeyEvent.VK_HOME: return 0x73;
         case KeyEvent.VK_LEFT: return 0x7B;
         case KeyEvent.VK_UP: return 0x7E;
         case KeyEvent.VK_RIGHT: return 0x7C;
         case KeyEvent.VK_DOWN: return 0x7D;
         case KeyEvent.VK_PRINTSCREEN: return 0x69; // F13
         case KeyEvent.VK_INSERT: return 0x72; // help
         case KeyEvent.VK_DELETE: return 0x75;
         case KeyEvent.VK_HELP: return 0x72;
         case KeyEvent.VK_0: return 0x1D;
         case KeyEvent.VK_1: return 0x12;
         case KeyEvent.VK_2: return 0x13;
         case KeyEvent.VK_3: return 0x14;
         case KeyEvent.VK_4: return 0x15;
         case KeyEvent.VK_5: return 0x17;
         case KeyEvent.VK_6: return 0x16;
         case KeyEvent.VK_7: return 0x1A;
         case KeyEvent.VK_8: return 0x1C;
         case KeyEvent.VK_9: return 0x19;
         case KeyEvent.VK_MINUS: return 0x1B;
         case KeyEvent.VK_EQUALS: return 0x18;
         case KeyEvent.VK_A: return 0x00;
         case KeyEvent.VK_B: return 0x0B;
         case KeyEvent.VK_C: return 0x08;
         case KeyEvent.VK_D: return 0x02;
         case KeyEvent.VK_E: return 0x0E;
         case KeyEvent.VK_F: return 0x03;
         case KeyEvent.VK_G: return 0x05;
         case KeyEvent.VK_H: return 0x04;
         case KeyEvent.VK_I: return 0x22;
         case KeyEvent.VK_J: return 0x26;
         case KeyEvent.VK_K: return 0x28;
         case KeyEvent.VK_L: return 0x25;
         case KeyEvent.VK_M: return 0x2E;
         case KeyEvent.VK_N: return 0x2D;
         case KeyEvent.VK_O: return 0x1F;
         case KeyEvent.VK_P: return 0x23;
         case KeyEvent.VK_Q: return 0x0C;
         case KeyEvent.VK_R: return 0x0F;
         case KeyEvent.VK_S: return 0x01;
         case KeyEvent.VK_T: return 0x11;
         case KeyEvent.VK_U: return 0x20;
         case KeyEvent.VK_V: return 0x09;
         case KeyEvent.VK_W: return 0x0D;
         case KeyEvent.VK_X: return 0x07;
         case KeyEvent.VK_Y: return 0x10;
         case KeyEvent.VK_Z: return 0x06;
         case KeyEvent.VK_NUMPAD0: return 0x52;
         case KeyEvent.VK_NUMPAD1: return 0x53;
         case KeyEvent.VK_NUMPAD2: return 0x54;
         case KeyEvent.VK_NUMPAD3: return 0x55;
         case KeyEvent.VK_NUMPAD4: return 0x56;
         case KeyEvent.VK_NUMPAD5: return 0x57;
         case KeyEvent.VK_NUMPAD6: return 0x58;
         case KeyEvent.VK_NUMPAD7: return 0x59;
         case KeyEvent.VK_NUMPAD8: return 0x5B;
         case KeyEvent.VK_NUMPAD9: return 0x5C;
         case KeyEvent.VK_MULTIPLY: return 0x43;
         case KeyEvent.VK_ADD: return 0x45;
         case KeyEvent.VK_SEPARATOR: return 0xFF; // not supported with Button or GetKeys
         case KeyEvent.VK_SUBTRACT: return 0x4E;
         case KeyEvent.VK_DECIMAL: return 0x41;
         case KeyEvent.VK_DIVIDE: return 0x4B;
         case KeyEvent.VK_F1: return 0x7A;
         case KeyEvent.VK_F2: return 0x7B;
         case KeyEvent.VK_F3: return 0x63;
         case KeyEvent.VK_F4: return 0x76;
         case KeyEvent.VK_F5: return 0x60;
         case KeyEvent.VK_F6: return 0x61;
         case KeyEvent.VK_F7: return 0x62;
         case KeyEvent.VK_F8: return 0x64;
         case KeyEvent.VK_F9: return 0x65;
         case KeyEvent.VK_F10: return 0x6D;
         case KeyEvent.VK_F11: return 0x67;
         case KeyEvent.VK_F12: return 0x6F;
         case KeyEvent.VK_F13: return 0x69;
         case KeyEvent.VK_F14: return 0x6B;
         case KeyEvent.VK_F15: return 0x71;
         case KeyEvent.VK_NUM_LOCK: return 0x47;
         default: return 0xFF;
      }

   }
}


