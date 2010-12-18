package org.sikuli.script;

public class SikuliEvent {
   int type;
   int handler_id;
   int x, y, w, h;
   Region region;

   public SikuliEvent(){
   }

   public SikuliEvent(SikuliEvent se, Region r){
      type = se.type;
      handler_id = se.handler_id;
      x = se.x;
      y = se.y;
      w = se.w;
      h = se.h;
      region = r;
   }

   public Region getRegion(){
      return region;
   }

   public String toString(){
      return String.format("SikuliEvent(%d) [%d,%d %dx%d] region: %s", 
               type, x, y, w, h, region);
   }
}

