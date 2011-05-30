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

   private Location _target = null;

   public Match(int _x, int _y, int _w, int _h, double _score, IScreen _parent) 
                                                            throws AWTException{
      init(_x, _y, _w, _h, _parent);
      score = _score;
   }

   public Match(Match m, IScreen _parent) throws AWTException{
      init(m.x, m.y, m.w, m.h, _parent);
      score = m.score;
   }

   public Match(FindResult f, IScreen _parent) throws AWTException{
      init(f.getX(), f.getY(), f.getW(), f.getH(), _parent);
      score = f.getScore();
   }

   public int compareTo(Object o){
      return getScore() < ((Match)o).getScore() ? -1 :
             getScore() > ((Match)o).getScore() ? 1 : 0;
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
      return String.format("Match[%d,%d %dx%d] score=%.2f, target=%s", x, y, w, h, score, target);
   }

   void setTargetOffset(Location offset){
      _target = new Location(getCenter());
      _target.translate(offset.x, offset.y);
   }
}

