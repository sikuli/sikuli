package org.sikuli.script;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

class Extension implements Serializable{
   public String name;
   public String url;
   public String version;

   public Extension(String name_, String url_, String version_){
      name = name_;
      url = url_;
      version = version_;
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
   
   
   protected Extension find(String name){
      for(Extension e : _extensions){
         if(e.name.equals(name))
            return e;
      }
      return null;
   }

   protected void addExtension(String name, String url, String version){
      
      // remove the previously installed extension of the same name
      Extension old_ext = find(name);
      if (old_ext != null)
         _extensions.remove(old_ext);
      
      Extension ext = new Extension(name, url, version);
      _extensions.add(ext);

      try{
         updateExtList();
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }
   
   
   public String getVersion(String name){
      Extension e = find(name);
      if (e != null){
         return e.version;
      }else{
         return   null;
      }
   }
   
   public boolean isOutOfDate(String name, String version){
      Extension e = find(name);
      if (e == null){
         return false;
      }else{
         String s1 = normalisedVersion(e.version); // installed version
         String s2 = normalisedVersion(version);  // version number to check
         int cmp = s1.compareTo(s2);
         return cmp < 0;            
      }
   }

   public boolean isInstalled(String name){
      Extension e = find(name);
      return e != null;
   }

   private String getExtName(String nameWithVer){
      int verSep = nameWithVer.lastIndexOf("-");
      if(verSep>=0){
         return nameWithVer.substring(0, verSep) + ".jar";
      }
      return nameWithVer;
   }

   /**
    *  install a Sikuli extension (.JAR)
    */
   public boolean install(String name, String url_, String version_){
      String extPath = getUserExtPath(); 
      String tmpdir = System.getProperty("java.io.tmpdir");
      try{
         URL url = new URL(url_);
         File localFile = new File(Util.downloadURL(url, tmpdir));
         String extName = getExtName(localFile.getName());
         File targetFile = new File(extPath, extName);
         if(targetFile.exists())
            targetFile.delete();
         if( !localFile.renameTo(targetFile) ){
            Debug.error("Failed to install " + localFile.getName() + " to " + extPath);
            return false;
         }
         addExtension(name, url_, version_);
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
   
   
  
  public static String normalisedVersion(String version) {
      return normalisedVersion(version, ".", 4);
  }

  public static String normalisedVersion(String version, String sep, int maxWidth) {
      String[] split = java.util.regex.Pattern.compile(sep, java.util.regex.Pattern.LITERAL).split(version);
      StringBuilder sb = new StringBuilder();
      for (String s : split) {
          sb.append(String.format("%" + maxWidth + 's', s));
      }
      return sb.toString();
  }

}
