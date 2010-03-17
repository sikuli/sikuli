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

   static Rectangle fullscreen = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );

   public ScreenCapturer() throws AWTException{ 
      _robot = new Robot();
      _robot.setAutoDelay(100);
   }

   public String capture(int x, int y, int w, int h) throws IOException{
      Rectangle rect = new Rectangle(x,y,w,h);
      return capture(rect);
   }

   public String capture() throws IOException{
      return capture(fullscreen);
   }

   public String capture(Rectangle rect) throws IOException{
      System.out.println( "capture: " + rect );
      _screen = _robot.createScreenCapture(rect);
      _tmp = File.createTempFile("sikuli",".png");
      _tmp.deleteOnExit();
      ImageIO.write(_screen, "png", _tmp);
      return _tmp.getAbsolutePath();
   }

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
