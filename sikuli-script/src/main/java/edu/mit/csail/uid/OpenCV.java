package edu.mit.csail.uid;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;

public class OpenCV {
   public static BufferedImage createBufferedImage(int w, int h)
   {
      ComponentColorModel cm = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
            false,  //no alpha channel
            false,  //not premultiplied
            ColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE); //important - data in the buffer is saved by the byte

      SampleModel sm = cm.createCompatibleSampleModel(w, h);
      DataBufferByte db = new DataBufferByte(w*h*3); //3 channels buffer
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

}

