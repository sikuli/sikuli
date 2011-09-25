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

import org.sikuli.script.natives.*;

public class OCRTest 
{

   List<String> _failedAssertion = new LinkedList<String>();

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

   protected float testOcrSuite(String suite, Stat accum) throws IOException{
      BufferedImage img = ImageIO.read(new File("test-res/OCR/" + suite + ".png"));
      List<OCRTruth> truth = readGroundTruth("test-res/OCR/" + suite + ".csv");
      int correct = 0, total = 0;
      int margin = 1;
      TextRecognizer tr = TextRecognizer.getInstance();
      for(OCRTruth t : truth){
         BufferedImage text_img = img.getSubimage(
               t.x-margin, t.y-margin, t.w+margin*2+1, t.h+margin*2+1);
         String recognized_text = tr.recognizeWord(text_img);
         //Screen scr = createMockScreen(text_img);
         //String recognized_text = scr.text(); // Region.text() uses listText, not good for single word images.
         if(t.text.equals(recognized_text))
            correct++;
         else{
            System.err.println("got: '" + recognized_text + "', expected: '" + t.text + "'");
         }
         total++;
      }
      System.err.println(suite + ": " + correct + "/" + total);
      accum.true_pos += correct;
      accum.recall_s += total;
      return correct/(float)total;
   }

   private List<Match> filter(List<Match> matches, double min_accept_score){
      List<Match> ret = new LinkedList<Match>();
      for(Match m : matches)
         if(m.score >= min_accept_score)
            ret.add(m);
      return ret;
   }

   protected float[] testListTextOnSuite(String suite, Stat accum) throws IOException{
      BufferedImage img = ImageIO.read(new File("test-res/OCR/" + suite + ".png"));
      List<OCRTruth> truth = readGroundTruth("test-res/OCR/" + suite + ".csv");
      double MIN_ACCEPT_SCORE = 0.7;
      int found = 0, correct = 0;
      Screen scr = createMockScreen(img);
      List<Match> blobs = scr.listText();
      //blobs = filter(blobs, MIN_ACCEPT_SCORE);
      int truth_size = truth.size();

      for(Match m : blobs){
         for(OCRTruth t : truth){
            if( overlap(m, t) ){
               truth.remove(t);
               found++;
               String ocr = m.text();
               if(ocr.equals(t.text))
                  correct++;
               else
                  System.out.println("got: '" + ocr + "', expected: " + t.text);
               break;
            }
         }
      }
      //System.err.println(suite + " precision: " + correct + "/" + blobs.size());
      //System.err.println(suite + " recall: " + correct + "/" + truth.size());
      float precision = correct / (float)blobs.size();
      float recall = correct / (float)truth_size;
      float coverage = found/(float)truth_size;
      accum.true_pos += correct;
      accum.coverage_c += found;
      accum.precision_s += blobs.size();
      accum.recall_s += truth_size;
      return new float[]{precision, recall, coverage};
   }


   String _suites[] = {
      "Linux-XFCE11", "Mac-desktop", "Mac-desktop2",
      "web-twitter",  "win-7-desktop", "win-vista-black"};

   @Test
   public void testAllOCR() throws Exception {

      float accuracy[] = {202/348f, 109/177f, 221/325f, 132/236f, 80/133f, 57/112f};
      int i = 0, correct = 0, total = 0;
      Stat accum = new Stat();
      for(String suite : _suites){
         float acc = testOcrSuite(suite, accum);
         // each case should not be worse than 95% of the expected accuracy.
         delayAssertTrue(suite + " accuracy too low: " + acc + 
                         " expected: " + accuracy[i] * 0.95,
                         acc >= (accuracy[i] * 0.95)); 
         i++;
      }
      float total_acc = accum.true_pos / (float)accum.recall_s;
      System.err.println("OCR Accuracy: " + (total_acc*100) + "%");
      delayAssertTrue("OCR avg accuracy too low: " + total_acc, 
                      total_acc >= 0.6);
      checkDelayAssertion();
   }

   protected boolean overlap(Region r, OCRTruth t){
      Rectangle a = new Rectangle(r.x, r.y, r.w, r.h);
      return overlap(a, t);
   }

   protected boolean overlap(Rectangle a, OCRTruth t){
      float MIN_OVERLAP_AREA = 0.95f;
      Rectangle b = new Rectangle(t.x, t.y, t.w, t.h);
      Rectangle inter = a.intersection(b);
      //System.out.println("intersection " + a + " " + b + " " + inter);
      if(inter.getWidth()>0 && inter.getHeight()>0){
         double inter_area = inter.getWidth()*inter.getHeight();
         if(inter_area/(t.w*t.h) >= MIN_OVERLAP_AREA)
            return true;
      }
      return false;
   }



   protected void delayAssertTrue(String msg, boolean condition){
      if(!condition)
         _failedAssertion.add(msg);
   }
   
   protected void checkDelayAssertion(){
      int failures = _failedAssertion.size();
      String msg = "";
      for(String m : _failedAssertion)
         msg += m + "\n";
      _failedAssertion.clear();
      if(failures>0)
         fail( failures + " assertion(s) failed.\n" + msg);
   }

   @Test
   public void testListText() throws Exception {
      double lb_precision[] = {.43, .36, .31, .45, .36, .63};
      double lb_recall[] = {.56, .46, .34, .47, .54, .57};
      double lb_coverage[] = {.67, .57, .39, .52, .66, .58};
      int i = 0;
      Stat accum = new Stat();
      for(String suite : _suites){
         Float recall = 0.f, precision = 0.f;
         float[] prc = testListTextOnSuite(suite, accum);
         System.err.println(suite + " precision: " + prc[0] +
                            ", recall: " + prc[1] +
                            ", coverage: " + prc[2]);
         delayAssertTrue(suite + " precision too low: " + prc[0] + 
                         " expected: " + lb_precision[i] * 0.95,
                         prc[0] >= (lb_precision[i] * 0.95)); 
         delayAssertTrue(suite + " recall too low: " + prc[1] + 
                         " expected: " + lb_recall[i] * 0.95,
                         prc[1] >= (lb_recall[i] * 0.95)); 
         delayAssertTrue(suite + " coverage too low: " + prc[2] + 
                         " expected: " + lb_coverage[i] * 0.95,
                         prc[2] >= (lb_coverage[i] * 0.95)); 
         i++;
      }
      float total_coverage = accum.coverage_c / (float)accum.recall_s;
      float total_precision = accum.true_pos/(float)accum.precision_s;
      float total_recall = accum.true_pos/(float)accum.recall_s;
      System.err.println("Region.listText precision: " + (total_precision*100) + "%");
      System.err.println("Region.listText recall: " + (total_recall*100) + "%");
      System.err.println("Region.listText coverage: " + (total_coverage*100) + "%");
      delayAssertTrue("avg precision too low: " + total_precision, 
                      total_precision >= 0.41);
      delayAssertTrue("avg recall too low: " + total_recall, 
                      total_recall >= 0.48);
      delayAssertTrue("avg coverage too low: " + total_coverage, 
                      total_coverage >= 0.55);
      checkDelayAssertion();
   }

}

class Stat {
   int true_pos, precision_s, recall_s;
   int coverage_c;
   
   public Stat(){
      true_pos = precision_s = recall_s = 0;
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


