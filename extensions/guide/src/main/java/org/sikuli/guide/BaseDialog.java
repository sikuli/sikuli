package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.TransparentWindow;

abstract public class BaseDialog extends TransparentWindow {
   
   static public final String PREVIOUS = "Previous";
   static public final String NEXT = "Next";
   static public final String CLOSE = "Close";

   protected Object owner;
   
   int style;
   
   public BaseDialog(Object owner){
      this.owner = owner;
   
      // this allows the window to be dragged to another location on the screen
      ComponentMover cm = new ComponentMover();
      cm.registerComponent(this);
      
      Container panel = this.getContentPane();
      panel.setBackground(Color.BLACK);

      // this makes the dialogbox semi-transparent
      setOpacity(0.9f);
   }
   
   
   public String getActionCommand(){
      return NEXT;
   }
   
   protected void dismiss(){
      setVisible(false);
      dispose();
      synchronized(owner){
         owner.notify();
      }
   }
   
}


