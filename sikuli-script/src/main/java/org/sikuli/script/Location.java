package org.sikuli.script;

import java.awt.Point;

public class Location extends Point{
   public Location(int _x, int _y){
      super(_x, _y); 
   }

   public Location(Location loc){
      super(loc.x, loc.y);
   }

   public Location offset(int dx, int dy){
      return new Location(x+dx, y+dy);
   }

   public Location left(int dx){
      return new Location(x-dx, y);
   }

   public Location right(int dx){
      return new Location(x+dx, y);
   }

   public Location above(int dy){
      return new Location(x, y-dy);
   }

   public Location below(int dy){
      return new Location(x, y+dy);
   }
   
   public String toString(){
      return "(" + x + "," + y + ")";
   }
}

