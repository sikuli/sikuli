package org.sikuli.guide;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JWindow;

import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Region;



// TODO: Automatically move mouse cursor to the click target. The current implementation
// is problematic for non-rectangular clickable widgets, for instance, a round buttone. 
// Since the highlighted region is always rectangular and is larger than the area that
// is actually cliable, users may click on the edge of the region and dismiss the window
// errorneously. This needs to be fixed.

public class ClickableWindow extends JWindow 
implements MouseListener, Transition, GlobalMouseMotionListener {

   ArrayList<Clickable> clickables = new ArrayList<Clickable>();
   Point clickLocation;
   public Point getClickLocation() {
      return clickLocation;
   }

   SikuliGuide guide;
   GlobalMouseMotionTracker mouseTracker;
   private Clickable lastClickedClickable;
   
   public ClickableWindow(SikuliGuide guide){
      this.guide = guide;
      
      // this allows us to layout the components ourselves
      setLayout(null);

      // this window should cover the same area as the guide
      setBounds(guide.getBounds());
      
      setAlwaysOnTop(true);


      mouseTracker = GlobalMouseMotionTracker.getInstance();
      mouseTracker.addListener(this);


      getContentPane().setBackground(null);
      setBackground(null);

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

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });
      
   }
   
   public void addClickable(Clickable c){
      clickables.add(c);
      //Debug.info("[ClickableWindow] adding clickable");
      
      // add an almost invisible clickable to the content pane to 
      // capture the mouse click happening to this region
      Clickable c1 = new Clickable(null);
      c1.setLocationRelativeToComponent(c, Layout.OVER);
      this.add(c1);
   }
   
   public void addClickableRegion(Region region, String name){
      Clickable c = new  Clickable(region);
      c.setName(name);
      addClickable(c);
   }

   
   @Override
   // notifies the owner of this click target that the target has
   // been clicked
   public void mouseClicked(MouseEvent e) {
      Debug.log("[ClickableWindow] clicked on " + e.getX() + "," + e.getY());
      Point p = e.getPoint();

      
      lastClickedClickable = null;
      // find and remember which clickable was most recently clicked
      for (Clickable c : clickables){         
         if (c.getActualBounds().contains(p)){
            lastClickedClickable = c;
            p.x -= c.getX();
            p.y -= c.getY();
            c.globalMouseClicked(p);
         }
      }         
      
      if (getLastClickedClickable() != null){      
         if (token != null){
            // hide so that the click can go through and hit the interface below
            setVisible(false);
            token.transitionOccurred(this);            
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
   }

   @Override
   public void mouseReleased(MouseEvent arg0) {
   }

   
//   @Override
//   public void stopTransition(){      
//   }
   
   
   TransitionListener token;
   @Override
   public String waitForTransition(TransitionListener token) {
      this.token = token;
      
      toFront();
      setVisible(true);
      setAlwaysOnTop(true);
      
      // force the invisible clickables to repaint
      //repaint();
      
      mouseTracker.start();      
      
//      synchronized(guide){
//         try {
//            guide.wait();
//         } catch (InterruptedException e) {
//            e.printStackTrace();
//         }
//      }
//      
//      if (getLastClickedClickable() != null)
//         return getLastClickedClickable().name;
//      else
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
         
         if (c.getBounds().contains(p)){
            Point cp = c.getLocation();
            p.x -= cp.x;
            p.y -= cp.y;
            c.globalMouseMoved(p);
         }
         
         
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
            
      //Debug.log("loc: " + getLocation());
      
      // keep moving to (0,0) to nullify the dragged move bug
      setLocation(0,0);

   }
   
   public void clear() {
      clickables.clear();
      getContentPane().removeAll();      
   }

   @Override
   public void globalMouseIdled(int x, int y) {
   }
   
   public Clickable getLastClickedClickable() {
      return lastClickedClickable;
   }

   public ArrayList<Clickable> getClickables() {
      return clickables;
   }

}