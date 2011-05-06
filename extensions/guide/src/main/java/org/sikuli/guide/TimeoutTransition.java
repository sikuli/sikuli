package org.sikuli.guide;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;
import org.sikuli.script.TransparentWindow;


public class TimeoutTransition implements Transition, ActionListener {

   Timer timer;
   TransitionListener listener;
   public TimeoutTransition(int timeout){
       timer = new Timer(timeout,this);
   }
      
   public String waitForTransition(final TransitionListener listener){
      this.listener = listener;
      timer.start();
      return "Next";
   }

   @Override
   public void actionPerformed(ActionEvent arg0) {
      listener.transitionOccurred(this);
   }         
     
}


