package org.sikuli.script;

public class SikuliException extends Exception {
   protected String _name = "SikuliException";

   public SikuliException(String msg){
      super(msg);
   }

   public String toString(){
      String ret = _name + ": " + getMessage() + "\n";
      for(StackTraceElement elm : getStackTrace()){
         if(elm.getClassName().startsWith("org.python.pycode")){
            ret += "  Line " + elm.getLineNumber() + 
                   ", in file " + elm.getFileName() + "\n";
            return ret;
         }
      }
      ret += "Line ?, in File ?";
      return ret;
   }
}


