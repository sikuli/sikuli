package org.sikuli.guide.test;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.junit.Test;
import org.sikuli.guide.SikuliGuideComponent;
import org.sikuli.guide.Bubble;
import org.sikuli.guide.ClickTarget;
import org.sikuli.guide.Flag;
import org.sikuli.guide.Bracket;
import org.sikuli.guide.SearchDialog;
import org.sikuli.guide.Beam;
import org.sikuli.guide.NavigationDialog;
import org.sikuli.guide.SikuliGuide;
import org.sikuli.guide.Portal;
import org.sikuli.guide.SikuliGuideArrow;
import org.sikuli.guide.SikuliGuideBracket;
import org.sikuli.guide.SikuliGuideCircle;
import org.sikuli.guide.SikuliGuideFlag;
import org.sikuli.guide.SikuliGuideImage;
import org.sikuli.guide.SikuliGuideRectangle;
import org.sikuli.guide.SikuliGuideSpotlight;
import org.sikuli.guide.SikuliGuideText;
import org.sikuli.guide.Tracker;
import org.sikuli.guide.TreeSearchDialog;
import org.sikuli.guide.SikuliGuide.Side;
import org.sikuli.guide.model.GUIModel;
import org.sikuli.guide.model.GUINode;

import org.sikuli.script.App;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.FindFailedResponse;
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

   @Test
   public void testAnimation(){
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,50,50);
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);


      SikuliGuideText t = new SikuliGuideText(g,"Sliding right");
      t.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(t);
      t.setEntranceAnimation(t.createSlidingAnimator(-20,0));
      
      SikuliGuideFlag f = new SikuliGuideFlag(g,"Sliding left");
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      g.addComponent(f);
      
      t = new SikuliGuideText(g,"Sliding down");
      t.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(t);
      t.setEntranceAnimation(t.createSlidingAnimator(0,-20));

      f = new SikuliGuideFlag(g,"Sliding up");
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(f);
      
      
      r = new Region(600,200,50,50);
      o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.blue);
      g.addComponent(o);

      t = new SikuliGuideText(g,"Circling");
      t.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(t);
      t.setEntranceAnimation(t.createCirclingAnimator(10));

      
      
      g.setDialog("Test");
      g.showNow(5);
      

   }
   
   
   @Test
   public void testSimpleFlag() {
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,50,50);
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);
      
      SikuliGuideFlag f = new SikuliGuideFlag(g,"LEFT");
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(f);

      
      f = new SikuliGuideFlag(g,"RIGHT");
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
     // f.setDirection(SimpleFlag.DIRECTION_WEST);
      g.addComponent(f);

      f = new SikuliGuideFlag(g,"TOP");
      //f.setDirection(SimpleFlag.DIRECTION_SOUTH);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(f);
      
      f = new SikuliGuideFlag(g,"BOTTOM");
      //f.setDirection(SimpleFlag.DIRECTION_NORTH);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(f);

      
      g.showNow(5);
   
   }
   
   
   @Test
   public void testSimpleShapes() {
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,300,150);
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);

      //r = new Region(550,550,200,150);
      
      SikuliGuideCircle c = new SikuliGuideCircle(g,r);
      c.setForeground(Color.green);
      g.addComponent(c);
      
      
      Region q = new Region(500,500,200,200);
      o = new SikuliGuideRectangle(g,q);
      o.setForeground(Color.red);
      g.addComponent(o);
      
      SikuliGuideArrow a = new SikuliGuideArrow(g, r.getTopLeft(), q.getTopLeft());
      a.setForeground(Color.blue);
      g.addComponent(a);
      
      g.showNow(5);

   }
   
   @Test
   public void testSimpleBracket() {
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,300,150);
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);
      
      SikuliGuideBracket f = new SikuliGuideBracket(g);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(f);

      f = new SikuliGuideBracket(g);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      g.addComponent(f);

      f = new SikuliGuideBracket(g);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(f);
      
      f = new SikuliGuideBracket(g);
      f.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(f);

      
      g.showNow(5);
   
   }
   
   @Test
   public void testTracker() throws FindFailed{

      SikuliGuide g = new SikuliGuide();

      
      Screen s = new Screen();
      Region r1,r2;
      SikuliGuideComponent c1,c2,c3,c4;
      
//      r1 = s.find("play.png");     
//      c1 = new SikuliGuideRectangle(g,r1); 
//      g.addComponent(c1);      
//
//      g.addTracker("play.png", r1, c1);
//            
//      g.setDialog("Tracking");
//      g.showNow();
      
      
      r2 = s.find("printer.png");
      c2 = new SikuliGuideRectangle(g,r2); 
      g.addComponent(c2);
//        
      c3 = new SikuliGuideText(g,"Printer");
      c3.setLocationRelativeToRegion(r2, SikuliGuideComponent.TOP);
      g.addComponent(c3);

      c4 = new SikuliGuideBracket(g);
      c4.setLocationRelativeToRegion(r2, SikuliGuideComponent.BOTTOM);
      g.addComponent(c4);

      
      ArrayList<SikuliGuideComponent> components = new ArrayList<SikuliGuideComponent>();
      components.add(c2);
      components.add(c3);
      components.add(c4);
      
      g.addTracker("printer.png", r2, components);
      
      g.setDialog("Tracking");
      g.showNow();
   
//   
      r1 = s.find("play.png");     
      c1 = new SikuliGuideSpotlight(g,r1); 
      g.addComponent(c1);      

      g.addTracker("play.png", r1, c1);
            
      g.setDialog("Tracking");
      g.showNow();

   }
   


   @Test
   public void testSimpleSpotlight(){
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,100,100);
      
      SikuliGuideSpotlight o = new SikuliGuideSpotlight(g,r);
      g.addComponent(o);
      
      SikuliGuideText t = new SikuliGuideText(g,"This is a spotlight");
      t.setLocationRelativeToRegion(r, SikuliGuideText.TOP);
      g.addComponent(t);
      
      
      r = new Region(500,500,100,100);
      o = new SikuliGuideSpotlight(g,r);
      o.setShape(SikuliGuideSpotlight.CIRCLE);
      o.setEntranceAnimation(o.createSlidingAnimator(-100, 0));
      g.addComponent(o);

      g.setDialog("Test spotlights");
      g.showNow();  
   }
   
   @Test
   public void testGuideText() {
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(250,250,200,300);
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);
      
      SikuliGuideText top = new SikuliGuideText(g,"This is Top");
      top.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(top);
      

      
      SikuliGuideText bottom = new SikuliGuideText(g,"This is Bottom");
      bottom.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(bottom);

      SikuliGuideText left = new SikuliGuideText(g,"This is Left");
      left.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(left);
      
      SikuliGuideText right = new SikuliGuideText(g,"This is Right");
      right.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      g.addComponent(right);
      
      SikuliGuideText tl = new SikuliGuideText(g,"Right Alignment");
      tl.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      tl.setVerticalAlignmentWithRegion(r, 0f);
      g.addComponent(tl);

      SikuliGuideText bl = new SikuliGuideText(g,"Bottom Alignment");
      bl.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      bl.setVerticalAlignmentWithRegion(r, 1f);
      g.addComponent(bl);
      
      SikuliGuideText ht = new SikuliGuideText(g,"Left Alignment");
      ht.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      ht.setHorizontalAlignmentWithRegion(r, 0f);
      ht.setLocation(ht.getX(),ht.getY()-100);
      g.addComponent(ht);

      SikuliGuideText rb = new SikuliGuideText(g,"Right Alignment");
      rb.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      rb.setHorizontalAlignmentWithRegion(r, 1f);
      rb.setLocation(rb.getX(),rb.getY()+100);
      g.addComponent(rb);

      SikuliGuideText cb = new SikuliGuideText(g,"Center Alignment");
      cb.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      cb.setHorizontalAlignmentWithRegion(r, 0.5f);
      cb.setLocation(cb.getX(),cb.getY()+50);
      g.addComponent(cb);
      
      
      SikuliGuideText qq = new SikuliGuideText(g,"Long text with maximum width set to 300");
      qq.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      qq.setVerticalAlignmentWithRegion(r, 0f);      
      qq.setMaximumWidth(300);
      g.addComponent(qq);
      
      
      
      r = new Region(800,400,100,100);
      o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.green);
      g.addComponent(o);

      
      SikuliGuideText t = new SikuliGuideText(g,"This is a sentence in small font and it is long and supposed to be auto wrapped.");      
      t.setFontSize(10);
      t.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      t.setHorizontalAlignmentWithRegion(r, 0.5f);      
      g.addComponent(t);

      
      g.setDialog("test text");
      g.showNow(5);

   }


   @Test
   public void testGuideComponent() {
      
      SikuliGuide g = new SikuliGuide();
      
      Region r = new Region(400,250,200,300);
      
      SikuliGuideComponent o = new SikuliGuideComponent(g);
      o.setBounds(r.getRect());
      o.setForeground(Color.red);
      g.addComponent(o);
      
      SikuliGuideComponent top = new SikuliGuideComponent(g);
      top.setSize(new Dimension(100,100));
      top.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(top);

      SikuliGuideComponent bottom = new SikuliGuideComponent(g);
      bottom.setSize(new Dimension(100,100));
      bottom.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(bottom);

      SikuliGuideComponent left = new SikuliGuideComponent(g);
      left.setSize(new Dimension(100,100));
      left.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      g.addComponent(left);
      
      SikuliGuideComponent right = new SikuliGuideComponent(g);
      right.setSize(new Dimension(100,100));
      right.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      g.addComponent(right);
      
      
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

      SikuliGuide guide = new SikuliGuide();

      Region r = new Region(100,100,20,20);
      guide.addText(r,"Move here", Side.TOP);
      guide.addBeam(r);
      guide.showNow();

      r = new Region(500,500,20,20);
      guide.addText(r,"Move here", Side.TOP);      
      guide.addBeam(r);
      guide.showNow();


      Debug.log("Yes");

      //sh.run(r, "click here to run");

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
      
      SikuliGuideRectangle o = new SikuliGuideRectangle(g,r);
      o.setForeground(Color.red);
      g.addComponent(o);

      SikuliGuideImage img = new SikuliGuideImage(g, "tools.png");
      img.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
      g.addComponent(img);
      
      img = new SikuliGuideImage(g, "tools.png");
      img.setScale(1.5f);
      img.setLocationRelativeToRegion(r, SikuliGuideComponent.BOTTOM);
      g.addComponent(img);

      img = new SikuliGuideImage(g, "tools.png");
      img.setLocationRelativeToRegion(r, SikuliGuideComponent.RIGHT);
      img.setEntranceAnimation(img.createCirclingAnimator(10));
      g.addComponent(img);
      
      img = new SikuliGuideImage(g, "tools.png");
      img.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);
      img.setEntranceAnimation(img.createSlidingAnimator(-20,0));
      g.addComponent(img);
      
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

      g.setTransition(tb);
      g.showNow();
   }

   @Test
   public void testDialog() {


      SikuliGuide g = new SikuliGuide();


      String cmd;

      g.getDialog().setLocation(100,100);

      
      g.setDialog("Step 1");
      cmd = g.showNow();
      Debug.log("cmd=", cmd);

      g.setDialog("Step 2");
      cmd = g.showNow();
      Debug.log("cmd=", cmd);

      g.setDialog("Step 3");
      cmd = g.showNow();
      Debug.log("cmd=", cmd);
      
      

      
      g.addText(new Location(50,50),"Step 1");
      cmd = g.showNowWithDialog(SikuliGuide.FIRST);
      Debug.log("cmd=" + cmd);

      g.addText(new Location(50,50),"Step 2");
      cmd = g.showNowWithDialog(SikuliGuide.MIDDLE);
      Debug.log("cmd=" + cmd);

      g.addText(new Location(50,50),"Step 3");
      cmd = g.showNowWithDialog(SikuliGuide.LAST);
      Debug.log("cmd=" + cmd);

   }

   @Test
   public void testICDLSimpleSearch() throws FindFailed{
      App a = new App("Chrome");
      //a.focus();

      Screen s = new Screen();
      Region r;
      SikuliGuide sa = new SikuliGuide();

      Location o = s.getTopLeft();
      o.translate(10,10);
      sa.addText(o,"1. All the different categories we see in the " +
            "Simple Search are like the shelves in a regular library. Today, " +
            "we are looking for Fairy Tales, so, we are going to look for " +
      "them by clicking on the Fairy Tales button!");


      r = s.find(new Pattern("fairy.png").similar(0.95f));
      sa.addClickTarget(r,"");
      //sa.addCircle(r);
      sa.showNow();


      sa.addText(o,"2. If we want to refine our search further, we can select other categories as well. " +
            "If we only want Fairy Tales that are for ages three to five, we can select " +
            "the Three to Five button as well. To remove a category from the search," +
      " click it again to unselect it.");
      r = s.find(new Pattern("three2five.png").similar(0.95f));
      sa.addClickTarget(r,"");
      //sa.addCircle(r);
      sa.showNow();

      Robot robot=null;
      try {
         robot = new Robot();
      } catch (AWTException e) {
         e.printStackTrace();
      }
      robot.delay(2000);

      sa.setDialog("3. Now we can see all the Fairy Tale books for age Three to Five in the library. " +
            "We can use the arrows in the results section to page through " +
      "all the different books. ");
      r = s.find(new Pattern("right.png").similar(0.95f));
      //sa.addCircle(r);
      r = s.find(new Pattern("left.png").similar(0.95f));
      //sa.addCircle(r);
      sa.showNow();


      sa.setDialog("4. To start over with and do a new search, " +
            "we can click the Trash Can button. To learn how to read a book" +
      " go to the reading books section.");
      r = s.find(new Pattern("trashcan.png").similar(0.95f));
      //sa.addCircle(r);
      sa.showNow();
   }



   @Test
   public void testICDL() throws FindFailed{

      //App a = new App("Firefox");
      //a.focus();

      Region r = null;
      Screen s = new Screen();
      SikuliGuide sa = new SikuliGuide(s);

      sa.setDialog("Welcome!");  
      sa.showNow();

      r = s.find("tiger.png");

      Location o = r.getTopLeft().above(100);

      sa.addText(o,"Click on the Tiger or the Unicorn");
      sa.addClickTarget(r,"Tiger");

      sa.addClickTarget(s.find("unicorn.png"),"Unicorn");
      sa.showNow();


      sa.addText(o, "You just clicked on the " + sa.getLastClickedTarget().getName());
      sa.showNow();

   }

   
   @Test
   public void testBubble(){
      
      
      SikuliGuide g = new SikuliGuide();
      Region t = new Region(100,100,50,50);
      Bubble b = new Bubble(g);
      
      b.addTarget(new Region(100,100,50,50));
      b.addTarget(new Region(800,120,50,50));
      
      g.setTransition(b);
      g.showNow();
      
   }




}


