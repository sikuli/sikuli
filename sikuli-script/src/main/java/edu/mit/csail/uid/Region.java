package edu.mit.csail.uid;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class Region {
   private Robot _robot;
   private Screen _scr;

   public int x, y, w, h;

   private boolean _stopIfWaitingFailed = true;
   private double _waitBeforeAction = 3.0;


   public Region(int x_, int y_, int w_, int h_) {
      init(x_,y_,w_,h_);
   }

   public Region(Region r) {
      init(r.x, r.y, r.w, r.h);
   }

   protected Region(){}

   void init(int x_, int y_, int w_, int h_) {
      x = x_;
      y = y_;
      w = w_;
      h = h_;
      initScreen();
   }

   void init(int x_, int y_, int w_, int h_, Screen scr) {
      x = x_;
      y = y_;
      w = w_;
      h = h_;
      _scr = scr;
      _robot = scr.getRobot();
   }

   void initScreen(){
      Rectangle roi = new Rectangle(x, y, w, h);
      for(int i=0;i<Screen.getNumberScreens();i++){
         Rectangle scrBound = Screen.getBounds(i);
         if(scrBound.contains(roi)){
            _scr = new Screen(i);
            _robot = _scr.getRobot();
            return;
         }
      }
      _scr = new Screen();
      _robot = _scr.getRobot();
   }

   public Screen getScreen(){ return _scr;   }

   public int getX(){ return x; }
   public int getY(){ return y; }
   public int getW(){ return w; }
   public int getH(){ return h; }

   public void setX(int _x){ x = _x; }
   public void setY(int _y){ y = _y; }
   public void setW(int _w){ w = _w; }
   public void setH(int _h){ h = _h; }

   public Rectangle getROI(){ return new Rectangle(x,y,w,h); }
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

   public Rectangle getRect(){ return getROI(); }
   public void setRect(Rectangle roi){ setROI(roi); }

   public Location getCenter(){ 
      return new Location(x+w/2, y+h/2);
   }

   ///// SPATIAL OPERATORS
   public Region nearby(){
      final int PADDING = 100;
      return nearby(PADDING);
   }

   public Region nearby(int range){
      Region r = new Region(this);
      r.x = x<range? 0 : x-range;
      r.y = y<range? 0 : y-range;
      r.w += range*2; 
      r.h += range*2;
      return r;
   }

   public Region right(){  return right(9999999); }
   public Region right(int range){
      Region r = new Region(this);
      r.x = x+w;
      r.y = y;
      r.w = range;
      r.h = h;
      return r;
   }

   public Region left(){   return left(9999999);   }
   public Region left(int range){
      Region r = new Region(this);
      r.x = x-range < 0? 0: x-range;
      r.y = y;
      r.w = x;
      r.h = h;
      return r;
   }

   public Region above(){    return above(9999999);    }
   public Region above(int range){
      Region r = new Region(this);
      r.x = x;
      r.y = y-range < 0? 0:y-range;
      r.w = w;
      r.h = y;
      return r;
   }

   public Region below(){     return below(999999);   }
   public Region below(int range){
      Region r = new Region(this);
      r.x = x;
      r.y = y+h;
      r.w = w;
      r.h = range;
      return r;
   }

   public Region inside(){ 
      return this;
   }

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
   public <PSC> Match find(PSC ptn) throws FindFailed{
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
    * Iterator<Match> findAll( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match.
    * If AutoWaitTimeout is set, this is equivalent to wait().
    */
   public <PSC> Iterator<Match> findAll(PSC ptn) 
                                             throws  FindFailed{
      if(_waitBeforeAction > 0)
         return waitAll(ptn, _waitBeforeAction);
      else{
         Iterator<Match> ms = findAllNow(ptn);
         if(ms == null && _stopIfWaitingFailed)
            throw new FindFailed(ptn + " can't be found.");
         return ms;
      }
   }



   /**
    *  Match wait(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target appears or timeout (in second) is passed
    */
   public <PSC> Match wait(PSC target, double timeout) throws FindFailed{
      Iterator<Match> ms = waitAll(target, timeout);
      Match ret = null;
      if(ms != null)
         ret = ms.next();
      if(ms instanceof Finder)
         ((Finder)ms).destroy();
      return ret;
   }

   /**
    *  boolean waitVanish(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target vanishes or timeout (in second) is passed
    *  @return true if the target vanishes, otherwise returns false.
    */
   public <PSC> boolean waitVanish(PSC target, double timeout) {
      int MaxTimePerScan = (int)(1000.0/Settings.WaitScanRate); 
      long begin_t = (new Date()).getTime();
      while( begin_t + timeout*1000 > (new Date()).getTime() ){
         long before_find = (new Date()).getTime();
         try{
            Match ms = findNow(target);
            if(ms == null)
               return true;
         }
         catch(FindFailed e){
            return true;
         }
         long after_find = (new Date()).getTime();
         if(after_find-before_find<MaxTimePerScan)
            _robot.delay((int)(MaxTimePerScan-(after_find-before_find)));
         else
            _robot.delay(10);
      }
      return false;
   }

   public <PSRML> int click(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON1_MASK, modifiers, false);
   }

   public <PSRML> int doubleClick(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON1_MASK, modifiers, true);
   }

   public <PSRML> int rightClick(PSRML target, int modifiers) 
                                                throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      return _click(loc, InputEvent.BUTTON3_MASK, modifiers, false);
   }

   public <PSRML> int hover(PSRML target) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if( loc != null){
         _robot.mouseMove(loc.x, loc.y);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public <PSRML> int dragDrop(PSRML t1, PSRML t2, int modifiers)
                                             throws  FindFailed {
      int ret = 0;
      pressModifiers(modifiers);
      if(drag(t1)!=0){
         _robot.delay((int)(Settings.DelayAfterDrag*1000));
         ret = dropAt(t2, Settings.DelayBeforeDrop);
      }
      releaseModifiers(modifiers);
      return ret;
   }

   public <PSRML> int drag(PSRML target) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _scr.showTarget(loc);
         _robot.mouseMove(loc.x, loc.y);
         _robot.mousePress(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public <PSRML> int dropAt(PSRML target, double delay) throws  FindFailed{
      Location loc = getLocationFromPSRML(target);
      if(loc != null){
         _scr.showDropTarget(loc);
         _robot.mouseMove(loc.x, loc.y);
         _robot.delay((int)(delay*1000));
         _robot.mouseRelease(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
         return 1;
      }
      return 0;
   }

   public <PSRML> int type(PSRML target, String text, int modifiers) 
                                                throws  FindFailed{
      click(target, 0);
      if(text != null){
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
   public <PSC> Match findNow(PSC ptn) throws  FindFailed{
      ScreenImage simg = _scr.capture(x, y, w, h);
      Finder f = new Finder(simg, this);
      Match ret = null;
      f.find(ptn);
      if(f.hasNext())
         ret = f.next();
      f.destroy();
      return ret;
   }

   /**
    * Match findAllNow( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match 
    * without waiting.
    */
   public <PSC> Iterator<Match> findAllNow(PSC ptn) 
                                             throws  FindFailed{
      ScreenImage simg = _scr.capture(x, y, w, h);
      Finder f = new Finder(simg, this);
      f.find(ptn);
      if(f.hasNext()) 
         return f;
      return null;
   }

   /**
    *  Iterator<Match> waitAll(Pattern/String/PatternClass target, timeout-sec)
    *  waits until target appears or timeout (in second) is passed
    */
   public <PSC> Iterator<Match> waitAll(PSC target, double timeout) 
                                             throws  FindFailed{
      int MaxTimePerScan = (int)(1000.0/Settings.WaitScanRate); 
      long begin_t = (new Date()).getTime();
      while( begin_t + timeout*1000 > (new Date()).getTime() ){
         long before_find = (new Date()).getTime();
         Iterator<Match> ms = findAllNow(target);
         if(ms != null)
            return ms;
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

   private <PSRML> Location getLocationFromPSRML(PSRML target) 
                                             throws  FindFailed {
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
      if(loc == null)
         return 0;
      Debug.log("click on " + loc + " BTN: " + buttons + ", MOD: " +modifiers); 
      pressModifiers(modifiers);
      _robot.mouseMove(loc.x, loc.y);
      _scr.showTarget(loc);
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
      if((modifiers & K_META) != 0) _robot.keyPress(KeyEvent.VK_META);
   }

   private void releaseModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyRelease(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0) _robot.keyRelease(KeyEvent.VK_META);
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
      for(int i=0;i<keyCodes.length;i++){
         if(mode==PRESS_ONLY){
            _robot.keyPress(keyCodes[i]);
         }
         else if(mode==RELEASE_ONLY){
            _robot.keyRelease(keyCodes[i]);
         }
         else{
            _robot.keyPress(keyCodes[i]);
            _robot.keyRelease(keyCodes[i]);
         }
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
         default:
            throw new IllegalArgumentException("Cannot type character " + character);
      }
   }

}


