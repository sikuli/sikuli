package org.sikuli.script;

import java.util.List;

public class SikuliEvent {
   public enum Type {
      APPEAR, VANISH, CHANGE
   }
   public Type type;

   //DEPRECATED (leave them to be compatiable to 0.10)
   public int x, y, w, h;

   public Region region;

   // AppearEvent must have a match
   // VanishEvent may have a match, depending on if the pattern appeared before
   public Match match;

   // ChangeEvent has 0+ changes.
   public List<Match> changes;

   // the pattern for observing this event
   public Object pattern;

   public SikuliEvent(){
   }


   public SikuliEvent(Object ptn, Match m, Region r){
      if(m != null){
         x = m.x;
         y = m.y;
         w = m.w;
         h = m.h;
      }
      region = r;
      match = m;
      pattern = ptn;
   }

   public Region getRegion(){
      return region;
   }

   public String toString(){
      return String.format("SikuliEvent(%s) on %s | %s | Last %s", 
               type, region, pattern, match);
   }
}

