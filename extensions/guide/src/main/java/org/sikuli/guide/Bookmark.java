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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;

import org.sikuli.script.Location;

public class Bookmark extends Component {
   
   
   Location location;
   String text;

   // which direction this bookmark is pointing
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   
   int direction = DIRECTION_EAST;
   
   Font font;
   
   int text_height; 
   int text_width; 
   int xspacing = 5;
   int yspacing = 5;
   int d = 10;
   
   public Bookmark(Location location, String message){
      this.location = location;
      this.text = message;
      
      
      font = new Font("sansserif", Font.BOLD, 16);
      FontMetrics fm = getFontMetrics(font);
      text_width = fm.stringWidth(text);
      text_height = fm.getHeight();
      
      text_width += xspacing*2;
      text_height += yspacing*2;
      
      setDirection(DIRECTION_EAST);
   }
   
   public void setDirection(int direction){
      this.direction = direction;
      if (direction == DIRECTION_EAST){
         setBounds(-3+location.x-d-text_width,location.y-text_height/2,text_width+d+10,text_height+10);
      } else if (direction == DIRECTION_WEST){
         setBounds(-3+location.x,location.y-text_height/2,text_width+d+10,text_height+10);            
      }
   }
   
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      
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
         
         p = new Location(text_width+d,text_height/2);
         a1 = new Location(text_width,0);
         a2 = new Location(text_width,text_height);            
         o1 = new Location(0,0);
         o2 = new Location(0,text_height);    
         
         to = o2;
         
      } else if (direction == DIRECTION_WEST){
      
         p = new Location(0,text_height/2);
         a1 = new Location(d,0);
         a2 = new Location(d,text_height);            
         o1 = new Location(d+text_width,0);
         o2 = new Location(d+text_width,text_height);
         
         to = a2;
      }
      
     // Rectangle rect = new Rectangle(x1,y1,width,height);

      
    
      // compute the vertices of the triangle pointing to the target
      int[]xs = new int[5];
      int[]ys = new int[5];

      xs[0] = (int) a1.x;
      xs[1] = (int) p.x;
      xs[2] = (int) a2.x;
      xs[3] = (int) o2.x;
      xs[4] = (int) o1.x;
      
      ys[0] = (int) a1.y;
      ys[1] = (int) p.y;
      ys[2] = (int) a2.y;
      ys[3] = (int) o2.y;
      ys[4] = (int) o1.y;

      Polygon shape = new Polygon(xs, ys, xs.length);

      g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));
      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
      g2d.translate(4,4);
      g2d.setColor(new Color(0.3f,0.3f,0.3f));
      g2d.fillPolygon(shape);

      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      g2d.translate(-2,-2);
      
      Stroke pen = new BasicStroke(3.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.black);
      g2d.fillPolygon(shape);

     

      g2d.setColor(Color.white);
      g2d.drawString(text, to.x + xspacing, to.y - yspacing*2);
      
      
      
   }
   
}