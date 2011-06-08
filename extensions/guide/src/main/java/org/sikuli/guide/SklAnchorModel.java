package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.sikuli.script.Pattern;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root
public class SklAnchorModel extends DefaultSklObjectModel {   
   
   public SklAnchorModel(){
      
   }
   
   
   static String CLICK_COMMAND = "click";
   static String ASSERT_COMMAND = "assert";
   static String TYPE_COMMAND = "type";
   
   private String action = ASSERT_COMMAND;
   
   public SklAnchorModel(Rectangle r){
      setX(r.x);
      setY(r.y);
      setWidth(r.width);
      setHeight(r.height);
   }
   
   @Override
   public Object clone() throws CloneNotSupportedException{
      SklAnchorModel o = (SklAnchorModel) super.clone();
      return o;
   }
   
   @Element (required = false)
   private SklPatternModel pattern;
   
   public void setPattern(SklPatternModel pattern) {
      this.pattern = pattern;
   }
   
   public SklPatternModel getPattern() {
      return pattern;
   }

   public void setCommand(String action) {
      this.action = action;
   }

   
   public String getCommand() {
      return action;
   }
   
   Object _arg;
   public void setArgument(Object arg){
      _arg = arg;
   }

   public Object getArgument() {
      return _arg;
   }
}


class SklAnchorView extends SklView {

   public SklAnchorView(SklModel model){
      super(model);
   }
      
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
         if (true){
            Rectangle r = getActualBounds();
            g2d.setColor(getForeground());
            g2d.drawRect(0,0,r.width-1,r.height-1);
            g2d.setColor(Color.white);
            g2d.drawRect(1,1,r.width-3,r.height-3);
            g2d.setColor(getForeground());
            g2d.drawRect(2,2,r.width-5,r.height-5);
            g2d.setColor(Color.white);
            g2d.drawRect(3,3,r.width-7,r.height-7);
         }else{
            Rectangle r = getActualBounds();
            g2d.setColor(Color.red);            
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawRect(1,1,r.width-3,r.height-3);
         }
   }
   


}