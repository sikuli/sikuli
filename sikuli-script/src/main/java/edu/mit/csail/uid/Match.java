package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Match {
   int x, y;
   int w, h;
   double score;
   String parent;
   SikuliScript _script = null;

   public Match(int _x, int _y, int _w, int _h, double _score){
      x = _x; y = _y;
      w = _w; h = _h;
      score = _score;
   }

   public Match(Match m){
      x = m.x; y = m.y;
      w = m.w; h = m.h;
      score = m.score;
      parent = m.parent;
      _script = m._script;
   }

   public void setSikuliScript(SikuliScript script){
      _script = script;
   }

   public int getX(){ return x; }
   public int getY(){ return y; }
   public int getW(){ return w; }
   public int getH(){ return h; }
   public double getScore(){  return score; }
   public String getParent(){  return parent; }

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

   public <T> Match find(T img) throws IOException, AWTException, FindFailed{
      if(_script != null){
         String subimage = getSubimageFromParent();
         Matches subMatches = _script._find(img, subimage);
         if( subMatches.size() > 0)
            return toGlobalCord(subMatches.getFirst(), x, y);
      }
      return null;
   }

   public <T> Matches findAll(T img) throws IOException, AWTException, FindFailed{
      if(_script != null){
         String subimage = getSubimageFromParent();
         Matches subMatches = null;
         if(img instanceof String){
            Pattern pat = new Pattern((String)img).firstN(-1);
            subMatches = _script._find(pat, subimage);
         }
         else if(img instanceof Pattern){
            subMatches = _script._find(img, subimage);
         }
         if( subMatches != null && subMatches.size() > 0)
            return toGlobalCord(subMatches, x, y);
      }
      return null;
   }

   private String getSubimageFromParent() throws IOException{
      File fParent = new File(parent);
      BufferedImage parentImg = ImageIO.read(fParent);
      if( x + w >= parentImg.getWidth() )
         w -= (x + w - parentImg.getWidth());
      if( y + h >= parentImg.getHeight() )
         h -= (y + h - parentImg.getHeight());
      BufferedImage region = parentImg.getSubimage(x, y, w, h);
      File tmp = File.createTempFile("sikuli-region-",".png");
      tmp.deleteOnExit();
      ImageIO.write(region, "png", tmp);
      Debug.log(4, "region: " + tmp.getAbsolutePath());
      return tmp.getAbsolutePath();
   }

   private Matches toGlobalCord(Matches ms, int x, int y){
      for(Match m : ms){
         m.x += x;
         m.y += y;
      }
      return ms;
   }

   private Match toGlobalCord(Match m, int x, int y){
      m.x += x;
      m.y += y;
      return m;
   }


   public String toString(){
      return String.format("Match[%d,%d-%dx%d %.2f]@%s", x, y, w, h, score, parent);
   }
}

