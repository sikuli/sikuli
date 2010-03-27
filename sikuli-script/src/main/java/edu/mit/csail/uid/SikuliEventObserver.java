package edu.mit.csail.uid;

import java.util.*;

class SikuliEvent {
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

   /*
   public static SikuliEvent create(int type_, int handler_id_, 
                             int x_, int y_, int w_, int h_){
      switch(type_){
         case EventManager.APPEAR:  
            return new AppearEvent(handler_id_, x_, y_, w_, h_);
         case EventManager.VANISH:  
            return new VanishEvent(handler_id_, x_, y_, w_, h_);
         case EventManager.CHANGE:  
            return new ChangeEvent(handler_id_, x_, y_, w_, h_);
      }
   }
   */
}

class AppearEvent extends SikuliEvent {
   public AppearEvent(SikuliEvent se){
      super(se);
   }
}

class VanishEvent extends SikuliEvent {
   public VanishEvent(SikuliEvent se){
      super(se);
   }
}

class ChangeEvent extends SikuliEvent {
   public ChangeEvent(SikuliEvent se){
      super(se);
   }
}

public class SikuliEventObserver implements EventListener {
   public void targetAppeared(AppearEvent e){
   }

   public void targetVanished(VanishEvent e){
   }

   public void targetChanged(ChangeEvent e){
   }
}
