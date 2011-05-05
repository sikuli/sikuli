/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.util.ArrayList;

// The *source* field is the Region object that has invoked an action.
//
// If the target is a Pattern or a String object, the *match* field will be set
// to be the last match of the region class, the *screenImage* field will be set to be
// the captured screen image that was given to the vision engine to discover the match.
//
// TODO:
// If the target is a Region, a Match, or a Location object, the *match* field will be set
// to NULL, because the target location is explicitly specified in this case and no 
// visual matching is performed to find a match. The *screenImage*
// will be the screen image captured right before the action (e.g., click) was performed.
//

public class SikuliActionManager {

   static SikuliActionManager _instance;
   
   public static SikuliActionManager getInstance(){
      if (_instance == null){
         _instance = new SikuliActionManager();
      }
      return _instance;
   }
   
   public synchronized <PSRML> void clickTarget(Region source, PSRML target, ScreenImage screenImage, Match match){
      notifyListeners(new SikuliAction(SikuliAction.ActionType.CLICK, source, target, screenImage, match));
   }

   public synchronized <PSRML> void doubleClickTarget(Region source, PSRML target, ScreenImage screenImage, Match match){
      notifyListeners(new SikuliAction(SikuliAction.ActionType.DOUBLE_CLICK, source, target, screenImage, match));
   }

   public synchronized <PSRML> void rightClickTarget(Region source, PSRML target, ScreenImage screenImage, Match match){
      notifyListeners(new SikuliAction(SikuliAction.ActionType.RIGHT_CLICK, source, target, screenImage, match));
   }

   
   ArrayList<SikuliActionListener> _listeners;
   
   SikuliActionManager(){
      _listeners = new ArrayList<SikuliActionListener>();
   }
   
   public synchronized void addListener(SikuliActionListener l ) {
      _listeners.add(l);
  }
   
   public synchronized void removeListener(SikuliActionListener l ) {
      _listeners.remove(l);
  }
   
   private synchronized void notifyListeners(SikuliAction action) {
      for (SikuliActionListener listener : _listeners){
         if (action.getType() == SikuliAction.ActionType.CLICK){
            listener.targetClicked(action);
         }else if (action.getType() == SikuliAction.ActionType.DOUBLE_CLICK){
            listener.targetDoubleClicked(action);
         }

      }
  }



   
   




}
