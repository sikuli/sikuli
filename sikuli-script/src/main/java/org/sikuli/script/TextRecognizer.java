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

import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.Vision;

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

   public String recognize(ScreenImage simg){
      BufferedImage img = simg.getImage();
      return recognize(img);
   }

   public String recognize(BufferedImage img){
      if (_init_succeeded){
         Mat mat = OpenCV.convertBufferedImageToMat(img);
         return Vision.recognize(mat);
      }else{
         return "";
      }
   }
}

