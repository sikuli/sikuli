package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;

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


   public ScreenImage capture(Rectangle rect) {
      Debug.log(5, "capture: " + rect);

      BufferedImage ret = new BufferedImage( rect.width, rect.height, 
                                             BufferedImage.TYPE_INT_RGB );
      Graphics2D g2d = ret.createGraphics();
      for (int i=0; i < Screen.getNumberScreens(); i++) {
         Rectangle scrBound = Screen.getBounds(i);
         if(scrBound.intersects(rect)){
            Rectangle inter = scrBound.intersection(rect);
            Debug.log(3, "scrBound: " + scrBound + ", inter: " +inter);
            int ix = inter.x, iy = inter.y;
            inter.x-=scrBound.x;
            inter.y-=scrBound.y;
            BufferedImage img = _robots[i].createScreenCapture(inter);
            g2d.drawImage(img, ix-_bounds.x, iy-_bounds.y, null);
         }
      }
      g2d.dispose();
      return new ScreenImage(rect, ret);
   }

   boolean useFullscreen(){
      if(getNumberScreens()==1)
         return true;
      return false;
   }

} 
