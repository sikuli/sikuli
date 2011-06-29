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

   public AndroidRobot(MonkeyDevice dev){
      _dev = dev;
      _mouse = new Location(0,0);
   }

   public void keyPress(int keycode){
      Debug.log(5, "AndroidRobot.keypress");
   }
   public void keyRelease(int keycode){
   }  
   public void mouseMove(int x, int y){
      _mouse.x = x;
      _mouse.y = y;
   }
   public void mousePress(int buttons){
      Debug.log(5, "AndroidRobot.mousePress");
      _dev.touch(_mouse.x, _mouse.y, MonkeyDevice.TouchPressType.DOWN);
   }
   public void mouseRelease(int buttons){
      Debug.log(5, "AndroidRobot.mouseRelease");
      _dev.touch(_mouse.x, _mouse.y, MonkeyDevice.TouchPressType.UP);
   }

   public void smoothMove(Location dest){
   }
   public void smoothMove(Location src, Location dest, long ms){
   }

   public void dragDrop(Location start, Location end, int steps, long ms, int buttons){
      _dev.drag(start.x, start.y, end.x, end.y, steps, ms);
   }
             

   public void mouseWheel(int wheelAmt){
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
   }

   public void delay(int ms){
      try{
         Thread.sleep(ms);
      }
      catch(InterruptedException e){
      }
   }
   public void setAutoDelay(int ms){
   }
}

