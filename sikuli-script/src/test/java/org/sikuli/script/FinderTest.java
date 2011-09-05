/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import org.junit.* ;
import static org.junit.Assert.* ;
import static org.mockito.Mockito.*;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;


import java.awt.Point;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.sikuli.script.natives.Vision;

public class FinderTest 
{

   private void testTargetScreenSetWithFindAll(String test_dir, float similarity) throws Exception{
      FinderTestImage testImgs = FinderTestImage.createFromDirectory(test_dir);
      ArrayList<FinderTestTarget> testTargets =  testImgs.getTestTargets();
      String screenImg = testImgs.getScreenImageFilename();
      Finder f = new Finder(screenImg);
      for(FinderTestTarget target : testTargets){
         String targetFname = target.getFilename();
         f.findAll(new Pattern(targetFname).similar(similarity));
         List<Point> truth = target.getGroundTruthLocations();
         while(f.hasNext() && !truth.isEmpty()){
            Match m = f.next();
            boolean found = false;
            for(Point t : truth){
               if( m.x <= t.x && t.x <= m.x+m.w && 
                   m.y <= t.y && t.y <= m.y+m.h){
                  truth.remove(t);
                  found = true;
                  break;
               }
            }
            if(!found)
               System.err.println("NOT MATCHED: " + m + " " + target);
            assertTrue(found);
         }
      }
   }

   private void testTargetScreenSet(String test_dir) throws Exception{
      FinderTestImage testImgs = FinderTestImage.createFromDirectory(test_dir);
      ArrayList<FinderTestTarget> testTargets =  testImgs.getTestTargets();
      String screenImg = testImgs.getScreenImageFilename();
      Finder f = new Finder(screenImg);
      for(FinderTestTarget target : testTargets){
         String targetFname = target.getFilename();
         f.find(targetFname);
         while(f.hasNext()){
            Match m = f.next();
            boolean matched = target.isMatched(m);
            if(!matched)
               System.err.println("NOT MATCHED: " + m + " " + target);
            assertTrue(matched);
         }
      }
   }

   @Before
   public void setUp(){
      Vision.setParameter("GPU", 1);
      Vision.setParameter("MinTargetSize", 6);
   }

   @Test
   public void testParameter() throws Exception {
      float old_param = Vision.getParameter("MinTargetSize");
      assertTrue(old_param>0);
      Vision.setParameter("MinTargetSize", old_param+2);
      assertEquals(Vision.getParameter("MinTargetSize"), old_param+2, 1e-5);
      Vision.setParameter("MinTargetSize", old_param);
   }


   @Test
   public void testFinderFolders() throws Exception {
      Vision.setParameter("MinTargetSize", 12);
      testTargetScreenSet("finderfolders");
   }

   @Test
   public void testMacDesktopDark() throws Exception {
      testTargetScreenSet("macdesktopdark");
   }

   @Test
   public void testMacDesktop() throws Exception {
      testTargetScreenSet("macdesktop");
   }

   @Test
   public void testMacDesktopSikuli() throws Exception {
      testTargetScreenSet("macdesktopsikuli");
   }

   @Test
   public void testFuzzyDesktop() throws Exception {
      testTargetScreenSet("fuzzydesktop");
   }


   @Test
   public void testFuzzyFarmville() throws Exception {
      testTargetScreenSet("fuzzyfarmville");
   }

   @Test
   public void testInbox() throws Exception {
      testTargetScreenSet("sikuliinbox");
   }

   @Test
   public void testSikuliOrg() throws Exception {
      Vision.setParameter("MinTargetSize", 12);
      testTargetScreenSet("sikuliorgbanner");
   }

   @Test
   public void testWhereSpace() throws Exception {
      testTargetScreenSet("wherespace");
   }


   @Test
   public void testXpPricingApp() throws Exception {
      testTargetScreenSet("xppricingapp");
   }

   @Test
   public void testXpDesktop() throws Exception {
      testTargetScreenSet("xpdesktop");
   }

   @Test
   public void testXpFolders() throws Exception {
      testTargetScreenSet("xpfolders");
   }

   @Test
   public void testMacDesktopAll() throws Exception {
      testTargetScreenSetWithFindAll("macdesktop", 0.0001f);
   }

   @Test
   public void testBubbles() throws Exception {
      testTargetScreenSetWithFindAll("bubbles", 1.f);
   }
}

