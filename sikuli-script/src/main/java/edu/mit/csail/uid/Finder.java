package edu.mit.csail.uid;

import java.awt.*;
import java.util.Iterator;
import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder implements Iterator<Match>{
   private long _instance = 0;
   static {
      try{
         NativeLoader.loadLibrary("ScreenMatchProxy");
         System.out.println("ScreenMatchProxy loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public Finder(String screenFilename){
      _instance = createFinder(screenFilename);
   }

   public Finder(ScreenImage img){
      byte[] data = OpenCV.convertBufferedImageToByteArray(img.getImage());
      _instance = createFinder(data, img.w, img.h);
   }

   protected void finalize() throws Throwable {
      destroy();
   }

   private native long createFinder(String screenFilename);
   private native long createFinder(byte[] screenImage, int w, int h);

   public void find(String templateFilename){
      find(templateFilename, Settings.MinSimilarity);
   }

   public void find(String templateFilename, double minSimilarity){
      find(_instance, templateFilename, minSimilarity);
   }

   @Override
   public boolean hasNext(){
      if(_instance!=0)
         return hasNext(_instance);
      return false;
   }

   @Override
   public Match next(){
      if(_instance!=0)
         return next(_instance);
      return null;
   }

   @Override
   public void remove(){
   }

   public void destroy(){  
      if(_instance!=0){
         destroy(_instance);  
         _instance = 0;
      }
   }


   private native void find(long finder, String templateFilename, double minSimilarity);
   private native void find(long finder, byte[] templateImage, int w, int h, double minSimilarity);
   private native boolean hasNext(long finder);
   private native Match next(long finder);
   private native void destroy(long finder);
}

