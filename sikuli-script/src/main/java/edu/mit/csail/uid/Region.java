package edu.mit.csail.uid;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class Region {
   private ScreenCapturer _capturer;

   public int x, y, w, h;

   public Region(int x_, int y_, int w_, int h_){
      init(x_,y_,w_,h_);
   }

   protected Region(){}

   protected void init(int x_, int y_, int w_, int h_){
      x = x_;
      y = y_;
      w = w_;
      h = h_;
   }

   /**
    * Match find( Pattern/String/PatternClass ) 
    * finds the given pattern on the screen and returns the best match.
    */
   public <T> Match find(T ptn) throws AWTException, FindFailed{
      ScreenImage simg = getCapturer().capture();
      Finder f = new Finder(simg);
      Match ret = null;
      if( ptn instanceof Pattern ){
         Pattern p = (Pattern)ptn;
         f.find(p.imgURL, p.similarity);
      }
      else
         f.find((String)ptn);
      if(f.hasNext())
         ret = toGlobalCord(f.next());
      f.destroy();
      return ret;
   }

   protected Match toGlobalCord(Match m){
      m.x += x;
      m.y += y;
      return m;
   }

   private ScreenCapturer getCapturer() throws AWTException{
      if(_capturer == null)
         _capturer = new ScreenCapturer(x, y, w, h);
      return _capturer;
   }
}


