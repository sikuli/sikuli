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

public class RegionFastTest 
{
   static protected int DESKTOP_X = 0, DESKTOP_Y = 0, DESKTOP_W = 1680, DESKTOP_H = 1050;
   static protected Rectangle DESKTOP_RECT = new Rectangle(DESKTOP_X, DESKTOP_Y, DESKTOP_W, DESKTOP_H);

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
      assertEquals(m.x, 792);
      assertEquals(m.y, 391);
      assertEquals(m.w, 52);
      assertEquals(m.h, 54);
   }
}

