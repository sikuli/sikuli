/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.*;
import java.awt.image.*;

public class UnionScreen extends Screen {
   static Rectangle _bounds;

   public UnionScreen(){
      super(0);
   }

   public int getIdFromPoint(int x, int y){
      Debug.log(5, "union bound: " + getBounds() );
      Debug.log(5, "x, y: " + x + "," + y);
      x += getBounds().x;
      y += getBounds().y;
      Debug.log(5, "new x, y: " + x + "," + y);
      for(int i=0;i<getNumberScreens();i++)
         if(Screen.getBounds(i).contains(x, y)){
            return i;
         }
      return 0;
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
            Debug.log(5, "scrBound: " + scrBound + ", inter: " +inter);
            int ix = inter.x, iy = inter.y;
            inter.x-=scrBound.x;
            inter.y-=scrBound.y;
            BufferedImage img = _robots[i].createScreenCapture(inter);
            g2d.drawImage(img, ix-rect.x, iy-rect.y, null);
         }
      }
      g2d.dispose();
      return new ScreenImage(rect, ret);
   }

   boolean useFullscreen(){
      return false;
   }

} 
