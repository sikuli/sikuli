package org.sikuli.script;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Pattern {
   String imgURL = null;
   float similarity = 0.7f;
   BufferedImage image;

   int dx=0, dy=0;

   public Pattern(){ }
   public Pattern(Pattern p){
      imgURL = p.imgURL;
      similarity = p.similarity;
      dx = p.dx;
      dy = p.dy;
   }

   public Pattern(String imgURL_){
      imgURL = imgURL_;
   }
   
   public Pattern(BufferedImage image){
      this.image = image;
   }

   public Pattern similar(float similarity_){
      Pattern ret = new Pattern(this);
      ret.similarity = similarity_;
      return ret;
   }   

   public Pattern exact(){
      Pattern ret = new Pattern(this);
      ret.similarity = 1.0f;
      return ret;
   }

   public Pattern targetOffset(int dx_, int dy_){
      Pattern ret = new Pattern(this);
      ret.dx = dx_;
      ret.dy = dy_;
      return ret;
   }

   public String toString(){
     String ret = "Pattern(\"" + imgURL + "\")";
     ret += ".similar(" + similarity +")";
     if(dx!=0 || dy!=0)
        ret += ".targetOffset(" + dx + "," + dy +")";
     return ret;
   }

   public Location getTargetOffset(){
      return new Location(dx, dy);
   }

   public String getFilename(){
      return imgURL;
   }
   
   public BufferedImage getImage() throws IOException{
      if (image == null){
         // locate and read the image into memory
         ImageLocator locator = new ImageLocator();
         String foundImageFullPath = locator.locate(getFilename());       
         image = ImageIO.read(new File(foundImageFullPath));
      }
      return image;
   }

}

