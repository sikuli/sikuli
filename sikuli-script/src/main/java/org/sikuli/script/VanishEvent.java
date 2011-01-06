package org.sikuli.script;

public class VanishEvent extends SikuliEvent {
   public VanishEvent(Object ptn, Match m, Region r){
      super(ptn, m, r);
      type = Type.VANISH;
   }
}

