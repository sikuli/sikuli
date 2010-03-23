package edu.mit.csail.uid;

import java.awt.Point;

public class Location extends Point{
   public Location(int _x, int _y){
      super(_x, _y); 
   }

   public Location(Location loc){
      super(loc.x, loc.y);
   }

   public Location offset(int dx, int dy){
      translate(dx, dy);
      return this;
   }

   public String toString(){
      return "(" + x + "," + y + ")";
   }
}

