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
   private
   SklPatternModel pattern;
   
   @Override
   public SklObjectView createView(){
      return new SklAnchorView(this);
   }

   public void setPattern(SklPatternModel pattern) {
      this.pattern = pattern;
   }
   
   SklTracker tracker;
   public void startTracking(){
      tracker = new SklTracker(pattern);
      tracker.setAnchor(this);      
      tracker.start();      
   }

   public void startTracking(Pattern pattern){
      tracker = new SklTracker(pattern);
      tracker.setAnchor(this);
      tracker.start();
   }

   
   public SklPatternModel getPattern() {
      return pattern;
   }
}


class SklAnchorView extends SklObjectView {

   public SklAnchorView(SklObjectModel model){
      super(model);
   }
      
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;

      //if (editable){

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

      //}
   }
   


}