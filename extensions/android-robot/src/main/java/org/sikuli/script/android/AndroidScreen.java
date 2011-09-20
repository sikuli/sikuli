/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.android;

import java.awt.*;
import java.awt.image.*;
import org.sikuli.script.Region;
import org.sikuli.script.IRobot;
import org.sikuli.script.IScreen;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Location;
import org.sikuli.script.Debug;
import java.io.File;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.adb.AdbBackend;
import com.android.sdklib.SdkConstants;
import org.python.core.PyObject;

public class AndroidScreen extends Region implements IScreen {
   protected MonkeyDevice _dev;
   protected IRobot _robot;

   static {
      //TODO: Let android.path be configurable
      String ANDROID_ROOT = System.getProperty("android.path") + File.separator;
      System.setProperty("java.library.path", ANDROID_ROOT + SdkConstants.OS_SDK_TOOLS_LIB_FOLDER);
      System.setProperty("com.android.monkeyrunner.bindir", ANDROID_ROOT + SdkConstants.OS_SDK_TOOLS_FOLDER);
   };

   private void initRobots(){
      Debug.log("AndroidRobot.init");
      try{
         AdbBackend adb = new AdbBackend();
         _dev = adb.waitForConnection();
      }
      catch(Exception e){
         Debug.error("no connection to android device.");
         e.printStackTrace();
      }
      _robot = new AndroidRobot(_dev);
   }

   public Region newRegion(Rectangle rect){
      return Region.create(rect, this);
   }
   public IRobot getRobot(){
      return _robot;
   }

   public Rectangle getBounds(){
      String width = _dev.getProperty("display.width");
      String height = _dev.getProperty("display.height");
      return new Rectangle(0, 0, 
                           Integer.parseInt(width), 
                           Integer.parseInt(height));
   }

   public AndroidScreen() {
      initRobots();
      Rectangle b = getBounds();
      init(b.x, b.y, b.width, b.height, this);
   }

   public ScreenImage capture() {
      return capture(getBounds());
   }

   public ScreenImage capture(int x, int y, int w, int h) {
      return _robot.captureScreen(new Rectangle(x,y,w,h));
   }

   public ScreenImage capture(Rectangle rect) {
      return _robot.captureScreen(rect);
   }

   public ScreenImage capture(Region reg) {
      return capture(reg.getROI());
   }


   public void showMove(Location loc){
   }
   public void showClick(Location loc){
   }
   public void showTarget(Location loc){
   }
   public void showDropTarget(Location loc){
   }


}
