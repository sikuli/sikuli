/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

public class ImageLocator {

   Map<URI,String> _cache = new HashMap<URI, String>();
   String _cache_dir;
   String _bundle_path;

   public ImageLocator(String bundlePath){
      _bundle_path = bundlePath;
      String name = "";
      if(bundlePath != null){
         File f = new File(bundlePath);
         name = f.getName() + "/";
      }
      _cache_dir = System.getProperty("java.io.tmpdir")
                   + "/sikuli_cache/" + name;
      File dir = new File(_cache_dir);
      if(!dir.exists())
         dir.mkdir();
      else{
         //TODO: init _cache from the content of the cache dir
      }
   }

   public ImageLocator(){
      this(Settings.BundlePath);
   }

   protected URL getURL(String s){
      try{
         URL url = new URL(s);
         return url;
      }
      catch(MalformedURLException e){
         return null;
      }
   }

   public String locateURL(URL url) throws IOException{
      Debug.log(3, "locateURL " + url);
      try{
         URI uri = url.toURI();
         if(_cache.containsKey(uri))
            return _cache.get(uri);
         String localFile = Util.downloadURL(url, _cache_dir);
         Debug.log(3, "download " + uri + " to local: "  + localFile);
         _cache.put(uri, localFile);
         return localFile;
      }
      catch(java.net.URISyntaxException e){
         Debug.error("URI syntax error: " + url + ", " + e.getMessage());
         return null;
      }
      catch(IOException e){
         //e.printStackTrace();
         throw e;
      }
   }

   public static void addImagePath(String path){
      String imgPath = System.getProperty("SIKULI_IMAGE_PATH");
      if(imgPath != null)
         imgPath += Env.getSeparator() + path;
      else
         imgPath = path;
      System.setProperty("SIKULI_IMAGE_PATH", imgPath);
   }

   protected static String[] splitImagePath(String path){
      path = path.replaceAll("[Hh][Tt][Tt][Pp]://","__http__//");
      path = path.replaceAll("[Hh][Tt][Tt][Pp][Ss]://","__https__//");
      String[] ret = path.split(Env.getSeparator());
      for(int i=0;i<ret.length;i++){
         boolean isURL = false;
         if(ret[i].indexOf("__http__")>=0){
            ret[i] = ret[i].replaceAll("__http__//", "http://");
            isURL = true;
         }
         else if(ret[i].indexOf("__https__")>=0){
            ret[i] = ret[i].replaceAll("__https__//", "https://");
            isURL = true;
         }
         if(isURL){
            if(!ret[i].endsWith("/")) ret[i] += "/";
         }
         else{
            if(!ret[i].endsWith(File.separator))
               ret[i] += File.separator;
         }
      }
      return ret;
   }

   public static void removeImagePath(String path){
      String imgPath = System.getProperty("SIKULI_IMAGE_PATH");
      if(imgPath != null){
         String[] paths = splitImagePath(imgPath);
         StringBuilder filteredPath = new StringBuilder();
         boolean first = true;
         for(String p : paths){
            if(!p.equals(path) && !p.equals(path+File.separator)){
               if(first)
                  first = false;
               else
                  filteredPath.append(Env.getSeparator());
               filteredPath.append(p);
            }
         }
         System.setProperty("SIKULI_IMAGE_PATH", filteredPath.toString());
      }
   }

   public static String[] getImagePath(){
      String sikuli_img_path = "";
      if(System.getenv("SIKULI_IMAGE_PATH") != null)
         sikuli_img_path += System.getenv("SIKULI_IMAGE_PATH");
      if(System.getProperty("SIKULI_IMAGE_PATH") != null){
         if(sikuli_img_path.length()>0 && 
           !sikuli_img_path.endsWith(Env.getSeparator()) )
            sikuli_img_path += Env.getSeparator();
         sikuli_img_path += System.getProperty("SIKULI_IMAGE_PATH");
      }
      if(sikuli_img_path.length() > 0){
         return splitImagePath(sikuli_img_path);
      }
      return new String[]{};
   }

   protected String searchFile(String filename) throws IOException {
      Debug.log("ImageLocator.searchFile: " + filename + " bundle path: " + _bundle_path);
      File f = new File(_bundle_path, filename);
      if( f.exists() ) return f.getAbsolutePath();
      String[] sikuli_img_path = getImagePath();
      for(String path : sikuli_img_path){
         Debug.log("ImageLocator: env+sys path: " + path);
         f = new File(path, filename);
         if( f.exists() ){
            Debug.log("ImageLocator found " + filename + " in " + path);
            return f.getAbsolutePath();
         }
         URL url = getURL(path);
         if(url != null){
            try{
               String ret = locateURL(new URL(url,filename));
               Debug.log(4, "ImageLocator found " + filename + " in " + path);
               return ret;
            }
            catch(IOException e){
               Debug.log(3, "can't find " + filename + " in " + url);
            }
         }
      }
      return null;
   }

   // find the file in the following order:
   // 1. absolute path > 2. the current bundle path >
   // 3. ENV[SIKULI_IMAGE_PATH] > 4. System.getProperty("SIKULI_IMAGE_PATH")
   public String locate(String filename) throws IOException{
      String ret = filename;
      URL url = getURL(filename);
      if( url != null )
         return locateURL(url);

      File f = new File(filename);
      if( f.isAbsolute() ){
         if( f.exists() )
            return filename;
      }
      else{
         Debug.log("search file " + filename);
         ret = searchFile(filename);
         if(ret != null)
            return ret;
      }
      throw new FileNotFoundException("File " + filename + " not exists");
   }
}
