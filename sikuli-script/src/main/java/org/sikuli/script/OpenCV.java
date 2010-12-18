package org.sikuli.script;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;

public class OpenCV {
   public static BufferedImage createBufferedImage(int w, int h)
   {
      ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
      int[] nBits = {8, 8, 8, 8};
      ColorModel cm = new ComponentColorModel(cs, nBits, 
                                    true, false,
                                    Transparency.TRANSLUCENT, 
                                    DataBuffer.TYPE_BYTE);

      SampleModel sm = cm.createCompatibleSampleModel(w, h);
      DataBufferByte db = new DataBufferByte(w*h*4); //4 channels buffer
      WritableRaster r = WritableRaster.createWritableRaster(sm, db, new Point(0,0));
      BufferedImage bm = new BufferedImage(cm,r,false,null);
      return bm;
   }

   public static byte[] convertBufferedImageToByteArray(BufferedImage img){
      BufferedImage cvImg = createBufferedImage(img.getWidth(), img.getHeight());
      Graphics2D g = cvImg.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
      return ((DataBufferByte)cvImg.getRaster().getDataBuffer()).getData();
   }

   public static Mat convertBufferedImageToMat(BufferedImage img){
      byte[] data = convertBufferedImageToByteArray(img);
      return Vision.createMat(img.getHeight(), img.getWidth(), data);
   }

}

