package org.sikuli.script;

import java.awt.*;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.sikuli.script.natives.FindInput;
import org.sikuli.script.natives.FindResult;
import org.sikuli.script.natives.FindResults;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.TARGET_TYPE;
import org.sikuli.script.natives.Vision;

import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder implements Iterator<Match>{
   private Region _region = null;
   private Pattern _pattern = null;
   private FindInput _findInput = new FindInput();
   private FindResults _results = null;
   private ImageLocator _imgLocator = null;
   private int _cur_result_i;

   static {
      try{
         NativeLoader.loadLibrary("VisionProxy");
         Debug.info("Sikuli vision engine loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
      Debug.ENABLE_PROFILING = true;
   }

   public Finder __enter__(){
      return this;
   }

   public void __exit__(Object type, Object value, Object trackback){
      destroy();
   }

   public Finder(String screenFilename) throws IOException{
      this(screenFilename, null);
   }

   public Finder(String screenFilename, Region region) throws IOException{
      String fname = findImageFile(screenFilename);
      _findInput.setSource(fname);
      _region = region;
   }

   public Finder(ScreenImage img, Region region){
      Mat target = OpenCV.convertBufferedImageToMat(img.getImage());
      _findInput.setSource(target);
      _region = region;
   }

   public void __del__(){
      destroy();
   }

   protected void finalize() throws Throwable {
      destroy();
   }

   protected String findImageFile(String file) throws IOException{
      if(_imgLocator == null)
         _imgLocator = new ImageLocator();
      return _imgLocator.locate(file);
   }

   protected <PSC> void setFindInput(PSC ptn) throws IOException{
      if( ptn instanceof Pattern ){
         _pattern = (Pattern)ptn;
         Mat targetMat = OpenCV.convertBufferedImageToMat(_pattern.getImage());
         _findInput.setTarget(targetMat);
         _findInput.setSimilarity(_pattern.similarity);
      }
      else if( ptn instanceof String){
         setTargetSmartly(_findInput, (String)ptn);
         _findInput.setSimilarity(Settings.MinSimilarity);
      }
   }

   protected void setTargetSmartly(FindInput fin, String target){
      try{
         //assume it's a file first
         String filename = findImageFile(target);
         fin.setTarget(TARGET_TYPE.IMAGE, filename);
      }
      catch(IOException e){
         // this will init text recognizer on demand
         TextRecognizer tr = TextRecognizer.getInstance();
         //assume it's text 
         fin.setTarget(TARGET_TYPE.TEXT, target);
      }
   }



   /**
    * void find( Pattern/String/PatternClass ) 
    * finds the given pattern in the given ScreenImage.
    */
   public <PSC> void find(PSC ptn) throws IOException{
      setFindInput(ptn);
      _results = Vision.find(_findInput);
      _cur_result_i = 0;
   }

   public void find(String templateFilename, double minSimilarity) throws IOException{
      setTargetSmartly(_findInput, templateFilename);
      _findInput.setSimilarity(minSimilarity);
      _results = Vision.find(_findInput);
      _cur_result_i = 0;
   }

   public <PSC> void findAll(PSC ptn) throws IOException {
      Debug timing = new Debug();
      timing.startTiming("Finder.findAll");

      setFindInput(ptn);
      _findInput.setFindAll(true);
      _results = Vision.find(_findInput);
      _cur_result_i = 0;

      timing.endTiming("Finder.findAll");
   }

   public void findAll(String templateFilename, double minSimilarity) throws IOException {
      Debug timing = new Debug();
      timing.startTiming("Finder.findAll");

      setTargetSmartly(_findInput, templateFilename);
      _findInput.setSimilarity(minSimilarity);
      _findInput.setFindAll(true);
      _results = Vision.find(_findInput);
      _cur_result_i = 0;

      timing.endTiming("Finder.findAll");
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

