/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.io.*;
import java.awt.Window;
import javax.swing.JWindow;
//import com.sun.awt.AWTUtilities;

public class LinuxUtil implements OSUtil {

   public int switchApp(String appName, int winNum){
      try{
         String cmd[] = {"wmctrl", "-a", appName};
         Debug.history("switchApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         Debug.error("switchApp: " + e.toString());
         return -1;
      }
   }

   public int switchApp(String appName){
      return switchApp(appName, 0);
   }

   public int openApp(String appName){
      try{
         Debug.history("openApp: " + appName);
         String cmd[] = {"sh", "-c", "("+ appName + ") &\necho -n $!"};
         Process p = Runtime.getRuntime().exec(cmd);

	 InputStream in = p.getInputStream();
	 byte pidBytes[]=new byte[64];
	 int len=in.read(pidBytes);
	 String pidStr=new String(pidBytes,0,len);
	 int pid=Integer.parseInt(new String(pidStr));
         p.waitFor();
	 return pid;
         //return p.exitValue();
      }
      catch(Exception e){
         Debug.error("openApp, crash: " + e.toString());
         return 0;
      }
   }


   public int closeApp(String appName){
      try{
         String cmd[] = {"killall", appName};
         Debug.history("closeApp: " + appName);
         Process p = Runtime.getRuntime().exec(cmd);
         p.waitFor();
         return p.exitValue();
      }
      catch(Exception e){
         Debug.error("closeApp: " + e.toString());
         return -1;
      }
   }

   private enum SearchType {
      APP_NAME,
      WINDOW_ID,
      PID
   };
   public Region getFocusedWindow(){
      String cmd[] = {"xdotool", "getactivewindow"};
      try {
	 Process p = Runtime.getRuntime().exec(cmd);
	 InputStream in = p.getInputStream();
	 BufferedReader bufin = new BufferedReader(new InputStreamReader(in));
	 String str = bufin.readLine();
	 long id=Integer.parseInt(str);
	 String hexid=String.format("0x%08x",id);
         return findRegion(hexid,0,SearchType.WINDOW_ID);
      } catch(IOException e) {
	 System.err.println("xdotool Error:"+e.toString());
	 return null;
      }
   }
   public Region getWindow(String appName){
      return getWindow(appName, 0);
   }

   private Region findRegion(String appName, int winNum,SearchType type){
      String winLine[]=findWindow(appName,winNum,type);
      return new Region(
	 Integer.parseInt(winLine[3]),
	 Integer.parseInt(winLine[4]),
	 Integer.parseInt(winLine[5]),
	 Integer.parseInt(winLine[6])
	 );
   }

   private String[] findWindow(String appName, int winNum,SearchType type){
      String found[]=null;
      int numFound=0;
      try {
	 String cmd[] = {"wmctrl", "-lpGx"};
	 Process p = Runtime.getRuntime().exec(cmd);
	 InputStream in = p.getInputStream();
	 BufferedReader bufin = new BufferedReader(new InputStreamReader(in));
	 String str;

	 int slash=appName.lastIndexOf("/");
	 if(slash>=0) {
	    // remove path: /usr/bin/....
	    appName=appName.substring(slash+1);
	 }

	 if(type==SearchType.APP_NAME) {
	    appName=appName.toLowerCase();
	 }
	 while ((str = bufin.readLine()) !=null) {
	    String winLine[]=str.split("\\s+");
	    boolean ok=false;

	    if(type==SearchType.WINDOW_ID) {
	       if(appName.equals(winLine[0])) {
                  ok=true;
	       }
	    } else if(type==SearchType.PID) {
	       if(appName.equals(winLine[2])) {
                  ok=true;
	       }
	    } else if(type==SearchType.APP_NAME) {
	       String pidFile="/proc/"+winLine[2]+"/status";
	       char buf[]=new char[1024];
	       FileReader pidReader=null;
	       try {
		  pidReader = new FileReader(pidFile);
		  pidReader.read(buf);
		  String pidName=new String(buf);
		  String nameLine[]=pidName.split("[:\n]");
		  String name=nameLine[1].trim();
		  if(name.equals(appName)) {
		     ok=true;
		  }

	       } catch(FileNotFoundException e) {
		 // pid killed before we could read /proc/
	       } finally {
		 if(pidReader!=null) pidReader.close();
	       }

	       if(!ok && winLine[7].toLowerCase().indexOf(appName)>=0) {
		  ok=true;
	       } 
	    }

	    if(ok) {
	       if(numFound>=winNum) {
		  found=winLine;
		  break;
	       }
	       numFound++;
	    }
	 }
	 in.close();
	 p.waitFor();
      } catch(Exception e){
	 Debug.error("findWindow Error:"+e.toString());
         return null;
      }
      return found;
   }

   public Region getWindow(String appName, int winNum){
      return findRegion(appName,winNum,SearchType.APP_NAME);
   }

   public Region getWindow(int pid){
      return getWindow(pid,0);
   }

   public Region getWindow(int pid, int winNum){
      return findRegion(""+pid,winNum,SearchType.PID);
   }

   public int closeApp(int pid){
      Debug.log("close: " + pid);
      String winLine[]=findWindow(""+pid,0,SearchType.PID);
      if(winLine==null) return -1;
      Debug.log("winLine " + winLine[0]);
      String cmd[] = {"wmctrl", "-ic", winLine[0]};
      try {
	 Process p = Runtime.getRuntime().exec(cmd);
	 p.waitFor();
	 return p.exitValue();
      } catch(Exception e) {
	 Debug.error("closeApp Error:"+e.toString());
	 return -1;
      }
   }

   public int switchApp(int pid, int num){
      String winLine[]=findWindow(""+pid,num,SearchType.PID);
      if(winLine==null) return -1;
      String cmd[] = {"wmctrl", "-ia", winLine[0]};
      try {
	 Process p = Runtime.getRuntime().exec(cmd);
	 p.waitFor();
	 return p.exitValue();
      } catch(Exception e) {
	 Debug.error("closeApp Error:"+e.toString());
	 return -1;
      }
   }

   public void setWindowOpacity(Window win, float alpha){
      //AWTUtilities.setWindowOpacity(win, alpha);
   }

   public void setWindowOpaque(Window win, boolean opaque){
      //AWTUtilities.setWindowOpaque(win, opaque);
   }


   public void bringWindowToFront(Window win, boolean ignoreMouse){}
} 


