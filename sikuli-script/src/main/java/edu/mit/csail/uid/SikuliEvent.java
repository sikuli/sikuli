package edu.mit.csail.uid;

public class SikuliEvent {
   int type;
   int handler_id;
   int x, y, w, h;

   public SikuliEvent(){
   }

   public SikuliEvent(SikuliEvent se){
      type = se.type;
      handler_id = se.handler_id;
      x = se.x;
      y = se.y;
      w = se.w;
      h = se.h;
   }

   public String toString(){
      return String.format("SikuliEvent(%d) [%d,%d %dx%d] handler: %d", 
               type, x, y, w, h, handler_id);
   }
}

