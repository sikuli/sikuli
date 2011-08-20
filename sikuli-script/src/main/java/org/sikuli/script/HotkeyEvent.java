/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;


public class HotkeyEvent {
   public int keyCode;
   public int modifiers;

   public HotkeyEvent(int code_, int mod_){
      init(code_, mod_);
   }

   void init(int code_, int mod_){
      keyCode = code_;
      modifiers = mod_;
   }
}

