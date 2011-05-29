/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.Rectangle;

public interface IRobot {
   void keyPress(int keycode);  
   void keyRelease(int keycode);  
   void mouseMove(int x, int y);
   void mousePress(int buttons);
   void mouseRelease(int buttons);
   void mouseWheel(int wheelAmt);
   ScreenImage captureScreen(Rectangle screenRect);
   void waitForIdle();
   void delay(int ms);
   void setAutoDelay(int ms);
}

