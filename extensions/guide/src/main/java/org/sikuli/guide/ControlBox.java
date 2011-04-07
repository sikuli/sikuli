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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class ControlBox extends SikuliGuideComponent {
   
   BufferedImage image;
   float scale;
   int w,h;
   
   private SikuliGuideComponent targetComponent;
   
   private EditorWindow editor;
   
   class ResizeMovement implements MouseMotionListener, MouseListener{

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
      public void mouseMoved(MouseEvent arg0) {
         // TODO Auto-generated method stub
         
         
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void mousePressed(MouseEvent e) {
         xo = e.getX();
         yo = e.getY();
         setAutoMoveEnabled(false);         
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         Debug.info("released at: " + e.getPoint());
         setAutoMoveEnabled(true); 
         
         // finished resizing
         editor.currentStepContentChanged();
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
         getTargetComponent().setBounds(bounds);
         
         
         if (getParent() != null)
            getParent().repaint();

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
   public ControlBox()  {
       super();
       
       tl = new ControlPoint();       
       add(tl);
       
       bl = new ControlPoint();
       add(bl);

       br = new ControlPoint();
       add(br);
       
       tr = new ControlPoint();
       add(tr);
       
//       ctl = new ConnectControlPoint();       
//       add(ctl);
       
//       cbl = new ConnectControlPoint();
//       add(cbl);
//
//       cbr = new ConnectControlPoint();
//       add(cbr);
//       
//       ctr = new ConnectControlPoint();
//       add(ctr);
       
       
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
   public void paintComponent(Graphics g){
      super.paintComponent(g);      
      Graphics2D g2d = (Graphics2D) g;      
      g2d.setColor(new Color(1f,1f,1f,0.5f));
      g2d.drawRect(5,5,getWidth()-11,getHeight()-11);
   }

   public void setTargetComponent(SikuliGuideComponent targetComponent) {
      this.targetComponent = targetComponent;
      

      //setAutoResizeEnabled(false);
      //setAutoMoveEnabled(false);
      
      setMargin(10,10,10,10);
      setLocationRelativeToComponent(targetComponent, SikuliGuideComponent.OVER);
   }   
   
   @Override
   public void setLocationRelativeToRegion(Region region, int side) {
      //Debug.info("[ControlBox] updated by relative component");
      super.setLocationRelativeToRegion(region, side); 
      updateControlPoints();
   }
   
   public SikuliGuideComponent getTargetComponent() {
      return targetComponent;
   }

   public void setEditor(EditorWindow editor) {
      this.editor = editor;
   }

   public EditorWindow getEditor() {
      return editor;
   }
      
   
}