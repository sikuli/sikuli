package edu.mit.csail.uid;

import java.io.*;
import java.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;

public class ScriptRunner {
   private static ScriptRunner _instance = null;
   private java.util.List<String> _headers;
   private java.util.List<String> _tmp_headers;
   private PythonInterpreter py; 

   public static ScriptRunner getInstance(String[] args){
      if(_instance == null)
         _instance = new ScriptRunner(args);
      return _instance;
   }

   protected ScriptRunner(String[] args){
      init(args);
   }

   void init(String[] args){
      PythonInterpreter.initialize(System.getProperties(),null, args);
      py = new PythonInterpreter();
      String[] h = new String[]{
         "from __future__ import with_statement",
         "from sikuli.Sikuli import *",
         "setThrowException(True)",
         "setShowActions(False)"
      };
      _headers = new LinkedList<String>(Arrays.asList(h));
      _tmp_headers = new LinkedList<String>();
   }

   public void addTempHeader(String line){
      _tmp_headers.add(line);
   }

   public PythonInterpreter getPythonInterpreter(){
      return py;
   }

   public void runPython(String bundlePath, File pyFile) throws IOException{
      Iterator<String> it = _headers.iterator();
      while(it.hasNext()){
         String line = it.next();
         py.exec(line);
      }
      it = _tmp_headers.iterator();
      while(it.hasNext()){
         String line = it.next();
         py.exec(line);
      }
      py.exec("setBundlePath('" + bundlePath + "')");
      py.execfile(pyFile.getAbsolutePath());
      _tmp_headers.clear();
      //py.cleanup();
   }

   public void runPython(String bundlePath) throws IOException {
      File pyFile = getPyFrom(bundlePath);
      runPython(bundlePath, pyFile);
   }

   private File getPyFrom(String bundlePath) throws IOException{
      String name = new File(bundlePath).getName();
      String prefix = name.substring(0, name.lastIndexOf('.'));
      return new File(bundlePath + "/"+ prefix + ".py");
   }
}

