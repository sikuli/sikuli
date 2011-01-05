package org.sikuli.script;


public class ChangeEvent extends SikuliEvent {
   public ChangeEvent(Match m, Region r){
      super(null, m, r);
      type = Type.CHANGE;
   }
}
