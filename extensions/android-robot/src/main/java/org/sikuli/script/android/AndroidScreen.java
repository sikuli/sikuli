/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script.android;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import org.sikuli.script.Region;
import org.sikuli.script.IRobot;
import org.sikuli.script.IScreen;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Location;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

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
      String[] ANDROID_JARS = {"monkeyrunner.jar", "guavalib.jar", "sdklib.jar", "ddmlib.jar" };
      for(String jar : ANDROID_JARS){
         String path = ANDROID_ROOT + "/tools/lib/"+ jar;
         Debug.log("load android jar: " + path);
         try{
            ClassPathHack.addFile(path);
         }
         catch(IOException e){
            Debug.error("Can't load Android lib: " + jar + "\n" + e.getMessage());
         }
      }
      System.setProperty("java.library.path", ANDROID_ROOT + SdkConstants.OS_SDK_TOOLS_LIB_FOLDER);
      System.setProperty("com.android.monkeyrunner.bindir", ANDROID_ROOT + SdkConstants.OS_SDK_TOOLS_FOLDER);
   };

   private void initRobots(long timeoutMs, String deviceIdRegex){
      Debug.log("AndroidRobot.init");
      try{
         AdbBackend adb = new AdbBackend();
         if(timeoutMs<0 || deviceIdRegex == null)
            _dev = adb.waitForConnection();
         else
            _dev = adb.waitForConnection(timeoutMs, deviceIdRegex);
      }
      catch(Exception e){
         Debug.error("no connection to android device.");
         e.printStackTrace();
      }
      _robot = new AndroidRobot(_dev);

      Rectangle b = getBounds();
      init(b.x, b.y, b.width, b.height, this);
   }

   private void initRobots(){
      initRobots(-1, null);
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

   public AndroidScreen(long timeout, String deviceIdRegex) {
      initRobots(timeout, deviceIdRegex);
   }

   public AndroidScreen() {
      initRobots();
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


   public <PSRML> int type(PSRML target, String text, int modifiers) throws FindFailed{
      click(target, 0);
      if(text != null){
         Debug.history(
           (modifiers!=0?KeyEvent.getKeyModifiersText(modifiers)+"+":"")+
               "ANDROID.TYPE \"" + text + "\"");
         _robot.pressModifiers(modifiers);
         _dev.type(text); //FIXME: assume text don't have special keys
         _robot.releaseModifiers(modifiers);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
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

class ClassPathHack {
    private static final Class[] parameters = new Class[] {URL.class};

    public static void addFile(String s) throws IOException
    {
        File f = new File(s);
        addFile(f);
    }

    public static void addFile(File f) throws IOException
    {
        //f.toURL is deprecated
        addURL(f.toURL());
    }

    public static void addURL(URL u) throws IOException
    {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }
}
