/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.sikuli.script.Debug;
import org.sikuli.script.Location;

public class Flag extends Component {
   
   
   Location location;
   Location dest_location;
   String text;
   public Animator anim = null;
   public SikuliGuide guide = null;
   
   public void start(){
      
      anim = new TimeBasedAnimator(
            new OutQuarticEase((float)30, (float)0, 1000));
   }

   // which direction this bookmark is pointing
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   public final static int DIRECTION_SOUTH = 3;
   public final static int DIRECTION_NORTH = 4;
   
   int direction = DIRECTION_EAST;
   
   Font font;
   
   int text_height; 
   int text_width; 
   int xspacing = 5;
   int yspacing = 5;
   int d = 10;
   
   public Flag(Location location, String message){
      this.location = location;
      this.text = message;
      this.dest_location = (Location) location.clone();
      
      
      font = new Font("sansserif", Font.BOLD, 16);
      FontMetrics fm = getFontMetrics(font);
      text_width = fm.stringWidth(text);
      text_height = fm.getHeight();
      
      text_width += xspacing*2;
      text_height += yspacing*2;
      
      setDirection(DIRECTION_EAST);
   }
   
   public void setLocation(Location location){
      this.location = location;
      setDirection(direction);
   }
   
   public void setDirection(int direction){
      this.direction = direction;
      if (direction == DIRECTION_EAST){
         setBounds(-3+location.x-d-text_width,location.y-text_height/2,text_width+d+10,text_height+10);
      } else if (direction == DIRECTION_WEST){
         setBounds(-3+location.x,location.y-text_height/2,text_width+d+10,text_height+10);            
      } else if (direction == DIRECTION_SOUTH){
         setBounds(-3+location.x-text_width/2,location.y-text_height-d,text_width+d+10,text_height+d+10);            
      } else if (direction == DIRECTION_NORTH){
         setBounds(-3+location.x-text_width/2,location.y,text_width+d+10,text_height+d+10);            
      }
   }
   
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      
//     if (anim == null)
//        return;
//      
//     if (anim != null && anim.running()){         
//         int dx = (int) anim.step();
//         setLocation(new Location(dest_location.x - dx, dest_location.y));
//         //g2d.translate(-dx,0);
//      }

      g2d.translate(3,0);
     
      
      g2d.setFont(font);
      
      Location o1 = null;
      Location o2 = null;

      Location a1 = null;
      Location p = null;
      Location a2 = null;
      
      Location to = null;
      
      // the upper left corner of the text box
      if (direction == DIRECTION_EAST){
//         
//         p = new Location(text_width+d,text_height/2);
//         a1 = new Location(text_width,0);
//         a2 = new Location(text_width,text_height);            
//         o1 = new Location(0,0);
//         o2 = new Location(0,text_height);    
//         
         to = new Location(0,text_height);
         
      } else if (direction == DIRECTION_WEST){
//      
//         p = new Location(0,text_height/2);
//         a1 = new Location(d,0);
//         a2 = new Location(d,text_height);            
//         o1 = new Location(d+text_width,0);
//         o2 = new Location(d+text_width,text_height);
         
         to =  new Location(d,text_height);            
      } else if (direction == DIRECTION_SOUTH){
         
         to =  new Location(0,text_height);     
      }  else if (direction == DIRECTION_NORTH){
         
         to =  new Location(0,d + text_height);     
      }
      
     // Rectangle rect = new Rectangle(x1,y1,width,height);

      
    
//      // compute the vertices of the triangle pointing to the target
//      int[]xs = new int[5];
//      int[]ys = new int[5];
//
//      xs[0] = (int) a1.x;
//      xs[1] = (int) p.x;
//      xs[2] = (int) a2.x;
//      xs[3] = (int) o2.x;
//      xs[4] = (int) o1.x;
//      
//      ys[0] = (int) a1.y;
//      ys[1] = (int) p.y;
//      ys[2] = (int) a2.y;
//      ys[3] = (int) o2.y;
//      ys[4] = (int) o1.y;
//      
      GeneralPath gp = new GeneralPath();
      if (direction == DIRECTION_WEST || direction == DIRECTION_EAST) {
         gp.moveTo(0,0);
         gp.lineTo(text_width,0);
         gp.lineTo(text_width+d, text_height/2);
         gp.lineTo(text_width, text_height);
         gp.lineTo(0,text_height);
         gp.closePath();
      }else{
         gp.moveTo(0,0);
         gp.lineTo(text_width,0);
         gp.lineTo(text_width, text_height);
         gp.lineTo(text_width/2+8, text_height);
         gp.lineTo(text_width/2,text_height+d);
         gp.lineTo(text_width/2-8, text_height);
         gp.lineTo(0,text_height);
         gp.closePath();
      }
      

      //Polygon shape = new Polygon(xs, ys, xs.length);

      g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));
      
      // drawing shadow
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
      
      
      if (direction == DIRECTION_WEST){
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(text_width+d, text_height);
         rat.rotate(Math.PI);
         gp.transform(rat);
      }else if (direction == DIRECTION_NORTH){
         
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(text_width, text_height+d);
         rat.rotate(Math.PI);
         gp.transform(rat);
      }
      
      //g2.transform(rat);
     // g2d.fillPolygon(shape);
      
      g2d.translate(4,4);
      g2d.setColor(new Color(0.3f,0.3f,0.3f));
      g2d.fill(gp);

      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      g2d.translate(-2,-2);
      
      Stroke pen = new BasicStroke(3.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.black);
      //g2d.fillPolygon(shape);
      g2d.fill(gp);
     // g2d.draw(gp);

      

      g2d.setColor(Color.white);
      g2d.drawString(text, to.x + xspacing, to.y - yspacing*2);
      
      
//      if (anim != null && anim.running()){
////         
////         int dx = (int) anim.step();
////         location.x = dest_location.x - dx; 
////         Debug.log("size:" + dx);
////
////         //         float size = anim.step();
//////         Debug.log("size:" + size);
//////         font = new Font("sansserif", Font.BOLD, (int)size);
//////         FontMetrics fm = getFontMetrics(font);
//////         text_width = fm.stringWidth(text);
//////         text_height = fm.getHeight();
//////         
//////         text_width += xspacing*2;
//////         text_height += yspacing*2;
//////         
////         setDirection(DIRECTION_EAST);
//         guide.repaint();
//      }
   }
   
}