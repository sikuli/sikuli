package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;

public class Screen extends Region implements Observer {
   private GraphicsDevice _curGD;
   private int _curID = 0;

   private boolean _waitPrompt;
   private CapturePrompt _prompt;

   static GraphicsDevice[] _gdev;
   static GraphicsEnvironment _genv;
   static Robot[] _robots;

   static{
      _genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
      _gdev = _genv.getScreenDevices();
      try{
         _robots = new Robot[_gdev.length];
         for(int i=0;i<_gdev.length;i++){
            _robots[i] = new Robot(_gdev[i]);
            _robots[i].setAutoWaitForIdle(false);
            _robots[i].setAutoDelay(0);
         }
      }
      catch(AWTException e){
         System.err.println("Can't initiate Java Robot: " + e);
      }
   }


   public static int getNumberScreens(){
      return _gdev.length;
   }

   public static Robot getRobot(int id){
      return _robots[id];
   }
   
   public Robot getRobot(){
      return _robots[_curID];
   }

   public GraphicsDevice getGraphicsDevice(){
      return _curGD;
   }

   public static Rectangle getBounds(int id){
      return _gdev[id].getDefaultConfiguration().getBounds();
   }

   public Rectangle getBounds(){
      return _curGD.getDefaultConfiguration().getBounds();
   }

   public Screen(int id) {
      if(id<_gdev.length){
         _curGD = _gdev[id];
         _curID = id;
      }
      else
         initGD();
      Rectangle bounds = getBounds();
      init((int)bounds.getX(), (int)bounds.getY(),
            (int)bounds.getWidth(), (int)bounds.getHeight(), this);
   }

   private void initGD(){
      _curGD = _genv.getDefaultScreenDevice();
      for(int i=0;i<_gdev.length;i++)
         if(_gdev[i] == _curGD)
            _curID = i;
   }

   public Screen() {
      initGD();
      Rectangle bounds = getBounds();
      init((int)bounds.getX(), (int)bounds.getY(),
            (int)bounds.getWidth(), (int)bounds.getHeight(), this);

   }

   public ScreenImage capture() {
      return capture(getBounds());
   }

   public ScreenImage capture(int x, int y, int w, int h) {
      Rectangle rect = new Rectangle(x,y,w,h);
      return capture(rect);
   }

   public ScreenImage capture(Rectangle rect) {
      Debug.log(3, "capture: " + rect);
      BufferedImage img = _robots[_curID].createScreenCapture(rect);
      return new ScreenImage(rect, img);
   }

   public ScreenImage userCapture() {
      _waitPrompt = true;
      Thread th = new Thread(){
         public void run(){
            System.out.println("starting CapturePrompt...");
            _prompt = new CapturePrompt(Screen.this);
         }
      };
      th.start();
      try{
         int count=0;
         while(_waitPrompt){ 
            Thread.sleep(100); 
            if(count++ > 1000) return null;
         }
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }
      ScreenImage ret = _prompt.getSelection();
      _prompt.close();
      return ret;
   }

   public void update(Subject s){
      _waitPrompt = false;
   }
}
