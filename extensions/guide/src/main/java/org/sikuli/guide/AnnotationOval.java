/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

class AnnotationOval extends Annotation {
	
   String text;
   int centerx,centery,cornerx,cornery;
   public AnnotationOval(int centerx, int centery, int cornerx, int cornery){
      this.centerx = centerx;
      this.centery = centery;
      this.cornerx = cornerx;
      this.cornery = cornery;
      color = Color.red;
   }

   public void setColor(Color color){
      this.color = color;
   }

   public void paintAnnotation(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;

      g2d.setColor(color);
      Stroke pen = new BasicStroke(3.0F);    
      g2d.setStroke(pen);
      // Assume x, y, and diameter are instance variables
      Ellipse2D.Double circle =
         new Ellipse2D.Double();
      circle.setFrameFromCenter(centerx,centery,cornerx,cornery);
      g2d.draw(circle);
   }
}