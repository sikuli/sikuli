/**
 * 
 */
package org.sikuli.guide;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class SikuliGuideComponent extends JComponent 
implements Cloneable{

   public Object clone() {
      SikuliGuideComponent clone;
      try {
         clone =  (SikuliGuideComponent) super.clone();

         // do not clone references to other components
         clone.followers = new ArrayList<SikuliGuideComponent>();
         clone.leader = null;
         clone.autolayout = null;
         clone.actualBounds = new Rectangle();
         //clone.connectors = new ArrayList<Connector>();
         return clone;
      }
      catch (CloneNotSupportedException e) {
         throw new InternalError(e.toString());
      }
   }

   //Region reference_region = null;
   //int reference_side = -1;

   //   ArrayList<SikuliGuideAnimator> anims = new ArrayList<SikuliGuideAnimator>();

   public void startAnimation(){          
      // TODO: this is ugly
      // use animation queue..

      if (entrance_anim != null){
         //if (entrance_anim.isRunning()){
         entrance_anim.stop();
         //}

         if (!entrance_anim.isPlayed()){
            //Debug.log("entrance anim started");
            //Debug.log("entrance anim:" + entrance_anim);
            entrance_anim.start();
         }
      } 

      if (emphasis_anim != null){     

         if (entrance_anim != null)
            entrance_anim.stop();

         //Debug.log("emphasis anim started");
         emphasis_anim.start();
      }
   }

   public void stopAnimation() {
      if (emphasis_anim != null){     
         emphasis_anim.stop();
      }
      if (entrance_anim != null){
         entrance_anim.stop();
      }
   }

   public SikuliGuideAnimator createSlidingAnimator(int offset_x, int offset_y){  
      Point dest = getLocation();
      Point src = new Point(dest.x + offset_x, dest.y + offset_y);
      return new MoveAnimator(this, src, dest);
   }

   public SikuliGuideAnimator createMoveAnimator(int dest_x, int dest_y){
      Point src = getLocation();
      Point dest = new Point(dest_x, dest_y);
      return new MoveAnimator(this, src, dest);
   }

   public SikuliGuideAnimator createCirclingAnimator(int radius) {      
      return new CircleAnimator(this, radius);
   }


   SikuliGuideAnimator entrance_anim;
   SikuliGuideAnimator emphasis_anim;

   public void setEntranceAnimation(SikuliGuideAnimator anim){
      if (entrance_anim != null)
         entrance_anim.stop();
      else
         entrance_anim = anim;
   } 

   public void setEmphasisAnimation(SikuliGuideAnimator anim){
      if (emphasis_anim != null)
         emphasis_anim.stop();

      if (entrance_anim != null)
         entrance_anim.stop();

      emphasis_anim = anim;
   } 

   public final static int TOP = 0;
   public final static int LEFT = 1;
   public final static int RIGHT = 2;
   public final static int BOTTOM = 3;
   public final static int INSIDE = 4;
   public final static int CENTER = 5;
   public final static int OVER = 6;
   public final static int ORIGIN = 7;

   private boolean autoLayoutEnabled = false;
   private boolean autoResizeEnabled = false;
   private boolean autoMoveEnabled = false;
   private boolean autoVisibilityEnabled = false;
   
   public SikuliGuideComponent(){    
      super();
      setMovable(false);      
   }

   // this allows the component to be dragged to another location on the screen
   ComponentMover cm = new ComponentMover();
   public void setMovable(boolean movable){
      if (movable){
         cm.registerComponent(this);         
      }else{
         cm.deregisterComponent(this);
      }
   }

   class AutoLayout implements ComponentListener {
      private SikuliGuideComponent targetComponent;
      AutoLayout(SikuliGuideComponent targetComponent){
         this.setTargetComponent(targetComponent);
         targetComponent.addComponentListener(this);
      }
      public void setTargetComponent(SikuliGuideComponent targetComponent) {
         this.targetComponent = targetComponent;
      }
      public SikuliGuideComponent getTargetComponent() {
         return targetComponent;
      }
            
      void update() {
         
         //Debug.info("Update caused by leader:" + this);
         
         // TODO calculate necesary region to udpate
//         if (getParent()!=null){
//            
//            if (getParent().getParent()!=null){
//               getParent().getParent().repaint();
//            }else{
//               getParent().repaint();
//            }
//         }
         
      }
      
      void stop(){
         targetComponent.removeComponentListener(this);
      }
      
      @Override
      public void componentHidden(ComponentEvent e) {
         
//         if (isAutoVisibilityEnabled()){
//            setVisible(false);
//            update();
//         }
      }

      @Override
      public void componentMoved(ComponentEvent e) {
//         if (isAutoMoveEnabled())
//            update();         
      }

      @Override
      public void componentResized(ComponentEvent e) {
//         if (isAutoResizeEnabled())
//            update();
      }

      @Override
      public void componentShown(ComponentEvent e) {
//         if (isAutoVisibilityEnabled()){
//            setVisible(true);
//            update();
//         }
      }
      
   }
   
   class AutoLayoutBySide extends AutoLayout{
      int side;
      
      AutoLayoutBySide(SikuliGuideComponent targetComponent, int side){
         super(targetComponent);
         this.side = side;
      }
      
      @Override
      void update(){
         Region region = new Region(getTargetComponent().getBounds());
         setLocationRelativeToRegion(region, side);     
         super.update();
      }
   }

   class AutoLayoutByMovement extends AutoLayout {      
      // previous known location of the target this component follows
      int x;
      int y;
      
      AutoLayoutByMovement(SikuliGuideComponent targetComponent){
         super(targetComponent);
         this.x = targetComponent.getX();
         this.y = targetComponent.getY();
      }
      
      
      @Override
      public void update() {
         
         Debug.info("auto moved by leader");
         
         int newx = getTargetComponent().getX();
         int newy = getTargetComponent().getY();
         
         int dx = newx - x;
         int dy = newy - y;
         
//         float targetZoomLevel = getTargetComponent().zoomLevel;
        // if (zoomLevel != targetZoomLevel){
//            dx = (int) (dx * targetZoomLevel / zoomLevel);
//            dy = (int) (dy * targetZoomLevel / zoomLevel);
//             dx = (int) (dx / (targetZoomLevel / zoomLevel));
//             dy = (int) (dy / (targetZoomLevel / zoomLevel));
//            dx = 0;
//            dy = 0;
         //}
         
         x = newx;
         y = newy;
         
         setLocation(getX()+dx, getY()+dy);
      }
      
//      @Override
//      public void componentMoved(ComponentEvent e) {
//         int newx = e.getComponent().getX();
//         int newy = e.getComponent().getY();
//         
//         int dx = newx - x;
//         int dy = newy - y;
//         
//         float targetZoomLevel = getTargetComponent().zoomLevel;
//        // if (zoomLevel != targetZoomLevel){
////            dx = (int) (dx * targetZoomLevel / zoomLevel);
////            dy = (int) (dy * targetZoomLevel / zoomLevel);
////             dx = (int) (dx / (targetZoomLevel / zoomLevel));
////             dy = (int) (dy / (targetZoomLevel / zoomLevel));
////            dx = 0;
////            dy = 0;
//         //}
//         
//         x = newx;
//         y = newy;
//         
//         setLocation(getX()+dx, getY()+dy);
//      }

   }
   
   class AutoLayoutByOffset extends AutoLayout {
      int offsetx;
      int offsety;
      
      AutoLayoutByOffset(SikuliGuideComponent targetComponent, int offsetx, int offsety){
         super(targetComponent);
         this.offsetx = offsetx;
         this.offsety = offsety;
      }

      @Override
      void update(){
         setOffset((int)(offsetx*zoomLevel), (int) (offsety*zoomLevel));         
         Region region = new Region(getTargetComponent().getBounds());
         setLocationRelativeToRegion(region, SikuliGuideComponent.ORIGIN);
         super.update();
      }
   }
   
   class AutoLayoutByRatio extends AutoLayout{
      float x, y;
      
      AutoLayoutByRatio(SikuliGuideComponent targetComponent, float x, float y){
         super(targetComponent);
         this.x = x;
         this.y = y;
      }
      
      @Override
      void update(){
         Region region = new Region(getTargetComponent().getBounds());
         setHorizontalAlignmentWithRegion(region, x);
         setVerticalAlignmentWithRegion(region, y);         
         super.update();
      }
   }
   
   AutoLayout autolayout = null;   
   
   public void setLocationRelativeToComponent(SikuliGuideComponent comp, int side) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      comp.addFollower(this);
      
      autolayout = new AutoLayoutBySide(comp, side);      
      autolayout.update();           
   }
   
   public void setLocationRelativeToComponent(SikuliGuideComponent comp, int offsetx, int offsety) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      comp.addFollower(this);
      
      autolayout = new AutoLayoutByOffset(comp, offsetx, offsety);     
      autolayout.update();      
   }

   public void setLocationRelativeToComponent(SikuliGuideComponent comp, float relativeX, float relativeY) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      autolayout = new AutoLayoutByRatio(comp, relativeX, relativeY);      
      autolayout.update();        
   }
   
   public void followComponent(SikuliGuideComponent leader){      
      if (autolayout != null){
         autolayout.stop();
      } 
      
      leader.addFollower(this);
      
      autolayout = new AutoLayoutByMovement(leader);      
      autolayout.update(); 
   }


   public void setLocationRelativeToPoint(Point point, int side){      
      Rectangle bounds = getBounds();
      // TODO implement other positioning parameters
      if (side == CENTER){
         setLocation(point.x - bounds.width/2, point.y - bounds.height/2);
      }
   }  

   Rectangle actualBounds = new Rectangle();
   
   float zoomLevel = 1.0f;
   public void setZoomLevel(float zoomLevel){
      
      this.zoomLevel = zoomLevel; 

      for (SikuliGuideComponent sklComp : getFollowers()){         
         if (sklComp.autolayout != null){
            sklComp.setZoomLevel(zoomLevel);
         }
      }

      Debug.info("Component:" + this);
      Debug.info("Actual bounds:" + actualBounds);
      Rectangle bounds = new Rectangle(actualBounds);

      bounds.x *= zoomLevel;
      bounds.y *= zoomLevel;
      bounds.width *= zoomLevel;
      bounds.height *= zoomLevel;      

      //super.setBounds(bounds);
      super.setBounds(bounds);
      
      for (SikuliGuideComponent sklComp : getFollowers()){         
         if (sklComp.autolayout != null){
            
            if (sklComp.autolayout instanceof AutoLayoutByMovement){               
               ((AutoLayoutByMovement) sklComp.autolayout).x = bounds.x;
               ((AutoLayoutByMovement) sklComp.autolayout).y = bounds.y;               
            } else if (sklComp.autolayout instanceof AutoLayoutByOffset){
               sklComp.autolayout.update();
            } else{
               sklComp.autolayout.update();
            }
         }
      }
      
      
      if (autolayout instanceof AutoLayoutByOffset){
         //((AutoLayoutByOffset autolayout)).offsetx *= zoomLevel;
         //((AutoLayoutByOffset autolayout)).offsety *= zoomLevel;         
      }
      
   }
   
//   public void zoomIn(){
//      
//      float zoomStep = 1.1f;
//      Rectangle bounds = getBounds();
//      bounds.x *= zoomStep;
//      bounds.y *= zoomStep;
//      bounds.width *= zoomStep;
//      bounds.height *= zoomStep;      
//
//      setBounds(bounds);
//
//      if (autolayout instanceof AutoLayoutByOffset){
//         // no need to do anything
//      } else if (autolayout instanceof AutoLayoutByMovement){
//         ((AutoLayoutByMovement) autolayout).x *= zoomStep;
//         ((AutoLayoutByMovement) autolayout).y *= zoomStep;
//         autolayout.update();
//      }
//   }
//   
//   public void zoomOut(){
//      float zoomStep = 0.9f;
//      Rectangle bounds = getBounds();
//      bounds.x *= zoomStep;
//      bounds.y *= zoomStep;
//      bounds.width *= zoomStep;
//      bounds.height *= zoomStep;      
//
//      setBounds(bounds);
//
//      if (autolayout instanceof AutoLayoutByOffset){
//         // no need to do anything
//      } else if (autolayout instanceof AutoLayoutByMovement){
//         ((AutoLayoutByMovement) autolayout).x *= zoomStep;
//         ((AutoLayoutByMovement) autolayout).y *= zoomStep;
//         autolayout.update();
//      }
//   }
//   
//   private float zoomLevel = 1.0f;
//   public void setZoomLevel(float zoomLevel) {
//      this.zoomLevel = zoomLevel;
//   }
//
//   public float getZoomLevel() {
//      return zoomLevel;
//   }

   class Margin{
      int top;
      int left;
      int bottom;
      int right;
   }
   Margin margin = null;
   public void setMargin(int top, int left, int bottom, int right){
      margin = new Margin();
      margin.top = top;
      margin.left = left;
      margin.bottom = bottom;
      margin.right = right;
   }
   
   int offsetx = 0;
   int offsety = 0;
   public void setOffset(int offsetx, int offsety){
      this.offsetx = offsetx;
      this.offsety = offsety;
   }

   public void setLocationRelativeToRegion(Region region, int side) {

    //  reference_region = region;
     // reference_side = side;

      if (margin != null){
         Region rectWithSpacing = new Region(region);
         rectWithSpacing.x -= margin.left;
         rectWithSpacing.y -= margin.top;
         rectWithSpacing.w += (margin.left + margin.right);
         rectWithSpacing.h += (margin.top + margin.bottom);
         region = rectWithSpacing;
      }
      
      region.x += offsetx;
      region.y += offsety;

      int height = getHeight();
      int width = getWidth();
      if (side == TOP){
         setLocation(region.x + region.w/2 - width/2, region.y - height);
      } else if (side == BOTTOM){
         setLocation(region.x + region.w/2 - width/2, region.y + region.h);         
      } else if (side == LEFT){
         setLocation(region.x - width, region.y + region.h/2 - height/2);                  
      } else if (side == RIGHT){
         setLocation(region.x + region.w, region.y + region.h/2 - height/2);                  
      } else if (side == INSIDE){
         setLocation(region.x + region.w/2 - width/2, region.y + region.h/2 - height/2);                  
      } else if (side == OVER){
         setLocation(region.x,region.y);
         setSize(region.w,region.h);
      } else if (side == ORIGIN){
         setLocation(region.x,region.y);
      }

   }


   public void setHorizontalAlignmentWithRegion(Region region, float f){
      //reference_region = region;

      int x0 = region.x;
      int x1 = region.x + region.w - getWidth();

      int x = (int) (x0 + (x1-x0)*f);

      setLocation(x,getY());
   }

   public void setVerticalAlignmentWithRegion(Region region, float f){
      //reference_region = region;

      int y0 = region.y;
      int y1 = region.y + region.h - getHeight();

      int y = (int) (y0 + (y1-y0)*f);

      setLocation(getX(),y);
   }


   private ArrayList<SikuliGuideComponent> followers = new ArrayList<SikuliGuideComponent>();
   SikuliGuideComponent leader;

   //SikuliGuideComponent followerAsDestination;

//   public void removeFollower(SikuliGuideComponent follower){
//      followers.remove(followers);
//   }
   
   public void removeLeader(){
      if (leader != null)
         leader.removeFollower(this);
      leader = null;
   }
   
   public void addFollower(SikuliGuideComponent sklComp){
      if (followers.indexOf(sklComp)<0){
         // if this component is not already a follower

         // add it to the list of follower
         followers.add(sklComp);
         
         // remove its previous leader
         sklComp.removeLeader();
         
         // set its new leader to self
         sklComp.setLeader(this);
      }
   }

   public void setLeader(SikuliGuideComponent leader){
      this.leader = leader;
   }

   @Override
   public void setVisible(boolean visible){
      for (SikuliGuideComponent follower : getFollowers()){
         follower.setVisible(visible);
      }
      super.setVisible(visible);
   }
   
   @Override
   public void setLocation(Point location){
      setLocation(location.x, location.y);
   }   
   
   @Override
   public void setLocation(int x, int y){
      
      actualBounds.x = (int) (x/zoomLevel);
      actualBounds.y = (int) (y/zoomLevel);

      super.setLocation(x,y);
      
      for (SikuliGuideComponent sklComp : getFollowers()){
         Debug.info("update followers");
         if (sklComp.autolayout != null){
            sklComp.autolayout.update();
         }
         
      }

   }
   
//   @Override
//   public void setBounds(int x, int y, int w, int h){
//      
//      Rectangle bounds = new Rectangle(x,y,w,h);
//      
//      actualBounds = new Rectangle(bounds);
//      actualBounds.x /= zoomLevel;
//      actualBounds.y /= zoomLevel;
//      actualBounds.width /= zoomLevel;
//      actualBounds.height /= zoomLevel;      
//      
//      for (SikuliGuideComponent sklComp : getFollowers()){         
//         if (sklComp.autolayout != null){
//            sklComp.autolayout.update();
//         }
//      }
//      super.setBounds(x,y,w,h);
//   }

   @Override
   public void setBounds(Rectangle bounds){
      
      actualBounds = new Rectangle(bounds);
      actualBounds.x /= zoomLevel;
      actualBounds.y /= zoomLevel;
      actualBounds.width /= zoomLevel;
      actualBounds.height /= zoomLevel;      
      
      for (SikuliGuideComponent sklComp : getFollowers()){         
         if (sklComp.autolayout != null){
            sklComp.autolayout.update();
         }
      }
      super.setBounds(bounds);
   }
   
   @Override
   public void setSize(int width, int height){
      actualBounds.width = (int) (width/zoomLevel);
      actualBounds.height = (int) (height/zoomLevel);
      
      for (SikuliGuideComponent sklComp : getFollowers()){         
         if (sklComp.autolayout != null){
            sklComp.autolayout.update();
         }         
      }     
      super.setSize(width, height); 
   }
   
   @Override
   public void setSize(Dimension size){
      actualBounds.width = (int) (size.width/zoomLevel);
      actualBounds.height = (int) (size.height/zoomLevel);
      
      for (SikuliGuideComponent sklComp : getFollowers()){         
         if (sklComp.autolayout != null){
            sklComp.autolayout.update();
         }         
      }     
      super.setSize(size);

   }


   public Point getCenter(){
      Point loc = new Point(getLocation());
      Dimension size = getSize();
      loc.x += size.width/2;
      loc.y += size.height/2;
      return loc;
   }

   public ArrayList<SikuliGuideComponent> getFollowers() {
      return followers;
   }

   public SikuliGuideComponent getLeader() {
      return leader;
   }

   public void removeFollower(SikuliGuideComponent comp) {
      followers.remove(comp);
   }

   public void setAutoLayoutEnabled(boolean autoLayoutEnabled) {
      this.autoLayoutEnabled = autoLayoutEnabled;
   }

   public boolean isAutoLayoutEnabled() {
      return autoLayoutEnabled;
   }

   public void setAutoResizeEnabled(boolean autoResizeEnabled) {
      this.autoResizeEnabled = autoResizeEnabled;
   }

   public boolean isAutoResizeEnabled() {
      return autoResizeEnabled;
   }

   public void setAutoMoveEnabled(boolean autoMoveEnabled) {
      this.autoMoveEnabled = autoMoveEnabled;
   }

   public boolean isAutoMoveEnabled() {
      return autoMoveEnabled;
   }

   public void removeFrom(Container container) {
      for (SikuliGuideComponent follower : getFollowers()){
         follower.removeFrom(container);
      }
      container.remove(this);
   }

   public void setAutoVisibilityEnabled(boolean autoVisibilityEnabled) {
      this.autoVisibilityEnabled = autoVisibilityEnabled;
   }

   public boolean isAutoVisibilityEnabled() {
      return autoVisibilityEnabled;
   }


}