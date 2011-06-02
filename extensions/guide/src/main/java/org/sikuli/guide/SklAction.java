package org.sikuli.guide;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

class SklAssertionAction extends SklAction{

   public SklAssertionAction(Pattern pattern) {
      super(pattern);
   }
   
   public void run(){
      waitForFirstAppearance();
   }
}

public class SklAction extends Thread implements Transition {

   Pattern _pattern;
   Region _match;
   Screen _screen;
   Pattern _centerPattern;
   boolean _running;

   public SklAction(SklPatternModel patternModel){
      this(new Pattern(patternModel.getImageUrl()));
   }
   
   public SklAction(Pattern pattern){      
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

   protected void waitForFirstAppearance(){
      Debug.log("[SklAction] Wait for the target's first appearance");      
      _match = null;
      while (_running && (_match == null)){         
         _match = _screen.exists(_pattern,0.5);         
      }
   }
   
   protected void clickOnLocation(int x, int y){
      try {
         _screen.click(new Location(x,y), 0);
      } catch (FindFailed e) {
      }
   }
   
//   abstract public void run();
//      _running = true;
//
//      // Looking for the target for the first time
//      waitForFirstAppearance();
//      
//      // this means the tracker has been stopped before the pattern is found
//      if (_match == null)
//         return;
//      
//      clickOnLocation(10,10);
//      
//      if (transitionListener != null)
//         transitionListener.transitionOccurred(this);
//
//   }

   public void cancel(){
      _running = false;
   }
   
   TransitionListener transitionListener;
   @Override
   public String waitForTransition(TransitionListener transitionListener) {
      this.transitionListener = transitionListener;
      return null;
   }
  
}
