package org.sikuli.guide;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.ScreenHighlighter;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;



public class Spotlight extends TransparentWindow 
implements SingletonInteractionTarget{


   SikuliGuide guide;
   public Spotlight(SikuliGuide guide, Region target){
      this.guide = guide;
      this.target = target;
      
      setBackground(null);

      // when opaque is set to false, the content seems to get cleared properly
      // this is tested on both Windows and Mac
      Env.getOSUtil().setWindowOpaque(this, false);
      
      setOpacity(0.7f);
   }

   public Point current = null;
   public Point to = null;
   Region target;

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);
      
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      drawRayPolygon(g, current, target.getRect());      
   }

   public void drawRayPolygon(Graphics g, Point p, Rectangle rect){
      if (p == null || rect == null)
         return;

      Graphics2D g2d = (Graphics2D)g;

      Rectangle r = rect;
      // corners of the target rectangle
      int cxs[] = {r.x,r.x,r.x+r.width,r.x+r.width};
      int cys[] = {r.y,r.y+r.height,r.y+r.height,r.height};


      ArrayList<Point> corners = new ArrayList<Point>();
      corners.add(new Point(r.x,r.y));
      corners.add(new Point(r.x+r.width,r.y+r.height));
      corners.add(new Point(r.x+r.width,r.y));
      corners.add(new Point(r.x,r.y+r.height));

      Collections.sort(corners, new Comparator(){
         @Override
         public int compare(Object arg0, Object arg1) {
            return (int) (current.distance((Point) arg0) - current.distance((Point) arg1));
         }});

      int[]xs;
      int[]ys;

      int d = 5;
      if (p.x > rect.getMinX()-5 && p.x < rect.getMaxX()+5 ||
            p.y > rect.getMinY()-5 && p.y < rect.getMaxY()+5){

         xs = new int[3];
         ys = new int[3];


         xs[0] = (int) p.x;
         xs[1] = (int) corners.get(0).x;
         xs[2] = (int) corners.get(1).x;

         ys[0] = (int) p.y;
         ys[1] = (int) corners.get(0).y;
         ys[2] = (int) corners.get(1).y;


      }else{

         xs = new int[4];
         ys = new int[4];


         xs[0] = (int) p.x;
         xs[1] = (int) corners.get(2).x;
         xs[2] = (int) corners.get(0).x;
         xs[3] = (int) corners.get(1).x;

         ys[0] = (int) p.y;
         ys[1] = (int) corners.get(2).y;
         ys[2] = (int) corners.get(0).y;
         ys[3] = (int) corners.get(1).y;
      }

      Polygon shape = new Polygon(xs, ys, xs.length);

      Stroke pen = new BasicStroke(3.0F);
      g2d.setStroke(pen);
      g2d.setColor(Color.black);
      //g2d.drawPolygon(pointing_triangle);
      //g2d.drawRect(x,y,w,h);


      g2d.setColor(Color.red);
      g2d.fillPolygon(shape);		
      g2d.drawRect(rect.x,rect.y,rect.width,rect.height);



   }

   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
         Env.getOSUtil().bringWindowToFront(this, true);
      }     
      super.toFront();
   }


   @Override
   public String waitUserAction() {

      setBounds(guide.getRegion().getRect());
      setVisible(true);
      toFront();

      Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
      Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
      Cursor currentCursor = null;

      boolean running = true;
      while (running){

         Rectangle target_rect = target.getRect();
         Location m = Env.getMouseLocation();

         current = new Point(m.x,m.y);

         Cursor cursor = null;
         if (target_rect.contains(current)){
            running = false;
            cursor = handCursor;

            setVisible(false);
            dispose();
            synchronized(guide){
               guide.notify();
               dispose();
               return SikuliGuideDialog.NEXT;
            }

         }else{
            cursor = defaultCursor;
            repaint();
         }


         if (cursor != currentCursor){
            setCursor(cursor);
            currentCursor = cursor;
         }

      }
      return SikuliGuideDialog.NEXT;
   }


}
