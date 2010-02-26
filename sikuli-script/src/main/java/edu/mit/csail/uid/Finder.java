package edu.mit.csail.uid;

import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder {
   private long _instance;
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

   protected void finalize() throws Throwable {
      destroy();
   }

   private native long createFinder(String screenFilename);

   public void find(String templateFilename){
      find(templateFilename, 0.0);
   }

   public void find(String templateFilename, double minSimilarity){
      find(_instance, templateFilename, minSimilarity);
   }
   public boolean hasNext(){
      return hasNext(_instance);
   }
   public Match next(){
      return next(_instance);
   }

   public void destroy(){  
      destroy(_instance);  
   }


   private native void find(long finder, String templateFilename, double minSimilarity);
   private native boolean hasNext(long finder);
   private native Match next(long finder);
   private native void destroy(long finder);
}

