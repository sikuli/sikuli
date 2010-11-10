package edu.mit.csail.uid;

public class AppNotFound extends SikuliException {
   public AppNotFound(String msg){
      super(msg);
      _name = "AppNotFound";
   }
}


