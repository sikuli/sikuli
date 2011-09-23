/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import org.sikuli.script.natives.FindResult;

public class Match extends Region implements Comparable {
   double score;
   String _text = null;

   private Location _target = null;

   public Match(int _x, int _y, int _w, int _h, double _score, IScreen _parent) {
      init(_x, _y, _w, _h, _parent);
      score = _score;
   }

   public Match(int _x, int _y, int _w, int _h, double _score, IScreen _parent, String text) {
      init(_x, _y, _w, _h, _parent);
      score = _score;
      _text = text;
   }


   public Match(Match m, IScreen _parent) {
      init(m.x, m.y, m.w, m.h, _parent);
      score = m.score;
   }

   public Match(FindResult f, IScreen _parent) {
      init(f.getX(), f.getY(), f.getW(), f.getH(), _parent);
      score = f.getScore();
   }

   public int compareTo(Object o){
      Match m = (Match)o;
      if(score != m.score)
         return score < m.score ? -1 : 1;
      if(x != m.x)
         return x - m.x;
      if(y != m.y)
         return y - m.y;
      if(w != m.w)
         return w - m.w;
      if(h != m.h)
         return h - m.h;
      if(equals(o))
         return 0;
      return -1;
   }

   public boolean equals(Object oThat) {
      if(this == oThat) return true;
      if( !(oThat instanceof Match) ) return false;
      Match that = (Match)oThat;
      return x == that.x && y == that.y && w == that.w && h == that.h && 
             Math.abs(score-that.score) < 1e-5 && getTarget().equals(that.getTarget());
   }
   

   public double getScore(){  return score; }

   public Location getTarget(){
      if(_target != null)
         return _target;
      return getCenter();
   }


   public String toString(){
      String target = "center";
      Location c = getCenter();
      if(_target != null && !c.equals(_target)) target = _target.toString();
      return String.format("Match[%d,%d %dx%d score=%.2f target=%s]", x, y, w, h, score, target);
   }

   void setTargetOffset(Location offset){
      _target = new Location(getCenter());
      _target.translate(offset.x, offset.y);
   }

   public String text(){
      if(_text==null)
         return super.text();
      return _text;
   }
}

