/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

public class AppNotFound extends SikuliException {
   public AppNotFound(String msg){
      super(msg);
      _name = "AppNotFound";
   }
}


