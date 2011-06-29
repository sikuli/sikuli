package org.sikuli.script.android;

import com.google.common.base.Predicate;
import org.python.util.PythonInterpreter;
import java.util.Properties;

public class MonkeyPlugin implements Predicate<PythonInterpreter> {
   @Override
   public boolean apply(PythonInterpreter anInterpreter) {
      System.out.println("MonkeyPlugin inited.");

      anInterpreter.exec("from org.sikuli.script import *");

      return false;
   }
}
