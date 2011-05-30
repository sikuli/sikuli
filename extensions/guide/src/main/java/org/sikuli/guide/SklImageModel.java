package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sikuli.script.Debug;
import org.sikuli.script.ImageLocator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class SklImageModel extends DefaultSklObjectModel {
   
   public SklImageModel(){
      super();
   }
   
   public SklImageModel(String imageUrl) {
      super();
      setImageUrl(imageUrl);
   }
   
   @Attribute
   private
   String imageUrl;
   
   @Override
   public SklObjectView createView(){
      return new SklImageView((SklImageModel) this);
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      getImage();
   }

   public String getImageUrl(){
      return imageUrl;
   }
   
   public void setImage(BufferedImage image){
      this.image = image;
      setWidth(image.getWidth());
      setHeight(image.getHeight());
   }
   
   String bundlePath;
   
   private BufferedImage image;
   public BufferedImage getImage() {
      if (image == null){         
         try {
            ImageLocator locator = new ImageLocator(bundlePath);       
            File fin = new File(locator.locate(imageUrl));
            BufferedImage bimage = ImageIO.read(fin);
            setImage(bimage);            
         } catch (IOException e) {
            e.printStackTrace();
         }         
      }
      return image;
   }
    
}

class SklImageView extends SklObjectView {

   public SklImageView(SklObjectModel model){
      super(model);
   }
   
   BufferedImage image;
   
   @Override
   protected void update(){
      super.update();
      SklImageModel imageModel = (SklImageModel) model;
      image = imageModel.getImage();
      setActualSize(imageModel.getSize());
   }

   @Override
   public void paintComponent(Graphics g){      
      Graphics2D g2d = (Graphics2D) g;
      if (image != null){
         g2d.drawImage(image, 0, 0, getActualWidth(), getActualHeight(), null); 
         g2d.drawRect(0,0,getActualWidth()-1,getActualHeight()-1);
      }
   }
   
}

