/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;

import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.Vision;
import org.sikuli.script.natives.OCRWord;
import org.sikuli.script.natives.OCRWords;

import com.wapmx.nativeutils.jniloader.NativeLoader;

// Singleton
public class TextRecognizer {
   protected static TextRecognizer _instance = null;

   static {
      try{
         NativeLoader.loadLibrary("VisionProxy");
         TextRecognizer tr = TextRecognizer.getInstance();
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   protected TextRecognizer(){
      init();
   }
   
   boolean _init_succeeded = false;

   public void init(){
      Debug.info("Text Recognizer inited.");
      try{
         String path = ResourceExtractor.extract("tessdata");
         // TESSDATA_PREFIX doesn't contain tessdata/
         if(path.endsWith("tessdata/"))
            path = path.substring(0,path.length()-9);
         Settings.OcrDataPath = path;
         Debug.log(3, "OCR data path: " + path);

         Vision.initOCR(Settings.OcrDataPath);
         _init_succeeded = true;
      }
      catch(IOException e){
         e.printStackTrace();
      }catch(Exception e){
         e.printStackTrace();         
      }
   }

   public static TextRecognizer getInstance(){
      if(_instance==null)
         _instance = new TextRecognizer();
      return _instance;
   }

   public enum ListTextMode {
      WORD, LINE, PARAGRAPH
   };

   public List<Match> listText(ScreenImage simg, Region parent){
      return listText(simg, parent, ListTextMode.WORD);
   }

   //TODO: support LINE and PARAGRAPH
   // listText only supports WORD mode now.
   public List<Match> listText(ScreenImage simg, Region parent, ListTextMode mode){
      Mat mat = OpenCV.convertBufferedImageToMat(simg.getImage());
      OCRWords words = Vision.recognize_as_ocrtext(mat).getWords();
      List<Match> ret = new LinkedList<Match>();
      for(int i=0;i<words.size();i++){
         OCRWord w = words.get(i);
         Match m = new Match(parent.x+w.getX(), parent.y+w.getY(), w.getWidth(), w.getHeight(), 
                             w.getScore(), parent.getScreen(), w.getString());
         ret.add(m);
      }
      return ret;
   }


   public String recognize(ScreenImage simg){
      BufferedImage img = simg.getImage();
      return recognize(img);
   }

   public String recognize(BufferedImage img){
      if (_init_succeeded){
         Mat mat = OpenCV.convertBufferedImageToMat(img);
         return Vision.recognize(mat).trim();
      }else{
         return "";
      }
   }

   public String recognizeWord(ScreenImage simg){
      BufferedImage img = simg.getImage();
      return recognizeWord(img);
   }

   public String recognizeWord(BufferedImage img){
      if (_init_succeeded){
         Mat mat = OpenCV.convertBufferedImageToMat(img);
         return Vision.recognizeWord(mat).trim();
      }else{
         return "";
      }
   }
}

