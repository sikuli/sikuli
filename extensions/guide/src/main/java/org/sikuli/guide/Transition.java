package org.sikuli.guide;

public interface Transition {
   
   public interface TransitionListener {      
      void transitionOccurred(Object source);      
   }

   String waitForTransition(TransitionListener token);
   
}

