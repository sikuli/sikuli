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


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;

public class RegionFastTest 
{
   static protected int DESKTOP_X = 0, DESKTOP_Y = 0, DESKTOP_W = 1680, DESKTOP_H = 1050;
   static protected Rectangle DESKTOP_RECT = new Rectangle(DESKTOP_X, DESKTOP_Y, DESKTOP_W, DESKTOP_H);
   static protected Rectangle NETWORK_ICON = new Rectangle(792, 391, 52, 54);
   static protected Screen _mockScr;

   @BeforeClass static public void setupMockScreen() throws Exception{
      BufferedImage desktop_img = ImageIO.read(new File("test-res/mac-desktop.png"));
      ScreenImage desktop_simg = new ScreenImage(DESKTOP_RECT, desktop_img);

      _mockScr = spy(new Screen());
      doReturn(_mockScr).when(_mockScr).getScreen();
      doReturn(desktop_simg).when(_mockScr).capture(anyInt(), anyInt(), anyInt(), anyInt());
   }

   @Test
   public void test_doFind() throws Exception {
      Match m = _mockScr.doFind("test-res/network.png");
      assertEquals(m.x, NETWORK_ICON.x);
      assertEquals(m.y, NETWORK_ICON.y);
      assertEquals(m.w, NETWORK_ICON.width);
      assertEquals(m.h, NETWORK_ICON.height);
      assertEquals(m.score, 1.0, 0.01);
   }

   @Test
   public void test_doFindAll_1match() throws Exception {
      Iterator<Match> matches = _mockScr.doFindAll("test-res/network.png");
      int count = 0;
      while(matches.hasNext()){
         count++;
         Match m = matches.next();
         assertEquals(m.x, NETWORK_ICON.x);
         assertEquals(m.y, NETWORK_ICON.y);
         assertEquals(m.w, NETWORK_ICON.width);
         assertEquals(m.h, NETWORK_ICON.height);
      }
      assertEquals(count, 1);
   }

   @Test
   public void test_doFindAll_many_match() throws Exception {
      Iterator<Match> matches = _mockScr.doFindAll("test-res/pdf_icon.png");
      int count = 0;
      while(matches.hasNext()){
         count++;
         Match m = matches.next();
         assertTrue(m.x == 1600 || m.x == 1490);
         assertTrue( (m.y % 92) == 25);
         assertEquals(m.w, 42);
         assertEquals(m.h, 53);
         //System.out.println(m);
      }
      assertEquals(count, 10);
   }


}

