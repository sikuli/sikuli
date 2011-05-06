/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

//
// A simple Java Console for your application (Swing version)
// Requires Java 1.1.5 or higher
//
// Disclaimer the use of this source is at your own risk. 
//
// Permision to use and distribute into your own applications
//
// RJHM van den Bergh , rvdb@comweb.nl

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;


import org.python.util.PythonInterpreter;

import org.sikuli.script.ScriptRunner;
import org.sikuli.script.Debug;

public class ConsolePane extends JPanel implements Runnable
{
   static boolean ENABLE_IO_REDIRECT = true;
   static {
      String flag = System.getProperty("sikuli.console");
      if (flag != null && flag.equals("false"))
         ENABLE_IO_REDIRECT = false;
   }

   final static int NUM_PIPES = 2;
   private JTextPane textArea;
   private Thread[] reader = new Thread[NUM_PIPES];
   private boolean quit;
               
   private final PipedInputStream[] pin=new PipedInputStream[NUM_PIPES]; 

   Thread errorThrower; // just for testing (Throws an Exception at this Console
   
   public ConsolePane()
   {
      super();
      textArea=new JTextPane();
      textArea.setContentType("text/html");
      String css = UserPreferences.getInstance().getConsoleCSS();
      ((HTMLEditorKit)textArea.getEditorKit()).getStyleSheet().addRule(css);
      textArea.setEditable(false);
      
      setLayout(new BorderLayout());
      add(new JScrollPane(textArea),BorderLayout.CENTER);
      
      if(ENABLE_IO_REDIRECT){
         for(int i=0;i<NUM_PIPES;i++)
            pin[i] = new PipedInputStream();
         Debug.log(2,"Redirect stdout/stderr to console.");


         PythonInterpreter py = 
            ScriptRunner.getInstance(null).getPythonInterpreter();
         try
         {
            PipedOutputStream pout=new PipedOutputStream(this.pin[0]);
            PrintStream ps = new PrintStream(pout,true);
            System.setOut(ps);
            py.setOut(ps);
         } 
         catch (java.io.IOException io)
         {
            appendMsg("Couldn't redirect STDOUT to this console\n"+io.getMessage());
         }
         catch (SecurityException se)
         {
            appendMsg("Couldn't redirect STDOUT to this console\n"+se.getMessage());
         } 

         try 
         {
            PipedOutputStream pout=new PipedOutputStream(this.pin[1]);
            PrintStream ps = new PrintStream(pout,true);
            System.setErr(ps);
            py.setErr(ps);
         } 
         catch (java.io.IOException io)
         {
            appendMsg("Couldn't redirect STDERR to this console\n"+io.getMessage());
         }
         catch (SecurityException se)
         {
            appendMsg("Couldn't redirect STDERR to this console\n"+se.getMessage());
         }       


         quit=false; // signals the Threads that they should exit

         // Starting two seperate threads to read from the PipedInputStreams            
         for(int i=0;i<NUM_PIPES;i++){
            reader[i]=new Thread(this);
            reader[i].setDaemon(true);   
            reader[i].start();   
         }
      }
            
   }

   private void appendMsg(String msg){
      HTMLDocument doc = (HTMLDocument)textArea.getDocument();
      HTMLEditorKit kit = (HTMLEditorKit)textArea.getEditorKit();
      try{
         kit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }
   
        /*
   public synchronized void windowClosed(WindowEvent evt)
   {
      quit=true;
      this.notifyAll(); // stop all threads
      try { reader.join(1000);pin.close();   } catch (Exception e){}      
      try { reader2.join(1000);pin2.close(); } catch (Exception e){}
      System.exit(0);
   }      
      
   public synchronized void windowClosing(WindowEvent evt)
   {
      frame.setVisible(false); // default behaviour of JFrame   
      frame.dispose();
   }
        */
   
   static final String lineSep = System.getProperty("line.separator");
   private String htmlize(String msg){
      StringBuffer sb = new StringBuffer();
      Pattern patMsgCat = Pattern.compile("\\[(.+?)\\].*");
      for(String line : msg.split(lineSep)){
         Matcher m = patMsgCat.matcher(line);
         String cls = "normal";
         if(m.matches())
            cls = m.group(1);
         line = "<span class='"+cls+"'>" + line + "</span>";
         sb.append(line + "<br>");
      }
      return sb.toString();
   }

   public synchronized void run()
   {
      try
      {         
         for(int i=0;i<NUM_PIPES;i++){
            while (Thread.currentThread()==reader[i])
            {
               try { this.wait(100);}catch(InterruptedException ie) {}
               if (pin[i].available()!=0)
               {
                  String input=this.readLine(pin[i]);
                  appendMsg(htmlize(input));
                  textArea.setCaretPosition(textArea.getDocument().getLength()-1);
               }
               if (quit) return;
            }
         }
      
      } 
      catch (Exception e)
      {
         appendMsg("\nConsole reports an Internal error.");
         appendMsg("The error is: "+e);         
      }
      
   }
   
   public synchronized String readLine(PipedInputStream in) throws IOException
   {
      String input="";
      do
      {
         int available=in.available();
         if (available==0) break;
         byte b[]=new byte[available];
         in.read(b);
         input=input+new String(b,0,b.length);                                          
      }while( !input.endsWith("\n") &&  !input.endsWith("\r\n") && !quit);
      return input;
   }   

   public void clear(){
      textArea.setText("");
   }
      
}
