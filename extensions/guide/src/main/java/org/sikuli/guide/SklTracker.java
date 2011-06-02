package org.sikuli.guide;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import org.sikuli.guide.Transition.TransitionListener;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

interface SklTrackerListener {

//   void patternAnchored(Object source);
   void patternFoundFirstTime(Object source, Region match);
   void patternFoundAgain(Object source, Region match);
   void patternNotFound(Object source);

}

class SklClicker implements SklTrackerListener, Transition{
   
   private SklAnchorModel _anchor;
  
   SklClicker(SklAnchorModel anchor){
      _anchor = anchor;
   }

   @Override
   public void patternFoundAgain(Object source, Region match) {
   }

   @Override
   public void patternFoundFirstTime(Object source, Region match) {
      _anchor.setLocation(match.x, match.y);
      _anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(_anchor).start();

      try {
         (new Screen()).click(match, 0);
      } catch (FindFailed e) {
      }

      if (transitionListener != null)
         transitionListener.transitionOccurred(this);
            
      ((SklTracker) source).stopTracking();
   }

   @Override
   public void patternNotFound(Object source) {
      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
   }

   public void setAnchor(SklAnchorModel anchor) {
      this._anchor = anchor;
   }

   public SklAnchorModel getAnchor() {
      return _anchor;
   }
   
   TransitionListener transitionListener;
   
   @Override
   public String waitForTransition(TransitionListener transitionListener) {
      this.transitionListener = transitionListener;
      return null;
   }
   
}

class SklVisibilityCheckerGroup implements Transition {
   TransitionListener transitionListener;
   
   
   ArrayList<SklVisibilityChecker> _checkers = new ArrayList<SklVisibilityChecker>();
   
   
   
   @Override
   public String waitForTransition(TransitionListener transitionListener) {
      this.transitionListener = transitionListener;
      return null;
   }


   int found = 0;
   public void oneMoreFound() {
      found += 1;
      if (found == _checkers.size() && transitionListener != null){
         
         // Wait for one second before transitioning
         Timer timer = new Timer(1000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
               transitionListener.transitionOccurred(SklVisibilityCheckerGroup.this);
            }
         
         });
         timer.setRepeats(false);
         timer.start();
      }
   }
}

class SklVisibilityChecker implements SklTrackerListener {
   
   private SklAnchorModel _anchor;
   private SklVisibilityCheckerGroup _group;
  
   SklVisibilityChecker(SklVisibilityCheckerGroup group, SklAnchorModel anchor){
      _anchor = anchor;
      _group = group;
      _group._checkers.add(this);
   }

   @Override
   public void patternFoundAgain(Object source, Region match) {
   }

   @Override
   public void patternFoundFirstTime(Object source, Region match) {
      _anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(_anchor).start();
      
      SklAnimation anim = SklAnimationFactory.createMoveToAnimation(_anchor, new Point(match.x,match.y));
      anim.setListener(new SklAnimationListener(){

         @Override
         public void animationCompleted() {
            _group.oneMoreFound();
         }         
      });
      
      anim.start();
      SklAnimationFactory.createMoveToAnimation(_anchor, new Point(match.x,match.y)).start();
            
      ((SklTracker) source).stopTracking();
      
   }

   @Override
   public void patternNotFound(Object source) {
      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
   }

   public void setAnchor(SklAnchorModel anchor) {
      this._anchor = anchor;
   }

   public SklAnchorModel getAnchor() {
      return _anchor;
   }
   

   
}

class SklAnchorTracker implements SklTrackerListener{
   
   private SklAnchorModel _anchor;
  
   SklAnchorTracker(SklAnchorModel anchor){
      _anchor = anchor;
   }

   @Override
   public void patternFoundAgain(Object source, Region match) {
      // make it visible
      SklAnimationFactory.createFadeinAnimation(_anchor).start();

      //               int dest_x = newMatch.x + newMatch.w/2;
      //               int dest_y = newMatch.y + newMatch.h/2;

      int destx = match.x;
      int desty = match.y;

      Debug.log("[Tracker] Pattern is moving to: (" + destx + "," + desty + ")");

      Point newLocation = new Point(destx, desty);
      SklAnimationFactory.createMoveToAnimation(_anchor, newLocation).start();
   }

   @Override
   public void patternFoundFirstTime(Object source, Region match) {
      _anchor.setLocation(match.x, match.y);
      _anchor.setSize(match.w, match.h);
      SklAnimationFactory.createFadeinAnimation(_anchor).start();
   }

   @Override
   public void patternNotFound(Object source) {
      SklAnimationFactory.createFadeoutAnimation(_anchor).start();
   }

   public void setAnchor(SklAnchorModel anchor) {
      this._anchor = anchor;
   }

   public SklAnchorModel getAnchor() {
      return _anchor;
   }
   
   
}

public class SklTracker extends Thread {

   Pattern _pattern;
   Region _match;
   Screen _screen;
   Pattern _centerPattern;

   public SklTracker(SklPatternModel patternModel){
      this(new Pattern(patternModel.getImageUrl()));
   }
   
   public SklTracker(Pattern pattern){
      
      _screen = new Screen();
      _pattern = pattern;
      
      try {
         BufferedImage image;
         BufferedImage center;
         image = pattern.getImage();
         int w = image.getWidth();
         int h = image.getHeight();
         center = image.getSubimage(w/4,h/4,w/2,h/2);         
         _centerPattern = new Pattern(center);
      } catch (IOException e) {
         e.printStackTrace();
      }      
   }
   
   SklTrackerListener _listener;   
   boolean isPatternStillThereInTheSameLocation(){

      try {
         sleep(1000);
      } catch (InterruptedException e) {
      }

      Region center = new Region(_match);
      center.x += center.w/4-2;
      center.y += center.h/4-2;
      center.w = center.w/2+4;
      center.h = center.h/2+4;

      Match m = center.exists(_centerPattern,0);

      if (m == null)
         Debug.log("[Tracker] Pattern is not seen in the same location.");
      
      return m != null;
      
      // Debug.log("[Tracker] Pattern is still in the same location" + m);
   }


   boolean running;
   public void run(){
      running = true;

      // Looking for the target for the first time
      Debug.log("[Tracker] Looking for the target for the first time");
      
      _match = null;
      while (running && (_match == null)){         
         _match = _screen.exists(_pattern,0.5);         
      }

      // this means the tracker has been stopped before the pattern is found
      if (_match == null)
         return;
      
      Debug.log("[Tracker] Pattern is found for the first time");
      _listener.patternFoundFirstTime(this,_match);
               
      while (running){

         if (_match != null && isPatternStillThereInTheSameLocation()){
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
         Match newMatch = _screen.exists(_pattern,1.0);
         if (newMatch == null){
            Debug.log("[Tracker] Pattern is not found on the screen");
            _listener.patternNotFound(this);
         }else {
            Debug.log("[Tracker] Pattern is found in a new location: " + newMatch);
            _listener.patternFoundAgain(this,newMatch);
         }

         _match = newMatch;
      } 
   }


   public void stopTracking(){
      running = false;
   }
  
}
