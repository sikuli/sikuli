package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.sikuli.script.App;
import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.FindFailedResponse;
import org.sikuli.script.Location;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.SikuliRobot;
import org.sikuli.script.TransparentWindow;

public class SikuliGuide extends TransparentWindow {


   static final float DEFAULT_SHOW_DURATION = 10.0f;

   Robot robot;

   // all the actions will be restricted to this region
   Region _region;

   // swing components will be drawn on this panel
   JPanel content = new JPanel(null);

   ArrayList<Annotation> _annotations = new ArrayList<Annotation>();
   
   ArrayList<ClickTarget> _clickTargets = new ArrayList<ClickTarget>();

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


      Color transparentColor = new Color(0F,0F,0F,0.0F);
      setBackground(transparentColor);
      content.setBackground(transparentColor);


      if(Env.getOS() == OS.WINDOWS){
         Env.getOSUtil().setWindowOpaque(this, false);
      }

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);

      setVisible(false);
      setAlwaysOnTop(true);
      setFocusableWindowState(false);
   }


   public void paint(Graphics g){

      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);
      
      
      for (Annotation an : _annotations){
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON);			
         an.paintAnnotation(g2d);
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
      }
   }

   public void clear(){
      _annotations.clear();
      content.removeAll();
      dialog = null;
      _clickTargets.clear();
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
   
   public void addHighlight(Region region){	
      Rectangle rect = new Rectangle(region.getRect());
      rect.translate(-_region.x, -_region.y);
      addAnnotation(new AnnotationHighlight(rect));
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

   public void addText(Location location, String message){
      // The location is in the global screen coordinate

      // the margin to the screen boundary
      final int margin = 5;

      //String tooltipStyle = "font-size:16px;background-color:#FFFFDD;padding:3px;";
      String bwStyle = "font-size:16px;color:white;background-color:#333333;padding:3px";

      String htmltxt = 
         "<html><div style='" + bwStyle + "'>"
         + message + "</div></html>";

      JLabel textbox = new JLabel(htmltxt);
      Dimension size = textbox.getPreferredSize();
      if (size.width > 300){
         // hack to limit the width of the text to 300px
         htmltxt = 
            "<html><div style='width:300;" + bwStyle + "'>"
            + message + "</div></html>";
         textbox = new JLabel(htmltxt);
      }
      size = textbox.getPreferredSize();

      int x_origin = location.x;
      int y_origin = location.y;

      Screen screen = _region.getScreen();

      Location screen_br = screen.getBottomRight();
      Location region_br = _region.getBottomRight();

      // calculate how much the text box goes over the screen boundary
      int x_overflow = x_origin + size.width - Math.min(screen_br.x, region_br.x);
      if (x_overflow > 0){
         x_origin -= x_overflow;
         x_origin -= margin; 
      }

      int y_overflow = y_origin + size.height - Math.min(screen_br.y, region_br.y);
      if (y_overflow > 0){
         y_origin -= y_overflow;
         y_origin -= margin;
      }

      // convert to region coordinate
      x_origin -= _region.x;
      y_origin -= _region.y;

      textbox.setBounds(x_origin,y_origin,size.width,size.height);
      content.add(textbox);
      repaint();
   }

   SingleButtonMessageBox dialog = null;
   public void addDialog(String button_text, String message){
      dialog = new SingleButtonMessageBox(this, button_text, message);    
      dialog.setAlwaysOnTop(true);
      dialog.pack();
      dialog.setLocationRelativeTo(this);
   }


   ClickTarget _lastClickedTarget = null;

   public ClickTarget getLastClickedTarget() {
      return _lastClickedTarget;
   }

   public void setLastClickedTarget(ClickTarget lastClickedTarget) {
      this._lastClickedTarget = lastClickedTarget;
   }

   public void showNow(){
      showNow(DEFAULT_SHOW_DURATION);
   }

   public void showNow(float secs){

      // do these to allow static elements to be drawn
      setVisible(true);
      toFront();


      // deal with interactive elements

      if (dialog != null){

         for (ClickTarget target : _clickTargets){
            target.setVisible(true);        
            target.setIgnoreMouse(true);
         }

         dialog.setVisible(true);
         synchronized(this){
            try {
               wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         for (ClickTarget target : _clickTargets){
            target.dispose();
         }        
         dialog.dispose();

         closeNow();

         focusBelow();

      }else if (!_clickTargets.isEmpty()){

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


         for (ClickTarget target : _clickTargets){
            target.dispose();
         }

         Debug.log("Last clicked:" + _lastClickedTarget.getName());

         closeNow();
         focusBelow();
  
         robot.mousePress(InputEvent.BUTTON1_MASK);            
         robot.mouseRelease(InputEvent.BUTTON1_MASK);

      }else{

         // if there's no interactive element
         // just close it after the timeout
         closeAfter(secs);
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



}
