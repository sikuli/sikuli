/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JLabel;

import org.sikuli.script.Region;

public class SikuliGuideFlag extends SikuliGuideComponent{


   // which direction this element is pointing
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   public final static int DIRECTION_SOUTH = 3;
   public final static int DIRECTION_NORTH = 4;
   
   String text;
   JLabel label;
   public SikuliGuideFlag(String text){         
      super();
      init(text);
   }

   Font font;
   Rectangle textBox;
   Rectangle triangle;
   FontMetrics fm;
   
   static final int PADDING_X = 3;
   static final int PADDING_Y = 3;
   static final int SHADOW_SIZE = 1;

   int direction;
   void init(String text){
      this.text = text;
      
      setForeground(Color.black);
      setBackground(Color.green);

      textBox = new Rectangle();
      triangle = new Rectangle();

      font = new Font("sansserif", Font.BOLD, 14);
      fm = getFontMetrics(font);
      textBox.setSize(fm.stringWidth(text),fm.getHeight());
      textBox.grow(PADDING_X, PADDING_Y);
      
      setDirection(DIRECTION_EAST);
   }

   public void setLocationRelativeToRegion(Region region, int side) {
      if (side == TOP){
         setDirection(DIRECTION_SOUTH);
      } else if (side == BOTTOM){
         setDirection(DIRECTION_NORTH);
      } else if (side == LEFT){
         setDirection(DIRECTION_EAST);
      } else if (side == RIGHT){
         setDirection(DIRECTION_WEST);
      }      

      super.setLocationRelativeToRegion(region,side);
   }
   
   public void startAnimation(){
      if (direction == DIRECTION_EAST){
         setEntranceAnimation(createSlidingAnimator(-20,0));
      } else if (direction == DIRECTION_WEST){
         setEntranceAnimation(createSlidingAnimator(20,0));
      } else if (direction == DIRECTION_SOUTH){
         setEntranceAnimation(createSlidingAnimator(0,-20));
      } else if (direction == DIRECTION_NORTH){
         setEntranceAnimation(createSlidingAnimator(0,20));
      }
      
      super.startAnimation();
   }
   
   public void setDirection(int direction){
      this.direction = direction;
      if (direction == DIRECTION_EAST || direction == DIRECTION_WEST){
         triangle.setSize(10,textBox.height);
         setSize(textBox.width + triangle.width + SHADOW_SIZE, textBox.height + SHADOW_SIZE);
      }else{
         triangle.setSize(20, 10);
         setSize(textBox.width + SHADOW_SIZE, textBox.height + triangle.height + SHADOW_SIZE);
      }      
      
      if (direction == DIRECTION_EAST){
         textBox.setLocation(0, 0);
      } else if (direction == DIRECTION_WEST){
         textBox.setLocation(triangle.width, 0);
      } else if (direction == DIRECTION_SOUTH){
         textBox.setLocation(0, 0);
      } else if (direction == DIRECTION_NORTH){
         textBox.setLocation(0, triangle.height);
      }
   }

   public void paint(Graphics g){
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;
      g2d.setFont(font);
      

      GeneralPath gp = new GeneralPath();
      if (direction == DIRECTION_WEST || direction == DIRECTION_EAST) {
         gp.moveTo(0,0);
         gp.lineTo(textBox.width,0);
         gp.lineTo(textBox.width+triangle.width, textBox.height/2);
         gp.lineTo(textBox.width, textBox.height);
         gp.lineTo(0,textBox.height);
         gp.closePath();
      }else{
         gp.moveTo(0,0);
         gp.lineTo(textBox.width,0);
         gp.lineTo(textBox.width, textBox.height);
         gp.lineTo(textBox.width/2+8, textBox.height);
         gp.lineTo(textBox.width/2,textBox.height+triangle.height);
         gp.lineTo(textBox.width/2-8, textBox.height);
         gp.lineTo(0,textBox.height);
         gp.closePath();
      }

      if (direction == DIRECTION_WEST){
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(textBox.width + triangle.width, textBox.height);
         rat.rotate(Math.PI);
         gp.transform(rat);
      }else if (direction == DIRECTION_NORTH){
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(textBox.width, textBox.height+triangle.height);
         rat.rotate(Math.PI);
         gp.transform(rat);
      }

      g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));

//      // draw shadow
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//      g2d.translate(SHADOW_SIZE,SHADOW_SIZE);
//      g2d.setColor(new Color(0.3f,0.3f,0.3f));
//      g2d.fill(gp);
//
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      
     // g2d.translate(-SHADOW_SIZE,-SHADOW_SIZE);
     
      // draw background      
      //g2d.setColor(Color.black);
      g2d.setColor(getBackground());
      g2d.fill(gp);
      
      // draw outline
      Stroke pen = new BasicStroke(1.0F);
      g2d.setStroke(pen);
      //g2d.setColor(new Color(0.6f,0.6f,0.6f));
      g2d.setColor(Color.white);
      g2d.draw(gp);
      

      g2d.setColor(getForeground());
      g2d.drawString(text, textBox.x + PADDING_X, 
            textBox.y +  textBox.height - fm.getDescent() - PADDING_Y);

   }


}
