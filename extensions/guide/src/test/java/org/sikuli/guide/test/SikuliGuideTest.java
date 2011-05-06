package org.sikuli.guide.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.sikuli.guide.Beam;
import org.sikuli.guide.BlobWindow;
import org.sikuli.guide.Bubble;
import org.sikuli.guide.Clickable;
import org.sikuli.guide.Hotspot;
import org.sikuli.guide.Magnet;
import org.sikuli.guide.Portal;
import org.sikuli.guide.SikuliGuide;
import org.sikuli.guide.SikuliGuideAnchor;
import org.sikuli.guide.SikuliGuideArea;
import org.sikuli.guide.SikuliGuideArrow;
import org.sikuli.guide.SikuliGuideBracket;
import org.sikuli.guide.SikuliGuideButton;
import org.sikuli.guide.SikuliGuideCallout;
import org.sikuli.guide.SikuliGuideCircle;
import org.sikuli.guide.SikuliGuideComponent;
import org.sikuli.guide.SikuliGuideFlag;
import org.sikuli.guide.SikuliGuideImage;
import org.sikuli.guide.SikuliGuideRectangle;
import org.sikuli.guide.SikuliGuideSpotlight;
import org.sikuli.guide.SikuliGuideText;
import org.sikuli.guide.TimeoutTransition;
import org.sikuli.guide.TransitionDialog;
import org.sikuli.guide.TreeSearchDialog;
import org.sikuli.guide.SikuliGuideAnchor.AnchorListener;
import org.sikuli.guide.SikuliGuideComponent.AnimationListener;
import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.guide.model.GUIModel;
import org.sikuli.guide.model.GUINode;
import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;


public class SikuliGuideTest {

   GUIModel createPPTModel(){

      GUINode top = new GUINode(null);
      GUIModel tree = new GUIModel(top);

      GUINode x = new GUINode(new Pattern("ppt_general.png"));
      x.setName("General");
      top.add(x);

      GUINode y = new GUINode(new Pattern("ppt_general_options.png"));
      y.setName("General Options");
      x.add(y);

      y = new GUINode(new Pattern("ppt_movie_options.png"));
      //y.setName("Movie Options");
      y.setName("Photo Browser");
      x.add(y);

      GUINode z = new GUINode(new Pattern("ppt_slide_transitions.png"));
      z.setName("Slide Transitions");
      y.add(z);


      x = new GUINode(new Pattern("ppt_view.png"));
      x.setName("View");
      top.add(x);

      y = new GUINode(new Pattern("ppt_show.png"));
      y.setName("Show");
      x.add(y);

      y = new GUINode(new Pattern("ppt_slideshow.png"));
      y.setName("Slideshow");
      x.add(y);

      x = new GUINode(new Pattern("ppt_edit.png"));
      x.setName("Edit");
      top.add(x);

      y = new GUINode(new Pattern("ppt_cutandpaste.png"));
      y.setName("Cut and paste");
      x.add(y);

      y = new GUINode(new Pattern("ppt_text.png"));
      y.setName("Text");
      x.add(y);


      return tree;
   }

   GUIModel createChromeModel(){

      GUINode top = new GUINode(null);
      GUIModel tree = new GUIModel(top);

      GUINode x = new GUINode(new Pattern("chrome_basics.png"));
      x.setName("Basics");
      x.addTag("basics");      
      top.add(x);

      GUINode y = new GUINode(new Pattern("chrome_onstartup.png"));
      y.setName("On Startup");
      y.addTag("startup");
      x.add(y);

      y = new GUINode(new Pattern("chrome_homepage.png"));
      y.setName("Homepage");
      y.addTag("homepage");
      x.add(y);

      y = new GUINode(new Pattern("chrome_toolbar.png").similar(0.9f));
      y.setName("Toolbar");
      y.addTag("toolbar");
      x.add(y);

      x = new GUINode(new Pattern("chrome_personalstuff.png"));
      x.setName("Personal Stuff");
      x.addTag("personal");
      x.addTag("stuff");
      top.add(x);

      y = new GUINode(new Pattern("chrome_passwords.png"));
      y.setName("Passwords");
      y.addTag("passwords");
      x.add(y);


      x = new GUINode(new Pattern("chrome_underthehood.png"));
      x.setName("Under the Hood");
      x.addTag("under");
      x.addTag("hood");
      top.add(x);

      return tree;
   }

   GUIModel createModel(){
      GUINode top = new GUINode(null);
      GUIModel tree = new GUIModel(top);

      GUINode file = new GUINode(new Pattern("file.png"));
      file.addTag("file");

      GUINode refresh = new GUINode(new Pattern("refresh.png"));
      refresh.addTag("refresh");

      GUINode print = new GUINode(new Pattern("print.png"));
      print.addTag("print");

      file.add(refresh);
      file.add(print);


      GUINode search = new GUINode(new Pattern("search.png"));
      search.addTag("search");

      GUINode references = new GUINode(new Pattern("references.png"));
      references.addTag("references");
      search.add(references);

      top.add(file);
      top.add(search);

      return tree;      
   }


   //   @Test
   //   public void testModel() throws FindFailed{
   //
   //      GUIModel tree = createModel();
   //      
   //
   //      ArrayList<GUINode> nodes = tree.getNodesByTag("refresh");
   //      for (GUINode node : nodes){
   //         Debug.info("node: " + node);
   //      }
   //
   //      GUINode target = nodes.get(0);
   //
   //      Screen s = new Screen();
   //      SikuliGuide g = new SikuliGuide();
   //      
   //      Match m = target.findOnScreen();
   //      
   //      if (m == null){
   //         m = target.findAncestorOnScreen();
   //         
   //         
   //         s.click(m, 0);
   //         
   //         m = s.wait(target.getPattern());
   //         
   //         
   //         g.addCircle(m);
   //         g.addFlag(m.getTopLeft().below(m.h/2),"    ");
   //         g.showNow(3);
   //         
   //      }
   //      
   //      GUINode print = tree.getNodesByTag("print").get(0);
   //      
   //      m = print.findOnScreen();
   //      g.addCircle(m);
   //      g.addFlag(m.getTopLeft().below(m.h/2),"    ");
   //      g.showNow(3);
   //      
   //
   //      target = tree.getNodesByTag("references").get(0);;
   //      
   //      m = target.findAncestorOnScreen();
   //      
   //      s.hover(m);
   //      
   //      m = s.wait(target.getPattern());
   //      
   //      g.addCircle(m);
   //      g.addFlag(m.getTopLeft().below(m.h/2),"    ");
   //      g.showNow(3);
   //      
   //
   //   }

   @Test
   public void testAnimation(){
      
      SikuliGuide g = new SikuliGuide();
      Region region = new Region(10,10,100,100);
      SikuliGuideRectangle r = new SikuliGuideRectangle(region);

      g.addToFront(r);
      
      r.addMoveAnimation(new Point(10,10), new Point(500, 250));
      r.addMoveAnimation(new Point(500, 250), new Point(1000, 10));
      r.addMoveAnimation(new Point(1000, 10), new Point(1000, 500));
      r.addResizeAnimation(new Dimension(100,100), new Dimension(200,200));

      SikuliGuideFlag f = new SikuliGuideFlag("Flag");
      f.addMoveAnimation(new Point(10,10), new Point(300,10));
      f.addCircleAnimation(new Point(300,10), 10);      
      f.setShadowDefault();
      
      g.addToFront(f);
      
      
      SikuliGuideText t = new SikuliGuideText("Fade in text");
      t.setActualLocation(200,200);
      t.setShadowDefault();
      t.addFadeinAnimation();
      g.addToFront(t);
      

      SikuliGuideCallout co = new SikuliGuideCallout("This is a big callout box");
      co.setActualLocation(10,500);
      co.setShadowDefault();
      co.addFadeinAnimation();
      g.addToFront(co);

      
      g.setDialog("testing animations");
      g.showNow(3);      
      
      r = new SikuliGuideRectangle(region);
      g.addToFront(r);

      Debug.info("Animation stopped");
      g.setDialog("animation stopped");
      g.showNow(3);      
      
   }
   
   @Test
   public void testFlag(){
      SikuliGuide g = new SikuliGuide();

      Region region = new Region(400,200,100,100);
      SikuliGuideRectangle r = new SikuliGuideRectangle(region);

      g.addToFront(r);

      SikuliGuideFlag c = new SikuliGuideFlag("TOP"); 
      c.setLocationRelativeToComponent(r, Layout.TOP);
      c.setShadowDefault();      
      c.addSlideAnimation(c.getActualLocation(), Layout.TOP);
      g.addToFront(c);     

      c = new SikuliGuideFlag("LEFT"); 
      c.setLocationRelativeToComponent(r, Layout.LEFT);
      c.setShadowDefault();
      c.addSlideAnimation(c.getActualLocation(), Layout.LEFT);
      g.addToFront(c);     

      c = new SikuliGuideFlag("RIGHT"); 
      c.setLocationRelativeToComponent(r, Layout.RIGHT);
      c.setShadowDefault();      
      
      Point p = c.getActualLocation();
      c.addSlideAnimation(p, Layout.RIGHT);
      c.addCircleAnimation(p, 3);

      g.addToFront(c);     
      
      

      c = new SikuliGuideFlag("BOTTOM"); 
      c.setLocationRelativeToComponent(r, Layout.BOTTOM);
      c.setShadowDefault();      
      c.addSlideAnimation(c.getActualLocation(), Layout.BOTTOM);
      
      g.addToFront(c);     

      g.setDialog("Testing flags");
      g.showNow(3);
   }
   
   @Test
   public void testCallout(){

      SikuliGuide guide = new SikuliGuide();

      Region region = new Region(400,200,100,100);
      //SikuliGuideRectangle r = new SikuliGuideRectangle(region);
      SikuliGuideSpotlight r = new SikuliGuideSpotlight(region);

      guide.addToFront(r);

      SikuliGuideCallout c = new SikuliGuideCallout("This is a callout box. " +
      "It has lots of text. Let's see if it wraps. Lots of text. Hahaha.");
      c.setLocationRelativeToComponent(r, Layout.TOP);
      c.setShadowDefault();
      
      guide.addToFront(c);     

      c = new SikuliGuideCallout("This is a callout box to the bottom");      
      c.setLocationRelativeToComponent(r, Layout.BOTTOM);
      c.setShadowDefault();

      guide.addToFront(c);     

      c = new SikuliGuideCallout("This is a callout box to the left");      
      c.setLocationRelativeToComponent(r, Layout.LEFT);
      c.setShadowDefault();

      guide.addToFront(c);     

      c = new SikuliGuideCallout("Right");      
      c.setLocationRelativeToComponent(r, Layout.RIGHT);
      c.setShadowDefault();
      //c.addSlideAnimation(c.getActualLocation(), Layout.RIGHT);

      guide.addToFront(c);     
      
      SikuliGuideComponent prev = c;

      c = new SikuliGuideCallout("Right of right");      
      c.setLocationRelativeToComponent(prev, Layout.RIGHT);
      c.setShadowDefault();
      guide.addToFront(c);     

      
      guide.setDialog("callout");
      guide.showNow(3);

   }


   @Test
   public void testModelSearchPath() throws FindFailed{

      GUIModel tree = createPPTModel();

      SikuliGuide guide = new SikuliGuide();

      tree.drawPathTo(guide, "photobrowser");      
      guide.showNow(3);


      //      tree.drawPathTo( "paste");      
      //      guide.showNow(3);
      //      
      //      tree.drawPathTo( "option");      
      //      guide.showNow(3);

   }


   @Test
   public void testModelSearch() throws FindFailed{

      GUIModel tree = createPPTModel();

      SikuliGuide guide = new SikuliGuide();

      TreeSearchDialog search = new TreeSearchDialog(guide, tree);
      search.setLocationRelativeTo(null);
      search.setAlwaysOnTop(true);

      guide.setSearchDialog(search);

      guide.setVisible(true);
      Debug.log(guide.showNow(20));


   }

//   @Test
//   public void testAnimation(){
//
//      SikuliGuide g = new SikuliGuide();
//
//      Region r = new Region(250,250,50,50);
//
//      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
//      o.setForeground(Color.red);
//      g.addToFront(o);
//
//
//      SikuliGuideText t = new SikuliGuideText("Sliding right");
//      t.setLocationRelativeToRegion(r, Layout.LEFT);
//      g.addToFront(t);
//      t.setEntranceAnimation(t.createSlidingAnimator(-20,0));
//
//      SikuliGuideFlag f = new SikuliGuideFlag("Sliding left");
//      f.setLocationRelativeToRegion(r, Layout.RIGHT);
//      g.addToFront(f);
//
//      t = new SikuliGuideText("Sliding down");
//      t.setLocationRelativeToRegion(r, Layout.TOP);
//      g.addToFront(t);
//      t.setEntranceAnimation(t.createSlidingAnimator(0,-20));
//
//      f = new SikuliGuideFlag("Sliding up");
//      f.setLocationRelativeToRegion(r, Layout.BOTTOM);
//      g.addToFront(f);
//
//
//      r = new Region(600,200,50,50);
//      o = new SikuliGuideRectangle(r);
//      o.setForeground(Color.blue);
//      g.addToFront(o);
//
//      t = new SikuliGuideText("Circling");
//      t.setLocationRelativeToRegion(r, Layout.LEFT);
//      g.addToFront(t);
//     // t.setEntranceAnimation(t.createCirclingAnimator(10));
//
//
//
//      g.setDialog("Test");
//      g.showNow(5);
//
//
//   }


   
   @Test
   public void testShadow(){
      
      SikuliGuide g = new SikuliGuide();

      Region r = new Region(250,250,50,50);


      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
      o.setForeground(Color.red);
      g.addToFront(o);

      SikuliGuideText top = new SikuliGuideText("This is Top");
      top.setLocationRelativeToRegion(r, Layout.TOP);
      top.setShadow(10,2);
      g.addToFront(top);
      
      g.setDialog("Test shadow");
      g.showNow(5);


   }
   
//   @Test
//   public void testSimpleFlag() {
//
//      SikuliGuide g = new SikuliGuide();
//
//      Region r = new Region(250,250,50,50);
//
//      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
//      o.setForeground(Color.red);
//      g.addToFront(o);
//
//      SikuliGuideFlag f = new SikuliGuideFlag("LEFT");
//      f.setLocationRelativeToRegion(r, Layout.LEFT);
//      g.addToFront(f);
//
//
//      f = new SikuliGuideFlag("RIGHT");
//      f.setLocationRelativeToRegion(r, Layout.RIGHT);
//      // f.setDirection(SimpleFlag.DIRECTION_WEST);
//      g.addToFront(f);
//
//      f = new SikuliGuideFlag("TOP");
//      //f.setDirection(SimpleFlag.DIRECTION_SOUTH);
//      f.setLocationRelativeToRegion(r, Layout.TOP);
//      g.addToFront(f);
//
//      f = new SikuliGuideFlag("BOTTOM");
//      //f.setDirection(SimpleFlag.DIRECTION_NORTH);
//      f.setLocationRelativeToRegion(r, Layout.BOTTOM);
//      g.addToFront(f);
//
//
//      g.showNow(5);
//
//   }

   @Test
   public void testBasicShapes() {

      SikuliGuide g = new SikuliGuide();

      Region r = new Region(250,250,300,150);

      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
      o.setForeground(Color.red);
      o.setShadow(5,2);
      g.addToFront(o);

      //r = new Region(550,550,200,150);

      SikuliGuideCircle c = new SikuliGuideCircle(r);
      c.setLocationRelativeToRegion(r, Layout.OVER);
      c.setShadow(5,2);
      c.setForeground(Color.green);
      g.addToFront(c);


      Region q = new Region(500,500,200,200);
      o = new SikuliGuideRectangle(q);
      o.setLocationRelativeToRegion(q, Layout.OVER);
      o.setForeground(Color.red);
      o.setShadow(5,2);
      g.addToFront(o);

      SikuliGuideArrow a = new SikuliGuideArrow( r.getTopLeft(), q.getTopLeft());
      a.setShadowDefault();
      a.setForeground(Color.blue);
      g.addToFront(a);
//
      a = new SikuliGuideArrow( r.getBottomLeft(), q.getBottomLeft());
      a.setShadowDefault();
      a.setStyle(SikuliGuideArrow.ELBOW_Y);
      a.setForeground(Color.cyan);
      g.addToFront(a);


      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,600);
      g.addToFront(btn);
      
      g.startTracking();
      g.showNow();

   }

   @Test
   public void testSimpleBracket() {

      SikuliGuide g = new SikuliGuide();

      Region r = new Region(250,250,300,150);

      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
      o.setForeground(Color.red);
      g.addToFront(o);

      SikuliGuideBracket f = new SikuliGuideBracket();
      f.setLocationRelativeToRegion(r, Layout.LEFT);
      f.setShadow(10,2);
      g.addToFront(f);
      

      f = new SikuliGuideBracket();
      f.setLocationRelativeToRegion(r, Layout.RIGHT);
      f.setShadow(10,2);
      g.addToFront(f);

      f = new SikuliGuideBracket();
      f.setLocationRelativeToRegion(r, Layout.TOP);
      f.setShadow(10,2);      
      g.addToFront(f);

      f = new SikuliGuideBracket();
      f.setLocationRelativeToRegion(r, Layout.BOTTOM);
      f.setShadow(10,2);      
      g.addToFront(f);


      g.showNow(5);

   }

   @Test
   public void testTracker() throws FindFailed{

      final SikuliGuide g = new SikuliGuide();
      final SikuliGuideAnchor a;

      Pattern p = new Pattern("printer.png");
      
      a = new SikuliGuideAnchor();
      //a.setEditable(true);
      a.setTracker(p);
      a.setOpacity(0f);
      a.addListener(new AnchorListener(){

         @Override
         public void anchored() {
            Debug.info("anchor is anchored");

            SikuliGuideComponent c;
            
            //c = new SikuliGuideCircle();
            c = new SikuliGuideSpotlight();
            c.setShadowDefault();
            c.setLocationRelativeToComponent(a, Layout.OVER);
            g.addToFront(c);
            
            c = new SikuliGuideCallout("Printer");
            c.setShadowDefault();
            c.setLocationRelativeToComponent(a, Layout.BOTTOM);
            g.addToFront(c);

            SikuliGuideComponent c1 = new SikuliGuideCallout("Another call");
            c1.setShadowDefault();
            c1.setLocationRelativeToComponent(c, Layout.RIGHT);
            g.addToFront(c1);

            c1 = new SikuliGuideFlag("Flag");
            c1.setShadowDefault();
            c1.setLocationRelativeToComponent(c, Layout.BOTTOM);
            g.addToFront(c1);

            
            a.addFadeinAnimation();
            a.startAnimation();            


         }
      });
      
      g.addToFront(a);

      
      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,600);
      g.addToFront(btn);
      
      g.showNow();

//
//      c4 = new SikuliGuideBracket();
//      c4.setLocationRelativeToRegion(r2, Layout.BOTTOM);
//      g.addToFront(c4);


//      ArrayList<SikuliGuideComponent> components = new ArrayList<SikuliGuideComponent>();
//      components.add(c2);
//      components.add(c3);
//      components.add(c4);
//
//      g.addTracker(new Pattern("printer.png"), r2, components);
//
//      g.setDialog("Tracking");
//      g.showNow();
//
//      //   
//      r1 = s.find("play.png");     
//      c1 = new SikuliGuideSpotlight(r1); 
//      g.addToFront(c1);      
//
//      g.addTracker(new Pattern("play.png"), r1, c1);


   }



   @Test
   public void testSpotlight(){
      SikuliGuide g = new SikuliGuide();

      Region r = new Region(250,250,100,100);

      SikuliGuideSpotlight o = new SikuliGuideSpotlight(r);
      g.addToFront(o);

      SikuliGuideText t = new SikuliGuideText("This is a spotlight");
      t.setLocationRelativeToComponent(o, Layout.TOP);
      t.setShadowDefault();
      g.addToFront(t);


      r = new Region(500,500,100,100);
      o = new SikuliGuideSpotlight(r);
      o.setShape(SikuliGuideSpotlight.CIRCLE);
      o.addSlideAnimation(o.getActualLocation(), Layout.LEFT);
      g.addToFront(o);

      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,600);
      g.addToFront(btn);

      g.showNow();  
   }

   
   @Test
   public void testText() {

      SikuliGuide g = new SikuliGuide();

      Region r = new Region(250,250,200,300);

      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
      o.setForeground(Color.red);
      g.addToFront(o);

      SikuliGuideText top = new SikuliGuideText("This is Top");
      top.setLocationRelativeToRegion(r, Layout.TOP);
      g.addToFront(top);



      SikuliGuideText bottom = new SikuliGuideText("This is Bottom");
      bottom.setLocationRelativeToRegion(r, Layout.BOTTOM);
      g.addToFront(bottom);

      SikuliGuideText left = new SikuliGuideText("This is Left");
      left.setLocationRelativeToRegion(r, Layout.LEFT);
      g.addToFront(left);

      SikuliGuideText right = new SikuliGuideText("This is Right");
      right.setLocationRelativeToRegion(r, Layout.RIGHT);
      g.addToFront(right);
      
      
      SikuliGuideText inside = new SikuliGuideText("This is Inside");
      inside.setLocationRelativeToRegion(r, Layout.INSIDE);
      g.addToFront(inside);


      SikuliGuideText tl = new SikuliGuideText("Right Alignment");
      tl.setLocationRelativeToRegion(r, Layout.LEFT);
      tl.setVerticalAlignmentWithRegion(r, 0f);
      g.addToFront(tl);

      SikuliGuideText bl = new SikuliGuideText("Bottom Alignment");
      bl.setShadowDefault();
      bl.setLocationRelativeToRegion(r, Layout.RIGHT);
      bl.setVerticalAlignmentWithRegion(r, 1f);
      g.addToFront(bl);

      SikuliGuideText ht = new SikuliGuideText("Left Alignment");
      ht.setShadowDefault(); 
      ht.setLocationRelativeToRegion(r, Layout.TOP);
      ht.setHorizontalAlignmentWithRegion(r, 0f);
      ht.setLocation(ht.getX(),ht.getY()-100);
      g.addToFront(ht);

      SikuliGuideText rb = new SikuliGuideText("Right Alignment");
      rb.setShadowDefault();
      rb.setLocationRelativeToRegion(r, Layout.BOTTOM);
      rb.setHorizontalAlignmentWithRegion(r, 1f);
      rb.setLocation(rb.getX(),rb.getY()+100);      
      g.addToFront(rb);

      SikuliGuideText cb = new SikuliGuideText("Center Alignment");
      cb.setLocationRelativeToRegion(r, Layout.BOTTOM);
      cb.setHorizontalAlignmentWithRegion(r, 0.5f);
      cb.setLocation(cb.getX(),cb.getY()+50);
      g.addToFront(cb);


      SikuliGuideText qq = new SikuliGuideText("Long text with maximum width set to 300");
      qq.setLocationRelativeToRegion(r, Layout.RIGHT);
      qq.setVerticalAlignmentWithRegion(r, 0f);      
      qq.setMaximumWidth(300);
      g.addToFront(qq);



      r = new Region(800,400,100,100);
      o = new SikuliGuideRectangle(r);
      o.setForeground(Color.green);
      g.addToFront(o);


      SikuliGuideText t = new SikuliGuideText("This is a sentence in small font and it is long and supposed to be auto wrapped.");      
      t.setFontSize(10);
      t.setLocationRelativeToComponent(o, Layout.TOP);
      //t.setHorizontalAlignmentWithRegion(r, 0.5f);      
      g.addToFront(t);
      
      o.resizeTo(new Dimension(200,200));

      g.setDialog("test text");
      g.showNow(5);

   }


   @Test
   public void testGuideComponent() {

      SikuliGuide g = new SikuliGuide();

      Region r = new Region(400,250,200,300);

      SikuliGuideComponent o = new SikuliGuideComponent();
      o.setBounds(r.getRect());
      o.setForeground(Color.red);
      g.addToFront(o);

      SikuliGuideComponent top = new SikuliGuideComponent();
      top.setSize(new Dimension(100,100));
      top.setLocationRelativeToRegion(r, Layout.TOP);
      g.addToFront(top);

      SikuliGuideComponent bottom = new SikuliGuideComponent();
      bottom.setSize(new Dimension(100,100));
      bottom.setLocationRelativeToRegion(r, Layout.BOTTOM);
      g.addToFront(bottom);

      SikuliGuideComponent left = new SikuliGuideComponent();
      left.setSize(new Dimension(100,100));
      left.setLocationRelativeToRegion(r, Layout.LEFT);
      g.addToFront(left);

      SikuliGuideComponent right = new SikuliGuideComponent();
      right.setSize(new Dimension(100,100));
      right.setLocationRelativeToRegion(r, Layout.RIGHT);
      g.addToFront(right);


      g.showNow(5);

   }

   //   @Test
   //   public void testFlag(){
   //
   //      SikuliGuide guide = new SikuliGuide();
   //      Flag flag;
   //      flag = new Flag(new Location(400,400),"Click here");
   //      guide.addComponent(flag);
   //      flag = new Flag(new Location(800,400),"or click here");
   //      guide.addComponent(flag);
   //      
   //
   //
   //      flag = new Flag(new Location(800,400));
   //      flag.setDirection(Flag.DIRECTION_WEST);
   //      guide.addComponent(flag);
   //
   //      
   //      flag = new Flag(new Location(20,10),"Click here");
   //      guide.addComponent(flag);
   //
   //
   //      //      guide.addDialog("Animated flag");
   //      guide.showNow(2);      
   //   }

   @Test
   public void testBeam(){

      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(100,100,100,30);
      
      
      //SikuliGuideAnchor a = new SikuliGuideAnchor(new Pattern("play.png"));
      
      SikuliGuideComponent comp = g.addBeam(r);
      
      SikuliGuideFlag f = new SikuliGuideFlag("Click here");
      f.setLocationRelativeToComponent(comp, Layout.LEFT);
      
      g.addToFront(f);            
      g.showNow();
      
      r = new Region(500,500,20,20);
      
      comp = g.addBeam(r);
      
      f = new SikuliGuideFlag("Click here");
      f.setLocationRelativeToComponent(comp, Layout.LEFT);
      g.addToFront(f);      
      g.addBeam(r);
      g.showNow();


      Debug.log("Done");

   }

   @Test
   public void testSearch(){
      //      JFrame frame = new JFrame();
      //      frame.setVisible(true);
      //      
      SikuliGuide guide = new SikuliGuide();

      guide.addSearchDialog();
      guide.addSearchEntry("run", new Region(20,20,50,50));
      guide.addSearchEntry("debug", new Region(150,120,100,100));
      guide.addSearchEntry("desk", new Region(550,120,100,100));
      guide.addSearchEntry("running", new Region(300,40,100,150));

      //      synchronized(this){
      //         try {
      //            wait();
      //         } catch (InterruptedException e) {
      //            e.printStackTrace();
      //         }
      //      }



      // guide.addHighlight(new Region(50,20,20,20));
      //guide.addHighlight(new Region(100,20,20,20));
      guide.setVisible(true);
      Debug.log(guide.showNow(20));


   }

   @Test
   public void testImage() throws IOException{

      SikuliGuide g = new SikuliGuide();

      Region r = new Region(400,300,200,100);

      SikuliGuideRectangle o = new SikuliGuideRectangle(r);
      o.setForeground(Color.red);
      g.addToFront(o);

      SikuliGuideImage img = new SikuliGuideImage( "tools.png");
      img.setLocationRelativeToRegion(r, Layout.TOP);
      g.addToFront(img);

      img = new SikuliGuideImage( "tools.png");
      img.setScale(1.5f);
      img.setLocationRelativeToRegion(r, Layout.BOTTOM);
      g.addToFront(img);

      img = new SikuliGuideImage( "tools.png");
      img.setLocationRelativeToRegion(r, Layout.RIGHT);
      //img.setEntranceAnimation(img.createCirclingAnimator(10));
      g.addToFront(img);

      img = new SikuliGuideImage( "tools.png");
      img.setLocationRelativeToRegion(r, Layout.LEFT);
      img.setEntranceAnimation(img.createSlidingAnimator(-20,0));
      g.addToFront(img);

      g.showNow(5);      

   }

   @Test
   public void testMagnifier(){

      SikuliGuide g = new SikuliGuide();
      g.addMagnifier(new Region(200,100,100,100));
      g.showNow(5);   
   }

   @Test
   public void testPortal(){

      SikuliGuide g = new SikuliGuide();
      Portal tb = new Portal(g);


      Region r1 = new Region(100,100,50,50);
      Region r2 = new Region(400,400,50,50);
      Region r3 = new Region(100,400,60,70);
      Region r4 = new Region(300,100,60,70);
      Region r5 = new Region(500,100,60,70);

      tb.addEntry("first", r1);
      tb.addEntry("second", r2);
      tb.addEntry("third", r3);
      tb.addEntry("fourth", r4);
      tb.addEntry("fifth", r5);

      //      g.addCircle(r1);
      //      g.addCircle(r2);
      //      g.addCircle(r3);

      g.addTransition(tb);
      g.showNow();
   }
   @Test
   public void testTransition() {
      SikuliGuide g = new SikuliGuide();


      TimeoutTransition tt = new TimeoutTransition(2000);
      g.addToFront(new SikuliGuideText("Timeout transition for 2 seconds"));
      g.addTransition(tt);
      g.showNow();

      g.addToFront(new SikuliGuideText("Default transition for 5 seconds"));

      SikuliGuideFlag flag = new SikuliGuideFlag("Flag");
      flag.setLocationRelativeToRegion(new Region(200,200,100,100), Layout.LEFT);     
      g.addToFront(flag);
      g.showNow();

   }

   @Test
   public void testDialog() {


      SikuliGuide g = new SikuliGuide();


      String cmd;
      TransitionDialog d;
      //      TransitionDialog d = new TransitionDialog();      
      //      d.setTitle("Step 1: Testing");
      //      d.setText("This is some plain text.");
      //      d.setLocation(100,100);
      //      d.setVisible(true);
      //      d.pack();
      //           
      //      d = new TransitionDialog();      
      //      d.setTitle("Default screen center");
      //      d.setText("This dialog should be in the center of the screen.");
      //      d.pack();      
      //      d.setLocationRelativeTo(null);
      //      d.setVisible(true);
      //      
      //      d = new TransitionDialog();      
      //      d.setText("This dialog should not have a title.");
      //      d.setLocation(300,300);
      //      d.setVisible(true);
      //      d.pack();
      //      
      //      d = new TransitionDialog();      
      //      d.setLocation(200,200);
      //      d.setTitle("Small");
      //      d.setText("Small");
      //      d.setVisible(true);
      //      
      //      d = new TransitionDialog();      
      //      d.setLocation(500,100);
      //      d.setTitle("Testing long text");
      //      d.setText("This is some plain text with really long text that is supposed to be wrapped" +
      //      		"and confined to the maximum width of 300 pixels.");
      //      d.setVisible(true);
      //      g.setDialog(d);
      //      Debug.log("cmd=" + g.showNow());


      d = new TransitionDialog();      
      d.setTitle("Custom buttons");
      d.setText("Custom buttons are added. It should be centered. It should timeout after 2 seconds.");
      d.addButton("Yes");
      d.addButton("No");
      d.addButton("Cancel");
      d.addButton("Next");
      d.addButton("Close");
      d.setTimeout(2000);
      d.pack();
      d.setLocationRelativeTo(null);

      g.addTransition(d);
      Debug.log("cmd=" + g.showNow());

      d = new TransitionDialog();      
      d.setText("This dialog should not have a title.");
      d.setLocation(300,300);

      g.addTransition(d);
      Debug.log("cmd=" + g.showNow());

      // a dialog with little text
      // positioning based on user's preferred location
      d = new TransitionDialog();     
      d.setTitle("Small");
      d.setText("Small");
      d.setLocationToUserPreferredLocation();
      d.setTimeout(1000);

      g.addTransition(d);
      Debug.log("cmd=" + g.showNow());

      d = new TransitionDialog();      
      d.setTitle("Default screen center");
      d.setText("This dialog should be in the center of the screen.");
      d.setLocationRelativeTo(null);

      g.addTransition(d);
      Debug.log("cmd=" + g.showNow());

      
      //      cmd = g.showNow();
      //      Debug.log("cmd=", cmd);
      //      
      //      
      //
      //      
      //      g.addText(new Location(50,50),"Step 1");
      //      cmd = g.showNowWithDialog(SikuliGuide.FIRST);
      //      Debug.log("cmd=" + cmd);
      //
      //      g.addText(new Location(50,50),"Step 2");
      //      cmd = g.showNowWithDialog(SikuliGuide.MIDDLE);
      //      Debug.log("cmd=" + cmd);
      //
      //      g.addText(new Location(50,50),"Step 3");
      //      cmd = g.showNowWithDialog(SikuliGuide.LAST);
      //      Debug.log("cmd=" + cmd);

   }

   @Test
   public void testICDLSimpleSearch() throws FindFailed{
      //      App a = new App("Chrome");
      //      //a.focus();
      //
      //      Screen s = new Screen();
      //      Region r;
      //      SikuliGuide sa = new SikuliGuide();
      //
      //      Location o = s.getTopLeft();
      //      o.translate(10,10);
      //      sa.addText(o,"1. All the different categories we see in the " +
      //            "Simple Search are like the shelves in a regular library. Today, " +
      //            "we are looking for Fairy Tales, so, we are going to look for " +
      //      "them by clicking on the Fairy Tales button!");
      //
      //
      //      r = s.find(new Pattern("fairy.png").similar(0.95f));
      //      //sa.addClickTarget(r,"");
      //      //sa.addCircle(r);
      //      sa.showNow();
      //
      //
      //      sa.addText(o,"2. If we want to refine our search further, we can select other categories as well. " +
      //            "If we only want Fairy Tales that are for ages three to five, we can select " +
      //            "the Three to Five button as well. To remove a category from the search," +
      //      " click it again to unselect it.");
      //      r = s.find(new Pattern("three2five.png").similar(0.95f));
      //      //sa.addClickTarget(r,"");
      //      //sa.addCircle(r);
      //      sa.showNow();
      //
      //      Robot robot=null;
      //      try {
      //         robot = new Robot();
      //      } catch (AWTException e) {
      //         e.printStackTrace();
      //      }
      //      robot.delay(2000);
      //
      //      sa.setDialog("3. Now we can see all the Fairy Tale books for age Three to Five in the library. " +
      //            "We can use the arrows in the results section to page through " +
      //      "all the different books. ");
      //      r = s.find(new Pattern("right.png").similar(0.95f));
      //      //sa.addCircle(r);
      //      r = s.find(new Pattern("left.png").similar(0.95f));
      //      //sa.addCircle(r);
      //      sa.showNow();
      //
      //
      //      sa.setDialog("4. To start over with and do a new search, " +
      //            "we can click the Trash Can button. To learn how to read a book" +
      //      " go to the reading books section.");
      //      r = s.find(new Pattern("trashcan.png").similar(0.95f));
      //      //sa.addCircle(r);
      //      sa.showNow();
   }



   @Test
   public void testICDL() throws FindFailed{

      //      //App a = new App("Firefox");
      //      //a.focus();
      //
      //      Region r = null;
      //      Screen s = new Screen();
      //      SikuliGuide sa = new SikuliGuide(s);
      //
      //      sa.setDialog("Welcome!");  
      //      sa.showNow();
      //
      //      r = s.find("tiger.png");
      //
      //      Location o = r.getTopLeft().above(100);
      //
      //      sa.addText(o,"Click on the Tiger or the Unicorn");
      //      //sa.addClickTarget(r,"Tiger");
      //
      //      //sa.addClickTarget(s.find("unicorn.png"),"Unicorn");
      //      sa.showNow();
      //
      //
      //      sa.addText(o, "You just clicked on the " + sa.getLastClickedTarget().getName());
      //      sa.showNow();

   }
   
   @Test
   public void testHotspot() throws FindFailed{


      SikuliGuide g = new SikuliGuide();

      Screen s = new Screen();
      
      SikuliGuideComponent comp = new SikuliGuideCallout("Run the program");
      comp.setShadow(10,2);
      
      Match m = s.find("junit_toolbar.png");
      Hotspot hotspot2 = new Hotspot(m, comp, g);
      g.addToFront(hotspot2);
      
      m = s.find("new.png");

      SikuliGuideCallout txt = new SikuliGuideCallout("Create a new project, source file, folder ... etc.");
      txt.setBackground(Color.yellow);
      Hotspot hotspot1 = new Hotspot(m,txt,g);
      
      g.addToFront(hotspot1);

      


//      c = new Hotspot(new Region(300,100,50,50), new SikuliGuideCallout("Another spot"),g);
//      g.addComponent(c);
      
      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,600);
      g.addToFront(btn);

      //SikuliGuideFlag flag = new SikuliGuideFlag( "Click Here");
      //flag.setLocationRelativeToRegion(m, Layout.RIGHT);
      //g.addComponent(flag);      

      Debug.info("clicked :" + g.showNow());
   }
   
   @Test
   public void testSteps() {

      SikuliGuide g = new SikuliGuide();
   
      
      SikuliGuideAnchor a = new SikuliGuideAnchor(new Pattern("new.png"));            
      SikuliGuideFlag t = new SikuliGuideFlag("new");
      t.setShadowDefault();
      t.setLocationRelativeToComponent(a, Layout.BOTTOM);
      
      g.addToFront(a);
      g.addToFront(t);  
            
      SikuliGuideButton btn = new SikuliGuideButton("Next");
      btn.setShadowDefault();
      btn.setActualLocation(10,600);
      g.addToFront(btn);

      g.showNow(5);

      a = new SikuliGuideAnchor(new Pattern("printer.png"));            
      t = new SikuliGuideFlag("printer");
      t.setShadowDefault();      
      t.setLocationRelativeToComponent(a, Layout.BOTTOM);
      
      Clickable c = new Clickable();
      c.setName("printer");
      c.setLocationRelativeToComponent(a, Layout.OVER);

      btn.setActualLocation(100,600);
      
      g.addToFront(a);
      g.addToFront(t);  
      g.addToFront(c);
      g.addToFront(btn);
      
      g.showNow(5);

      a = new SikuliGuideAnchor(new Pattern("play.png"));            
      t = new SikuliGuideFlag("play");
      t.setShadowDefault();      
      t.setLocationRelativeToComponent(a, Layout.BOTTOM);
      
      btn.setActualLocation(300,600);
      
      g.addToFront(a);
      g.addToFront(t);  
      g.addToFront(btn);

      g.showNow(5);
      
   }
   

   @Test
   public void testClickable() throws FindFailed{


      SikuliGuide g = new SikuliGuide();

      Screen s = new Screen();
      Match m = s.find("new.png");

      Clickable c = new Clickable(m);
      c.setName("new");
      g.addToFront(c);

      SikuliGuideFlag flag = new SikuliGuideFlag( "Click Here");
      flag.setLocationRelativeToRegion(m, Layout.RIGHT);
      g.addToFront(flag);      

      c = new Clickable(new Region(200,200,50,50));
      c.setName("2");
      g.addToFront(c);


      //Debug.info("clicked :" + g.showNow());

      c = new Clickable(new Region(300,200,50,50));
      c.setName("Continue");
      g.addToFront(c);
      
      c = new SikuliGuideButton("Continue");
      c.setLocationRelativeToPoint(new Screen().getCenter(), Layout.CENTER);
      g.addToFront(c);

      Debug.info("clicked :" + g.showNow());

   }

   @Test
   public void testBubble(){


      SikuliGuide g = new SikuliGuide();
      Region t = new Region(100,100,50,50);
      Bubble b = new Bubble(g);

      b.addTarget(new Region(100,100,50,50));
      b.addTarget(new Region(800,120,50,50));
      b.addTarget(new Region(700,120,50,50));

      g.addTransition(b);
      g.showNow();

   }


   @Test
   public void testGUIPath() throws IOException, FindFailed{


      SikuliGuide g = new SikuliGuide();

      ArrayList<GUINode> path = new ArrayList<GUINode>();

      path.add(new GUINode(new Pattern("play.png")));      
      path.add(new GUINode(new Pattern("ppt_show.png")));
      path.add(new GUINode(new Pattern("ppt_slideshow.png")));


      GUINode head = path.get(0);
      //SikuliGuideImage img = new SikuliGuideImage(head.getPattern().getFilename());
      //g.addComponent(img);

      Screen s = new Screen();
      Match m = s.find(head.getPattern());


      SikuliGuideRectangle match = new SikuliGuideRectangle(m);
      g.addToFront(match);

      int ox = m.x;
      int oy = m.y;

      SikuliGuideComponent previous = match;

      for (int i = 1; i < path.size(); ++i){


         ox += previous.getWidth();
         oy += previous.getHeight();

         // add spacing between nodes
         ox += 5;
         oy += 10;        

         GUINode node = path.get(i);
         SikuliGuideImage current = new SikuliGuideImage(node.getPattern().getFilename());
         current.setLocation(ox,oy);
         g.addToFront(current);


         Rectangle r1 = previous.getBounds();
         Rectangle r2 = current.getBounds();


         // draw an elbow between the two
         Point p1 = new Point(r1.x + r1.width/2, r1.y + r1.height/2); // center bottom        
         Point p2 = new Point(r2.x, r2.y + r2.height/2); // left middle

         // give some margin between the arrow head and the pointed image
         p2.x -= 5;

         SikuliGuideArrow arrow = new SikuliGuideArrow( p1, p2);
         arrow.setStyle(SikuliGuideArrow.ELBOW_Y);
         g.addToFront(arrow);



         previous = current;

      }


      g.showNow(3);

   }
   @Test
   public void testMagnet() throws FindFailed, IOException{

      
//      Screen s = new Screen();
      
//      Pattern p = new Pattern("chrome_wrench.png");
//      Match m = s.find(p);
      
      SikuliGuide g = new SikuliGuide();
      
      Magnet m = new Magnet(g);
      m.addTarget(new Pattern("printer.png"));
      m.addTarget(new Pattern("play.png"));
      
      g.addTransition(m);
      g.showNow();
   }
   
   
   @Test
   public void testMagnet1() throws FindFailed, IOException{
      
      Screen s = new Screen();
      
      Pattern p = new Pattern("chrome_wrench.png");
      Match m = s.find(p);

      
      Pattern p1 = new Pattern("chrome_home.png");
      Match m1 = s.find(p1);

      SikuliGuide g = new SikuliGuide();
      
      final SikuliGuideImage img = new SikuliGuideImage("chrome_wrench.png");
      img.setLocationRelativeToRegion(m, Layout.OVER);
            
      final SikuliGuideImage img1 = new SikuliGuideImage("chrome_home.png");
      img1.setLocationRelativeToRegion(m1, Layout.OVER);
      
      SikuliGuideCallout c1 = new SikuliGuideCallout("Settings");
      c1.setLocationRelativeToComponent(img, Layout.TOP);
      c1.setShadow(10,2);
      
      SikuliGuideCallout c2 = new SikuliGuideCallout("Home");
      c2.setLocationRelativeToComponent(img1, Layout.TOP);
      c2.setShadow(10,2);

      
      //final SikuliGuideRectangle r = new SikuliGuideRectangle(null);
      //r.setLocationRelativeToRegion(m, Layout.OVER);
      //r.setShadow(5,2);
 
      
      Dimension targetSize = new Dimension(p.getImage().getWidth()*2, p.getImage().getHeight()*2);
      //img.resizeTo(targetSize);
      img.popup();
      img1.popup();
      
      img.addAnimationListener(new AnimationListener(){

         @Override
         public void animationCompleted() {
            Location loc = Env.getMouseLocation();
            loc.x += 25;
            loc.y -= 15;

            img.moveTo(loc);            
         }
         
      });
      
      img1.addAnimationListener(new AnimationListener(){

         @Override
         public void animationCompleted() {
            Location loc = Env.getMouseLocation();
            loc.x -= 50;
            loc.y -= 15;

            img1.moveTo(loc);            
         }
         
      });
      
      //img.moveTo(loc);      
      g.addToFront(c1);
      g.addToFront(c2);
      g.addToFront(img);
      g.addToFront(img1);

      //g.addComponent(r);
      g.showNow();
      

   }

   @Test
   public void testBlob() throws FindFailed{
      Screen s = new Screen();
      s.find("play.png");

      SikuliGuide g = new SikuliGuide();
      BlobWindow bw = new BlobWindow(g);

      Region r = new Region(10,10,100,100);
      g.addToFront(new SikuliGuideRectangle(r));

      bw.addClickableRegion(r);

      g.addTransition(bw);
      g.showNow();

      //      Screen s = new Screen();
      //      ScreenImage img = s.capture(0,0,500,500);
      //      
      //      SikuliGuide g = new SikuliGuide();
      //      
      //      s.find("play.png");
      //      
      //      Mat m = OpenCV.convertBufferedImageToMat(img.getImage());
      //      FindResults results = Vision.findBlobs(m);
      //      Debug.info("find " + results.size() + " blobs.");
      //      
      //      for (int i=0; i < results.size()-1; ++i){
      //         FindResult result = results.get(i);
      //         
      //         Region r = new Region(result.getX(),result.getY(),result.getW(),result.getH());
      //         g.addComponent(new SikuliGuideRectangle(r));         
      //      }
      //         
      //         
      //      g.setDialog("blobs");
      //      g.showNow();

   }
   
   
   @Test
   public void testAreaRibbon() throws FindFailed{
      
      
      SikuliGuide g = new SikuliGuide();
      
      Screen s = new Screen();
      

      SikuliGuideArea area = new SikuliGuideArea();

      SikuliGuideAnchor a;
      
      
      Pattern p1 = new Pattern("ribbon1.png");
      Pattern p2 = new Pattern("ribbon2.png");
      Pattern p3 = new Pattern("ribbon3.png");
      
      Match m1 = s.find(p1);
      Match m2 = s.find(p2);
      Match m3 = s.find(p3);
      
      SikuliGuideAnchor a1 = new SikuliGuideAnchor(m1);    
      a1.setEditable(true);
      area.addLandmark(a1);

      SikuliGuideAnchor a2 = new SikuliGuideAnchor(m2);    
      a2.setEditable(true);
      area.addLandmark(a2);
            
      SikuliGuideAnchor a3 = new SikuliGuideAnchor(m3);    
      a3.setEditable(true);
      area.addLandmark(a3);
      
      
      g.addTracker(p1, m1, a1);
      g.addTracker(p2, m2, a2);
      g.addTracker(p3, m3, a3);
      

      SikuliGuideSpotlight spotlight = new SikuliGuideSpotlight(null);
      spotlight.setLocationRelativeToComponent(area, Layout.OVER);

      g.addToFront(spotlight);
      g.addToFront(area);
      g.addToFront(a1);
      g.addToFront(a2);
      g.addToFront(a3);


      g.setDialog("Test area");
      
      g.startTracking();      
      g.showNow();

   }
   
   @Test
   public void testButtons() throws FindFailed {
      SikuliGuide g = new SikuliGuide();
      
      SikuliGuideText t = new SikuliGuideText("Step 2: Open a dataset");
      t.setShadow(10,2);
      t.setActualLocation(500,150);
      g.addToFront(t);

      Screen s = new Screen();
      Match m = s.find("lifeflow_open.png");
      
      SikuliGuideFlag co = new SikuliGuideFlag("Click here");
      co.setShadow(10,2);
      co.setLocationRelativeToRegion(m, Layout.LEFT);
      g.addToFront(t);

      
      SikuliGuideButton bd = new SikuliGuideButton("Do it for me");
      bd.setLocationRelativeToRegion(m, Layout.BOTTOM);
      g.addToFront(bd);

      
      g.addToFront(co);

      
      SikuliGuideButton b1 = new SikuliGuideButton("Previous");
      b1.setMargin(10,10,10,10);
      b1.setLocationRelativeToComponent(t,Layout.RIGHT);
      g.addToFront(b1);

      SikuliGuideButton b2 = new SikuliGuideButton("Next");
      b2.setMargin(0,5,0,5);
      b2.setLocationRelativeToComponent(b1, Layout.RIGHT);
      g.addToFront(b2);

      SikuliGuideButton b3 = new SikuliGuideButton("Quit");
      b3.setMargin(0,100,0,50);
      b3.setLocationRelativeToComponent(b2, Layout.RIGHT);
      g.addToFront(b3);

      //g.setDialog("Test area");
      
      g.startTracking();      
      g.showNow();

   }
   
   @Test
   public void testFacebook() throws FindFailed, IOException {

      SikuliGuide g = new SikuliGuide();

      Pattern p = new Pattern("facebookpassword.png");
      
      SikuliGuideAnchor a = new SikuliGuideAnchor();
      a.setActualSize(p.getImage().getWidth(), p.getImage().getHeight());      
      //a.setEditable(true);
      
      SikuliGuideCircle c = new SikuliGuideCircle(null);
      c.setLocationRelativeToComponent(a, Layout.OVER);
      c.setShadow(10,2);
      
      g.addTracker(p, null, a);
      
      g.addToFront(a);
      g.addToFront(c);
      
      TransitionDialog dialog = new TransitionDialog();
      dialog.addButton("Next");
      dialog.setText("Enter Password Here");     
      
      g.addTransition(dialog);
      
      g.startTracking();
      g.showNow();

   }
   
   @Test
   public void testAreaFinder() throws FindFailed{
      SikuliGuide g = new SikuliGuide();
      
      Screen s = new Screen();
      

      SikuliGuideArea varea = new SikuliGuideArea();
      SikuliGuideArea harea = new SikuliGuideArea();
      
      varea.setMode(SikuliGuideArea.VERTICAL);
      harea.setMode(SikuliGuideArea.HORIZONTAL);

      Pattern p1 = new Pattern("finder1.png");
      Pattern p2 = new Pattern("finder2.png");
      Pattern p3 = new Pattern("finder3.png");
      Pattern p4 = new Pattern("finder4.png");
      
      Match m1 = s.find(p1);
      Match m2 = s.find(p2);
      Match m3 = s.find(p3);
      Match m4 = s.find(p4);
      
      SikuliGuideAnchor a1 = new SikuliGuideAnchor(m1);    
      a1.setEditable(true);
      varea.addLandmark(a1);

      SikuliGuideAnchor a2 = new SikuliGuideAnchor(m2);    
      a2.setEditable(true);
      harea.addLandmark(a2);
            
      SikuliGuideAnchor a3 = new SikuliGuideAnchor(m3);    
      a3.setEditable(true);
      varea.addLandmark(a3);
      
      SikuliGuideAnchor a4 = new SikuliGuideAnchor(m4);    
      a4.setEditable(true);
      harea.addLandmark(a4);
      
      g.addTracker(p1, m1, a1);
      g.addTracker(p2, m2, a2);
      g.addTracker(p3, m3, a3);
      g.addTracker(p4, m4, a4);
      
       SikuliGuideArea intersection = new SikuliGuideArea();
       intersection.setRelationship(SikuliGuideArea.INTERSECTION);
       intersection.addLandmark(varea);
       intersection.addLandmark(harea);
      

      SikuliGuideSpotlight spotlight = new SikuliGuideSpotlight(null);
      spotlight.setLocationRelativeToComponent(intersection, Layout.OVER);

      g.addToFront(spotlight);
      g.addToFront(varea);
      g.addToFront(harea);
      g.addToFront(intersection);
      g.addToFront(a1);
      g.addToFront(a2);
      g.addToFront(a3);
      g.addToFront(a4);


      SikuliGuideButton btn = new SikuliGuideButton("Next");
      btn.setActualLocation(10,10);
      g.addToFront(btn);
      
      //g.setDialog("Test area");
      
      g.startTracking();      
      g.showNow();
   }
   
   @Test
   public void testArrow() throws FindFailed{
      
      
      SikuliGuide g = new SikuliGuide();
      
      Pattern p1 = new Pattern("chrome_wrench.png");
      Pattern p2 = new Pattern("chrome_toolbar.png");
      
      SikuliGuideAnchor a1 = new SikuliGuideAnchor(p1);    
      a1.setEditable(true);
      
      SikuliGuideAnchor a2 = new SikuliGuideAnchor(p2);    
      a2.setEditable(true);

      SikuliGuideArrow arrow = new SikuliGuideArrow(a1,a2);
      
      g.addToFront(a1);
      g.addToFront(a2);
      g.addToFront(arrow);
      
      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,50);
      g.addToFront(btn);

      g.showNow();

   }
   
   @Test
   public void testArea() throws FindFailed{
      
      
      SikuliGuide g = new SikuliGuide();
      

      SikuliGuideArea area = new SikuliGuideArea();      
      Pattern p1 = new Pattern("chrome_wrench.png");
      Pattern p2 = new Pattern("chrome_toolbar.png");
      Pattern p3 = new Pattern("chrome_corner.png");
      
      SikuliGuideAnchor a1 = new SikuliGuideAnchor(p1);    
      a1.setEditable(true);
      area.addLandmark(a1);

      SikuliGuideAnchor a2 = new SikuliGuideAnchor(p2);    
      a2.setEditable(true);
      area.addLandmark(a2);
      
      SikuliGuideAnchor a3 = new SikuliGuideAnchor(p3);    
      a3.setEditable(true);
      area.addLandmark(a3);    

      //area.setMode(SikuliGuideArea.HORIZONTAL);
      //area.setMode(SikuliGuideArea.VERTICAL);
      
      SikuliGuideRectangle rect = new SikuliGuideRectangle(null);
      rect.setMargin(3,3,3,3);
      rect.setLocationRelativeToComponent(area, Layout.OVER);
      Debug.info("" + area.isVisible());
      
      g.addToFront(area); 
      g.addToFront(a1);
      g.addToFront(a2);
      g.addToFront(a3);

      g.addToFront(rect);

      

      
//      SikuliGuideText t = new SikuliGuideText("Text");
//      t.setMargin(10,10,10,10);
//      t.setLocationRelativeToComponent(area, Layout.RIGHT);
//      
//      SikuliGuideText t1 = new SikuliGuideText("Third");
//      t1.setLocationRelativeToComponent(area, 0.33f, 0.33f);
//
//      
// 
//      g.addComponent(t);
//      g.addComponent(t1);
//      
//      SikuliGuideArea staticArea = new SikuliGuideArea();
//      staticArea.addLandmark(new SikuliGuideRectangle(new Region(25,100,800,200)));
//      g.addComponent(staticArea);      
//      
//      SikuliGuideArea intersection = new SikuliGuideArea();
//      intersection.setRelationship(SikuliGuideArea.INTERSECTION);
//      intersection.addLandmark(area);
//      intersection.addLandmark(staticArea);
//      
//      SikuliGuideRectangle r = new SikuliGuideRectangle(null);
//      r.setMargin(20,20,20,20);
//      r.setOffset(10,10);
//      r.setLocationRelativeToComponent(intersection, Layout.OVER);
//
//      //g.addComponent(intersection);
//      SikuliGuideCallout c = new SikuliGuideCallout("Intersection");
//      c.setLocationRelativeToComponent(intersection, Layout.RIGHT);
//
//      g.addComponent(r);
//      g.addComponent(c);
      
      SikuliGuideButton btn = new SikuliGuideButton("Close");
      btn.setActualLocation(10,10);
      g.addToFront(btn);

      g.showNow();
      
   }


}


