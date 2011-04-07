package org.sikuli.guide;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.sikuli.script.Debug;
import org.sikuli.script.ImageLocator;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class Tracker extends Thread{


   SikuliGuide guide;
   Pattern pattern;
   Region match;
   Screen screen;
   String image_filename;
   Pattern centerPattern;

   public Tracker(SikuliGuide guide, Pattern pattern, Region match){
      this.guide = guide;    
      this.match = match;
      screen = new Screen();

      BufferedImage image;
      BufferedImage center;
      this.pattern = pattern;
      try {
         image = pattern.getImage();
         int w = image.getWidth();
         int h = image.getHeight();
         center = image.getSubimage(w/4,h/4,w/2,h/2);         
         centerPattern = new Pattern(center);
      } catch (IOException e) {
         e.printStackTrace();
      }      
   }
   
//   public Tracker(SikuliGuide guide, String image_filename, Region match){
//      this.guide = guide;    
//      this.match = match;
//      screen = new Screen();
//      pattern = new Pattern(image_filename);
//      this.image_filename = image_filename;
//
//      // TODO: Sikuli needs to support searching for an image in memory
//      // so we don't need to use the temp file hack
//      File temp = null;
//
//      try {
//         ImageLocator imgLocator = new ImageLocator();
//         String full_image_filename = imgLocator.locate(image_filename);
//         
//         BufferedImage image = ImageIO.read(new File(full_image_filename));
//         int w = image.getWidth();
//         int h = image.getHeight();
//         BufferedImage center = image.getSubimage(w/4,h/4,w/2,h/2);
//
//
//
//         temp = File.createTempFile("ctr",".png");
//         temp.deleteOnExit();
//         ImageIO.write(center,"png",temp);
//      } catch (IOException ex) {
//         System.err.println("Cannot create temp file: " + ex.getMessage());
//      } finally {
//
//      }
//
//      Debug.log("temp: " + temp.getAbsolutePath());
//      centerPattern = new Pattern(temp.getAbsolutePath());
//
//
//   }

   
   //void init()

   ArrayList<SikuliGuideComponent> components = new ArrayList<SikuliGuideComponent>();
   ArrayList<Point> offsets = new ArrayList<Point>();

   public void addReferencingComponent(SikuliGuideComponent component) {
      Point loc = component.getLocation();
      Point offset = new Point(loc.x - match.x, loc.y - match.y);
      offsets.add(offset);
      components.add(component);
   }
   

   boolean running;
   public void run(){
      running = true;

      while (running){


         while (running){

            Region center = new Region(match);
            center.x += center.w/4-2;
            center.y += center.h/4-2;
            center.w = center.w/2+4;
            center.h = center.h/2+4;

            Match m = center.exists(centerPattern,0);

            if (m == null)
               break;

           // Debug.log("still there m: " + m);

            for (SikuliGuideComponent comp : components){
               comp.setVisible(true);
            }
            guide.repaint();
            
            try {
               sleep(100);
            } catch (InterruptedException e) {
            }

         }
         
         
         // try for at least 1.0 sec. to have a better chance of finding the
         // new position of the pattern.
         // the first attempt often fails because the target is only a few
         // pixels away when the screen capture is made and it is still
         // due to occlusion by foreground annotations      
         // however, it would mean it takes at least 1.0 sec to realize
         // the pattern has disappeared and the referencing annotations should
         // be hidden
         Match new_match = screen.exists(pattern,1.0);        
         
         if (new_match != null){

            //Debug.log("[Tracker] Pattern is found in a new location: " + new_match);

            if (match.x == new_match.x && match.y == new_match.y)
               continue;

            if (match != null){

               for (int i=0; i < components.size(); ++i){
                  SikuliGuideComponent comp  = components.get(i);
                  Point offset = offsets.get(i);

                  int dest_x = new_match.x + offset.x;
                  int dest_y = new_match.y + offset.y;
                  
                  comp.setEmphasisAnimation(comp.createMoveAnimator(dest_x, dest_y));                  
                  comp.startAnimation();
               }
            }


            match = new_match;

         } else {

            //Debug.log("[Tracker] Pattern has disappeared");

            for (SikuliGuideComponent comp : components){
               comp.setVisible(false);
            }
            guide.repaint();

         }



      }
   }

   public void stopTracking(){
      running = false;
   }

   public boolean isAlreadyTracking(Pattern pattern, Region match) {
      try {
         boolean sameMatch = this.match == match; 
         boolean sameBufferedImage = this.pattern.getImage() == pattern.getImage();
         boolean sameFilename = (this.pattern.getFilename() != null &&
               (this.pattern.getFilename().compareTo(pattern.getFilename()) == 0));
      
         return sameMatch || sameBufferedImage || sameFilename;
      } catch (IOException e) {
         return false;
      }
   }

}
