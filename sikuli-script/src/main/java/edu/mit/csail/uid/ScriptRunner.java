package edu.mit.csail.uid;

import java.io.*;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;

public class ScriptRunner {
   private java.util.List<String> _headers;

   public ScriptRunner(){
      init();
   }

   void init(){
      String[] h = new String[]{
         "from __future__ import with_statement",
         "from sikuli.Sikuli import *",
/*
         "from sikuli import Sikuli",
         "import __main__",
         "__main__.screen = Screen()",
         "__main__.screen._exposeAllMethods()",
         "print dir()",
*/
         "setThrowException(True)",
         "setShowActions(False)"
      };
      _headers = new LinkedList<String>(Arrays.asList(h));
   }

   public void addHeader(String line){
      _headers.add(line);
   }

   public void runPython(String bundlePath, File pyFile, String[] args) throws IOException{
      PythonInterpreter.initialize(System.getProperties(),null, args);
      PythonInterpreter py = new PythonInterpreter();
      Iterator<String> it = _headers.iterator();
      while(it.hasNext()){
         String line = it.next();
         py.exec(line);
      }
      py.exec("setBundlePath('" + bundlePath + "')");
      py.execfile(pyFile.getAbsolutePath());
      py.cleanup();
   }

   public void runPython(String bundlePath) throws IOException {
      File pyFile = getPyFrom(bundlePath);
      runPython(bundlePath, pyFile, null);
   }

   private File getPyFrom(String bundlePath) throws IOException{
      String name = new File(bundlePath).getName();
      String prefix = name.substring(0, name.lastIndexOf('.'));
      return new File(bundlePath + "/"+ prefix + ".py");
   }
}

