package edu.mit.csail.uid;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class Region {
   private ScreenCapturer _capturer;
   private Robot _robot;

   public int x, y, w, h;
   public Rectangle ROI;

   private boolean _stopIfWaitingFailed = true;
   private double _waitBeforeAction = 3.0;

   private Location _center;

   public Region(int x_, int y_, int w_, int h_) throws AWTException{
      init(x_,y_,w_,h_);
   }

   protected Region(){}

   void init(int x_, int y_, int w_, int h_) throws AWTException{
      x = x_;
      y = y_;
      w = w_;
      h = h_;
      setROI(new Rectangle(x, y, w, h));
      for(int i=0;i<Screen.getNumberScreens();i++){
         Rectangle scrBound = Screen.getBounds(i);
         if(scrBound.contains(ROI)){
            _robot = new Robot(Screen.getGraphicsDevice(i));
            return;
         }
      }
      _robot = new Robot();
   }

   public int getX(){ return x; }
   public int getY(){ return y; }
   public int getW(){ return w; }
   public int getH(){ return h; }

   public void setX(int _x){ x = _x; updateROI(); }
   public void setY(int _y){ y = _y; updateROI(); }
   public void setW(int _w){ w = _w; updateROI(); }
   public void setH(int _h){ h = _h; updateROI(); }

   public Rectangle getROI(){ return ROI; }
   public void setROI(Rectangle roi){
      ROI = new Rectangle(roi); 
      _center = new Location((int)ROI.getCenterX(), (int)ROI.getCenterY());
   }

   public Rectangle getRect(){ return getROI(); }
   public void setRect(Rectangle roi){ setROI(roi); }

   
   private void updateROI(){
      setROI(new Rectangle(x, y, w, h));
   }

   public Location getCenter(){ return _center; }

   //////////// Settings
   public void setThrowException(boolean flag){ _stopIfWaitingFailed = flag; } 
   public void setAutoWaitTimeout(double sec){ _waitBeforeAction = sec; }

   public boolean getThrowException(){ return _stopIfWaitingFailed; }
   public double getAutoWaitTimeout(){ return _waitBeforeAction; }


   ////////////

   /**
    * Match find( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match.
    * If AutoWaitTimeout is set, this is equivalent to wait().
    */
   public <T> Match find(T ptn) throws AWTException, FindFailed{
      if(_waitBeforeAction > 0)
         return wait(ptn, _waitBeforeAction);
      else{
         Match match = findNow(ptn);
         if(match == null && _stopIfWaitingFailed)
            throw new FindFailed(ptn + " can't be found.");
         return match;
      }
   }

   /**
    * Match findNow( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match 
    * without waiting.
    */
   public <T> Match findNow(T ptn) throws AWTException, FindFailed{
      ScreenImage simg = getCapturer().capture();
      Finder f = new Finder(simg);
      Match ret = null;
      if( ptn instanceof Pattern ){
         Pattern p = (Pattern)ptn;
         f.find(p.imgURL, p.similarity);
      }
      else if( ptn instanceof String){
         f.find((String)ptn);
      }
      else{
         throw new FindFailed("doesn't support the type of target: " + ptn);
      }
      if(f.hasNext()){
         ret = toGlobalCoord(f.next());
         if( ptn instanceof Pattern ){
            Location c = ret.getCenter();
            Location offset = ((Pattern)ptn).getTargetOffset();
            ret.setTargetOffset(offset);
         }
      }
      f.destroy();
      return ret;
   }

   /**
    *  Match wait(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target appears or timeout (in second) is passed
    */
   public <T> Match wait(T target, double timeout) 
                                             throws AWTException, FindFailed{
      int MaxTimePerScan = (int)(1000.0/Settings.WaitScanRate); 
      long begin_t = (new Date()).getTime();
      while( begin_t + timeout*1000 > (new Date()).getTime() ){
         long before_find = (new Date()).getTime();
         Match match = findNow(target);
         if(match != null)
            return match;
         long after_find = (new Date()).getTime();
         if(after_find-before_find<MaxTimePerScan)
            _robot.delay((int)(MaxTimePerScan-(after_find-before_find)));
         else
            _robot.delay(10);
      }
      if(_stopIfWaitingFailed)
         throw new FindFailed(target + " can't be found.");
      return null;
   }

   public <PSRML> int click(PSRML target, int modifiers) 
                                                throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON1_MASK, modifiers, false);
   }

   public <PSRML> int doubleClick(PSRML target, int modifiers) 
                                                throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON1_MASK, modifiers, true);
   }

   public <PSRML> int rightClick(PSRML target, int modifiers) 
                                                throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON3_MASK, modifiers, false);
   }

   public <PSRML> int hover(PSRML target) throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      if( loc != null){
         _robot.mouseMove(loc.x, loc.y);
         return 1;
      }
      return 0;
   }

   public <PSRML> int dragDrop(PSRML t1, PSRML t2, double delay1, double delay2)
                                             throws AWTException, FindFailed {
      if(drag(t1)!=0){
         _robot.delay((int)(delay1*1000));
         return dropAt(t2, delay2);
      }
      return 0;
   }

   public <PSRML> int drag(PSRML target) throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _robot.mouseMove(loc.x, loc.y);
         _robot.mousePress(InputEvent.BUTTON1_MASK);
         return 1;
      }
      return 0;
   }

   public <PSRML> int dropAt(PSRML target, double delay) throws AWTException, FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _robot.mouseMove(loc.x, loc.y);
         _robot.delay((int)(delay*1000));
         _robot.mouseRelease(InputEvent.BUTTON1_MASK);
         return 1;
      }
      return 0;
   }

   ////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS
   ////////////////////////////////////////////////////////////////

   static final int K_SHIFT = InputEvent.SHIFT_MASK;
   static final int K_CTRL = InputEvent.CTRL_MASK;
   static final int K_META = InputEvent.META_MASK;
   static final int K_ALT = InputEvent.ALT_MASK;

   private <PSRML> Location getLocationFromPSRML(PSRML target) 
                                             throws AWTException, FindFailed {
      if(target instanceof Pattern || target instanceof String){
         Match m = find(target);
         return m.getTarget();
      }
      if(target instanceof Match) return ((Match)target).getTarget();
      if(target instanceof Region) return ((Region)target).getCenter();
      if(target instanceof Location) return (Location)target;
      return null;
   }

   private int _click(Location loc, int buttons, int modifiers, 
                      boolean dblClick) {
      Debug.log("click on " + loc + " BTN: " + buttons + ", MOD: " +modifiers); 
      pressModifiers(modifiers);
      _robot.mouseMove(loc.x, loc.y);
      //showClick(m.x, m.y, m.w, m.h); FIXME
      _robot.mousePress(buttons);
      _robot.mouseRelease(buttons);
      if( dblClick ){
         _robot.mousePress(buttons);
         _robot.mouseRelease(buttons);
      }
      releaseModifiers(modifiers);
      return 1;
   }

   private void pressModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyPress(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyPress(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyPress(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0) _robot.keyPress(KeyEvent.VK_META);
   }

   private void releaseModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyRelease(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0) _robot.keyRelease(KeyEvent.VK_META);
   }


   protected Match toGlobalCoord(Match m){
      m.x += x;
      m.y += y;
      return m;
   }

   private ScreenCapturer getCapturer() throws AWTException{
      if(_capturer == null)
         _capturer = new ScreenCapturer(x, y, w, h);
      return _capturer;
   }
}


