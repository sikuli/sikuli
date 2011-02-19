/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import org.sikuli.script.Region;

public class Image extends Component {
   
   BufferedImage image;
   float ratio;
   int w,h;
   public Image(BufferedImage image){
      this.image = image;
      ratio = 2.0f;      
      w = (int) ratio*image.getWidth();
      h = (int) ratio*image.getHeight();
      setSize(new Dimension(w,h));
   }
      
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      
      Rectangle r = new Rectangle(5,5,120,140);
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

   }
}