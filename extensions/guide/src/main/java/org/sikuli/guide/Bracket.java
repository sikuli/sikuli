/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.sikuli.script.Region;

public class Bracket extends Component {
   
   // which direction this bookmark is pointing
   public final static int SIDE_LEFT = 1;
   public final static int SIDE_RIGHT = 2;
   public final static int SIDE_TOP = 3;
   public final static int SIDE_BOTTOM = 4;

   int side;
   
   Region region;
   
   public Bracket(Region region){
   
      this.region = region;
      
      
      setSide(SIDE_LEFT);
   }
   
   int thickness = 10; 
   int margin = 5;
      
   public void setSide(int side){
      this.side = side;
      if (side == SIDE_RIGHT){
         setBounds(region.x+region.w,region.y,thickness,region.h);
      }else if (side == SIDE_LEFT){
         setBounds(region.x-10,region.y,thickness,region.h);            
      }else if (side == SIDE_TOP){
         setBounds(region.x,region.y-10,region.w,thickness);//region.x,region.y,region.w,thickness);            
      }else if (side == SIDE_BOTTOM){
         setBounds(region.x,region.y+region.h,region.w,thickness);//.x,region.y+region.h-10,region.w,thickness*3);//region.x,region.y,region.w,thickness);            
      }
   }
   
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;

      Stroke pen = new BasicStroke(3.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.red);
      
      int length;
      if (side == SIDE_LEFT || side == SIDE_RIGHT){
         length = region.h;
      }else{
         length = region.w;
      }
               
      GeneralPath polyline = new GeneralPath();
      polyline.moveTo(0,0);
      polyline.lineTo(5,5);
      polyline.lineTo(5,length/2-6);
      polyline.lineTo(8,length/2);
      polyline.lineTo(5,length/2+6);
      polyline.lineTo(5,length-5);
      polyline.lineTo(0,length);
      

      
      AffineTransform rat = new AffineTransform();
      
      if (side == SIDE_LEFT){
          // need to translate the rotated shape so that it can be in the visible bounds
         rat.translate(10,region.h);
         rat.rotate(Math.PI);
         g2d.transform(rat);
      } else if (side == SIDE_TOP){
         rat.translate(0,10);
         rat.rotate(-Math.PI/2);
         g2d.transform(rat);            
      } else if (side == SIDE_BOTTOM){
         rat.translate(region.w,0);
         rat.rotate(Math.PI/2);
         g2d.transform(rat);            
      }
      
      g2d.draw(polyline);
   }
}