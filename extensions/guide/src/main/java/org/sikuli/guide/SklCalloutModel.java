package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.sikuli.guide.SklCalloutView.RoundedBox;
import org.sikuli.guide.SklCalloutView.Triangle;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class SklCalloutModel extends DefaultSklTextModel {

   public SklCalloutModel() {
      setBackground(Color.yellow);
   }

   
   public SklCalloutModel(String text) {
      super(text);
      setBackground(Color.yellow);
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
      return new SklCalloutView((SklCalloutModel) this);
   }
   
}

class SklCalloutView extends SklObjectView {
   
   static final int TRIANGLE_SIZE = 20;
   static final int PADDING_X = 5;
   static final int PADDING_Y = 5;

   public SklCalloutView(SklCalloutModel model){
      super(model);
      setLayout(null);
   }

   //HTMLTextPane textArea;
   //HTMLTextPane textArea;
   JLabel textArea;
   RoundedBox rbox;
   Triangle triangle;

   class Triangle extends JComponent {

      GeneralPath gp;
      public Triangle() {
         super();         
         gp = new GeneralPath();
         gp.moveTo(TRIANGLE_SIZE*0.45,0);
         gp.lineTo(TRIANGLE_SIZE*0.5,TRIANGLE_SIZE);
         gp.lineTo(TRIANGLE_SIZE*0.85,0);
         gp.closePath();

         setSize(new Dimension(TRIANGLE_SIZE,TRIANGLE_SIZE));
      }

      public void rotate(double radius){
         gp = new GeneralPath();
         gp.moveTo(TRIANGLE_SIZE*0.45,0);
         gp.lineTo(TRIANGLE_SIZE*0.5,TRIANGLE_SIZE);
         gp.lineTo(TRIANGLE_SIZE*0.85,0);
         gp.closePath();

         setSize(new Dimension(TRIANGLE_SIZE,TRIANGLE_SIZE));

         
         AffineTransform rat = new AffineTransform();
         rat.rotate(radius, TRIANGLE_SIZE/2, TRIANGLE_SIZE/2);
         gp.transform(rat);
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;

         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON); 
         g2d.setColor(getBackground());
         g2d.fill(gp);
      }
   }

   class RoundedBox extends JComponent {

      public RoundedBox() {
         super();
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;

         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON); 

         RoundRectangle2D roundedRectangle = new 
         RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
         g2d.setColor(getBackground());
         g2d.fill(roundedRectangle);
      }

   }


   int direction;
   
   @Override 
   protected void update(){
      super.update();
      
      if (textArea == null){
         textArea = new JLabel();
         triangle = new Triangle();  
         rbox = new RoundedBox();
         
         setLayout(null);
         
         add(rbox);
         rbox.add(textArea);
         rbox.setLayout(null);
         
         add(triangle,0);
         
         updateDirection();
      }
      
      //textArea.setBackground(model.getBackground());
      triangle.setForeground(model.getForeground());
      triangle.setBackground(model.getBackground());

      rbox.setForeground(model.getForeground());
      rbox.setBackground(model.getBackground());
      setBackground(model.getBackground());
      
      SklCalloutModel calloutModel = (SklCalloutModel) model;
      String text = calloutModel.getText();
      int fontSize = calloutModel.getFontSize();

      //      JTextArea textArea = new JTextArea();
      //      //textArea.setColumns(20);
      //      textArea.setEditable(false);
      //      //textArea.setRows(5);
      //      textArea.setText(text);
      //      textArea.setLineWrap(true);
      //      textArea.setWrapStyleWord(true);
      //      

      
      textArea.setText("<html><div style='font-size:"+fontSize+"px;'>"+text+"</html>");
      
      Dimension size = textArea.getPreferredSize();
      if (size.width > calloutModel.getMaximumWidth()){
         textArea.setText("<html><div style='font-size:"+fontSize+"px; width:" + 
               calloutModel.getMaximumWidth() + "px'>"+text+"</html>");
      }
      textArea.setLocation(PADDING_X,PADDING_Y);
      textArea.setSize(textArea.getPreferredSize());

      Rectangle rect = textArea.getBounds();
      rect.grow(PADDING_X,PADDING_Y);      
      rbox.setBounds(rect);
      
      //

      updateDirection();
      
      Dimension d = getActualSize();
      model.setWidth(d.width);
      model.setHeight(d.height);
   }

   
   void updateDirection(){
      
      int direction = ((SklCalloutModel) model).getDirection();
      
      Rectangle r = rbox.getBounds();
      Rectangle t = triangle.getBounds();

         if (direction == SklCalloutModel.DIRECTION_SOUTH){

            triangle.setLocation(r.x + r.width/2, r.height);
         } else if (direction == SklCalloutModel.DIRECTION_NORTH){

            triangle.rotate(Math.PI);
            triangle.setLocation(r.x + r.width/2,0);
            rbox.setLocation(0,t.height);
            
         } else if (direction == SklCalloutModel.DIRECTION_EAST){

            triangle.rotate(-Math.PI/2);
            triangle.setLocation(r.width,r.height/2 - t.height/2);
            rbox.setLocation(0, 0);
            
         } else if (direction == SklCalloutModel.DIRECTION_WEST){
            
            triangle.rotate(Math.PI/2);
            triangle.setLocation(0,r.height/2 - t.height/2);
            rbox.setLocation(t.width, 0);

         }

         Rectangle bounds = rbox.getBounds();
         bounds.add(triangle.getBounds());
         setActualSize(bounds.getSize());

   }
   
}