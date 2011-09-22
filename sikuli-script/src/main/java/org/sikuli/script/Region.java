/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import org.python.util.PythonInterpreter;
import org.python.core.*;

public class Region {
   final static float DEFAULT_HIGHLIGHT_TIME = 2f;
   private IRobot _robot;
   private IScreen _scr;
   private ScreenHighlighter _overlay = null;

   public int x, y, w, h;
   
   protected FindFailedResponse _defaultFindFailedResponse = 
                                          FindFailedResponse.ABORT;
   public void setFindFailedResponse(FindFailedResponse res){
      _defaultFindFailedResponse = res;
   }
   public FindFailedResponse getFindFailedResponse(){
      return _defaultFindFailedResponse;
   }

   protected boolean _throwException = true;
   protected double _autoWaitTimeout = 3.0;

   protected boolean _observing = false;
   protected EventManager _evtMgr = null;

   // the last captured screen image the last match or matches are based on
   // TODO: consider moving this to Screen class
   protected ScreenImage _lastScreenImage;
   
   protected Match _lastMatch;
   protected Iterator<Match> _lastMatches;


  /**
   * Create a region with the provided coordinate / size
   *
   * @deprecated Not for public use in Java. Use Region.create() instead.
   *
   * @param x_ X position
   * @param y_ Y position  
   * @param w_ width
   * @param h_ heigth
   */
   @Deprecated
   public Region(int x_, int y_, int w_, int h_) {
      init(x_,y_,w_,h_, null);
   }

  /**
   * Create a region from a Rectangle
   *
   * @deprecated Not for public use in Java. Use Region.create() instead.
   *
   * @param r the Rectangle
   */
   @Deprecated
   public Region(Rectangle r) {
      init(r.x, r.y, r.width, r.height, null);
   }

  /**
   * Create a region from an other region
   *
   * @deprecated Not for public use in Java. Use Region.create() instead.
   *
   * @param r the region
   */
   @Deprecated
   public Region(Region r) {
      init(r.x, r.y, r.w, r.h, r.getScreen());
   }

   Region(Rectangle r, IScreen parentScreen) {
      init(r.x, r.y, r.width, r.height, parentScreen);
   }

  /**
   * Create a region from a Rectangle
   *
   * @param r the Rectangle
   */
   public static Region create(Rectangle rect){
      return create(new Region(rect));
   }
   
  /**
   * Create a region with the provided coordinate / size
   *
   * @param x_ X position
   * @param y_ Y position  
   * @param w_ width
   * @param h_ heigth
   */
   public static Region create(int x_, int y_, int w_, int h_) {
      return create(new Region(x_, y_, w_, h_));
   }

   public static Region create(Rectangle r, IScreen parentScreen){
      return create(new Region(r, parentScreen));
   }

  /**
   * Create a region from an other region
   *
   * @param r the region
   */
   public static Region create(Region r){
      //TODO: determine if the caller is Jython
      return toJythonRegion(r);
   }

   protected Region(){}

   /////////////////////////////////////////////////////////////////
   
   public String toString(){
      return String.format("Region[%d,%d %dx%d]@%s E:%s, T:%.1f", 
                            x, y, w, h, _scr.toString(),
                            _throwException?"Y":"N", _autoWaitTimeout);
   }


   protected void init(int x_, int y_, int w_, int h_, IScreen parentScreen) {
      x = x_;
      y = y_;
      w = w_;
      h = h_;
      if(parentScreen != null)
         _scr = parentScreen;
      else
         _scr = initScreen();
      _robot = _scr.getRobot();
   }

   protected EventManager getEventManager(){
      if(_evtMgr == null)
         _evtMgr = new EventManager(this);
      return _evtMgr;
   }

   private Screen initScreen(){
      if(this instanceof Screen)
         return (Screen)this;
      Rectangle roi = new Rectangle(x, y, w, h);
      for(int i=0;i<Screen.getNumberScreens();i++){
         Rectangle scrBound = Screen.getBounds(i);
         if(scrBound.contains(roi)){
            return new Screen(i);
         }
      }
      return new Screen();
   }


   protected void updateSelf(){
      if(_overlay != null)
         _overlay.highlight(this);
   }

   ////////////////////////////////////////////////////////

   public IScreen getScreen(){ return _scr;   }

   public int getX(){ return x; }
   public int getY(){ return y; }
   public int getW(){ return w; }
   public int getH(){ return h; }

   public void setX(int _x){ x = _x; }
   public void setY(int _y){ y = _y; }
   public void setW(int _w){ w = _w; }
   public void setH(int _h){ h = _h; }

   public Rectangle getROI(){ return new Rectangle(x,y,w,h); }
   public void setROI(int X, int Y, int W, int H){
      x = X;   y = Y;   w = W;   h = H;
   }
   public void setROI(Region roi){
      x = roi.x;
      y = roi.y;
      w = roi.w;
      h = roi.h;
   }

   public void setROI(Rectangle roi){
      x = (int)roi.getX();
      y = (int)roi.getY();
      w = (int)roi.getWidth();
      h = (int)roi.getHeight();
   }

   public void highlight(){
      if(_overlay==null)
         highlight(true);
      else
         highlight(false);
   }

   protected void highlight(boolean toEnable){
      Debug.history("toggle highlight " + toString() + ": " + toEnable); 
      if(!(_scr instanceof Screen)){
         Debug.error("highlight only work on the physical desktop screens.");
         return;
      }
      Screen scr = (Screen)getScreen();
      if(toEnable){
         _overlay = new ScreenHighlighter(scr);
         _overlay.highlight(this);
      }
      else{
         if(_overlay != null){
            _overlay.close();
            _overlay = null;
         }
      }
   }

   public void highlight(float secs){
      Debug.history("highlight " + toString() + " for " + secs + " secs"); 
      if(!(_scr instanceof Screen)){
         Debug.error("highlight only work on the physical desktop screens.");
         return;
      }
      Screen scr = (Screen)getScreen();
      ScreenHighlighter overlay = new ScreenHighlighter(scr);
      overlay.highlight(this, secs);
   }

   public Rectangle getRect(){ return getROI(); }
   public void setRect(Rectangle roi){ setROI(roi); }
   public void setRect(int X, int Y, int W, int H){ setROI(X, Y, W, H); }
   public void setRect(Region roi){ setROI(roi); }

   public Location getCenter(){ 
      return new Location(x+w/2, y+h/2);
   }

   public Location getTopLeft(){ 
      return new Location(x, y);
   }

   public Location getTopRight(){ 
      return new Location(x+w, y);
   }

   public Location getBottomLeft(){ 
      return new Location(x, y+h);
   }

   public Location getBottomRight(){ 
      return new Location(x+w, y+h);
   }

   ///// SPATIAL OPERATORS

   public Region offset(Location loc){
      return Region.create(x+loc.x, y+loc.y, w, h);
   }

   public Region moveTo(Location loc){
      x = loc.x;
      y = loc.y;
      updateSelf();
      return this;
   }

   public Region morphTo(Region r){
      x = r.x;
      y = r.y;
      w = r.w;
      h = r.h;
      updateSelf();
      return this;
   }

   public Region nearby(){
      final int PADDING = 50;
      return nearby(PADDING);
   }

   public Region nearby(int range){
      Rectangle bounds = getScreen().getBounds();
      Rectangle rect = new Rectangle(x-range,y-range,w+range*2,h+range*2);
      rect = rect.intersection(bounds);
      return Region.create(rect);
   }

   public Region right(){  return right(9999999); }
   public Region right(int range){
      Rectangle bounds = getScreen().getBounds();
      Rectangle rect = new Rectangle(x+w,y,range,h);
      rect = rect.intersection(bounds);
      return Region.create(rect);
   }

   public Region left(){   return left(9999999);   }
   public Region left(int range){
      Rectangle bounds = getScreen().getBounds();
      Region r = Region.create(this);
      r.x = x-range < bounds.x? bounds.x: x-range;
      r.y = y;
      r.w = x - r.x;
      r.h = h;
      return r;
   }

   public Region above(){    return above(9999999);    }
   public Region above(int range){
      Rectangle bounds = getScreen().getBounds();
      Region r = Region.create(this);
      r.x = x;
      r.y = y-range < bounds.y? bounds.y : y-range;
      r.w = w;
      r.h = y-r.y;
      return r;
   }

   public Region below(){     return below(999999);   }
   public Region below(int range){
      Rectangle bounds = getScreen().getBounds();
      Rectangle rect = new Rectangle(x,y+h,w,range);
      rect = rect.intersection(bounds);
      return Region.create(rect);
   }

   public Region inside(){ 
      return this;
   }

   //////////// Settings
   public void setThrowException(boolean flag){ 
      _throwException = flag; 
      if (_throwException){
         _defaultFindFailedResponse = FindFailedResponse.ABORT;
      }else{
         _defaultFindFailedResponse = FindFailedResponse.SKIP;         
      }
   } 
   
   public void setAutoWaitTimeout(double sec){ _autoWaitTimeout = sec; }

   public boolean getThrowException(){ return _throwException; }
   public double getAutoWaitTimeout(){ return _autoWaitTimeout; }



   
   //////////// CORE FUNCTIONS

   /**
    * Match find( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match.
    * If AutoWaitTimeout is set, this is equivalent to wait().
    *
    * @param target A search criteria 
    * @return If found, the element. null otherwise
    * @throws FindFailed if the Find operation failed
    */
   public <PSC> Match find(final PSC target) throws FindFailed{
      if(_autoWaitTimeout > 0){
         return wait(target, _autoWaitTimeout);
      }
      while (true){
         try{
            _lastMatch = doFind(target);
         }catch (Exception e){
            throw new FindFailed(e.getMessage());
         }
         
         if (_lastMatch != null){
            return _lastMatch;
         }       
         
         if (!handleFindFailed(target))
            return null;
      }
   }
   
   // return false to skip
   // return true to try again
   // throw FindFailed to abort
   <PSC> boolean handleFindFailed(PSC target) throws FindFailed{
     
      FindFailedResponse response;
      if (_defaultFindFailedResponse == FindFailedResponse.PROMPT){
         FindFailedDialog fd = new FindFailedDialog(target);
         fd.setVisible(true);
         response = fd.getResponse();
         
      }else{
         response = _defaultFindFailedResponse;
      }


      if (response == FindFailedResponse.SKIP){
         return false;
      }else if (response == FindFailedResponse.RETRY){
         return true;
      }else if (response == FindFailedResponse.ABORT){
         throw new FindFailed("can not find " + target + " on the screen.");
      }
      
      return false;
   }

   /**
    * Iterator<Match> findAll( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match.
    * If AutoWaitTimeout is set, this is equivalent to wait().
    *
    * @param target A search criteria 
    * @return All elements matching
    * @throws FindFailed if the Find operation failed
    */
   public <PSC> Iterator<Match> findAll(PSC target) 
                                             throws  FindFailed{
      
      while (true){
         
         try{
            
            if(_autoWaitTimeout > 0){
               RepeatableFindAll rf = new RepeatableFindAll(target);
               rf.repeat(_autoWaitTimeout);
               _lastMatches = rf.getMatches();
            }
            else{
               _lastMatches = doFindAll(target);
            }
            
         }catch (Exception e){
            throw new FindFailed(e.getMessage());
         }
         
         if (_lastMatches != null){
            return _lastMatches;
         }       
         
         if (!handleFindFailed(target))
            return null;
      }     
   }


   //WARNING: wait(long timeout) is taken by Java Object
   public void wait(double timeout) {
      try{
         Thread.sleep((long)(timeout*1000L));
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }
   }
   

   
   public <PSC> Match wait(PSC target) throws FindFailed{
      return wait(target, _autoWaitTimeout);
   }
   

   /**
    *  Match wait(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target appears or timeout (in second) is passed
    * 
    * @param target A search criteria 
    * @param timeout Timeout in seconds
    * @return All elements matching
    * @throws FindFailed if the Find operation failed
    */
   public <PSC> Match wait(PSC target, double timeout) throws FindFailed{
      
      while (true){         
         try {
            Debug.log(2, "waiting for " + target + " to appear");
            RepeatableFind rf = new RepeatableFind(target);
            rf.repeat(timeout);
            _lastMatch = rf.getMatch();
            
         } catch (Exception e) {
            throw new FindFailed(e.getMessage());
         }  
         
         if (_lastMatch != null){
            Debug.log(2, "" + target + " has appeared.");
            break;
         }

         Debug.log(2, "" + target + " has not appeared.");
         
         if (!handleFindFailed(target))
            return null;
      }
      
      return _lastMatch;
   }

   /**
    *  Check if target exists (with the default autoWaitTimeout)
    * 
    * @param target A search criteria 
    * @return The element matching
    */
   public <PSC> Match exists(PSC target) {
      return exists(target, _autoWaitTimeout);
   }

   /**
    *  Check if target exists with a specified timeout
    * 
    * @param target A search criteria 
    * @param timeout Timeout in second
    * @return The element matching
    */
   public <PSC> Match exists(PSC target, double timeout) {
      try{
         RepeatableFind rf = new RepeatableFind(target);
         if (rf.repeat(timeout)){
            _lastMatch = rf.getMatch();
            return _lastMatch;
         }
      }
      catch(Exception ff){
         // TODO: This should throw an exception since
         // it is likely caused by not able to read the input
         // image.
      }
      return null;
   }


   public <PSC> boolean waitVanish(PSC target) {
      return waitVanish(target, _autoWaitTimeout);
   }

   /**
    *  boolean waitVanish(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target vanishes or timeout (in second) is passed
    *  @return true if the target vanishes, otherwise returns false.
    */
   public <PSC> boolean waitVanish(PSC target, double timeout) {
      try {
         Debug.log(2, "waiting for " + target + " to vanish");
         RepeatableVanish r = new RepeatableVanish(target);
         if (r.repeat(timeout)){
            // target has vanished before timeout
            Debug.log(2, "" + target + " has vanished");
            return true;
         }else{            
            // target has not vanished before timeout
            Debug.log(2, "" + target + " has not vanished before timeout");
            return false;
         }

      } catch (Exception e) {
         // TODO: This should throw an error (IOException caused by target
         // image not readable).
         Debug.error(e.getMessage());
      }  

      return false;
   }


  /**
   * Click on the item provided by "target" 
   *
   * @param target  Where to click
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int click(PSRML target) throws FindFailed{
      return click(target, 0);
   }

  /**
   * Click on the item provided by "target" 
   *
   * @param target  Where to click
   * @param modifiers Can be 0 (no modifier), K_SHIFT, K_CTRL, K_ALT or K_META
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int click(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      int ret = _click(loc, InputEvent.BUTTON1_MASK, modifiers, false);
      
      SikuliActionManager.getInstance().clickTarget(this, target, _lastScreenImage, _lastMatch);      
      return ret;
   }


  /**
   * Double click on the item provided by "target" 
   *
   * @param target  Where to double click
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int doubleClick(PSRML target) throws  FindFailed{
      return doubleClick(target, 0);
   }

  /**
   * Double click on the item provided by "target" 
   *
   * @param target  Where to double click
   * @param modifiers Can be 0 (no modifier), K_SHIFT, K_CTRL, K_ALT or K_META
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int doubleClick(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      int ret = _click(loc, InputEvent.BUTTON1_MASK, modifiers, true);

      SikuliActionManager.getInstance().doubleClickTarget(this, target, _lastScreenImage, _lastMatch);      
      return ret;
   }


  /**
   * Right click on the item provided by "target" 
   *
   * @param target  Where to right click
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int rightClick(PSRML target) throws  FindFailed{
      return rightClick(target, 0);
   }


  /**
   * Right click on the item provided by "target" 
   *
   * @param target  Where to right click
   * @param modifiers Can be 0 (no modifier), K_SHIFT, K_CTRL, K_ALT or K_META
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int rightClick(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      int ret = _click(loc, InputEvent.BUTTON3_MASK, modifiers, false);
      
      SikuliActionManager.getInstance().rightClickTarget(this, target, _lastScreenImage, _lastMatch);      
      return ret;
   }

  /**
   * Move the wheel at the current position
   *
   * @param direction the direction applied
   * @param steps the number of step
   * @return 1 in any case
   */
   public int wheel(int direction, int steps) throws FindFailed{
      for(int i=0;i<steps;i++){
         _robot.mouseWheel(direction);
         _robot.delay(50);
      }
      return 1;
   }


  /**
   * Move the wheel at the specified position
   *
   * @param target The specified position
   * @param direction the direction applied
   * @param steps the number of step
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int wheel(PSRML target, int direction, int steps) throws FindFailed{
      if( target == null || hover(target) != 0){
         return wheel(direction, steps);
      }
      return 0;
   }

   public <PSRML> int mouseMove(PSRML target) throws FindFailed{
      return hover(target);
   }

   public <PSRML> int hover(PSRML target) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if( loc != null){
         _scr.showMove(loc);
         _robot.smoothMove(loc);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }


  /**
   * Drag and drop from a position to the other
   *
   * @param t1 The specified source position
   * @param t2 The specified destination position
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int dragDrop(PSRML t1, PSRML t2) throws FindFailed {
      return dragDrop(t1, t2, 0);
   }

  /**
   * Drag and drop from a position to the other
   *
   * @param t1 The specified source position
   * @param t2 The specified destination position
   * @param modifiers Can be 0 (no modifier), K_SHIFT, K_CTRL, K_ALT or K_META
   * @return 1 if success, 0 otherwise
   * @throws FindFailed if the Find operation failed
   */
   public <PSRML> int dragDrop(PSRML t1, PSRML t2, int modifiers)
                                             throws  FindFailed {
      int ret = 0;
      Location loc1 = getLocationFromPSRML(t1);
      Location loc2 = getLocationFromPSRML(t2);
      Debug.history( 
        (modifiers!=0?KeyEvent.getKeyModifiersText(modifiers)+"+":"")+
        "DRAG "  + loc1 + " to " + loc2);
      if(loc1 != null && loc2 != null){
         _robot.pressModifiers(modifiers);
         _robot.dragDrop(loc1, loc2, 10, 
                        (long)(Settings.MoveMouseDelay*1000), InputEvent.BUTTON1_MASK);
         _robot.releaseModifiers(modifiers);
         return 1;
      }
      return 0;
   }

   public <PSRML> int drag(PSRML target) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _robot.smoothMove(loc);
         _scr.showTarget(loc);
         _robot.mousePress(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public <PSRML> int dropAt(PSRML target) throws  FindFailed{
      return dropAt(target, Settings.DelayBeforeDrop);
   }

   public <PSRML> int dropAt(PSRML target, double delay) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _scr.showDropTarget(loc);
         _robot.smoothMove(loc);
         _robot.delay((int)(delay*1000));
         _robot.mouseRelease(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public <PSRML> int type(String text) throws FindFailed{
      return type(null, text, 0);
   }

   public <PSRML> int type(String text, int modifiers) throws FindFailed{
      return type(null, text, modifiers);
   }

   public <PSRML> int type(PSRML target, String text) throws FindFailed{
      return type(target, text, 0);
   }

   public <PSRML> int type(PSRML target, String text, int modifiers) 
                                                throws  FindFailed{
      click(target, 0);
      if(text != null){
         Debug.history(
           (modifiers!=0?KeyEvent.getKeyModifiersText(modifiers)+"+":"")+
               "TYPE \"" + text + "\"");
         for(int i=0; i < text.length(); i++){
            _robot.pressModifiers(modifiers);
            _robot.typeChar(text.charAt(i), IRobot.KeyMode.PRESS_RELEASE); 
            _robot.releaseModifiers(modifiers);
            _robot.delay(20);
         }
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public int paste(String text) throws FindFailed{
      return paste(null, text);
   }

   public <PSRML> int paste(PSRML target, String text) 
                                                throws  FindFailed{
      click(target, 0);
      if(text != null){
         Clipboard.putText(Clipboard.PLAIN, Clipboard.UTF8, 
                           Clipboard.BYTE_BUFFER, text);
         int mod = Env.getHotkeyModifier();
         _robot.keyPress(mod);
         _robot.keyPress(KeyEvent.VK_V);
         _robot.keyRelease(KeyEvent.VK_V);
         _robot.keyRelease(mod);
         return 1;
      }
      return 0;
   }


   private int _hold_buttons = 0;
   public void mouseDown(int buttons) {
      _hold_buttons = buttons;
      _robot.mousePress(buttons);
      _robot.waitForIdle();
   }

   public void mouseUp() {
      mouseUp(0);
   }

   public void mouseUp(int buttons) {
      if(buttons==0)
         _robot.mouseRelease(_hold_buttons);
      else
         _robot.mouseRelease(buttons);
      _robot.waitForIdle();
   }

   /**
    * press down the key (given by the key code) on the underlying device.
    * The code depend on the type of the device.
    */
   public void keyDown(int keycode){
      _robot.keyPress(keycode);
   }

   /**
    * release the key (given by the key code) on the underlying device.
    * The code depend on the type of the device.
    */
   public void keyUp(int keycode){
      _robot.keyRelease(keycode);
   }

   private String _hold_keys = "";
   public void keyDown(String keys){
      if(keys != null){
         for(int i=0; i < keys.length(); i++){
            if(_hold_keys.indexOf(keys.charAt(i)) == -1){
               Debug.log(5, "press: " + keys.charAt(i));
               _robot.typeChar(keys.charAt(i), IRobot.KeyMode.PRESS_ONLY); 
               _hold_keys += keys.charAt(i);
            }
         }
         _robot.waitForIdle();
         return;
      }
   }

   public void keyUp(){
      keyUp(null);
   }

   public void keyUp(String keys){
      if(keys == null)
         keys = _hold_keys;
      for(int i=0; i < keys.length(); i++){
         int pos;
         if( (pos=_hold_keys.indexOf(keys.charAt(i))) != -1 ){
            Debug.log(5, "release: " + keys.charAt(i));
            _robot.typeChar(keys.charAt(i), IRobot.KeyMode.RELEASE_ONLY); 
            _hold_keys = _hold_keys.substring(0,pos) + 
                         _hold_keys.substring(pos+1);
         }
      }
      _robot.waitForIdle();
   }


   public <PSC> void onAppear(PSC target, SikuliEventObserver observer){
      getEventManager().addAppearObserver(target, observer);
   }

   public <PSC> void onVanish(PSC target, SikuliEventObserver observer){
      getEventManager().addVanishObserver(target, observer);
   }

   public void onChange(int threshold, SikuliEventObserver observer){
      getEventManager().addChangeObserver(threshold, observer);
   }

   public void onChange(SikuliEventObserver observer){
      getEventManager().addChangeObserver(Settings.ObserveMinChangedPixels,
                                          observer);
   }

   public void observe(){
      observe(Float.POSITIVE_INFINITY);
   }

   public void observeInBackground(final double secs){
      Thread th = new Thread(){
         public void run(){
            observe(secs);
         }
      };
      th.start();
   }

   public void stopObserver(){
      _observing = false;
   }

   public void observe(double secs){
      if(_evtMgr == null)
         return;
      int MaxTimePerScan = (int)(1000.0/Settings.ObserveScanRate); 
      long begin_t = (new Date()).getTime();
      _observing = true;
      while( _observing && begin_t + secs*1000 > (new Date()).getTime() ){
         long before_find = (new Date()).getTime();
         ScreenImage simg = _scr.capture(x, y, w, h);
         _lastScreenImage = simg;
         _evtMgr.update(simg);
         long after_find = (new Date()).getTime();
         try{
            if(after_find-before_find<MaxTimePerScan)
               Thread.sleep((int)(MaxTimePerScan-(after_find-before_find)));
         }
         catch(Exception e){ }
      }
   }

   public Match getLastMatch(){
      return _lastMatch;
   }

   public Iterator<Match> getLastMatches(){
      return _lastMatches;
   }

   public String text(){
      ScreenImage simg = getScreen().capture(x, y, w, h);
      _lastScreenImage = simg;
      return TextRecognizer.getInstance().recognize(simg);
   }


   ////////////////////////////////////////////////////////////////
   // SPECIAL FUNCTIONS FOR JYTHON
   ////////////////////////////////////////////////////////////////
   

   public static Region toJythonRegion(Region r){
      if(r == null)
         return null;
      PythonInterpreter interpreter = new PythonInterpreter();
      interpreter.exec("from sikuli import Region");
      PyObject regionClass = interpreter.get("Region");
      PyObject pyRegion = regionClass.__call__(Py.java2py(r));
      return (Region)pyRegion.__tojava__(Region.class);
   }



   ////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS
   ////////////////////////////////////////////////////////////////


   /**
    * Match findNow( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match 
    * without waiting.
    */
   public <PSC> Match findNow(PSC ptn) throws FindFailed{
      Debug.log("capture: " + x + "," + y);
      ScreenImage simg = _scr.capture(x, y, w, h);
      Debug.log("ScreenImage: " + simg.getROI());
      _lastScreenImage = simg;
      Finder f = new Finder(simg, this);
      Match ret = null;
      try{
         f.find(ptn);
         if(f.hasNext())
            ret = f.next();
         f.destroy();
      }
      catch(IOException e){
         throw new FindFailed(e.getMessage());
      }
      return ret;
   }
   
   
   <PSC> Match doFind(PSC ptn) throws IOException{
      ScreenImage simg = getScreen().capture(x, y, w, h);
      _lastScreenImage = simg;
      Finder f = new Finder(simg, this);
      f.find(ptn);
      if(f.hasNext()){
         return f.next();
      }
      return null;
   }

   <PSC> Iterator<Match> doFindAll(PSC ptn) throws IOException{
      ScreenImage simg = getScreen().capture(x, y, w, h);
      _lastScreenImage = simg;
      Finder f = new Finder(simg, this);
      f.findAll(ptn);
      if(f.hasNext()){
         return f;
      }
      return null;
   }
   
   /**
    * Match findAllNow( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match 
    * without waiting.
    */
   public <PSC> Iterator<Match> findAllNow(PSC ptn) 
                                             throws  FindFailed{
      ScreenImage simg = _scr.capture(x, y, w, h);
      _lastScreenImage = simg;
      Finder f = new Finder(simg, this);
      try{
         f.findAll(ptn);
         if(f.hasNext()){
            return f;
         }
         f.destroy();
      }
      catch(IOException e){
         throw new FindFailed(e.getMessage());
      }
      return null;
   }

   /**
    *  Iterator<Match> waitAll(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target appears or timeout (in second) is passed
    */
   @Deprecated
   public <PSC> Iterator<Match> waitAll(PSC target, double timeout) 
                                             throws  FindFailed{
      
      while (true){         
         try {

            RepeatableFindAll rf = new RepeatableFindAll(target);
            rf.repeat(timeout);
            _lastMatches = rf.getMatches();
            
         } catch (Exception e) {
            throw new FindFailed(e.getMessage());
         }  
         
         if (_lastMatches != null)
            break;
         
         if (!handleFindFailed(target))
            return null;
      }
      
      return _lastMatches;
   }
   
   /**
    * Figure out where is located target
    *
    * @param target The specified position
    * @return The Region if found. null otherwise.
    */
   public <PSRM> Region getRegionFromPSRM(PSRM target) 
                                             throws  FindFailed {
      if(target instanceof Pattern || target instanceof String){
         Match m = find(target);
         if(m!=null)
            return m;
         return null;
      }
      if(target instanceof Region)
         return (Region)target;
      return null;
   }

    
   /**
    * Figure out where is located target
    *
    * @param target The specified position
    * @return The Location if found. null otherwise.
    */
   public <PSRML> Location getLocationFromPSRML(PSRML target) 
                                             throws  FindFailed {
      if(target instanceof Pattern || target instanceof String){
         Match m = find(target);
         if(m!=null)
            return m.getTarget();
         return null;
      }
      if(target instanceof Match) return ((Match)target).getTarget();
      if(target instanceof Region) return ((Region)target).getCenter();
      if(target instanceof Location) return (Location)target;
      return null;
   }

   private String getClickMsg(Location loc, int buttons, int modifiers, 
                              boolean dblClick){
      String msg = "";
      if(modifiers != 0)
         msg += KeyEvent.getKeyModifiersText(modifiers) + "+";
      if(buttons==InputEvent.BUTTON1_MASK && !dblClick)
         msg += "CLICK";
      if(buttons==InputEvent.BUTTON1_MASK && dblClick)
         msg += "DOUBLE CLICK";
      if(buttons==InputEvent.BUTTON3_MASK)
         msg += "RIGHT CLICK";
      else if(buttons==InputEvent.BUTTON2_MASK)
         msg += "MID CLICK";
      msg += " on " + loc;
      return msg;
   }

   private int _click(Location loc, int buttons, int modifiers, 
                      boolean dblClick) {
      if(loc == null)
         return 0;
      Debug.history( getClickMsg(loc, buttons, modifiers, dblClick) );
      _robot.pressModifiers(modifiers);
      _robot.smoothMove(loc);
      _scr.showClick(loc);
      _robot.mousePress(buttons);
      _robot.mouseRelease(buttons);
      if( dblClick ){
         _robot.mousePress(buttons);
         _robot.mouseRelease(buttons);
      }
      _robot.releaseModifiers(modifiers);
      _robot.waitForIdle();
      return 1;
   }

   Location toRobotCoord(Location l){
      return new Location(l.x-x, l.y-y);
   }

   Match toGlobalCoord(Match m){
      m.x += x;
      m.y += y;
      return m;
   }



   abstract class Repeatable{

      abstract void run() throws Exception;
      abstract boolean ifSuccessful();
      
      // return TRUE if successful before timeout
      // return FALSE if otherwise
      // throws Exception if any unexpected error occurs
      boolean repeat(double timeout) throws Exception{

         int MaxTimePerScan = (int)(1000.0/Settings.WaitScanRate); 
         long begin_t = (new Date()).getTime();
         do{
            long before_find = (new Date()).getTime();
            
            run();
            if (ifSuccessful())
               return true;

            long after_find = (new Date()).getTime();
            if(after_find-before_find<MaxTimePerScan)
               _robot.delay((int)(MaxTimePerScan-(after_find-before_find)));
            else
               _robot.delay(10);
         }while( begin_t + timeout*1000 > (new Date()).getTime() );

         return false;
      }
   }
   
   class RepeatableFind extends Repeatable{
      
      Object _target;
      Match _match = null;
      public <PSC> RepeatableFind(PSC target){
         _target = target;
      }
      
      public Match getMatch() {
         return _match;
      }

      @Override
      public void run() throws IOException{
         _match = doFind(_target);
      }

       @Override
      boolean ifSuccessful() {
         return _match != null;
      }

   }   

   class RepeatableVanish extends RepeatableFind{
      public <PSC> RepeatableVanish(PSC target){
         super(target);
      }
      
      @Override
      boolean ifSuccessful() {
         return _match == null;
      }
   }
   
   class RepeatableFindAll extends Repeatable{
      
      Object _target;
      Iterator<Match> _matches = null;
      public <PSC> RepeatableFindAll(PSC target){
         _target = target;
      }
      
      public Iterator<Match> getMatches() {
         return _matches;
      }

      @Override
      public void run() throws IOException{
         _matches = doFindAll(_target);
      }

       @Override
      boolean ifSuccessful() {
         return _matches != null;
      }

   }
   

}


