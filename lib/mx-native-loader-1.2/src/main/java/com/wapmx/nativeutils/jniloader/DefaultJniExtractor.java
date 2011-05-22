// $Id: DefaultJniExtractor.java 135270 2008-01-16 16:00:27Z richardv $
package com.wapmx.nativeutils.jniloader;

/*
 * Copyright 2006 MX Telecom Ltd.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Vector;


/**
 * @author Richard van der Hoff <richardv@mxtelecom.com>
 */
public class DefaultJniExtractor implements JniExtractor {
    private static boolean debug = false;

    /**
     * this is where JNI libraries are extracted to.
     */
    private File jniDir = null;
    private static Map _cache;

    
    static {
        // initialise the debug switch
        String s = System.getProperty("java.library.debug");
        if(s != null && (s.toLowerCase().startsWith("y") || s.startsWith("1")))
            debug = true;
        _cache = new HashMap();
        if(debug) System.err.println("cache " + _cache);
    }
    
    /**
     * Gets the working directory to use for jni extraction.
     * <p>
     * Attempts to create it if it doesn't exist.
     *
     * @return jni working dir
     * @throws IOException if there's a problem creating the dir
     */
    public File getJniDir() throws IOException {
       String[] absLibPaths = {
          "/Applications/Sikuli-IDE.app/Contents/Frameworks", 
          System.getenv("SIKULI_HOME") + "libs"
       };
       String[] relLibPaths = {
          "Sikuli-IDE.app/Contents/Frameworks", 
          "libs",
       };
       Vector<String> libPaths = new Vector(Arrays.asList(relLibPaths));
       String isTesting = System.getProperty("sikuli.testing");
       if(isTesting == null || !isTesting.equals("yes")){
          libPaths.addAll(Arrays.asList(absLibPaths));
       }
       if(jniDir == null) {
          for(String path : libPaths){
             jniDir = new File(path);
             if( jniDir.exists() ){
                System.setProperty("java.library.tmpdir", path);
                return jniDir;
             }
          }

          String tmpdir = System.getProperty("java.io.tmpdir") + "/tmplib";
          jniDir = new File( System.getProperty("java.library.tmpdir",tmpdir));
          if(debug)
             System.err.println("Initialised JNI library working directory to '"+jniDir+"'");
       }

       if ( !jniDir.exists() ) {
          if( !jniDir.mkdirs())
             throw new IOException("Unable to create JNI library working directory "+jniDir);
       }
       return jniDir;
    }

    /**
     * extract a JNI library from the classpath
     *
     * @param libname   - System.loadLibrary() - compatible library name
     * @return the extracted file
     * @throws IOException
     */
    public File extractJni(String libname) throws IOException {
        String mappedlib = System.mapLibraryName(libname);

        /* on darwin, the default mapping is to .jnilib; but
         * we use .dylibs so that library interdependencies are handled correctly.
         * if we don't find a .jnilib, try .dylib instead.
         */
        if(mappedlib.endsWith(".jnilib")) {
            if(this.getClass().getClassLoader().getResource("META-INF/lib/"+mappedlib) == null)
                mappedlib = mappedlib.substring(0, mappedlib.length()-7)+".dylib";
        }
        
        if(_cache.containsKey(mappedlib)){
           if(debug) System.err.println("in cache " + mappedlib);
           return (File)_cache.get(mappedlib);
        }
        else{
           if(debug) System.err.println("not in cache " + mappedlib);
           File ret = extractResource("META-INF/lib/"+mappedlib,mappedlib);
           _cache.put(mappedlib, ret);
           return ret;
        }
    }
    
    /**
     * extract a resource to the tmp dir (this entry point is used for unit testing)
     * 
     * @param resourcename      the name of the resource on the classpath
     * @param outputname        the filename to copy to (within the tmp dir)
     * @return  the extracted file
     * @throws IOException
     */
    File extractResource(String resourcename,String outputname) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcename);
        if(in == null)
            throw new IOException("Unable to find library "+resourcename+" on classpath");
        File outfile = new File(getJniDir(),outputname);
        if(debug)
            System.err.println("Extracting '"+resourcename+"' to '"+outfile.getAbsolutePath()+"'");
        //FIXME: check the file utime and determine if it needs overwritten
        if( !outfile.exists() || outfile.getParent().endsWith("tmplib") ){
           OutputStream out = new FileOutputStream(outfile);
           copy(in,out);
           out.close();
        }
        in.close();
        return outfile;
    }

    /**
     * copy an InputStream to an OutputStream.
     *
     * @param in        InputStream to copy from
     * @param out       OutputStream to copy to
     * @throws IOException if there's an error
     */
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
