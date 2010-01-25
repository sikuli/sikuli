package edu.mit.csail.uid;

import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class ScreenMatchProxy {
   static {
      // load libScreenMatchProxy.{so,jnilib}
      //System.loadLibrary("ScreenMatchProxy");
      try{
         NativeLoader.loadLibrary("ScreenMatchProxy");
         System.out.println("ScreenMatchProxy loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   /*
   public native Match[] screenMatchByOCR(String target, String screen);
   public native Match[] screenMatchByOCR(String target, String screen, double threshold);
   */
   public native Match[] screenMatchByOCR(
         String target, String screen, double threshold, int numMatches);

   /*
   public native Match[] screenMatch(String target, String screen);
   public native Match[] screenMatch(String target, String screen, double threshold);
   */
   public native Match[] screenMatch(
         String target, String screen, double threshold, int numMatches);

   public native Match[] screenDiff(String before, String after);

}
