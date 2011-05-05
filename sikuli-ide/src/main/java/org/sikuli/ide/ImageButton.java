/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;


import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.text.*;
import javax.imageio.*;

import org.sikuli.script.Location;
import org.sikuli.script.Debug;

class ImageButton extends JButton implements ActionListener, Serializable /*, MouseListener*/ {
   static final int DEFAULT_NUM_MATCHES = 10;
   static final float DEFAULT_SIMILARITY = 0.7f;


   private String _imgFilename, _thumbFname;
   private SikuliPane _pane;
   private float _similarity;
   private int _numMatches = DEFAULT_NUM_MATCHES;
   private boolean _exact;
   private Location _offset;
   private int _imgW, _imgH;
   private float _scale = 1f;

   private PatternWindow pwin = null;


   /*
   public void mousePressed(java.awt.event.MouseEvent e) {}
   public void mouseReleased(java.awt.event.MouseEvent e) {}
   public void mouseClicked(java.awt.event.MouseEvent e) {}

   public void mouseEntered(java.awt.event.MouseEvent e) {
   }

   public void mouseExited(java.awt.event.MouseEvent e) {
   }
   */


   public String getFilename(){
      File img = new File(_imgFilename);
      String oldBundle = img.getParent();
      String newBundle = _pane.getSrcBundle();
      Debug.log("ImageButton.getFilename: " + oldBundle + " " + newBundle);
      if(oldBundle == newBundle)
         return _imgFilename;
      setFilename(newBundle + File.separatorChar +  img.getName());
      return _imgFilename;
   }

   public void setFilename(String newFilename){
      _imgFilename = newFilename;
      _thumbFname = createThumbnail(_imgFilename);
      setIcon(new ImageIcon(_thumbFname));
      setToolTipText( this.toString() );
   }

   public void setTargetOffset(Location offset){
      Debug.log("setTargetOffset: " + offset);
      _offset = offset;
      setToolTipText( this.toString() );
   }

   public void setParameters(boolean exact, float similarity, int numMatches){
      Debug.log(3, "setParameters: " + exact + "," + similarity + "," + numMatches);
      _exact = exact;
      if(similarity>=0) _similarity = similarity;
      setToolTipText( this.toString() );
   }

   public BufferedImage createThumbnailImage(int maxHeight){
      return createThumbnailImage(_imgFilename, maxHeight);
   }

   private BufferedImage createThumbnailImage(String imgFname, int maxHeight){
      try{
         BufferedImage img = ImageIO.read(new File(imgFname));
         int w = img.getWidth(null), h = img.getHeight(null);
         _imgW = w;
         _imgH = h;
         if(maxHeight >= h)  
            return img;
         _scale = (float)maxHeight/h;
         w *= _scale;
         h *= _scale;
         BufferedImage thumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2d = thumb.createGraphics();
         g2d.drawImage(img, 0, 0,  w, h, null);
         g2d.dispose();
         return thumb;
      }
      catch(IOException e){
         Debug.error("Can't read file: " + e.getMessage());
         return null;
      }
   }

   private String createThumbnail(String imgFname, int maxHeight){
      BufferedImage thumb = createThumbnailImage(imgFname, maxHeight);
      return Utils.saveTmpImage(thumb);
   }

   private String createThumbnail(String imgFname){
      final int max_h = UserPreferences.getInstance().getDefaultThumbHeight();
      return createThumbnail(imgFname, max_h);
   }
   
   public ImageButton(SikuliPane pane, String imgFilename){
      _pane = pane;
      setFilename(imgFilename);
      _exact = false;
      _similarity = DEFAULT_SIMILARITY;
      _numMatches = DEFAULT_NUM_MATCHES;

      setBorderPainted(true);
      setCursor(new Cursor (Cursor.HAND_CURSOR));
      addActionListener(this);
      //addMouseListener(this);
      setToolTipText( this.toString() );
   }

   private boolean useThumbnail(){
      return !_imgFilename.equals(_thumbFname);
   }
   
   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;
      drawText(g2d);
      if( useThumbnail() ){
         g2d.setColor( new Color(0, 128, 128, 128) );
         g2d.drawRoundRect(3, 3, getWidth()-7, getHeight()-7, 5, 5);
      }
   }

   private static Font textFont = new Font("arial", Font.BOLD, 12);
   private void drawText(Graphics2D g2d){
      String strSim=null, strOffset=null;
      if( _similarity != DEFAULT_SIMILARITY){
         if(_similarity==1)
            strSim = "1.0";
         else{
            strSim = String.format("%.2f", _similarity);
            if(strSim.charAt(0) == '0')
               strSim = strSim.substring(1);
         }
      }
      if(_offset != null && (_offset.x!=0 || _offset.y !=0)){
         strOffset = _offset.toString();
      }
      if(strOffset == null && strSim == null)
         return;

      final int fontH = g2d.getFontMetrics().getMaxAscent();
      final int x = getWidth(), y = 0;
      drawText(g2d, strSim, x, y);
      if(_offset!=null)
         drawCross(g2d);
   }

   private void drawCross(Graphics2D g2d){
      int x,y;
      final String cross = "+";
      final int w = g2d.getFontMetrics().stringWidth(cross);
      final int h = g2d.getFontMetrics().getMaxAscent();
      if(_offset.x>_imgW/2) x = getWidth()-w;
      else if(_offset.x<-_imgW/2) x = 0;
      else x= (int)(getWidth()/2 + _offset.x * _scale - w/2 );
      if(_offset.y>_imgH/2) y = getHeight()+h/2-3;
      else if(_offset.y<-_imgH/2) y = h/2+2;
      else y= (int)(getHeight()/2 + _offset.y * _scale + h/2);
      g2d.setFont( textFont );
      g2d.setColor( new Color(0, 0, 0, 180) );
      g2d.drawString(cross, x+1, y+1);
      g2d.setColor( new Color(255, 0, 0, 180) );
      g2d.drawString(cross, x, y);
   }

   private void drawText(Graphics2D g2d, String str, int x, int y){
      if(str==null)
         return;
      final int w = g2d.getFontMetrics().stringWidth(str);
      final int fontH = g2d.getFontMetrics().getMaxAscent();
      final int borderW = 2;
      g2d.setFont( textFont );
      g2d.setColor( new Color(0, 128, 0, 128) );
      g2d.fillRoundRect(x-borderW*2-w, y, w+borderW*2, fontH+borderW*2, 3, 3);
      g2d.setColor( Color.white );
      g2d.drawString(str, x-w-3, y+fontH+1);
   }

   public void actionPerformed(ActionEvent e) {
      Debug.log("open Pattern Settings");
      pwin = new PatternWindow(this, _exact, _similarity, _numMatches);
      pwin.setTargetOffset(_offset);
   }

   public Location getTargetOffset(){
      return _offset;
   }

   public String toString(){
      String img = new File(_imgFilename).getName();
      String pat = "Pattern(\"" + img + "\")"; 
      String ret = "";
      if(_exact)
         ret += ".exact()";
      if(_similarity != DEFAULT_SIMILARITY && !_exact)
         ret += String.format(Locale.ENGLISH, ".similar(%.2f)", _similarity);
      if(_offset != null && (_offset.x!=0 || _offset.y!=0))
         ret += ".targetOffset(" + _offset.x + "," + _offset.y +")";
      if(!ret.equals(""))
         ret = pat + ret;
      else
         ret = "\"" + img + "\"";
      return ret;
   }
}

