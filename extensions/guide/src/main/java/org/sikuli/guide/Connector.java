/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;



public class Connector extends SikuliGuideArrow {

   public Connector(SikuliGuideComponent source, SikuliGuideComponent destination){
      super(source, destination);
      //      setForeground(Color.white);
      //      sourceComponent = source;
      //      destinationComponent = destination;
   }      

   @Override
   protected void updateBounds() {
      super.updateBounds();

      Point src = getSource();
      Point dest = getDestination();
      
      int dx = src.x - dest.x;
      int dy = src.y - dest.y;
      if (Math.abs(dx) < Math.abs(dy))
         setStyle(ELBOW_X);
      else
         setStyle(ELBOW_Y);
         
      
   }
}