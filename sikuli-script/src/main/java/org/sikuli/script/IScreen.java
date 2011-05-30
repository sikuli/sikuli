/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.Rectangle;

public interface IScreen {
   public IRobot getRobot();
   public Rectangle getBounds();

   public Region newRegion(Rectangle rect);

   public ScreenImage capture();
   public ScreenImage capture(int x, int y, int w, int h);
   public ScreenImage capture(Rectangle rect);
   public ScreenImage capture(Region reg);


   public void showMove(Location loc);
   public void showClick(Location loc);
   public void showTarget(Location loc);
   public void showDropTarget(Location loc);
}
