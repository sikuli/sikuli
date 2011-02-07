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

/** TODO:

- allow users to specify alignment properties explicitly
- dimming highlight effect
- rename to SikuliGuide
- (done) make add[X] uniform. The first argument is always a location.

- Clickable targets
   - allow other kinds of clicks (i.e., double-click, right-click)
   - (done) allow multiple targets

- Dialog
   - auto-advance if used with clickable targets
   - add the ability to customize the position to dislay the dialog box
   - always-on-top? this can be tricky because it will complicate Sikuli's screen capture.
      - solution 1: prompt users to move the dialog box somewhere else when find fails
      - solution 2: automatically position the dialog box outside of the application bounds

- how should clickable targets and the dialog box co-exist?
   - (done) option 1: all clickable targets don't hide until the box is dismissed. but the clicks are passed through


- automatically check whether ui changes have become stable enough to run the next step

- error handling

- ability to update the positions of the annotations when the screen content changes (e.g., scrolled)

- a way to specify pre-conditions, what images must be present to start

- (done) get it to work with multi-screen
- (done) inherit directly from TransparentWindow
- (done) take a Region object to construct
- (done) default to the primary screen (i.e., Screen(0))
- (done) all annotation objects take global screen locations and internally convert to relative screen locations.
- (done) automatically position text so that it won't run outside the screen boundary

 */

public class ScreenAnnotator extends TransparentWindow {


   static final float DEFAULT_SHOW_DURATION = 3.0f;

   Robot robot;

   // all the actions will be restricted to this region
   Region _region;

   // swing components will be drawn on this panel
   JPanel content = new JPanel(null);

   ArrayList<Annotation> _annotations = new ArrayList<Annotation>();
   
   ArrayList<ClickTarget> _clickTargets = new ArrayList<ClickTarget>();

   public ScreenAnnotator(){
      init(new Screen());
   }

   public ScreenAnnotator(Region region) {
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


      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);

      setVisible(false);
      setAlwaysOnTop(true);

   }


   public void paint(Graphics g){

      Graphics2D g2d = (Graphics2D)g;

      // clear the screen
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
      g2d.fillRect(0,0,getWidth(),getHeight());	



      for (Annotation an : _annotations){
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
               RenderingHints.VALUE_ANTIALIAS_ON);			
         an.paintAnnotation(g2d);
      }


      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);

      super.paint(g);
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
      //Location o = region.getTopLeft();
      Location o = region.getCenter();
      Location p = region.getTopLeft();
      o.translate(-_region.x, -_region.y);
      
      p.translate((int)((p.x-o.x)*0.44), (int) ((p.y-o.y)*0.44));
      addAnnotation(new AnnotationOval(o.x,o.y,p.x,p.y));
   }
   
   public void addHighlight(Region region){	
      Rectangle rect = new Rectangle(region.getRect());
      rect.translate(-_region.x, -_region.y);
      addAnnotation(new AnnotationHighlight(rect));
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

         closeAfter(DEFAULT_SHOW_DURATION);
      }

   }

   public void showNow(float secs){

      setVisible(true);
      toFront();

      closeAfter(secs);
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
   
   public static void testICDLSimpleSearch() throws FindFailed{
      App a = new App("Firefox");
      a.focus();
      
      Screen s = new Screen();
      Region r;
      ScreenAnnotator sa = new ScreenAnnotator();
      
      Location o = s.getTopLeft();
      o.translate(10,10);
      sa.addText(o,"1. All the different categories we see in the " +
            "Simple Search are like the shelves in a regular library. Today, " +
            "we are looking for Fairy Tales, so, we are going to look for " +
            "them by clicking on the Fairy Tales button!");

      
      r = s.find(new Pattern("fairy.png").similar(0.95f));
      sa.addClickTarget(r,"");
      sa.addCircle(r);
      sa.showNow();
      
      
      sa.addText(o,"2. If we want to refine our search further, we can select other categories as well. " +
      		"If we only want Fairy Tales that are for ages three to five, we can select " +
      		"the Three to Five button as well. To remove a category from the search," +
      		" click it again to unselect it.");
      r = s.find(new Pattern("three2five.png").similar(0.95f));
      sa.addClickTarget(r,"");
      sa.addCircle(r);
      sa.showNow();
      
      Robot robot=null;
      try {
         robot = new Robot();
      } catch (AWTException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      robot.delay(2000);
      //SikuliRobot robot = new SikuliRobot();
      //rdelay(3);
      
      sa.addDialog("Next","3. Now we can see all the Fairy Tale books for age Three to Five in the library. " +
      		"We can use the arrows in the results section to page through " +
      		"all the different books. ");
      r = s.find(new Pattern("right.png").similar(0.95f));
      sa.addCircle(r);
      r = s.find(new Pattern("left.png").similar(0.95f));
      sa.addCircle(r);
      sa.showNow();
      
      
      sa.addDialog("Finish", "4. To start over with and do a new search, " +
      		"we can click the Trash Can button. To learn how to read a book" +
      		" go to the reading books section.");
      r = s.find(new Pattern("trashcan.png").similar(0.95f));
      sa.addCircle(r);
      sa.showNow();
   }

   public static void testICDL() throws FindFailed{

      App a = new App("Firefox");
      a.focus();

      Region s = a.window(0);
      Region r = null;

      ScreenAnnotator sa = new ScreenAnnotator(s);

      sa.addDialog("Next","Welcome!");  
      sa.showNow();

      r = s.find("tiger.png");
      //sa.addHighlight(r);

      Location o = r.getTopLeft().above(100);

      sa.addText(o,"Click on the Tiger or the Unicorn");


//      sa.addDialog("Next","Click here");  

      sa.addClickTarget(r,"Tiger");

      sa.addClickTarget(s.find("unicorn.png"),"Unicorn");
      sa.showNow();


      sa.addText(o, "You just clicked on the " + sa.getLastClickedTarget().getName());

      sa.showNow();

      r = s.find("tiger.png");
      sa.addClickTarget(r,"Tiger");
      sa.showNow();
      
   }


   public static void testFirefox() throws FindFailed{
      App a = new App("Firefox");
      a.focus();

      Region s = a.window(0);

      Debug.log("s=" + s);
      s.getCenter();

      s.setFindFailedResponse(FindFailedResponse.PROMPT);


      //      Settings.ShowActions = true;

      Region r = null;

      //      s.click("tools.png",0);

      ScreenAnnotator sa = new ScreenAnnotator(s);

      r = s.find("tools.png");
      sa.addText(r.getBottomLeft().below(5),"Tools");
      sa.showNow();



      // sa.showWaitForButtonClick("Continue", "Tools");
   }


   public static void main(String[] args) throws AWTException, FindFailed {
      testFirefox();

      //testICDLSimpleSearch();
      
      
      //      
      //
      //      //		Screen screen = new Screen();
      //      //
      //      //		ScreenAnnotator sa = new ScreenAnnotator(screen);
      //      //		
      //      //		Screen s = new Screen();
      //      //		Region r = null;
      //      //		
      //
      //      //r = s.find(new Pattern("http://sikuli.org/images/puzzle.png"));
      //      // = s.find(new Pattern("http://udn.com/2010MAIN/photonews/6048009-2482714_small.jpg"));
      //
      //      //r = s.find(new Pattern("puzzle.png"));
      //      //		sh.addToolTip("Text recog", new Point(r.x,r.y+r.h+5));
      //      //		sh.addHighlight(r);
      //      //		sh.drawNow(3.0f);
      //
      //      //		sh.clear();
      //
      //
      //      //Screen s = new UnionScreen();
      //      //Screen s = new Screen(0);
      //
      //      App a = new App("Firefox");
      //      a.focus();
      //      
      //      Region s = a.window();
      //      s.getCenter();
      //      
      //      
      //      
      //      
      //      Settings.ShowActions = true;
      //     // s.setFindFailedResponse(FindFailedResponse.PROMPT);
      //      //s.waitVanish("play.png",5);
      //      //s.wait("tools.png",5);
      //      
      //      
      //     // s.click("play.png",0);
      //      
      //      //s.click("tools.png",0);
      //
      //      Region r = null;
      //
      //      //ScreenAnnotator sa = new ScreenAnnotator(s);
      //      ScreenAnnotator sa = new ScreenAnnotator();
      //
      //      s.setFindFailedResponse(FindFailedResponse.PROMPT);
      ////      r = s.find("tools.png");
      ////   
      ////      sa.addHighlight(r);
      ////      sa.showWaitForButtonClick("Tools", "Step 1");
      ////      
      //      r = s.find("play.png");
      //      Debug.log("r:" + r);
      //      sa.addHighlight(r);
      //      sa.addToolTip("Run", new Point(r.x,r.y+r.h+5));
      //
      //      //sa.show(3.0f);
      //      sa.showWaitForButtonClick("Continue", "Step 1");
      //
      //      //		r = s.find("Package Explorer");
      //      //		sa.addHighlight(r);
      //      //	
      //      r = s.find("addjava.png");
      //      sa.addHighlight(r);
      //      Point x = new Point(r.x,r.y);
      //      Point x1 = new Point(x);
      //      x1.translate(0, r.h+5);
      //
      //      Point c =  r.getCenter();
      //
      //      sa.addText("Click this to create a Java class", x1);
      //      sa.addArrow(x1,c);
      //
      ////      r = s.find("tools.png");
      ////      sa.addHighlight(r);
      //
      //      //sa.show(3.0f);
      //      sa.showWaitForButtonClick("Finish", "Step 2");
      //      
      //      //
      //      //
      //      //		
      //      //		r = s.find("new.png");
      //      //		sa.addHighlight(r);
      //      //		sa.addToolTip("Create a new project", new Point(r.x,r.y+r.h+5));
      //      //		//sa.addText("Click this to create <br>a new project", new Point(r.x,r.y+r.h+20));
      //      //		
      //      //		
      //      //		sa.show(3.0f);
      //



   }


   // send focus to the application right below the current mouse cusor
   public void focusBelow(){
      if(Env.getOS() == OS.MAC){
         // TODO: replace this hack with a more robust method
         
         // Mac's hack to bring focus to the window directly underneath
         // this hack works on the assumption that the caller has
         // the input focus but no interaction area at the current
         // mouse cursor position
         robot.mousePress(InputEvent.BUTTON1_MASK);            
         robot.mouseRelease(InputEvent.BUTTON1_MASK);
      }
      
      // TODO: Verify its correctness on Windows
      // on Windows, it seems we don't have to do any additional thing
      

   }


   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
         Env.getOSUtil().bringWindowToFront(this, true);
         //FIXME: windows?
      }
      /*
      else if(Env.getOS() == OS.WINDOWS){
         Win32Util.bringWindowToFront(this, true);
      }
      else
       */
      super.toFront();
   }



}
