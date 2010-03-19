package edu.mit.csail.uid;

import java.awt.*;

public class Screen extends Region {
   private GraphicsDevice _curGD;

   static GraphicsDevice[] _gdev;
   static GraphicsEnvironment _genv;
   static{
      _genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
      _gdev = _genv.getScreenDevices();
   }



   public static int getNumberScreens(){
      return _gdev.length;
   }

   public Rectangle getBounds(){
      return _curGD.getDefaultConfiguration().getBounds();
   }

   public Screen(int id){
      if(id<_gdev.length)
         _curGD = _gdev[id];
      else
         _curGD = _genv.getDefaultScreenDevice();
      Rectangle bounds = getBounds();
      init((int)bounds.getX(), (int)bounds.getY(),
            (int)bounds.getWidth(), (int)bounds.getHeight());
   }

   public Screen(){
      _curGD = _genv.getDefaultScreenDevice();
      Rectangle bounds = getBounds();
      init((int)bounds.getX(), (int)bounds.getY(),
            (int)bounds.getWidth(), (int)bounds.getHeight());

   }
}
