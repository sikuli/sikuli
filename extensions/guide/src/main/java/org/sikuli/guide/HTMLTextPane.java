/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextPane;

import org.sikuli.script.Debug;

class HTMLTextPane extends JTextPane{
   static final int DEFAULT_MAXIMUM_WIDTH = 300;
   
   int maximum_width;
   String text;
   public Dimension preferredDimension;
   public HTMLTextPane(){
      maximum_width = DEFAULT_MAXIMUM_WIDTH;         
      setContentType("text/html");
      setBackground(Color.yellow);
//      setMaximumSize(new Dimension(500,Integer.MAX_VALUE));
   }
         
   @Override
   public void setText(String text){  
      this.text = text;
      String htmltxt = "<html><font size=5>"+text+"</font></html>";
      
      super.setText(htmltxt);
      
      JTextPane tp = new JTextPane();
      tp.setText(htmltxt);

      if (getPreferredSize().getWidth() > maximum_width){
         
         htmltxt = "<html><div width='"+maximum_width+"'><font size=5>"+text+"</font></div></html>";
         super.setText(htmltxt);
      }
      
      setSize(getPreferredSize());
   }
      
   
   @Override
   public String getText(){
      return this.text;
   }
   
   void setMaximumWidth(int maximum_width){
      this.maximum_width = maximum_width;
      setText(this.text);      
   }
}