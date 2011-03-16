/**
 * 
 */
package org.sikuli.guide;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.sikuli.script.Region;

public class SikuliGuideComponent extends JComponent{

 
   final SikuliGuide sikuliGuide;

//   ArrayList<SikuliGuideAnimator> anims = new ArrayList<SikuliGuideAnimator>();
   
   public void startAnimation(){     
      if (entrance_anim != null)
         entrance_anim.start();
   }
   
   public SikuliGuideAnimator createSlidingAnimator(int offset_x, int offset_y){  
      Point dest = getLocation();
      Point src = new Point(dest.x + offset_x, dest.y + offset_y);
      return new MoveAnimator(this, src, dest);
   }


   public SikuliGuideAnimator createCirclingAnimator(int radius) {      
      return new CircleAnimator(this, radius);
   }
   
   
   SikuliGuideAnimator entrance_anim;
   public void setEntranceAnimation(SikuliGuideAnimator anim){
      entrance_anim = anim;
   } 

   public final static int TOP = 0;
   public final static int LEFT = 1;
   public final static int RIGHT = 2;
   public final static int BOTTOM = 3;

   public SikuliGuideComponent(SikuliGuide sikuliGuide){    
      super();
      this.sikuliGuide = sikuliGuide;
   }
   

   public void setLocationRelativeToRegion(Region region, int side) {
      int height = getHeight();
      int width = getWidth();
      if (side == TOP){
         setBounds(region.x + region.w/2 - width/2, region.y - height, width, height);
      } else if (side == BOTTOM){
         setBounds(region.x + region.w/2 - width/2, region.y + region.h, width, height);         
      } else if (side == LEFT){
         setBounds(region.x - width, region.y + region.h/2 - height/2, width, height);                  
      } else if (side == RIGHT){
         setBounds(region.x + region.w, region.y + region.h/2 - height/2, width, height);                  
      }
   }

   public void setHorizontalAlignmentWithRegion(Region region, float f){


      int x0 = region.x;
      int x1 = region.x + region.w - getWidth();

      int x = (int) (x0 + (x1-x0)*f);

      setLocation(x,getY());
   }
   
   public void setVerticalAlignmentWithRegion(Region region, float f){


      int y0 = region.y;
      int y1 = region.y + region.h - getHeight();

      int y = (int) (y0 + (y1-y0)*f);

      setLocation(getX(),y);
   }
}