package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.sikuli.guide.SikuliGuideAnchor.AnchorListener;
import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.guide.Transition.TransitionListener;
import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

public class SikuliGuide extends TransparentWindow {


   static float DEFAULT_TIMEOUT = 10.0f;


   static public final int FIRST = 0;
   static public final int MIDDLE = 1;
   static public final int LAST = 2;
   static public final int SIMPLE = 4;


   Robot robot;

   public void setDefaultTimeout(float timeout_in_seconds){
      DEFAULT_TIMEOUT = timeout_in_seconds;
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
   ArrayList<Transition> transitions = new ArrayList<Transition>();

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


      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      ((JPanel)getContentPane()).setBackground(null);      
      content.setBackground(null);

      Env.getOSUtil().setWindowOpaque(this, false);

      getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
      ((JPanel)getContentPane()).setDoubleBuffered(true);

      setVisible(false);
      setAlwaysOnTop(true);
      setFocusableWindowState(false);


      clickableWindow = new ClickableWindow(this);
      
      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            //Debug.info("[SikuliGuide] window closed");
            GlobalMouseMotionTracker.getInstance().stop();
         }
      });


   }



   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);
   }

   public void clear(){

      if (clickableWindow != null)
         clickableWindow.clear();

      stopAnimation();
      stopTracking();

      setDarken(false);

      content.removeAll();
      transition = null;
      beam = null;
      
      setVisible(false);
      
      GlobalMouseMotionTracker.getInstance().stop();

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


   final float DIMMING_OPACITY = 0.5f;
   public void updateSpotlights(ArrayList<Region> regions){
      removeSpotlights();

      if (regions.isEmpty()){

         setBackground(null);
         content.setBackground(null);

      }else{

         // if there are spotlights added, darken the background
         setBackground(new Color(0f,0f,0f,DIMMING_OPACITY));
         content.setBackground(new Color(0f,0f,0f,DIMMING_OPACITY));
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

   
   public void setDarken(boolean darken){
      
      if (darken){
         //setBackground(new Color(0f,0f,0f,DIMMING_OPACITY));
         content.setBackground(new Color(0f,0f,0f,DIMMING_OPACITY));
      }else{
         setBackground(null);
         content.setBackground(null);
      }
   }

   public void addComponent(JComponent comp){
      content.add(comp,0);
   }
   
   public void addComponent(SikuliGuideComponent comp, int index){
      if (comp instanceof Clickable){

         // add to the guide window
         content.add(comp,0);

         // add it to the glasspane window to capture mouse events
         clickableWindow.addClickable((Clickable) comp);
         
         addTransition(clickableWindow);
         return;
      }
      
      content.add(comp,index);
      if (comp instanceof SikuliGuideSpotlight){
         // if there's any spotlight added, darken the background
         setDarken(true);
      }
      //repaint();
   }
   
   public void addToFront(SikuliGuideComponent comp){
      addComponent(comp,0);
   }

   public void addToFront(JComponent comp){
      addComponent(comp);
   }

   public void removeComponent(Component comp){
      content.remove(comp);
   }

   Beam beam = null;
   public SikuliGuideComponent addBeam(Region r){
      beam = new Beam(this, r);
      SikuliGuideAnchor anchor = new SikuliGuideAnchor(r);
      beam.setAlwaysOnTop(true);
      addTransition(beam);
      return anchor;
   }

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

   
//      return showNow(defaultTimeout);
//   }
//   
   boolean hasSpotlight(){
      for (Component comp : content.getComponents()){
         if (comp instanceof SikuliGuideSpotlight)
            return true;
      }
      return false;
   }
   
//   public void setTimeout(float secs) {
//      transitions.add(new TimeoutTransition(this, (int)secs*1000));
//   }

   public String showNow(float secs){
      transitions.add(new TimeoutTransition((int)secs*1000));
      return showNow();
   }
   
   Transition triggeredTransition;
   public String showNow(){
      
      if (content.getComponentCount()  == 0 
            && transitions.isEmpty()
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
         reset();
         focusBelow();
         return key;
      }

      
      if (transitions.isEmpty()){
         // if no transition is added, use the default timeout transition
         transitions.add(new TimeoutTransition((int)DEFAULT_TIMEOUT*1000));
      }
      
      final Object token = new Object();
      synchronized(token){
      
         for (Transition transition : transitions){
            transition.waitForTransition(new TransitionListener(){

               @Override
               public void transitionOccurred(Object source) {
                  triggeredTransition = (Transition) source;
                  synchronized(token){
                     token.notify();
                  }
               }
               
            });
         }         
         
         try {
            //Debug.info("[SikuliGuide] Waiting for transition");
            token.wait();
            //Debug.info("[SikuliGuide] Transition has occurred");

         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      
      String cmd=null;
      
      if (triggeredTransition instanceof ClickableWindow){
         // relay the click at the same position
         
         ClickableWindow cw = (ClickableWindow) triggeredTransition;
         if (!(cw.getLastClickedClickable() instanceof SikuliGuideButton)){
          //Debug.info("clicking");
            
            Clickable clickable = cw.getLastClickedClickable();
            
            Debug.info("Focusing below");
            focusBelow();
            
            if (clickable.clickPoint == null){
               robot.mousePress(InputEvent.BUTTON1_MASK);            
               robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }else{
               
               Location o = Env.getMouseLocation();
               Point p = clickable.clickPoint;
               robot.mouseMove(p.x,p.y);
               robot.mousePress(InputEvent.BUTTON1_MASK);            
               robot.mouseRelease(InputEvent.BUTTON1_MASK);          
               robot.mouseMove(o.x,o.y);
            }
         }
         
         cmd = cw.getLastClickedClickable().getName();
         
      }else if (triggeredTransition instanceof TimeoutTransition){
         
         cmd = "timeout";
      }
      
      reset();
      
      return cmd;

   }
   
   private void reset(){
      
      if (clickableWindow != null)
         clickableWindow.clear();

      stopAnimation();
      stopTracking();

      content.removeAll();
      transition = null;
      beam = null;
      
      transitions.clear();
      
      setDarken(false);
      setVisible(false);
      
      GlobalMouseMotionTracker.getInstance().stop();
      
      // now we dipose window so the .py script can terminate
      if (clickableWindow != null)
         clickableWindow.dispose();
      dispose();
   }

//   private void closeAfter(float secs){
//      try{
//         Thread.sleep((int)secs*1000);
//      }
//      catch(InterruptedException e){
//         closeNow();
//         e.printStackTrace();
//      }
//      closeNow();
//   }

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

   public void addMagnifier(Region region) {
      Magnifier mag = new Magnifier(this,region);
      content.add(mag);
   }

   public void addTransition(Transition t) {
      transitions.add(t);
   }
   
   public Transition getTransition(){
      return transition;
   }

   public void removeComponents() {
      content.removeAll();
   }

   public void startTracking(){
      for (Component co : content.getComponents()){
         if (co instanceof SikuliGuideAnchor){
            ((SikuliGuideAnchor) co).startTracking();
         }
      }
   }

   public void stopTracking(){
      for (Component co : content.getComponents()){
         if (co instanceof SikuliGuideAnchor){
            ((SikuliGuideAnchor) co).stopTracking();
         }
      }
   }

   public void addTracker(Pattern pattern, SikuliGuideAnchor anchor){
      Tracker tracker = null;

//      // find a tracker already assigned to this pattern
//      for (Tracker t : trackers){
//         if (t.isAlreadyTracking(pattern,r)){
//            tracker = t;
//            break;
//         }
//      }      

//      if (tracker == null){
         tracker = new Tracker(this, pattern, null);
         trackers.add(tracker);
//      }
         BufferedImage img;
         try {
            img = pattern.getImage();
            anchor.setActualSize(img.getWidth(), img.getHeight());
            tracker.setAnchor(anchor);

         } catch (IOException e) {
            e.printStackTrace();
         }

   }
   
   public void addTracker(Pattern pattern, Region r, SikuliGuideComponent c){     
      Tracker tracker = null;

      // find a tracker already assigned to this pattern
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

      tracker.setAnchor(c);
   }

   public void addTracker(Pattern pattern, Region r, ArrayList<SikuliGuideComponent> components) {
      Tracker tracker = new Tracker(this, pattern, r);
      for (SikuliGuideComponent c : components){
         tracker.setAnchor(c);
      }      
      trackers.add(tracker);   
   }


   public void playSteps(ArrayList<Step> steps) throws FindFailed{

      Screen s = new Screen();
      
      for (Step step : steps){
         SikuliGuideButton btn = new SikuliGuideButton("Next");
         btn.setLocation(s.getTopRight().left(200).below(50));


         addToFront(btn);
         
         step.setTransition(getTransition());

         playStep(step, 1.0f);
      }
      
   }

   abstract class TrackerAdapter {      
      abstract void patternAnchored();
   }
   
   public void playStepOnWebpage(Step step, Region leftmarker, Region rightmarker){
      
      //Point screenshotOrigin = new Point(614,166);
      
      int originalWidth = step.getScreenImage().getWidth();
      int originalHeight = step.getScreenImage().getHeight();
      
      int displayWidth = rightmarker.x + rightmarker.w - leftmarker.x;
      float scale = 1.0f*displayWidth/originalWidth;
      Debug.info("scale:" + scale);
      int displayHeight = (int) (originalHeight * scale);

      int originX = leftmarker.x;
      int originY = leftmarker.y - displayHeight;
      Point screenshotOrigin = new Point(originX, originY);
      
      //scale = 1.0f;
      //Point screenshotOrigin = new Point(715,192);
      //Point screenshotOrigin = new Point(953,257);//
      
      Debug.info("Step size:" + step.getView().getSize());
      //Dimension displaySize = new Dimension(480,363);
      //float scale = 480f/640f;
      
      Screen s = new Screen();
      for (Part part : step.getParts()){

         Point targetOrigin = part.getTargetOrigin();
         
         Point screenOrigin = new Point();
         screenOrigin.x = screenshotOrigin.x + (int)(targetOrigin.x*scale);
         screenOrigin.y = screenshotOrigin.y + (int)(targetOrigin.y*scale);

         part.setAnchorScreenLocation(screenOrigin);
      }
      
      playStep(step, scale);
   }
      
   public void playStep(Step step){
      playStep(step, 1.0f);
   }
   

   public void playStepList(SklStepListModel stepListModel){
      for (Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements() ;) {
          SklStepModel eachStepModel = (SklStepModel) e.nextElement();
          String ret = playStep(eachStepModel);
          if (ret == null)
             continue;
          
          if (ret.equals("Exit")){
             return;
          }
       }
      
   }
   
   public String playStep(SklStepModel step_){
      
      SklStepModel step = null;
      try {
         step = (SklStepModel) step_.clone();
      } catch (CloneNotSupportedException e1) {
         e1.printStackTrace();
      }
      
      for (SklObjectModel model : step.getModels()){
         model.setOpacity(0f);         
         SklObjectView view = SklViewFactory.createView(model);
         addToFront(view);
      }
      
      
      SikuliGuideButton btn = new SikuliGuideButton("Exit");
      btn.setActualLocation(10,50);
      addToFront(btn);

      SikuliGuideButton skip = new SikuliGuideButton("Skip");
      skip.setActualLocation(90,50);
      addToFront(skip);
      
      // do these to allow static elements to be drawn
      setVisible(true);
      toFront();

      step.startTracking(this);
      step.startAnimation();
      
      String ret = showNow();
      Debug.info("[SikuliGuide.playStep] ret = " + ret);
      
      return ret;      
   }

   
   public void playStep(Step step, final float scale){

      
      SikuliGuideImage screenshot = new SikuliGuideImage(step.getScreenImage());     
      screenshot.setLocationRelativeToRegion(new Screen(), Layout.INSIDE);
//      screenshot.setOpacity(0);
//      screenshot.addAnimation(AnimationFactory.createOpacityAnimation(screenshot,0.1f,0.9f));
//      // TODO replace these with a Pause animation
//      screenshot.addAnimation(AnimationFactory.createOpacityAnimation(screenshot,0.9f,0.9f));
//      screenshot.addAnimation(AnimationFactory.createOpacityAnimation(screenshot,0.9f,0.9f));
//      screenshot.addAnimation(AnimationFactory.createOpacityAnimation(screenshot,1,0));      
//      addToFront(screenshot);
      
      for (final Part part : step.getParts()){

         Pattern pattern = part.getTargetPattern();

         BufferedImage patternImage = null;
         try {
            patternImage = pattern.getImage();
         } catch (IOException e) {
         }

         final SikuliGuideAnchor anchor = new SikuliGuideAnchor(pattern);
         anchor.setEditable(true);
         anchor.setOpacity(1f);
         anchor.setAnimateAnchoring(true);
         
         Point anchorLocation = new Point(part.getTargetOrigin());
         Point screenshotLocation = screenshot.getActualLocation();
         anchorLocation.translate(screenshotLocation.x,screenshotLocation.y);
         anchor.setActualLocation(anchorLocation);
                  
         Clickable clickable = new Clickable(null);
         clickable.setLocationRelativeToComponent(anchor, Layout.OVER);
         addToFront(clickable);

         // add an image to visualize the target pattern
         final SikuliGuideImage sklImage = new SikuliGuideImage(patternImage);
         sklImage.setLocationRelativeToComponent(anchor, Layout.OVER);
                  
         anchor.addListener(new AnchorListener(){

            @Override
            public void anchored() {
               sklImage.removeFromLeader();
               sklImage.setVisible(false);
               repaint();
               
               
               for (SikuliGuideComponent comp : anchor.getFollowers()){
                  if (comp instanceof SikuliGuideText || comp instanceof SikuliGuideFlag ){
                     comp.popin(); 
                  }
               }

               anchor.popin();
            }

            @Override
            public void found(SikuliGuideAnchor source) {
            }
            
         });

         addToFront(sklImage);
         addToFront(anchor);

         Point o = part.getTargetOrigin();
         Point p = anchor.getActualLocation();
         
         for (SikuliGuideComponent compo : part.getAnnotationComponents()){
            
            SikuliGuideComponent comp = (SikuliGuideComponent) compo.clone();

            Point loc = comp.getActualLocation();
            loc.x = (int) ((int) (loc.x-o.x)*scale) + p.x;
            loc.y = (int) ((int) (loc.y-o.y)*scale) + p.y;

            comp.setActualLocation(loc);            
            comp.setLocationRelativeToComponent(anchor);
            comp.setActualSize((int)(comp.getActualWidth()*scale),(int)(comp.getActualHeight()*scale));

            addToFront(comp);

            comp.popout();

         }

         anchor.popout();
         
      }
      
      SikuliGuideButton btn = new SikuliGuideButton("Exit");
      btn.setActualLocation(50,50);
      addToFront(btn);
      
      showNow();

   }
   

}



