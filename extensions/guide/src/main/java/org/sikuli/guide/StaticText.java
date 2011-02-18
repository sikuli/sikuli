/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLabel;

import org.sikuli.guide.SikuliGuide.HorizontalAlignment;
import org.sikuli.guide.SikuliGuide.VerticalAlignment;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

class StaticText extends JLabel{

   /**
    * 
    */
   private final SikuliGuide sikuliGuide;

   static final String style = "font-size:16px;color:white;background-color:#333333;padding:3px";

   String raw_text;
   StaticText(SikuliGuide sikuliGuide, String text){         
      super();
      this.sikuliGuide = sikuliGuide;
      raw_text = text;

      String htmltxt = 
         "<html><div style='" + style + "'>"  + raw_text + "</div></html>";

      setText(htmltxt);
      setMaximumWidth(300);
      
      setSize(getPreferredSize());
   }

   void setMaximumWidth(int min_width){
      Dimension size = getPreferredSize();
      if (size.width > min_width){
         // hack to limit the width of the text to 300px
         String htmltxt = 
            "<html><div style='width:" + min_width + ";" + style + "'>"
            + raw_text + "</div></html>";
         setText(htmltxt);
      }
   }
   
   void moveInside(Region region){
      
      // the margin to the boundary
      final int margin = 5;
      
      Point p = getLocation();
      Dimension size = getSize();
      
      int x_origin = p.x;
      int y_origin = p.y;

      Screen screen = region.getScreen();

      Location screen_br = screen.getBottomRight();
      Location region_br = region.getBottomRight();

      // calculate how much the text box goes over the screen boundary
      int x_overflow = x_origin + size.width - Math.min(screen_br.x, region_br.x);
      if (x_overflow > 0){
         x_origin -= x_overflow;
         x_origin -= margin; 
      }

      int y_overflow = y_origin + size.height - Math.min(screen_br.y, region_br.y);
      if (y_overflow > 0){
         y_origin -= y_overflow;
         y_origin -= margin;
      }

      // convert to region coordinate
      x_origin -= this.sikuliGuide._region.x;
      y_origin -= this.sikuliGuide._region.y;

      setBounds(x_origin,y_origin,size.width,size.height);
      
   }
   
   public void align(Location location, HorizontalAlignment horizontal_alignment, 
      VerticalAlignment vertical_alignment){
      
      setLocation(location);
      Dimension size = getSize();
      
      
      // adjust the location based on the alignment
      if (horizontal_alignment == HorizontalAlignment.CENTER){
         location.x -= size.width/2;
      }else if (horizontal_alignment == HorizontalAlignment.RIGHT){
         location.x -= size.width;
      }

      if (vertical_alignment == VerticalAlignment.MIDDLE){
         location.y -= size.height/2;
      }else if (vertical_alignment == VerticalAlignment.BOTTOM){
         location.y -= size.height;
      }

      setLocation(location);
   }
}