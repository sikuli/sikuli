package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class ScreenRegionSelectionWindow extends JWindow{

   RectangleSelectionMouseAdapter adapter;
   
   public ScreenRegionSelectionWindow(JFrame owner){
      
      setBounds(new Screen().getRect());
      setLocation(0,0);
      setBackground(Color.black);
      getRootPane().putClientProperty("Window.alpha", new Float(0.2f));
      setAlwaysOnTop(true);

      adapter = new RectangleSelectionMouseAdapter();
      addMouseMotionListener(adapter);
      addMouseListener(adapter);
   }


   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      super.paint(g);

      Rectangle r = getSelectedRectangle();
      if (r != null){

         g2d.setColor(Color.white);
         g2d.fillRect(r.x,r.y,r.width,r.height);

         g2d.setStroke(new BasicStroke(3.0f));
         g2d.setColor(Color.red);
         g2d.drawRect(r.x,r.y,r.width,r.height);
      }

   }

   Point p = null, q = null;
   Rectangle getSelectedRectangle(){
      if (p == null)
         return null;

      Rectangle r = new Rectangle(p);
      r.add(q);
      return r;
   }   

   class RectangleSelectionMouseAdapter extends MouseAdapter{


      boolean selecting = false;
      boolean running = true;
      Object action;



      @Override
      public void mouseDragged(MouseEvent e) {
         if (running){
            if (selecting){
               q = e.getPoint();
               repaint();
            }
         }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
         if (running){
            Debug.info("moved to: " + e.getX() + "," + e.getY());     

            if (selecting)
               q = e.getPoint();
         }
      }

      @Override
      public void mousePressed(MouseEvent e) {
         if (running){
            Debug.info("pressed at: " + e.getX() + "," + e.getY());     

            selecting = true;
            p = e.getPoint();
            q = e.getPoint();         
         }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         if (running){
            selecting = false;
            q = e.getPoint();
            
            Rectangle r = getSelectedRectangle();
            if (r.width < 100 || r.height < 100){
               // selected rectangle too small, ignore and retry
            }else{  
            
               notifyWaiter();
               setVisible(false);
            }
         }
      }
   };
   
   void notifyWaiter(){      
      synchronized(this){
         notify();
      }
   }


   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
         Env.getOSUtil().bringWindowToFront(this, false);
      }     
      super.toFront();

   }


   public Region getSelectedRegion() {
      return new Region(getSelectedRectangle());
   }


   public void startModal() {
      setVisible(true);
      toFront();
      
      synchronized(this){
         try {
            wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }      
   }
}
