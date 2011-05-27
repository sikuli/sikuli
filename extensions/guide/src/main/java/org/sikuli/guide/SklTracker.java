package org.sikuli.guide;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.sikuli.script.Debug;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

interface SklTrackerListener {
   void patternAnchored();
}

public class SklTracker extends Thread {


   //SikuliGuide guide;
   Pattern pattern;
   Region match;
   Screen screen;
   String image_filename;
   Pattern centerPattern;


   boolean initialFound = false;
   
   // TODO: refactor to merge the two constructors
   public SklTracker(SklPatternModel patternModel){
      //this.guide = guide;    
      //this.match = match;
      screen = new Screen();

      BufferedImage image;
      BufferedImage center;

      this.pattern = new Pattern(patternModel.getImageUrl());
      
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
   
   public SklTracker(Pattern pattern){
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
   
   ArrayList<SikuliGuideComponent> components = new ArrayList<SikuliGuideComponent>();
   ArrayList<Point> offsets = new ArrayList<Point>();

   SklAnchorModel anchor;
   public void setAnchor(SklAnchorModel anchor) {
      this.anchor = anchor;
   }

   SklTrackerListener listener;
   
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
      
      
      // TODO:
      //anchor.found(bounds);
      anchor.setLocation(match.x, match.y);
      anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(anchor).start();
         
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
            
            SklAnimationFactory.createFadeoutAnimation(anchor).start();

         }else {

            Debug.log("[Tracker] Pattern is found in a new location: " + newMatch);

            // make it visible
            SklAnimationFactory.createFadeinAnimation(anchor).start();

            //               int dest_x = newMatch.x + newMatch.w/2;
            //               int dest_y = newMatch.y + newMatch.h/2;

            int destx = newMatch.x;
            int desty = newMatch.y;


            Debug.log("[Tracker] Pattern is moving to: (" + destx + "," + desty + ")");

            Point newLocation = new Point(destx, desty);

            SklAnimationFactory.createMoveAnimation(anchor, anchor.getLocation(), newLocation).start();
         }


         match = newMatch;


      } 
   }


   public void stopTracking(){
      running = false;
   }
  
}
