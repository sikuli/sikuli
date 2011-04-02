/**
 * 
 */
package org.sikuli.guide;

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
         clone.connectors = new ArrayList<Connector>();
         return clone;
      }
      catch (CloneNotSupportedException e) {
         throw new InternalError(e.toString());
      }
   }

   Region reference_region = null;
   int reference_side = -1;

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
      private JComponent targetComponent;
      AutoLayout(JComponent targetComponent){
         this.setTargetComponent(targetComponent);
         targetComponent.addComponentListener(this);
      }
      public void setTargetComponent(JComponent targetComponent) {
         this.targetComponent = targetComponent;
      }
      public JComponent getTargetComponent() {
         return targetComponent;
      }
            
      void update() {
         
         // TODO calculate necesary region to udpate
         if (getParent()!=null){
            
            if (getParent().getParent()!=null){
               getParent().getParent().repaint();
            }else{
               getParent().repaint();
            }
         }
      }
      
      void stop(){
         targetComponent.removeComponentListener(this);
      }
      
      @Override
      public void componentHidden(ComponentEvent e) {
         setVisible(false);
      }

      @Override
      public void componentMoved(ComponentEvent e) {
         update();         
      }

      @Override
      public void componentResized(ComponentEvent e) {
         update();
      }

      @Override
      public void componentShown(ComponentEvent e) {
         setVisible(true);
      }
      
   }
   
   class AutoLayoutBySide extends AutoLayout{
      int side;
      
      AutoLayoutBySide(JComponent targetComponent, int side){
         super(targetComponent);
         this.side = side;
      }
      
      void update(){
         Region region = new Region(getTargetComponent().getBounds());
         setLocationRelativeToRegion(region, side);     
         super.update();
      }
   }
   
   class AutoLayoutByOffset extends AutoLayout {
      int offsetx;
      int offsety;
      
      AutoLayoutByOffset(JComponent targetComponent, int offsetx, int offsety){
         super(targetComponent);
         this.offsetx = offsetx;
         this.offsety = offsety;
      }

      void update(){
         setOffset(offsetx, offsety);         
         Region region = new Region(getTargetComponent().getBounds());
         setLocationRelativeToRegion(region, SikuliGuideComponent.ORIGIN);
         super.update();
      }
   }
   
   class AutoLayoutByRatio extends AutoLayout{
      float x, y;
      
      AutoLayoutByRatio(JComponent targetComponent, float x, float y){
         super(targetComponent);
         this.x = x;
         this.y = y;
      }
      
      void update(){
         Region region = new Region(getTargetComponent().getBounds());
         setHorizontalAlignmentWithRegion(region, x);
         setVerticalAlignmentWithRegion(region, y);         
         super.update();
      }
   }
   
   AutoLayout autolayout = null;   
   
   public void setLocationRelativeToComponent(JComponent comp, int side) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      autolayout = new AutoLayoutBySide(comp, side);      
      autolayout.update();           
   }
   
   public void setLocationRelativeToComponent(JComponent comp, int offsetx, int offsety) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      autolayout = new AutoLayoutByOffset(comp, offsetx, offsety);     
      autolayout.update();      
   }

   public void setLocationRelativeToComponent(JComponent comp, float relativeX, float relativeY) {
      if (autolayout != null){
         autolayout.stop();
      } 
      
      autolayout = new AutoLayoutByRatio(comp, relativeX, relativeY);      
      autolayout.update();        
   }


   public void setLocationRelativeToPoint(Point point, int side){      
      Rectangle bounds = getBounds();
      // TODO implement other positioning parameters
      if (side == CENTER){
         setLocation(point.x - bounds.width/2, point.y - bounds.height/2);
      }
   }


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

      reference_region = region;
      reference_side = side;

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
      reference_region = region;

      int x0 = region.x;
      int x1 = region.x + region.w - getWidth();

      int x = (int) (x0 + (x1-x0)*f);

      setLocation(x,getY());
   }

   public void setVerticalAlignmentWithRegion(Region region, float f){
      reference_region = region;

      int y0 = region.y;
      int y1 = region.y + region.h - getHeight();

      int y = (int) (y0 + (y1-y0)*f);

      setLocation(getX(),y);
   }


   private ArrayList<SikuliGuideComponent> followers = new ArrayList<SikuliGuideComponent>();
   SikuliGuideComponent leader;

   SikuliGuideComponent followerAsDestination;
   private ArrayList<Connector> connectors = new ArrayList<Connector>();  

   public void addFollower(SikuliGuideComponent follower){
      getFollowers().add(follower);
      follower.setLeader(this);
   }

   public void setLeader(SikuliGuideComponent followee){
      this.leader = followee;
   }

   @Override
   public void setLocation(int x, int y){
      
      
//      for (Connector connector : connectors){
//         connector.update(this);
//      }
     
      super.setLocation(x,y);
      
      // notify dependents immediately so they can get repainted together
      for (ComponentListener listener : getComponentListeners()){         
         listener.componentMoved(null);
      }
   }

   public void addConnector(Connector connector) {
      this.connectors.add(connector);
   }

   public ArrayList<Connector> getConnectors() {
      return connectors;
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

}