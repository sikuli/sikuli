/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.sikuli.script.Region;

public class SikuliGuideButton extends Clickable {
         
      Font f = new Font("sansserif", Font.BOLD, 18);
      JLabel label;
      
      public SikuliGuideButton(String name){
         super(new Region(0,0,0,0));
         setName(name);
         normalColor = new Color(1.0f,1.0f,0,0.5f);
         mouseOverColor = new Color(1.0f,0,0,0.5f);

      }

      public void setName(String name){
         super.setName(name);

         this.label = new JLabel(name);
         label.setFont(f);
         add(label);
         
         Dimension s = label.getPreferredSize();
         label.setLocation(5,5);
         label.setSize(s);

         s.height += 10;
         s.width += 10;
         setSize(s);
          
         
         // center the label in the button
//         label.setLocation(region.w/2 - s.width/2,
//               region.h/2 - s.height/2);
      }
      
      public void paintComponent(Graphics g){
         super.paintComponent(g);               
         label.paintComponents(g);
      }
}