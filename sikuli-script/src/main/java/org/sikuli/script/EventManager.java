/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.awt.AWTException;

import org.sikuli.script.natives.FindInput;
import org.sikuli.script.natives.FindResult;
import org.sikuli.script.natives.FindResults;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.Vision;

public class EventManager {
   protected enum State {
      UNKNOWN, APPEARED, VANISHED
   }

   private Region _region;
   private Mat _lastImgMat = null;
   private Map<Object, State> _state;
   private Map<Object, Match> _lastMatch;
   private Map<Object, SikuliEventObserver> _appearOb, _vanishOb;
   private Map<Integer, SikuliEventObserver> _changeOb;
   private int _minChanges;


   public EventManager(Region region){
      _region = region;
      _state = new HashMap<Object, State>();
      _lastMatch = new HashMap<Object, Match>();
      _appearOb = new HashMap<Object, SikuliEventObserver>();
      _vanishOb = new HashMap<Object, SikuliEventObserver>();
      _changeOb = new HashMap<Integer, SikuliEventObserver>();
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

   public void addChangeObserver(int threshold, SikuliEventObserver ob){
      _changeOb.put(new Integer(threshold), ob);
      _minChanges = getMinChanges();
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

   protected void callChangeObserver(FindResults results) throws AWTException{
      for(Integer n : _changeOb.keySet()){
         List<Match> changes = new ArrayList<Match>();
         for(int i=0;i<results.size();i++){
            FindResult r = results.get(i);
            if( r.getW() * r.getH() >= n )
               changes.add(_region.toGlobalCoord(new Match(r, _region.getScreen())));
         }
         if(changes.size() > 0){
            ChangeEvent se = new ChangeEvent(changes, _region);
            SikuliEventObserver ob = _changeOb.get(n);
            ob.targetChanged(se);
         }
      }
   }

   protected int getMinChanges(){
      int min = Integer.MAX_VALUE;
      for(Integer n : _changeOb.keySet())
         if(n < min)
            min = n;
      return min;
   }

   protected void checkChanges(ScreenImage img){
      if(_lastImgMat == null){
         _lastImgMat = OpenCV.convertBufferedImageToMat(img.getImage());
         return;
      }

      FindInput fin = new FindInput();
      fin.setSource(_lastImgMat);
      Mat target = OpenCV.convertBufferedImageToMat(img.getImage());
      fin.setTarget(target);
      fin.setSimilarity(_minChanges);

      FindResults results = Vision.findChanges(fin);
      try{
         callChangeObserver(results);
      }
      catch(AWTException e){
         e.printStackTrace();
      }

      _lastImgMat = target;
   }

   public void update(ScreenImage img){
      checkPatterns(img);
      if(_changeOb.size()>0)
         checkChanges(img);
   }

   protected void finalize() throws Throwable {
   }


   public void dispose(){
   }

}


