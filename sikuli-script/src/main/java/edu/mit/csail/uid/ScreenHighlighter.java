package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


class ScreenHighlighter extends JWindow implements MouseListener {
   enum VizMode { ONE_TARGET, DRAG_DROP };

   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static Color _transparentColor = new Color(0F,0F,0F,0.0F);
   final static int TARGET_SIZE = 50;
   static int MARGIN = 20;


   Screen _scr;
   VizMode _mode = null;
   BufferedImage _screen = null;
   BufferedImage _darker_screen = null;
   int srcx, srcy, destx, desty;
   Location _lastTarget;
   boolean _borderOnly = false;

   boolean _native_transparent = false;
   Animator _anim;

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

      /*
      g2d.setColor(Color.white);
      g2d.setStroke(_StrokeCross);
      g2d.drawLine(cx, srcy, cx, desty);
      g2d.drawLine(srcx, cy, destx, cy);
      */

      if(_anim.running()){
         g2d.setColor(Color.red);
         g2d.setStroke(_StrokeBorder);
         drawCircle( cx, cy, (int)_anim.step(), g2d);
         repaint();
      }
   }

   private void drawBorder(Graphics2D g2d){
      g2d.setColor(Color.red);
      g2d.setStroke(_StrokeBorder);
      int w = (int)_StrokeBorder.getLineWidth();
      g2d.drawRect(w/2, w/2, _screen.getWidth()-w, _screen.getHeight()-w);
   }

   public boolean isDoubleBuffered() {
      return false;
   }

   BufferedImage _buf = null;
   public void paint(Graphics g)
   {
      if( _screen != null ){
         Graphics2D bfG2 = (Graphics2D)g;
         bfG2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
         bfG2.fillRect(0,0,getWidth(),getHeight());
         bfG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         /*
         if ( _buf==null || _buf.getWidth(this) != getWidth() ||
              _buf.getHeight(this) != getHeight() ) {
            _buf = new BufferedImage( 
                  getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
         }
         Graphics2D bfG2 = _buf.createGraphics();
         bfG2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         bfG2.setBackground(_transparentColor);
         bfG2.setColor(_transparentColor);
         bfG2.fillRect(0, 0, _buf.getWidth(), _buf.getHeight()); 
         */
         bfG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
         if(_borderOnly){
            if(!_native_transparent)
               bfG2.drawImage(_screen, 0, 0, this);
            drawBorder(bfG2);
         }
         else{
            if(!_native_transparent)
               bfG2.drawImage(_darker_screen,0,0,this);
            switch(_mode){
               case ONE_TARGET: drawTarget(bfG2); break;
               case DRAG_DROP:  drawDragDrop(bfG2); break;
            }
         }
         /*
         Graphics2D g2d = (Graphics2D)g;
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
         g2d.fillRect(0,0,getWidth(),getHeight());
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         g2d.drawImage(_buf, 0, 0, this);
         */
         if(!isVisible())
            setVisible(true);
      }
      else{
         if(isVisible())
            setVisible(false);
      }
   }

   void init(){
      if(Env.getOS() == OS.MAC)
         _native_transparent = true;

      if(_native_transparent){
         this.setBackground(_transparentColor);
         getRootPane().putClientProperty("Window.alpha", new Float(0.8f));
      }

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      addMouseListener(this);
   }

   public void close(){
      setVisible(false);
      dispose();
   }


   public void showDragDrop(int _srcx, int _srcy, int _destx, int _desty, float secs){
      _mode = VizMode.DRAG_DROP;
      int x1 = (_srcx < _destx) ? _srcx : _destx;
      int y1 = (_srcy < _desty) ? _srcy : _desty;
      int x2 = (_srcx > _destx) ? _srcx : _destx;
      int y2 = (_srcy > _desty) ? _srcy : _desty;
      srcx = _srcx-x1+MARGIN;     srcy = _srcy-y1+MARGIN;
      destx = _destx-x1+MARGIN;   desty = _desty-y1+MARGIN;
      showWindow(x1-MARGIN, y1-MARGIN, x2-x1+2*MARGIN, y2-y1+2*MARGIN, secs);
   }

   public void showDropTarget(Location loc, float secs){
      showDragDrop(_lastTarget.x, _lastTarget.y, loc.x, loc.y, secs);
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

   public void showTarget(Location loc, float secs){
      _mode = VizMode.ONE_TARGET;
      final int w = TARGET_SIZE, h = TARGET_SIZE;
      int x = loc.x-w/2, y = loc.y-w/2;
      _lastTarget = loc;

      Debug.log(1, "showTarget " + x + " " + y + " " + w + " " + h);
      srcx = 0; destx = w;
      srcy = 0; desty = h;
      _anim = new Animator(TARGET_SIZE/2, 0, (long)(secs*1000));
      showWindow(x, y, w, h, secs);
   }

   private void showWindow(int x, int y, int w, int h, float secs){
      captureScreen(x, y, w, h);
      setLocation(x,y);
      setSize(w, h);
      setVisible(true);
      toFront();
      closeAfter(secs);
   }

   public ScreenHighlighter(Screen scr){
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


