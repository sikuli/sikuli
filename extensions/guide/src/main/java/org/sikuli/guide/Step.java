package org.sikuli.guide;

import java.awt.Point;
import java.util.ArrayList;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;


public class Step {
   ArrayList<Part> contentParts = new ArrayList<Part>();
   Transition transition;
   
   public Transition getTransition() {
      return transition;
   }



   public void setTransition(Transition transition) {
      this.transition = transition;
   }



   public void addPart(Part part){
      contentParts.add(part);
   }
   
   
   
   public void play(SikuliGuide guide) throws FindFailed{
      Screen s = new Screen();
      
      for (Part part : contentParts){
         
         Pattern pattern = part.getTargetPattern();
         
         Match m = s.find(pattern);
         
         
         SikuliGuideRectangle rect = new SikuliGuideRectangle(m);
         guide.addComponent(rect);

         guide.addTracker(pattern, m, rect);
         
         Point o = part.getTargetOrigin();
         
         for (SikuliGuideComponent comp : part.getAnnotationComponents()){
            
            //TODO remove scale
            Point loc = comp.getLocation();
            loc.x = (int) ((loc.x - o.x)/EditorWindow.SCALE + m.x);
            loc.y = (int) ((loc.y - o.y)/EditorWindow.SCALE + m.y);
            comp.setLocation(loc);
            
            rect.addFollower(comp);
            
            guide.addComponent(comp);
         }
         
      }

      guide.setTransition(transition);
      guide.showNow();
   }
         
}
