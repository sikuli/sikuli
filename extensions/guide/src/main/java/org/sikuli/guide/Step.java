package org.sikuli.guide;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;


public class Step {
   private ArrayList<Part> contentParts = new ArrayList<Part>();
   Transition transition;
   private BufferedImage screenImage;   
   private BufferedImage thumbnailImage;
   private StepView stepView;
   private int index;
   
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
      return thumbnailImage;
   }

   public void setView(StepView stepView) {
      this.stepView = stepView;
   }

   public StepView getView() {
      return stepView;
   }

   public void refreshThumbnailImage() {
      thumbnailImage = stepView.createForegroundThumbnail(200,150);      
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public int getIndex() {
      return index;
   }

         
}
