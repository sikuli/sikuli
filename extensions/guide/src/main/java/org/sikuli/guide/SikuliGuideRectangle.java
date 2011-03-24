/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JLabel;

import org.sikuli.guide.SikuliGuide.HorizontalAlignment;
import org.sikuli.guide.SikuliGuide.VerticalAlignment;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SikuliGuideRectangle extends SikuliGuideComponent{

   Region region;
   public SikuliGuideRectangle(SikuliGuide sikuliGuide, Region region){         
      super(sikuliGuide);
      this.region = region;
      setBounds(region.getRect());
      setForeground(Color.red);
   }

   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;

      Stroke pen = new BasicStroke(3.0F);    
      g2d.setStroke(pen);

      Rectangle r = getBounds();      
      g2d.drawRect(0,0,r.width-1,r.height-1);
   } 

}