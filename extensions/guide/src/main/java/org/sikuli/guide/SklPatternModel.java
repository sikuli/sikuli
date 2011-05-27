package org.sikuli.guide;

import java.awt.Rectangle;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class SklPatternModel {
   
   @Attribute
   private String imageUrl = "";
   
   private Rectangle ROI = null;

   public SklPatternModel(){
      
   }
   
   public SklPatternModel(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   
   public void setROI(Rectangle r){
      ROI = r;
   }
   
   public Rectangle getROI(){
      return ROI;
   }

}
