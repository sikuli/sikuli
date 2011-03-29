/**
 * 
 */
package org.sikuli.guide;

import java.awt.Point;
import java.util.ArrayList;

import org.sikuli.script.Pattern;

public class Part {
   public Part(Pattern targetPattern){
      this.targetPattern = targetPattern;
      targetOrigin = new Point(0,0);
   }
   
   public Pattern getTargetPattern(){
      return targetPattern;
   }
   
   public ArrayList<SikuliGuideComponent> getAnnotationComponents(){
      return annotationComponents;
   }
   
   public void addComponent(SikuliGuideComponent comp){
      annotationComponents.add(comp);
   }
   
   public void setTargetOrigin(Point targetOrigin) {
      this.targetOrigin = targetOrigin;
   }

   public Point getTargetOrigin() {
      return targetOrigin;
   }

   private Point targetOrigin;
   
   Pattern targetPattern;   
   ArrayList<SikuliGuideComponent> annotationComponents = new
      ArrayList<SikuliGuideComponent>();
      
}