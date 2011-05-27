package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class ScreenRecorderWindow extends JWindow{

   ArrayList<RecordedClickEvent> clickEvents = new ArrayList<RecordedClickEvent>();
   Robot robot;
   
   void stopCapturing(){
      dispose();
      notifyWaiter();
   }
   
   class BackgroundWindow extends JWindow{
      
      public BackgroundWindow() {
         //setLayout(null);
         JButton button = new JButton("Stop");         
         button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               stopCapturing();
               dispose();
            }
            
         });
         
         
         //add(button);
         //button.setBounds(0,0,100,30);
         //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
         setLayout(new BorderLayout());
         
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.add(button);        
         panel.setLocation(0,0);
         panel.setBackground(new Color(0f,0f,0f,0.5f));
         panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
         add(panel,BorderLayout.NORTH);
         
         setBackground(null);
         getContentPane().setBackground(null);
         Env.getOSUtil().setWindowOpaque(this, false);
         setAlwaysOnTop(true);
      }
      
      public void paint(Graphics g){
         Graphics2D g2d = (Graphics2D) g;
         super.paint(g);
         
         g.translate(0,30);
         
         Dimension d = getSize();
         g2d.setStroke(new BasicStroke(3f));
         g2d.setColor(Color.green);
         g2d.drawRect(0,0,d.width-1,d.height-31);
      }
   }
   
   RectangleSelectionMouseAdapter adapter;
   Point currentMouseLocation;
   public ScreenRecorderWindow(JFrame owner){

      try {
         robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }
      
      setBounds(new Rectangle(100,100,640,480));

      setBackground(null);//Color.black);
      getContentPane().setBackground(null);
      Env.getOSUtil().setWindowOpaque(this, false);
      
      final BackgroundWindow bw = new BackgroundWindow();
      Rectangle bwbounds = new Rectangle(getBounds());
      bwbounds.grow(2,2);
      bwbounds.y -= 30;
      bwbounds.height += 30;
      bw.setBounds(bwbounds);
      bw.setVisible(true);
      bw.addComponentListener(new ComponentListener(){

         @Override
         public void componentHidden(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            
         }

         @Override
         public void componentMoved(ComponentEvent e) {            
            Debug.info("Background window moved");
            Point newLocation = bw.getLocation();
            newLocation.y += 30;
            newLocation.x += 2;
            newLocation.y += 2;
            setLocation(newLocation);
         }

         @Override
         public void componentResized(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            
         }

         @Override
         public void componentShown(ComponentEvent arg0) {
            // TODO Auto-generated method stub
            
         }
         
      });
   
      //getRootPane().putClientProperty("Window.alpha", new Float(0.0f));
      setAlwaysOnTop(true);

      adapter = new RectangleSelectionMouseAdapter();
      addMouseMotionListener(adapter);
      addMouseListener(adapter);
      
      
   }


   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      super.paint(g);
      
      
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
//      g2d.setColor(Color.black);
//      g2d.fillRect(0,0,getWidth(),getHeight());
//      
//      if (currentMouseLocation != null){
//         
//         Point p = currentMouseLocation;
//         g2d.translate(p.x,p.y);
//         Ellipse2D.Double ellipse =
//            new Ellipse2D.Double(-10,-10,20,20);
//         g2d.fill(ellipse);
//
//      }
//      Rectangle r = getSelectedRectangle();
//      if (r != null){
//
//         g2d.setColor(Color.white);
//         g2d.fillRect(r.x,r.y,r.width,r.height);
//
//         g2d.setStroke(new BasicStroke(3.0f));
//         g2d.setColor(Color.red);
//         g2d.drawRect(r.x,r.y,r.width,r.height);
//      }
//      Dimension d = getSize();
//      g2d.setStroke(new BasicStroke(3f));
//      g2d.setColor(Color.green);
//      g2d.drawRect(2,2,d.width-4,d.height-4);

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
      }

      @Override
      public void mouseMoved(MouseEvent e) {
         currentMouseLocation = new Point(e.getX(), e.getY());
      }

      @Override
      public void mousePressed(MouseEvent e) {
            Debug.info("pressed at: " + e.getX() + "," + e.getY());    
            
            p = new Point(e.getPoint());
            
            RecordedClickEvent ce = new RecordedClickEvent();
            ce.setClickLocation(p);         

            Screen s = new Screen();
            BufferedImage image = s.capture(getBounds()).getImage();
            ce.setScreenImage(image);

            clickEvents.add(ce);
            
            ce.export();            
            setVisible(false);
            
            Thread t = new Thread(){
               
               public void run(){
                  focusBelow();            
                  robot.mousePress(InputEvent.BUTTON1_MASK);            
                  robot.mouseRelease(InputEvent.BUTTON1_MASK);
                  robot.waitForIdle();
                  setVisible(true);                  
               }
            };
            t.start();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }
   }
   
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
   
   public void focusBelow(){
      if(Env.getOS() == OS.MAC){
         // TODO: replace this hack with a more robust method

         // Mac's hack to bring focus to the window directly underneath
         // this hack works on the assumption that the caller has
         // the input focus but no interaction area at the current
         // mouse cursor position
         // This hack does not work well with applications that
         // can receive mouse clicks without having the input focus
         // (e.g., finder, system preferences)
         //         robot.mousePress(InputEvent.BUTTON1_MASK);            
         //         robot.mouseRelease(InputEvent.BUTTON1_MASK);

         // Another temporary hack to switch to the previous window on Mac
         robot.keyPress(KeyEvent.VK_META);
         robot.keyPress(KeyEvent.VK_TAB);
         robot.keyRelease(KeyEvent.VK_META);
         robot.keyRelease(KeyEvent.VK_TAB);

         // wait a little bit for the switch to complete
         robot.delay(1000);
      }

   }
}
