package edu.mit.csail.uid;

import java.util.*;

class AppearEvent extends EventObject {
   public AppearEvent(Object source){
      super(source);
   }
}

class VanishEvent extends EventObject {
   public VanishEvent(Object source){
      super(source);
   }
}

class ChangeEvent extends EventObject {
   public ChangeEvent(Object source){
      super(source);
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
