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
         //matches[i].setSikuliScript(script);
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

}

