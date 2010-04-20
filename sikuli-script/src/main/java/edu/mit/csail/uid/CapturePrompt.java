package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import javax.imageio.*;

class CapturePrompt extends JWindow implements Subject{
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   final static float MIN_DARKER_FACTOR = 0.6f;
   final static long MSG_DISPLAY_TIME = 2000;
   static GraphicsDevice _gdev = null;

   Observer _obs;

   Screen _scr;
   BufferedImage _scr_img = null;
   BufferedImage _darker_screen = null;
   float _darker_factor;
   Rectangle rectSelection;
   BasicStroke bs;
   int srcx, srcy, destx, desty;
   boolean _canceled = false;
   long _msg_start;
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
   private void drawSelection(Graphics2D g2d){
      if (srcx != destx || srcy != desty)
      {
         int x1 = (srcx < destx) ? srcx : destx;
         int y1 = (srcy < desty) ? srcy : desty;
         int x2 = (srcx > destx) ? srcx : destx;
         int y2 = (srcy > desty) ? srcy : desty;

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
      }
   }

   static Font fontMsg = new Font("Arial", Font.PLAIN, 40);
   void drawMessage(Graphics2D g2d){
      if(_msg == null)
         return;
      if(_msg_start == -1) _msg_start = (new Date()).getTime();
      long now = (new Date()).getTime();
      if(now - _msg_start <= MSG_DISPLAY_TIME){
         float alpha = 1f - (float)(now-_msg_start)/MSG_DISPLAY_TIME;
         g2d.setFont(fontMsg);
         g2d.setColor(new Color(1f,1f,1f,alpha));
         g2d.drawString(_msg, 0, 45);
         repaint();
      }


   }

   public void paint(Graphics g)
   {
      if( _scr_img != null ){
         Graphics2D g2d = (Graphics2D)g;

         /*
         Thread th =  new Thread() {
            public void run() {
               try{
                  Thread.sleep(1000);
                  while(_darker_factor>=MIN_DARKER_FACTOR){
                     _darker_factor-=0.1f;
                     CapturePrompt.this.repaint();
                     Thread.sleep(40);
                  }
               }
               catch(InterruptedException ie){
               }
            }
         };
         if(_darker_factor==1.0f)
            th.start();

         RescaleOp op = new RescaleOp(_darker_factor, 0, null);
         _darker_screen = op.filter(_scr_img, null);
         */
         g2d.drawImage(_darker_screen,0,0,this);
         drawMessage(g2d);
         drawSelection(g2d);
         setVisible(true);
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
      if(_gdev != null) 
         _gdev.setFullScreenWindow(null);
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
      this.setVisible(true);
      this.setAlwaysOnTop(true);
      _darker_factor = 1f;
      _msg = msg;
      _msg_start = -1;

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
