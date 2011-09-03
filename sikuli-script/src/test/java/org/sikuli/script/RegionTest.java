/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.util.Date;
import java.util.Iterator;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Unit test for simple SikuliScript.
 */
public class RegionTest implements SikuliEventObserver
{
    @Test
    public void test_click() throws Exception
    {
       JButtons frame = new JButtons();
       assertEquals(0, frame.getCount()[0]);
       assertEquals(0, frame.getCount()[1]);
       assertEquals(0, frame.getCount()[2]);
       Screen scr = new Screen();
       scr.setAutoWaitTimeout(10);
       Settings.MoveMouseDelay = 0;
       Pattern ptn = new Pattern("test-res/network.png");
       int ret = scr.click(ptn, 0);
       Thread.sleep(500);
       assertEquals(0, frame.getCount()[0]);
       assertEquals(0, frame.getCount()[1]);
       assertEquals(1, frame.getCount()[2]);
       ptn = new Pattern("test-res/sound.png");
       ret = scr.click(ptn, 0);
       Thread.sleep(500);
       assertEquals(0, frame.getCount()[0]);
       assertEquals(1, frame.getCount()[1]);
       assertEquals(1, frame.getCount()[2]);
       frame.dispose();
    }


    @Test
    public void testFindInROI() throws Exception {
       JButtons frame = new JButtons();
       Screen scr = new Screen();
       Match m = scr.wait("test-res/network.png", 10);
       scr.setRect(new Rectangle(m.x-9, m.y-10, m.w+10, m.h+11));
       Match m2 = scr.doFind("test-res/network.png");
       assertEquals(m, m2);
       frame.dispose();
    }

    @Ignore("the items in this test needs to be replaced with images instead of text") 
    @Test
    public void testDragDrop() throws Exception {
       JFrame frame = DragListDemo.createAndShowGUI();
       Screen scr = new Screen();
       Settings.MoveMouseDelay = 1;
       try{
          scr.wait("test-res/item1-list2.png", 10);
       }
       catch(Exception e){
         e.printStackTrace();
         ScreenImage img = scr.capture();
         BufferedImage bimg = img.getImage();
         File outf = new File("testDragDrop.png");
         ImageIO.write(bimg, "png", outf);
         Debug.error("fail to wait test-res/item1-list2.png. Save screenshot to " + outf.getAbsolutePath());
       }
       scr.dragDrop("test-res/item2-list1.png", "test-res/item1-list2.png",0);
       assertNotNull(scr.wait("test-res/draglist-result.png",10));
       frame.dispose();
    }


    @Test
    public void testKeys() throws Exception {
       JFrame frame = KeyEventDemo.createAndShowGUI();
       Screen scr = new Screen();
       scr.type("abcde" + Key.ENTER);
       scr.type("captial letter", KeyModifier.SHIFT);
       //TODO: verify output
       frame.dispose();
    }




    public void testRegionClickOffset() throws Exception
    {
       System.out.println("testRegionClickOffset");
       JButtons frame = new JButtons();
       assertEquals(0, frame.getCount()[1]);
       assertEquals(0, frame.getCount()[2]);
       Screen scr = new Screen();
       Pattern ptn = new Pattern("test-res/show-all.png").similar(0.9f).targetOffset(-80,2);
       int ret = scr.click(ptn, 0);
       Thread.sleep(2000);
       assertEquals(1, frame.getCount()[1]);
       assertEquals(0, frame.getCount()[2]);
       frame.dispose();
    }

    public void testRegionFind() throws Exception
    {
       System.out.println("testRegionFind");
       Screen scr = new Screen();
       scr.setROI(Region.create(0,0,300,300));
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



    public void testSmallRegion() throws Exception
    {
       System.out.println("testSmallRegion");
       Screen scr = new Screen();
       scr.setROI(Region.create(0,0,100,200));
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
    }

    /*
    public void testFindAll() throws Exception{
       GridLayoutDemo f = GridLayoutDemo.createAndShowGUI();
       Thread.sleep(1000);
       Screen s = new Screen();
       float min=0.70f;
       Iterator<Match> ms = s.findAll(new Pattern("test-res/button-8.png").similar(min));
       int count[] = new int[101];
       while(ms.hasNext()){
         Match m = ms.next();
         count[(int)(m.score*100)]++;
       }
       for(int i=(int)(min*100);i<=100;i++)
          System.out.println("" + i + " " + count[i]);
    }
    */


    public void _testSpatialOperators() throws Exception{
       GridLayoutDemo f = GridLayoutDemo.createAndShowGUI();
       Thread.sleep(1000);
       Screen s = new Screen();
       Match b8 = s.find(new Pattern("test-res/button-8.png").similar(0.97f));
       s.click(b8, 0);
       assertEquals("clicked", f.getText(8));
       Thread.sleep(1000);
       System.out.println("above: " + b8.above().find("test-res/button-2.png"));
       System.out.println("btn2: " + s.find("test-res/button-2.png"));
       Thread.sleep(1000);
       assertEquals("clicked", f.getText(2));
       f.setVisible(false);
       f.dispose();
    }


    /*
    public void testSpatialOperators() throws Exception{
       GridLayoutDemo f = GridLayoutDemo.createAndShowGUI();
       Thread.sleep(1000);
       Screen s = new Screen();
       //Match b2 = s.find((new Pattern("test-res/button-2.png")).similar(0.99f));
       //s.click(b2, 0);
       Match b5 = s.find((new Pattern("test-res/button-5.png")).similar(0.99f));
       s.click(b5, 0);
       assertEquals("clicked", f.getText(5));
       b5.above().click("test-res/button-2.png", 0);
       //s.click(b5.above().find("test-res/button-2.png"), 0);
       Thread.sleep(1000);
       s.click(b5.left().find("test-res/button-4.png"), 0);
       Thread.sleep(1000);
       s.click(b5.right().find("test-res/button-6.png"), 0);
       Thread.sleep(1000);
       s.click(b5.below().find("test-res/button-8.png"), 0);
       Thread.sleep(1000);
       assertEquals("clicked", f.getText(2));
       assertEquals("clicked", f.getText(4));
       assertEquals("clicked", f.getText(6));
       assertEquals("clicked", f.getText(8));
       f.setVisible(false);
       f.dispose();
    }
    */

    public void targetAppeared(AppearEvent e){
       System.out.println("targetAppeared: " + e);
       appear_count++;
    }

    public void targetVanished(VanishEvent e){
       System.out.println("targetVanished: " + e);
       vanish_count++;
    }

    public void targetChanged(ChangeEvent e){
       System.out.println("targetChanged: " + e);
       change_count++;
    }

    int appear_count = 0, vanish_count = 0, change_count = 0;

    /*

    public void testObserve() throws Exception {
       System.out.println("testObserve");
       Region r = Region.create(0, 0, 300, 300);
       r.onAppear("test-res/cup-btn.png", this);
       r.onVanish("test-res/cup-btn.png", this);
       r.onChange(this);
       Thread th = new Thread(){
          public void run(){
             JButtons frame = new JButtons();
             try{
                Thread.sleep(2000);
             }
             catch(Exception e){
                e.printStackTrace();
             }
             frame.setVisible(false);
             frame.dispose();
          }
       };
       th.start();
       r.observe(6);
       assertEquals(1, appear_count);
       assertEquals(2, change_count);
       assertEquals(2, vanish_count);
    }


    public void testType() throws Exception
    {
       System.out.println("testType");
       InputsFrame f = new InputsFrame();
       Region r = Region.create(0,0,200,200);
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
       System.out.println("testPaste");
       InputsFrame f = new InputsFrame();
       Region r = Region.create(0,0,200,200);
       String s = "123!!";
       r.paste("test-res/input-y.png", s);
       Thread.sleep(500);
       assertEquals(s, f.getText(1));
    }

    public void testRegion() throws Exception
    {
       System.out.println("testRegion");
       JButtons frame = new JButtons();
       Screen scr = new Screen();
       Match ret = scr.find("test-res/cup-btn.png");
       assertNotNull(ret);
       Region region = Region.create(10,0,200,200);
       ret = region.find("test-res/cup-btn.png");
       assertNotNull(ret);
       region = Region.create(600,0,200,200);
       region.setThrowException(false);
       ret = region.find("test-res/cup-btn.png");
       assertNull(ret);
    }


    public void testClick() throws Exception
    {
       System.out.println("testClick");
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
       System.out.println("testRegionClickOffset");
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

    public void testRegionFind() throws Exception
    {
       System.out.println("testRegionFind");
       Screen scr = new Screen();
       scr.setROI(Region.create(0,0,300,300));
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



    public void testSmallRegion() throws Exception
    {
       System.out.println("testSmallRegion");
       Screen scr = new Screen();
       scr.setROI(Region.create(0,0,100,200));
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
    }

    */
}

