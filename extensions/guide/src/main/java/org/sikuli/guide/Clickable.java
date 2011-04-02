/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.sikuli.script.Region;

public class Clickable extends SikuliGuideComponent {
   
      Color normalColor = new Color(1.0f,1.0f,0,0.1f);
      Color mouseOverColor = new Color(1.0f,0,0,0.1f);

      String name;
      Region region;
      public Clickable(Region region){
         this.region = region;
         if (region != null){
            this.setBounds(region.getRect());
            this.setLocation(region.x,region.y);
         }
      }

      public void setName(String name){ 
         this.name = name;
      }
            
      boolean borderVisible = true;
      public void setBorderVisible(boolean borderVisible){
         this.borderVisible = borderVisible;
      }
      
      boolean mouseOver;
      public void setMouseOver(boolean mouseOver){
         if (this.mouseOver != mouseOver){
            
            if (this.mouseOver){
               globalMouseExited();
            }else{
               globalMouseEntered();
            }
            
            this.repaint();
         }            
         this.mouseOver = mouseOver;
         
      }
      
      public boolean isMouseOver(){
         return mouseOver;
      }
      
      public void globalMouseEntered(){  
         //mouseOver = true;
      }
      
      public void globalMouseExited(){
         //mouseOver = false;         
      }
      
      public void paintComponent(Graphics g){
         super.paintComponent(g);
         
         Graphics2D g2d = (Graphics2D) g;
         
         g2d.setColor(new Color(1,1,1,0.1f));         
         g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
         
//         if (mouseOver){
//            g2d.setColor(mouseOverColor);
//         }else{
//            g2d.setColor(normalColor);
//         }
//         
//         if (borderVisible){
//            g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
//            g2d.setColor(Color.white);
//            g2d.drawRect(0,0,getWidth()-1,getHeight()-1);
//            g2d.setColor(Color.black);
//            g2d.drawRect(1,1,getWidth()-3,getHeight()-3);
//         }
      }
}