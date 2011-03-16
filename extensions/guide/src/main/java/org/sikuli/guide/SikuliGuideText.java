/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.Timer;

import org.sikuli.guide.SikuliGuide.HorizontalAlignment;
import org.sikuli.guide.SikuliGuide.VerticalAlignment;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SikuliGuideText extends SikuliGuideComponent {

   
   static final String style = "font-size:16px;color:white;background-color:#333333;padding:3px";

   String text;
   JLabel label;
   public SikuliGuideText(SikuliGuide sikuliGuide, String text){         
      super(sikuliGuide);
      this.text = text;

      label = new JLabel();
      add(label);
      
      updateLabel();      
   }

   public void setMaximumWidth(int max_width){
      this.max_width = max_width;
      updateLabel();
      updateSize();
   }
   
   void updateLabel(){
      Dimension size = getPreferredSize();
      if (size.width > max_width){
         // hack to limit the width of the text to width
         String htmltxt = 
            "<html><div style='width:" + max_width + ";" + getStyleString() + "'>"
            + text + "</div></html>";
         label.setText(htmltxt);
      }else{
         String htmltxt = 
            "<html><div style='" + getStyleString() + "'>"
            + text + "</div></html>";
         label.setText(htmltxt);
      }
      
      updateSize();
   }
   
   void updateSize(){
      Dimension size = label.getPreferredSize();
      label.setSize(size);
      setSize(size);
   }

   int fontSize = 16;
   int max_width = Integer.MAX_VALUE;
   String getStyleString(){
      return "font-size:"+fontSize+"px;color:white;background-color:#333333;padding:3px";
   }
      
   public void setFontSize(int i) {
      fontSize = i;
      updateLabel();
   }

   
//   void moveInside(Region region){
//      
//      // the margin to the boundary
//      final int margin = 5;
//      
//      Point p = getLocation();
//      Dimension size = getSize();
//      
//      int x_origin = p.x;
//      int y_origin = p.y;
//
//      Screen screen = region.getScreen();
//
//      Location screen_br = screen.getBottomRight();
//      Location region_br = region.getBottomRight();
//
//      // calculate how much the text box goes over the screen boundary
//      int x_overflow = x_origin + size.width - Math.min(screen_br.x, region_br.x);
//      if (x_overflow > 0){
//         x_origin -= x_overflow;
//         x_origin -= margin; 
//      }
//
//      int y_overflow = y_origin + size.height - Math.min(screen_br.y, region_br.y);
//      if (y_overflow > 0){
//         y_origin -= y_overflow;
//         y_origin -= margin;
//      }
//
//      // convert to region coordinate
//      x_origin -= this.sikuliGuide._region.x;
//      y_origin -= this.sikuliGuide._region.y;
//
//      setBounds(x_origin,y_origin,size.width,size.height);
//      
//   }
   

}