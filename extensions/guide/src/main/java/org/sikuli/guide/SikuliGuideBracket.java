/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JLabel;

import org.sikuli.guide.SikuliGuide.HorizontalAlignment;
import org.sikuli.guide.SikuliGuide.VerticalAlignment;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SikuliGuideBracket extends SikuliGuideComponent{


   // which direction this element is pointing
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   public final static int DIRECTION_SOUTH = 3;
   public final static int DIRECTION_NORTH = 4;


   String text;
   JLabel label;
   int length;
   public SikuliGuideBracket(SikuliGuide sikuliGuide){         
      super(sikuliGuide);
      this.length = 30;
      init();
   }
   
   static final int PADDING_X = 2;
   static final int PADDING_Y = 2;
   static final int SHADOW_SIZE = 2;

   int direction;
   boolean entrance = true;
   void init(){
      
            
      setDirection(DIRECTION_EAST);
   }

   public void setLocationRelativeToRegion(Region region, int side) {
      if (side == TOP){
         setSize(region.w,thickness);
         setDirection(DIRECTION_SOUTH);
      } else if (side == BOTTOM){
         setSize(region.w,thickness);
         setDirection(DIRECTION_NORTH);
      } else if (side == LEFT){
         setSize(thickness,region.h);
         setDirection(DIRECTION_EAST);
      } else if (side == RIGHT){
         setSize(thickness,region.h);
         setDirection(DIRECTION_WEST);
      }      
      
      if (side == LEFT || side == RIGHT){
         length = region.h;
      }else{
         length = region.w;
      }
      
      super.setLocationRelativeToRegion(region,side);
   }
   
   public void startAnimation(){

      // TODO: move this somewhere else
      // this should only be called first time animation
      // is started
      // Why called here? Because ... 
      // we want the location to be decided
         if (direction == DIRECTION_EAST){
            setEntranceAnimation(createSlidingAnimator(-20,0));
         } else if (direction == DIRECTION_WEST){
            setEntranceAnimation(createSlidingAnimator(20,0));
         } else if (direction == DIRECTION_SOUTH){
            setEntranceAnimation(createSlidingAnimator(0,-20));
         } else if (direction == DIRECTION_NORTH){
            setEntranceAnimation(createSlidingAnimator(0,20));
         }

      
      super.startAnimation();
   }
   

   int thickness = 10; 
   int margin = 5;
   
   public void setDirection(int direction){
      this.direction = direction;  
   }

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;

      Stroke pen = new BasicStroke(3.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.red);
      
      
      GeneralPath polyline = new GeneralPath();
      polyline.moveTo(0,0);
      polyline.lineTo(5,5);
      polyline.lineTo(5,length/2-6);
      polyline.lineTo(8,length/2);
      polyline.lineTo(5,length/2+6);
      polyline.lineTo(5,length-5);
      polyline.lineTo(0,length);
      
      
      AffineTransform rat = new AffineTransform();
      
      if (direction == DIRECTION_EAST){
          // need to translate the rotated shape so that it can be in the visible bounds
         rat.translate(thickness,length);
         rat.rotate(Math.PI);
      } else if (direction == DIRECTION_SOUTH){
         rat.translate(0,thickness);
         rat.rotate(-Math.PI/2);
      } else if (direction == DIRECTION_NORTH){
         rat.translate(length,0);
         rat.rotate(Math.PI/2);
      }
      g2d.transform(rat);        
             
      g2d.draw(polyline);
   }


}
