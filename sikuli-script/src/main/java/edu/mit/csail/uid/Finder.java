package edu.mit.csail.uid;

import java.awt.*;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder implements Iterator<Match>{
   private long _instance = 0;
   private Region _region = null;
   private Pattern _pattern = null;
   private FindInput _findInput = null;
   private FindResults _results = null;
   private int _cur_result_i = 0;

   static {
      try{
         NativeLoader.loadLibrary("VisionProxy");
         System.out.println("Sikuli vision engine loaded.");
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

   public Finder(String screenFilename){
      this(screenFilename, null);
   }

   public Finder(String screenFilename, Region region){
      String fname = screenFilename;
      if( !(new File(screenFilename)).exists() && Settings.BundlePath!=null)
         fname = Settings.BundlePath + File.separator + screenFilename;
      _findInput = new FindInput();
      _findInput.setSource(screenFilename);
      _region = region;
   }

   public Finder(ScreenImage img, Region region){
      _region = region;
      byte[] data = OpenCV.convertBufferedImageToByteArray(img.getImage());
      Mat target = new Mat(img.h, img.w, VisionProxyConstants.CV_8UC3, data);
   }

   public void __del__(){
      destroy();
   }

   protected void finalize() throws Throwable {
      destroy();
   }

   public <PSC> void setFindInput(PSC ptn){
      if( ptn instanceof Pattern ){
         _pattern = (Pattern)ptn;
         _findInput.setTarget(_pattern.imgURL);
         _findInput.setSimilarity(_pattern.similarity);
      }
      else if( ptn instanceof String){
         boolean isText = false;
         //TODO: check if we need to use OCR
         _findInput.setTarget((String)ptn, isText);
         _findInput.setSimilarity(Settings.MinSimilarity);
      }
   }


   /**
    * void find( Pattern/String/PatternClass ) 
    * finds the given pattern in the given ScreenImage.
    */
   public <PSC> void find(PSC ptn){
      setFindInput(ptn);
      _results = Vision.find(_findInput);
   }

   public void find(String templateFilename, double minSimilarity){
      String fname = templateFilename;
      if( !(new File(templateFilename)).exists() && Settings.BundlePath!=null)
         fname = Settings.BundlePath + File.separator + templateFilename;
      _findInput.setTarget(templateFilename);
      _findInput.setSimilarity(minSimilarity);
      _results = Vision.find(_findInput);
   }

   public <PSC> void findAll(PSC ptn){
      setFindInput(ptn);
      _findInput.setFindAll(true);
      _results = Vision.find(_findInput);
   }

   public void findAll(String templateFilename, double minSimilarity){
      String fname = templateFilename;
      if( !(new File(templateFilename)).exists() && Settings.BundlePath!=null)
         fname = Settings.BundlePath + File.separator + templateFilename;
      _findInput.setTarget(templateFilename);
      _findInput.setSimilarity(minSimilarity);
      _findInput.setFindAll(true);
      _results = Vision.find(_findInput);
   }

   public boolean hasNext(){
      if(_results != null && _results.size() > _cur_result_i)
         return true;
      return false;
   }


   public Match next(){
      Match ret = null;
      if(hasNext()){
         FindResult fr = _results.get(_cur_result_i++);
         try{
            ret = new Match(fr);
         }
         catch(AWTException e){
            e.printStackTrace();
         }
         if(_region != null)
            ret = _region.toGlobalCoord(ret);
         if(_pattern != null){
            Location offset = _pattern.getTargetOffset();
            ret.setTargetOffset(offset);
         }
      }
      return ret;
   }

   public void remove(){
   }

   public void destroy(){  
   }

}

