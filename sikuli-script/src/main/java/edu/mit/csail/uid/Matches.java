package edu.mit.csail.uid;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Matches extends LinkedList<Match>{
   SikuliScript _script = null;

   public Matches(){
   }

   public Matches(Match[] matches, SikuliScript script){
      for(int i=0;i<matches.length;i++){
         matches[i].setSikuliScript(script);
         this.add(matches[i]);
      }
      _script = script;
   }

   public Matches(Match[] matches){
      for(int i=0;i<matches.length;i++){
         this.add(matches[i]);
      }
   }

   public Matches addAll(Matches m){
      super.addAll(m);
      return this;
   }

   public Matches nearby(){
      final int PADDING = 50;
      for(Match m : this){
         m.x = m.x<PADDING? 0 : m.x-PADDING;
         m.y = m.y<PADDING? 0 : m.y-PADDING;
         m.w += PADDING*2; 
         m.h += PADDING*2;
      }
      return this;
   }

   public <T> Matches find(T img) throws IOException, AWTException, FindFailed{
      Matches ret = new Matches();
      if(_script != null && size()>0){
         File fParent = new File(getFirst().parent);
         BufferedImage parentImg = ImageIO.read(fParent);
         for(Match m : this){
            if( m.x + m.w >= parentImg.getWidth() )
               m.w -= (m.x + m.w - parentImg.getWidth());
            if( m.y + m.h >= parentImg.getHeight() )
               m.h -= (m.y + m.h - parentImg.getHeight());
            BufferedImage region = parentImg.getSubimage(m.x, m.y, m.w, m.h);
            File tmp = File.createTempFile("sikuli-region",".png");
            //tmp.deleteOnExit();
            ImageIO.write(region, "png", tmp);
            System.out.println("region: " + tmp.getAbsolutePath());
            Matches subMatches = _script._find(img, tmp.getAbsolutePath());
            ret.addAll(toGlobalCord(subMatches, m.x, m.y));
            //TODO: filter redundant matches
         }
      }
      return ret;
   }

   private Matches toGlobalCord(Matches ms, int x, int y){
      for(Match m : ms){
         m.x += x;
         m.y += y;
      }
      return ms;
   }
}

