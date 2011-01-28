package org.sikuli.guide;

import org.sikuli.script.Debug;
import org.sikuli.script.SikuliAction;
import org.sikuli.script.SikuliActionListener;
import org.sikuli.script.SikuliActionManager;

public class SikuliActionSimpleLogger implements SikuliActionListener{

   public SikuliActionSimpleLogger(){
      SikuliActionManager.getInstance().addListener(this);
   }

   @Override
   public void targetClicked(SikuliAction action) {
      Debug.info("Sikuli clicked on " + action.getMatch());      
   }

   @Override
   public void targetDoubleClicked(SikuliAction action) {
      Debug.info("Sikuli double-clicked on " + action.getMatch());      
   }

   @Override
   public void targetRightClicked(SikuliAction action) {
      Debug.info("Sikuli right-clicked on " + action.getMatch());            
   }
}
