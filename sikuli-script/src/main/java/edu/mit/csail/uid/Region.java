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
            type_ch(text.charAt(i)); 
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
      //showClick(m.x, m.y, m.w, m.h); FIXME
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

   private void doType(int... keyCodes) {
      doType(keyCodes, 0, keyCodes.length);
   }

   private void doType(int[] keyCodes, int offset, int length) {
      if (length == 0) {
         return;
      }

      _robot.keyPress(keyCodes[offset]);
      doType(keyCodes, offset + 1, length - 1);
      _robot.keyRelease(keyCodes[offset]);
   }


   private void type_ch(char character) {
      switch (character) {
         case 'a': doType(KeyEvent.VK_A); break;
         case 'b': doType(KeyEvent.VK_B); break;
         case 'c': doType(KeyEvent.VK_C); break;
         case 'd': doType(KeyEvent.VK_D); break;
         case 'e': doType(KeyEvent.VK_E); break;
         case 'f': doType(KeyEvent.VK_F); break;
         case 'g': doType(KeyEvent.VK_G); break;
         case 'h': doType(KeyEvent.VK_H); break;
         case 'i': doType(KeyEvent.VK_I); break;
         case 'j': doType(KeyEvent.VK_J); break;
         case 'k': doType(KeyEvent.VK_K); break;
         case 'l': doType(KeyEvent.VK_L); break;
         case 'm': doType(KeyEvent.VK_M); break;
         case 'n': doType(KeyEvent.VK_N); break;
         case 'o': doType(KeyEvent.VK_O); break;
         case 'p': doType(KeyEvent.VK_P); break;
         case 'q': doType(KeyEvent.VK_Q); break;
         case 'r': doType(KeyEvent.VK_R); break;
         case 's': doType(KeyEvent.VK_S); break;
         case 't': doType(KeyEvent.VK_T); break;
         case 'u': doType(KeyEvent.VK_U); break;
         case 'v': doType(KeyEvent.VK_V); break;
         case 'w': doType(KeyEvent.VK_W); break;
         case 'x': doType(KeyEvent.VK_X); break;
         case 'y': doType(KeyEvent.VK_Y); break;
         case 'z': doType(KeyEvent.VK_Z); break;
         case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
         case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
         case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
         case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
         case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
         case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
         case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
         case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
         case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
         case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
         case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
         case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
         case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
         case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
         case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
         case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
         case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
         case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
         case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
         case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
         case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
         case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
         case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
         case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
         case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
         case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
         case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
         case '0': doType(KeyEvent.VK_0); break;
         case '1': doType(KeyEvent.VK_1); break;
         case '2': doType(KeyEvent.VK_2); break;
         case '3': doType(KeyEvent.VK_3); break;
         case '4': doType(KeyEvent.VK_4); break;
         case '5': doType(KeyEvent.VK_5); break;
         case '6': doType(KeyEvent.VK_6); break;
         case '7': doType(KeyEvent.VK_7); break;
         case '8': doType(KeyEvent.VK_8); break;
         case '9': doType(KeyEvent.VK_9); break;
         case '-': doType(KeyEvent.VK_MINUS); break;
         case '=': doType(KeyEvent.VK_EQUALS); break;
         case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
         case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
         case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
         case '#': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_3); break;
         case '$': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_4); break;
         case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
         case '^': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_6); break;
         case '&': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_7); break;
         case '*': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_8); break;
         case '(': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_9); break;
         case ')': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_0); break;
         case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
         case '+': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS); break;
         case '\b': doType(KeyEvent.VK_BACK_SPACE); break;
         case '\t': doType(KeyEvent.VK_TAB); break;
         case '\r': doType(KeyEvent.VK_ENTER); break;
         case '\n': doType(KeyEvent.VK_ENTER); break;
         case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
         case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
         case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
         case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
         case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
         case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
         case ';': doType(KeyEvent.VK_SEMICOLON); break;
         case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
         case '\'': doType(KeyEvent.VK_QUOTE); break;
         case '"': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE); break;
         case ',': doType(KeyEvent.VK_COMMA); break;
         case '<': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break;
         case '.': doType(KeyEvent.VK_PERIOD); break;
         case '>': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break;
         case '/': doType(KeyEvent.VK_SLASH); break;
         case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
         case ' ': doType(KeyEvent.VK_SPACE); break;
         case '\u001b': doType(KeyEvent.VK_ESCAPE); break;
         case '\ue000': doType(KeyEvent.VK_UP); break;
         case '\ue001': doType(KeyEvent.VK_RIGHT); break;
         case '\ue002': doType(KeyEvent.VK_DOWN); break;
         case '\ue003': doType(KeyEvent.VK_LEFT); break;
         case '\ue004': doType(KeyEvent.VK_PAGE_UP); break;
         case '\ue005': doType(KeyEvent.VK_PAGE_DOWN); break;
         case '\ue006': doType(KeyEvent.VK_DELETE); break;
         case '\ue007': doType(KeyEvent.VK_END); break;
         case '\ue008': doType(KeyEvent.VK_HOME); break;
         case '\ue009': doType(KeyEvent.VK_INSERT); break;
         case '\ue011': doType(KeyEvent.VK_F1); break;
         case '\ue012': doType(KeyEvent.VK_F2); break;
         case '\ue013': doType(KeyEvent.VK_F3); break;
         case '\ue014': doType(KeyEvent.VK_F4); break;
         case '\ue015': doType(KeyEvent.VK_F5); break;
         case '\ue016': doType(KeyEvent.VK_F6); break;
         case '\ue017': doType(KeyEvent.VK_F7); break;
         case '\ue018': doType(KeyEvent.VK_F8); break;
         case '\ue019': doType(KeyEvent.VK_F9); break;
         case '\ue01A': doType(KeyEvent.VK_F10); break;
         case '\ue01B': doType(KeyEvent.VK_F11); break;
         case '\ue01C': doType(KeyEvent.VK_F12); break;
         case '\ue01D': doType(KeyEvent.VK_F13); break;
         case '\ue01E': doType(KeyEvent.VK_F14); break;
         case '\ue01F': doType(KeyEvent.VK_F15); break;
         default:
                   throw new IllegalArgumentException("Cannot type character " + character);
      }
   }

}


