package org.sikuli.guide;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;


public class Step {
   
   public interface StepListener{
      void thumbnailRefreshed(Step step);
   }
   
   private ArrayList<Part> contentParts = new ArrayList<Part>();
   Transition transition;
   private BufferedImage screenImage;   
   private BufferedImage thumbnailImage;
   private StepView stepView;

   public Transition getTransition() {
      return transition;
   }

   public void setTransition(Transition transition) {
      this.transition = transition;
   }

   public void addPart(Part part){
      getParts().add(part);
   }
   
   public void clear(){
      getParts().clear();
      transition = null;
   }
   
   public void play(SikuliGuide guide) throws FindFailed{
      Screen s = new Screen();
      
      for (Part part : getParts()){
         
         Pattern pattern = part.getTargetPattern();
         
         Match m = s.find(pattern);
         
         
         SikuliGuideRectangle rect = new SikuliGuideRectangle(m);
         guide.addToFront(rect);

         guide.addTracker(pattern, m, rect);
         
         Point o = part.getTargetOrigin();
         
         for (SikuliGuideComponent comp : part.getAnnotationComponents()){
            
            //TODO remove scale
            Point loc = comp.getLocation();
            loc.x = (int) ((loc.x - o.x)/EditorWindow.SCALE + m.x);
            loc.y = (int) ((loc.y - o.y)/EditorWindow.SCALE + m.y);
            comp.setLocation(loc);
            
            rect.addFollower(comp);
            
            guide.addToFront(comp);
         }
         
      }

      guide.addTransition(transition);
      guide.showNow();
   }


   public ArrayList<Part> getParts() {
      return contentParts;
   }



   public void setScreenImage(BufferedImage screenImage) {
      this.screenImage = screenImage;
   }



   public BufferedImage getScreenImage() {
      return screenImage;
   }

   public void setThumbnailImage(BufferedImage thumbnailImage) {
      this.thumbnailImage = thumbnailImage;
   }

   public BufferedImage getThumbnailImage() {
      if (thumbnailImage == null)
         thumbnailImage = stepView.createForegroundThumbnail(200,150);
      return thumbnailImage;
   }

   public void setView(StepView stepView) {
      this.stepView = stepView;
   }

   public StepView getView() {
      return stepView;
   }

   public StepListener listener;
   public void refreshThumbnailImage() {
      thumbnailImage = stepView.createForegroundThumbnail(200,150);
      if (listener!=null)
         listener.thumbnailRefreshed(this);
   }

         
}
