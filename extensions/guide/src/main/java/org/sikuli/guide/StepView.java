/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.sikuli.guide.EditorWindow.BackgroundImage;
import org.sikuli.script.Screen;

public class StepView extends SikuliGuideComponent {
      
      Step step;
      public StepView(Step step){
         this.step = step;
         setLayout(null);         
         // TODO set bounds to be equal to the container
         //setBounds(new Screen().getRect());
      }
      
      @Override
      public void paintComponent(Graphics g){        
         //((Graphics2D) g).scale(zoomLevel,zoomLevel);      
         super.paintComponent(g);
      }
      

      
      Rectangle getForegroundBounds(){
         
         Rectangle bounds = null;
         for (Component comp : getComponents()){

            if (!(comp instanceof BackgroundImage)){

               if (bounds == null){
                  bounds = new Rectangle(comp.getBounds());               
               }else{
                  bounds.add(comp.getBounds());
               }
            }
         }
         
         // 
         return bounds;     
      }
      
      
      public BufferedImage createForegroundThumbnail(int width, int height){
         
         
         Rectangle fr = getForegroundBounds();
         if (fr == null){
            // if no foreground component, return the bounds of the whole component
           fr = getBounds();
         }
         else
            fr.grow(20,20);
        
         // how much to scale to fit
         float xscale = (float)width / (float)fr.width;
         float yscale = (float)height/ (float)fr.height;
         
         float scale = Math.min(xscale, yscale);
         
         
//         Rectangle sq =  new Rectangle(maxd,maxd);
//         
//         // the top-left corner (x,y) of the square rectangle
//         int x = fr.x + fr.width/2 - maxd/2;
//         int y = fr.y + fr.height/2 - maxd/2;
//         sq.setLocation(x,y);
         
         scale = Math.min(scale, 1.0f);
         
         BufferedImage tb = new BufferedImage(width, height,
               BufferedImage.TYPE_INT_ARGB); 
        
         Graphics2D g2d = (Graphics2D) tb.createGraphics();

         g2d.scale(scale,scale);

         g2d.translate(-fr.x, -fr.y);

         //g2d.scale(1/zoomLevel,1/zoomLevel);

         g2d.setColor(Color.black);
         g2d.fillRect(0,0,getWidth(),getHeight());
         paint(g2d);
        
         
         g2d.dispose();
         
         
         return tb;
         
      }
      
      public BufferedImage createThumbnail(float scale){
         
         BufferedImage tb = new BufferedImage((int)(getWidth()*scale), 
               (int)(getHeight()*scale), 
               BufferedImage.TYPE_INT_ARGB); 
        
         Graphics2D g2d = (Graphics2D) tb.createGraphics();
         g2d.scale(scale,scale);
         g2d.setColor(Color.black);
         g2d.fillRect(0,0,getWidth(),getHeight());
         paint(g2d);
         
         
         Rectangle fr = getForegroundBounds();
         fr.grow(10,10);
         
         // square around the foreground bounds
         int maxd = Math.max(fr.width,fr.height);
         Rectangle sq =  new Rectangle(maxd,maxd);
         
         // the top-left corner (x,y) of the square rectangle
         int x = fr.x + fr.width/2 - maxd/2;
         int y = fr.y + fr.height/2 - maxd/2;
         sq.setLocation(x,y);
         
            
         g2d.setColor(Color.green);
         g2d.drawRect(fr.x,fr.y,fr.width,fr.height);

         g2d.setColor(Color.red);
         g2d.drawRect(sq.x,sq.y,sq.width,sq.height);

         
         g2d.dispose();
         
         
         return tb;
      }
      
      
//      static private float zoomLevel = 1.0f;
//      
//      static public void setZoomLevel(float newZoomLevel) {
//         zoomLevel = newZoomLevel;
//      }
//
//      static public float getZoomLevel() {
//         return zoomLevel;
//      }
   }