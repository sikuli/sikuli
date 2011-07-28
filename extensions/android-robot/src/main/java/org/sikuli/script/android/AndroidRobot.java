/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.android;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyEvent;


import org.sikuli.script.IRobot;
import org.sikuli.script.Debug;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Location;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.MonkeyRunner;
import com.android.monkeyrunner.MonkeyImage;

public class AndroidRobot implements IRobot {
   protected MonkeyDevice _dev;
   protected Location _mouse;
   protected int _autoDelay = 0;

   public AndroidRobot(MonkeyDevice dev){
      _dev = dev;
      _mouse = new Location(0,0);
   }

   // convert Java keycode to Android keycode
   // See http://developer.android.com/reference/android/view/KeyEvent.html for the key list.
   protected void key(boolean down, int keycode){
      /*
      String code = "KEYCODE_UNKNOWN";
      switch(keycode){
          case KeyEvent.VK_SOFT_LEFT: code = "KEYCODE_SOFT_LEFT";
          case KeyEvent.VK_SOFT_RIGHT: code = "KEYCODE_SOFT_RIGHT";
          case KeyEvent.VK_HOME: code = "KEYCODE_HOME";
          case KeyEvent.VK_BACK: code = "KEYCODE_BACK";
          case KeyEvent.VK_CALL: code = "KEYCODE_CALL";
          case KeyEvent.VK_ENDCALL: code = "KEYCODE_ENDCALL";
          case KeyEvent.VK_0: code = "KEYCODE_0";
          case KeyEvent.VK_1: code = "KEYCODE_1";
          case KeyEvent.VK_2: code = "KEYCODE_2";
          case KeyEvent.VK_3: code = "KEYCODE_3";
          case KeyEvent.VK_4: code = "KEYCODE_4";
          case KeyEvent.VK_5: code = "KEYCODE_5";
          case KeyEvent.VK_6: code = "KEYCODE_6";
          case KeyEvent.VK_7: code = "KEYCODE_7";
          case KeyEvent.VK_8: code = "KEYCODE_8";
          case KeyEvent.VK_9: code = "KEYCODE_9";
          case KeyEvent.VK_STAR: code = "KEYCODE_STAR";
          case KeyEvent.VK_POUND: code = "KEYCODE_POUND";
          case KeyEvent.VK_DPAD_UP: code = "KEYCODE_DPAD_UP";
          case KeyEvent.VK_DPAD_DOWN: code = "KEYCODE_DPAD_DOWN";
          case KeyEvent.VK_DPAD_LEFT: code = "KEYCODE_DPAD_LEFT";
          case KeyEvent.VK_DPAD_RIGHT: code = "KEYCODE_DPAD_RIGHT";
          case KeyEvent.VK_DPAD_CENTER: code = "KEYCODE_DPAD_CENTER";
          case KeyEvent.VK_VOLUME_UP: code = "KEYCODE_VOLUME_UP";
          case KeyEvent.VK_VOLUME_DOWN: code = "KEYCODE_VOLUME_DOWN";
          case KeyEvent.VK_POWER: code = "KEYCODE_POWER";
          case KeyEvent.VK_CAMERA: code = "KEYCODE_CAMERA";
          case KeyEvent.VK_CLEAR: code = "KEYCODE_CLEAR";
          case KeyEvent.VK_A: code = "KEYCODE_A";
          case KeyEvent.VK_B: code = "KEYCODE_B";
          case KeyEvent.VK_C: code = "KEYCODE_C";
          case KeyEvent.VK_D: code = "KEYCODE_D";
          case KeyEvent.VK_E: code = "KEYCODE_E";
          case KeyEvent.VK_F: code = "KEYCODE_F";
          case KeyEvent.VK_G: code = "KEYCODE_G";
          case KeyEvent.VK_H: code = "KEYCODE_H";
          case KeyEvent.VK_I: code = "KEYCODE_I";
          case KeyEvent.VK_J: code = "KEYCODE_J";
          case KeyEvent.VK_K: code = "KEYCODE_K";
          case KeyEvent.VK_L: code = "KEYCODE_L";
          case KeyEvent.VK_M: code = "KEYCODE_M";
          case KeyEvent.VK_N: code = "KEYCODE_N";
          case KeyEvent.VK_O: code = "KEYCODE_O";
          case KeyEvent.VK_P: code = "KEYCODE_P";
          case KeyEvent.VK_Q: code = "KEYCODE_Q";
          case KeyEvent.VK_R: code = "KEYCODE_R";
          case KeyEvent.VK_S: code = "KEYCODE_S";
          case KeyEvent.VK_T: code = "KEYCODE_T";
          case KeyEvent.VK_U: code = "KEYCODE_U";
          case KeyEvent.VK_V: code = "KEYCODE_V";
          case KeyEvent.VK_W: code = "KEYCODE_W";
          case KeyEvent.VK_X: code = "KEYCODE_X";
          case KeyEvent.VK_Y: code = "KEYCODE_Y";
          case KeyEvent.VK_Z: code = "KEYCODE_Z";
          case KeyEvent.VK_COMMA: code = "KEYCODE_COMMA";
          case KeyEvent.VK_PERIOD: code = "KEYCODE_PERIOD";
          case KeyEvent.VK_ALT_LEFT: code = "KEYCODE_ALT_LEFT";
          case KeyEvent.VK_ALT_RIGHT: code = "KEYCODE_ALT_RIGHT";
          case KeyEvent.VK_SHIFT_LEFT: code = "KEYCODE_SHIFT_LEFT";
          case KeyEvent.VK_SHIFT_RIGHT: code = "KEYCODE_SHIFT_RIGHT";
          case KeyEvent.VK_TAB: code = "KEYCODE_TAB";
          case KeyEvent.VK_SPACE: code = "KEYCODE_SPACE";
          case KeyEvent.VK_SYM: code = "KEYCODE_SYM";
          case KeyEvent.VK_EXPLORER: code = "KEYCODE_EXPLORER";
          case KeyEvent.VK_ENVELOPE: code = "KEYCODE_ENVELOPE";
          case KeyEvent.VK_ENTER: code = "KEYCODE_ENTER";
          case KeyEvent.VK_DEL: code = "KEYCODE_DEL";
          case KeyEvent.VK_GRAVE: code = "KEYCODE_GRAVE";
          case KeyEvent.VK_MINUS: code = "KEYCODE_MINUS";
          case KeyEvent.VK_EQUALS: code = "KEYCODE_EQUALS";
          case KeyEvent.VK_LEFT_BRACKET: code = "KEYCODE_LEFT_BRACKET";
          case KeyEvent.VK_RIGHT_BRACKET: code = "KEYCODE_RIGHT_BRACKET";
          case KeyEvent.VK_BACKSLASH: code = "KEYCODE_BACKSLASH";
          case KeyEvent.VK_SEMICOLON: code = "KEYCODE_SEMICOLON";
          case KeyEvent.VK_APOSTROPHE: code = "KEYCODE_APOSTROPHE";
          case KeyEvent.VK_SLASH: code = "KEYCODE_SLASH";
          case KeyEvent.VK_AT: code = "KEYCODE_AT";
          case KeyEvent.VK_NUM: code = "KEYCODE_NUM";
          case KeyEvent.VK_HEADSETHOOK: code = "KEYCODE_HEADSETHOOK";
          case KeyEvent.VK_PLUS: code = "KEYCODE_PLUS";
          case KeyEvent.VK_MENU: code = "KEYCODE_MENU";
          case KeyEvent.VK_NOTIFICATION: code = "KEYCODE_NOTIFICATION";
          case KeyEvent.VK_SEARCH: code = "KEYCODE_SEARCH";
          case KeyEvent.VK_MEDIA_PLAY_PAUSE: code = "KEYCODE_MEDIA_PLAY_PAUSE";
          case KeyEvent.VK_MEDIA_STOP: code = "KEYCODE_MEDIA_STOP";
          case KeyEvent.VK_MEDIA_NEXT: code = "KEYCODE_MEDIA_NEXT";
          case KeyEvent.VK_MEDIA_PREVIOUS: code = "KEYCODE_MEDIA_PREVIOUS";
          case KeyEvent.VK_MEDIA_REWIND: code = "KEYCODE_MEDIA_REWIND";
          case KeyEvent.VK_MEDIA_FAST_FORWARD: code = "KEYCODE_MEDIA_FAST_FORWARD";
          case KeyEvent.VK_MUTE: code = "KEYCODE_MUTE";
          case KeyEvent.VK_PAGE_UP: code = "KEYCODE_PAGE_UP";
          case KeyEvent.VK_PAGE_DOWN: code = "KEYCODE_PAGE_DOWN";
          case KeyEvent.VK_BUTTON_A: code = "KEYCODE_BUTTON_A";
          case KeyEvent.VK_BUTTON_B: code = "KEYCODE_BUTTON_B";
          case KeyEvent.VK_BUTTON_C: code = "KEYCODE_BUTTON_C";
          case KeyEvent.VK_BUTTON_X: code = "KEYCODE_BUTTON_X";
          case KeyEvent.VK_BUTTON_Y: code = "KEYCODE_BUTTON_Y";
          case KeyEvent.VK_BUTTON_Z: code = "KEYCODE_BUTTON_Z";
          case KeyEvent.VK_BUTTON_L1: code = "KEYCODE_BUTTON_L1";
          case KeyEvent.VK_BUTTON_R1: code = "KEYCODE_BUTTON_R1";
          case KeyEvent.VK_BUTTON_L2: code = "KEYCODE_BUTTON_L2";
          case KeyEvent.VK_BUTTON_R2: code = "KEYCODE_BUTTON_R2";
          case KeyEvent.VK_BUTTON_THUMBL: code = "KEYCODE_BUTTON_THUMBL";
          case KeyEvent.VK_BUTTON_THUMBR: code = "KEYCODE_BUTTON_THUMBR";
          case KeyEvent.VK_BUTTON_START: code = "KEYCODE_BUTTON_START";
          case KeyEvent.VK_BUTTON_SELECT: code = "KEYCODE_BUTTON_SELECT";
          case KeyEvent.VK_BUTTON_MODE: code = "KEYCODE_BUTTON_MODE";
      }
      press(code, down?MonkeyDevice.DOWN : MonkeyDevice.UP);
      */
   }

   public void typeChar(char character, KeyMode mode){
      //TODO
   
   }

   public void pressModifiers(int modifiers){
      //TODO
   
   }

   public void releaseModifiers(int modifiers){
      //TODO
   
   }

   public void keyPress(int keycode){
      Debug.log(5, "AndroidRobot.keypress");
      // TODO: keycode to string
      //_dev.press(keycode, MonkeyDevice.DOWN);
      delay(_autoDelay);
   }

   public void keyRelease(int keycode){
      Debug.log(5, "AndroidRobot.keyRelease");
      // TODO: keycode to string
      //_dev.press(keycode, MonkeyDevice.UP);
      delay(_autoDelay);
   }  

   public void mouseMove(int x, int y){
      _mouse.x = x;
      _mouse.y = y;
   }
   public void mousePress(int buttons){
      Debug.log(5, "AndroidRobot.mousePress");
      _dev.touch(_mouse.x, _mouse.y, MonkeyDevice.TouchPressType.DOWN);
      delay(_autoDelay);
   }
   public void mouseRelease(int buttons){
      Debug.log(5, "AndroidRobot.mouseRelease");
      _dev.touch(_mouse.x, _mouse.y, MonkeyDevice.TouchPressType.UP);
      delay(_autoDelay);
   }

   public void smoothMove(Location dest){
      // no implementation on android
   }
   public void smoothMove(Location src, Location dest, long ms){
      // no implementation on android
   }

   public void dragDrop(Location start, Location end, int steps, long ms, int buttons){
      _dev.drag(start.x, start.y, end.x, end.y, steps, ms);
      delay(_autoDelay);
   }
             

   public void mouseWheel(int wheelAmt){
      // no implementation on android
   }

   //FIXME: use in-memory conversion instead
   public ScreenImage captureScreen(Rectangle screenRect){
      Debug.log(5, "AndroidRobot.captureScreen " + screenRect.toString());
      MonkeyImage img = _dev.takeSnapshot();
      String filename = "/tmp/android_screen.png";
      try{
         img.writeToFile(filename, "png");
         BufferedImage bimg = ImageIO.read(new File(filename));
         BufferedImage sub_bimg = bimg.getSubimage(screenRect.x, screenRect.y, screenRect.width, screenRect.height);
         ScreenImage simg = new ScreenImage(screenRect, sub_bimg);
         return simg;
      }
      catch(IOException e){
         e.printStackTrace();
         return null;
      }
   }

   public void waitForIdle(){
      // no implementation on android
   }

   public void delay(int ms){
      try{
         Thread.sleep(ms);
      }
      catch(InterruptedException e){
      }
   }

   public void setAutoDelay(int ms){
      _autoDelay = ms;
   }
}

