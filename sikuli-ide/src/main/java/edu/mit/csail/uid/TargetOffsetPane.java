package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;


class TargetOffsetPane extends JPanel implements MouseListener, ChangeListener{
   final static int DEFAULT_H = 300;
   final static float DEFAULT_PATTERN_RATIO=0.4f;
   ScreenImage _simg;
   BufferedImage _img;
   int _w, _h;
   Match _match;

   int _viewX, _viewY, _viewW, _viewH;
   float _zoomRatio, _ratio;
   Location _tar = new Location(0,0);
   Location _offset = new Location(0,0);
   JSpinner txtX, txtY;

   public TargetOffsetPane(ScreenImage simg, String patFilename, Location initOffset){
      _simg = simg;
      Finder f = new Finder(_simg, new Region(simg.getROI()));
      f.find(patFilename);
      if(f.hasNext()){
         _match = f.next();
         if(initOffset!=null)
            setTarget(initOffset.x, initOffset.y);
         else
            setTarget(0,0);
         try {
            _img = ImageIO.read(new File(patFilename));
         } catch (IOException e) {
            Debug.error("Can't load " + patFilename);
         }
      }
      _ratio = DEFAULT_PATTERN_RATIO;
      Rectangle r = _simg.getROI();
      int w = DEFAULT_H/r.height*r.width;
      setPreferredSize(new Dimension(w, DEFAULT_H));
      addMouseListener(this);
   }

   private void zoomToMatch(){
      Rectangle scr = _simg.getROI();
      _viewW = (int)(_match.w/_ratio);
      _zoomRatio = getWidth()/(float)_viewW;
      _viewH = (int)(getHeight()/_zoomRatio);
      _viewX = _match.x + _match.w/2 - _viewW/2;
      _viewY = _match.y + _match.h/2 - _viewH/2;
   }

   public void setTarget(int dx, int dy){
      Debug.log(3, "new target: " + dx + "," + dy);
      Location center = _match.getCenter();
      _tar.x = center.x + dx;
      _tar.y = center.y + dy;
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
      Location center = _match.getCenter();
      setTarget(tar.x-center.x, tar.y-center.y);
   }

   public void mouseClicked ( MouseEvent me ) { }

   public void mouseReleased ( MouseEvent me ) {
   }

   public void mouseEntered ( MouseEvent me ) { }

   public void mouseExited ( MouseEvent me ) { }


   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      if( getWidth() > 0 && getHeight() > 0){
         zoomToMatch();
         BufferedImage clip = 
            _simg.getImage().getSubimage(_viewX, _viewY, _viewW, _viewH);
         g2d.drawImage(clip, 0, 0, getWidth(), getHeight(), null);
         paintMatch(g2d);
         paintTarget(g2d);
      }
   }

   Location convertViewToScreen(Point p){
      Location ret = new Location(0,0);
      ret.x = (int)(p.x/_zoomRatio+_viewX);
      ret.y = (int)(p.y/_zoomRatio+_viewY);
      return ret;
   }

   Point convertScreenToView(Location loc){
      Point ret = new Point();
      ret.x = (int)((loc.x - _viewX) * _zoomRatio);
      ret.y = (int)((loc.y - _viewY) * _zoomRatio);
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
      Color c = SimilaritySlider.getScoreColor(_match.score);
      g2d.setColor(c);
      g2d.fillRect(x, y, w, h);
      g2d.drawRect(x, y, w-1, h-1);
   }

   public JComponent createControls(){
      JPanel pane = new JPanel(new GridBagLayout());
      JLabel lblTargetX = new JLabel("Target offset  X:");
      JLabel lblY = new JLabel(", Y:");

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
