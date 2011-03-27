package org.sikuli.guide;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OpenCV;
import org.sikuli.script.Region;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.TransparentWindow;
import org.sikuli.script.natives.FindResult;
import org.sikuli.script.natives.FindResults;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.Vision;



// TODO: Automatically move mouse cursor to the click target. The current implementation
// is problematic for non-rectangular clickable widgets, for instance, a round buttone. 
// Since the highlighted region is always rectangular and is larger than the area that
// is actually cliable, users may click on the edge of the region and dismiss the window
// errorneously. This needs to be fixed.

public class BlobWindow extends JWindow 
implements MouseListener, Transition, GlobalMouseMotionListener {

   ArrayList<Clickable> clickables = new ArrayList<Clickable>();
   class Clickable extends JComponent {
      
         Color normalColor = new Color(1.0f,1.0f,0,0.1f);
         Color mouseOverColor = new Color(1.0f,0,0,0.1f);

         Region region;
         public Clickable(Region region){
            this.region = region;
            this.setBounds(region.getRect());
            this.setLocation(region.x,region.y);
            
            Debug.info("bounds: " + getBounds());
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
   GlobalMouseLocationTracker mouseTracker;
   public BlobWindow(SikuliGuide guide){
      this.guide = guide;
      
      // this allows us to layout the components ourselves
      setLayout(null);

      setBounds(guide.getBounds());
      
      setAlwaysOnTop(true);


      mouseTracker = GlobalMouseLocationTracker.getInstance();
      mouseTracker.addListener(this);

//            JLabel label = new JLabel("<html><font size=10>This is a clickable window</font></html>");
//            //label.setLocation(20,20);
//            //label.setSize(label.getPreferredSize());
//            label.setOpaque(true);
//            label.setBackground(Color.white);
//            add(label);

      Container panel = this.getContentPane();
      panel.setBackground(null);
      setBackground(null);
      

      // This makes the JWindow transparent
      Env.getOSUtil().setWindowOpaque(this, false);
      
      // TODO: figure out how to make this JWindow non-draggable
      // 1) associate this window with a JFrame and make the JFrame not movable
//      setFocusableWindowState(false);
//      setFocusable(false);
//      setEnabled(false);      
//      getContentPane().setFocusable(false);
      getRootPane().setFocusable(false);
      
      // capture the click event
      addMouseListener(this);

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });
      
   }
   
   public void addClickableRegion(Region region){
      Clickable c = new  Clickable(region);
      clickables.add(c);
      add(c);
   }

   @Override
   // notifies the owner of this click target that the target has
   // been clicked
   public void mouseClicked(MouseEvent e) {
      Debug.log("clicked on " + e.getX() + "," + e.getY());
      clickLocation = e.getPoint();

      synchronized(guide){
         guide.notify();
         // ((SikuliGuide) owner).setLastClickedTarget(this);
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
   }

   @Override
   public void mouseReleased(MouseEvent arg0) {
   }

   @Override
   public String waitForTransition() {
      
      toFront();
      setVisible(true);
      setAlwaysOnTop(true);
      
      mouseTracker.start();      
      
      synchronized(guide){
         try {
            guide.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      //dispose();
      setVisible(false);
      return "Next";
   }
   
   Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
   Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
   Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
   Cursor currentCursor = null;
   
   ConvolveOp getBlurOp(int size) {
      float[] data = new float[size * size];
      float value = 1 / (float) (size * size);
      for (int i = 0; i < data.length; i++) {
          data[i] = value;
      }
      return new ConvolveOp(new Kernel(size, size, data));
  }
   
   public void copyImageAndPaste(Region source, int x, int y){
      
      ScreenImage img = source.getScreen().capture(source);
      
      SikuliGuideImage sgimage = new SikuliGuideImage(img.getImage());
      guide.addComponent(sgimage);
      sgimage.setLocation(x,y);
   }
   
   public void cropImageAndPaste(BufferedImage source, Rectangle r, int x, int y){
      
      BufferedImage subimage = source.getSubimage(r.x, r.y, r.width, r.height);
      
      SikuliGuideImage sgimage = new SikuliGuideImage(subimage);
      sgimage.setScale(2.0f);
      guide.addComponent(sgimage);
      sgimage.setLocation(x,y);
   }

   @Override
   public void globalMouseMoved(int x, int y) {
      Debug.log("moved to " + x + "," + y);

      guide.clear();

      
      Region neighborhood = new Region(x-20,y-20,40,40);
      
      
      //Region largeneighorhood = neig
      int d = 50;
      Region large = new Region(x-d,y-d,2*d,2*d);
      
      //ScreenImage 
      ScreenImage img = neighborhood.getScreen().capture(large);
      
      int blursize = 2;
      BufferedImage bimg = img.getImage();
//      BufferedImage bbimg = new BufferedImage(getWidth() + blursize * 2,
//            getHeight() + blursize * 2, BufferedImage.TYPE_INT_ARGB);
      //getBlurOp(blursize).filter(bimg, bbimg);

      Mat m = OpenCV.convertBufferedImageToMat(bimg);
    FindResults results = Vision.findBlobs(m);
    Debug.info("find " + results.size() + " blobs.");
    
    
    
    
    ArrayList<Region> valid = new ArrayList<Region>();
    for (int i=0; i < results.size()-1; ++i){
       FindResult result = results.get(i);       
       Region r = new Region(result.getX(),result.getY(),result.getW(),result.getH());
       
       if (r.w > 10 && r.h > 10 && r.w < 50 && r.h < 50)
          valid.add(r);     
    }
    
    double theta = 0;
    double dtheta = 2.0f * Math.PI / (double) valid.size();

    
    for (Region r : valid){
//
//    for (int i=0; i < results.size()-1; ++i){
//       FindResult result = results.get(i);
//       
//       Region r = new Region(result.getX(),result.getY(),result.getW(),result.getH());
       
//       int px = large.x + r.x - 200;
//       int py = large.y + r.y;
       
       
       double k = 100;
       int px = (int) (x + k * Math.cos(theta));
       int py = (int) (y + k * Math.sin(theta));
       theta += dtheta;
       
       cropImageAndPaste(bimg, r.getRect(), px, py);
       
       // convert to screen coordinate
       r.x += large.x;
       r.y += large.y;
       
       //copyImageAndPaste(r, x-d-200, y-d);
          
       //guide.addComponent(new SikuliGuideRectangle(guide,r));         
    }
      
   // copyImageAndPaste(large, x-d-200, y-d);
      large.x -= 10;
      large.y -= 10;
      large.h += 20;
      large.w += 20;
      guide.addComponent(new SikuliGuideCircle(large));
      
      guide.repaint();
      
//      Point p = new Point(x,y);
//      for (Clickable c : clickables){
//         
//         c.setMouseOver(c.getBounds().contains(p));
//         
//         
//         // TODO: figure out why setCursor is not working
////         if (c.getBounds().contains(p)){
////            Debug.log("inside");
////            cursor = handCursor;
////            this.getContentPane().setCursor(hourglassCursor);
////            setCursor(hourglassCursor);
////            getOwner().setCursor(hourglassCursor);
////            c.setCursor(hourglassCursor);
////         }
//         
//         
//      }
//            


   }
   
      

   public void clear() {
      clickables.clear(); 
      getContentPane().removeAll();      
   }

   @Override
   public void globalMouseIdled(int x, int y) {      
      Debug.log("idled" + x + "," + y);
      
      Point p = new Point(x,y);
      for (Clickable c : clickables){
         c.setMouseOver(c.getBounds().contains(p));
      }      
   }

}