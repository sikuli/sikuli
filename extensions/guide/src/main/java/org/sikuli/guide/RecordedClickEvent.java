/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

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
      this.clickLocation = new Point(clickLocation);
   }
   public Point getClickLocation() {
      return clickLocation;
   }
   
   public RecordedClickEvent(){
     
   }
   
   public String toString(){
      return "clicklocation: (" + clickLocation.x + "," + clickLocation.y + ")";
   }
   
   public static RecordedClickEvent[] importFromDirectory(File path) throws IOException{
      File files[] = path.listFiles(new FileFilter(){
         @Override
         public boolean accept(File pathname) {
            return pathname.getName().endsWith("png");
         }         
      });
      
      
      RecordedClickEvent[] es = new RecordedClickEvent[files.length];
      
      for (int i = 0 ; i < files.length; ++i){
         es[i] = new RecordedClickEvent(files[i]);
      }
      
      return es;
   }
   
   public RecordedClickEvent(File file) throws IOException{
      String filename = file.getName();
      String[] toks = filename.split("_");
      
      clickLocation = new Point(Integer.parseInt(toks[1]),Integer.parseInt(toks[2])); 
      screenImage = ImageIO.read(file);      
   }
   
   String encodeFilename(){
      Date d = new Date();
      return "capture" + "_" + clickLocation.x + "_" + clickLocation.y + "_" + d.getTime() + ".png";
   }
   
   public void export(){
      
      BufferedImage exportedImage = new BufferedImage(screenImage.getWidth(),screenImage.getHeight(),BufferedImage.TYPE_INT_RGB);
      Graphics2D g = (Graphics2D) exportedImage.getGraphics();
      g.drawImage(screenImage,0,0,null);
      
      // draw cursor
      int cursorSize = 10;
      Color cursorColor = Color.red;
      Ellipse2D.Double circle = new Ellipse2D.Double((double)clickLocation.x-cursorSize/2,(double)clickLocation.y-cursorSize/2,cursorSize,cursorSize);
      g.setColor(cursorColor);
      g.draw(circle);

      try {
         ImageIO.write(exportedImage, "png", new File("/Users/tomyeh/Desktop/captured" + encodeFilename()));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}