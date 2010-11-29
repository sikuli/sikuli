package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


class OverlayWindow extends JWindow implements MouseListener {
   enum VizMode { ONE_TARGET, DRAG_DROP };

   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static Color _transparentColor = new Color(0F,0F,0F,0.0F);
   static int MARGIN = 20;


   Screen _scr;
   VizMode _mode = null;
   BufferedImage _screen = null;
   BufferedImage _darker_screen = null;
   int srcx, srcy, destx, desty;
   Location _lastTarget;
   boolean _borderOnly = false;

   boolean _native_transparent = false;

   BasicStroke _StrokeCross = new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float [] { 2f }, 0);

   BasicStroke _StrokeBorder = new BasicStroke(3);

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

   private void closeAfter(float secs){
      try{
         Thread.sleep((int)secs*1000);
      }
      catch(InterruptedException e){
         close();
         e.printStackTrace();
      }
      close();
   }

   private void captureScreen(int x, int y, int w, int h) {
      ScreenImage img = _scr.capture(x, y, w, h);
      _screen = img.getImage();

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
         if(_borderOnly){
            if(!_native_transparent)
               g2d.drawImage(_screen, 0, 0, this);
            g2d.setColor(Color.red);
            g2d.setStroke(_StrokeBorder);
            int w = (int)_StrokeBorder.getLineWidth();
            g2d.drawRect(w/2, w/2, _screen.getWidth()-w, _screen.getHeight()-w);
         }
         else{
            if(!_native_transparent)
               g2d.drawImage(_darker_screen,0,0,this);
            switch(_mode){
               case ONE_TARGET: drawTarget(g2d); break;
               case DRAG_DROP:  drawDragDrop(g2d); break;
            }
         }
         setVisible(true);
      }
      else{
         setVisible(false);
      }
   }

   void init(){
      if(Env.getOS() == OS.MAC)
         _native_transparent = true;
      JPanel pan = new JPanel();
      //pan.setBorder(new LineBorder(Color.red));

      if(_native_transparent){
         pan.setBackground(_overlayColor);
         getRootPane().putClientProperty("Window.alpha", new Float(0.6f));
      }

      getContentPane().add(pan,"Center");
      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      addMouseListener(this);
   }

   public void close(){
      setVisible(false);
      dispose();
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

   public void showDropTarget(Location loc){
      showDragDrop(_lastTarget.x, _lastTarget.y, loc.x, loc.y);
   }

   public void highlight(Region r_){
      _borderOnly = true;
      Region r = new Region(r_);
      r.setROI(new Rectangle(r_.x-3, r_.y-3, r_.w+6, r_.h+6));
      captureScreen(r.x, r.y, r.w, r.h);
      setLocation(r.x,r.y);
      setSize(r.w, r.h);
      this.setBackground(_transparentColor);
      setVisible(true);
      toFront();
   }

   public void highlight(Region r_, float secs){
      highlight(r_);
      closeAfter(secs);
   }

   public void showTarget(Location loc){
      _mode = VizMode.ONE_TARGET;
      final int w = 50, h = 50;
      int x = loc.x-w/2, y = loc.y-w/2;
      _lastTarget = loc;

      Debug.log(1, "showTarget " + x + " " + y + " " + w + " " + h);
      srcx = 0; destx = w;
      srcy = 0; desty = h;
      showWindow(x, y, w, h);
   }

   private void showWindow(int x, int y, int w, int h){
      captureScreen(x, y, w, h);
      setLocation(x,y);
      setSize(w, h);
      setVisible(true);
      toFront();
      try{
         Thread.sleep((int)Settings.ShowActionDelay*1000);
      }
      catch(InterruptedException e){
         close();
         e.printStackTrace();
      }
      close();
   }

   public OverlayWindow(Screen scr){
      _scr = scr;
      init();
      setVisible(false);
      setAlwaysOnTop(true);
   }

   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         MacUtil.bringWindowToFront(this, true);
      }
      else
         super.toFront();
   }

}


