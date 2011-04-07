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

import org.sikuli.script.Region;

public class SikuliGuideAnchor extends SikuliGuideComponent{

   Region region;
   boolean editable = false;
   public SikuliGuideAnchor(Region region){         
      super();
      this.region = region;
      setBounds(region.getRect());
      setForeground(Color.black);
   }

   public void setEditable(boolean editable){
      this.editable = editable;
   }

   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;

      if (editable){
         Rectangle r = getBounds();
         g2d.setColor(getForeground());
         g2d.drawRect(0,0,r.width-1,r.height-1);
         g2d.setColor(Color.white);
         g2d.drawRect(1,1,r.width-3,r.height-3);
         g2d.setColor(getForeground());
         g2d.drawRect(2,2,r.width-5,r.height-5);
         g2d.setColor(Color.white);
         g2d.drawRect(3,3,r.width-7,r.height-7);
         
         
//         Ellipse2D.Double ellipse =
//            new Ellipse2D.Double(0,0,6,6);
//         g2d.translate(r.width/2-3,r.height/2-3);
//         g2d.draw(ellipse);
//         
//         ellipse =
//            new Ellipse2D.Double(0,0,9,9);
//         g2d.translate(-3,-3);
//         g2d.draw(ellipse);

      }
   } 

}