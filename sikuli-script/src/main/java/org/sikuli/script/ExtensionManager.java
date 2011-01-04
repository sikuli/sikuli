package org.sikuli.script;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

class Extension implements Serializable{
   public String name;
   public String url;

   public Extension(String name_, String url_){
      name = name_;
      url = url_;
   }
}

public class ExtensionManager {
   protected final static String EXT_LIST_FILE = ".ext-list";
   protected static ExtensionManager _instance = null;
   protected File _extListFile;
   protected ArrayList<Extension> _extensions;

   public static ExtensionManager getInstance(){
      if(_instance == null)
         _instance = new ExtensionManager();
      return _instance;
   }

   protected void readExtList() {
      try{
         FileInputStream fis = new FileInputStream(_extListFile);
         ObjectInputStream in = new ObjectInputStream(fis);
         _extensions = (ArrayList<Extension>)in.readObject();
         in.close();
      }
      catch(Exception e){
         _extensions = new ArrayList<Extension>();
      }
   }

   protected void updateExtList() throws IOException, ClassNotFoundException{
      FileOutputStream fos = new FileOutputStream(_extListFile);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(_extensions);
      out.close();
   }

   protected ExtensionManager(){
      _extListFile = new File(getUserExtPath(),EXT_LIST_FILE);
      readExtList();
   }

   protected void addExtension(String name, String url){
      Extension ext = new Extension(name, url);
      _extensions.add(ext);
      try{
         updateExtList();
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   public boolean isInstalled(String name){
      for(Extension e : _extensions){
         if(e.name.equals(name))
            return true;
      }
      return false;
   }

   /**
    *  install a Sikuli extension (.JAR)
    */
   public boolean install(String name, String url_){
      String extPath = getUserExtPath(); 
      String tmpdir = System.getProperty("java.io.tmpdir");
      try{
         URL url = new URL(url_);
         File localFile = new File(Util.downloadURL(url, tmpdir));
         if( !localFile.renameTo(new File(extPath,localFile.getName())) ){
            Debug.error("Failed to install " + localFile.getName() + " to " + extPath);
            return false;
         }
         addExtension(name, url_);
      }
      catch(IOException e){
         Debug.error("Failed to download " + url_);
         return false;
      }
      return true;
   }

   /**
    *  returns the absolute path to the user's extension path
    */
   public String getUserExtPath(){
      String ret = Env.getSikuliDataPath() + File.separator + "extensions";
      File f = new File(ret);
      if(!f.exists())
        f.mkdirs(); 
      return ret;
   }
}
