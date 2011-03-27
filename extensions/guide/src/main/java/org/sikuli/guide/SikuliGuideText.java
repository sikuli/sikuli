/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

public class SikuliGuideText extends SikuliGuideComponent {

   static final int SHADOW_SIZE = 10;
   static final int DEFAULT_MAXIMUM_WIDTH = 300;

   String text;
   JLabel label;
   public SikuliGuideText(String text){         
      super();
      this.text = text;

      label = new JLabel();
      add(label);

      setMaximumWidth(DEFAULT_MAXIMUM_WIDTH);
      updateLabel();      
   }

   public void setMaximumWidth(int max_width){
      this.max_width = max_width;
      updateLabel();
      updateSize();
   }

   void updateLabel(){

      String htmltxt = 
         "<html><div style='" + getStyleString() + "'>"
         + text + "</div></html>";
      label.setText(htmltxt);

      Dimension size = label.getPreferredSize();
      if (size.width > max_width){
         // hack to limit the width of the text to width
         htmltxt = 
            "<html><div style='width:" + max_width + ";" + getStyleString() + "'>"
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


   int fontSize = 12;
   int max_width = Integer.MAX_VALUE;
   String getStyleString(){
      //return "font-size:"+fontSize+"px;color:white;background-color:#333333;padding:3px";
      //return "font-size:"+fontSize+"px;color:black;background-color:#FFF1A8;padding:3px";
      return "font-size:"+fontSize+"px;color:black;background-color:#FFFF00;padding:3px";
   }

   public void setFontSize(int i) {
      fontSize = i;
      updateLabel();
   }
   
   private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
      float[] clr1 = c1.getComponents(null);
      float[] clr2 = c2.getComponents(null);
      for (int i = 0; i < clr1.length; i++) {
          clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
      }
      return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
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