package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

class CapturePrompt extends JWindow implements Subject{
   static Rectangle fullscreenRect = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static GraphicsDevice _gdev;

   Observer _obs;

   BufferedImage _screen = null;
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

   private void captureScreen() throws AWTException{
      Robot _robot = new Robot();
      _screen = _robot.createScreenCapture(fullscreenRect);

      float scaleFactor = .6f;
      RescaleOp op = new RescaleOp(scaleFactor, 0, null);
      _darker_screen = op.filter(_screen, null);
   }

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

         g2d.setColor(Color.white);
         g2d.setStroke(bs);
         g2d.draw(rectSelection);

         int cx = (x1+x2)/2;
         int cy = (y1+y2)/2;
         g2d.setStroke(_StrokeCross);
         g2d.drawLine(cx, y1, cx, y2);
         g2d.drawLine(x1, cy, x2, cy);
      }
   }

   public void paint(Graphics g)
   {
      if( _screen != null ){
         Graphics2D g2d = (Graphics2D)g;

         g2d.drawImage(_darker_screen,0,0,this);
         drawSelection(g2d);
         setVisible(true);
      }
      else
         setVisible(false);
   }

   void init(){
      rectSelection = new Rectangle ();
      bs = new BasicStroke (3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0, new float [] { 12, 12 }, 0);
      addMouseListener(new MouseAdapter(){
         public void mousePressed(java.awt.event.MouseEvent e){
            if (_screen == null) return;
            destx = srcx = e.getX();
            desty = srcy = e.getY();
            repaint();
         }

         public void mouseReleased(java.awt.event.MouseEvent e){
            if (_screen == null) return;
            notifyObserver();
         }
      });

      addMouseMotionListener( new MouseMotionAdapter(){
         public void mouseDragged(java.awt.event.MouseEvent e) {
            if (_screen == null) return;
            destx = e.getX();
            desty = e.getY();
            repaint(); 
         }
      });
   }

   public void close(){
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
            _screen.getSubimage(rectSelection.x, rectSelection.y, w, h),
            null, 0, 0
         );
      }
      catch (RasterFormatException e) {
         e.printStackTrace();
      }
      crop_g2d.dispose();
      return crop;
   }

   public String getSelection(){
      BufferedImage cropImg = cropSelection();
      String filename = saveTmpImage(cropImg);
      return filename;
   }

   public CapturePrompt(Observer ob){
      addObserver(ob);
      init();
      try{
         captureScreen();
      }
      catch(AWTException e){
         e.printStackTrace();
      }
      _gdev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      if( _gdev.isFullScreenSupported() ){
         _gdev.setFullScreenWindow(this);
      }
      else{
         Debug.log("Fullscreen mode is not supported.");
      }
      setLocation(0,0);
   }
   
   private static String saveTmpImage(BufferedImage img){
      File tempFile;
      try{
         tempFile = File.createTempFile("sikuli-tmp", ".png" );
         tempFile.deleteOnExit();
         ImageIO.write(img, "png", tempFile);
         return tempFile.getAbsolutePath();
      }
      catch(IOException e){
         e.printStackTrace();
      }
      return null;
   }


}
