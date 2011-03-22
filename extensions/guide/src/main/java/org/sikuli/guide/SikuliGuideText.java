/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.sikuli.guide.SikuliGuide.HorizontalAlignment;
import org.sikuli.guide.SikuliGuide.VerticalAlignment;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SikuliGuideText extends SikuliGuideComponent {

   static final int SHADOW_SIZE = 10;
   static final int DEFAULT_MAXIMUM_WIDTH = 300;

   String text;
   JLabel label;
   public SikuliGuideText(SikuliGuide sikuliGuide, String text){         
      super(sikuliGuide);
      this.text = text;

      label = new JLabel();
      add(label);

      setMaximumWidth(DEFAULT_MAXIMUM_WIDTH);
      updateLabel();      
   }

   public void setMaximumWidth(int max_width){
      this.max_width = max_width;
      updateLabel();
      updateSize();
   }

   void updateLabel(){

      String htmltxt = 
         "<html><div style='" + getStyleString() + "'>"
         + text + "</div></html>";
      label.setText(htmltxt);

      Dimension size = label.getPreferredSize();
      if (size.width > max_width){
         // hack to limit the width of the text to width
         htmltxt = 
            "<html><div style='width:" + max_width + ";" + getStyleString() + "'>"
            + text + "</div></html>";
         label.setText(htmltxt);
      }
      updateSize();
   }

   void updateSize(){
      Dimension size = label.getPreferredSize();
      label.setSize(size);

//      size.height += 2*shadowSize;      
//      size.width += 2*shadowSize;
      setSize(size);
   }


   int fontSize = 16;
   int max_width = Integer.MAX_VALUE;
   String getStyleString(){
      //return "font-size:"+fontSize+"px;color:white;background-color:#333333;padding:3px";
      return "font-size:"+fontSize+"px;color:black;background-color:#FFF1A8;padding:3px";
   }

   public void setFontSize(int i) {
      fontSize = i;
      updateLabel();
   }
   
   private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
      float[] clr1 = c1.getComponents(null);
      float[] clr2 = c2.getComponents(null);
      for (int i = 0; i < clr1.length; i++) {
          clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
      }
      return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
  }
   

   
   
//   public void setLocation(Point p){
//      super.setLocation(p.x-shadowSize, p.y-shadowSize);
//   }
//
//   public void setLocation(int x, int y){
//      super.setLocation(x-shadowSize, y-shadowSize);      
//   }
   
   public void paint(Graphics g){

      Graphics2D g2d = (Graphics2D)g;
      //g2d.translate(3,3);
//
//      g2d.setColor(Color.black);
//      //g2d.drawRect(2,2,getWidth()-3,getHeight()-3);
//      
//      Rectangle r = getBounds();
//      r.grow(SHADOW_SIZE, SHADOW_SIZE);

      //Point o = getLocation();
      //setBounds(o.x-3,o.y-3,getWidth(),getHeight());
      
      
//      getBlurOp(shadowSize).filter(createShadowMask(image), shadow);
     
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, shadowOpacity)); 
//      g2d.setColor(shadowColor); 
//      g2d.fillRect(0, 0, getWidth(), getHeight()); 
      //g2d.dispose(); 
      
      
      
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//      //g2d.translate(SHADOW_SIZE,SHADOW_SIZE);
//      //g2d.setColor(new Color(0.3f,0.3f,0.3f));
//      
//      int sw = 10*2;
//      for (int i=sw; i >= 2; i-=2) {
//          float pct = (float)(sw - i) / (sw - 1);
//          g2d.setColor(getMixedColor(Color.LIGHT_GRAY, pct,
//                                    Color.WHITE, 1.0f-pct));
//          g2d.setStroke(new BasicStroke(i));
//          g2d.drawRect(0,0,getWidth(),getHeight());
//      }
//      
      //GradientPaint gp = new GradientPaint(0f,0f,blue,0f,30f,green);
      //g2.setPaint(gp);
      
      //g2d.fillRect(0,0,getWidth(),getHeight());
//      g2d.fillRect(0,0,getWidth()-4,getHeight()-4);
//      g2d.fillRect(0,0,getWidth()-3,getHeight()-3);
//      g2d.fillRect(0,0,getWidth()-2,getHeight()-2);
//      g2d.fillRect(0,0,getWidth(),getHeight());

//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      
      
      //g2d.translate(SHADOW_SIZE,SHADOW_SIZE);
      
//      BufferedImage image = new BufferedImage(getWidth() + shadowSize * 2,
//            getHeight() + shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
//      Graphics2D g2 = image.createGraphics();
//      g2.translate(shadowSize,shadowSize);
//      label.paint(g2);
//      
//      BufferedImage shadow = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
//      getBlurOp(shadowSize).filter(createShadowMask(image), shadow);
//
//      
//      g2d.drawImage(shadow, 0, 0, null, null);
//      
//      g2d.translate(shadowSize,shadowSize);
      //label.paint(g2d);
      //g2d.drawImage(image, shadowSize, shadowSize, null, null);
      
     
      super.paint(g);
      
      //setLocation(o.x,o.y);
   }


   //   void moveInside(Region region){
   //      
   //      // the margin to the boundary
   //      final int margin = 5;
   //      
   //      Point p = getLocation();
   //      Dimension size = getSize();
   //      
   //      int x_origin = p.x;
   //      int y_origin = p.y;
   //
   //      Screen screen = region.getScreen();
   //
   //      Location screen_br = screen.getBottomRight();
   //      Location region_br = region.getBottomRight();
   //
   //      // calculate how much the text box goes over the screen boundary
   //      int x_overflow = x_origin + size.width - Math.min(screen_br.x, region_br.x);
   //      if (x_overflow > 0){
   //         x_origin -= x_overflow;
   //         x_origin -= margin; 
   //      }
   //
   //      int y_overflow = y_origin + size.height - Math.min(screen_br.y, region_br.y);
   //      if (y_overflow > 0){
   //         y_origin -= y_overflow;
   //         y_origin -= margin;
   //      }
   //
   //      // convert to region coordinate
   //      x_origin -= this.sikuliGuide._region.x;
   //      y_origin -= this.sikuliGuide._region.y;
   //
   //      setBounds(x_origin,y_origin,size.width,size.height);
   //      
   //   }


}