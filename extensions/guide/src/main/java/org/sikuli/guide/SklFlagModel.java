package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class SklFlagModel extends DefaultSklTextModel {
   
   public SklFlagModel(){
      
   }
   
   public SklFlagModel(String text) {
      super(text);
      setBackground(Color.green);
   }
   
   public void setDirection(int direction) {
      this.direction = direction;
   }
   public int getDirection() {
      return direction;
   }

   @Attribute
   private int direction = DIRECTION_EAST;
   
   // which direction this element is pointing
   public final static int DIRECTION_EAST = 1;
   public final static int DIRECTION_WEST = 2;
   public final static int DIRECTION_SOUTH = 3;
   public final static int DIRECTION_NORTH = 4;

   
   @Override
   public SklObjectView createView(){
      return new SklFlagView((SklFlagModel) this);
   }
   
}

class SklFlagView extends SklObjectView {

   public SklFlagView(SklFlagModel model){
      super(model);   
   }
   
   Font font;
   Rectangle textBox;
   Rectangle triangle;
   FontMetrics fm;
   
   static final int PADDING_X = 3;
   static final int PADDING_Y = 3;
 

   int direction;
   
   @Override
   protected void update(){
      super.update();
      
      SklFlagModel flagModel = (SklFlagModel) model;
      String text = flagModel.getText();
      
      setForeground(Color.black);
      setBackground(Color.green);

      textBox = new Rectangle();
      triangle = new Rectangle();

      font = new Font("sansserif", Font.BOLD, 14);
      fm = getFontMetrics(font);
      textBox.setSize(fm.stringWidth(text),fm.getHeight());
      textBox.grow(PADDING_X, PADDING_Y);

      setDirection(flagModel.getDirection());
      
      Dimension d = getActualSize();
      model.setWidth(d.width);
      model.setHeight(d.height);
   }

//   public void setLocationRelativeToRegion(Region region, Layout side) {
//      if (side == Layout.TOP){
//         setDirection(DIRECTION_SOUTH);
//      } else if (side == Layout.BOTTOM){
//         setDirection(DIRECTION_NORTH);
//      } else if (side == Layout.LEFT){
//         setDirection(DIRECTION_EAST);
//      } else if (side == Layout.RIGHT){
//         setDirection(DIRECTION_WEST);
//      }      
//
//      super.setLocationRelativeToRegion(region,side);
//   }
   
  
   
   Dimension canonicalSize;
   
   GeneralPath flagShape;
   
   public void setDirection(int direction){
      this.direction = direction;
      if (direction == SklFlagModel.DIRECTION_EAST || direction == SklFlagModel.DIRECTION_WEST){
         triangle.setSize(10,textBox.height);
         canonicalSize = new Dimension(textBox.width + triangle.width, textBox.height);                  
      }else{
         triangle.setSize(20, 10);
         setActualSize(textBox.width, textBox.height + triangle.height);
         canonicalSize = new Dimension(textBox.width,  textBox.height + triangle.height);
      }
      
      setActualSize(canonicalSize);
      
      if (direction == SklFlagModel.DIRECTION_EAST){
         textBox.setLocation(0, 0);
      } else if (direction == SklFlagModel.DIRECTION_WEST){
         textBox.setLocation(triangle.width, 0);
      } else if (direction == SklFlagModel.DIRECTION_SOUTH){
         textBox.setLocation(0, 0);
      } else if (direction == SklFlagModel.DIRECTION_NORTH){
         textBox.setLocation(0, triangle.height);
      }
      
      
      flagShape = new GeneralPath();
      if (direction == SklFlagModel.DIRECTION_WEST || direction == SklFlagModel.DIRECTION_EAST) {
         flagShape.moveTo(0,0);
         flagShape.lineTo(textBox.width,0);
         flagShape.lineTo(textBox.width+triangle.width, textBox.height/2);
         flagShape.lineTo(textBox.width, textBox.height);
         flagShape.lineTo(0,textBox.height);
         flagShape.closePath();
      }else{
         flagShape.moveTo(0,0);
         flagShape.lineTo(textBox.width,0);
         flagShape.lineTo(textBox.width, textBox.height);
         flagShape.lineTo(textBox.width/2+8, textBox.height);
         flagShape.lineTo(textBox.width/2,textBox.height+triangle.height);
         flagShape.lineTo(textBox.width/2-8, textBox.height);
         flagShape.lineTo(0,textBox.height);
         flagShape.closePath();
      }

      if (direction == SklFlagModel.DIRECTION_WEST){
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(textBox.width + triangle.width, textBox.height);
         rat.rotate(Math.PI);
         flagShape.transform(rat);
      }else if (direction == SklFlagModel.DIRECTION_NORTH){
         AffineTransform rat = new AffineTransform();
         rat.setToTranslation(textBox.width, textBox.height+triangle.height);
         rat.rotate(Math.PI);
         flagShape.transform(rat);
      }
   }

   @Override
   public void paintComponent(Graphics g){
      
      Dimension d = new Dimension(textBox.width + triangle.width, textBox.height);
      
      Dimension originalSize = canonicalSize;
      Dimension actualSize = getActualSize();
      
      float scalex = 1f * actualSize.width / originalSize.width;
      float scaley = 1f * actualSize.height / originalSize.height;      
      ((Graphics2D) g).scale(scalex, scaley);

      super.paintComponent(g);

      Graphics2D g2d = (Graphics2D) g;
      g2d.setFont(font);
      



      g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));

      g2d.setColor(getBackground());
      g2d.fill(flagShape);
      
      // draw outline
      Stroke pen = new BasicStroke(1.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.white);
      g2d.draw(flagShape);      

      g2d.setColor(getForeground());
      g2d.drawString(((SklFlagModel)model).getText(), textBox.x + PADDING_X, 
            textBox.y +  textBox.height - fm.getDescent() - PADDING_Y);

   }
   
}

