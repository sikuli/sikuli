package edu.mit.csail.uid;

import java.awt.*;
import java.util.Iterator;
import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder implements Iterator<Match>{
   private long _instance = 0;
   private Region _region = null;
   private Pattern _pattern = null;

   static {
      try{
         NativeLoader.loadLibrary("ScreenMatchProxy");
         System.out.println("ScreenMatchProxy loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public Finder __enter__(){
      return this;
   }

   public void __exit__(Object type, Object value, Object trackback){
      destroy();
   }

   public Finder(String screenFilename, Region region){
      _instance = createFinder(screenFilename);
      _region = region;
   }

   public Finder(ScreenImage img, Region region){
      byte[] data = OpenCV.convertBufferedImageToByteArray(img.getImage());
      _instance = createFinder(data, img.w, img.h);
      _region = region;
   }

   public void __del__(){
      destroy();
   }

   protected void finalize() throws Throwable {
      destroy();
   }

   private native long createFinder(String screenFilename);
   private native long createFinder(byte[] screenImage, int w, int h);


   /**
    * void find( Pattern/String/PatternClass ) 
    * finds the given pattern in the given ScreenImage.
    */
   public <PSC> void find(PSC ptn){
      if( ptn instanceof Pattern ){
         _pattern = (Pattern)ptn;
         find(_pattern.imgURL, _pattern.similarity);
      }
      else if( ptn instanceof String){
         find((String)ptn, Settings.MinSimilarity);
      }
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
      if(_instance!=0){
         Match ret = next(_instance);
         ret = _region.toGlobalCoord(ret);
         if(_pattern != null){
            Location offset = _pattern.getTargetOffset();
            ret.setTargetOffset(offset);
         }
         return ret;
      }
      return null;
   }

   @Override
   public void remove(){
   }

   public void destroy(){  
      Debug.log("destroy finder " + _instance);
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

