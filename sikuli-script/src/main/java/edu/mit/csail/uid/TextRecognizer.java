package edu.mit.csail.uid;

import java.awt.image.*;

// Singleton
public class TextRecognizer {
   protected static TextRecognizer _instance = null;

   protected TextRecognizer(){
      Vision.initOCR(Settings.OcrDataPath);
   }

   public static TextRecognizer getInstance(){
      if(_instance==null)
         _instance = new TextRecognizer();
      return _instance;
   }

   public String recognize(ScreenImage simg){
      BufferedImage img = simg.getImage();
      Mat mat = OpenCV.convertBufferedImageToMat(img);
      return Vision.recognize(mat);
   }
}

