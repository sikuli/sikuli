package org.sikuli.script;

public class FindFailed extends SikuliException {
   public FindFailed(String msg){
      super(msg);
      _name = "FindFailed";
   }
}

