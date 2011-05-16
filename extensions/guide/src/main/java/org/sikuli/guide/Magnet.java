package org.sikuli.guide;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JWindow;

import org.sikuli.guide.SikuliGuideAnchor.AnchorListener;
import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.Location;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;

public class Magnet 
implements Transition, GlobalMouseMotionListener {

   SikuliGuide guide;
   GlobalMouseMotionTracker mouseTracker;
   private Clickable lastClickedClickable;
   
   public Magnet(SikuliGuide guide){
      this.guide = guide;

      mouseTracker = GlobalMouseMotionTracker.getInstance();
      mouseTracker.addListener(this);
      
      // TOOD: fix this hack
      // use this trick to engage clickablewindow
      guide.addComponent(new Clickable(),0);
      
   }
   
   ArrayList<SikuliGuideAnchor> targets = new ArrayList<SikuliGuideAnchor>();
   ArrayList<Link> links = new ArrayList<Link>();
//   void flyTarget(SikuliGuideAnchor a){
//      
//      Location mouseLocation = Env.getMouseLocation();
//
//      try {
//         Pattern pattern = a.getPattern();
//         SikuliGuideImage img = new SikuliGuideImage(pattern.getImage());
//         img.setActualLocation(a.getActualLocation());
//         
//         Dimension currentSize = a.getActualSize();
//         Dimension targetSize = new Dimension(currentSize);
//         targetSize.width *= 1.5;
//         targetSize.height *= 1.5;
//
//         img.addResizeAnimation(currentSize, targetSize);
//         
//         
//         Point currentLocation = new Point(a.getActualLocation());
//         currentLocation.x += img.getActualWidth();
//         currentLocation.y += img.getActualHeight();
//         
//         int dx = mouseLocation.x - currentLocation.x;
//         int dy = mouseLocation.y - currentLocation.y;
//         
//         int radius = 50;
//         double distance = mouseLocation.distance(currentLocation);
//         double m = radius / distance;
//
//         Point targetLocation = new Point();
//         targetLocation.x = (int) (mouseLocation.x - dx*m) - img.getActualWidth()/2;
//         targetLocation.y = (int) (mouseLocation.y - dy*m) - img.getActualHeight()/2;
//         
//         
//         Rectangle desiredSpot = new Rectangle(targetLocation, targetSize);
//         desiredSpot = getFreeSpot(desiredSpot);
//         targetLocation = desiredSpot.getLocation();
//         
//         
//         img.addMoveAnimation(currentLocation,  targetLocation);
//         guide.addToFront(img);
//         img.startAnimation();
//         
//         
//         Region r = new Region(mouseLocation.x-radius,mouseLocation.y-radius,radius*2,radius*2);               
//         SikuliGuideCircle c = new SikuliGuideCircle(r);
//         guide.addComponent(c,1);
//         
//         guide.repaint();
//         
//
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
   
   class Link {
      SikuliGuideImage image;
      SikuliGuideAnchor anchor;
   }
   
   void attractTarget(SikuliGuideAnchor a, Point targetLocation){
    

    try {
       Pattern pattern = a.getPattern();
       SikuliGuideImage img = new SikuliGuideImage(pattern.getImage());
       
       Clickable clickable = new Clickable();
       clickable.setLocationRelativeToComponent(img, Layout.OVER);
       guide.addToFront(clickable);
       
       clickable.clickPoint = a.getCenter();
       
       
       Link link = new Link();
       link.image = img;
       link.anchor = a;
       links.add(link);
       
       img.setShadowDefault();
       img.setActualLocation(a.getActualLocation());
       
       Dimension currentSize = a.getActualSize();
       Dimension targetSize = new Dimension(currentSize);
       targetSize.width *= 1.5;
       targetSize.height *= 1.5;

       img.addResizeAnimation(currentSize, targetSize);
       
       
       Point currentLocation = new Point(a.getActualLocation());

       targetLocation.x -= targetSize.width/2;
       targetLocation.y -= targetSize.height/2;
       
       img.addMoveAnimation(currentLocation,  targetLocation);
       guide.addToFront(img);
       img.startAnimation();
       
       guide.repaint();
       

    } catch (IOException e) {
       e.printStackTrace();
    }
      
   }
   
   SikuliGuideCircle selection;
   public void allTargetAnchored(){
      
      double theta = 0;
      double dtheta = 2.0f * Math.PI / (double) targets.size();
         
      Location mouseLocation = Env.getMouseLocation();
      int x = mouseLocation.x;
      int y = mouseLocation.y;
      int radius = 50;
      
      
      Region r = new Region(mouseLocation.x-radius,mouseLocation.y-radius,radius*2,radius*2);               
      SikuliGuideCircle c = new SikuliGuideCircle(r);
      guide.addToFront(c);

      selection = new SikuliGuideCircle();
      guide.addToFront(selection);
            
      
      // sort targets along x-axis
      Collections.sort(targets, new Comparator<SikuliGuideAnchor>(){
         @Override
         public int compare(SikuliGuideAnchor a, SikuliGuideAnchor b) {
            return b.getX() - a.getX();
         }});

      
         for (SikuliGuideAnchor target : targets){
            
            int px = (int) (x + radius * Math.cos(theta));
            int py = (int) (y + radius * Math.sin(theta));
            theta += dtheta;
                        
            attractTarget(target, new Point(px,py));
         }
         
   }
   
   int anchoredCount = 0;
   public void addTarget(final Pattern pattern){
      
      final SikuliGuideAnchor a = new SikuliGuideAnchor(pattern);
      guide.addToFront(a);
      
      targets.add(a);
      
      SikuliGuideFlag f = new SikuliGuideFlag("Flag");
      f.setLocationRelativeToComponent(a, Layout.LEFT);
      guide.addToFront(f);
      
      
      a.addListener(new AnchorListener(){

         @Override
         public void anchored() {
            Debug.info("[Magnet] pattern anchored");
                        
            anchoredCount += 1;
            
            if (anchoredCount == targets.size()){
               allTargetAnchored();
            }
         }
         
      });
      
   }
   
//   ArrayList<Rectangle> occupiedSpots = new ArrayList<Rectangle>();
//   Rectangle getFreeSpot(Rectangle desired){
//      
//      Rectangle freeSpot = new Rectangle(desired);
//      
//      for (Rectangle occupiedSpot : occupiedSpots){
//         
//         if (freeSpot.intersects(occupiedSpot)){            
//            freeSpot.x = occupiedSpot.x + occupiedSpot.width + 10;
//         }
//         
//      }
//      
//      occupiedSpots.add(freeSpot);
//      
//      return freeSpot;
//   }
   
   
   TransitionListener token;
   @Override
   public String waitForTransition(TransitionListener token) {
      this.token = token;
      mouseTracker.start();  
         return "Next";
   }   

   @Override
   public void globalMouseMoved(int x, int y) {
//      Debug.log("[Magnet] moved to " + x + "," + y);
      
      Point p = new Point(x,y);
      for (Link link : links){
        
         if (link.image.getActualBounds().contains(p)){
            //Debug.info("[Magnet] mouseover on a target");
            
            if (selection != null){
               selection.setMargin(5,5,5,5);
               selection.setLocationRelativeToComponent(link.anchor, Layout.OVER);
               guide.repaint();
            }
         }
      }
         
         
   }
   
   @Override
   public void globalMouseIdled(int x, int y) {
   }
   

}