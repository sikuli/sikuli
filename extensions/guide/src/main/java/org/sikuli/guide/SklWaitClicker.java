package org.sikuli.guide;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.sikuli.guide.Transition.TransitionListener;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class SklWaitClicker extends Thread implements Transition {

   Pattern _pattern;
   Region match;
   Screen _screen;
   Pattern _centerPattern;

   boolean initialFound = false;
   
   public SklWaitClicker(SklPatternModel patternModel){
      this(new Pattern(patternModel.getImageUrl()));
   }
   
   public SklWaitClicker(Pattern pattern){      
      _screen = new Screen();
      _pattern = pattern;

      BufferedImage image;
      BufferedImage center;
      try {
         image = pattern.getImage();
         int w = image.getWidth();
         int h = image.getHeight();
         center = image.getSubimage(w/4,h/4,w/2,h/2);         
         _centerPattern = new Pattern(center);
      } catch (IOException e) {
         e.printStackTrace();
      }      
   }

   SklAnchorModel anchor;
   public void setAnchor(SklAnchorModel anchor) {
      this.anchor = anchor;
   }

   SklTrackerListener listener;
   
   boolean _running;
   public void run(){
      _running = true;
      initialFound = true;

      // Looking for the target for the first time
      Debug.log("[Tracker] Looking for the target for the first time");
      
      match = null;
      while (_running && (match == null)){         
         match = _screen.exists(_pattern,0.5);
         
      }

      // this means the tracker has been stopped before the pattern is found
      if (match == null)
         return;
      
      Debug.log("[Tracker] Pattern is found for the first time");
   
      
      anchor.setLocation(match.x, match.y);
      anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(anchor).start();

      
      try {
         _screen.click(match, 0);
      } catch (FindFailed e) {
      }
      
      if (transitionListener != null)
         transitionListener.transitionOccurred(this);

   }

   public void stopTracking(){
      _running = false;
   }
   
   TransitionListener transitionListener;   
   @Override
   public String waitForTransition(TransitionListener transitionListener) {
      this.transitionListener = transitionListener;
      return null;
   }   
  
}
