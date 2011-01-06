package org.sikuli.script;

import java.util.List;

public class ChangeEvent extends SikuliEvent {
   public ChangeEvent(List<Match> results, Region r){
      type = Type.CHANGE;
      changes = results;
      region = r;
   }

   public String toString(){
      return String.format("ChangeEvent on %s | %d changes", 
               region, changes.size());
   }
}
