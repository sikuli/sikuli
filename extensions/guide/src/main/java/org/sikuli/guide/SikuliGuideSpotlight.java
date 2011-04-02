/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import org.sikuli.script.Region;

public class SikuliGuideSpotlight extends SikuliGuideComponent{

   public final static int RECTANGLE = 0;
   public final static int CIRCLE = 1;
   

   boolean border = true;
   Color border_color = Color.black;
   int shape = RECTANGLE;
   
   Region region;
   public SikuliGuideSpotlight(Region region){         
      super();
      this.region = region;
      
      if (region != null)
         setBounds(region.getRect());
      
      
      
      setShape(RECTANGLE);
   }

   public void setShape(int shape_constant){
      this.shape = shape_constant;
   }
   
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
      
      Rectangle r = getBounds();
      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));        
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);       

      if (shape == RECTANGLE){
         g2d.fillRect(0,0,r.width-1,r.height-1);

      }else if (shape == CIRCLE){
         Ellipse2D.Double ellipse =
            new Ellipse2D.Double(0,0,r.width,r.height);
         g2d.fill(ellipse);         

         // adding visual ringing effect
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         
         int ds[] =  {0,2,4,6};
         float bs[] = {0.25f,0.15f,0.1f};
         
         for (int i = 0; i < 3; ++i){
            int d = ds[i];
            float b = bs[i];
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            ellipse =
               new Ellipse2D.Double(d,d,r.width-2*d,r.height-2*d);
            g2d.setColor(new Color(0f,0f,0f,b));
            g2d.fill(ellipse);
            
            d = ds[i+1];
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));        
             ellipse =
                new Ellipse2D.Double(d,d,r.width-2*d,r.height-2*d);
             g2d.setColor(Color.black);
             g2d.fill(ellipse);
         }
         

      }
      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));        

   } 

}