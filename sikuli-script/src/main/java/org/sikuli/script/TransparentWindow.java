/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import javax.swing.JFrame;
import javax.swing.JWindow;

public class TransparentWindow extends JWindow {
   
   public void setOpacity(float alpha){
      Env.getOSUtil().setWindowOpacity(this, alpha);
   }
   
   public void close(){
      setVisible(false);
      dispose();
   }
   

}
