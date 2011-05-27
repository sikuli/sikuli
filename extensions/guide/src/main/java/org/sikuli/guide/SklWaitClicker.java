package org.sikuli.guide;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SklWaitClicker extends Thread implements Transition {


   //SikuliGuide guide;
   Pattern pattern;
   Region match;
   Screen screen;
   String image_filename;
   Pattern centerPattern;


   boolean initialFound = false;
   
   // TODO: refactor to merge the two constructors
   public SklWaitClicker(SklPatternModel patternModel){
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
   
   public SklWaitClicker(Pattern pattern){
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
   
      
      anchor.setLocation(match.x, match.y);
      anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(anchor).start();

      
      try {
         screen.click(match, 0);
      } catch (FindFailed e) {
      }
      
      if (transitionListener != null)
         transitionListener.transitionOccurred(this);

   }

   public void stopTracking(){
      running = false;
   }

   
   TransitionListener transitionListener;
   @Override
   public String waitForTransition(TransitionListener transitionListener) {
      this.transitionListener = transitionListener;
      return null;
   }
  
}
