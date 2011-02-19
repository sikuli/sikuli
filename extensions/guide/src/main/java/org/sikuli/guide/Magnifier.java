/**
 * 
 */
package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.sikuli.guide.Flag.SlidingAnimator;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.ScreenImage;

public class Magnifier extends Component {
   
   
   MagnificationAnimator anim;
   public void start(){
      anim = new MagnificationAnimator();
      anim.animateStep();
      repaint();
   }

   
   class MagnificationAnimator extends TimeBasedAnimator{
      
      public MagnificationAnimator(){
         super(new OutQuarticEase((float)1f, (float)2f, 1500)); 
      }
     
      public void animateStep(){
         ratio = step();      
         //Debug.log("ratio="+ratio);
         w = (int) (ratio* (float)image.getWidth());
         h = (int) (ratio* (float)image.getHeight());
         setSize(new Dimension(w,h));
         
         Location loc = region.getTopLeft();
         loc.translate(-(int)(w-region.w)/2, -(int) (h-region.h)/2);
         setLocation(loc);
         
      }
      
   }
   
   BufferedImage image;
   float ratio;
   int w,h;
   SikuliGuide guide;
   
   public Magnifier(SikuliGuide guide, BufferedImage image){
      this.guide = guide;
      this.image = image;
      ratio = 1.0f;      
      w = (int) (ratio* (float)image.getWidth());
      h = (int) (ratio* (float)image.getHeight());
      setSize(new Dimension(w,h));
   }
   
   public BufferedImage capture(Rectangle rect) {
      BufferedImage img = null;
      try {
         img = (new Robot()).createScreenCapture(rect);
      } catch (AWTException e) {
      }
      return img;
   }

   
   Region region;
   public Magnifier(SikuliGuide guide, Region region){
      
      BufferedImage img = capture(region.getRect());
      this.region = region;
      
      setLocation(region.getTopLeft());
      
      this.guide = guide;
      this.image = img;
      ratio = 1.0f;      
      w = (int) (ratio* (float)image.getWidth());
      h = (int) (ratio* (float)image.getHeight());
      setSize(new Dimension(w,h));      
   }
      
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      super.paint(g);
      if (anim == null)
         return;
      
      Ellipse2D.Double ellipse =
         new Ellipse2D.Double(0,0,w,h);
      g2d.clip(ellipse);      
      g2d.drawImage(image, 0, 0, w, h, null); 

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);       
      
      g2d.setClip(null);      
      g2d.setStroke(new BasicStroke(3.0F));      
      g2d.setColor(Color.white);
      g2d.draw(ellipse);
      
      if (anim != null && anim.running()){
         anim.animateStep();
         guide.repaint();
      }

   }
}