package org.sikuli.script;

public class AppNotFound extends SikuliException {
   public AppNotFound(String msg){
      super(msg);
      _name = "AppNotFound";
   }
}


