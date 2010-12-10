package edu.mit.csail.uid;

import javax.swing.JWindow;

public interface OSUtil {
   public int switchApp(String appName);
   public int openApp(String appName);
   public int closeApp(String appName);
   public Region getWindow(String appName);
   public Region getWindow(String appName, int winNum);
   public Region getFocusedWindow();
   public void setWindowOpacity(JWindow win, float alpha);
   public void setWindowOpaque(JWindow win, boolean opaque);
   public void bringWindowToFront(JWindow win, boolean ignoreMouse);
}

