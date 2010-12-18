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
import javax.swing.*;

import org.python.util.PythonInterpreter;

import org.sikuli.script.ScriptRunner;

public class ConsolePane extends JPanel implements Runnable
{
   static boolean ENABLE_IO_REDIRECT = true;
   static {
      String flag = System.getProperty("sikuli.console");
      if (flag != null && flag.equals("false"))
         ENABLE_IO_REDIRECT = false;
   }

   final static int NUM_PIPES = 2;
   private JTextArea textArea;
   private Thread[] reader = new Thread[NUM_PIPES];
   private boolean quit;
               
   private final PipedInputStream[] pin=new PipedInputStream[NUM_PIPES]; 

   Thread errorThrower; // just for testing (Throws an Exception at this Console
   
   public ConsolePane()
   {
      super();
      textArea=new JTextArea();
      textArea.setEditable(false);
      
      setLayout(new BorderLayout());
      add(new JScrollPane(textArea),BorderLayout.CENTER);
      
      
      if(ENABLE_IO_REDIRECT){
         for(int i=0;i<NUM_PIPES;i++)
            pin[i] = new PipedInputStream();
         System.out.println("Redirect stdout/stderr to console.");


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
            textArea.append("Couldn't redirect STDOUT to this console\n"+io.getMessage());
         }
         catch (SecurityException se)
         {
            textArea.append("Couldn't redirect STDOUT to this console\n"+se.getMessage());
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
            textArea.append("Couldn't redirect STDERR to this console\n"+io.getMessage());
         }
         catch (SecurityException se)
         {
            textArea.append("Couldn't redirect STDERR to this console\n"+se.getMessage());
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
                  textArea.append(input);
                  textArea.setCaretPosition(textArea.getText().length());
               }
               if (quit) return;
            }
         }
      
      } 
      catch (Exception e)
      {
         textArea.append("\nConsole reports an Internal error.");
         textArea.append("The error is: "+e);         
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
