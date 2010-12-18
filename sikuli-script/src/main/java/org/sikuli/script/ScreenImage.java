package org.sikuli.script;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class ScreenImage {
   public int x, y, w, h;
   protected Rectangle _roi;
   protected BufferedImage _img;
   protected String _filename = null; 

   public ScreenImage(Rectangle roi, BufferedImage img){
      _img = img;
      _roi = roi;
      x = (int)roi.getX();
      y = (int)roi.getY();
      w = (int)roi.getWidth();
      h = (int)roi.getHeight();
   }

   /**
    * lazy method: creates the file only when needed.
    */
   public String getFilename() throws IOException{ 
      if(_filename == null){
         File tmp = File.createTempFile("sikuli-scr-",".png");
         tmp.deleteOnExit();
         ImageIO.write(_img, "png", tmp);
         _filename = tmp.getAbsolutePath();
      }
      return _filename;
   }
   
   public BufferedImage getImage(){
      return _img;
   }

   public Rectangle getROI(){
      return _roi;
   }
}
