package edu.mit.csail.uid;

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
   private native boolean hasNext(long finder);
   private native Match next(long finder);
   private native void destroy(long finder);
}

