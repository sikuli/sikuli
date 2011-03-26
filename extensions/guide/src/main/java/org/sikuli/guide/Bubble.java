package org.sikuli.guide;
import org.sikuli.script.Debug;
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
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;



public class Bubble extends TransparentWindow 
implements Transition{


   ArrayList<Region> targets = new ArrayList<Region>();


   SikuliGuide guide;
   public Bubble(SikuliGuide guide){
      this.guide = guide;

      setBackground(null);

      // when opaque is set to false, the content seems to get cleared properly
      // this is tested on both Windows and Mac
      Env.getOSUtil().setWindowOpaque(this, false);

      setOpacity(0.7f);

      // addTarget(target);
   }

   public void addTarget(Region target){
      targets.add(target);
   }

   public Point current = null;
   //public Point to = null;
   //Region target;

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      //drawRayPolygon(g, current, target.getRect());


      for (Region target : targets)
         drawBubble(g, target);  

      
      if (current != null){
         Region t = getNearestTarget();
         g2d.drawRect(t.x,t.y,t.w,t.h);
      }
      
   }

   
   public Region getNearestTarget(){
      
      double min_distance = Double.MAX_VALUE;
      Region nearest_target = null;
      
      for (Region target : targets){
         Point target_point = new Point(target.getCenter());
         double distance = current.distance(target_point);
         
         if (nearest_target == null || distance < min_distance){
            nearest_target = target;
            min_distance = distance;
         }
         
      }
      
      
      return nearest_target;
   }


   public void drawBubble(Graphics g, Region target){
      if (current == null || target == null)
         return;


      Point target_point = new Point(target.getCenter());
      double distance = current.distance(target_point);
      double inner_radius = ((target.h/2)^2 + (target.w/2)^2)^(1/2);
      double distance_to_boundary = distance - inner_radius;

      int prim_radius = (int) Math.max(10, 100 - distance_to_boundary);

      Graphics2D g2d = (Graphics2D)g;

      Region perim = new Region(target);
      Rectangle r = perim.getRect();
      r.grow(prim_radius, prim_radius);
      perim.setRect(r);

      Ellipse2D.Double inner =
         new Ellipse2D.Double(target.x,target.y,target.w,target.h);

      Ellipse2D.Double outter =
         new Ellipse2D.Double(perim.x,perim.y,perim.w,perim.h);

      //g2d.clip(ellipse);      
      //g2d.drawImage(image, 0, 0, w, h, null); 

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);       
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));        

      g2d.setStroke(new BasicStroke(3.0F));      
      g2d.setColor(Color.red);
      g2d.fill(outter);
      
      g2d.drawLine(current.x,current.y,target_point.x,target_point.y);
      

      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));        
      g2d.setColor(new Color(0,0,0,0));
      g2d.fill(inner);

      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
      Point c = target.getCenter();
      g2d.setColor(Color.black);
      g2d.drawString(""+distance, c.x, c.y);




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

   
   ArrayList<Point> lastMouseLocations = new  ArrayList<Point>();
   

   @Override
   public String waitForTransition() {

      setBounds(guide.getRegion().getRect());
      setVisible(true);
      toFront();

      Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
      Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
      Cursor currentCursor = null;
      
      

      //Screen s = new Screen();
      Robot robot = null;
      try {
         robot = new Robot();
      } catch (AWTException e) {
      }
      
      
      boolean running = true;
      while (running){
         
         
         try {
            Thread.sleep(50);
         } catch (InterruptedException e) {
         }
         
         Location m = Env.getMouseLocation();
         current = new Point(m.x,m.y);
         Cursor cursor = null;
         
         if (lastMouseLocations.size() == 5){
            lastMouseLocations.remove(0);
         } 
         lastMouseLocations.add(current);
                  
         //Debug.log("locs:" + lastMouseLocations);
   
         
         Iterator<Point> a = lastMouseLocations.iterator();
         Iterator<Point> b = lastMouseLocations.iterator();
         b.next();
         
         ArrayList<Point> moves = new  ArrayList<Point>();

         int px=0;
         int py=0;
         int cnt=0;
         
         String str = "";
         while (b.hasNext()){
            
            Point p = a.next();
            Point q = b.next();
            
            Point move = new Point(q.x-p.x, q.y-p.y); 
            moves.add(move);
            
            px += move.x;
            py += move.y;
            
            if (move.x!=0||move.y!=0){
               cnt++;
            }
            
            str += "(" + (q.x - p.x) + "," + (q.y - p.y) + ")";
         }

         if (cnt > 3 && (px != 0 || py != 0)){

            Debug.log("move: (" + px + "," + py + ")");

         }

//         if (moves.size()>0){
//            Point last = moves.get(moves.size()-1);
//
//            if (last.x != 0 || last.y != 0){
//
//               Debug.log("move:" + last);
//
//            }
//         }
         
         Region r = getNearestTarget();
         Point dest = r.getCenter();
         
         int dx = (dest.x - current.x)/20;
         int dy = (dest.y - current.y)/20;
         
//         robot.mouseMove(current.x+dx, current.y+dy);
//         robot.delay(50);

         //         try {
//            s.hover(new Location(current.x+dx, current.y+dy));
//         } catch (FindFailed e) {
//         }


         for (Region target: targets){
            Rectangle target_rect = target.getRect();
            if (target_rect.contains(current)){
               running = false;
               cursor = handCursor;

               setVisible(false);
               dispose();
               synchronized(guide){
                  guide.notify();
                  dispose();
                  return BaseDialog.NEXT;
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

      }
      return BaseDialog.NEXT;
   }


}
