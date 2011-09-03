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

   protected BufferedImage _desktop_img = null;

   public RegionFastTest() throws Exception{
      _desktop_img = ImageIO.read(new File("test-res/mac-desktop.png"));
   }


   protected Screen createMockScreen(Rectangle rect){
      BufferedImage img = _desktop_img.getSubimage(rect.x, rect.y, 
                                                   rect.width, rect.height);
      ScreenImage desktop_simg = new ScreenImage(rect, img);

      Screen mockScr = spy(new Screen());
      doReturn(mockScr).when(mockScr).getScreen();
      doReturn(desktop_simg).when(mockScr).capture(anyInt(), anyInt(), anyInt(), anyInt());
      return mockScr;
   }

   @Before public void setupMockScreen() throws Exception{
      _mockScr = createMockScreen(DESKTOP_RECT);
   }

   /*
   @Test
   public void test_callJythonMethod() throws Exception {
      Region jyr = Region.toJythonRegion(_mockScr);
      jyr.__enter__();
   }
   */

   @Test
   public void test_doFind() throws Exception {
      Match m = _mockScr.doFind("test-res/network.png");
      assertEquals(m.x, NETWORK_ICON.x);
      assertEquals(m.y, NETWORK_ICON.y);
      assertEquals(m.w, NETWORK_ICON.width);
      assertEquals(m.h, NETWORK_ICON.height);
      assertEquals(m.score, 1.0, 1e-5);
   }


   @Test
   public void test_doFindInScreenROI() throws Exception {
      int x = NETWORK_ICON.x-50, y = NETWORK_ICON.y-50, 
          w = 100+NETWORK_ICON.width, h = 100+NETWORK_ICON.height;
      Rectangle roi = new Rectangle(x,y,w,h);
      Screen scr = createMockScreen(roi);
      scr.setROI(roi);
      Match m = scr.doFind("test-res/network.png");
      assertEquals(m.x, NETWORK_ICON.x);
      assertEquals(m.y, NETWORK_ICON.y);
      assertEquals(m.w, NETWORK_ICON.width);
      assertEquals(m.h, NETWORK_ICON.height);
      assertEquals(m.score, 1.0, 1e-5);
   }

   @Test
   public void test_autoWaitTimeout() throws Exception {
      double timeout = 0.1;
      String ptn = "test-res/network.png";
      _mockScr.setAutoWaitTimeout(timeout);
      _mockScr.find(ptn);
      verify(_mockScr).wait(ptn, timeout);
   }

   @Test
   public void test_autoWaitTimeout_no_wait() throws Exception {
      _mockScr.setAutoWaitTimeout(0);
      String ptn = "test-res/network.png";
      _mockScr.find(ptn);
      verify(_mockScr, never()).wait(anyObject(), anyDouble());
   }

   @Test
   public void test_getLastMatch() throws Exception {
      String ptn = "test-res/network.png";
      assertNull(_mockScr.getLastMatch());
      Match m = _mockScr.find(ptn);
      assertEquals(_mockScr.getLastMatch(), m);
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

