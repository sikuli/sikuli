/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Timer;

import org.sikuli.script.Debug;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class SklAnimationManager{
   
   @Element
   SklAnimationSequence seq;
   public SklAnimationManager(){
      seq = new SklAnimationSequence();
   }
   
   public void add(SklAnimation anim){
      seq.add(anim);
   }
   
   public void start(){
      seq.start();
   }
}

@Root
class SklAnimationSequence {
   
   @ElementList
   Queue<SklAnimation> queue = new LinkedBlockingQueue<SklAnimation>();

   private void startNextAnimation(){
      if (queue.peek() != null){
         
         SklAnimation anim = queue.remove();
         anim.start();
         anim.setListener(new SklAnimationListener(){

            @Override
            public void animationCompleted() {
               startNextAnimation();
            }            
         });
         
      }
   }
   
   public void add(SklAnimation animator){
      queue.add(animator);
   }
   
   public void start(){
      startNextAnimation();
   }
}


