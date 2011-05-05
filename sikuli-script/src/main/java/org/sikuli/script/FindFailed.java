/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

public class FindFailed extends SikuliException {
   public FindFailed(String msg){
      super(msg);
      _name = "FindFailed";
   }
}

