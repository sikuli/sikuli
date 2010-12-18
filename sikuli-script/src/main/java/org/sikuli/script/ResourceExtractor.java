package org.sikuli.script;

import java.io.*;

public class ResourceExtractor {

   /*
    * Assume the list of resources can be found at path/filelist.txt
    * @return the local path to the extracted resources
    */
   public static String extract(String path) throws IOException{
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      InputStream in = cl.getResourceAsStream(path + "/filelist.txt");
      String localPath = System.getProperty("java.io.tmpdir") + "/sikuli/"+ path;
      new File(localPath).mkdirs();
      Debug.log(4, "extract resources " + path + " to " + localPath);
      writeFileList(in, path, localPath);
      return localPath + "/";
   }

   protected static void writeFileList(InputStream ins, String fromPath, String outPath) throws IOException{
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      BufferedReader r = new BufferedReader(new InputStreamReader(ins));
      String line;
      while((line=r.readLine()) != null){
         Debug.log(7, "write " + line);
         if(line.startsWith("./"))
            line = line.substring(1);
         String fullpath = outPath + line;
         File outf = new File(fullpath);
         outf.getParentFile().mkdirs();
         try{
            InputStream in = cl.getResourceAsStream(fromPath + line);
            OutputStream out = new FileOutputStream(outf);
            copy(in, out);
            out.close();
         }
         catch(Exception e){
            Debug.log("Can't extract " + fromPath + line);
         }
      }
   }

   static void copy(InputStream in, OutputStream out) throws IOException {
      byte[] tmp = new byte[8192];
      int len = 0;
      while (true) {
         len = in.read(tmp);
         if (len <= 0) {
            break;
         }
         out.write(tmp, 0, len);
      }
   }
}

