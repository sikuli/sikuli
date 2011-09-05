package org.sikuli.script;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

//import org.apache.log4j.Logger;

class FinderTestTarget {
   File file;
   BufferedImage image;
   BufferedImage getImage() throws IOException{
      if (image == null){
         image = ImageIO.read(file);
      }
      return image;
   }
   
   public String toString(){
      String targetName = file.getName();
      /*
      if (targetName.indexOf("_") > 0){
         targetName = targetName.substring(0,targetName.indexOf("_"));
      }
      */
      return targetName;
   }
   
   public FinderTestImage getTestImage(){
      return testImage;
   }

   public String getFilename(){
      return file.getAbsolutePath();
   }
   
   //static Logger logger = Logger.getLogger(FinderTestTarget.class);

   static boolean recomputeGroundTruth = false;
   
   FinderTestImage testImage;

   FinderTestTarget(FinderTestImage scr, File file){
      testImage = scr;
      this.file = file;
      //groundTruth = extractGroundTruthFromFilename();
      
      groundTruthLocations = getEncodedGroundTruthLocationsFromFilename();
      
//      if (groundTruth != null){
//         int x = groundTruth.x + groundTruth.width/2;
//         int y = groundTruth.y + groundTruth.height/2;
//         addGroundTruthLocation(new Point(x,y));
//      }
   }

   //FindResult groundTruth = null;
   
   
   List<Point> groundTruthLocations = new ArrayList<Point>();
   List<Point> getGroundTruthLocations(){            
      return groundTruthLocations;
   }

   void addGroundTruthLocation(Point location){
      groundTruthLocations.add(location);
   }

   public boolean isMatched(Match m){
      for(Point t : groundTruthLocations){
         if( m.x <= t.x && t.x <= m.x+m.w && m.y <= t.y && t.y <= m.y+m.h)
            return true;
      }
      return false;
   }
   
   
   Point getGroundTruthLocation(){
//    groundTruth = extractGroundTruthFromFilename();
//    if (groundTruth == null || recomputeGroundTruth){
//       groundTruth = computeGroundTruthUsingExactMatch();
//    }
//    return groundTruth;
    return groundTruthLocations.get(0);
 }

   
 /*
   FindResult getGroundTruth(){
//      groundTruth = extractGroundTruthFromFilename();
//      if (groundTruth == null || recomputeGroundTruth){
//         groundTruth = computeGroundTruthUsingExactMatch();
//      }
//      return groundTruth;
      return null;
   }

   FindResult extractGroundTruthFromFilename(){
      // target2_569_20_45_26.png   => FindResult {569,20,45,26}

      String name = file.getName();
      name = name.substring(0,name.lastIndexOf("."));
      String[] toks = name.split("_");
      if (toks.length > 1){
         // ground truth is embedded
         FindResult r = new FindResult();
         r.x = Integer.parseInt(toks[1]);
         r.y = Integer.parseInt(toks[2]);
         //r.width = Integer.parseInt(toks[3]);
         //r.height = Integer.parseInt(toks[4]);         
         //logger.debug("ground truth extracted from filename: " + r);
         return r;
      }else{
         return null;
      }
   }

   void encodeGroundTruthIntoFilename(){

      FindResult r = groundTruth;
      String rstr = r.x + "_" + r.y + "_" + r.width + "_" + r.height; 

      // /path/to/target_0.png => target_0.png
      String baseName = file.getName();
      // target0_283_40_55_45.png => target0_283_40_55_45
      // target0.png => target0
      baseName = baseName.substring(0,baseName.lastIndexOf("."));
      
      // target0_283_40_55_45 => target0
      // target0 => target0      
      String[] toks = baseName.split("_");
      String prefix = toks[0];
      
      // target0 => target0_40_40_80_80.png 
      String newName = prefix + "_" + rstr + ".png";

      String parent = file.getParent();
      File newFile = new File(parent, newName);
      
      logger.info("renaming to " + newFile.getName());
      
      file.renameTo(newFile);
      file = newFile;
   }

   */
   /*
   FindResult computeGroundTruthUsingExactMatch() {      
      try {
         logger.debug("compute ground truth");
         BufferedImage inputImage = ImageIO.read(testImage.file);
         BufferedImage targetImage = ImageIO.read(this.file);      
         FindResult r = BaseTemplateFinder.findGroundTruthTopMatch(inputImage, targetImage, 0.95);

         groundTruth = r;
         encodeGroundTruthIntoFilename();

         return r;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }
   */

   public BufferedImage getScreenImage() throws IOException {
      return testImage.getImage();
   }
   
   
   private List<Point> getEncodedGroundTruthLocationsFromFilename(){
      List<Point> ps = new ArrayList<Point>();
      
      
      String name = file.getName();
      name = name.substring(0,name.lastIndexOf("."));
      String[] toks = name.split("_");
      
      for (int i=1; i < toks.length; i = i + 2){
         // ground truth is embedded
         Point p = new Point();
         p.x = Integer.parseInt(toks[i]);
         p.y = Integer.parseInt(toks[i+1]);
         ps.add(p);
      }
      
      return ps;
   }
   
   private String getEncodeGroundTruthLocationsFilename(){

      //FindResult r = groundTruth;
      
      // (20,20) (40,40) => _20_20_40_40
      String rstr = "";
      for (Point p : groundTruthLocations) {      
         rstr = rstr + "_" + p.x + "_" + p.y;      
      }
      

      // /path/to/target_0.png => target_0.png
      String baseName = file.getName();
      // target0_283_40.png => target0_283_40
      // target0.png => target0
      baseName = baseName.substring(0,baseName.lastIndexOf("."));
      
      // target0_283_40 => target0
      // target0 => target0      
      String[] toks = baseName.split("_");
      String prefix = toks[0];
      
      // target0 => target0_20_20_40_40.png 
      String newName = prefix + rstr + ".png";

      return newName;

   }
   
   public void clear(){
      groundTruthLocations.clear();
   }
   
   public void save() {
      String newName = getEncodeGroundTruthLocationsFilename();
      
      String parent = file.getParent();
      File newFile = new File(parent, newName);      
      //logger.info("renaming to " + newFile.getName());
      
      file.renameTo(newFile);
      file = newFile;
   }

}
