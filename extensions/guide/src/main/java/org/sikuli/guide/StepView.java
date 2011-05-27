/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.sikuli.script.Debug;

public class StepView extends SikuliGuideComponent {
   
   class BackgroundImage extends SikuliGuideComponent {

      BufferedImage image;
      //BufferedImage darkenImage;
      double scale = (double) 1f;
      public BackgroundImage(BufferedImage image){
         this.image = image;
         setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
         setActualBounds(new Rectangle(0,0,(int)(image.getWidth()*scale),(int)(image.getHeight()*scale)));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);         
         Graphics2D g2d = (Graphics2D) g;
         if (image != null){            
            g2d.drawImage(image, 0, 0, null);
         }    
      }

      public BufferedImage crop(Rectangle r) {
         // TODO Make this boundary safe
         return image.getSubimage(r.x,r.y,r.width,r.height);
      }
   }
      
      private BackgroundImage screenImage;
      
      
      LayoutManager layout;
   
      JPanel background;
      Step step;
      
      public StepView(Step step_){         
         this.step = step_;
         
         setLayout(null);
         
         addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent arg0) {
               if (screenImage!=null){
                  
                  if (getBounds().width>0){
                     Debug.info("" + getBounds());
                     screenImage.setActualLocation(getWidth()/2-screenImage.getWidth()/2,
                        getHeight()/2-screenImage.getHeight()/2);
                     screenImage.setVisible(true);
                  }
               }
            }
            
         });
      }
      
      @Override
      public void paintComponent(Graphics g){        
         super.paintComponent(g);
      }
      
      @Override
      public void paintChildren(Graphics g){
         super.paintChildren(g);
         
         if (false){
            // debug
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.green);
            g2d.drawRect(0,0,getWidth()-1,getHeight()-1);

            Rectangle fb = getForegroundBounds();     
            g2d.setColor(Color.cyan);
            g2d.drawRect(fb.x,fb.y,fb.width,fb.height);
         }
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

         //g2d.setColor(Color.black);
         //g2d.fillRect(0,0,getWidth(),getHeight());
         
         paintPlain(g2d);
        
         
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

      public void addComponent(SikuliGuideComponent component){
         Debug.info("screenimage's location" + screenImage.getActualLocation());
         Point loc = component.getActualLocation();
         loc.x += getOrigin().x;
         loc.y += getOrigin().y;
         component.setActualLocation(loc);
         component.setLocationRelativeToComponent(screenImage);
         add(component,0);         
      }
      
      public Point getOrigin(){
         return screenImage.getActualLocation();
      }
      
      Point origin;
      public void setScreenImage(BufferedImage screenImage_){ 
            
         this.screenImage = new BackgroundImage(screenImage_);  ;
         
         screenImage.setVisible(false);
         setSize(screenImage.getSize());
         
         add(screenImage);
      }

      public BufferedImage getImage(Rectangle bounds) {
         
         Point o = new Point(screenImage.getActualLocation());
         BufferedImage croppedImage 
         = screenImage.image.getSubimage(bounds.x - o.x, bounds.y - o.y, bounds.width, bounds.height);
         return croppedImage;
      }
      
      
   }