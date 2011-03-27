/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SikuliGuideImage extends SikuliGuideComponent {
   
   BufferedImage image;
   float scale;
   int w,h;
   
   public SikuliGuideImage(String filename) throws IOException{
       super();
       File sourceimage = new File(filename);
       BufferedImage bimage = ImageIO.read(sourceimage);
       init(bimage);
   }
   
   public SikuliGuideImage(BufferedImage image){
      super();
      init(image);
   }
   
   void init(BufferedImage image){
      this.image = image;
      setScale(1.0f);      
   }
   
   public void setScale(float scale){
      this.scale = scale;
      w = (int) (scale*image.getWidth());
      h = (int) (scale*image.getHeight());
      setSize(new Dimension(w,h));
   }
      
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      
      g2d.drawImage(image, 0, 0, w, h, null); 
      g2d.drawRect(0,0,w-1,h-1);
   }
   
}