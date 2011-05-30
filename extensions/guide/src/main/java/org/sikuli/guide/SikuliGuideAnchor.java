/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.sikuli.script.Debug;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;

public class SikuliGuideAnchor extends SikuliGuideComponent{

   Region region;
   boolean editable = false;

   
   public interface AnchorListener {
      public void anchored();
      public void found(SikuliGuideAnchor source);
   }
   
//   public class AnchorAdapter implements AnchorListener {
//      public void anchored(){};
//      public void found(){};     
//   }
   
   ArrayList<AnchorListener> listeners = new ArrayList<AnchorListener>();
   public void anchored(){
      for (AnchorListener listener : listeners){
         listener.anchored();
      }
      // this implements the default behavior for fadein entrance when 
      // the anchor pattern is found and anchored for the first time
      addFadeinAnimation();
      startAnimation();
   }
   
   private boolean animateAnchoring = false;
   
   public void found(Rectangle bounds){
      for (AnchorListener listener : listeners){
         listener.found(this);
      }
      
      
      if (isAnimateAnchoring()){
      
         Point center = new Point();
//         center.x = (int) bounds.getCenterX();
//         center.y = (int) bounds.getCenterY();
          center.x = (int) bounds.x + bounds.width/2;
          center.y = (int) bounds.y + bounds.height/2;

         
         moveTo(center, new AnimationListener(){
             public void animationCompleted(){
                anchored();          
             }
         });
      
      }else{
         
         setActualLocation(bounds.x, bounds.y);
         anchored();
      }
   }
   
   
   public void addListener(AnchorListener listener){
      listeners.add(listener);
   }

   public SikuliGuideAnchor(Pattern pattern){
      super();
      this.pattern = pattern;
      setTracker(pattern);
   }
   
   public SikuliGuideAnchor(){         
      super();
      setForeground(Color.black);
   }
   
   Pattern pattern;
   Tracker tracker;
   public void setTracker(Pattern pattern){
      setOpacity(0f);

      tracker = new Tracker(pattern);

      BufferedImage img;
      try {
         img = pattern.getImage();
         setActualSize(img.getWidth(), img.getHeight());
         tracker.setAnchor(this);

      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void startTracking(){
      if  (tracker != null){
         //Debug.info("[Anchor] start tracking");
         tracker.start();
      }
   }
   
   public void stopTracking(){
      if  (tracker != null)
         tracker.stopTracking();
   }
   
   public SikuliGuideAnchor(Region region){         
      super();
      if (region != null){
            this.region = region;
            setActualBounds(region.getRect());
      }
      setForeground(Color.black);
   }
   
   

   public void setEditable(boolean editable){
      this.editable = editable;
   }

   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;

      if (editable){

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


         //         Ellipse2D.Double ellipse =
         //            new Ellipse2D.Double(0,0,6,6);
         //         g2d.translate(r.width/2-3,r.height/2-3);
         //         g2d.draw(ellipse);
         //         
         //         ellipse =
         //            new Ellipse2D.Double(0,0,9,9);
         //         g2d.translate(-3,-3);
         //         g2d.draw(ellipse);

      }
   }

   public Pattern getPattern() {
      return pattern;
   }


   public void setAnimateAnchoring(boolean animateAnchoring) {
      this.animateAnchoring = animateAnchoring;
   }

   public boolean isAnimateAnchoring() {
      return animateAnchoring;
   }



}