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

public class DesktopRobot extends Robot implements IRobot{
   final static int MAX_DELAY = 60000;

   public DesktopRobot(GraphicsDevice screen) throws AWTException{
      super(screen);
   
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
}
