package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

interface StepPlayerListener {      
   void stepCompleted(SklStepModel step);
   void stepFailed(SklStepModel step);
}


public class SklStepPlayView extends TransparentWindow {
   
   
   SklStepModel _stepModel;
   final Rectangle _regionModel = new Rectangle();
   
   ScreenRegionSelector _screenRegionSelector;
   
   SklStepPlayView(){      
      setLayout(null);      
      // It turns out these are useful after all
      // so that it works on Windows
      setBackground(null);
      ((JPanel)getContentPane()).setBackground(null);      
      Env.getOSUtil().setWindowOpaque(this, false);
      
      setBounds(100,100,500,500);

      _screenRegionSelector = new ScreenRegionSelector(getBounds());
      _screenRegionSelector.addListener(new ScreenRegionSelectorListener(){

         @Override
         public void selectionChanged(Rectangle region) {
            setBounds(region);       
            _regionModel.setBounds(region);
         }
         
      });
      
      _regionModel.setBounds(getBounds());

   }
   
   void stop(){
      for (SklTracker tracker : _trackers){
         tracker.stopTracking();
      }
      stopFailureTimer();

   //  remove existing objs (of the previous step) from the content pane
      getContentPane().removeAll();
      repaint();
   }
   
   @Override
   public void setVisible(boolean visible){
      
      _screenRegionSelector.setVisible(visible);
      _screenRegionSelector.setAlwaysOnTop(visible);

      super.setVisible(visible);
      //_screenRegionSelector.toBack();
      
   }
   
   ArrayList<SklTracker> _trackers = new ArrayList<SklTracker>(); 
   void play(SklStepModel stepModel){
      
      _screenRegionSelector.setVisible(true);
      _screenRegionSelector.setAlwaysOnTop(true);

      setVisible(true);
      toFront();
      setAlwaysOnTop(true);
      
      // make a copy of the step model
      SklStepModel stepModelCopy = null;
      try {
         stepModelCopy = (SklStepModel) stepModel.clone();
      } catch (CloneNotSupportedException e1) {
         return;
      }
      
      // remove existing objs (of the previous step) from the content pane
      getContentPane().removeAll();
      repaint();
      
      // add to content pane
      for (SklModel model : stepModelCopy.getModels()){
         model.setOpacity(0f);         
         SklView view = SklViewFactory.createView(model);
         add(view);
      }
      
      stepModelCopy.compile();
        

      _trackers.clear();
      numPatternsToVerify = 0;
      numPatternsVerified = 0;
      for (SklModel model : stepModelCopy.getModels()){
         if (model instanceof SklAnchorModel){
            
            SklAnchorModel anchor = (SklAnchorModel) model;

            // set up the pattern image
            BufferedImage image = stepModelCopy.getReferenceImageModel().getImage();
            BufferedImage patternImage = image.getSubimage(anchor.getX(), anchor.getY(), anchor.getWidth(), anchor.getHeight());            
            Pattern pattern = new Pattern(patternImage);

            SklTracker tracker = new SklTracker(pattern);
            _trackers.add(tracker);
            tracker.setRegionModel(_regionModel);
            tracker.start();
            
            if (anchor.getCommand() == SklAnchorModel.ASSERT_COMMAND){
               
               Verifier c = new Verifier(anchor);
               anchor.setOpacity(0.0f);
               tracker._listener = c;
               numPatternsToVerify += 1;
               
            } else if (anchor.getCommand() == SklAnchorModel.CLICK_COMMAND){
               Debug.info("click");
               Clicker c = new Clicker(anchor);
               tracker._listener = c;
               
            } else if (anchor.getCommand() == SklAnchorModel.TYPE_COMMAND){
               Debug.info("type");
               Typer c = new Typer(anchor, (String) anchor.getArgument());
               tracker._listener = c;
            }

         }
      }
      
      // if there's some active anchor (being tracked)
      if (_trackers.size() > 0){
         // starts timer
         startFailureTimer();
      }else{         
         stop();
         fireStepCompleted();
      }
   }


   
   Timer failureTimer;
   void startFailureTimer(){
      failureTimer = new Timer(10000, new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent arg0) {
            stop();
            fireStepFailed();
         }         
      });
      failureTimer.setRepeats(false);
      failureTimer.start();

   }
   
   void stopFailureTimer(){
      if (failureTimer != null)
         failureTimer.stop();
   }

   
   ArrayList<StepPlayerListener> listeners = new ArrayList<StepPlayerListener>();
   void addListener(StepPlayerListener listener){
      listeners.add(listener);
   }
   
   void allPatternsVerified(){
      stopFailureTimer();
      fireStepCompletedLater(2);
   }
   
   
   void fireStepCompletedLater(float secs){
      Timer t = new Timer((int) (secs*1000), new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent e) {
            fireStepCompleted();            
         }
         
      });
      t.setRepeats(false);
      t.start();
   }
   
   void fireStepCompleted(){
      for (StepPlayerListener listener : listeners){
         listener.stepCompleted(_stepModel);
      }
   }
   
   void fireStepFailed(){
      for (StepPlayerListener listener : listeners){
         listener.stepFailed(_stepModel);
      }
   }

   
   @Override
   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D) g;
      Rectangle r = getBounds();
      g2d.setColor(Color.green);
      g2d.setStroke(new BasicStroke(3f));
      g2d.drawRect(2,2,r.width-4,r.height-4);
   }
   
   int numPatternsToVerify = 0;
   int numPatternsVerified = 0;
   void patternVerified(){
      numPatternsVerified += 1;
      Debug.info("[StepPlayer] " + numPatternsVerified + " of " + numPatternsToVerify + " patterns verified");
      
      if (numPatternsVerified == numPatternsToVerify)
         allPatternsVerified();
   }
   
   class Verifier implements SklTrackerListener {
      
      private SklAnchorModel _anchor;
      
      Verifier(SklAnchorModel anchor){
         _anchor = anchor;
      }


      @Override
      public void patternFoundFirstTime(Object source, Region match) {
         
         // translate to window location
         Point loc = new Point();
         Point o = getLocation();
         loc.x = match.x - o.x;
         loc.y = match.y - o.y;
         
         _anchor.setLocation(loc.x, loc.y);
         _anchor.setSize(match.w, match.h);
         
         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
         anim.setListener(new SklAnimationListener(){

            @Override
            public void animationCompleted() {
               patternVerified();
            }
            
         });
         anim.start();
         
         ((SklTracker) source).stopTracking();         
      }

      @Override
      public void patternFoundAgain(Object source, Region match) {
         
      }

      @Override
      public void patternNotFound(Object source) {
         
      }
      
      
      
      
   }
   
   class Clicker implements SklTrackerListener {
      
      private SklAnchorModel _anchor;
     
      Clicker(SklAnchorModel anchor){
         _anchor = anchor;
      }

      @Override
      public void patternFoundAgain(Object source, Region match) {
      }

      @Override
      public void patternFoundFirstTime(Object source, Region match) {
         
         // translate to window location
         Point loc = new Point();
         Point o = getLocation();
         loc.x = match.x - o.x;
         loc.y = match.y - o.y;
         
         _anchor.setLocation(loc.x, loc.y);
         _anchor.setSize(match.w, match.h);
         
         
         stopFailureTimer();
         
         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
         anim.setListener(new SklAnimationListener(){

            @Override
            public void animationCompleted() {
               fireStepCompletedLater(1);
            }
            
         });
         anim.start();
         

         try {
            (new Screen()).click(match, 0);
         } catch (FindFailed e) {
         }

         ((SklTracker) source).stopTracking();
      }

      @Override
      public void patternNotFound(Object source) {
         SklAnimationFactory.createFadeoutAnimation(_anchor).start();
      }
   }
   
   class Typer implements SklTrackerListener {
      
      private SklAnchorModel _anchor;
      private String _stringToType;
     
      Typer(SklAnchorModel anchor, String toType){
         _anchor = anchor;
         _stringToType = toType;
      }
      

      @Override
      public void patternFoundAgain(Object source, Region match) {
      }

      @Override
      public void patternFoundFirstTime(Object source, Region match) {
         
         // translate to window location
         Point loc = new Point();
         Point o = getLocation();
         loc.x = match.x - o.x;
         loc.y = match.y - o.y;
         
         _anchor.setLocation(loc.x, loc.y);
         _anchor.setSize(match.w, match.h);
         
         
         stopFailureTimer();
         
         SklAnimation anim = SklAnimationFactory.createFadeinAnimation(_anchor);
         anim.setListener(new SklAnimationListener(){

            @Override
            public void animationCompleted() {
               fireStepCompletedLater(1);
            }
            
         });
         anim.start();
         
         try {
            //(new Screen()).paste(match, _stringToType);
            (new Screen()).type(match, _stringToType, 0);
         } catch (FindFailed e) {
         }

         ((SklTracker) source).stopTracking();
      }

      @Override
      public void patternNotFound(Object source) {
         SklAnimationFactory.createFadeoutAnimation(_anchor).start();
      }
   }
   
   void setModel(SklStepModel stepModel){
      _stepModel = stepModel;
//      try {
//         _stepModel = (SklStepModel) stepModel.clone();
//      } catch (CloneNotSupportedException e) {
//         e.printStackTrace();
//      }      
   }

}
