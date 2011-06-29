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
import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.MonkeyRunner;
import com.android.monkeyrunner.MonkeyImage;

public class AndroidRobot implements IRobot {
   protected MonkeyDevice _dev;

   public AndroidRobot(MonkeyDevice dev){
      _dev = dev;
   }

   public void keyPress(int keycode){
      Debug.log("AndroidRobot.keypress");
   }
   public void keyRelease(int keycode){
   }  
   public void mouseMove(int x, int y){
   }
   public void mousePress(int buttons){
      Debug.log("AndroidRobot.mousePress");
   }
   public void mouseRelease(int buttons){
   }

   public void mouseWheel(int wheelAmt){
   }

   public ScreenImage captureScreen(Rectangle screenRect){
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
   }
   public void setAutoDelay(int ms){
   }
}

