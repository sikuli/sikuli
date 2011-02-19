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
   public SlidingAnimator anim = null;
   public SikuliGuide guide = null;

   class SlidingAnimator extends TimeBasedAnimator{
      
      public SlidingAnimator(){
         super(new OutQuarticEase((float)30, (float)0, 1000)); 
      }
      
      public void animateStep(){
         int d = (int) step();
         if (direction == DIRECTION_EAST){
            setLocation(new Location(dest_location.x - d, dest_location.y));
         } else if (direction == DIRECTION_WEST){
            setLocation(new Location(dest_location.x + d, dest_location.y));           
         } else if (direction == DIRECTION_SOUTH){
            setLocation(new Location(dest_location.x, dest_location.y - d));          
         } else if (direction == DIRECTION_NORTH){
            setLocation(new Location(dest_location.x, dest_location.y + d));            
         }
      }
      
   }
   
   public void start(){
      anim = new SlidingAnimator();
      anim.animateStep();
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
         setBounds(-6+location.x-d-text_width,location.y-text_height/2-2,text_width+d+10,text_height+10);
      } else if (direction == DIRECTION_WEST){
         setBounds(-3+location.x,location.y-text_height/2-2,text_width+d+10,text_height+10);            
      } else if (direction == DIRECTION_SOUTH){
         setBounds(-3+location.x-text_width/2,location.y-text_height-d-3,text_width+d+10,text_height+d+10);            
      } else if (direction == DIRECTION_NORTH){
         setBounds(-3+location.x-text_width/2,location.y,text_width+d+10,text_height+d+10);            
      }
   }

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      super.paint(g2d);
      if (anim == null)
         return;

      g2d.translate(3,0);
      g2d.setFont(font);

      Location to = null;
      // the upper left corner of the text box
      if (direction == DIRECTION_EAST){
         to = new Location(0,text_height);
      } else if (direction == DIRECTION_WEST){
         to =  new Location(d,text_height);            
      } else if (direction == DIRECTION_SOUTH){
         to =  new Location(0,text_height);     
      }  else if (direction == DIRECTION_NORTH){
         to =  new Location(0,d + text_height);     
      }

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

      if (anim != null && anim.running()){
         anim.animateStep();
         guide.repaint();
      }

   }

}