package edu.mit.csail.uid;

public class FindFailed extends SikuliException {
   public FindFailed(String msg){
      super(msg);
      _name = "FindFailed";
   }
}

