package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import org.simpleframework.xml.Root;

@Root
public class SklRectangleModel extends DefaultSklObjectModel {   

   public SklRectangleModel(){
      
   }
   
   public SklRectangleModel(Rectangle r){
      super();
      setBounds(r);
   }
 
   public void setBounds(Rectangle r){
      setLocation(r.getLocation());
      setSize(r.getSize());
   }
}

class SklRectangleView extends SklView {

   public SklRectangleView(SklRectangleModel model){
      super(model);   
   }
      
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
      Stroke pen = new BasicStroke(3.0F);    
      g2d.setStroke(pen);
      
      g2d.drawRect(0,0,_model.getWidth()-1,_model.getHeight()-1);
   } 
   
}
