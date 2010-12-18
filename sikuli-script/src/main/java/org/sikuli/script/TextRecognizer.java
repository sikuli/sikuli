package org.sikuli.script;

import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
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

   public void init(){
      System.out.println("Text Recgonizer inited.");
      try{
         String path = ResourceExtractor.extract("tessdata");
         // TESSDATA_PREFIX doesn't contain tessdata/
         if(path.endsWith("tessdata/"))
            path = path.substring(0,path.length()-9);
         Settings.OcrDataPath = path;
         Debug.log(3, "OCR data path: " + path);
      }
      catch(IOException e){
         e.printStackTrace();
      }
      Vision.initOCR(Settings.OcrDataPath);
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
      Mat mat = OpenCV.convertBufferedImageToMat(img);
      return Vision.recognize(mat);
   }
}

