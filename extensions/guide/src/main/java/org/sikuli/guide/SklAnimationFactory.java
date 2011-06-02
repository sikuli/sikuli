/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.sikuli.script.Debug;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class SklAnimationFactory{
   static public SklAnimation createResizeAnimation(DefaultSklObjectModel sklComponent, Dimension currentSize, Dimension targetSize){
      return new SklResizeAnimation(sklComponent, currentSize, targetSize);
   }
   
//   static NewAnimator createCenteredResizeToAnimation(SikuliGuideComponent sklComponent, Dimension targetSize){
//      return new CenteredResizeToAnimator(sklComponent, targetSize);
//   }
//   
//   static NewAnimator createCenteredMoveAnimation(SikuliGuideComponent sklComponent, Point source, Point destination){
//      NewMoveAnimator anim = new NewMoveAnimator(sklComponent, source, destination);
//      anim.centered = true;
//      return anim;
//   }

//   static NewAnimator createMoveAnimation(SikuliGuideComponent sklComponent, Point source, Point destination){      
//      return new NewMoveAnimator(sklComponent, source, destination);
//   }

   static public SklAnimation createMoveAnimation(DefaultSklObjectModel sklComponent, Point source, Point destination){      
      return new SklMoveAnimation(sklComponent, source, destination);
   }

   static public SklAnimation createMoveAnimation(SklModel sklComponent, Point source, Point destination){      
      return new SklMoveAnimation(sklComponent, source, destination);
   }

   static public SklAnimation createMoveToAnimation(SklModel sklComponent, Point destination){      
      return new SklMoveToAnimation(sklComponent, destination);
   }

   static public SklAnimation createResizeToAnimation(SklModel sklComponent, Dimension targetSize){      
      return new SklResizeToAnimation(sklComponent, targetSize);
   }
   
//   static public NewAnimator createCircleAnimation(
//         SikuliGuideComponent sklComponent, Point origin, float radius) {
//      return new CircleAnimator(sklComponent, origin, radius);
//   }   
//
   
   static public SklAnimation createFadeoutAnimation(DefaultSklObjectModel sklComponent){      
      return new SklOpacityAnimation(sklComponent, sklComponent.getOpacity(), 0f);
   }

   static public SklAnimation createFadeinAnimation(SklModel model){      
      return new SklOpacityAnimation(model, model.getOpacity(), 1f);
   }
   
   static public SklAnimation createOpacityAnimation(
         DefaultSklObjectModel sklComponent, float sourceOpacity, float targetOpacity) {
      return new SklOpacityAnimation(sklComponent, sourceOpacity, targetOpacity);
   }   

}

interface SklAnimationListener {   
   void animationCompleted();
}

class SklLinearStepper {
   float beginVal;
   float endVal;
   int step;
   int steps;

   public SklLinearStepper(float beginVal, float endVal, int steps){
      this.step = 0;
      this.steps = steps;
      this.beginVal = beginVal;
      this.endVal = endVal;
   }

   public float next(){
      float ret = beginVal + step * (endVal - beginVal) / steps;
      step += 1;
      return ret;
   }

   public boolean hasNext(){
      return step <= steps;
   }
}


@Root
class SklAnimation implements ActionListener {
   Timer timer;      

   boolean looping = false;
   boolean animationRunning = false;
   protected SklModel model;
   
   @Element
   DefaultSklObjectModel sklComponent;

   
   SklAnimation(){
      
   }
   
   SklAnimation(DefaultSklObjectModel sklComponent){
      this.sklComponent = sklComponent; 
   }      

   protected void init(){

   }

   public void start(){         
      init();
      timer = new Timer(25, this);
      timer.start();
      animationRunning = true;
   }

   protected boolean isRunning(){
      return animationRunning;
   }

   public void setLooping(boolean looping){
      this.looping = looping;
   }

   SklAnimationListener listener;
   public void setListener(SklAnimationListener listener) {
      this.listener = listener;
   }

   protected void animate(){

   }


   public void actionPerformed(ActionEvent e){
      if (isRunning()){         


         animate();
         
//         if (sklComponent != null)
//            sklComponent.updateView();



      }else{            
         timer.stop();
         if (looping){
            start();
         }else{
            animationRunning = false;
            if (listener != null)
               listener.animationCompleted();
         }
      }
   }
}

//
@Root
class SklMoveAnimation extends SklAnimation {

   SklLinearStepper xStepper;
   SklLinearStepper yStepper;
   
   @Attribute
   int srcx;
   @Attribute
   int srcy;
   @Attribute
   int destx;
   @Attribute
   int desty; 
   
   SklMoveAnimation(){
      
   }

   
   SklMoveAnimation(DefaultSklObjectModel sklComponent, Point source, Point destination){
      super(sklComponent);
      this.srcx = source.x;
      this.srcy = source.y;
      this.destx = destination.x;
      this.desty = destination.y;
   }
   
   SklMoveAnimation(SklModel model, Point source, Point destination){
      //super(model);
      this.model = model;
      this.srcx = source.x;
      this.srcy = source.y;
      this.destx = destination.x;
      this.desty = destination.y;
   }
   
   boolean centered = false;

   @Override
   protected void init(){
      xStepper = new SklLinearStepper(srcx, destx, 10);
      yStepper = new SklLinearStepper(srcy, desty, 10);                  
   }

   @Override
   protected boolean isRunning(){
      return xStepper.hasNext();
   }

   @Override
   protected void animate(){
      //Debug.info("animate");
      float x = xStepper.next();
      float y = yStepper.next();

//      if (centered){
//         x -= sklComponent.getActualWidth()/2;
//         y -= sklComponent.getActualHeight()/2;
//      }
      
      if (model != null){
         model.setX((int)x);
         model.setY((int)y);
      }
      
      if (sklComponent != null)
         sklComponent.setLocation(new Point((int)x,(int)y));
   }

}

@Root
class SklMoveToAnimation extends SklAnimation {

   SklLinearStepper xStepper;
   SklLinearStepper yStepper;
   
   @Attribute
   int destx;
   @Attribute
   int desty; 
   
   SklMoveToAnimation(){
      
   }

   
   SklMoveToAnimation(DefaultSklObjectModel sklComponent, Point destination){
      super(sklComponent);
      this.destx = destination.x;
      this.desty = destination.y;
   }
   
   SklMoveToAnimation(SklModel model, Point destination){
      //super(model);
      this.model = model;
      this.destx = destination.x;
      this.desty = destination.y;
   }
   
   boolean centered = false;

   @Override
   protected void init(){
      xStepper = new SklLinearStepper(model.getX(), destx, 10);
      yStepper = new SklLinearStepper(model.getY(), desty, 10);                  
   }

   @Override
   protected boolean isRunning(){
      return xStepper.hasNext();
   }

   @Override
   protected void animate(){
      //Debug.info("animate");
      float x = xStepper.next();
      float y = yStepper.next();

//      if (centered){
//         x -= sklComponent.getActualWidth()/2;
//         y -= sklComponent.getActualHeight()/2;
//      }
      
      if (model != null){
         model.setX((int)x);
         model.setY((int)y);
      }
      
      if (sklComponent != null)
         sklComponent.setLocation(new Point((int)x,(int)y));
   }

}
//
//class CircleAnimator extends NewAnimator {
//   LinearStepper radiusStepper;
//
//   Point origin;
//   float radius;
//   CircleAnimator(SikuliGuideComponent sklComponent, Point origin, float radius){
//      super(sklComponent);
//      this.radius = radius;
//      this.origin = origin;
//      setLooping(true);
//   }
//
//   @Override
//   protected void init(){
//      radiusStepper = new LinearStepper(0,(float) (2*Math.PI),20);
//   }
//
//   @Override
//   protected boolean isRunning(){
//      return radiusStepper.hasNext();
//   }
//
//   @Override
//   protected void animate(){
//      float theta = radiusStepper.next();
//
//      int x = (int) (origin.x + (int) radius * Math.sin(theta));
//      int y=  (int) (origin.y + (int) radius * Math.cos(theta));
//
//      sklComponent.setActualLocation((int) x, (int) y);
//   }  
//}
//
class SklResizeAnimation extends SklAnimation {
   SklLinearStepper widthStepper;
   SklLinearStepper heightStepper;

   Dimension currentSize;
   Dimension targetSize;
   
   @Attribute
   int srcWidth;
   @Attribute
   int srcHeight;
   @Attribute
   int destWidth;
   @Attribute
   int destHeight;
   
   SklResizeAnimation(DefaultSklObjectModel sklComponent, Dimension currentSize, Dimension targetSize){
      super(sklComponent);
      srcWidth = currentSize.width;
      srcHeight = currentSize.height;
      destWidth = targetSize.width;
      destHeight = targetSize.height;                       
   }

   @Override
   protected void init(){
      widthStepper = new SklLinearStepper(srcWidth, destWidth, 10);
      heightStepper = new SklLinearStepper(srcHeight, destHeight, 10);
   }

   @Override
   protected boolean isRunning(){
      return widthStepper.hasNext();
   }

   @Override
   protected void animate(){
      float width = widthStepper.next();
      float height = heightStepper.next();
      sklComponent.setSize(new Dimension((int) width, (int) height));
   }       
}

class SklResizeToAnimation extends SklAnimation {
   SklLinearStepper widthStepper;
   SklLinearStepper heightStepper;

   Dimension targetSize;
   
   @Attribute
   int destWidth;
   @Attribute
   int destHeight;
   
   SklResizeToAnimation(SklModel model, Dimension targetSize){
      //TODO: make super constructor takes model
      //super(sklComponent);
      this.model = model;
      destWidth = targetSize.width;
      destHeight = targetSize.height;                       
   }

   @Override
   protected void init(){
      widthStepper = new SklLinearStepper(model.getWidth(), destWidth, 10);
      heightStepper = new SklLinearStepper(model.getHeight(), destHeight, 10);
   }

   @Override
   protected boolean isRunning(){
      return widthStepper.hasNext();
   }

   @Override
   protected void animate(){
      model.setWidth((int) widthStepper.next());
      model.setHeight((int) heightStepper.next());
   }       
}


//
//class CenteredResizeToAnimator extends NewAnimator {
//   LinearStepper widthStepper;
//   LinearStepper heightStepper;
//
//   Dimension currentSize;
//   Dimension targetSize;
//   
//   Point centerLocation;
//   
//   CenteredResizeToAnimator(SikuliGuideComponent sklComponent, Dimension targetSize){
//      super(sklComponent);
//      this.targetSize = targetSize;                       
//   }
//
//   @Override
//   protected void init(){
//      centerLocation = sklComponent.getCenter();
//      widthStepper = new LinearStepper(sklComponent.getActualWidth(), targetSize.width, 10);
//      heightStepper = new LinearStepper(sklComponent.getActualHeight(), targetSize.height, 10);
//   }
//
//   @Override
//   protected boolean isRunning(){
//      return widthStepper.hasNext();
//   }
//
//   @Override
//   protected void animate(){
//      float width = widthStepper.next();
//      float height = heightStepper.next();
//      
//      Point newLocation = new Point(centerLocation);
//      newLocation.x -= width/2;
//      newLocation.y -= height/2;
//      sklComponent.setActualSize(new Dimension((int) width, (int) height));
//      sklComponent.setActualLocation(newLocation);
//   }       
//}

@Root
class SklOpacityAnimation extends SklAnimation {
   LinearStepper stepper;

   @Attribute
   float sourceOpacity;
   @Attribute
   float targetOpacity;
   
   SklOpacityAnimation(){
      
   }
   
   SklOpacityAnimation(SklModel model, float sourceOpacity, float targetOpacity){
      //super(sklComponent);
      this.model = model;
      this.sourceOpacity = sourceOpacity;
      this.targetOpacity = targetOpacity;
   }

   @Override
   protected void init(){
      stepper = new LinearStepper(sourceOpacity, targetOpacity, 10);         
   }

   @Override
   protected boolean isRunning(){
      return stepper.hasNext();
   }

   @Override
   public void animate(){
      float f = stepper.next();
      model.setOpacity(f);
   }
}
//
//class PopupAnimator implements ActionListener{
//   LinearStepper shadowSizeStepper;
//   LinearStepper offsetStepper;
//   LinearStepper scaleStepper;
//   LinearStepper widthStepper;
//   LinearStepper heightStepper;
//
//
//   Timer timer;      
//
//   Point centerLocation;
//   PopupAnimator(){
//      //         shadowSizeStepper = new LinearStepper(5,13,10);
//      //         offsetStepper = new LinearStepper(0,10,5);    
//      //         //scaleStepper = new LinearStepper(1f,1.2f,);
//      //         widthStepper = new LinearStepper(getActualWidth(),1.0f*getActualWidth()*1.1f,10);
//      //         heightStepper = new LinearStepper(getActualHeight(),1.0f*getActualHeight()*1.1f,10);
//      //         
//      //         centerLocation = new Point(getActualLocation());
//      //         centerLocation.x = centerLocation.x + getActualWidth()/2;
//      //         centerLocation.y = centerLocation.y + getActualHeight()/2;         
//   }      
//
//   public void start(){         
//      //         Timer timer = new Timer(25, this);
//      //         timer.start();
//      //         animationRunning = true;
//   }
//
//   @Override
//   public void actionPerformed(ActionEvent e){
//      //         if (shadowSizeStepper.hasNext()){
//      //
//      //            float shadowSize = shadowSizeStepper.next();
//      //            float offset = offsetStepper.next();
//      //            float width = widthStepper.next();
//      //            float height = heightStepper.next();                        
//      //
//      //            Rectangle r = getBounds();
//      //            
//      //            setActualLocation((int)(centerLocation.x - width/2), (int)(centerLocation.y - height/2));
//      //            setActualSize((int)width, (int)height);
//      //
//      //            setShadow((int)shadowSize,(int) 2);
//      //            //Point p = getActualLocation();
//      //            //p.x -= 1;
//      //            //p.y -= 1;
//      //            //setActualLocation(p);
//      //            r.add(getBounds());
//      //            
//      //            getParent().getParent().repaint();//r.x,r.y,r.width,r.height);
//      //         }else{
//      //            ((Timer)e.getSource()).stop();
//      //            animationRunning = false;
//      //            animationCompleted();
//      //         }
//   }
//}





