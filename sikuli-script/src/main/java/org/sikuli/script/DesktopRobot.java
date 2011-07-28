/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;


import java.awt.Robot;
import java.awt.GraphicsDevice;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public class DesktopRobot extends Robot implements IRobot{
   final static int MAX_DELAY = 60000;

   public DesktopRobot(GraphicsDevice screen) throws AWTException{
      super(screen);
   
   }

   public void smoothMove(Location dest){
      smoothMove(Env.getMouseLocation(), dest, (long)(Settings.MoveMouseDelay*1000L));
   }

   public void smoothMove(Location src, Location dest, long ms){
      if(ms == 0){
         mouseMove(dest.x, dest.y);
         return;
      }

      Animator aniX = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.x, (float)dest.x, ms));
      Animator aniY = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.y, (float)dest.y, ms));
      while(aniX.running()){
         float x = aniX.step();
         float y = aniY.step();
         mouseMove((int)x, (int)y);
         delay(50);
      }
   }

   public void dragDrop(Location start, Location end, int steps, long ms, int buttons){
      mouseMove(start.x, start.y);
      mousePress(buttons);
      delay((int)(Settings.DelayAfterDrag*1000));
      waitForIdle();
      smoothMove(start, end, ms);
      delay((int)(Settings.DelayBeforeDrop*1000));
      mouseRelease(buttons);
      waitForIdle();
   }

   
   public void delay(int ms){
      if(ms<0)
         ms = 0;
      while(ms>MAX_DELAY){
         super.delay(MAX_DELAY);
         ms -= MAX_DELAY;
      }
      super.delay(ms);
   }

   public ScreenImage captureScreen(Rectangle rect){
      BufferedImage img = createScreenCapture(rect);
      return new ScreenImage(rect, img);
   }


   public void pressModifiers(int modifiers){
      if((modifiers & KeyModifier.SHIFT) != 0) keyPress(KeyEvent.VK_SHIFT);
      if((modifiers & KeyModifier.CTRL) != 0) keyPress(KeyEvent.VK_CONTROL);
      if((modifiers & KeyModifier.ALT) != 0) keyPress(KeyEvent.VK_ALT);
      if((modifiers & KeyModifier.META) != 0){
         if( Env.getOS() == OS.WINDOWS )
            keyPress(KeyEvent.VK_WINDOWS);
         else
            keyPress(KeyEvent.VK_META);
      }
   }

   public void releaseModifiers(int modifiers){
      if((modifiers & KeyModifier.SHIFT) != 0) keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & KeyModifier.CTRL) != 0) keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & KeyModifier.ALT) != 0) keyRelease(KeyEvent.VK_ALT);
      if((modifiers & KeyModifier.META) != 0){ 
         if( Env.getOS() == OS.WINDOWS )
            keyRelease(KeyEvent.VK_WINDOWS);
         else
            keyRelease(KeyEvent.VK_META);
      }
   }

   protected void doType(KeyMode mode, int... keyCodes) {
      if(mode==KeyMode.PRESS_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            keyPress(keyCodes[i]);
         }
      }
      else if(mode==KeyMode.RELEASE_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            keyRelease(keyCodes[i]);
         }
      }
      else{
         for(int i=0;i<keyCodes.length;i++)
            keyPress(keyCodes[i]);
         for(int i=0;i<keyCodes.length;i++)
            keyRelease(keyCodes[i]);
      }
   }

   public void typeChar(char character, KeyMode mode) {
      switch (character) {
         case 'a': doType(mode,KeyEvent.VK_A); break;
         case 'b': doType(mode,KeyEvent.VK_B); break;
         case 'c': doType(mode,KeyEvent.VK_C); break;
         case 'd': doType(mode,KeyEvent.VK_D); break;
         case 'e': doType(mode,KeyEvent.VK_E); break;
         case 'f': doType(mode,KeyEvent.VK_F); break;
         case 'g': doType(mode,KeyEvent.VK_G); break;
         case 'h': doType(mode,KeyEvent.VK_H); break;
         case 'i': doType(mode,KeyEvent.VK_I); break;
         case 'j': doType(mode,KeyEvent.VK_J); break;
         case 'k': doType(mode,KeyEvent.VK_K); break;
         case 'l': doType(mode,KeyEvent.VK_L); break;
         case 'm': doType(mode,KeyEvent.VK_M); break;
         case 'n': doType(mode,KeyEvent.VK_N); break;
         case 'o': doType(mode,KeyEvent.VK_O); break;
         case 'p': doType(mode,KeyEvent.VK_P); break;
         case 'q': doType(mode,KeyEvent.VK_Q); break;
         case 'r': doType(mode,KeyEvent.VK_R); break;
         case 's': doType(mode,KeyEvent.VK_S); break;
         case 't': doType(mode,KeyEvent.VK_T); break;
         case 'u': doType(mode,KeyEvent.VK_U); break;
         case 'v': doType(mode,KeyEvent.VK_V); break;
         case 'w': doType(mode,KeyEvent.VK_W); break;
         case 'x': doType(mode,KeyEvent.VK_X); break;
         case 'y': doType(mode,KeyEvent.VK_Y); break;
         case 'z': doType(mode,KeyEvent.VK_Z); break;
         case 'A': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
         case 'B': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
         case 'C': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
         case 'D': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
         case 'E': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
         case 'F': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
         case 'G': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
         case 'H': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
         case 'I': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
         case 'J': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
         case 'K': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
         case 'L': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
         case 'M': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
         case 'N': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
         case 'O': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
         case 'P': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
         case 'Q': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
         case 'R': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
         case 'S': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
         case 'T': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
         case 'U': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
         case 'V': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
         case 'W': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
         case 'X': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
         case 'Y': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
         case 'Z': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
         case '`': doType(mode,KeyEvent.VK_BACK_QUOTE); break;
         case '0': doType(mode,KeyEvent.VK_0); break;
         case '1': doType(mode,KeyEvent.VK_1); break;
         case '2': doType(mode,KeyEvent.VK_2); break;
         case '3': doType(mode,KeyEvent.VK_3); break;
         case '4': doType(mode,KeyEvent.VK_4); break;
         case '5': doType(mode,KeyEvent.VK_5); break;
         case '6': doType(mode,KeyEvent.VK_6); break;
         case '7': doType(mode,KeyEvent.VK_7); break;
         case '8': doType(mode,KeyEvent.VK_8); break;
         case '9': doType(mode,KeyEvent.VK_9); break;
         case '-': doType(mode,KeyEvent.VK_MINUS); break;
         case '=': doType(mode,KeyEvent.VK_EQUALS); break;
         case '~': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
         case '!': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
         case '@': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
         case '#': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_3); break;
         case '$': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_4); break;
         case '%': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
         case '^': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_6); break;
         case '&': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_7); break;
         case '*': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_8); break;
         case '(': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_9); break;
         case ')': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_0); break;
         case '_': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
         case '+': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS); break;
         case '\b': doType(mode,KeyEvent.VK_BACK_SPACE); break;
         case '\t': doType(mode,KeyEvent.VK_TAB); break;
         case '\r': doType(mode,KeyEvent.VK_ENTER); break;
         case '\n': doType(mode,KeyEvent.VK_ENTER); break;
         case '[': doType(mode,KeyEvent.VK_OPEN_BRACKET); break;
         case ']': doType(mode,KeyEvent.VK_CLOSE_BRACKET); break;
         case '\\': doType(mode,KeyEvent.VK_BACK_SLASH); break;
         case '{': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
         case '}': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
         case '|': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
         case ';': doType(mode,KeyEvent.VK_SEMICOLON); break;
         case ':': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
         case '\'': doType(mode,KeyEvent.VK_QUOTE); break;
         case '"': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE); break;
         case ',': doType(mode,KeyEvent.VK_COMMA); break;
         case '<': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break;
         case '.': doType(mode,KeyEvent.VK_PERIOD); break;
         case '>': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break;
         case '/': doType(mode,KeyEvent.VK_SLASH); break;
         case '?': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
         case ' ': doType(mode,KeyEvent.VK_SPACE); break;
         case Key.ESC        : doType(mode,KeyEvent.VK_ESCAPE); break;
         case Key.UP         : doType(mode,KeyEvent.VK_UP); break;
         case Key.RIGHT      : doType(mode,KeyEvent.VK_RIGHT); break;
         case Key.DOWN       : doType(mode,KeyEvent.VK_DOWN); break;
         case Key.LEFT       : doType(mode,KeyEvent.VK_LEFT); break;
         case Key.PAGE_UP    : doType(mode,KeyEvent.VK_PAGE_UP); break;
         case Key.PAGE_DOWN  : doType(mode,KeyEvent.VK_PAGE_DOWN); break;
         case Key.DELETE     : doType(mode,KeyEvent.VK_DELETE); break;
         case Key.END        : doType(mode,KeyEvent.VK_END); break;
         case Key.HOME       : doType(mode,KeyEvent.VK_HOME); break;
         case Key.INSERT     : doType(mode,KeyEvent.VK_INSERT); break;
         case Key.F1         : doType(mode,KeyEvent.VK_F1); break;
         case Key.F2         : doType(mode,KeyEvent.VK_F2); break;
         case Key.F3         : doType(mode,KeyEvent.VK_F3); break;
         case Key.F4         : doType(mode,KeyEvent.VK_F4); break;
         case Key.F5         : doType(mode,KeyEvent.VK_F5); break;
         case Key.F6         : doType(mode,KeyEvent.VK_F6); break;
         case Key.F7         : doType(mode,KeyEvent.VK_F7); break;
         case Key.F8         : doType(mode,KeyEvent.VK_F8); break;
         case Key.F9         : doType(mode,KeyEvent.VK_F9); break;
         case Key.F10        : doType(mode,KeyEvent.VK_F10); break;
         case Key.F11        : doType(mode,KeyEvent.VK_F11); break;
         case Key.F12        : doType(mode,KeyEvent.VK_F12); break;
         case Key.F13        : doType(mode,KeyEvent.VK_F13); break;
         case Key.F14        : doType(mode,KeyEvent.VK_F14); break;
         case Key.F15        : doType(mode,KeyEvent.VK_F15); break;
         case Key.SHIFT      : doType(mode,KeyEvent.VK_SHIFT); break;
         case Key.CTRL       : doType(mode,KeyEvent.VK_CONTROL); break;
         case Key.ALT        : doType(mode,KeyEvent.VK_ALT); break;
         case Key.META       : doType(mode,KeyEvent.VK_META); break;
         case Key.PRINTSCREEN: doType(mode,KeyEvent.VK_PRINTSCREEN); break;
         case Key.SCROLL_LOCK: doType(mode,KeyEvent.VK_SCROLL_LOCK); break;
         case Key.PAUSE      : doType(mode,KeyEvent.VK_PAUSE); break;
         case Key.CAPS_LOCK  : doType(mode,KeyEvent.VK_CAPS_LOCK); break;
         case Key.NUM0       : doType(mode,KeyEvent.VK_NUMPAD0); break;
         case Key.NUM1       : doType(mode,KeyEvent.VK_NUMPAD1); break;
         case Key.NUM2       : doType(mode,KeyEvent.VK_NUMPAD2); break;
         case Key.NUM3       : doType(mode,KeyEvent.VK_NUMPAD3); break;
         case Key.NUM4       : doType(mode,KeyEvent.VK_NUMPAD4); break;
         case Key.NUM5       : doType(mode,KeyEvent.VK_NUMPAD5); break;
         case Key.NUM6       : doType(mode,KeyEvent.VK_NUMPAD6); break;
         case Key.NUM7       : doType(mode,KeyEvent.VK_NUMPAD7); break;
         case Key.NUM8       : doType(mode,KeyEvent.VK_NUMPAD8); break;
         case Key.NUM9       : doType(mode,KeyEvent.VK_NUMPAD9); break;
         case Key.SEPARATOR  : doType(mode,KeyEvent.VK_SEPARATOR); break;
         case Key.NUM_LOCK   : doType(mode,KeyEvent.VK_NUM_LOCK); break;
         case Key.ADD        : doType(mode,KeyEvent.VK_ADD); break;
         case Key.MINUS      : doType(mode,KeyEvent.VK_MINUS); break;
         case Key.MULTIPLY   : doType(mode,KeyEvent.VK_MULTIPLY); break;
         case Key.DIVIDE     : doType(mode,KeyEvent.VK_DIVIDE); break;
         default:
            throw new IllegalArgumentException("Cannot type character " + character);
      }
   }

}
