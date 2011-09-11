/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.net.URL;
import java.io.*;

public class Util {
   final static int DOWNLOAD_BUFFER_SIZE = 153600;

   public static String downloadURL(URL url, String localPath) throws IOException{
      InputStream reader = url.openStream();
      String[] path = url.getPath().split("/");
      String filename = path[path.length-1];
      File fullpath =  new File(localPath, filename);
      FileOutputStream writer = new FileOutputStream(fullpath);
      byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
      int totalBytesRead = 0;
      int bytesRead = 0;
      while ((bytesRead = reader.read(buffer)) > 0){  
         writer.write(buffer, 0, bytesRead);
         totalBytesRead += bytesRead;
      }
      reader.close();
      writer.close();

      return fullpath.getAbsolutePath();
   }

   public static boolean isImageFile(String fname){
      int dot = fname.lastIndexOf('.');
      if(dot<0)
         return false;
      String suffix = fname.substring(dot+1).toLowerCase();
      if(suffix.equals("png") || suffix.equals("jpg"))
         return true;
      return false;
   }

   public static String getParentPath(String path){
      File f = new File(path);
      return f.getParent();
   }
}
