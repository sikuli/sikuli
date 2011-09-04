/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.util.Date;

/**
 * Debug is a utility class that wraps println statements and allows
 * more or less command line output to be turned on.
 * <p>
 * Change the value of the DEBUG_LEVEL constant using a system property:
 * sikuli.Debug. For example, on the command line, use -Dsikuli.Debug=x,
 * or simply -Dsikuli.Debug to enable it at level 0.
 * The log(level, message, ...) method will print to standard output if the
 * level number is less than or equal to the currently set DEBUG_LEVEL.
 */

public class Debug {
   private static final int DEBUG_LEVEL;
   static {
      String debug = System.getProperty("sikuli.Debug");
      if (debug == null) {
         // No system property = disabled
         DEBUG_LEVEL = 1;
      } else if (debug == "") {
         // Empty property = level 0
         DEBUG_LEVEL = 0;
      } else {
         DEBUG_LEVEL = Integer.parseInt(debug);
      }
   }

   private static final int DEFAULT_LEVEL = 1;

   private long _beginTime;

   /** Log message if the log level >= level. Uses printf. */
   public static void log(int level, String prefix, String message, Object... args) {
      if (isEnabled(level)) {
         if(args.length!=0)
            System.out.printf(prefix + message, args);
         else
            System.out.print(prefix + message);
         System.out.println();
      }
   }

   public static void log(int level, String message, Object... args) {
      if(Settings.DebugLogs)
         log(level, "[debug] ", message, args);
   }

   /** @return true if level is being logged. */
   public static boolean isEnabled(int level) {
      return level <= DEBUG_LEVEL;
   }

   /** @return true if the default level is being logged. */
   public static boolean isEnabled() {
      return isEnabled(DEFAULT_LEVEL);
   }

   /** Logs message at the default log level. */
   public static void log(String message, Object... args) {
      log(DEFAULT_LEVEL, message, args);
   }

   public static void history(String message, Object... args) {
      if(Settings.ActionLogs)
         log(-1, "[log] ", message, args);
   }

   public static void info(String message, Object... args) {
      if(Settings.InfoLogs)
         log(-1, "[info] ", message, args);
   }

   public static void error(String message, Object... args) {
      log(-1, "[error] ", message, args);
   }

   public static void profile(String message, Object... args) {
      if(Settings.ProfileLogs)
         log(-1, "[profile] ", message, args);
   }

   public void startTiming(String message){
      if(Settings.ProfileLogs){
         Debug.profile(message + " START");
         _beginTime = (new Date()).getTime();
      }
   }

   public long endTiming(String message){
      if(Settings.ProfileLogs){
         long t = (new Date()).getTime();
         long dt = t - _beginTime;
         Debug.profile(message + " END: " +  dt + "ms");
         return dt;
      }
      return 0;
   }
}
