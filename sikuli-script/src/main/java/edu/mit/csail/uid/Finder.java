package edu.mit.csail.uid;

import java.awt.*;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class Finder implements Iterator<Match>{
   private Region _region = null;
   private Pattern _pattern = null;
   private FindInput _findInput = new FindInput();
   private FindResults _results = null;
   private ImageLocator _imgLocator = null;
   private int _cur_result_i = 0;

   static {
      try{
         NativeLoader.loadLibrary("VisionProxy");
         System.out.println("Sikuli vision engine loaded.");
         TextRecognizer tr = TextRecognizer.getInstance();
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
         _findInput.setTarget(findImageFile(_pattern.imgURL));
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
         fin.setTarget(filename, false);
      }
      catch(IOException e){
         //assume it's text 
         fin.setTarget(target, true);
      }
   }



   /**
    * void find( Pattern/String/PatternClass ) 
    * finds the given pattern in the given ScreenImage.
    */
   public <PSC> void find(PSC ptn) throws IOException{
      setFindInput(ptn);
      _results = Vision.find(_findInput);
   }

   public void find(String templateFilename, double minSimilarity) throws IOException{
      setTargetSmartly(_findInput, templateFilename);
      _findInput.setSimilarity(minSimilarity);
      _results = Vision.find(_findInput);
   }

   public <PSC> void findAll(PSC ptn) throws IOException {
      setFindInput(ptn);
      _findInput.setFindAll(true);
      _results = Vision.find(_findInput);
   }

   public void findAll(String templateFilename, double minSimilarity) throws IOException {
      setTargetSmartly(_findInput, templateFilename);
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

