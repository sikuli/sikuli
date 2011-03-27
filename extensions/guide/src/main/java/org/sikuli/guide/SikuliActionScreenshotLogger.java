package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.SikuliAction;
import org.sikuli.script.SikuliActionListener;
import org.sikuli.script.SikuliActionManager;

public class SikuliActionScreenshotLogger implements SikuliActionListener {

   // keep track of the number of actions already logged
   int _counter = 0;
   
   String _outputPath = ".";
   
   Dimension neighborhoodSize = null;
   
   public String getOutputPath() {
      return _outputPath;
   }

   public void setOutputPath(String outputPath) {
      this._outputPath = outputPath;
   }

   public SikuliActionScreenshotLogger(){
      SikuliActionManager.getInstance().addListener(this);
   }
   
   void drawTarget(Graphics g, Location target){
      Graphics2D g2d = (Graphics2D) g;
      
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

      int cx = target.x;
      int cy = target.y;
      
      g2d.setColor(Color.red);
      Color color_list[] = {Color.red, Color.white, Color.red};
      int radius_list[] = {5,3,1};
      for (int i=0; i<3; ++i){
         
         int radius = radius_list[i];
         Color color = color_list[i];
         
         g2d.setColor(color);
         g2d.fillOval(cx - radius, cy - radius, radius*2, radius*2);            
      }
   }
   
   void drawAction(Graphics g, String action_text, Match match){
      Graphics2D g2d = (Graphics2D) g;
            
      // TODO: Fix this
//       SikuliGuideRectangle rect = new SikuliGuideRectangle(match.getRect());
//       rect.paintAnnotation(g2d);

      Location l = match.getBottomLeft();
      
      // TODO: replace this text with new tooltip element
//      AnnotationToolTip text = new AnnotationToolTip(action_text, l);
//      text.paintAnnotation(g2d);
      
      drawTarget(g2d, match.getTarget());
      
      
      // if neighborhood size is defined, crop the neighborhood
      // around the match, with the match centered in the cropped image
      
      // if the neighborhood size is large than the screen image,
      // do nothing, simply use the whole screen image.
      
      // compute a rectangle centered on the match
      
      // if some part of this rectangle is outside of the image, move
      // this rectangle toward inside to align with the closest corner
      // of the image.
   
   }
   
   @Override
   public void targetClicked(SikuliAction action) {

      BufferedImage image = action.getScreenImage().getImage();
      Match match = action.getMatch();
      
      drawAction(image.getGraphics(), "click", match);
      
      
      
      
      // prepare a blank bitmap with the desired dimension
      
      // copy the cropped part of the source image to this bitmap
      
      saveActionImage(image);
   }

   @Override
   public void targetDoubleClicked(SikuliAction action) {
      BufferedImage image = action.getScreenImage().getImage();
      Match match = action.getMatch();
      
      drawAction(image.getGraphics(), "double-click", match);
      saveActionImage(image);

      
   }

   @Override
   public void targetRightClicked(SikuliAction action) {
      BufferedImage image = action.getScreenImage().getImage();
      Match match = action.getMatch();
      
      drawAction(image.getGraphics(), "right-click", match);
      saveActionImage(image);

      
   }
   
   void saveActionImage(BufferedImage image){
      
      
      String fmtString = "yyyyddhhmmss";
      
      DateFormat sdf = new SimpleDateFormat(fmtString);
      String dateString = sdf.format(new Date());
      
      //
      // Create an instance of SimpleDateFormat with the specified
      // format.
      //
//      DateFormat sdf = new SimpleDateFormat(fmtString);
//      try {
//          // 
//          // The get the date object from the string just called the 
//          // parse method and pass the time string to it. The method 
//          // throws ParseException if the time string is in an 
//          // invalid format. But remember as we don't pass the date 
//          // information this date object will represent the 1st of
//          // january 1970.
//          Date date = sdf.parse(time);            
//          System.out.println("Date and Time: " + date);
//      } catch (Exception e) {
//          e.printStackTrace();
//      }

      
      try {         
         String dest = _outputPath + File.separator + _counter + "_" + dateString + ".png";
         File file = new File(dest);
         ImageIO.write(image, "png", file);
         _counter = _counter + 1;
     } catch (IOException e) {
     }
   }

}
