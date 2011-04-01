/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class Hotspot extends Clickable {

   Font f = new Font("sansserif", Font.BOLD, 18);
   JLabel label;

   SikuliGuide guide;
   SikuliGuideSpotlight spotlight;
   SikuliGuideComponent text;
   JLabel symbol;
   SikuliGuideCircle circle;

   public Hotspot(Region region, SikuliGuideComponent text, SikuliGuide g){
      super(region);
      guide = g;
      spotlight = new SikuliGuideSpotlight(region);
      spotlight.setShape(SikuliGuideSpotlight.CIRCLE);
      
      Rectangle bounds = spotlight.getBounds();
      bounds.grow(10,10);      
      spotlight.setBounds(bounds);
      
      this.text = text;
      //this.text.setLocationRelativeToRegion(new Region(bounds), SikuliGuideComponent.RIGHT);

      // draw a question mark centered on the region
      Font f = new Font("sansserif", Font.BOLD, 18);
      symbol = new JLabel("?");
      symbol.setFont(f);
      Dimension size = symbol.getPreferredSize();
      symbol.setSize(size);
      symbol.setForeground(Color.white);
      symbol.setLocation(region.x+region.w/2-size.width/2, 
            region.y+region.h/2-size.height/2);
      
      // draw a circle around the question mark
      Rectangle cc = new Rectangle(symbol.getBounds());
      cc.grow(7,0);
      circle = new SikuliGuideCircle(new Region(cc));
      
      
      g.content.add(symbol);
      g.addComponent(circle);
      
      g.addComponent(spotlight);
      g.addComponent(text);
      
      text.setVisible(false);
      spotlight.setVisible(false);

   }

   @Override
   public void globalMouseEntered(){
      Debug.info("Entered");
      circle.setVisible(false);
      symbol.setVisible(false);
      spotlight.setVisible(true);
      text.setVisible(true);
      guide.repaint();
   }

   @Override
   public void globalMouseExited(){
      Debug.info("Exited");
      circle.setVisible(true);
      symbol.setVisible(true);
      spotlight.setVisible(false);
      text.setVisible(false);      
      guide.repaint();
   }
   
   @Override
   public void paintComponent(Graphics g){
      //super.paintComponent(g);
      
      Graphics2D g2d = (Graphics2D) g;
      
      if (mouseOver){
         g2d.setColor(new Color(0,0,0,0));
      }else{
         g2d.setColor(normalColor);
      }
      
      
      //JLabel label = new JLabel("?");
      //label.paintComponents(g2d);
      
      //label.paintComponents(g2d);

//      g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
//      g2d.setColor(Color.white);
//      g2d.drawRect(0,0,getWidth()-1,getHeight()-1);
//      g2d.setColor(Color.black);
//      g2d.drawRect(1,1,getWidth()-3,getHeight()-3);         
   }

}