package org.sikuli.ide;

import org.apache.commons.cli.*;
import org.sikuli.script.Debug;

public class CommandArgs {
   Options _options;

   public CommandArgs(){ 
      init();
   }

   public CommandLine getCommandLine(String[] args){
      CommandLineParser parser = new PosixParser();
      CommandLine cmd = null;
      try {
         cmd = parser.parse( _options, args, false );
      }
      catch( ParseException exp ) {
         Debug.error( exp.getMessage() );
      }
      return cmd;
   }

   void init(){
      _options = new Options();
      _options.addOption("h", "help", false, "print this help message");
      _options.addOption("s", "stderr", false, "print runtime errors to stderr instead of popping up a message box");
      _options.addOption(
            OptionBuilder.withLongOpt("test")
                         .withDescription("run .sikuli as a unit test case with junit's text UI runner")
                         .hasArg()
                         .withArgName("sikuli-test-case")
                         .create('t') );
      _options.addOption(
            OptionBuilder.withLongOpt("run")
                         .withDescription("run .sikuli or .skl file")
                         .hasArg()
                         .withArgName("sikuli-file")
                         .create('r') );
      _options.addOption(
            OptionBuilder.hasArgs()
                         .withLongOpt("args")
                         .withArgName("arguments")
                         .withDescription("specify the arguments passed to Jython's sys.argv")
                         .create() );

   }

   public void printHelp(){
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Sikuli-IDE", _options, true );
   }
}

