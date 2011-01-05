package org.sikuli.script;

public class AppearEvent extends SikuliEvent {

   public AppearEvent(Object ptn, Match m, Region r){
      super(ptn, m, r);
      type = Type.APPEAR;
   }

}
