/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import org.sikuli.script.ScreenImage;
import org.sikuli.script.Match;
import org.sikuli.script.Location;
import org.sikuli.script.Finder;
import org.sikuli.script.Region;
import org.sikuli.script.Debug;

import org.sikuli.ide.util.LoadingSpinner;

class TargetOffsetPane extends JPanel implements MouseListener, MouseWheelListener, ChangeListener{
   final static int DEFAULT_H = 300;
   final static float DEFAULT_PATTERN_RATIO=0.4f;
   ScreenImage _simg;
   BufferedImage _img;
   Match _match = null;

   int _viewX, _viewY, _viewW, _viewH;
   float _zoomRatio, _ratio;
   Location _tar = new Location(0,0);
   Location _offset = new Location(0,0);
   JSpinner txtX, txtY;

   private LoadingSpinner _loading;
   private boolean _finding = true;

   public TargetOffsetPane(ScreenImage simg, String patFilename, Location initOffset){
      _simg = simg;
      _ratio = DEFAULT_PATTERN_RATIO;
      Rectangle r = _simg.getROI();
      int w = DEFAULT_H/r.height*r.width;
      setPreferredSize(new Dimension(w, DEFAULT_H));

      addMouseListener(this);
      addMouseWheelListener(this);

      _loading = new LoadingSpinner();
      findTarget(patFilename, initOffset);
   }

   void findTarget(final String patFilename, final Location initOffset){
      Thread thread = new Thread(new Runnable(){
         public void run(){
            Finder f = new Finder(_simg, new Region(0,0,0,0));
            try{
               f.find(patFilename);
               if(f.hasNext()){
                  _match = f.next();
                  if(initOffset!=null)
                     setTarget(initOffset.x, initOffset.y);
                  else
                     setTarget(0,0);
               }
               _img = ImageIO.read(new File(patFilename));
            } catch (IOException e) {
               Debug.error("Can't load " + patFilename);
            }
            synchronized(this){
               _finding = false;
            }
            repaint();
         }
      });
      thread.start();
   }

   static String _I(String key, Object... args){ 
      return I18N._I(key, args);
   }


   private void zoomToMatch(){
      _viewW = (int)(_match.w/_ratio);
      _zoomRatio = getWidth()/(float)_viewW;
      _viewH = (int)(getHeight()/_zoomRatio);
      _viewX = _match.x + _match.w/2 - _viewW/2;
      _viewY = _match.y + _match.h/2 - _viewH/2;
   }

   public void setTarget(int dx, int dy){
      Debug.log(3, "new target: " + dx + "," + dy);
      if(_match != null){
         Location center = _match.getCenter();
         _tar.x = center.x + dx;
         _tar.y = center.y + dy;
      }
      else{
         _tar.x = dx;
         _tar.y = dy;
      }

      _offset = new Location(dx, dy);
      if(txtX != null){
         txtX.setValue(new Integer(dx));
         txtY.setValue(new Integer(dy));
      }
      repaint();
   }

   public void mousePressed ( MouseEvent me ) {
      Location tar = convertViewToScreen(me.getPoint());
      Debug.log(4, "click: " + me.getPoint() + " -> " + tar);
      if(_match != null){
         Location center = _match.getCenter();
         setTarget(tar.x-center.x, tar.y-center.y);
      }
      else{
         setTarget(tar.x, tar.y);
      }
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      int rot = e.getWheelRotation();
      int patW = (int)(getWidth()*_ratio);
      float zoomRatio = patW/(float)_img.getWidth();
      int patH = (int)(_img.getHeight()*_zoomRatio);;
      if(rot<0){
         if(patW < 2*getWidth() && patH < 2*getHeight())
            _ratio *= 1.1;
      }
      else{
         if(patW > 20 && patH > 20)
            _ratio *= 0.9;
      }
      repaint();
   }
   public void mouseClicked ( MouseEvent me ) { }

   public void mouseReleased ( MouseEvent me ) {
   }

   public void mouseEntered ( MouseEvent me ) { }

   public void mouseExited ( MouseEvent me ) { }

   private static Color COLOR_BG_LINE = new Color(210,210,210,130);
   void paintRulers(Graphics g2d){
      int step = (int)(10*_zoomRatio);
      if(step<2) step = 2;
      int h = getHeight(), w = getWidth();
      if(h%2==1)  h--;
      if(w%2==1)  w--;
      g2d.setColor(COLOR_BG_LINE);
      for(int x=w/2;x>=0;x-=step){
         g2d.drawLine(x, 0, x, h);
         g2d.drawLine(w-x, 0, w-x, h);
      }
      for(int y=h/2;y>=0;y-=step){
         g2d.drawLine(0, y, w, y);
         g2d.drawLine(0, h-y, w, h-y);
      }
   }

   void paintBackground(Graphics g2d){
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, getWidth(), getHeight());
   }

   void paintPatternOnly(Graphics g2d){
      int patW = (int)(getWidth()*_ratio);
      _zoomRatio = patW/(float)_img.getWidth();
      int patH = (int)(_img.getHeight()*_zoomRatio);;
      int patX = getWidth()/2-patW/2, patY = getHeight()/2-patH/2;
      paintBackground(g2d);
      g2d.drawImage(_img, patX, patY, patW, patH, null);
   }

   void paintSubScreen(Graphics g2d){
      if(_viewX<0 || _viewY<0) paintBackground(g2d);
      int subX = _viewX<0?0:_viewX, subY = _viewY<0?0:_viewY;
      int subW = _viewW-(subX-_viewX), subH = _viewH-(subY-_viewY);
      BufferedImage img = _simg.getImage();
      if(subX+subW >= img.getWidth()) subW = img.getWidth()-subX;
      if(subY+subH >= img.getHeight()) subH = img.getHeight()-subY;

      BufferedImage clip = img.getSubimage(subX, subY, subW, subH);
      int destX = (int)((subX-_viewX)*_zoomRatio), 
          destY = (int)((subY-_viewY)*_zoomRatio);
      int destW = (int)(subW * _zoomRatio), 
          destH = (int)(subH * _zoomRatio);
      g2d.drawImage(clip, destX, destY, destW, destH, null);
   }

   public void paintComponent(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      if( getWidth() > 0 && getHeight() > 0){
         if(_match!=null){
            zoomToMatch();
            paintSubScreen(g2d);
            paintMatch(g2d);
         }
         else
            paintPatternOnly(g2d);
         paintRulers(g2d);
         paintTarget(g2d);
         synchronized(this){
            if(_finding) 
               paintLoading(g2d);
         }

      }
   }

   void paintLoading(Graphics2D g2d){
      int w = getWidth(), h = getHeight();
      g2d.setColor(new Color(0,0,0,200));
      g2d.fillRect(0, 0, w, h);
      BufferedImage spinner = _loading.getFrame();
      g2d.drawImage(spinner, null, w/2-spinner.getWidth()/2, h/2-spinner.getHeight()/2);
      repaint();
   }


   Location convertViewToScreen(Point p){
      Location ret = new Location(0,0);
      if(_match!=null){
         ret.x = (int)(p.x/_zoomRatio+_viewX);
         ret.y = (int)(p.y/_zoomRatio+_viewY);
      }
      else{
         ret.x = (int)((p.x-getWidth()/2)/_zoomRatio);
         ret.y = (int)((p.y-getHeight()/2)/_zoomRatio);
      }
      return ret;
   }

   Point convertScreenToView(Location loc){
      Point ret = new Point();
      if(_match!=null){
         ret.x = (int)((loc.x - _viewX) * _zoomRatio);
         ret.y = (int)((loc.y - _viewY) * _zoomRatio);
      }
      else{
         ret.x = (int)(getWidth()/2 + loc.x * _zoomRatio);
         ret.y = (int)(getHeight()/2 + loc.y * _zoomRatio);
      }
      return ret;
   }

   void paintTarget(Graphics2D g2d){
      final int CROSS_LEN=20/2;
      Point l = convertScreenToView(_tar);
      g2d.setColor(Color.BLACK);
      g2d.drawLine(l.x-CROSS_LEN, l.y+1, l.x+CROSS_LEN, l.y+1);
      g2d.drawLine(l.x+1, l.y-CROSS_LEN, l.x+1, l.y+CROSS_LEN);
      g2d.setColor(Color.WHITE);
      g2d.drawLine(l.x-CROSS_LEN, l.y, l.x+CROSS_LEN, l.y);
      g2d.drawLine(l.x, l.y-CROSS_LEN, l.x, l.y+CROSS_LEN);
   }

   void paintMatch(Graphics2D g2d){
      int w = (int)(getWidth() * _ratio), 
          h = (int)((float)w/_img.getWidth()*_img.getHeight());
      int x = getWidth()/2- w/2, y = getHeight()/2-h/2;
      Color c = SimilaritySlider.getScoreColor(_match.getScore());
      g2d.setColor(c);
      g2d.fillRect(x, y, w, h);
      g2d.drawRect(x, y, w-1, h-1);
   }

   public JComponent createControls(){
      JPanel pane = new JPanel(new GridBagLayout());
      JLabel lblTargetX = new JLabel(_I("lblTargetOffsetX"));
      JLabel lblY = new JLabel(_I("lblTargetOffsetY"));

      int x = _offset!=null? _offset.x : 0;
      int y = _offset!=null? _offset.y : 0;
      txtX = new JSpinner(new SpinnerNumberModel(x, -999, 999, 1)); 
      txtY = new JSpinner(new SpinnerNumberModel(y, -999, 999, 1)); 
      txtX.addChangeListener(this);
      txtY.addChangeListener(this);

      GridBagConstraints c = new GridBagConstraints();

      c.fill = 1;
      c.gridy = 0;
      pane.add( lblTargetX, c );
      pane.add( txtX, c );
      pane.add( lblY, c );
      pane.add( txtY, c );

      return pane;
      
   }

   public void stateChanged(javax.swing.event.ChangeEvent e) {
      int x = (Integer)txtX.getValue();
      int y = (Integer)txtY.getValue();
      setTarget(x, y);
   }

   public Location getTargetOffset(){
      return new Location(_offset);
   }
}
