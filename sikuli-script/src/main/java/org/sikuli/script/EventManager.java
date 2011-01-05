package org.sikuli.script;

import java.awt.*;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class EventManager {
   protected enum State {
      UNKNOWN, APPEARED, VANISHED
   }

   private Region _region;
   private Map<Object, State> _state;
   private Map<Object, Match> _lastMatch;
   private Map<Object, SikuliEventObserver> _appearOb, _vanishOb;
   //, _changeOb;


   public EventManager(Region region){
      _region = region;
      _state = new HashMap<Object, State>();
      _lastMatch = new HashMap<Object, Match>();
      _appearOb = new HashMap<Object, SikuliEventObserver>();
      _vanishOb = new HashMap<Object, SikuliEventObserver>();
//      _changeOb = new HashMap<Object, SikuliEventObserver>();
   }

   private <PSC> float getSimiliarity(PSC ptn){
      float similarity=-1f;
      if( ptn instanceof Pattern ){
         similarity=((Pattern)ptn).similarity;
      }
      if(similarity<0) {
         similarity=(float)Settings.MinSimilarity;
      }
      return similarity;
   }

   public <PSC> void addAppearObserver(PSC ptn, SikuliEventObserver ob){
      _appearOb.put(ptn, ob);
      _state.put(ptn, State.UNKNOWN);
   }

   public <PSC> void addVanishObserver(PSC ptn, SikuliEventObserver ob){
      _vanishOb.put(ptn, ob);
      _state.put(ptn, State.UNKNOWN);
   }

   //FIXME add paramater: change threshold
   public void addChangeObserver(SikuliEventObserver ob){
      //_changeOb.put(ptn, ob);
   }

   protected void callAppearObserver(Object ptn, Match m){
      AppearEvent se = new AppearEvent(ptn, m, _region);
      SikuliEventObserver ob = _appearOb.get(ptn);
      ob.targetAppeared(se);
   }

   protected void callVanishObserver(Object ptn, Match m){
      VanishEvent se = new VanishEvent(ptn, m, _region);
      SikuliEventObserver ob = _vanishOb.get(ptn);
      ob.targetVanished(se);
   }


   protected void checkPatterns(ScreenImage img){
      Finder finder = new Finder(img, _region);
      for(Object ptn : _state.keySet()){
         try{
            finder.find(ptn);
            Match m = null;
            boolean hasMatch = false;
            if(finder.hasNext()){
               m = finder.next();
               if(m.getScore() >= getSimiliarity(ptn)){
                  hasMatch = true;
                  _lastMatch.put(ptn, m);
               }
            }
            Debug.log(9, "check pattern: " + _state.get(ptn) + " match:" + hasMatch);
            if(_appearOb.containsKey(ptn)){
               if(_state.get(ptn) != State.APPEARED && hasMatch)
                  callAppearObserver(ptn, m);
            }
            if(_vanishOb.containsKey(ptn)){
               if(_state.get(ptn) != State.VANISHED && !hasMatch){
                  callVanishObserver(ptn, _lastMatch.get(ptn));
               }
            }
            if(hasMatch)
               _state.put(ptn, State.APPEARED);
            else
               _state.put(ptn, State.VANISHED);
         }
         catch(IOException e){
            Debug.error("Can't access "+ ptn +"\n" + e.getMessage()); 
         }
      }

   }


   public void update(ScreenImage img){
      checkPatterns(img);
   }

   protected void finalize() throws Throwable {
   }


   public void dispose(){
   }

}


