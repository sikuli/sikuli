package edu.mit.csail.uid;

public class FindFailed extends Exception {
   public FindFailed(String msg){
      super(msg);
   }

   public String toString(){
      String ret = "FindFailed: " + getMessage() + "\n";
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

