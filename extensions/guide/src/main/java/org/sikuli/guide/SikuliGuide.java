package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

public class SikuliGuide extends TransparentWindow {


   static float defaultTimeout = 10.0f;


   static public final int FIRST = 0;
   static public final int MIDDLE = 1;
   static public final int LAST = 2;
   static public final int SIMPLE = 4;


   Robot robot;
   
   public void setDefaultTimeout(float timeout_in_seconds){
      defaultTimeout = timeout_in_seconds;
   }

   // all the actions will be restricted to this region
   Region _region;

   public Region getRegion(){
      return _region;
   }

   // swing components will be drawn on this panel
   JPanel content = new JPanel(null);
   //Container content;
   
   
   Transition transition;
   //Transition defaultTransition;   
   
   ArrayList<Tracker> trackers = new ArrayList<Tracker>();
   ClickableWindow clickableWindow;
   
   
   public SikuliGuide(){
      init(new Screen());
   }

   public SikuliGuide(Region region) {
      init(region);
   }

   void init(Region region){
      
      try {
         robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }

      _region = region;      
      Rectangle rect = _region.getRect();
      content.setPreferredSize(rect.getSize());
      add(content);

      setBounds(rect);

      setBackground(null);

      // It turns out these are useful after all
      // so that it works on Windows
      ((JPanel)getContentPane()).setBackground(null);      
     // content.setBackground(null);

      Env.getOSUtil().setWindowOpaque(this, false);
      //setOpacity(1.0f);

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);

      setVisible(false);
      setAlwaysOnTop(true);
      setFocusableWindowState(false);

        
      clickableWindow = new ClickableWindow(this);
   }

   
   
   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);
   }

   public void clear(){
      
      if (clickableWindow != null)
         clickableWindow.clear();
      
      stopAnimation();
      
      setBackground(null);
      content.setBackground(null);

      for (Tracker track : trackers){
         track.stopTracking();
      }
      trackers.clear();
      
      
      content.removeAll();
      transition = null;
      beam = null;

   }

   SearchDialog search = null;
   public void addSearchDialog(){
      search = new SearchDialog(this, "Enter the search string:");
      //search = new GUISearchDialog(this);
      search.setLocationRelativeTo(null);
      search.setAlwaysOnTop(true);
   }

   public void setSearchDialog(SearchDialog search){
      this.search = search;
   }

   public void addSearchEntry(String key, Region region){
      if (search == null)
         addSearchDialog();
      search.addEntry(key, region);
   }

   Point convertToRegionLocation(Point point_in_global_coordinate){
      Point ret = new Point(point_in_global_coordinate);
      ret.translate(-_region.x, -_region.y);
      return ret;
   }

//   public void addArrow(Point from, Point to){
//      Point from1 = convertToRegionLocation(from);
//      Point to1 = convertToRegionLocation(to);
//
//      //      addAnnotation(new AnnotationArrow(from1, to1, Color.black));
//   }

   public void updateSpotlights(ArrayList<Region> regions){
      removeSpotlights();

      if (regions.isEmpty()){

         setBackground(null);
         content.setBackground(null);

      }else{

         // if there are spotlights added, darken the background
         setBackground(new Color(0f,0f,0f,0.2f));
         content.setBackground(new Color(0f,0f,0f,0.2f));
         for (Region r : regions){
            SikuliGuideSpotlight spotlight = new SikuliGuideSpotlight(r);
            spotlight.setShape(SikuliGuideSpotlight.CIRCLE);
            //addSpotlight(r,SikuliGuideSpotlight.CIRCLE);
         }
      }

      repaint();
   }

   public void removeSpotlights(){
      for (Component co : content.getComponents()){
         if (co instanceof SikuliGuideSpotlight){
            content.remove(co);
         }
      }
   }

//   public SikuliGuideSpotlight addSpotlight(Region region){   
//      return addSpotlight(region, SikuliGuideSpotlight.RECTANGLE);
//   }

//   public SikuliGuideSpotlight addSpotlight(Region region, int style){	
//      //      Rectangle rect = new Rectangle(region.getRect());
//      //      rect.translate(-_region.x, -_region.y);
//      //      
//
//      SikuliGuideSpotlight spotlight = new SikuliGuideSpotlight(this,region);
//      spotlight.setShape(style);
//      addComponent(spotlight);
//      //_spotlights.add(spotlight);
//
//      // if there's any spotlight added, darken the 
//      // background
//      //setBackground(new Color(0f,0f,0f,0.2f));      
//      //content.setBackground(new Color(0f,0f,0f,0.2f));      
//
//      return spotlight;
//   }

   //   public void addRectangle(Region region){  
   //      Rectangle rect = new Rectangle(region.getRect());
   //      rect.translate(-_region.x, -_region.y);
   //      addAnnotation(new AnnotationRectangle(rect));
   //   }

   public void addClickable(Clickable c){
      clickableWindow.addClickable(c);
      setTransition(clickableWindow);      
   }

   //   public void addToolTip(Location location, String message){
   //      Point screen_loc = convertToRegionLocation(location);
   //      addAnnotation(new AnnotationToolTip(message, screen_loc));
   //   }
//
//   public enum VerticalAlignment{
//      TOP, MIDDLE, BOTTOM
//   }
//
//   public enum HorizontalAlignment{
//      LEFT, CENTER, RIGHT
//   }

//   public void addText(Location location, String message){
//      // The location is in the global screen coordinate
//
//      addText(location, message, HorizontalAlignment.LEFT, VerticalAlignment.TOP);      
//   }

//   public void addFlag(Location location, String message){
//      Flag flag = new Flag(location, message);
//      addComponent(flag);
//   }

   public void addComponent(SikuliGuideComponent comp){
      content.add(comp);
      if (comp instanceof SikuliGuideSpotlight){
         // if there's any spotlight added, darken the 
         // background
         setBackground(new Color(0f,0f,0f,0.2f));
         content.setBackground(new Color(0f,0f,0f,0.2f));
      }
      
      if (comp instanceof SikuliGuideText ||
            comp instanceof SikuliGuideRectangle ||
            comp instanceof SikuliGuideCircle ||
            comp instanceof SikuliGuideArrow ||
            comp instanceof SikuliGuideImage ||
            comp instanceof SikuliGuideFlag ||
            comp instanceof SikuliGuideCallout ||
            comp instanceof SikuliGuideBracket) {
         SikuliGuideComponent s = new SikuliGuideShadow(comp);
         content.add(s);
      }
   }

   public void removeComponent(Component comp){
      content.remove(comp);
   }

//   public void addBookmark(Location location, String message){
//      Flag b = new Flag(location, message);
//      //   b.setLocation(location);
//      //b.setBounds(_region.getRect());
//      content.add(b);
//   }

   Beam beam = null;
   public void addBeam(Region r){
      beam = new Beam(this, r);
      beam.setAlwaysOnTop(true);
      transition = beam;
   }

//   public void addText(Location location, String message, HorizontalAlignment horizontal_alignment, 
//         VerticalAlignment vertical_alignment){
//      StaticText textbox = new StaticText(this, message);
//      textbox.align(location, horizontal_alignment, vertical_alignment);
//      textbox.moveInside(_region);
//      content.add(textbox);
//      repaint();
//   }

//   public enum Side {
//      TOP,
//      LEFT,
//      RIGHT,
//      BOTTOM
//   }

//   public void addText(Region r, String message, Side side){
//      HorizontalAlignment h = null;
//      VerticalAlignment v = null;      
//      Location p = null;      
//
//      if (side == Side.TOP){
//         p = new Location(r.x+r.w/2, r.y);
//         h = HorizontalAlignment.CENTER;
//         v = VerticalAlignment.BOTTOM;
//      } else if (side == Side.BOTTOM){
//         p = new Location(r.x+r.w/2, r.y+r.h);
//         h = HorizontalAlignment.CENTER;
//         v = VerticalAlignment.TOP; 
//      } else if (side == Side.LEFT){
//         p = new Location(r.x, r.y+r.h/2);
//         h = HorizontalAlignment.RIGHT;
//         v = VerticalAlignment.MIDDLE; 
//      } else if (side == Side.RIGHT){
//         p = new Location(r.x+r.w, r.y+r.h/2);
//         h = HorizontalAlignment.LEFT;
//         v = VerticalAlignment.MIDDLE; 
//      }      
//      addText(p, message, h, v);
//   }

//   TransitionDialog dialog = null;
//   public void setDialog(String message, int style){
//      TransitionDialog dialog = new TransitionDialog();
//      dialog.setText(message);
////      dialog.setStyle(style);
//      transition = dialog;
//   }
//
//   public TransitionDialog getDialog(){
//      return dialog;
//   }

   public void setDialog(String message){
      TransitionDialog dialog = new TransitionDialog();
      dialog.setText(message);
      transition = dialog;
   }

   public void setDialog(TransitionDialog dialog_){
      //dialog = dialog_;
      transition = dialog_;
   }

   
   public void stopAnimation(){
      for (Component co : content.getComponents()){

         if (co instanceof Magnifier){
            ((Magnifier) co).start();
         }

         if (co instanceof SikuliGuideComponent){
            ((SikuliGuideComponent) co).stopAnimation();
         }
      }
   }
   
   public void startAnimation(){
      for (Component co : content.getComponents()){

         if (co instanceof Magnifier){
            ((Magnifier) co).start();
         }

         if (co instanceof SikuliGuideComponent){
            ((SikuliGuideComponent) co).startAnimation();
         }
      }
   }
   

//   public String showNowWithDialog(int style){
//      //dialog.setStyle(style);
//      //setTransition(dialog);
//      return showNow();        
//   }

   public String showNow(){
      return showNow(defaultTimeout);
   }

   public String showNow(float secs){
      
      if (content.getComponentCount()  == 0 
            && transition == null 
            && search == null){
         // if no component at all, return immediately because
         // there's nothing to show
         return "Next";         
      }
      
      startAnimation();      
      startTracking();

      // do these to allow static elements to be drawn
      setVisible(true);
      toFront();


      // deal with interactive elements
      if (search != null){

         search.setVisible(true);
         search.requestFocus();



         synchronized(this){
            try {
               wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         search.dispose();
         search.setVisible(false);

         String key = search.getSelectedKey();
         search = null;
         closeNow();
         focusBelow();
         return key;
      }
      //else if (transition != null){
         
         if (transition == null){
            transition = new TimeoutTransition((int)secs*1000);
         }
         
         String cmd = transition.waitForTransition();
         focusBelow();
         
         if (transition instanceof ClickableWindow){
            // relay the click at the same position
            //Debug.info("clicking");
            robot.mousePress(InputEvent.BUTTON1_MASK);            
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
         }
         
         closeNow();
        
         return cmd;
//      }
//      else if (!_clickTargets.isEmpty()){
//
//         for (ClickTarget target : _clickTargets){
//            target.setVisible(true);
//         }
//
//         synchronized(this){
//            try {
//               wait();
//            } catch (InterruptedException e) {
//               e.printStackTrace();
//            }
//         }
//
//         Debug.log("Last clicked:" + _lastClickedTarget.getName());
//
//         closeNow();
//         focusBelow();
//
//         robot.mousePress(InputEvent.BUTTON1_MASK);            
//         robot.mouseRelease(InputEvent.BUTTON1_MASK);
//
//         return SikuliGuideDialog.NEXT;

//      }else {
//
//
//         // if there's no transition element
//         // just close it after the timeout
//         closeAfter(secs);
//
//         return BaseDialog.NEXT;
//      } 
   }

   private void closeNow(){
      clear();
      setVisible(false);
      
      dispose();
//      if (dialog != null)
//         dialog.dispose();
      
      if (clickableWindow != null){
         clickableWindow.dispose();
      }
   }

   private void closeAfter(float secs){
      try{
         Thread.sleep((int)secs*1000);
      }
      catch(InterruptedException e){
         closeNow();
         e.printStackTrace();
      }
      closeNow();
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

   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
         Env.getOSUtil().bringWindowToFront(this, true);
      }     
      super.toFront();
   }
//   public void addImage(Location location, String filename) {
//      addImage(location,filename,1.0f);
//   }


//   public void addImage(Location location, String filename, float scale) {
//      BufferedImage bimage = null;
//      try {
//         File sourceimage = new File(filename);
//         bimage = ImageIO.read(sourceimage);
//         SikuliGuideImage img = new SikuliGuideImage(bimage);
//         img.setLocation(location);
//         img.setScale(scale);
//         content.add(img);
//      } catch (IOException ioe) {
//         ioe.printStackTrace();
//      }
//   }

   public void addMagnifier(Region region) {
      Magnifier mag = new Magnifier(this,region);
      content.add(mag);
   }

   public void setTransition(Transition t) {
      this.transition = t;      
   }

   public void removeComponents() {
      content.removeAll();
   }
   
   
   public void startTracking(){
      for (Tracker tracker : trackers){
         tracker.start();
      }
   }

   public void addTracker(Pattern pattern, Region r, SikuliGuideComponent c){     
      Tracker tracker = null;
      
      // find a tracker already assigned to the filename
      for (Tracker t : trackers){
         if (t.isAlreadyTracking(pattern,r)){
            tracker = t;
            break;
         }
      }      
      
      if (tracker == null){
         tracker = new Tracker(this, pattern, r);
         trackers.add(tracker);
      }
      
      tracker.addReferencingComponent(c);
   }
   
   public void addTracker(String image_filename, Region r, SikuliGuideComponent c){     
      Tracker tracker = null;
      
      // find a tracker already assigned to the filename
      for (Tracker t : trackers){
         if (t.isAlreadyTracking(image_filename,r)){
            tracker = t;
            break;
         }
      }      
      
      if (tracker == null){
         tracker = new Tracker(this, image_filename, r);
         trackers.add(tracker);
      }
      
      tracker.addReferencingComponent(c);
   }

   public void addTracker(String image_filename, Region r, ArrayList<SikuliGuideComponent> components) {
      Tracker tracker = new Tracker(this, image_filename, r);
      for (SikuliGuideComponent c : components){
         tracker.addReferencingComponent(c);
      }      
      trackers.add(tracker);   
   }
   
   
}



