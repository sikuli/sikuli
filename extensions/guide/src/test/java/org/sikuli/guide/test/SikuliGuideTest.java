package org.sikuli.guide.test;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.junit.Test;
import org.sikuli.guide.ClickTarget;
import org.sikuli.guide.Flag;
import org.sikuli.guide.Bracket;
import org.sikuli.guide.SearchDialog;
import org.sikuli.guide.Beam;
import org.sikuli.guide.NavigationDialog;
import org.sikuli.guide.SikuliGuide;
import org.sikuli.guide.Portal;
import org.sikuli.guide.TreeSearchDialog;
import org.sikuli.guide.SikuliGuide.Side;
import org.sikuli.guide.model.GUIModel;
import org.sikuli.guide.model.GUINode;
import org.sikuli.guide.Spotlight;



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
   

   @Test
   public void testModel() throws FindFailed{

      GUIModel tree = createModel();
      

      ArrayList<GUINode> nodes = tree.getNodesByTag("refresh");
      for (GUINode node : nodes){
         Debug.info("node: " + node);
      }

      GUINode target = nodes.get(0);

      Screen s = new Screen();
      SikuliGuide g = new SikuliGuide();
      
      Match m = target.findOnScreen();
      
      if (m == null){
         m = target.findAncestorOnScreen();
         
         
         s.click(m, 0);
         
         m = s.wait(target.getPattern());
         
         
         g.addCircle(m);
         g.addFlag(m.getTopLeft().below(m.h/2),"    ");
         g.showNow(3);
         
      }
      
      GUINode print = tree.getNodesByTag("print").get(0);
      
      m = print.findOnScreen();
      g.addCircle(m);
      g.addFlag(m.getTopLeft().below(m.h/2),"    ");
      g.showNow(3);
      

      target = tree.getNodesByTag("references").get(0);;
      
      m = target.findAncestorOnScreen();
      
      s.hover(m);
      
      m = s.wait(target.getPattern());
      
      g.addCircle(m);
      g.addFlag(m.getTopLeft().below(m.h/2),"    ");
      g.showNow(3);
      

   }


   @Test
   public void testModelSearch() throws FindFailed{

      GUIModel tree = createChromeModel();
      
      SikuliGuide guide = new SikuliGuide();

      TreeSearchDialog search = new TreeSearchDialog(guide, tree);
      search.setLocationRelativeTo(null);
      search.setAlwaysOnTop(true);
      
      guide.setSearchDialog(search);
      
      guide.setVisible(true);
      Debug.log(guide.showNow(20));
      

   }





   @Test
   public void testTextAlignment() {
      SikuliGuide g = new SikuliGuide();

      Region r = new Region(200,200,100,150);
      //g.addRectangle(r);

      g.addText(r,"TOP", Side.TOP);
      g.addText(r,"BOTTOM", Side.BOTTOM);
      g.addText(r,"LEFT", Side.LEFT);
      g.addText(r,"RIGHT", Side.RIGHT);


      r = new Region(900,200,100,150);
      g.addRectangle(r);

      g.addComponent(new Flag(r.getTopLeft(),"Top Left"));
      g.addComponent(new Flag(r.getBottomLeft(),"Bottom Left"));

      Flag b = new Flag(r.getTopRight(),"Top Right");
      b.setDirection(Flag.DIRECTION_WEST);
      g.addComponent(b);

      b = new Flag(r.getBottomRight(),"Bottom Right");
      b.setDirection(Flag.DIRECTION_WEST);
      g.addComponent(b);

      b = new Flag(r.getCenter().above(r.h/2),"Top Flag");
      b.setDirection(Flag.DIRECTION_SOUTH);
      g.addComponent(b);

      b = new Flag(r.getCenter().below(r.h/2),"Bottom Flag");
      b.setDirection(Flag.DIRECTION_NORTH);
      g.addComponent(b);

      r = new Region(500,200,300,450);
      g.addRectangle(r);

      Bracket bracket = new Bracket(r);
      g.addComponent(bracket);

      bracket = new Bracket(r);
      bracket.setSide(Bracket.SIDE_RIGHT);
      g.addComponent(bracket);

      bracket = new Bracket(r);
      bracket.setSide(Bracket.SIDE_TOP);
      g.addComponent(bracket);

      bracket = new Bracket(r);
      bracket.setSide(Bracket.SIDE_BOTTOM);
      g.addComponent(bracket);


      //g.addBookmark(r.getTopLeft(), "Hello");

      //      g.addDialog("This is a test dialog with really long message. hahaha. This is a " +
      //      		"test dialog with really long message ");

      String dialogmsg = "This is a test dialog with really long message. hahaha. This is a " +
      "test dialog with really long message ";
      NavigationDialog dialog = new NavigationDialog(g,dialogmsg, SikuliGuide.SIMPLE);
      dialog.setTitle("This is a test title");
      //dialog.setLocation(new Point(100,100));
      g.setDialog(dialog);

      g.showNow(10);
   }

   @Test
   public void testFlag(){

      SikuliGuide guide = new SikuliGuide();
      Flag flag;
      flag = new Flag(new Location(400,400),"Click here");
      guide.addComponent(flag);
      flag = new Flag(new Location(800,400),"or click here");
      guide.addComponent(flag);
      


      flag = new Flag(new Location(800,400));
      flag.setDirection(Flag.DIRECTION_WEST);
      guide.addComponent(flag);

      
      flag = new Flag(new Location(20,10),"Click here");
      guide.addComponent(flag);


      //      guide.addDialog("Animated flag");
      guide.showNow(2);      
   }

   @Test
   public void testSpotlight(){

      SikuliGuide guide = new SikuliGuide();


      guide.addSpotlight(new Region(100,100,50,30));
      guide.addSpotlight(new Region(20,20,20,20));
      guide.addSpotlight(new Region(200,200,100,100), Spotlight.CIRCLE);
      guide.addSpotlight(new Region(400,50,100,100), Spotlight.CIRCLE);

      guide.setDialog("Some spotlights");
      guide.showNow(3);          

      guide.addSpotlight(new Region(300,300,50,30));
      guide.addSpotlight(new Region(420,420,20,20));
      guide.showNow(3);
   }


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
   public void testImage(){

      SikuliGuide g = new SikuliGuide();
      g.addImage(new Location(10,10),"tools.png");
      g.addImage(new Location(100,10),"tools.png",2.0f);
      g.addImage(new Location(200,10),"tools.png",3.0f);
      g.showNow();      

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
   public void testFirefox() throws FindFailed {

      App a = new App("Firefox");

      // a.focus();
      Debug.log("t");
      //
      //      a.focus();
      //      Debug.log("t");
      //
      //      Region s = a.window(0);

      Screen s = new Screen();

      Debug.log("s=" + s);
      //      s.getCenter();
      //      s.setFindFailedResponse(FindFailedResponse.PROMPT);
      //
      //
      //      Settings.ShowActions = true;

      Region r = null;

      //      s.click("tools.png",0);

      SikuliGuide sa = new SikuliGuide();

      r = s.find("tools.png");
      //sa.addHighlight(r);
      sa.addCircle(r);
      sa.showNow();
      // sa.showWaitForButtonClick("Continue", "Tools");

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
      sa.addCircle(r);
      sa.showNow();


      sa.addText(o,"2. If we want to refine our search further, we can select other categories as well. " +
            "If we only want Fairy Tales that are for ages three to five, we can select " +
            "the Three to Five button as well. To remove a category from the search," +
      " click it again to unselect it.");
      r = s.find(new Pattern("three2five.png").similar(0.95f));
      sa.addClickTarget(r,"");
      sa.addCircle(r);
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
      sa.addCircle(r);
      r = s.find(new Pattern("left.png").similar(0.95f));
      sa.addCircle(r);
      sa.showNow();


      sa.setDialog("4. To start over with and do a new search, " +
            "we can click the Trash Can button. To learn how to read a book" +
      " go to the reading books section.");
      r = s.find(new Pattern("trashcan.png").similar(0.95f));
      sa.addCircle(r);
      sa.showNow();
   }

   @Test
   public void testMute() throws FindFailed{
      //App a = new App("System Preferences");
      //a.focus();

      Region s = new Screen(0);

      Debug.log("s=" + s);
      s.getCenter();

      s.setFindFailedResponse(FindFailedResponse.PROMPT);


      Region r = null;

      SikuliGuide sa = new SikuliGuide(s);
      r = s.find("sound.png");
      sa.addText(r.getBottomLeft().below(5),"Click this");
      sa.addRectangle(r);
      sa.addClickTarget(r, "");
      //sa.addDialog("Next", "Hello");
      sa.showNow();

      sa.setDialog("Another step");
      sa.showNow();

      sa.setDialog("Yet another step");
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





}


