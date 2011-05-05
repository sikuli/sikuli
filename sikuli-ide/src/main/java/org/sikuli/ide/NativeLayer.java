/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

public interface NativeLayer {
   public void initApp();
   public void initIDE(SikuliIDE ide);
   public void installHotkey(int keyCode, int modifiers, 
                              SikuliIDE ide, 
                              String callbackMethod, String callbackType);
}

