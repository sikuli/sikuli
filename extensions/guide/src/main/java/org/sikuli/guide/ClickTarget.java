package org.sikuli.guide;
import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.TransparentWindow;



// TODO: Automatically move mouse cursor to the click target. The current implementation
// is problematic for non-rectangular clickable widgets, for instance, a round buttone. 
// Since the highlighted region is always rectangular and is larger than the area that
// is actually cliable, users may click on the edge of the region and dismiss the window
// errorneously. This needs to be fixed.

public class ClickTarget extends TransparentWindow implements MouseListener {
   
   // object to notify when this target is clicked
   Object owner;

   // rectangle defining the bounds of the click target in global screen coordinate   
   Rectangle rect;
   
   // name of this click target for the caller to identify which target is actually clicked
   String name;
   
   public String getName() {
      return name;
   }

   Point clickLocation;
     
   public Point getClickLocation() {
      return clickLocation;
   }

   public ClickTarget(Object owner_, Rectangle rect, String name){
         
      this.rect = rect;
      this.owner = owner_;
      this.name = name;

      // Positions the click target to the given rectangle
      setBounds(rect);

      setAlwaysOnTop(true);
      
      // This paints a transparent black window on top the target
      //Color transparentColor = new Color(0F,0F,0F,0F);
      
      // add a little shade otherwise the window can't be clicked
      Color transparentColor = new Color(1F,0F,0F,0.1F);
      //setBackground(transparentColor);
      
      Container panel = this.getContentPane();
      panel.setBackground(Color.red);
      setBackground(Color.red);
      setOpacity(0.1f);

     // toBack();
      //setFocusableWindowState(false);
      addMouseListener(this);
      panel.addMouseListener(this);
      
      addMouseMotionListener(new MouseMotionListener(){

         @Override
         public void mouseDragged(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
         }

         @Override
         public void mouseMoved(MouseEvent arg0) {
            Debug.info("Mouse moved");
            
         }});
   }
   
   public void setIgnoreMouse(boolean ignore){
      Env.getOSUtil().bringWindowToFront(this, ignore);
   }

   @Override
   // notifies the owner of this click target that the target has
   // been clicked
   public void mouseClicked(MouseEvent e) {
      // System.out.println(e.getX() + "," + e.getY());
      clickLocation = e.getPoint();

      synchronized(owner){
         owner.notify();
         ((SikuliGuide) owner).setLastClickedTarget(this);
      }
   }

   @Override
   public void mouseEntered(MouseEvent arg0) {
      Debug.info("Mouse entered a clickable");
   }

   @Override
   public void mouseExited(MouseEvent arg0) {
   }

   @Override
   public void mousePressed(MouseEvent arg0) {
   }

   @Override
   public void mouseReleased(MouseEvent arg0) {
      // TODO Auto-generated method stub
      
   }
   
}