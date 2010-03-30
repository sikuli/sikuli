package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

class CapturePrompt extends JWindow implements Subject{
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static GraphicsDevice _gdev = null;

   Observer _obs;

   Screen _scr;
   BufferedImage _scr_img = null;
   BufferedImage _darker_screen = null;
   Rectangle rectSelection;
   BasicStroke bs;
   int srcx, srcy, destx, desty;

   BasicStroke _StrokeCross = new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float [] { 2f }, 0);

   public void addObserver(Observer o){
      _obs = o;
   }
   public void notifyObserver(){
      _obs.update(this);
   }

   private void captureScreen(Screen scr) {
      ScreenImage simg = scr.capture();
      /*
      try{
         System.out.println( "screen file: " + simg.getFilename() );
      }
      catch(IOException e){}
      */

      _scr_img = simg.getImage();
      float scaleFactor = .6f;
      RescaleOp op = new RescaleOp(scaleFactor, 0, null);
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

   public void paint(Graphics g)
   {
      if( _scr_img != null ){
         Graphics2D g2d = (Graphics2D)g;

         g2d.drawImage(_darker_screen,0,0,this);
         drawSelection(g2d);
         setVisible(true);
      }
      else
         setVisible(false);
   }

   void init(){
      getContentPane().setCursor(
            Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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
               close();
               return;  // don't notify the observers
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
      BufferedImage cropImg = cropSelection();
      rectSelection.x += _scr.x;
      rectSelection.y += _scr.y;
      ScreenImage ret = new ScreenImage(rectSelection, cropImg);
      return ret;
   }

   public void prompt(){
      Debug.log(3, "starting CapturePrompt @" + _scr);
      captureScreen(_scr);
      setLocation(_scr.x, _scr.y);
      this.setSize(new Dimension(_scr.w, _scr.h));
      this.setBounds(_scr.x, _scr.y, _scr.w, _scr.h);
      this.setVisible(true);
      this.setAlwaysOnTop(true);

      if( _scr.getID()==0 ){
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
      if(scr == null)
         scr = new Screen(); // FIXME: scr == null means all screens
      _scr = scr;
      init();
   }
   

}
