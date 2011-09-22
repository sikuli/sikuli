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
import java.io.*;
import java.util.*;


public class OCRTest 
{
   protected Screen createMockScreen(BufferedImage img){
      Rectangle rect = new Rectangle(0, 0, img.getWidth(), img.getHeight());
      ScreenImage desktop_simg = new ScreenImage(rect, img);

      Screen mockScr = spy(new Screen());
      doReturn(mockScr).when(mockScr).getScreen();
      doReturn(desktop_simg).when(mockScr).capture(anyInt(), anyInt(), anyInt(), anyInt());
      return mockScr;
   }

   protected List<OCRTruth> readGroundTruth(String csv_filename) throws IOException{
      File file = new File(csv_filename);
      BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
      List<OCRTruth> ret = new ArrayList<OCRTruth>();
      String line;
      while((line = bufRdr.readLine()) != null) {
         ret.add(new OCRTruth(line));
      }
      return ret;
   }

   protected float testOcrSuite(String suite) throws IOException{
      BufferedImage img = ImageIO.read(new File("test-res/OCR/" + suite + ".png"));
      List<OCRTruth> truth = readGroundTruth("test-res/OCR/" + suite + ".csv");
      int correct = 0, total = 0;
      for(OCRTruth t : truth){
         BufferedImage text_img = img.getSubimage(t.x, t.y, t.w, t.h);
         Screen scr = createMockScreen(text_img);
         String recognized_text = scr.text().trim();
         if(t.text.equals(recognized_text))
            correct++;
         else{
            //System.err.println("'" + recognized_text + "' expected: '" + t.text + "'");
         }
         total++;
      }
      System.err.println(suite + ": " + correct + "/" + total);
      return correct/(float)total;
   }

   @Test
   public void testLinux_XFCE11() throws Exception {
      String name = "Linux-XFCE11";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 27/348f);
   }

   @Test
   public void testMacDesktop() throws Exception {
      String name = "Mac-desktop";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 22/177f);
   }

   @Test
   public void testMacDesktop2() throws Exception {
      String name = "Mac-desktop2";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 13/325f);
   }

   @Test
   public void testTwitter() throws Exception {
      String name = "web-twitter";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 33/326f);
   }


   @Test
   public void testWin7Desktop() throws Exception {
      String name = "win-7-desktop";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 10/133f);
   }

   @Test
   public void testWinVistaBlackDesktop() throws Exception {
      String name = "win-vista-black";
      float accuracy = testOcrSuite(name);
      assertTrue(accuracy >= 7/112f);
   }

}

class OCRTruth {
   public int x, y, w, h;
   public String text;

   public OCRTruth(String csvLine){
      String[] cols = csvLine.split("\t");
      x = Integer.parseInt(cols[0]);
      y = Integer.parseInt(cols[1]);
      w = Integer.parseInt(cols[2]);
      h = Integer.parseInt(cols[3]);
      text = cols[4];
   }
}


