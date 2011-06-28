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
   * @param x_ X position
   * @param y_ Y position  
   * @param w_ width
   * @param h_ heigth
   */
   public Region(int x_, int y_, int w_, int h_) {
      init(x_,y_,w_,h_, null);
   }

  /**
   * Create a region from a Rectangle
   *
   * @param r the Rectangle
   */
   public Region(Rectangle r) {
      init(r.x, r.y, r.width, r.height, null);
   }

  /**
   * Create a region from an other region
   *
   * @param r the region
   */
   public Region(Region r) {
      init(r.x, r.y, r.w, r.h, null);
   }

   public String toString(){
      return String.format("Region[%d,%d %dx%d]@%s E:%s, T:%.1f", 
                            x, y, w, h, _scr.toString(),
                            _throwException?"Y":"N", _autoWaitTimeout);
   }

   Region(Rectangle r, IScreen parentScreen) {
      init(r.x, r.y, r.width, r.height, parentScreen);
   }


   protected Region(){}

   void init(int x_, int y_, int w_, int h_, IScreen parentScreen) {
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

   void smoothMove(Location dest){
      long delay = (long)(Settings.MoveMouseDelay * 1000);
      if(delay == 0){
         _robot.mouseMove(dest.x, dest.y);
         return;
      }

      Location src = Env.getMouseLocation();
      Animator aniX = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.x, (float)dest.x, delay));
      Animator aniY = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.y, (float)dest.y, delay));
      while(aniX.running()){
         float x = aniX.step();
         float y = aniY.step();
         _robot.mouseMove((int)x, (int)y);
         _robot.delay(50);
      }
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
      return new Region(x+loc.x, y+loc.y, w, h);
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
      return new Region(rect);
   }

   public Region right(){  return right(9999999); }
   public Region right(int range){
      Rectangle bounds = getScreen().getBounds();
      Rectangle rect = new Rectangle(x+w,y,range,h);
      rect = rect.intersection(bounds);
      return new Region(rect);
   }

   public Region left(){   return left(9999999);   }
   public Region left(int range){
      Rectangle bounds = getScreen().getBounds();
      Region r = new Region(this);
      r.x = x-range < bounds.x? bounds.x: x-range;
      r.y = y;
      r.w = x - r.x;
      r.h = h;
      return r;
   }

   public Region above(){    return above(9999999);    }
   public Region above(int range){
      Rectangle bounds = getScreen().getBounds();
      Region r = new Region(this);
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
      return new Region(rect);
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
         Match match = null;
         try{
            match = doFind(target);
         }catch (Exception e){
            throw new FindFailed(e.getMessage());
         }
         
         if (match != null){
            _lastMatch = match;
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
         throw new FindFailed("can not find " + target);
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
         
         Iterator<Match> matches = null;
         try{
            
            if(_autoWaitTimeout > 0){
               RepeatableFindAll rf = new RepeatableFindAll(target);
               rf.repeat(_autoWaitTimeout);
               matches = rf.getMatches();
            }
            else{
               matches = doFindAll(target);
            }
            
         }catch (Exception e){
            throw new FindFailed(e.getMessage());
         }
         
         if (matches != null){
            _lastMatches = matches;
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
         smoothMove(loc);
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
         pressModifiers(modifiers);
         if(drag(loc1)!=0){
            _robot.delay((int)(Settings.DelayAfterDrag*1000));
            ret = dropAt(loc2, Settings.DelayBeforeDrop);
         }
         releaseModifiers(modifiers);
         return 1;
      }
      return 0;
   }

   public <PSRML> int drag(PSRML target) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         smoothMove(loc);
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
         smoothMove(loc);
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
            pressModifiers(modifiers);
            type_ch(text.charAt(i), PRESS_RELEASE); 
            releaseModifiers(modifiers);
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

   private String _hold_keys = "";
   public void keyDown(String keys){
      if(keys != null){
         for(int i=0; i < keys.length(); i++){
            if(_hold_keys.indexOf(keys.charAt(i)) == -1){
               Debug.log(5, "press: " + keys.charAt(i));
               type_ch(keys.charAt(i), PRESS_ONLY); 
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
            type_ch(keys.charAt(i), RELEASE_ONLY); 
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
      ScreenImage simg = _scr.capture(x, y, w, h);
      _lastScreenImage = simg;
      return TextRecognizer.getInstance().recognize(simg);
   }

   ////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS
   ////////////////////////////////////////////////////////////////

   static final int K_SHIFT = InputEvent.SHIFT_MASK;
   static final int K_CTRL = InputEvent.CTRL_MASK;
   static final int K_META = InputEvent.META_MASK;
   static final int K_ALT = InputEvent.ALT_MASK;

   /**
    * Match findNow( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match 
    * without waiting.
    */
   public <PSC> Match findNow(PSC ptn) throws FindFailed{
      ScreenImage simg = _scr.capture(x, y, w, h);
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
      pressModifiers(modifiers);
      smoothMove(loc);
      _scr.showClick(loc);
      _robot.mousePress(buttons);
      _robot.mouseRelease(buttons);
      if( dblClick ){
         _robot.mousePress(buttons);
         _robot.mouseRelease(buttons);
      }
      releaseModifiers(modifiers);
      _robot.waitForIdle();
      return 1;
   }

   private void pressModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyPress(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyPress(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyPress(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0){
         if( Env.getOS() == OS.WINDOWS )
            _robot.keyPress(KeyEvent.VK_WINDOWS);
         else
            _robot.keyPress(KeyEvent.VK_META);
      }
   }

   private void releaseModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyRelease(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0){ 
         if( Env.getOS() == OS.WINDOWS )
            _robot.keyRelease(KeyEvent.VK_WINDOWS);
         else
            _robot.keyRelease(KeyEvent.VK_META);
      }
   }

   Location toRobotCoord(Location l){
      return new Location(l.x-x, l.y-y);
   }

   Match toGlobalCoord(Match m){
      m.x += x;
      m.y += y;
      return m;
   }

   static final int PRESS_ONLY = 0;
   static final int RELEASE_ONLY = 1;
   static final int PRESS_RELEASE = 2;
   private void doType(int mode, int... keyCodes) {
      if(mode==PRESS_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            _robot.keyPress(keyCodes[i]);
         }
      }
      else if(mode==RELEASE_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            _robot.keyRelease(keyCodes[i]);
         }
      }
      else{
         for(int i=0;i<keyCodes.length;i++)
            _robot.keyPress(keyCodes[i]);
         for(int i=0;i<keyCodes.length;i++)
            _robot.keyRelease(keyCodes[i]);
      }
   }


   private void type_ch(char character, int mode) {
      switch (character) {
         case 'a': doType(mode,KeyEvent.VK_A); break;
         case 'b': doType(mode,KeyEvent.VK_B); break;
         case 'c': doType(mode,KeyEvent.VK_C); break;
         case 'd': doType(mode,KeyEvent.VK_D); break;
         case 'e': doType(mode,KeyEvent.VK_E); break;
         case 'f': doType(mode,KeyEvent.VK_F); break;
         case 'g': doType(mode,KeyEvent.VK_G); break;
         case 'h': doType(mode,KeyEvent.VK_H); break;
         case 'i': doType(mode,KeyEvent.VK_I); break;
         case 'j': doType(mode,KeyEvent.VK_J); break;
         case 'k': doType(mode,KeyEvent.VK_K); break;
         case 'l': doType(mode,KeyEvent.VK_L); break;
         case 'm': doType(mode,KeyEvent.VK_M); break;
         case 'n': doType(mode,KeyEvent.VK_N); break;
         case 'o': doType(mode,KeyEvent.VK_O); break;
         case 'p': doType(mode,KeyEvent.VK_P); break;
         case 'q': doType(mode,KeyEvent.VK_Q); break;
         case 'r': doType(mode,KeyEvent.VK_R); break;
         case 's': doType(mode,KeyEvent.VK_S); break;
         case 't': doType(mode,KeyEvent.VK_T); break;
         case 'u': doType(mode,KeyEvent.VK_U); break;
         case 'v': doType(mode,KeyEvent.VK_V); break;
         case 'w': doType(mode,KeyEvent.VK_W); break;
         case 'x': doType(mode,KeyEvent.VK_X); break;
         case 'y': doType(mode,KeyEvent.VK_Y); break;
         case 'z': doType(mode,KeyEvent.VK_Z); break;
         case 'A': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
         case 'B': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
         case 'C': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
         case 'D': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
         case 'E': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
         case 'F': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
         case 'G': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
         case 'H': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
         case 'I': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
         case 'J': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
         case 'K': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
         case 'L': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
         case 'M': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
         case 'N': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
         case 'O': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
         case 'P': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
         case 'Q': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
         case 'R': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
         case 'S': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
         case 'T': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
         case 'U': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
         case 'V': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
         case 'W': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
         case 'X': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
         case 'Y': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
         case 'Z': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
         case '`': doType(mode,KeyEvent.VK_BACK_QUOTE); break;
         case '0': doType(mode,KeyEvent.VK_0); break;
         case '1': doType(mode,KeyEvent.VK_1); break;
         case '2': doType(mode,KeyEvent.VK_2); break;
         case '3': doType(mode,KeyEvent.VK_3); break;
         case '4': doType(mode,KeyEvent.VK_4); break;
         case '5': doType(mode,KeyEvent.VK_5); break;
         case '6': doType(mode,KeyEvent.VK_6); break;
         case '7': doType(mode,KeyEvent.VK_7); break;
         case '8': doType(mode,KeyEvent.VK_8); break;
         case '9': doType(mode,KeyEvent.VK_9); break;
         case '-': doType(mode,KeyEvent.VK_MINUS); break;
         case '=': doType(mode,KeyEvent.VK_EQUALS); break;
         case '~': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
         case '!': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
         case '@': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
         case '#': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_3); break;
         case '$': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_4); break;
         case '%': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
         case '^': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_6); break;
         case '&': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_7); break;
         case '*': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_8); break;
         case '(': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_9); break;
         case ')': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_0); break;
         case '_': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
         case '+': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS); break;
         case '\b': doType(mode,KeyEvent.VK_BACK_SPACE); break;
         case '\t': doType(mode,KeyEvent.VK_TAB); break;
         case '\r': doType(mode,KeyEvent.VK_ENTER); break;
         case '\n': doType(mode,KeyEvent.VK_ENTER); break;
         case '[': doType(mode,KeyEvent.VK_OPEN_BRACKET); break;
         case ']': doType(mode,KeyEvent.VK_CLOSE_BRACKET); break;
         case '\\': doType(mode,KeyEvent.VK_BACK_SLASH); break;
         case '{': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
         case '}': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
         case '|': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
         case ';': doType(mode,KeyEvent.VK_SEMICOLON); break;
         case ':': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
         case '\'': doType(mode,KeyEvent.VK_QUOTE); break;
         case '"': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE); break;
         case ',': doType(mode,KeyEvent.VK_COMMA); break;
         case '<': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break;
         case '.': doType(mode,KeyEvent.VK_PERIOD); break;
         case '>': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break;
         case '/': doType(mode,KeyEvent.VK_SLASH); break;
         case '?': doType(mode,KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
         case ' ': doType(mode,KeyEvent.VK_SPACE); break;
         case '\u001b': doType(mode,KeyEvent.VK_ESCAPE); break;
         case '\ue000': doType(mode,KeyEvent.VK_UP); break;
         case '\ue001': doType(mode,KeyEvent.VK_RIGHT); break;
         case '\ue002': doType(mode,KeyEvent.VK_DOWN); break;
         case '\ue003': doType(mode,KeyEvent.VK_LEFT); break;
         case '\ue004': doType(mode,KeyEvent.VK_PAGE_UP); break;
         case '\ue005': doType(mode,KeyEvent.VK_PAGE_DOWN); break;
         case '\ue006': doType(mode,KeyEvent.VK_DELETE); break;
         case '\ue007': doType(mode,KeyEvent.VK_END); break;
         case '\ue008': doType(mode,KeyEvent.VK_HOME); break;
         case '\ue009': doType(mode,KeyEvent.VK_INSERT); break;
         case '\ue011': doType(mode,KeyEvent.VK_F1); break;
         case '\ue012': doType(mode,KeyEvent.VK_F2); break;
         case '\ue013': doType(mode,KeyEvent.VK_F3); break;
         case '\ue014': doType(mode,KeyEvent.VK_F4); break;
         case '\ue015': doType(mode,KeyEvent.VK_F5); break;
         case '\ue016': doType(mode,KeyEvent.VK_F6); break;
         case '\ue017': doType(mode,KeyEvent.VK_F7); break;
         case '\ue018': doType(mode,KeyEvent.VK_F8); break;
         case '\ue019': doType(mode,KeyEvent.VK_F9); break;
         case '\ue01A': doType(mode,KeyEvent.VK_F10); break;
         case '\ue01B': doType(mode,KeyEvent.VK_F11); break;
         case '\ue01C': doType(mode,KeyEvent.VK_F12); break;
         case '\ue01D': doType(mode,KeyEvent.VK_F13); break;
         case '\ue01E': doType(mode,KeyEvent.VK_F14); break;
         case '\ue01F': doType(mode,KeyEvent.VK_F15); break;
         case '\ue020': doType(mode,KeyEvent.VK_SHIFT); break;
         case '\ue021': doType(mode,KeyEvent.VK_CONTROL); break;
         case '\ue022': doType(mode,KeyEvent.VK_ALT); break;
         case '\ue023': doType(mode,KeyEvent.VK_META); break;
         case '\ue024': doType(mode,KeyEvent.VK_PRINTSCREEN); break;
         case '\ue025': doType(mode,KeyEvent.VK_SCROLL_LOCK); break;
         case '\ue026': doType(mode,KeyEvent.VK_PAUSE); break;
         case '\ue027': doType(mode,KeyEvent.VK_CAPS_LOCK); break;
         case '\ue030': doType(mode,KeyEvent.VK_NUMPAD0); break;
         case '\ue031': doType(mode,KeyEvent.VK_NUMPAD1); break;
         case '\ue032': doType(mode,KeyEvent.VK_NUMPAD2); break;
         case '\ue033': doType(mode,KeyEvent.VK_NUMPAD3); break;
         case '\ue034': doType(mode,KeyEvent.VK_NUMPAD4); break;
         case '\ue035': doType(mode,KeyEvent.VK_NUMPAD5); break;
         case '\ue036': doType(mode,KeyEvent.VK_NUMPAD6); break;
         case '\ue037': doType(mode,KeyEvent.VK_NUMPAD7); break;
         case '\ue038': doType(mode,KeyEvent.VK_NUMPAD8); break;
         case '\ue039': doType(mode,KeyEvent.VK_NUMPAD9); break;
         case '\ue03A': doType(mode,KeyEvent.VK_SEPARATOR); break;
         case '\ue03B': doType(mode,KeyEvent.VK_NUM_LOCK); break;
         case '\ue03C': doType(mode,KeyEvent.VK_ADD); break;
         case '\ue03D': doType(mode,KeyEvent.VK_MINUS); break;
         case '\ue03E': doType(mode,KeyEvent.VK_MULTIPLY); break;
         case '\ue03F': doType(mode,KeyEvent.VK_DIVIDE); break;
         default:
            throw new IllegalArgumentException("Cannot type character " + character);
      }
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


