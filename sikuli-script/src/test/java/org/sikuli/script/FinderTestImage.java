package org.sikuli.script;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

class FinderTestImage {

   File file;
   BufferedImage image;
   BufferedImage getImage() throws IOException{
      if (image == null){
         image = ImageIO.read(file);
      }
      return image;
   }

   String getScreenImageFilename(){
      return file.getAbsolutePath();
   }
   
   public String toString(){
      return file.getParentFile().getName();
   }
   
   final static String ROOT = "test-res/cv";

   private ArrayList<FinderTestTarget> testTargets = new  ArrayList<FinderTestTarget>();  
   ArrayList<FinderTestTarget> getTestTargets(){
      return testTargets;
   }
   
   FinderTestTarget getTestTarget(int index) {
      return testTargets.get(index);
   }

   
   public FinderTestImage(String name){
      File rootDir = new File(ROOT);


      File testcaseDir = new File(rootDir, name);

      file = new File(testcaseDir, "screen.png");
      File[] targetFiles = testcaseDir.listFiles(new FilenameFilter(){
         public boolean accept(File dir, String name) { 
            return name.startsWith("target"); 
         } 
      });      

      for (File targetFile : targetFiles){
         FinderTestTarget testTarget = new FinderTestTarget(this,targetFile);
         testTargets.add(testTarget);
      }

   }

   static FinderTestImage createFromDirectory(String name){
      return new FinderTestImage(name);
   }


   
   
}
