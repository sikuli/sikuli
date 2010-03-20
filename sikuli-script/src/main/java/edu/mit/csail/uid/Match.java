package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Match extends Region {
   double score;

   private Location _target = null;

   public Match(int _x, int _y, int _w, int _h, double _score) 
                                                            throws AWTException{
      init(_x, _y, _w, _h);
      score = _score;
   }

   public Match(Match m) throws AWTException{
      init(m.x, m.y, m.w, m.h);
      score = m.score;
   }

   public double getScore(){  return score; }

   public Location getTarget(){
      if(_target != null)
         return _target;
      return getCenter();
   }


   /*
   public Match nearby(){
      final int PADDING = 50;
      return nearby(PADDING);
   }

   public Match nearby(int range){
      Match r = new Match(this);
      r.x = x<range? 0 : x-range;
      r.y = y<range? 0 : y-range;
      r.w += range*2; 
      r.h += range*2;
      return r;
   }

   public Match right(){
      Match r = new Match(this);
      r.x = x+w;
      r.y = y;
      r.w = 9999999;
      r.h = h;
      return r;
   }

   public Match left(){
      Match r = new Match(this);
      r.x = 0;
      r.y = y;
      r.w = x;
      r.h = h;
      return r;
   }

   public Match top(){
      Match r = new Match(this);
      r.x = x;
      r.y = 0;
      r.w = w;
      r.h = y;
      return r;
   }

   public Match bottom(){
      Match r = new Match(this);
      r.x = x;
      r.y = y+h;
      r.w = w;
      r.h = 9999999;
      return r;
   }

   public Match inside(){  
      return this;
   }
   */

   public String toString(){
      return String.format("Match[%d,%d-%dx%d %.2f]", x, y, w, h, score);
   }

   void setTargetOffset(Location offset){
      _target = new Location(getCenter());
      _target.translate(offset.x, offset.y);
   }
}

