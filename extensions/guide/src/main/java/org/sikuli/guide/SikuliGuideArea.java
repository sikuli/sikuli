/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class SikuliGuideArea extends SikuliGuideComponent 
implements ComponentListener{

   ArrayList<Region> regions = new ArrayList<Region>();

   ArrayList<SikuliGuideComponent> landmarks = new ArrayList<SikuliGuideComponent>();

   public SikuliGuideArea(){         
      super();
   }

   public static final int BOUNDING = 0;
   public static final int INTERSECTION = 1;


   int relationship = BOUNDING;
   public void setRelationship(int relationship){
      this.relationship = relationship;      
   }


   // update the bounds to the union of all the rectangles
   void updateBounds(){

      Rectangle rect = null;



      for (SikuliGuideComponent comp : landmarks){


         if (rect == null){
            rect = new Rectangle(comp.getBounds());
            continue;
         }else {


            if (relationship == BOUNDING){
               rect.add(comp.getBounds());
            }else if (relationship == INTERSECTION){            
               rect = rect.intersection(comp.getBounds());
            }
            
         }
      }

      if (rect.width<0 || rect.height<=0){
         setVisible(false);
      }else{
         setVisible(true);
         
         // hack to get the locations of the followers to update
         setLocation(rect.x,rect.y);
         setSize(rect.getSize());
      }
   }


   public void addLandmark(SikuliGuideComponent comp){
      landmarks.add(comp);    
      updateBounds();

      comp.addComponentListener(this);

   }




   public void addRegion(Region region){

      if (regions.isEmpty()){

         setBounds(region.getRect());

      }else{         

         Rectangle bounds = getBounds();
         bounds.add(region.getRect());
         setBounds(bounds);

      }

      regions.add(region);
   }

   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;

      Rectangle r = getBounds();
      g2d.setColor(Color.black);
      g2d.drawRect(0,0,r.width-1,r.height-1);
      g2d.setColor(Color.white);
      g2d.drawRect(1,1,r.width-3,r.height-3);
   }


   @Override
   public void componentHidden(ComponentEvent e) {
      boolean allHidden = true;
      for (SikuliGuideComponent landmark : landmarks){
         allHidden = allHidden && !landmark.isVisible();
      }
      
      if (allHidden){
         //Debug.info("Area is hidden");
      }
      setVisible(!allHidden);
   }


   @Override
   public void componentMoved(ComponentEvent e) {
      updateBounds();
      repaint();
   }

   @Override
   public void componentResized(ComponentEvent e) {
   }


   @Override
   public void componentShown(ComponentEvent e) {
      setVisible(true);
   } 

}