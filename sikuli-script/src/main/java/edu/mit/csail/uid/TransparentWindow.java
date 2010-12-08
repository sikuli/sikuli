package edu.mit.csail.uid;

import javax.swing.JWindow;

public class TransparentWindow extends JWindow {
   public void setOpacity(float alpha){
      if( Env.getOS() == OS.MAC )
         getRootPane().putClientProperty("Window.alpha", new Float(alpha));
      else if( Env.getOS() == OS.WINDOWS )
         Win32Util.setWindowOpacity(this, alpha);

   }
}
