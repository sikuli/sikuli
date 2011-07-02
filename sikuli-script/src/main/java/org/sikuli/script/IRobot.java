/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.Rectangle;

public interface IRobot {
   enum KeyMode {
      PRESS_ONLY, RELEASE_ONLY, PRESS_RELEASE
   };
   void keyPress(int keycode);  
   void keyRelease(int keycode);  
   void typeChar(char character, KeyMode mode);
   void mouseMove(int x, int y);
   void mousePress(int buttons);
   void mouseRelease(int buttons);
   void smoothMove(Location dest);
   void smoothMove(Location src, Location dest, long ms);
   void dragDrop(Location start, Location end, int steps, long ms, int buttons);
   void mouseWheel(int wheelAmt);
   ScreenImage captureScreen(Rectangle screenRect);
   void waitForIdle();
   void delay(int ms);
   void setAutoDelay(int ms);
   
}

