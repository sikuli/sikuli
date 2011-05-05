/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import javax.swing.JWindow;

public interface OSUtil {
   // Windows: returns PID, 0 if fails
   // Others: return 0 if succeeds, -1 if fails
   public int openApp(String appName);

   // Windows: returns PID, 0 if fails
   // Others: return 0 if succeeds, -1 if fails
   public int switchApp(String appName);
   public int switchApp(String appName, int winNum);

   //internal use
   public int switchApp(int pid, int num);

   // returns 0 if succeeds, -1 if fails
   public int closeApp(String appName);

   //internal use
   public int closeApp(int pid);

   public Region getWindow(String appName);
   public Region getWindow(String appName, int winNum);
   Region getWindow(int pid);
   Region getWindow(int pid, int winNum);

   public Region getFocusedWindow();

   public void setWindowOpacity(JWindow win, float alpha);
   public void setWindowOpaque(JWindow win, boolean opaque);
   public void bringWindowToFront(JWindow win, boolean ignoreMouse);
}

