package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Set;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.border.*;


public class ScreenHighlighter extends TransparentWindow implements MouseListener {
   enum VizMode { ONE_TARGET, DRAG_DROP };

   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static Color _transparentColor = new Color(0F,0F,0F,0.0F);
   static Color _targetColor = new Color(1F,0F,0F,0.7F);
   final static int TARGET_SIZE = 80;
   final static int DRAGGING_TIME = 200;
   static int MARGIN = 20;


   static Set<ScreenHighlighter> _opened = new HashSet<ScreenHighlighter>();
   Screen _scr;
   VizMode _mode = null;
   BufferedImage _screen = null;
   BufferedImage _darker_screen = null;
   int srcx, srcy, destx, desty;
   Location _lastTarget;
   boolean _borderOnly = false;

   boolean _native_transparent = false;
   boolean _double_buffered = false;
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

      if(_aniX.running()){
         g2d.setColor(_targetColor);
         g2d.setStroke(_StrokeBorder);
         int x = (int)_aniX.step(), y = (int)_aniY.step();
         g2d.drawLine(srcx, srcy, x, y);
         drawCircle(srcx, srcy, 5, g2d);
         repaint();
      }

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
         g2d.setColor(_targetColor);
         g2d.setStroke(_StrokeBorder);
         int size = (int)_anim.step();
         int size2 = size==0?0 : size - 5;
         drawCircle( cx, cy, size, g2d);
         drawCircle( cx, cy, size2, g2d);
         repaint();
      }
   }

   private void drawBorder(Graphics2D g2d){
      g2d.setColor(_targetColor);
      g2d.setStroke(_StrokeBorder);
      int w = (int)_StrokeBorder.getLineWidth();
      g2d.drawRect(w/2, w/2, getWidth()-w, getHeight()-w);
   }

   BufferedImage bi = null;
   public void paint(Graphics g)
   {
      if( _native_transparent || _screen != null ){
         if ( bi==null || bi.getWidth(this) != getWidth() ||
              bi.getHeight(this) != getHeight() ) {
            bi = new BufferedImage( 
                  getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
         }
         Graphics2D bfG2 = bi.createGraphics();
         Graphics2D g2d;
         if(_double_buffered)
            g2d = bfG2;
         else
            g2d = (Graphics2D)g;
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
         g2d.fillRect(0,0,getWidth(),getHeight());
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
         if(_borderOnly){
            if(!_native_transparent)
               g2d.drawImage(_screen, 0, 0, this);
            drawBorder(g2d);
         }
         else{
            if(!_native_transparent)
               g2d.drawImage(_screen,0,0,this);
            switch(_mode){
               case ONE_TARGET: drawTarget(g2d); break;
               case DRAG_DROP:  drawDragDrop(g2d); break;
            }
         }
         if(_double_buffered)
            ((Graphics2D)g).drawImage(bi, 0, 0, this);
         if(!isVisible())
            setVisible(true);
      }
      else{
         if(isVisible())
            setVisible(false);
      }
   }

   void init(){
      _opened.add(this);
      if(Env.getOS() == OS.MAC) 
         _native_transparent = true;
      if(Env.getOS() == OS.WINDOWS){
//         _double_buffered = true;
         _native_transparent = true;
         Env.getOSUtil().setWindowOpaque(this, false);
      }
      if(Env.getOS() == OS.LINUX){
         //_native_transparent = true;
         _double_buffered = true;
         //Env.getOSUtil().setWindowOpaque(this, false);
      }

      if(_native_transparent){
         this.setBackground(_transparentColor);
         //setOpacity(0.8f);
      }

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);
      addMouseListener(this);
   }

   public void close(){
      setVisible(false);
      _opened.remove(this);
      dispose();
   }

   public static void closeAll(){
      Debug.log(1, "close all ScreenHighlighter");
      for(ScreenHighlighter s : _opened){
         if(s.isVisible()){
            s.setVisible(false);
            s.dispose();
         }
      }
      _opened.clear();
   }


   Animator _aniX, _aniY;
   public void showDragDrop(int _srcx, int _srcy, int _destx, int _desty, float secs){
      _mode = VizMode.DRAG_DROP;
      int x1 = (_srcx < _destx) ? _srcx : _destx;
      int y1 = (_srcy < _desty) ? _srcy : _desty;
      int x2 = (_srcx > _destx) ? _srcx : _destx;
      int y2 = (_srcy > _desty) ? _srcy : _desty;
      srcx = _srcx-x1+MARGIN;     srcy = _srcy-y1+MARGIN;
      destx = _destx-x1+MARGIN;   desty = _desty-y1+MARGIN;
      _aniX = new TimeBasedAnimator(
                 new StopExtention( 
                    new OutQuarticEase(
                       (float)srcx, (float)destx, DRAGGING_TIME),
                    (long)(1000*secs)));
      _aniY = new TimeBasedAnimator(
                 new StopExtention( 
                    new OutQuarticEase(
                       (float)srcy, (float)desty, DRAGGING_TIME),
                    (long)(1000*secs)));
      showWindow(x1-MARGIN, y1-MARGIN, x2-x1+2*MARGIN, y2-y1+2*MARGIN, secs);
   }

   public void showDropTarget(Location loc, float secs){
      showDragDrop(_lastTarget.x, _lastTarget.y, loc.x, loc.y, secs);
   }

   public void highlight(Region r_){
      if(Env.getOS() == OS.LINUX){
         Debug.error("highlight does not work on Linux.");
         return;
      }
      _borderOnly = true;
      Region r;
      if(_native_transparent)
         r = r_;
      else{
         r = new Region(r_);
         r.setROI(new Rectangle(r_.x-3, r_.y-3, r_.w+6, r_.h+6));
         captureScreen(r.x, r.y, r.w, r.h);
      }
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
      _anim = new PulseAnimator(TARGET_SIZE/2-5, 0, 350, (long)(secs*1000));
      showWindow(x, y, w, h, secs);
   }

   private void showWindow(int x, int y, int w, int h, float secs){
      if(!_native_transparent)
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
         Env.getOSUtil().bringWindowToFront(this, true);
         //FIXME: windows?
      }
      /*
      else if(Env.getOS() == OS.WINDOWS){
         Win32Util.bringWindowToFront(this, true);
      }
      else
      */
         super.toFront();
   }

}


