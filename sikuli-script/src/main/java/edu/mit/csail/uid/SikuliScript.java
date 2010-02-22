package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
import java.awt.Robot.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;




class ScreenCapturer implements Observer{
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
//      _tmp.delete();
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

public class SikuliScript {
   static final float MATCHING_THRESHOLD = 0.7f;
   static final int DEFAULT_NUM_MATCHES = 1;

   static final int K_SHIFT = InputEvent.SHIFT_MASK;
   static final int K_CTRL = InputEvent.CTRL_MASK;
   static final int K_META = InputEvent.META_MASK;
   static final int K_ALT = InputEvent.ALT_MASK;

   private OverlayWindow _overlay;
   private ScreenMatchProxy _imgMatcher;
   private Robot _robot;
   private OSUtil _osUtil;
   private int _waitBeforeAction = 3000;
   private boolean _stopIfWaitingFailed = true;
   private boolean _showActions = false;
   private String _bundlePath = null;

   public SikuliScript() throws AWTException{
      _overlay = new OverlayWindow();
      _imgMatcher = new ScreenMatchProxy();
      _robot = new Robot();
      String OS = System.getProperty("os.name").toLowerCase();
      if( OS.startsWith("mac os x") )
         _osUtil = new MacUtil();
      else if( OS.startsWith("windows"))
      	_osUtil = new Win32Util();
      else if( OS.startsWith("linux"))
      	_osUtil = new LinuxUtil();
      else{
         System.err.println("Warning: Sikuli doesn't fully support " + OS);
         _osUtil = new DummyOSUtil();
      }
   }

   public void setBundlePath(String path){
      _bundlePath = path;
   }

   public void setThrowException(boolean flag){
      _stopIfWaitingFailed = flag;
   } 

   public void setAutoWaitTimeout(int ms){
      _waitBeforeAction = ms;
   }

   public void setShowActions(boolean flag){
      _showActions = flag;
   }

   public int getAutoWaitTimeout(){   return _waitBeforeAction; }

   public String input(String msg){
      return (String)JOptionPane.showInputDialog(msg);
   }
   
   public String capture() throws AWTException{
      ScreenCapturer capturer = new ScreenCapturer();
      String file = capturer.promptCapture();
      return file;
   }

   public String captureScreen(int x, int y, int w, int h) 
                                          throws AWTException, IOException{
      ScreenCapturer capturer = new ScreenCapturer();
      String screen = capturer.capture(x,y,w,h);
      return screen;
   }

   public int switchApp(String appName){
      return _osUtil.switchApp(appName);
   }

   public int openApp(String appName){
      return _osUtil.openApp(appName);
   }

   public int closeApp(String appName){
      return _osUtil.closeApp(appName);
   }
   

   public Matches findAll(String img) throws IOException, AWTException, FindFailed{
      Pattern p = new Pattern(img).firstN(-1);
      return find(p);
   }
   /**
    *  Matches find(ImageURL img, boolean confirm?)
    */

   public <T> Matches find(T pat) throws IOException, AWTException, FindFailed{
      if(_waitBeforeAction!=0)
         return wait(pat, _waitBeforeAction);
      else{
         Matches match = find_without_wait(pat);
         if(match.size() == 0 && _stopIfWaitingFailed)
            throw new FindFailed(pat + " can't be found.");
         return match;
      }
   }

   public <T> Matches find_without_wait(T pat) throws IOException, AWTException, FindFailed{
      ScreenCapturer capturer = new ScreenCapturer();
      String screen = capturer.capture();
      Matches match = _find(pat, screen);
      return match;
   }

   /**
    *  Matches wait(ImageURL img, int timeout)
    *  wait until img appears or timeout milliseconds is passed
    */
   public <T> Matches wait(T img, int timeout) 
                                       throws IOException, AWTException, FindFailed{
      Matches match = new Matches();
      long begin_t = (new Date()).getTime();
      while( begin_t + timeout > (new Date()).getTime() ){
         match = find_without_wait(img);
         if(match.size()>0)
            return match;
         _robot.delay(200);
      }
      if(_stopIfWaitingFailed)
         throw new FindFailed(img + " can't be found.");
      return match;
   }

   public <T> void waitNotExist(T img, int timeout) 
                                       throws IOException, AWTException, FindFailed{
      long begin_t = (new Date()).getTime();
      while( begin_t + timeout > (new Date()).getTime() ){
         Matches match = find_without_wait(img);
         if(match.size()==0)
            return;
         _robot.delay(200);
      }
      if(_stopIfWaitingFailed)
         throw new FindFailed(img + " can't be found.");
      return;
   }
   
   /**
    *  int click(ImageURL img, int modifiers, boolean confirm?)
    */
   public <T> int click(T img, int modifiers) throws IOException, AWTException, FindFailed{
      return _click(img, InputEvent.BUTTON1_MASK, modifiers, 1, false);
   }


   /**
    *  int click(Match matches, int modifiers, boolean confirm?)
    */
   public int click(Match match, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(match, InputEvent.BUTTON1_MASK, modifiers, 1, false);
   }

   /**
    *  int click(Matches matches, int modifiers, boolean confirm?)
    */
   public int click(Matches matches, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(matches, InputEvent.BUTTON1_MASK, modifiers, 1, false);
   }

   /**
    *  int clickAll(ImageURL img, int modifiers, boolean confirm?)
    */
   public <T> int clickAll(T img, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(img, InputEvent.BUTTON1_MASK, modifiers, -1, false);
   }

   /**
    *  int clickAll(Matches matches, int modifiers, boolean confirm?)
    */
   public int clickAll(Matches matches, int modifiers) 
                                          throws IOException, AWTException, FindFailed{
      return _click(matches, InputEvent.BUTTON1_MASK, modifiers, -1, false);
   }



   /**
    *  int repeatClickAll(ImageURL img, int modifiers, boolean confirm?)
    */
   public <T> int repeatClickAll(T img, int modifiers) 
                                          throws IOException, AWTException, FindFailed{
      Matches matches = find(img);
      int ret = 0;
      while(matches.size()> 0){
         ret += clickAll(matches, modifiers);
         matches = find(img);
      }
      return ret;
   }

   /**
    *  int doubleClick(ImageURL img, int modifiers, boolean confirm?)
    */
   public <T> int doubleClick(T img, int modifiers) 
                                     throws IOException, AWTException, FindFailed{
      return _click(img, InputEvent.BUTTON1_MASK, modifiers, 1, true);
   }

   public int doubleClick(Match match , int modifiers) 
                                    throws IOException, AWTException, FindFailed{
      return _click(match, InputEvent.BUTTON1_MASK, modifiers, 1, true);
   }

   public int doubleClick(Matches matches , int modifiers) 
                                    throws IOException, AWTException, FindFailed{
      return _click(matches, InputEvent.BUTTON1_MASK, modifiers, 1, true);
   }


   public <T> int doubleClickAll(T img, int modifiers) 
                                          throws IOException, AWTException, FindFailed{
      return _click(img, InputEvent.BUTTON1_MASK, modifiers, -1, true);
   }

   public int doubleClickAll(Matches matches , int modifiers) 
                                          throws IOException, AWTException, FindFailed{
      return _click(matches, InputEvent.BUTTON1_MASK, modifiers, -1, true);
   }

   public <T> int repeatDoubleClickAll(T img, int modifiers) 
                                          throws IOException, AWTException, FindFailed{
      Matches matches = find(img);
      int ret = 0;
      while(matches.size() > 0){
         ret += doubleClickAll(matches, modifiers);
         matches = find(img);
      }
      return ret;
   }


   /**
    *  int rightClick(ImageURL img, int modifiers, boolean confirm?)
    */
   public <T> int rightClick(T img, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(img, InputEvent.BUTTON3_MASK, modifiers, 1, false);
   }

   public int rightClick(Match match, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(match, InputEvent.BUTTON3_MASK, modifiers, 1, false);
   }

   public int rightClick(Matches matches, int modifiers) 
                                       throws IOException, AWTException, FindFailed{
      return _click(matches, InputEvent.BUTTON3_MASK, modifiers, 1, false);
   }

   public <T> int dragDrop( T src, T dest) 
                                       throws IOException, AWTException, FindFailed{
      Match match_src = find1(src);
      Match match_dst = find1(dest);
      Debug.log("dragDrop: " + match_src + ", " + match_dst);
      if( match_src != null && match_dst != null ){
         int src_x = _getCenterX(match_src);
         int src_y = _getCenterY(match_src);
         int dst_x = _getCenterX(match_dst);
         int dst_y = _getCenterY(match_dst);

         _dragDrop(src_x, src_y, dst_x, dst_y);
         return 1;
      }
      return 0;
   }

   public <T> int dragDrop( T src, int x, int y) 
                                 throws IOException, AWTException, FindFailed{
      Match match_src = find1(src);
      return dragDrop( match_src, x, y);
   }

   public int dragDrop( Match match_src, Match match_dest) 
	                             throws IOException, AWTException, FindFailed{
      if( match_src == null || match_dest == null )
         return 0;
      return dragDrop(match_src, 
                      _getCenterX(match_dest), _getCenterY(match_dest));
   }
	
   public int dragDrop( Match match_src, int x, int y) 
                                 throws IOException, AWTException, FindFailed{
      if( match_src != null ){
         int src_x = _getCenterX(match_src);
         int src_y = _getCenterY(match_src);

         _dragDrop(src_x, src_y, x, y);
         return 1;
      }
      return 0;
   }

   public <T> int paste( T img, String text) throws IOException, AWTException, FindFailed{
      Match match = null;
      if( img != null )
         match = find1(img);
      return paste(match, text);
   }

   public int paste( Match match, String text) throws IOException, AWTException, FindFailed{
      if( match != null ){
         int x = _getCenterX(match);
         int y = _getCenterY(match);
         _robot.mouseMove(x, y);
         _robot.mousePress(InputEvent.BUTTON1_MASK);
         _robot.mouseRelease(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
      }
      if( text != null ){
         Debug.log(1, "paste: " + text);
         Clipboard.putText(Clipboard.PLAIN, Clipboard.UTF8, 
                           Clipboard.BYTE_BUFFER, text);
         int mod = getOSHotkeyModifier();
         _robot.keyPress(mod);
         _robot.keyPress(KeyEvent.VK_V);
         _robot.keyRelease(KeyEvent.VK_V);
         _robot.keyRelease(mod);
         return 1;
      }
      return 0;
   }

   /*
   private Transferable getUTF8TransferStr(final String text){
      Transferable trans = new Transferable(){ 
         String _mimeType = "text/html; charset=UTF-8";
         public DataFlavor[] getTransferDataFlavors() { 
            try{
               return new DataFlavor[] { new DataFlavor(_mimeType) }; 
            }
            catch( ClassNotFoundException e){
               e.printStackTrace();
            }
            return null;
         } 
         public boolean isDataFlavorSupported(DataFlavor flavor) { 
            return flavor.getMimeType().equals(_mimeType);
         } 
         public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException { 
            if(isDataFlavorSupported(flavor)) 
               return text; 
            throw new UnsupportedFlavorException(flavor); 
         } 
      }; 
      return trans;
   }
   */

   public <T> int type( T img, String text) throws IOException, AWTException, FindFailed{
      return type(img, text, 0);
   }

   public <T> int type( T img, String text, int modifiers) throws IOException, AWTException, FindFailed{
      Match match = null;
      if( img != null )
         match = find1(img);
      return type(match, text, modifiers);
   }
   
   public int type( Match match, String text) throws IOException, AWTException{
      return type(match, text, 0);
   }

   static String SHIFT_CHARS = "~!@#$%^&*()_+|}{\":?><";
   private boolean needsShift(char ch){
      if(Character.isUpperCase(ch)) return true;
      if( SHIFT_CHARS.indexOf(ch) >= 0 )  return true;
      return false;
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


   public int type( Match match, String text, int modifiers) throws IOException, AWTException{
      Debug.log("type: '" + text + "' +" + modifiers);
      if( match != null ){
         int x = _getCenterX(match);
         int y = _getCenterY(match);
         _robot.mouseMove(x, y);
         _robot.mousePress(InputEvent.BUTTON1_MASK);
         _robot.mouseRelease(InputEvent.BUTTON1_MASK);
         _robot.waitForIdle();
      }
      if( text != null ){
/*
         Charset charSet = Charset.forName("US-ASCII");
         CharsetEncoder encoder = charSet.newEncoder();
         ByteBuffer bb = encoder.encode(CharBuffer.wrap(text.toUpperCase()));
*/
         for(int i=0; i < text.length(); i++){
            //boolean shift = needsShift(text.charAt(i));
            pressModifiers(modifiers);
            type_ch(text.charAt(i)); 
            /*
            if(shift) _robot.keyPress(KeyEvent.VK_SHIFT);
            _robot.keyPress(bb.get(i));
            _robot.keyRelease(bb.get(i));
            if(shift) _robot.keyRelease(KeyEvent.VK_SHIFT);
            */
            releaseModifiers(modifiers);
            _robot.delay(20);
         }
         return 1;
      }
      return 0;
   }

   public void popup(String message){
      JOptionPane.showMessageDialog(null, message, 
                                    "Sikuli", JOptionPane.PLAIN_MESSAGE);
   }

   public <T> int hover(T pat) throws IOException, AWTException, FindFailed{
      Match m = find1(pat);
      if( m != null){
         int x = _getCenterX(m);
         int y = _getCenterY(m);
         _robot.mouseMove(x, y);
         return 0;
      }
      return -1;
   }

   ////// Low level actions

   public void mouseMove(int x, int y) {
      _robot.mouseMove(x,y);
   }

   
   public <T> Matches _find(T pat, String region) throws IOException, AWTException, FindFailed{
      if( pat instanceof Pattern ){
         Pattern p = (Pattern)pat;
         return _find(p.imgURL, region, p.similarity, p.numMatches);
      }
      else
         return _find((String)pat, region, MATCHING_THRESHOLD);
   }

   ///////////////////////////////////////////////////////////////////////

   Matches _find(String img, String region, float similarity) throws IOException, AWTException, FindFailed{
      return _find(img, region, similarity, DEFAULT_NUM_MATCHES);
   }

   Matches _find(String img, String region, float similarity, int numMatches) throws IOException, AWTException, FindFailed{
      Matches matches;
      if( _bundlePath != null ){
         File f = new File(img);
         if(!f.exists() || !f.isAbsolute())
            img = _bundlePath + File.separator + img;
      }
         
      Debug.log(2, "find: %s in %s, t=%f, n=%d", img, region, similarity, numMatches); 
      if( img.endsWith(".png") || img.endsWith(".jpg") )
         matches = new Matches(
             _imgMatcher.screenMatch(img, region, similarity, numMatches),
             this);
      else
         matches = new Matches(
             _imgMatcher.screenMatchByOCR(img, region, similarity, numMatches),
             this);
      int n;
      Match cutPoint = null;
      for(Match m : matches)
         if( m.score >= similarity ){
            Debug.log(7, "%f", m.score );
         }
         else{
            cutPoint = m;
            break;
         }
      if(cutPoint != null){
         while( matches.getLast() != cutPoint )
            matches.removeLast();
         matches.removeLast();
      }
      System.out.println( matches.size() + " matches found" );
      return matches;
   }

   
   private int _getKeyCodeFromChar(char ch){
      char u_ch = Character.toUpperCase(ch);
      String keyname = "X";
      if( (u_ch >= 'A' && u_ch <= 'Z') || (u_ch >= '0' && u_ch <= '9') )
         keyname = new Character(u_ch).toString();
      else{
         if( u_ch == '.' )
            keyname = "PERIOD";
      }
         
      return AWTKeyStroke.getAWTKeyStroke("pressed " + keyname).getKeyCode();
   }


   private <T> Match find1(T img) throws IOException, AWTException, FindFailed{
      Matches matches = find(img);
      if( matches.size() == 0 )
         return null;
      return matches.getFirst();
   }

   private void _dragDrop(int src_x, int src_y, int dst_x, int dst_y) 
                                                   throws AWTException, FindFailed{
      _robot.mouseMove(src_x, src_y);
      showDragDrop(src_x, src_y, dst_x, dst_y);
      _robot.mousePress(InputEvent.BUTTON1_MASK);
      _robot.delay(1500);
      _robot.mouseMove(dst_x, dst_y);
      _robot.delay(1000);
      _robot.mouseRelease(InputEvent.BUTTON1_MASK);
   }

   private int _getCenterX(Match m){
      return m.x + m.w/2;
   }

   private int _getCenterY(Match m){
      return m.y + m.h/2;
   }

   private <T> int _click(T img, int buttons, int modifiers, 
                      int numClick, boolean dblClick) 
                                             throws IOException, AWTException, FindFailed{
      Matches matches = find(img);
      if(matches!=null)
         return _click(matches, buttons, modifiers, numClick, dblClick);
      return -1;
   }

   private int _click(Match match, int buttons, int modifiers, 
                      int numClick, boolean dblClick) 
                                             throws IOException, AWTException{
      Match[] matches = new Match[1];
      matches[0] = match;
      return _click(new Matches(matches), buttons, modifiers, numClick, dblClick);
   }

   private void releaseModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyRelease(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0) _robot.keyRelease(KeyEvent.VK_META);
   }

   private void pressModifiers(int modifiers){
      if((modifiers & K_SHIFT) != 0) _robot.keyPress(KeyEvent.VK_SHIFT);
      if((modifiers & K_CTRL) != 0) _robot.keyPress(KeyEvent.VK_CONTROL);
      if((modifiers & K_ALT) != 0) _robot.keyPress(KeyEvent.VK_ALT);
      if((modifiers & K_META) != 0) _robot.keyPress(KeyEvent.VK_META);
   }

   private int _click(Matches matches, int buttons, int modifiers, 
                      int numClick, boolean dblClick) 
                                             throws IOException, AWTException{
      if( numClick < 0 )
         numClick = matches.size();
      else
         numClick = matches.size()<numClick? matches.size() : numClick;

      Debug.log("click " + numClick + " times");
      int count = 0;
      for(Match m : matches){
         if( count++ >= numClick )
            break;
         int x = _getCenterX(m);
         int y = _getCenterY(m);
         Debug.log("click on (" + x + "," + y + 
                   ") BTN: " + buttons + ", MOD: " + modifiers); 
         pressModifiers(modifiers);
         _robot.mouseMove(x, y);
         showClick(m.x, m.y, m.w, m.h);
         _robot.mousePress(buttons);
         _robot.mouseRelease(buttons);
         if( dblClick ){
            _robot.mousePress(buttons);
            _robot.mouseRelease(buttons);
         }
         releaseModifiers(modifiers);
         _robot.delay(500);
      }
      return numClick;
   }

   private void showClick(int x, int y, int w, int h){
      if(_showActions){
         _overlay.showTarget(x,y,w,h);
      }
   }

   private void showDragDrop(int x, int y, int x2, int y2){
      if(_showActions){
         _overlay.showDragDrop(x,y, x2, y2);
      }
   }
	
   private int getOSHotkeyModifier(){
      String OS = System.getProperty("os.name").toLowerCase();
      if(OS.equals("mac os x"))
         return KeyEvent.VK_META;
      else
         return KeyEvent.VK_CONTROL;
   }
	
   public String run(String cmdline){
      String lines="";

      try {
         String line;
         Process p = Runtime.getRuntime().exec(cmdline);
         BufferedReader input = 
            new BufferedReader
            (new InputStreamReader(p.getInputStream()));
         while ((line = input.readLine()) != null) {
            lines = lines + '\n' + line;
         }
      } 
      catch (Exception err) {
         err.printStackTrace();
      }
      return lines;

   }

   public static void main(String[] args){
      if(args.length == 0 ){
         System.out.println("Usage: sikuli-script [file.sikuli]");
         return;
      }
      try{
         ScriptRunner runner = new ScriptRunner();
         runner.runPython(args[0]);
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

}

