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

import org.sikuli.script.Region;

public class SikuliGuideRectangle extends SikuliGuideComponent{

   Region region;
   public SikuliGuideRectangle(Region region){         
      super();
      if (region != null){
         this.region = region;
         setActualBounds(region.getRect());
      }
      setForeground(Color.red);
   }

   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
      Stroke pen = new BasicStroke(3.0F);    
      g2d.setStroke(pen);
      g2d.drawRect(0,0,getActualWidth()-1,getActualHeight()-1);

   } 

}