/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;


import org.sikuli.script.Region;

public class SikuliGuideCallout extends SikuliGuideComponent{

   static final int TRIANGLE_SIZE = 20;
   static final int PADDING_X = 5;
   static final int PADDING_Y = 5;
   

   
   
   String text;

   public SikuliGuideCallout(String text){         
      super();
      init(text);
   }

   HTMLTextPane textArea;
   RoundedBox rbox;
   Triangle triangle;

   class Triangle extends SikuliGuideComponent {

      GeneralPath gp;
      public Triangle() {
         super();         
         gp = new GeneralPath();
         gp.moveTo(TRIANGLE_SIZE*0.45,0);
         gp.lineTo(TRIANGLE_SIZE*0.5,TRIANGLE_SIZE);
         gp.lineTo(TRIANGLE_SIZE*0.85,0);
         gp.closePath();

         setActualSize(new Dimension(TRIANGLE_SIZE,TRIANGLE_SIZE));
      }

      public void rotate(double radius){
         AffineTransform rat = new AffineTransform();
         rat.rotate(radius, TRIANGLE_SIZE/2, TRIANGLE_SIZE/2);
         gp.transform(rat);
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;

         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON); 
         g2d.fill(gp);
      }
   }

   class RoundedBox extends SikuliGuideComponent {

      public RoundedBox(Rectangle rect) {
         super();
         setActualBounds(rect);
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;

         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON); 

         RoundRectangle2D roundedRectangle = new 
         RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
         g2d.fill(roundedRectangle);
      }

   }

   
   @Override
   public void setBackground(Color color){
      textArea.setBackground(color);
      triangle.setForeground(color);
      rbox.setForeground(color);
      super.setBackground(color);
   }
   
    void init(String text){
      this.text = text;

      //      JTextArea textArea = new JTextArea();
      //      //textArea.setColumns(20);
      //      textArea.setEditable(false);
      //      //textArea.setRows(5);
      //      textArea.setText(text);
      //      textArea.setLineWrap(true);
      //      textArea.setWrapStyleWord(true);
      //      

      textArea = new HTMLTextPane();
      textArea.setText(text);
      textArea.setMaximumWidth(400);
      textArea.setLocation(PADDING_X,PADDING_Y);

      Rectangle rect = textArea.getBounds();
      rect.grow(PADDING_X,PADDING_Y);      
      rbox = new RoundedBox(rect);

      
      add(textArea);
      add(rbox);

      triangle = new Triangle();  
      triangle.setLocationRelativeToComponent(rbox, Layout.BOTTOM);
      add(triangle);
      
      Rectangle bounds = rbox.getBounds();
      bounds.add(triangle.getBounds());
      setActualBounds(bounds);
      
      setBackground(Color.yellow);
   }

   int dx=0;
   int dy=0;
   
   Layout currentSide = null;
   public void setLocationRelativeToRegion(Region region, Layout side) {

      if (side != currentSide){
         currentSide = side;


         if (side == Layout.TOP){

            triangle.setLocationRelativeToComponent(rbox, Layout.BOTTOM);

         } else if (side == Layout.BOTTOM){

            dy = TRIANGLE_SIZE;

            triangle.rotate(Math.PI);
            triangle.setLocationRelativeToComponent(rbox, Layout.TOP);

         } else if (side == Layout.LEFT){

            triangle.rotate(-Math.PI/2);
            triangle.setLocationRelativeToComponent(rbox, Layout.RIGHT);

         } else if (side == Layout.RIGHT){

            dx = TRIANGLE_SIZE;

            triangle.rotate(Math.PI/2);
            triangle.setLocationRelativeToComponent(rbox, Layout.LEFT);
         }      

         Rectangle bounds = rbox.getBounds();
         bounds.add(triangle.getBounds());
         setActualBounds(bounds);

      }

      super.setLocationRelativeToRegion(region,side);
   }


   @Override
   public void paintComponent(Graphics g){
      g.translate(dx, dy);
      super.paintComponent(g);
   }      

}
