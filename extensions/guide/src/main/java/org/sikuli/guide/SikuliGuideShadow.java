/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class SikuliGuideShadow extends SikuliGuideComponent{


   int shadowSize = 10;
   SikuliGuideComponent source;
   public SikuliGuideShadow(SikuliGuide sikuliGuide, SikuliGuideComponent source) {
      super(sikuliGuide);
      this.source = source;
      
      
      if (source instanceof SikuliGuideCircle ||
            source instanceof SikuliGuideArrow ||
            source instanceof SikuliGuideRectangle ||
            source instanceof SikuliGuideBracket){
         shadowSize = 5;
      } else if (source instanceof SikuliGuideFlag ||
            source instanceof SikuliGuideText){
         shadowSize = 10;
      }
      
      Rectangle r = source.getBounds();
      r.grow(shadowSize,shadowSize);

      // offset shadow      
      r.x += shadowSize/4;
      r.y += shadowSize/4;
      
      setBounds(r);
      
      source.setShadow(this);
   }
   
   float shadowOpacity = 0.8f;
   Color shadowColor = Color.black;
   BufferedImage createShadowMask(BufferedImage image){ 
      BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); 

      Graphics2D g2d = mask.createGraphics(); 
      g2d.drawImage(image, 0, 0, null); 
      // Ar = As*Ad - Cr = Cs*Ad -> extract 'Ad' 
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, shadowOpacity)); 
      g2d.setColor(shadowColor); 
      g2d.fillRect(0, 0, image.getWidth(), image.getHeight()); 
      g2d.dispose(); 
      return mask; 
   } 
//   
   
   ConvolveOp getBlurOp(int size) {
      float[] data = new float[size * size];
      float value = 1 / (float) (size * size);
      for (int i = 0; i < data.length; i++) {
          data[i] = value;
      }
      return new ConvolveOp(new Kernel(size, size, data));
  }
   
   public void paint(Graphics g){

      Graphics2D g2d = (Graphics2D)g;
      BufferedImage image = new BufferedImage(getWidth() + shadowSize * 2,
            getHeight() + shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      g2.translate(shadowSize,shadowSize);
      source.paint(g2);
      
      BufferedImage shadow = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
      getBlurOp(shadowSize).filter(createShadowMask(image), shadow);

      
      g2d.drawImage(shadow, 0, 0, null, null);
      
   }
}