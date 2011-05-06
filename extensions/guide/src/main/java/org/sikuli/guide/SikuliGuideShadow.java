/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JComponent;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class SikuliGuideShadow extends SikuliGuideComponent{


   int shadowSize = 10;
   SikuliGuideComponent source;
   
   Dimension sourceSize = new Dimension();
   public SikuliGuideShadow(SikuliGuideComponent source_) {
      super();
      this.source = source_;

      setBoundsRelativeToComponent(source);
      setLocationRelativeToComponent(source,-shadowSize+2,-shadowSize+2);      
   }

   @Override
   public void setLocationRelativeToComponent(SikuliGuideComponent comp, int offsetx, int offsety) {
      this.source = comp;
      setBoundsRelativeToComponent(comp);
      super.setLocationRelativeToComponent(comp, offsetx, offsety);       
   }
   
   @Override
   public void setLocationRelativeToRegion(Region region, Layout side) {
      setBoundsRelativeToComponent(source);
      Debug.info("[Shadow] UDPATED: " + this);

      super.setLocationRelativeToRegion(region, side);       
   }

   
   

   void setBoundsRelativeToComponent(SikuliGuideComponent comp){
      if (sourceSize.equals(comp.getSize()))
         return;
      
      
      sourceSize = (Dimension) comp.getSize().clone();
      source = comp;
      
      if (comp instanceof SikuliGuideCircle ||
            comp instanceof SikuliGuideArrow ||
            comp instanceof SikuliGuideRectangle ||
            comp instanceof SikuliGuideBracket){
         shadowSize = 5;
      } else if (source instanceof SikuliGuideFlag ||
            source instanceof SikuliGuideText){
         shadowSize = 10;
      } else{
         shadowSize = 10;
      }

      Rectangle r = comp.getBounds();
      r.grow(shadowSize,shadowSize);
      
      setSize(r.getSize());
      createShadowImage();
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

   ConvolveOp getBlurOp(int size) {
      float[] data = new float[size * size];
      float value = 1 / (float) (size * size);
      for (int i = 0; i < data.length; i++) {
         data[i] = value;
      }
      return new ConvolveOp(new Kernel(size, size, data));
   }

   BufferedImage shadowImage = null;
   public BufferedImage createShadowImage(){    

      BufferedImage image = new BufferedImage(source.getWidth() + shadowSize * 2,
            source.getHeight() + shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      g2.translate(shadowSize,shadowSize);
      source.paint(g2);

      shadowImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
      getBlurOp(shadowSize).filter(createShadowMask(image), shadowImage);

      
      Debug.info("[Shadow] shadowImage: " + shadowImage);
      Debug.info("[Shadow] bounds: " + getBounds());

      return shadowImage;
   }

   public void paintComponent(Graphics g){
      super.paintComponent(g);

      Graphics2D g2d = (Graphics2D)g;
      g2d.drawImage(shadowImage, 0, 0, getWidth(), getHeight(), null, null);
   }
}