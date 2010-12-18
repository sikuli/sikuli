package org.sikuli.script;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import javax.imageio.*;

class CapturePrompt extends TransparentWindow implements Subject{
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   final static float MIN_DARKER_FACTOR = 0.6f;
   final static long MSG_DISPLAY_TIME = 2000;
   final static long WIN_FADE_IN_TIME = 200;

   static GraphicsDevice _gdev = null;

   Observer _obs;

   Screen _scr;
   BufferedImage _scr_img = null;
   BufferedImage _darker_screen = null;
   float _darker_factor;
   Rectangle rectSelection;
   BasicStroke bs;
   int srcScreenId=0;
   int srcx, srcy, destx, desty;
   boolean _canceled = false;
   Animator _aniMsg, _aniWin;
   String _msg;

   BasicStroke _StrokeCross = new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float [] { 2f }, 0);

   public void addObserver(Observer o){
      _obs = o;
   }
   public void notifyObserver(){
      _obs.update(this);
   }

   private void captureScreen(Screen scr) {
      ScreenImage simg = scr.capture();
      _scr_img = simg.getImage();

      _darker_factor = 0.6f;
      RescaleOp op = new RescaleOp(_darker_factor, 0, null);
      _darker_screen = op.filter(_scr_img, null);

   }

   private Color selFrameColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
   private Color selCrossColor = new Color(1.0f, 0.0f, 0.0f, 0.6f);
   private Color screenFrameColor = new Color(1.0f, 0.0f, 0.0f, 0.6f);
   BasicStroke strokeScreenFrame = new BasicStroke(5);


   private void drawScreenFrame(Graphics2D g2d, int scrId){
      Rectangle rect = Screen.getBounds(scrId);
      Rectangle ubound = (new UnionScreen()).getBounds();
      g2d.setColor(screenFrameColor);
      g2d.setStroke(strokeScreenFrame);
      rect.x -= ubound.x;
      rect.y -= ubound.y;
      int sw = (int)(strokeScreenFrame.getLineWidth()/2);
      rect.x += sw;
      rect.y += sw;
      rect.width -= sw*2; 
      rect.height -= sw*2;
      g2d.draw(rect);
   }

   private void drawSelection(Graphics2D g2d){
      if (srcx != destx || srcy != desty)
      {
         int x1 = (srcx < destx) ? srcx : destx;
         int y1 = (srcy < desty) ? srcy : desty;
         int x2 = (srcx > destx) ? srcx : destx;
         int y2 = (srcy > desty) ? srcy : desty;

         if(Screen.getNumberScreens()>1){
            Rectangle selRect = new Rectangle(x1,y1,x2-x1,y2-y1);
            Rectangle ubound = (new UnionScreen()).getBounds();
            selRect.x += ubound.x;
            selRect.y += ubound.y;
            Rectangle inBound = selRect.intersection(Screen.getBounds(srcScreenId));
            x1 = inBound.x - ubound.x;
            y1 = inBound.y - ubound.y;
            x2 = x1 + inBound.width-1;
            y2 = y1 + inBound.height-1;
         }

         rectSelection.x = x1;
         rectSelection.y = y1;
         rectSelection.width = (x2-x1)+1;
         rectSelection.height = (y2-y1)+1;

         if(rectSelection.width>0 && rectSelection.height>0)
            g2d.drawImage(_scr_img.getSubimage(x1, y1,x2-x1+1, y2-y1+1),
                          null, x1, y1);

         g2d.setColor(selFrameColor);
         g2d.setStroke(bs);
         g2d.draw(rectSelection);

         int cx = (x1+x2)/2;
         int cy = (y1+y2)/2;
         g2d.setColor(selCrossColor);
         g2d.setStroke(_StrokeCross);
         g2d.drawLine(cx, y1, cx, y2);
         g2d.drawLine(x1, cy, x2, cy);

         if(Screen.getNumberScreens()>1)
            drawScreenFrame(g2d, srcScreenId);
      }
   }

   static Font fontMsg = new Font("Arial", Font.PLAIN, 60);
   void drawMessage(Graphics2D g2d){
      if(_msg == null)
         return;
      if(_aniMsg.running()){
         float alpha = _aniMsg.step();
         g2d.setFont(fontMsg);
         g2d.setColor(new Color(1f,1f,1f,alpha));
         int sw = g2d.getFontMetrics().stringWidth(_msg);
         int sh = g2d.getFontMetrics().getMaxAscent();
         Rectangle ubound = (new UnionScreen()).getBounds();
         for(int i=0;i<Screen.getNumberScreens();i++){
            Rectangle bound = Screen.getBounds(i);
            int cx = bound.x+ (bound.width-sw)/2 - ubound.x;
            int cy = bound.y+ (bound.height-sh)/2 - ubound.y;
            g2d.drawString(_msg, cx, cy);
         }
         repaint();
      }


   }

   BufferedImage bi = null;
   public void paint(Graphics g)
   {
      if( _scr_img != null ){
         Graphics2D g2dWin = (Graphics2D)g;

         if ( bi==null || bi.getWidth(this) != getWidth() ||
              bi.getHeight(this) != getHeight() ) {
            bi = new BufferedImage( 
                  getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
         }
         Graphics2D bfG2 = bi.createGraphics();
         bfG2.drawImage(_darker_screen,0,0,this);
         drawMessage(bfG2);
         drawSelection(bfG2);
         g2dWin.drawImage(bi, 0, 0, this);
         setVisible(true);
         if(_aniWin!=null && _aniWin.running()){
            float a = _aniWin.step();
            setOpacity(a);
            repaint();
         }
      }
      else
         setVisible(false);
   }

   void init(){
      _canceled = false;
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      rectSelection = new Rectangle ();
      bs = new BasicStroke(1);
      addMouseListener(new MouseAdapter(){
         public void mousePressed(java.awt.event.MouseEvent e){
            if (_scr_img == null) return;
            destx = srcx = e.getX();
            desty = srcy = e.getY();
            srcScreenId = (new UnionScreen()).getIdFromPoint(srcx, srcy);
            Debug.log(3, "pressed " + srcx + "," + srcy + " at screen " + srcScreenId);

            repaint();
         }

         public void mouseReleased(java.awt.event.MouseEvent e){
            if (_scr_img == null) return;
            if( e.getButton() == java.awt.event.MouseEvent.BUTTON3 ){
               _canceled = true;
               close();
            }
            notifyObserver();
         }
      });

      addMouseMotionListener( new MouseMotionAdapter(){
         public void mouseDragged(java.awt.event.MouseEvent e) {
            if (_scr_img == null) return;
            destx = e.getX();
            desty = e.getY();
            repaint(); 
         }
      });
   }

   public void close(){
      if(_gdev != null ){
         try{
            _gdev.setFullScreenWindow(null);
         }
         catch(Exception e){
            Debug.log("Switch to windowed mode failed: " + e.getMessage());
         }
      }
      this.setVisible(false);
      this.dispose();
   }



   private BufferedImage cropSelection(){
      int w = rectSelection.width, h = rectSelection.height;
      if(w<=0 || h<=0)
         return null;
      BufferedImage crop  = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics2D crop_g2d = crop.createGraphics();
      try {
         crop_g2d.drawImage(
            _scr_img.getSubimage(rectSelection.x, rectSelection.y, w, h),
            null, 0, 0
         );
      }
      catch (RasterFormatException e) {
         e.printStackTrace();
      }
      crop_g2d.dispose();
      /*
      try{
         ImageIO.write(crop, "png", new File("debug_crop.png"));
      }
      catch(IOException e){}
      */
      return crop;
   }

   public ScreenImage getSelection(){
      if(_canceled)
         return null;
      BufferedImage cropImg = cropSelection();
      if(cropImg == null)
         return null;
      rectSelection.x += _scr.x;
      rectSelection.y += _scr.y;
      ScreenImage ret = new ScreenImage(rectSelection, cropImg);
      return ret;
   }

   public void prompt(String msg, int delayMS){
      try{
         Thread.sleep(delayMS);
      }
      catch(InterruptedException ie){
      }
      prompt(msg);

   }

   public void prompt(int delayMS){
      prompt(null, delayMS);
   }

   public void prompt(){
      prompt(null);
   }

   public void prompt(String msg){
      Debug.log(3, "starting CapturePrompt @" + _scr);
      captureScreen(_scr);
      setLocation(_scr.x, _scr.y);
      this.setSize(new Dimension(_scr.w, _scr.h));
      this.setBounds(_scr.x, _scr.y, _scr.w, _scr.h);
      this.setAlwaysOnTop(true);
      _msg = msg;
      _aniMsg = new LinearAnimator(1f, 0f, MSG_DISPLAY_TIME);

      if(Env.getOS() == OS.MAC || Env.getOS() == OS.WINDOWS){
         _aniWin = new LinearAnimator(0f, 1f, WIN_FADE_IN_TIME);
         setOpacity(0);
         getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
         this.setVisible(true);
         if(Env.getOS() == OS.MAC){
            Env.getOSUtil().bringWindowToFront(this, false);
         }
      }
      else
         this.setVisible(true);
      if( _scr.useFullscreen() ){
         _gdev = _scr.getGraphicsDevice();
         if( _gdev.isFullScreenSupported() ){
            _gdev.setFullScreenWindow(this);
         }
         else{
            Debug.log("Fullscreen mode is not supported.");
         }
      }
   }


   public CapturePrompt(Screen scr, Observer ob){
      this(scr);
      addObserver(ob);
   }

   public CapturePrompt(Screen scr){
      if(scr == null){
         if(Screen.getNumberScreens()>1)
            scr = new UnionScreen(); 
         else
            scr = new Screen();
      }
      _scr = scr;
      init();
   }
   

}
