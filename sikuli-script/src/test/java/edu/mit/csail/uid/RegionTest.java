package edu.mit.csail.uid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.event.InputEvent;
import java.util.Date;
import javax.swing.*;

/**
 * Unit test for simple SikuliScript.
 */
public class RegionTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RegionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RegionTest.class );
    }

    /*
    public void testSpatialOperators() throws Exception{
       GridLayoutDemo f = GridLayoutDemo.createAndShowGUI();
       //Thread.sleep(50000);
       Screen s = new Screen();
       Match b5 = s.find((new Pattern("test-res/button-5.png")).similar(0.98f));
       s.click(b5, 0);
       assertEquals("clicked", f.getText(5));
       s.click(b5.above().find("test-res/button-2.png"), 0);
       s.click(b5.left().find("test-res/button-4.png"), 0);
       s.click(b5.right().find("test-res/button-6.png"), 0);
       s.click(b5.below().find("test-res/button-8.png"), 0);
       f.setVisible(false);
       f.dispose();
    }
    */

    public void testType() throws Exception
    {
       InputsFrame f = new InputsFrame();
       Region r = new Region(0,0,200,200);
       String s = "123!!";
       r.type("test-res/input-y.png", s, 0);
       Thread.sleep(500);
       assertEquals(s, f.getText(1));
       r.type(null, "\b\b\b\b\b", 0);
       Thread.sleep(500);
       assertEquals("", f.getText(1));
    }

    public void testPaste() throws Exception
    {
       InputsFrame f = new InputsFrame();
       Region r = new Region(0,0,200,200);
       String s = "123!!";
       r.paste("test-res/input-y.png", s);
       Thread.sleep(500);
       assertEquals(s, f.getText(1));
    }

    public void testRegion() throws Exception
    {
      JButtons frame = new JButtons();
      Screen scr = new Screen();
      Match ret = scr.find("test-res/cup-btn.png");
      assertNotNull(ret);
      Region region = new Region(10,0,200,200);
      ret = region.find("test-res/cup-btn.png");
      assertNotNull(ret);
      region = new Region(600,0,200,200);
      region.setThrowException(false);
      ret = region.find("test-res/cup-btn.png");
      assertNull(ret);
    }


    public void testClick() throws Exception
    {
      JButtons frame = new JButtons();
      assertEquals(0, frame.getCount()[2]);
      Screen scr = new Screen();
      int ret = scr.click("test-res/cup-btn.png", 0);
      Thread.sleep(500);
      assertTrue(ret==1);
      assertEquals(1, frame.getCount()[2]);
      frame.dispose();
    }

    public void testRegionClickOffset() throws Exception
    {
      JButtons frame = new JButtons();
      assertEquals(0, frame.getCount()[1]);
      assertEquals(0, frame.getCount()[2]);
      Screen scr = new Screen();
      Pattern ptn = new Pattern("test-res/cup-btn.png").targetOffset(-80,2);
      int ret = scr.click(ptn, 0);
      Thread.sleep(500);
      assertEquals(1, frame.getCount()[1]);
      assertEquals(0, frame.getCount()[2]);
      frame.dispose();
    }

    public void testDragDrop() throws Exception {
        JFrame frame = DragListDemo.createAndShowGUI();
        Screen scr = new Screen();
        scr.wait("test-res/item1-list2.png", 3);
        scr.dragDrop("test-res/item2-list1.png", "test-res/item1-list2.png",0);
        assertNotNull(scr.wait("test-res/draglist-result.png",3));
        frame.dispose();
    }

    public void testRegionFind() throws Exception
    {
      Screen scr = new Screen();
      scr.setROI(new Region(0,0,300,300));
      scr.setAutoWaitTimeout(2);
      scr.setThrowException(true);
      long begin = (new Date()).getTime();
      boolean gotFindFailed = false;
      try{
         Match m = scr.find("test-res/google.png");
         System.out.println("match: " + m);
      }
      catch(FindFailed e){
         gotFindFailed = true;
      }
      long end = (new Date()).getTime();
      assertTrue(gotFindFailed);
      assertTrue(end-begin >= 2000);

      scr.setAutoWaitTimeout(0);
      scr.setThrowException(false);
      begin = (new Date()).getTime();
      scr.find("test-res/google.png");
      end = (new Date()).getTime();
      assertTrue(end-begin < 2500);
    }


}

