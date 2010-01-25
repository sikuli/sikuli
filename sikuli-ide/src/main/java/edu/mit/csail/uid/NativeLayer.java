package edu.mit.csail.uid;

public interface NativeLayer {
   public void initIDE(SikuliIDE ide);
   public void installHotkey(int keyCode, int modifiers, 
                              SikuliIDE ide, 
                              String callbackMethod, String callbackType);
}

