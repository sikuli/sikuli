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
import java.awt.event.ComponentAdapter;
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

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
      setVisible(false);
      dispose();
      bw.setVisible(false);
      bw.dispose();
      notifyWaiter();
   }
   
   JButton stopButton;
   JButton captureButton;
   JLabel statusLabel;
   JCheckBox capturingClickCheckBox;
   
   // how many to record
   int counter;
   
   class BackgroundWindow extends JWindow{
      
      public BackgroundWindow() {
         //setLayout(null);
         stopButton = new JButton("Close");         
         stopButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               stopCapturing();
            }
            
         });
         
         captureButton = new JButton("Capture");
         captureButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               doRecordScreen();
               counter -= 1;
               if (counter == 0){
                  stopCapturing();
               }
            }
            
         });
         
         statusLabel = new JLabel("   Click inside the window below to capture a step");
         statusLabel.setForeground(Color.white);
         
         
         capturingClickCheckBox = new JCheckBox("Auto capture on click");  
         capturingClickCheckBox.setForeground(Color.white);
         capturingClickCheckBox.setSelected(true);
         capturingClickCheckBox.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
               AbstractButton abstractButton = (AbstractButton)e.getSource();
               boolean selected = abstractButton.getModel().isSelected();
               ScreenRecorderWindow.this.setVisible(selected);
            }
            
         });
         
         setLayout(new BorderLayout());
         
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
         
         panel.add(captureButton);
         panel.add(capturingClickCheckBox);
         panel.add(Box.createHorizontalGlue());
         panel.add(stopButton);
         panel.setBorder(BorderFactory.createLineBorder(Color.white));

         panel.setLocation(0,0);
         panel.setBackground(new Color(0f,0f,0f,1f));
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
         g2d.setStroke(new BasicStroke(1f));
         g2d.setColor(Color.black);
         g2d.drawRect(0,0,d.width-1,d.height-31);
         g2d.setColor(Color.white);
         g2d.drawRect(1,1,d.width-3,d.height-33);
         g2d.setColor(Color.black);
         g2d.drawRect(2,2,d.width-5,d.height-35);

      }
   }
   
   RectangleSelectionMouseAdapter adapter;
   Point currentMouseLocation;
   BackgroundWindow bw;
   
   
   public ScreenRecorderWindow(JFrame owner){

      try {
         robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }
      
      setBackground(null);
      getContentPane().setBackground(null);
      Env.getOSUtil().setWindowOpaque(this, false);
      
      bw = new BackgroundWindow();      
      bw.setVisible(true);
      bw.addComponentListener(new ComponentAdapter(){

         // The purpose is to lock the transparent window in place
         @Override
         public void componentMoved(ComponentEvent e) {            
            Debug.info("Background window moved");
            Point newLocation = bw.getLocation();
            newLocation.y += 30;
            newLocation.x += 2;
            newLocation.y += 2;
            setLocation(newLocation);
         }
      });
   
      //getRootPane().putClientProperty("Window.alpha", new Float(0.0f));
      setAlwaysOnTop(true);

      adapter = new RectangleSelectionMouseAdapter();
      addMouseMotionListener(adapter);
      addMouseListener(adapter);
      
      
   }

   SklEditor editor;
   
   boolean _isAutoCapture;
   public void setAutoCapture(boolean isAutoCapture){
      _isAutoCapture = isAutoCapture;
   }
   
   public void setBounds(Rectangle bounds){
      super.setBounds(bounds);

      Rectangle bwbounds = new Rectangle(getBounds());
      bwbounds.grow(2,2);
      bwbounds.y -= 30;
      bwbounds.height += 30;
      bw.setBounds(bwbounds);      
   }

   Point p = null, q = null;
   Rectangle getSelectedRectangle(){
      if (p == null)
         return null;

      Rectangle r = new Rectangle(p);
      r.add(q);
      return r;
   }   
   
   
   void fireScreenRecorded(RecordedClickEvent ce){
      // TODO: listener callback
      if (editor != null)
         editor.importStep(ce);
      clickEvents.add(ce);
   }
   

   BufferedImage captureScreenImage(){
      return robot.createScreenCapture(getBounds());
   }
   
   void doRecordClick(Point clickLocation){
      RecordedClickEvent ce = new RecordedClickEvent();
      ce.setClickLocation(clickLocation);         

      BufferedImage screenImage = captureScreenImage();
      ce.setScreenImage(screenImage);

      fireScreenRecorded(ce);
   }
   
   void doRecordScreen(){
      RecordedClickEvent ce = new RecordedClickEvent();
      //ce.setClickLocation(new Point(50,50));         

      BufferedImage screenImage = captureScreenImage();
      ce.setScreenImage(screenImage);

      fireScreenRecorded(ce);
   }

   
   class RectangleSelectionMouseAdapter extends MouseAdapter{

      boolean selecting = false;
      boolean running = true;
      Object action;

      @Override
      public void mouseMoved(MouseEvent e) {
         currentMouseLocation = new Point(e.getX(), e.getY());
      }

      @Override
      public void mousePressed(MouseEvent e) {
            Debug.info("pressed at: " + e.getX() + "," + e.getY());                
                        
            // if auto capture on click is not selected
            if (!capturingClickCheckBox.isSelected()){
               // do nothing. just return
               return;
            }
            
            doRecordClick(e.getPoint());
            
            setVisible(false);
            
            counter -= 1;
            if (counter == 0){
               stopCapturing();
               return;
            }
            
            Thread t = new Thread(){
               
               public void run(){
                  Debug.info("[RecorderWindow] replayed click");
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

   public void startModal(int counter) {
      this.counter = counter;
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
   

   public void startModal() {
      startModal(Integer.MAX_VALUE);   
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
