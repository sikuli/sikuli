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
      
   public void setText(String text){
      this.text = text;
      updateLabel();
   }
   
   public String getText(){
      return text;
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
   
   TextPropertyEditor ed = null;
   public void setEditable(boolean editable){
      if (editable){
         
      }else{
         
 //        this.getParent().remove(ed);
         
      }
   }
}