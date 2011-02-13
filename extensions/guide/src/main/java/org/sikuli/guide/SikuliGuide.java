package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
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


   static public final int FIRST = 0;
   static public final int MIDDLE = 1;
   static public final int LAST = 2;
   static public final int SIMPLE = 4;


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
   
   public void addComponent(Component comp){
      content.add(comp);
   }
   
   public void addBookmark(Location location, String message){
      Flag b = new Flag(location, message);
   //   b.setLocation(location);
      //b.setBounds(_region.getRect());
      content.add(b);
   }
   
   
   class StaticText extends JLabel{

      static final String style = "font-size:16px;color:white;background-color:#333333;padding:3px";

      String raw_text;
      StaticText(String text){         
         super();
         raw_text = text;

         String htmltxt = 
            "<html><div style='" + style + "'>"  + raw_text + "</div></html>";

         setText(htmltxt);
         setMaximumWidth(300);
         
         setSize(getPreferredSize());
      }

      void setMaximumWidth(int min_width){
         Dimension size = getPreferredSize();
         if (size.width > min_width){
            // hack to limit the width of the text to 300px
            String htmltxt = 
               "<html><div style='width:" + min_width + ";" + style + "'>"
               + raw_text + "</div></html>";
            setText(htmltxt);
         }
      }
      
      void moveInside(Region region){
         
         // the margin to the boundary
         final int margin = 5;
         
         Point p = getLocation();
         Dimension size = getSize();
         
         int x_origin = p.x;
         int y_origin = p.y;

         Screen screen = region.getScreen();

         Location screen_br = screen.getBottomRight();
         Location region_br = region.getBottomRight();

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

         setBounds(x_origin,y_origin,size.width,size.height);
         
      }
      
      public void align(Location location, HorizontalAlignment horizontal_alignment, 
         VerticalAlignment vertical_alignment){
         
         setLocation(location);
         Dimension size = getSize();
         
         
         // adjust the location based on the alignment
         if (horizontal_alignment == HorizontalAlignment.CENTER){
            location.x -= size.width/2;
         }else if (horizontal_alignment == HorizontalAlignment.RIGHT){
            location.x -= size.width;
         }

         if (vertical_alignment == VerticalAlignment.MIDDLE){
            location.y -= size.height/2;
         }else if (vertical_alignment == VerticalAlignment.BOTTOM){
            location.y -= size.height;
         }

         setLocation(location);
      }
   }

   public void addText(Location location, String message, HorizontalAlignment horizontal_alignment, 
         VerticalAlignment vertical_alignment){

     

      StaticText textbox = new StaticText(message);

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
      
      
      //loc.y = r.y;
      
      addText(p, message, h, v);
      
   }

   NavigationDialog dialog = null;
   public void addDialog(String message, int style){
      //dialog = new SingleButtonMessageBox(this, button_text, message);  
      dialog = new NavigationDialog(this, message, style);  
      dialog.setAlwaysOnTop(true);
      dialog.pack();
      //dialog.setLocationRelativeTo(this);
   }

   public void addDialog(String message){
      addDialog(message, SIMPLE);
      dialog.setLocationRelativeTo(this);
   }
   
   public void addDialog(NavigationDialog dialog_){
      dialog = dialog_;
      dialog.setAlwaysOnTop(true);
      dialog.pack();
      //dialog.setLocationRelativeTo(this);
   }

   ClickTarget _lastClickedTarget = null;

   public ClickTarget getLastClickedTarget() {
      return _lastClickedTarget;
   }

   public void setLastClickedTarget(ClickTarget lastClickedTarget) {
      this._lastClickedTarget = lastClickedTarget;
   }


   public String showNowWithDialog(int style){

      // create the default dialog, unless the user
      // has already added one
      if (dialog == null){
         addDialog("",style);
         dialog.setLocationRelativeTo(this);
      } else{
         dialog.setStyle(style);
      }
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
         String cmd = dialog.getActionCommand();

         closeNow();

         focusBelow();

         return cmd;

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

         return SikuliGuideDialog.NEXT;

      }else{

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



}
