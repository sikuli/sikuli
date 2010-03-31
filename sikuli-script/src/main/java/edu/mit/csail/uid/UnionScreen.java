package edu.mit.csail.uid;

import java.awt.*;

public class UnionScreen extends Screen {
   Rectangle _bounds;

   public UnionScreen(){
      super(0);
   }

   public Rectangle getBounds(){
      if(_bounds == null){
         _bounds = new Rectangle();
         for (int i=0; i < Screen.getNumberScreens(); i++) {
            _bounds = _bounds.union(Screen.getBounds(i));
         }
      }
      return _bounds;
   }

   boolean useFullscreen(){
      return false;
   }

} 
