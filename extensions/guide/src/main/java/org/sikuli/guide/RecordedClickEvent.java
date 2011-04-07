/**
 * 
 */
package org.sikuli.guide;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class RecordedClickEvent {         
   private BufferedImage screenImage;
   private Point clickLocation;
   
   public void setScreenImage(BufferedImage screenImage) {
      this.screenImage = screenImage;
   }
   public BufferedImage getScreenImage() {
      return screenImage;
   }
   public void setClickLocation(Point clickLocation) {
      this.clickLocation = clickLocation;
   }
   public Point getClickLocation() {
      return clickLocation;
   }         
}