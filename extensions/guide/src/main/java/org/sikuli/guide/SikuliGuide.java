package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.Location;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

public class SikuliGuide extends TransparentWindow {


   static final float DEFAULT_SHOW_DURATION = 10.0f;


   static public final int FIRST = 0;
   static public final int MIDDLE = 1;
   static public final int LAST = 2;
   static public final int SIMPLE = 4;


   Robot robot;

   // all the actions will be restricted to this region
   Region _region;
   
   public Region getRegion(){
      return _region;
   }

   // swing components will be drawn on this panel
   JPanel content = new JPanel(null);

   ArrayList<Annotation> _annotations = new ArrayList<Annotation>();
   ArrayList<Spotlight> _spotlights = new ArrayList<Spotlight>(); 

   ArrayList<ClickTarget> _clickTargets = new ArrayList<ClickTarget>();

   
   SingletonInteractionTarget interactionTarget;
   
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
      content.setBackground(null);

      Env.getOSUtil().setWindowOpaque(this, false);
      //setOpacity(1.0f);

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);

      setVisible(false);
      setAlwaysOnTop(true);
      setFocusableWindowState(false);
      
      
      
      dialog = new NavigationDialog(this);
      dialog.setAlwaysOnTop(true);
      dialog.pack();
      dialog.setLocationRelativeTo(this);
   }


   public void paint(Graphics g){

     
      Graphics2D g2d = (Graphics2D)g;

      super.paint(g);

      if (_spotlights.size() > 0){
      
//         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
//         g2d.setColor(Color.black);
//         g2d.fillRect(0,0,_region.w,_region.h);

         // draw highlight before other annotation elements 
         for (Spotlight h : _spotlights){
            h.paintAnnotation(g2d);
         }
      }

      for (Annotation an : _annotations){
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON);			
         an.paintAnnotation(g2d);
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
      }
   }

   public void clear(){
      for (ClickTarget target : _clickTargets){
         target.dispose();
      }
      setBackground(null);
      content.setBackground(null);
      _spotlights.clear();
      _annotations.clear();
      content.removeAll();
      interactionTarget = null;
      beam = null;
      _clickTargets.clear();
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
   
   public void addAnnotation(Annotation annotation){
      _annotations.add(annotation);
   }

   Point convertToRegionLocation(Point point_in_global_coordinate){
      Point ret = new Point(point_in_global_coordinate);
      ret.translate(-_region.x, -_region.y);
      return ret;
   }

   public void addArrow(Point from, Point to){
      Point from1 = convertToRegionLocation(from);
      Point to1 = convertToRegionLocation(to);

      addAnnotation(new AnnotationArrow(from1, to1, Color.black));
   }

   // Draw an oval containing the given region
   public void addCircle(Region region){
      Location o = region.getCenter();
      Location p = region.getTopLeft();
      o.translate(-_region.x, -_region.y);
      p.translate(-_region.x, -_region.y);

      p.translate((int)((p.x-o.x)*0.44), (int) ((p.y-o.y)*0.44));
      addAnnotation(new AnnotationOval(o.x,o.y,p.x,p.y));
   }


   public void updateSpotlights(ArrayList<Region> regions){
      removeSpotlights();
      
      if (regions.isEmpty()){
         
         setBackground(null);
         content.setBackground(null);
         
      }else{
      
         for (Region r : regions){
            addSpotlight(r,Spotlight.CIRCLE);
         }
      }
      
      repaint();
   }
   
   public void removeSpotlights(){
    
      _spotlights.clear();
   }

   public Spotlight addSpotlight(Region region){   
      return addSpotlight(region, Spotlight.RECTANGLE);
   }
   
   public Spotlight addSpotlight(Region region, int style){	
      Rectangle rect = new Rectangle(region.getRect());
      rect.translate(-_region.x, -_region.y);
      Spotlight spotlight = new Spotlight(rect);
      spotlight.setStyle(style);
      _spotlights.add(spotlight);
      
      // if there's any spotlight added, darken the 
      // background
      setBackground(new Color(0f,0f,0f,0.2f));      
      content.setBackground(new Color(0f,0f,0f,0.2f));      
      
      return spotlight;
   }

   public void addRectangle(Region region){  
      Rectangle rect = new Rectangle(region.getRect());
      rect.translate(-_region.x, -_region.y);
      addAnnotation(new AnnotationRectangle(rect));
   }

   ClickTarget _clickTarget = null;
   public void addClickTarget(Region region, String name){
      _clickTargets.add(new ClickTarget(this, region.getRect(), name));
   }

   public void addToolTip(Location location, String message){
      Point screen_loc = convertToRegionLocation(location);
      addAnnotation(new AnnotationToolTip(message, screen_loc));
   }

   public enum VerticalAlignment{
      TOP, MIDDLE, BOTTOM
   }

   public enum HorizontalAlignment{
      LEFT, CENTER, RIGHT
   }

   public void addText(Location location, String message){
      // The location is in the global screen coordinate

      addText(location, message, HorizontalAlignment.LEFT, VerticalAlignment.TOP);      
   }
   
   public void addFlag(Location location, String message){
      Flag flag = new Flag(location, message);
      addComponent(flag);
   }
   
   public void addComponent(Component comp){
      if (comp instanceof Flag){
         ((Flag) comp).guide = this;
      }
      content.add(comp);
   }
   
   public void addBookmark(Location location, String message){
      Flag b = new Flag(location, message);
   //   b.setLocation(location);
      //b.setBounds(_region.getRect());
      content.add(b);
   }
   
   Beam beam = null;
   public void addBeam(Region r){
      beam = new Beam(this, r);
      beam.setAlwaysOnTop(true);
      interactionTarget = beam;
   }
   
   public void addText(Location location, String message, HorizontalAlignment horizontal_alignment, 
         VerticalAlignment vertical_alignment){
      StaticText textbox = new StaticText(this, message);
      textbox.align(location, horizontal_alignment, vertical_alignment);
      textbox.moveInside(_region);
      content.add(textbox);
      repaint();
   }
   
   public enum Side {
      TOP,
      LEFT,
      RIGHT,
      BOTTOM
   }
   
   public void addText(Region r, String message, Side side){
      HorizontalAlignment h = null;
      VerticalAlignment v = null;      
      Location p = null;      
      
      if (side == Side.TOP){
         p = new Location(r.x+r.w/2, r.y);
         h = HorizontalAlignment.CENTER;
         v = VerticalAlignment.BOTTOM;
      } else if (side == Side.BOTTOM){
         p = new Location(r.x+r.w/2, r.y+r.h);
         h = HorizontalAlignment.CENTER;
         v = VerticalAlignment.TOP; 
      } else if (side == Side.LEFT){
         p = new Location(r.x, r.y+r.h/2);
         h = HorizontalAlignment.RIGHT;
         v = VerticalAlignment.MIDDLE; 
      } else if (side == Side.RIGHT){
         p = new Location(r.x+r.w, r.y+r.h/2);
         h = HorizontalAlignment.LEFT;
         v = VerticalAlignment.MIDDLE; 
      }      
      addText(p, message, h, v);
   }

   NavigationDialog dialog = null;
   public void setDialog(String message, int style){
      dialog.setMessage(message);
      dialog.setStyle(style);
      interactionTarget = dialog;
   }

   public NavigationDialog getDialog(){
      return dialog;
   }
   
   public void setDialog(String message){
      setDialog(message, SIMPLE);
   }
   
   public void setDialog(NavigationDialog dialog_){
      dialog = dialog_;
      interactionTarget = dialog;
   }

   ClickTarget _lastClickedTarget = null;

   public ClickTarget getLastClickedTarget() {
      return _lastClickedTarget;
   }

   public void setLastClickedTarget(ClickTarget lastClickedTarget) {
      this._lastClickedTarget = lastClickedTarget;
   }


   public void startAnimation(){
      for (Component co : content.getComponents()){
         if (co instanceof Flag){
            ((Flag) co).start();
         }
         if (co instanceof Magnifier){
            ((Magnifier) co).start();
         }
      }
   }
   
   public String showNowWithDialog(int style){
      dialog.setStyle(style);
      setTransition(dialog);
      return showNow();        
   }

   public String showNow(){
      return showNow(DEFAULT_SHOW_DURATION);
   }

   public String showNow(float secs){

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
      else if (interactionTarget != null){
         


         for (ClickTarget target : _clickTargets){
            target.setVisible(true);        
            target.setIgnoreMouse(true);
         }
         
         String cmd = interactionTarget.waitUserAction();

         closeNow();
         focusBelow();
         return cmd;
      }
      else if (!_clickTargets.isEmpty()){

         for (ClickTarget target : _clickTargets){
            target.setVisible(true);
         }

         synchronized(this){
            try {
               wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         Debug.log("Last clicked:" + _lastClickedTarget.getName());

         closeNow();
         focusBelow();

         robot.mousePress(InputEvent.BUTTON1_MASK);            
         robot.mouseRelease(InputEvent.BUTTON1_MASK);

         return SikuliGuideDialog.NEXT;

      }else{
         
         startAnimation();

         // if there's no interactive element
         // just close it after the timeout
         closeAfter(secs);

         return SikuliGuideDialog.NEXT;
      }
   }

   private void closeNow(){
      clear();
      setVisible(false);
      dispose();
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
   public void addImage(Location location, String filename) {
      addImage(location,filename,1.0f);
   }
   
   
   public void addImage(Location location, String filename, float scale) {
      BufferedImage bimage = null;
      try {
         File sourceimage = new File(filename);
         bimage = ImageIO.read(sourceimage);
         Image img = new Image(bimage);
         img.setLocation(location);
         img.setScale(scale);
         content.add(img);
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }

   public void addMagnifier(Region region) {
       Magnifier mag = new Magnifier(this,region);
       content.add(mag);
   }

   public void setTransition(SingletonInteractionTarget t) {
      this.interactionTarget = t;      
   }


}



