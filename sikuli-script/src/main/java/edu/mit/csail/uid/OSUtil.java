package edu.mit.csail.uid;

public interface OSUtil {
   public int switchApp(String appName);
   public int openApp(String appName);
   public int closeApp(String appName);
   public Region getWindow(String appName);
   public Region getWindow(String appName, int winNum);
   public Region getFocusedWindow();
}

