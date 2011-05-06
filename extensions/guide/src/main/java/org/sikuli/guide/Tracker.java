package org.sikuli.guide;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.sikuli.guide.SikuliGuide.TrackerAdapter;
import org.sikuli.guide.SikuliGuideComponent.AnimationListener;
import org.sikuli.script.Debug;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

interface TrackerListener {
   void patternAnchored();
}

public class Tracker extends Thread {


   SikuliGuide guide;
   Pattern pattern;
   Region match;
   Screen screen;
   String image_filename;
   Pattern centerPattern;


   boolean initialFound = false;
   public Tracker(Pattern pattern){
      //this.guide = guide;    
      //this.match = match;
      screen = new Screen();

      BufferedImage image;
      BufferedImage center;


      this.pattern = pattern;
      try {
         image = pattern.getImage();
         int w = image.getWidth();
         int h = image.getHeight();
         center = image.getSubimage(w/4,h/4,w/2,h/2);         
         centerPattern = new Pattern(center);
      } catch (IOException e) {
         e.printStackTrace();
      }      
   }
   
   public Tracker(SikuliGuide guide, Pattern pattern, Region match){
      this.guide = guide;    
      //this.match = match;
      screen = new Screen();

      BufferedImage image;
      BufferedImage center;


      this.pattern = pattern;
      try {
         image = pattern.getImage();
         int w = image.getWidth();
         int h = image.getHeight();
         center = image.getSubimage(w/4,h/4,w/2,h/2);         
         centerPattern = new Pattern(center);
      } catch (IOException e) {
         e.printStackTrace();
      }      
   }



   ArrayList<SikuliGuideComponent> components = new ArrayList<SikuliGuideComponent>();
   ArrayList<Point> offsets = new ArrayList<Point>();

   SikuliGuideAnchor anchor;
   public void setAnchor(SikuliGuideComponent component) {           
      Point loc = component.getLocation();
      //Point offset = new Point(loc.x - match.x, loc.y - match.y);
      Point offset = new Point(0,0);//loc.x - match.x, loc.y - match.y);
      offsets.add(offset);
      components.add(component);

      anchor = (SikuliGuideAnchor) component; 
   }


   boolean isAnimationStillRunning(){
      for (SikuliGuideComponent comp : components){
         if (comp instanceof SikuliGuideAnchor){

            if (comp.animationRunning)
               return true;;
         }
      }
      return false;
   }

   TrackerListener listener;
   
   boolean isPatternStillThereInTheSameLocation(){

      try {
         sleep(1000);
      } catch (InterruptedException e) {
      }

      Region center = new Region(match);
      center.x += center.w/4-2;
      center.y += center.h/4-2;
      center.w = center.w/2+4;
      center.h = center.h/2+4;

      Match m = center.exists(centerPattern,0);

      if (m == null)
         Debug.log("[Tracker] Pattern is not seen in the same location.");
      
      return m != null;

      
      // Debug.log("[Tracker] Pattern is still in the same location" + m);
   }


   boolean running;
   public void run(){
      running = true;
      initialFound = true;

      // Looking for the target for the first time
      Debug.log("[Tracker] Looking for the target for the first time");
      
      match = null;
      while (running && (match == null)){         
         match = screen.exists(pattern,0.5);
         
      }

      // this means the tracker has been stopped before the pattern is found
      if (match == null)
         return;
      
      Debug.log("[Tracker] Pattern is found for the first time");
      
      
      if (true){
         anchor.setActualLocation(match.x, match.y);
         anchor.anchored();
      }else{
         // animate the initial movement to the anchor position

         // uncomment this for popup demo
         anchor.moveTo(new Point(match.x, match.y), new AnimationListener(){
         public void animationCompleted(){
            anchor.anchored();
            if (listener != null){
               listener.patternAnchored();
            }
         }
         });
      }
      
      while (running){

         if (match != null && isPatternStillThereInTheSameLocation()){
            //Debug.log("[Tracker] Pattern is seen in the same location.");
            continue;
         }


         // try for at least 1.0 sec. to have a better chance of finding the
         // new position of the pattern.
         // the first attempt often fails because the target is only a few
         // pixels away when the screen capture is made and it is still
         // due to occlusion by foreground annotations      
         // however, it would mean it takes at least 1.0 sec to realize
         // the pattern has disappeared and the referencing annotations should
         // be hidden
         Match newMatch = screen.exists(pattern,1.0);

         if (newMatch == null){

            Debug.log("[Tracker] Pattern is not found on the screen");
            //anchor.setOpacity(0.0f);
            
            //not_found_counter += 1;

            //if (not_found_counter > 2){
               anchor.addFadeoutAnimation();
               anchor.startAnimation();
              // not_found_counter = 0;
            //}


         }else {

            Debug.log("[Tracker] Pattern is found in a new location: " + newMatch);

            // make it visible
            anchor.addFadeinAnimation();
            anchor.startAnimation();

//            anchor.setVisible(true);

//            // if the match is in a different location
//            if (match.x != newMatch.x || match.y != newMatch.y){

               //            for (int i=0; i < components.size(); ++i){
               // comp  = components.get(i);
               Point offset = offsets.get(0);

               int dest_x = newMatch.x + offset.x;
               int dest_y = newMatch.y + offset.y;

               // comp.setEmphasisAnimation(comp.createMoveAnimator(dest_x, dest_y));                  
               //comp.startAnimation();
               
               Debug.log("[Tracker] Pattern is moving to: (" + dest_x + "," + dest_y + ")");
               anchor.moveTo(new Point(dest_x, dest_y));     
         }


         match = newMatch;


      } 

      //            if (!initialFound){
      //            Debug.log("[Tracker] Pattern has disappeared after initial find");
      //
      //            
      //
      //            for (SikuliGuideComponent comp : components){
      //               comp.setVisible(false);
      //            }
      //            guide.repaint();
      //         }

   }


   public void stopTracking(){
      running = false;
   }
  

   public boolean isAlreadyTracking(Pattern pattern, Region match) {
      try {
         boolean sameMatch = this.match == match; 
         boolean sameBufferedImage = this.pattern.getImage() == pattern.getImage();
         boolean sameFilename = (this.pattern.getFilename() != null &&
               (this.pattern.getFilename().compareTo(pattern.getFilename()) == 0));

         return sameMatch || sameBufferedImage || sameFilename;
      } catch (IOException e) {
         return false;
      }
   }

}
