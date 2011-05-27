/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.sikuli.script.Debug;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class NewClickRecorder extends Clickable implements ComponentListener {
   
      ArrayList<RecordedClickEvent> clickEvents = new ArrayList<RecordedClickEvent>();
   
      Screen s = new Screen();
      private BufferedImage image;
   
      public NewClickRecorder(Region region){
         super(region);
         addComponentListener(this);
      }
      
      
      Point currentMouseLocation;
      @Override
      public void globalMouseMoved(Point p){  
         Debug.info("ClickRecorder: mouseMoved to " + p);
         
//         Rectangle r = new Rectangle(currentMouseLocation);
//         r.add(p);
//         
         //invalidate(r);//getParent().getParent().repaint(r);
         
         currentMouseLocation = p;
         
         //repaint(r);
         
         
         getParent().getParent().repaint();
      }

            
      @Override
      public void globalMouseEntered(){  
         Debug.info("ClickRecorder: mouseEntered");
      }
      
      @Override
      public void globalMouseExited(){
         Debug.info("ClickRecorder: mouseExited");
      }
      
      
      boolean isCapturePending = false;
      RecordedClickEvent currentClickEvent;
      @Override
      public void globalMouseClicked(Point p){
         Debug.info("ClickRecorder: mouseClicked at: " + p);         

         isCapturePending = true;


         RecordedClickEvent ce = new RecordedClickEvent();
         ce.setClickLocation(p);         
         clickEvents.add(ce);
         
         currentClickEvent = ce;
         
         setVisible(false);
         getParent().getParent().repaint();
      }

      public ArrayList<RecordedClickEvent> getClickEvents(){
            return clickEvents;
      }
      
      
      public void paintComponent(Graphics g){
         //super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;
         
         g2d.setStroke(new BasicStroke(5.0f));
         g2d.setColor(new Color(1f,0,0,0.5f));
         
         g2d.drawRect(0,0,getWidth(),getHeight());
         
         if (currentMouseLocation != null){
            
            Point p = currentMouseLocation;
            g2d.translate(p.x,p.y);
            Ellipse2D.Double ellipse =
               new Ellipse2D.Double(-10,-10,20,20);
            g2d.fill(ellipse);

         }
      }


      @Override
      public void componentHidden(ComponentEvent e) {
         // TODO a better way to time screen capture
         
         // do screen capture 500ms after the screen is hidden 
         // so that the visualization can be cleared
         Timer doScreenCaptureLater = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
               if (isCapturePending){                 
                  BufferedImage image = s.capture(region).getImage();
                  currentClickEvent.setScreenImage(image); 
                  isCapturePending = false;
                  setVisible(true);
               }
               
            }
        });
         doScreenCaptureLater.setRepeats(false);
         doScreenCaptureLater.start();
                 
      }


      @Override
      public void componentMoved(ComponentEvent arg0) {
         // TODO Auto-generated method stub
         
      }


      @Override
      public void componentResized(ComponentEvent arg0) {
         // TODO Auto-generated method stub
         
      }


      @Override
      public void componentShown(ComponentEvent arg0) {
         // TODO Auto-generated method stub
         
      }


      public RecordedClickEvent  getLastClickEvent() {
         return currentClickEvent;
      }
}