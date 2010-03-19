package edu.mit.csail.uid;

import java.awt.*;
import java.awt.Robot.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class ScreenCapturer implements Observer{
   Robot _robot;
   BufferedImage _screen;
   File _tmp;
   Rectangle _roi = null;

   static Rectangle fullscreen = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );

   public ScreenCapturer(int x, int y, int w, int h) throws AWTException{ 
      //FIXME: get screen device according to x,y,w,h
      _robot = new Robot();
      _robot.setAutoDelay(100);
      _roi = new Rectangle(x, y, w, h);
   }

   public ScreenCapturer() throws AWTException{ 
      _robot = new Robot();
      _robot.setAutoDelay(100);
   }

   public ScreenImage capture() {
      if(_roi!=null)
         return capture(_roi);
      return capture(fullscreen);
   }

   public ScreenImage capture(int x, int y, int w, int h) {
      Rectangle rect = new Rectangle(x,y,w,h);
      return capture(rect);
   }

   public ScreenImage capture(Rectangle rect) {
      Debug.log(1, "capture: " + rect);
      BufferedImage img = _robot.createScreenCapture(rect);
      return new ScreenImage(rect, img);
   }

   /*
   public String capture(Rectangle rect) throws IOException{
      System.out.println( "capture: " + rect );
      _screen = _robot.createScreenCapture(rect);
      _tmp = File.createTempFile("sikuli",".png");
      _tmp.deleteOnExit();
      ImageIO.write(_screen, "png", _tmp);
      return _tmp.getAbsolutePath();
   }
   */

   protected void finalize() throws Throwable {
      _tmp.delete();
//      System.out.println("delete " + _tmp.getAbsolutePath());
   }

   boolean waitPrompt;
   CapturePrompt prompt;
   public String promptCapture() {
      waitPrompt = true;
      Thread th = new Thread(){
         public void run(){
            System.out.println("starting CapturePrompt...");
            prompt = new CapturePrompt(ScreenCapturer.this);
         }
      };
      th.start();
      try{
         int count=0;
         while(waitPrompt){ 
            Thread.sleep(200); 
            if(count++ > 100) return null;
            //System.out.println(count);
         }
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }
      String file = prompt.getSelection();
      prompt.close();
      return file;
   }

   public void update(Subject s){
      waitPrompt = false;
   }
}
