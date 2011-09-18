/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import javax.imageio.*;

import org.sikuli.script.Debug;
import org.sikuli.script.Util;


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

   public static boolean isWindows(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("windows" ) )
         return true;
      return false;
   }

   public static boolean isLinux(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("linux" ) )
         return true;
      return false;
   }
   
   public static boolean isMacOSX(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("mac os x" ) )
         return true;
      return false;
   }


   public static void zip(String path, String outZip) 
                                    throws IOException, FileNotFoundException {
      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZip)); 
      zipDir(path, zos);
      zos.close();
   }

   public static void unzip(String zip, String path)
                                    throws IOException, FileNotFoundException {
      final int BUF_SIZE = 2048;
      FileInputStream fis = new FileInputStream(zip);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while((entry = zis.getNextEntry()) != null) {
         int count;
         byte data[] = new byte[BUF_SIZE];
         FileOutputStream fos = new FileOutputStream(
               new File(path, entry.getName()));
         BufferedOutputStream dest = new BufferedOutputStream(fos, BUF_SIZE);
         while ((count = zis.read(data, 0, BUF_SIZE)) != -1) {
            dest.write(data, 0, count);
         }
         dest.close();
      }
      zis.close();
   }

   public static void zipDir(String dir, ZipOutputStream zos) throws IOException
   { 
      File zipDir = new File(dir); 
      String[] dirList = zipDir.list(); 
      byte[] readBuffer = new byte[1024]; 
      int bytesIn = 0; 
      for(int i=0; i<dirList.length; i++) { 
         File f = new File(zipDir, dirList[i]); 
         /*
         if(f.isDirectory()) { 
            String filePath = f.getPath(); 
            zipDir(filePath, zos); 
            continue; 
         } 
         */
         if(f.isFile()) { 
            FileInputStream fis = new FileInputStream(f); 
            ZipEntry anEntry = new ZipEntry(f.getName()); 
            zos.putNextEntry(anEntry); 
            while((bytesIn = fis.read(readBuffer)) != -1) { 
               zos.write(readBuffer, 0, bytesIn); 
            } 
            fis.close(); 
         }
      } 
   }

   public static String slashify(String path, boolean isDirectory) {
      return Util.slashify(path, isDirectory);
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

   public static boolean rename(String oldFile, String newFile){
      File old = new File(oldFile);
      return old.renameTo(new File(newFile));
   }

   public static String getName(String filename){
      File f = new File(filename);
      return f.getName();
   }

   public static boolean exists(String path){
      File f = new File(path);
      return f.exists();
   }

   public static void mkdir(String path){
      File f = new File(path);
      if( !f.exists() )
         f.mkdir();
   }

   public static String getTimestamp(){
      return (new Date()).getTime() + "";
   }

   protected static String getAltFilename(String filename){
      int pDot = filename.lastIndexOf('.');
      int pDash = filename.lastIndexOf('-');
      int ver = 1;
      String postfix = filename.substring(pDot);
      String name;
      if(pDash >= 0){
         name = filename.substring(0, pDash);
         ver = Integer.parseInt(filename.substring(pDash+1, pDot));
         ver++;
      }
      else
         name = filename.substring(0, pDot);
      return name + "-" + ver + postfix;
   }
   
   public static String saveImage(BufferedImage img, String filename, String bundlePath){
      final int MAX_ALT_NUM = 100;
      String fullpath = bundlePath;
      File path = new File(fullpath);
      if( !path.exists() ) path.mkdir();
      if(!filename.endsWith(".png"))
         filename += ".png";
      File f = new File(path, filename);
      int count = 0;
      while( f.exists() && count < MAX_ALT_NUM){
         Debug.log( f.getName() + " exists");
         f = new File(path, getAltFilename(f.getName()));
         count++;
      }
      if(count >= MAX_ALT_NUM)
         f = new File(path, getTimestamp() + ".png");
      fullpath = f.getAbsolutePath();
      fullpath = fullpath.replaceAll("\\\\","/");
      try{
         ImageIO.write(img, "png", new File(fullpath));
      }
      catch(IOException e){
         e.printStackTrace();
         return null;
      }
      return fullpath;
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

   /**
    *  Copy a file *src* to the path *dest* and check if the file name conflicts. 
    *  If a file with the same name exists in that path, rename *src* to an alternative name.
    */
   public static File smartCopy(String src, String dest) throws IOException{
      File fSrc = new File(src);
      String newName = fSrc.getName();
      File fDest = new File(dest, newName);
      if(fSrc.equals(fDest))
         return fDest;
      while(fDest.exists()){
         newName = getAltFilename(newName);
         fDest = new File(dest, newName);
      }
      xcopy(src, fDest.getAbsolutePath());
      if(fDest.exists())
         return fDest;
      return null;
   }

   public static void xcopy(String src, String dest) throws IOException{
      File fSrc = new File(src);
      File fDest = new File(dest);
      if(fSrc.getAbsolutePath().equals(fDest.getAbsolutePath()))
         return;
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
