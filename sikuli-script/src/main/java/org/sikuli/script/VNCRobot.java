/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;


import java.awt.Robot;
import java.awt.GraphicsDevice;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public class VNCRobot implements IRobot{
   final static int MAX_DELAY = 60000;
   long client;
   int x,y,buttons;

   class VNCRobotLoop implements Runnable {
      long client;
    
      public VNCRobotLoop(long client) { this.client=client; }
      public void run() {
         while(true) {
            if(VNCNative.WaitForMessage(client,50)<0)
               break;
            if(!VNCNative.HandleRFBServerMessage(client))
               break;
         }
      }

      public void NewLoop() {
         (new Thread(this)).start();
      }
   }

   public VNCRobot(String[] args,String password) {
      x=0; y=0; buttons=0;
      client=VNCNative.rfbGetClient(8,3,4);
      VNCNative.rfbInitClient(client,args,password);
      VNCRobotLoop loop=new VNCRobotLoop(client);
      loop.NewLoop();
   }

   public GraphicsDevice getGraphicsDevice() { return null; }
   public Rectangle getBounds() {
      Rectangle rect=new Rectangle(
         0,0,
         VNCNative.GetWidth(client),
         VNCNative.GetHeight(client)
         );
      return rect;
   }


   public void mouseMove(int x, int y) {
      this.x=x;
      this.y=y;
      VNCNative.SendPointerEvent(client,x,y,this.buttons);
   }
   public void mousePress(int buttons) {
      if((buttons&Button.LEFT)>0)  this.buttons|=1;
      if((buttons&Button.MIDDLE)>0)  this.buttons|=2;
      if((buttons&Button.RIGHT)>0)  this.buttons|=4;
      VNCNative.SendPointerEvent(client,x,y,this.buttons);
   }
   public void mouseRelease(int buttons) {
      if((buttons&Button.LEFT)>0)  this.buttons&=-1^1;
      if((buttons&Button.MIDDLE)>0)  this.buttons&=-1^2;
      if((buttons&Button.RIGHT)>0)  this.buttons&=-1^4;
      VNCNative.SendPointerEvent(client,x,y,this.buttons);
   }
   public void keyPress(int c) {
      VNCNative.SendKeyEvent(client,c,true);
   }
   public void keyRelease(int c) {
      VNCNative.SendKeyEvent(client,c,false);
   }
   public void mouseWheel(int move) {
      int wheelButton=move<0?8:16;
      this.buttons|=wheelButton;
      VNCNative.SendPointerEvent(client,x,y,this.buttons);
      try {
         Thread.sleep(20);
      } catch(InterruptedException e) { e.printStackTrace(); }
      this.buttons&=-1^wheelButton;
      VNCNative.SendPointerEvent(client,x,y,this.buttons);
   }
   public void waitForIdle() {
   }
   public void delay(long msecs) {
      try {
         Thread.sleep(msecs);
      } catch(InterruptedException e) {
         e.printStackTrace();
      }
   }
   public void setAutoDelay(int msecs) {
   }

   public void smoothMove(Location dest){
      smoothMove(Env.getMouseLocation(), dest, (long)(Settings.MoveMouseDelay*1000L));
   }


   public void smoothMove(Location src, Location dest, long ms){
      if(ms == 0){
         mouseMove(dest.x, dest.y);
         return;
      }

      Animator aniX = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.x, (float)dest.x, ms));
      Animator aniY = new TimeBasedAnimator(
                        new OutQuarticEase((float)src.y, (float)dest.y, ms));
      while(aniX.running()){
         float x = aniX.step();
         float y = aniY.step();
         mouseMove((int)x, (int)y);
         delay(50);
      }
   }

   public void dragDrop(Location start, Location end, int steps, long ms, int buttons){
      mouseMove(start.x, start.y);
      mousePress(buttons);
      delay((int)(Settings.DelayAfterDrag*1000));
      waitForIdle();
      smoothMove(start, end, ms);
      delay((int)(Settings.DelayBeforeDrop*1000));
      mouseRelease(buttons);
      waitForIdle();
   }

   
   public void delay(int ms){
      try {
         if(ms<0)
            ms = 0;
         while(ms>MAX_DELAY){
            Thread.sleep(MAX_DELAY);
            ms -= MAX_DELAY;
         }
         Thread.sleep(ms);
      } catch(InterruptedException e) {
         e.printStackTrace();
      }
   }

   public ScreenImage captureScreen(Rectangle rect){
      int sz=rect.width*rect.height;
      int[] data=new int[sz];

      VNCNative.CopyScreenToData(client,data,rect.x,rect.y,rect.width,rect.height);
      BufferedImage img = new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
      DataBufferInt bufferBytes=new DataBufferInt(data,sz);
      int bitMask[] = new int[]{0xff,0xff00,0xff0000};
      SinglePixelPackedSampleModel sm=new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT,rect.width,rect.height,bitMask);
      img.setData(Raster.createRaster(sm,bufferBytes,null));
      return new ScreenImage(rect,img);
   }


   public void pressModifiers(int modifiers){
      if((modifiers & KeyModifier.SHIFT) != 0) keyPress(KeyEvent.VK_SHIFT);
      if((modifiers & KeyModifier.CTRL) != 0) keyPress(KeyEvent.VK_CONTROL);
      if((modifiers & KeyModifier.ALT) != 0) keyPress(KeyEvent.VK_ALT);
      if((modifiers & KeyModifier.META) != 0){
         if( Env.getOS() == OS.WINDOWS )
            keyPress(KeyEvent.VK_WINDOWS);
         else
            keyPress(KeyEvent.VK_META);
      }
   }

   public void releaseModifiers(int modifiers){
      if((modifiers & KeyModifier.SHIFT) != 0) keyRelease(KeyEvent.VK_SHIFT);
      if((modifiers & KeyModifier.CTRL) != 0) keyRelease(KeyEvent.VK_CONTROL);
      if((modifiers & KeyModifier.ALT) != 0) keyRelease(KeyEvent.VK_ALT);
      if((modifiers & KeyModifier.META) != 0){ 
         if( Env.getOS() == OS.WINDOWS )
            keyRelease(KeyEvent.VK_WINDOWS);
         else
            keyRelease(KeyEvent.VK_META);
      }
   }

   protected void doType(KeyMode mode, int... keyCodes) {
      if(mode==KeyMode.PRESS_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            keyPress(keyCodes[i]);
         }
      }
      else if(mode==KeyMode.RELEASE_ONLY){
         for(int i=0;i<keyCodes.length;i++){
            keyRelease(keyCodes[i]);
         }
      }
      else{
         for(int i=0;i<keyCodes.length;i++)
            keyPress(keyCodes[i]);
         for(int i=0;i<keyCodes.length;i++)
            keyRelease(keyCodes[i]);
      }
   }

   public void typeChar(char character, KeyMode mode) {
      doType(mode, character);
   }

   public Object getDevice(){
      return null;
   }

}

// vim: set smartindent expandtab ts=3 sw=3:
