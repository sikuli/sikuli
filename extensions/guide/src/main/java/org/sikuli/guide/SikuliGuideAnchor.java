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

public class SikuliGuideAnchor extends SikuliGuideComponent{

   Region region;
   public SikuliGuideAnchor(Region region){         
      super();
      this.region = region;
      setBounds(region.getRect());
   }

   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;

//      Rectangle r = getBounds();
//      g2d.setColor(Color.black);
//      g2d.drawRect(0,0,r.width-1,r.height-1);
//      g2d.setColor(Color.white);
//      g2d.drawRect(1,1,r.width-3,r.height-3);
   } 

}