package edu.mit.csail.uid;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import javax.imageio.*;



public class Utils {

   public static int stopRecorder(){
      try{
         String cmd[] = {"sh", "-c", "ps aux | grep MacRecorder | awk '{print $2}' | xargs kill"};
         Process p = Runtime.getRuntime().exec(cmd);         
         p.getInputStream().close();
         p.getOutputStream().close();
         p.getErrorStream().close();
         p.waitFor();
         return p.exitValue();
      } 
      catch(Exception e){
         return -1; 
      }
   }

   public static int runRecorder(){
      try{
         String cmd[] = {"MacRecorder.app/Contents/MacOS/MacRecorder", "no-play"};         
         Process p = Runtime.getRuntime().exec(cmd);         
         p.getInputStream().close();
         p.getOutputStream().close();
         p.getErrorStream().close();
         p.waitFor();
         return p.exitValue();
      } 
      catch(Exception e){
         return -1; 
      }
   }

   public static String slashify(String path, boolean isDirectory) {
      String p = path;
      if (File.separatorChar != '/')
         p = p.replace(File.separatorChar, '/');
      if (!p.endsWith("/") && isDirectory)
         p = p + "/";
      return p;
   }
   
   public static String saveTmpImage(BufferedImage img){
      File tempFile;
      try{
         tempFile = File.createTempFile("sikuli-tmp", ".png" );
         tempFile.deleteOnExit();
         ImageIO.write(img, "png", tempFile);
         return tempFile.getAbsolutePath();
      }
      catch(IOException e){
         e.printStackTrace();
      }
      return null;
   }

   public static void mkdir(String path){
      File f = new File(path);
      if( !f.exists() )
         f.mkdir();
   }
   
   public static String saveImage(BufferedImage img, String bundlePath){
      //String filename = System.getProperty("user.dir") + "/captureImages/";
      String fullpath = bundlePath;
      File f = new File(fullpath);
      if( !f.exists() ) f.mkdir();
      String filename = (new Date()).getTime() + ".png";
      fullpath += filename;
	  fullpath = fullpath.replaceAll("\\\\","/");
      try{
         ImageIO.write(img, "png", new File(fullpath));
      }
      catch(IOException e){
         e.printStackTrace();
         return null;
      }
      return filename;
   }


   public static File createTempDir() {
      final String baseTempPath = System.getProperty("java.io.tmpdir");

      Random rand = new Random();
      int randomInt = 1 + rand.nextInt();

      File tempDir = new File(baseTempPath + File.separator + "tmp-" + randomInt + ".sikuli");
      if (tempDir.exists() == false) {
         tempDir.mkdir();
      }

      tempDir.deleteOnExit();

      return tempDir;
   }

   public static void xcopy(String src, String dest) throws IOException{
      File fSrc = new File(src);
      File fDest = new File(dest);
      if(fSrc.isDirectory()){
         if(!fDest.exists()) fDest.mkdir();
         String[] children = fSrc.list();
         for(String child : children){
            xcopy(src + File.separator + child, dest + File.separator + child);
         } 
      }
      else{
         if( fDest.isDirectory() )
            dest += File.separator + fSrc.getName();
         InputStream in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dest);

         // Copy the bits from instream to outstream
         byte[] buf = new byte[1024];
         int len;
         while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
         }
         in.close();
         out.close();         
      }
   }

   public static String convertStreamToString(InputStream is) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
         while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return sb.toString();
   }

   public static String convertKeyToText(int code, int mod){
      String txtMod = KeyEvent.getKeyModifiersText(mod);
      String txtCode = KeyEvent.getKeyText(code);
      String ret;
      if( txtCode.equals("Ctrl") || txtCode.equals("Alt") || 
          txtCode.equals("Windows") || txtCode.equals("Shift") ||
          txtCode.equals("\u2303") || txtCode.equals("\u2325") || 
          txtCode.equals("\u2318") || txtCode.equals("\u21E7") )
         ret = txtMod;
      else
         ret = txtMod + " " + txtCode;
      return ret;
   }
}
