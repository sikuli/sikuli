/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import org.sikuli.script.Debug;

public class SklControlBox extends DefaultSklObjectModel {

   private SklObjectModel target;

   SklControlBox(SklObjectModel target){
      this.setTarget(target);
      
   }

   public SklObjectView createView(){   
      return new SklControlBoxView(this);
   }
   
   class TargetPropertyChangeListener implements PropertyChangeListener{
      @Override
      public void propertyChange(PropertyChangeEvent e) {         
         Debug.info("target fired");
         SklControlBox.this.pcs.firePropertyChange(PROPERTY_TARGET, 
               null,  
               SklControlBox.this.target);
      }
   }
   TargetPropertyChangeListener targetPropertyChangeListener = new TargetPropertyChangeListener();
   
   public void setTarget(SklObjectModel target) {

      // unfollow the existing target
      if (this.target != null)
         this.target.removePropertyChangeListener(targetPropertyChangeListener);
      
      // follow the new target
      if (target != null){
         target.addPropertyChangeListener(targetPropertyChangeListener);
      }
      
      this.pcs.firePropertyChange(PROPERTY_TARGET, this.target, this.target = target);
   }

   public SklObjectModel getTarget() {
      return target;
   }

   static public final String PROPERTY_TARGET = "target";

}

class SklControlBoxView extends SklObjectView {

   class ResizeMovement extends MouseAdapter {

      int xo=0,yo=0;

      @Override
      public void mouseDragged(MouseEvent e) {
         Debug.info("dragged to: " + e.getPoint());

         Point p = e.getPoint();
         p.x -= 5;
         p.y -= 5;
         update((ControlPoint) e.getSource(), p);
      }

      @Override
      public void mousePressed(MouseEvent e) {
         xo = e.getX();
         yo = e.getY();
         //setAutoMoveEnabled(false);         
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         Debug.info("released at: " + e.getPoint());
         //setAutoMoveEnabled(true); 

         // finished resizing
         //editor.currentStepContentChanged();
      }

      void update(ControlPoint cp, Point p){
         //Debug.info("released at: " + e.getPoint());
         repaint();
         int dx=0;
         int dy=0;
         int dw=0;
         int dh=0;
         if (cp  == tr){
            dw = p.x - xo;
            dy = p.y - yo;
            dh = -dy;            
         } else if (cp == tl){
            dx = p.x - xo;
            dy = p.y - yo;
            dh = -dy;
            dw = -dx;
         } else if (cp == bl){
            dx = p.x - xo;
            dh = p.y - yo;
            dw = -dx;
         } else if (cp == br){
            dx = 0;
            dy = 0;
            dw = p.x - xo;
            dh = p.y - yo;
         }

         Rectangle rect = getBounds();
         rect.x += dx;
         rect.y += dy;
         rect.height += dh;
         rect.width += dw;
         setBounds(rect);


         updateControlPoints();


         Rectangle bounds = new Rectangle(getBounds());
         bounds.grow(-10,-10);


         SklObjectModel controlledModel = ((SklControlBox) model).getTarget();
         controlledModel.setX(bounds.x);
         controlledModel.setY(bounds.y);
         controlledModel.setWidth(bounds.width);
         controlledModel.setHeight(bounds.height);


      }


   }


   class ControlPoint extends SikuliGuideComponent{

      ControlPoint(){
         setSize(10,10);
         addMouseMotionListener(new ResizeMovement());
         addMouseListener(new ResizeMovement());
         setForeground(new Color(1f,1f,1f,0.5f));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;
         g2d.fillRect(0,0,getWidth(),getHeight());
         g2d.setColor(Color.black);
         g2d.drawRect(0,0,getWidth()-1,getHeight()-1);
      }

   }

   class ConnectControlPoint extends SikuliGuideComponent{

      ConnectControlPoint(){
         setSize(10,10);
         addMouseMotionListener(new ResizeMovement());
         addMouseListener(new ResizeMovement());
         setForeground(new Color(0.8f,0f,0f,0.9f));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);        
         Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON);                
         Ellipse2D.Double ellipse =
            new Ellipse2D.Double(0,0,getWidth(),getHeight());
         g2d.fill(ellipse);
      }
   }

   ControlPoint tl,bl,tr,br;
   ConnectControlPoint ctl,cbl,ctr,cbr;
   public SklControlBoxView(SklObjectModel model)  {
      super(model);
   }

   @Override
   protected void init(){
      setLayout(null);      
      tl = new ControlPoint();       
      add(tl);

      bl = new ControlPoint();
      add(bl);

      br = new ControlPoint();
      add(br);

      tr = new ControlPoint();
      add(tr);
   }

   @Override
   protected void update(){

      Rectangle r = getBounds();


      super.update();      
      
      
      SklObjectModel target = ((SklControlBox) getModel()).getTarget();
      
      if (target == null)
         return;
      
      Rectangle rnew = new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight());
      rnew.grow(10,10);
      setBounds(rnew);
      updateControlPoints();


      r.add(getBounds());
      if (getParent() != null){
         getParent().repaint(r.x,r.y,r.width,r.height);
         getParent().repaint();
      }

   }

   void updateControlPoints(){

      int w = getWidth();
      int h = getHeight();

      tl.setLocation(0,0);
      bl.setLocation(0,h-10);     
      br.setLocation(w-10,h-10);     
      tr.setLocation(w-10,0);     

      //      ctl.setLocation(w/2-5,0);

      //      cbl.setLocation(w/2-5,h-10);     
      //      cbr.setLocation(0,h/2-5);     
      //      ctr.setLocation(w-10,h/2-5);


   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(SklControlBox.PROPERTY_TARGET)){ 
         update();
      }
      super.propertyChange(evt);      
   }

   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);      

      Graphics2D g2d = (Graphics2D) g;      
      g2d.setColor(new Color(1f,1f,1f,0.5f));
      g2d.drawRect(5,5,getWidth()-11,getHeight()-11);
   }

}