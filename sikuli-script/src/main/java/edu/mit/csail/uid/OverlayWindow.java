package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;


class OverlayWindow extends JWindow implements MouseListener {
   enum VizMode { ONE_TARGET, DRAG_DROP };

   static Rectangle fullscreenRect = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static GraphicsDevice _gdev;
   static int MARGIN = 20;


   VizMode _mode = null;
   BufferedImage _screen = null;
   BufferedImage _darker_screen = null;
   int srcx, srcy, destx, desty;

   BasicStroke _StrokeCross = new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float [] { 2f }, 0);


   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseClicked(MouseEvent e) {
      dispose(); 
   }

   private void captureScreen(int x, int y, int w, int h) throws AWTException{
      Robot _robot = new Robot();
      Rectangle rect = new Rectangle(x, y, w, h);
      _screen = _robot.createScreenCapture(rect);

      float scaleFactor = .6f;
      RescaleOp op = new RescaleOp(scaleFactor, 0, null);
      _darker_screen = op.filter(_screen, null);

   }

   private void drawCircle(int x, int y, int radius, Graphics g){
      g.drawOval(x - radius, y - radius, radius*2, radius*2);
   }

   private void drawDragDrop(Graphics2D g2d){

      g2d.setColor(Color.white);
      g2d.setStroke(_StrokeCross);
      g2d.drawLine(srcx, srcy, destx, desty);
      drawCircle(srcx, srcy, 5, g2d);

   }

   private void drawTarget(Graphics2D g2d){
      int cx = (srcx+destx)/2;
      int cy = (srcy+desty)/2;

      g2d.setColor(Color.white);
      g2d.setStroke(_StrokeCross);
      g2d.drawLine(cx, srcy, cx, desty);
      g2d.drawLine(srcx, cy, destx, cy);

      drawCircle( cx, cy, 5, g2d);
   }

   public void paint(Graphics g)
   {
      if( _screen != null ){
         Graphics2D g2d = (Graphics2D)g;

         g2d.drawImage(_darker_screen,0,0,this);
         switch(_mode){
            case ONE_TARGET: drawTarget(g2d); break;
            case DRAG_DROP:  drawDragDrop(g2d); break;
         }
         setVisible(true);
      }
      else{
         setVisible(false);
      }
   }

   void init(){
      addMouseListener(this);
   }

   private void close(){
      //_gdev.setFullScreenWindow(null);
      this.setVisible(false);
   }


   public void showDragDrop(int _srcx, int _srcy, int _destx, int _desty){
      _mode = VizMode.DRAG_DROP;
      int x1 = (_srcx < _destx) ? _srcx : _destx;
      int y1 = (_srcy < _desty) ? _srcy : _desty;
      int x2 = (_srcx > _destx) ? _srcx : _destx;
      int y2 = (_srcy > _desty) ? _srcy : _desty;
      srcx = _srcx-x1+MARGIN;     srcy = _srcy-y1+MARGIN;
      destx = _destx-x1+MARGIN;   desty = _desty-y1+MARGIN;
      showWindow(x1-MARGIN, y1-MARGIN, x2-x1+2*MARGIN, y2-y1+2*MARGIN);
   }

   public void showTarget(int x, int y, int w, int h){
      _mode = VizMode.ONE_TARGET;
      Debug.log(1, "showTarget " + x + " " + y + " " + w + " " + h);

      srcx = 0; destx = w;
      srcy = 0; desty = h;
      showWindow(x, y, w, h);
   }

   private void showWindow(int x, int y, int w, int h){
      try{
         captureScreen(x, y, w, h);
      }
      catch(AWTException e){
         e.printStackTrace();
      }
      setLocation(x,y);
      setSize(w, h);
      setVisible(true);
      toFront();
      try{
         Thread.sleep(3000);
      }
      catch(InterruptedException e){
         close();
         e.printStackTrace();
      }
      close();
   }

   public OverlayWindow(){
      init();
      setVisible(false);
      _gdev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      setAlwaysOnTop(true);
   }

}


