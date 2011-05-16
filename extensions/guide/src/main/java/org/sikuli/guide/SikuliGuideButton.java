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
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class SikuliGuideButton extends Clickable {
         
      Font f = new Font("sansserif", Font.BOLD, 18);
      JLabel label;
      
      public SikuliGuideButton(String name){
         super(new Region(0,0,0,0));
         setName(name);
         //normalColor = new Color(1.0f,1.0f,0,1.0f);
         normalColor = Color.magenta;
         //setForeground(Color.white);
         setBackground(null);
         //mouseOverColor = new Color(1.0f,0,0,1.0f);
         mouseOverColor = new Color(0.3f,0.3f,0.3f);

      }
      
      public void globalMouseEntered(){
         Rectangle r = getBounds();
         if (getTopLevelAncestor() != null)
            getTopLevelAncestor().repaint(r.x,r.y,r.width,r.height);
         //getParent().getParent().repaint(r.x,r.y,r.width,r.height);
      }
      
      public void globalMouseExited(){
         Rectangle r = getBounds();
         if (getTopLevelAncestor() != null)
            getTopLevelAncestor().repaint(r.x,r.y,r.width,r.height);

//         getParent().getParent().repaint(r.x,r.y,r.width,r.height);
      }

      public void setName(String name){
         super.setName(name);

         this.label = new JLabel(name);
         label.setFont(f);
         label.setForeground(Color.white);
         add(label);
         
         Dimension s = label.getPreferredSize();
         label.setLocation(5,5);
         label.setSize(s);

         s.height += 10;
         s.width += 10;
         setActualSize(s);                
      }
      
      public void paintComponent(Graphics g){
         super.paintComponent(g);               
         
         Graphics2D g2d = (Graphics2D) g;
         RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getActualWidth()-1, getActualHeight()-1, 10, 10);
         if (isMouseOver()){
            g2d.setColor(mouseOverColor);
         }else{
            g2d.setColor(normalColor);            
         }
         
         //g2d.setColor(normalColor); 
         g2d.fill(roundedRectangle);
         g2d.setColor(Color.white);
         g2d.draw(roundedRectangle);
         
         roundedRectangle = new RoundRectangle2D.Float(1, 1, getActualWidth()-3, getActualHeight()-3, 10, 10);
         g2d.setColor(Color.black);
         g2d.draw(roundedRectangle);
         
         label.paintComponents(g);
         
      }
}