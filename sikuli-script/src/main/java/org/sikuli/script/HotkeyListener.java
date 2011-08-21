/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import javax.swing.SwingUtilities;

public abstract class HotkeyListener {
   /**
    *  Override this to implement your own hotkey handler.
    */
   abstract public void hotkeyPressed(HotkeyEvent e);

   /**
    *  Only used Sikuli's internal code
    */
   public void invokeHotkeyPressed(final HotkeyEvent e){
      Thread hotkeyThread = new Thread(){
         public void run() {
            hotkeyPressed(e);
         }
      };
      hotkeyThread.start();
   }
}
