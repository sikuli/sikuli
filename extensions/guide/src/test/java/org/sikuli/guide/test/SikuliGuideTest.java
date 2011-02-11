package org.sikuli.guide.test;

import java.awt.AWTException;
import java.awt.Robot;

import org.junit.Test;
import org.sikuli.guide.SikuliGuideDialog;
import org.sikuli.guide.SikuliGuide;
import org.sikuli.script.App;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.FindFailedResponse;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.Settings;


public class SikuliGuideTest {
   
   
   @Test
   public void testDialog() {
      
      
      SikuliGuide g = new SikuliGuide();
      
      String cmd;
      
//      g.addDialog("next","Step 1");
//      cmd = g.showNow();
//      Debug.log("cmd=", cmd);
//      
//      g.addDialog("next","Step 2");
//      cmd = g.showNow();
//      Debug.log("cmd=", cmd);
//
//      g.addDialog("next","Step 3");
//      cmd = g.showNow();
//      Debug.log("cmd=", cmd);

       g.addText(new Location(50,50),"Step 1");
       g.showNowWithDialog(SikuliGuide.FIRST);
       
       g.addText(new Location(50,50),"Step 2");
       g.showNowWithDialog(SikuliGuide.MIDDLE);

       g.addText(new Location(50,50),"Step 3");
       g.showNowWithDialog(SikuliGuide.LAST);

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
      App a = new App("Firefox");
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

      sa.addDialog("3. Now we can see all the Fairy Tale books for age Three to Five in the library. " +
            "We can use the arrows in the results section to page through " +
      "all the different books. ");
      r = s.find(new Pattern("right.png").similar(0.95f));
      sa.addCircle(r);
      r = s.find(new Pattern("left.png").similar(0.95f));
      sa.addCircle(r);
      sa.showNow();


      sa.addDialog("4. To start over with and do a new search, " +
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

      sa.addDialog("Another step");
      sa.showNow();

      sa.addDialog("Yet another step");
      sa.showNow();


   }
   
   @Test
   public void testICDL() throws FindFailed{

      //App a = new App("Firefox");
      //a.focus();
      
      Region r = null;
      Screen s = new Screen();
      SikuliGuide sa = new SikuliGuide(s);

      sa.addDialog("Welcome!");  
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


