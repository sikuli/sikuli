package org.sikuli.guide;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;



// TODO: Automatically move mouse cursor to the click target. The current implementation
// is problematic for non-rectangular clickable widgets, for instance, a round buttone. 
// Since the highlighted region is always rectangular and is larger than the area that
// is actually cliable, users may click on the edge of the region and dismiss the window
// errorneously. This needs to be fixed.

public class EditorWindowExp extends JWindow 
implements MouseListener, Transition, GlobalMouseMotionListener, MouseMotionListener {

   ArrayList<Clickable> clickables = new ArrayList<Clickable>();
   class Clickable extends JComponent {
      
         Color normalColor = new Color(1.0f,1.0f,0,0.1f);
         Color mouseOverColor = new Color(1.0f,0,0,0.1f);

         String name;
         Region region;
         public Clickable(Region region){
            this.region = region;
            this.setBounds(region.getRect());
            this.setLocation(region.x,region.y);
         }

         public void setName(String name){
            this.name = name;
         }
         
         boolean mouseOver;
         public void setMouseOver(boolean mouseOver){
            if (this.mouseOver != mouseOver){
               repaint();
            }            
            this.mouseOver = mouseOver;
            
         }
         
         public void paintComponent(Graphics g){
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            
            if (mouseOver){
               g2d.setColor(mouseOverColor);
            }else{
               g2d.setColor(normalColor);
            }
            
            g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
            g2d.setColor(Color.white);
            g2d.drawRect(0,0,getWidth()-1,getHeight()-1);

         }
   }

   Point clickLocation;
   public Point getClickLocation() {
      return clickLocation;
   }

   SikuliGuide guide;
   GlobalMouseMotionTracker mouseTracker;
   Clickable lastClickedClickable;
   
   public EditorWindowExp(JFrame f){
     // super(f);
      
      // this allows us to layout the components ourselves
     // setLayout();

      // this window should cover the same area as the guide
      setBounds(new Screen().getRect());//guide.getBounds());
      
      setAlwaysOnTop(true);


//      mouseTracker = GlobalMouseLocationTracker.getInstance();
//      mouseTracker.addListener(this);

      //Color bg = new Color(1.0f,1.0f,1.0f,1.0f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      Color bg = Color.black;

      JTextPane p = new JTextPane();
      p.setContentType("text/html");
      p.setText("<font size=20>This is some text</font>");
      p.setBounds(getBounds());
      //p.setOpaque(true);
      p.requestFocus();
      //add(p);
      
      //Env.getOSUtil().setWindowOpacity(this, 0.5f);

      
      //add(new JTextField(50));
      
      getContentPane().setBackground(bg);
      setBackground(bg);
     // p.setBackground(bg);

      
      //getContentPane().setOpaque(false);
      // This makes the JWindow transparent
      Env.getOSUtil().setWindowOpaque(this, false);
      
      // TODO: figure out how to make this JWindow non-draggable
      // 1) associate this window with a JFrame and make the JFrame not movable
//      setFocusableWindowState(false);
//      setFocusable(false);
//      setEnabled(false);      
//      getContentPane().setFocusable(false);
//      getRootPane().setFocusable(false);
      
      // capture the click event
      addMouseListener(this);
      addMouseMotionListener(this);

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });
      
   }
   
   
   @Override
   public void setLocation(int x, int y){   
   }
   
   @Override
   public void setLocation(Point p){
   
   }
   
   @Override
   public void setBounds(int x,int y,int w,int h){ 
      super.setBounds(0,0,w,h);
   }

   
   public void addClickableRegion(Region region, String name){
      Clickable c = new  Clickable(region);
      c.setName(name);
      clickables.add(c);
      add(c);
   }

   @Override
   // notifies the owner of this click target that the target has
   // been clicked
   public void mouseClicked(MouseEvent e) {
      //Debug.log("clicked on " + e.getX() + "," + e.getY());
      Point p = e.getPoint();

      lastClickedClickable  = null;
      // find clicked clickable
      for (Clickable c : clickables){         
         if (c.getBounds().contains(p)){
            lastClickedClickable = c;
         }
      }         
      
      if (lastClickedClickable != null){
      
         synchronized(guide){
            guide.notify();
         }
      }
   }

   @Override
   public void mouseEntered(MouseEvent arg0) {
   }

   @Override
   public void mouseExited(MouseEvent arg0) {
   }

   @Override
   public void mousePressed(MouseEvent arg0) {
      Debug.log("pressed");
//      setLocation(100,100);
//      repaint();
   }

   @Override
   public void mouseReleased(MouseEvent arg0) {
      Debug.log("released");
//      setLocation(0,0);
//      repaint();
   }
   

   @Override
   public String waitForTransition() {
      
      toFront();
      setVisible(true);
      setAlwaysOnTop(true);
      
      //mouseTracker.start();      
      
      synchronized(this){
         try {
            this.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      setVisible(false);
      
      if (lastClickedClickable != null)
         return lastClickedClickable.name;
      else
         return "Next";
   }
   
   Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
   Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
   Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
   Cursor currentCursor = null;

   @Override
   public void globalMouseMoved(int x, int y) {
      //Debug.log("moved to " + x + "," + y);
      
      Point p = new Point(x,y);
      for (Clickable c : clickables){
         
         c.setMouseOver(c.getBounds().contains(p));
         
         
         // TODO: figure out why setCursor is not working
//         if (c.getBounds().contains(p)){
//            Debug.log("inside");
//            cursor = handCursor;
//            this.getContentPane().setCursor(hourglassCursor);
//            setCursor(hourglassCursor);
//            getOwner().setCursor(hourglassCursor);
//            c.setCursor(hourglassCursor);
//         }
         
         
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

   public void clear() {
      clickables.clear();
      getContentPane().removeAll();      
   }

   @Override
   public void globalMouseIdled(int x, int y) {
   }

   
   Point mouseLoc;
   @Override
   public void mouseDragged(MouseEvent e) {
      Debug.info("dragged to: " + e.getX() + "," + e.getY());
      mouseLoc = e.getPoint();
      repaint();
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      Debug.info("moved to: " + e.getX() + "," + e.getY());     
      mouseLoc = e.getPoint();
      repaint();
      
   }
   
   @Override
   public void paint(Graphics g){
      super.paint(g);
      
      Graphics2D g2d = (Graphics2D) g;
      
   //   g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));        
//      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
//            RenderingHints.VALUE_ANTIALIAS_ON);       

      if (mouseLoc != null){
     // g2d.setColor(Color.white);
      //g2d.fillRect(mouseLoc.x,mouseLoc.y,100,100);
         g2d.setColor(Color.black);
         g2d.drawRect(mouseLoc.x,mouseLoc.y,100,100);
      }

//      setLocation(0,0);
//      setBounds(10,10, 500,500);
//      setLocation(0,0);
   }

}