package org.sikuli.script;


import java.awt.Robot;
import java.awt.GraphicsDevice;
import java.awt.AWTException;

public class SikuliRobot extends Robot {
   final static int MAX_DELAY = 60000;

   public SikuliRobot(GraphicsDevice screen) throws AWTException{
      super(screen);
   
   }
   
   public void delay(int ms){
      if(ms<0)
         ms = 0;
      while(ms>MAX_DELAY){
         super.delay(MAX_DELAY);
         ms -= MAX_DELAY;
      }
      super.delay(ms);
   }
}
