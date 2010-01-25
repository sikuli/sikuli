package edu.mit.csail.uid;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class SubregionJ {
   public int x, y, w, h;
   public Matches matches;
   SikuliScript _script = null;

   public SubregionJ(int x_, int y_, int w_, int h_, SikuliScript script){
      x = x_;
      y = y_;
      w = w_;
      h = h_;
      _script = script;
      Debug.log("SubregionJ: %d,%d,%d,%d %s",x,y,w,h,script);
   }

   private Matches toGlobalCord(Matches ms, int x, int y){
      for(Match m : ms){
         m.x += x;
         m.y += y;
      }
      return ms;
   }

   public SubregionJ inside(){
      return this;
   }

   public <T> Matches find(T img) throws IOException, AWTException, FindFailed{
      if(_script != null){
         String subregion = _script.captureScreen(x,y,w,h);
         Matches m = _script._find(img, subregion);
         matches = toGlobalCord(m, x, y);
         return matches;
      }
      return null;
   }
}

