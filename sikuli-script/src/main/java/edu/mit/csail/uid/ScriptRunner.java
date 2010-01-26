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
         "from python.edu.mit.csail.uid.Sikuli import *",
         "setThrowException(False)",
         "setShowActions(False)"
      };
      _headers = new LinkedList<String>(Arrays.asList(h));
   }

   public void runPython(String dotSikuliPath) throws IOException{
      PythonInterpreter py = new PythonInterpreter();
      Iterator<String> it = _headers.iterator();
      while(it.hasNext())
         py.exec(it.next());
      py.exec("setBundlePath('" + dotSikuliPath + "')");
      File pyFile = getPyFrom(dotSikuliPath);
      py.execfile(pyFile.getAbsolutePath());
   }

   private File getPyFrom(String dotSikuliPath) throws IOException{
      String name = new File(dotSikuliPath).getName();
      String prefix = name.substring(0, name.lastIndexOf('.'));
      return new File(dotSikuliPath + "/"+ prefix + ".py");
   }
}

