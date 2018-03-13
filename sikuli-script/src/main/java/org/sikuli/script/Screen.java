/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.util.ArrayList;
import java.awt.*;
import java.awt.image.*;

public class Screen extends Region implements Observer, IScreen {
//   protected GraphicsDevice _curGD;
   protected int _curID = 0;
   protected static int _primaryScreen = -1;

   protected boolean _waitPrompt;
   protected CapturePrompt _prompt;
   protected ScreenHighlighter _overlay;

//   static GraphicsDevice[] _gdev;
   static GraphicsEnvironment _genv;
   static ArrayList<IRobot> _robots;

   static{
      _genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
//      _gdev = _genv.getScreenDevices();
      initRobots();
   }

   public Region newRegion(Rectangle rect){
      return Region.create(rect, this);
   }

   private static void initRobots(){
      try{
         _robots = new ArrayList<IRobot>();
         GraphicsDevice[] _gdev = _genv.getScreenDevices();
         for(int i=0;i<_gdev.length;i++){
            DesktopRobot robot=new DesktopRobot(_gdev[i]);
            _robots.add(robot );
            //_robots[i].setAutoWaitForIdle(false); //TODO: make sure we don't need this
            robot.setAutoDelay(10);
         }
      }
      catch(AWTException e){
         Debug.error("Can't initiate Java Robot: " + e);
      }
   }

   public static int connectVNC(String[] args,String password)  {
      VNCRobot vnc=new VNCRobot(args,password);
      _robots.clear(); // have to clear, this will overlap the main screen and find() will look for the screen by coords.
      int i=_robots.size();
      _robots.add(vnc);
      _primaryScreen=i;
      return i;
   }

   public static int getNumberScreens(){
//      return _gdev.length;
      return _robots.size();
   }

   public static IRobot getRobot(int id){
      return _robots.get(id);
   }

   public static int getPrimaryId(){
      if(_primaryScreen < 0){
         _primaryScreen = 0;
         for(int i=0;i<getNumberScreens();i++){
            Rectangle bound = getBounds(i);
            if(bound.x == 0 && bound.y == 0){
               _primaryScreen = i;
               break;
            }
         }
      }
      return _primaryScreen;
   }
   
   public IRobot getRobot(){
      return getRobot(getPrimaryId());
   }

   public GraphicsDevice getGraphicsDevice(){
      return getRobot().getGraphicsDevice();
   }

   public static Rectangle getBounds(int id){
//      return getRobot(id).getGraphicsDevice().getDefaultConfiguration().getBounds();
      return getRobot(id).getBounds();
   }

   public Rectangle getBounds(){
//      return getRobot().getGraphicsDevice().getDefaultConfiguration().getBounds();
      return getBounds(getPrimaryId());
   }

   public int getID(){
      return _curID;
   }

   public Screen(int id) {
      if(id<getNumberScreens()){
//         _curGD = _gdev[id];
         _curID = id;
      }
      else
         initGD();
      initBounds();
   }

   protected void initBounds(){
      Rectangle bounds = getBounds();
      init((int)bounds.getX(), (int)bounds.getY(),
           (int)bounds.getWidth(), (int)bounds.getHeight(), this);
      _overlay = new ScreenHighlighter(this);
   }

   private void initGD(){
      GraphicsDevice _curGD = _genv.getDefaultScreenDevice();
      for(int i=0;i<getNumberScreens();i++)
         if(getRobot(i).getGraphicsDevice() == _curGD)
            _curID = i;
   }

   public Screen() {
      initGD();
      initBounds();
   }

   public ScreenImage capture() {
      return capture(getBounds());
   }

   public ScreenImage capture(int x, int y, int w, int h) {
      Rectangle rect = new Rectangle(x,y,w,h);
      return capture(rect);
   }

   public ScreenImage capture(Rectangle rect) {
      Debug.log(5, "capture: " + rect);
      Rectangle bounds = getBounds();
      rect.x -= bounds.x;
      rect.y -= bounds.y;
      ScreenImage simg = getRobot(_curID).captureScreen(rect);
      simg.x += bounds.x;
      simg.y += bounds.y;
      return simg;
   }

   public ScreenImage capture(Region reg) {
      return capture(reg.getROI());
   }

   public ScreenImage userCapture() {
      return userCapture("Select a region on the screen");
   }

   public ScreenImage userCapture(final String msg) {
      _waitPrompt = true;
      Thread th = new Thread(){
         public void run(){
            _prompt = new CapturePrompt(Screen.this, Screen.this);
            _prompt.prompt(msg);
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

   public Region selectRegion(){
      return selectRegion("Select a region on the screen");
   }


   public Region selectRegion(final String msg){
      ScreenImage sim = userCapture(msg);
      if(sim == null)
         return null;
      Rectangle r = sim.getROI();
      return Region.create((int)r.getX(), (int)r.getY(), 
                        (int)r.getWidth(), (int)r.getHeight());
   }

   public void showMove(Location loc){
      showTarget(loc);
   }
   
   /**
    * Show the click
    *
    * @param loc the location of the click
    */
   public void showClick(Location loc){
      showTarget(loc);
   }

   public void showTarget(Location loc){
      showTarget(loc, Settings.SlowMotionDelay);
   }

   public void showTarget(Location loc, double secs){
      if(Settings.ShowActions){
         _overlay.showTarget(loc, (float)secs);
      }
   }

   public void showDropTarget(Location loc){
      if(Settings.ShowActions){
         _overlay.showDropTarget(loc, Settings.SlowMotionDelay);
      }
   }

   boolean useFullscreen(){
      return false;
   }


   public String toString(){
      Rectangle r = getBounds();
      return String.format("Screen(%d)[%d,%d %dx%d] E:%s, T:%.1f",
               _curID, (int)r.getX(), (int)r.getY(), 
               (int)r.getWidth(), (int)r.getHeight(),
               _throwException?"Y":"N", _autoWaitTimeout);
   }
}
