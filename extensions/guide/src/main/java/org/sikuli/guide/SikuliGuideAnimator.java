package org.sikuli.guide;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class SikuliGuideAnimator implements ActionListener {

   Timer timer;
   SikuliGuideComponent comp;
   int duration = 1000;
   int cycle = 50; 
   
   public SikuliGuideAnimator(SikuliGuideComponent comp){
      this.comp = comp;
      timer = new Timer(cycle,this);
   }
   
   public void start(){
         timer.start();
   }
   
   
//   static SikuliGuideAnimator createCircling(SikuliGuideComponent comp, int radius){
//         return new CircleAnimator(comp, radius);
//   }
//   
//   static SikuliGuideAnimator createSliding(SikuliGuideComponent comp, int offset_x, int offset_y){
//      Point dest = getLocation();
//      Point src = new Point(dest.x + offset_x, dest.y + offset_y);
//
//      return new MoveAnimator(comp,src,dest);
//   }

}



class CircleAnimator extends SikuliGuideAnimator{
   int repeatCount;
   int count;

   LinearInterpolation funcr;
   
   Point origin;
   int radius;
   
   public CircleAnimator(SikuliGuideComponent comp, int radius){
      super(comp);

      repeatCount = duration / cycle;     
      count = 0;
      
      funcr = new LinearInterpolation(0,(float) (2*Math.PI),repeatCount);

      origin = comp.getLocation();
      this.radius = radius;
   }
   

   @Override
   public void actionPerformed(ActionEvent e) {
         
      
      float r = funcr.getValue(count);
      
      int x = (int) (origin.x + (int) radius * Math.sin(r));
      int y=  (int) (origin.y + (int) radius * Math.cos(r));
      
      Point p = new Point(x,y);
      
      comp.setLocation(p);         
      comp.sikuliGuide.repaint();
      
      if (count == repeatCount)
         count = 0;
      else
         count++;
   
      
   }
}


class MoveAnimator extends  SikuliGuideAnimator{

   int repeatCount;
   int count;
   
   OutQuarticEase tfuncx,tfuncy;
   
   public MoveAnimator(SikuliGuideComponent comp, Point src, Point dest){
      super(comp);
      
      repeatCount = duration / cycle;     
      count = 0;
      
      tfuncx = new OutQuarticEase(src.x,dest.x,repeatCount);
      tfuncy = new OutQuarticEase(src.y,dest.y,repeatCount);
      
   }
   
   public void start(){
      timer.start();
   }
   
   public void actionPerformed(ActionEvent e){
      if ( count <= repeatCount){
         
         int x = (int) tfuncx.getValue(count);
         int y = (int) tfuncy.getValue(count);
         
         count++;

         comp.setLocation(x,y); 
         comp.sikuliGuide.repaint();
         
      }else{
         timer.stop();
      
      }
      
   }
}
