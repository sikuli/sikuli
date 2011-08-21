/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.*;
import java.io.IOException;
import java.util.prefs.*;
import com.apple.eawt.*;

import org.sikuli.script.Debug;

// http://lists.apple.com/archives/mac-games-dev/2001/Sep/msg00113.html
// full key table: http://www.mactech.com/articles/mactech/Vol.04/04.12/Macinkeys/
// modifiers code: http://www.mactech.com/macintosh-c/chap02-1.html

public class NativeLayerForMac implements NativeLayer {

   public void initIDE(final SikuliIDE ide){
   }

   public void initApp(){
      Application app = Application.getApplication();
      app.addPreferencesMenuItem();
      app.setEnabledPreferencesMenu(true);
      app.addApplicationListener(
         new ApplicationAdapter() {
            public void handleOpenApplication(ApplicationEvent event){
               Debug.info("open application: Sikuli-IDE");
               System.setProperty("apple.laf.useScreenMenuBar", "true");
               System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Sikuli IDE");
            }

            public void handleOpenFile(ApplicationEvent evt) {
               final String fname = evt.getFilename();
               Debug.log(1, "opening " + fname);
               if(fname.endsWith(".skl")){
                  SikuliIDE._runningSkl = true;
                  Thread t = new Thread() {
                     public void run() {
                        try{
                           SikuliIDE.runSkl(fname, null); 
                        }
                        catch(IOException e){
                           e.printStackTrace();
                        }
                     }
                  };
                  t.setDaemon(false);
                  t.start();
               }
               else if(fname.endsWith(".sikuli")){
                  SikuliIDE ide = SikuliIDE.getInstance(null);
                  ide.loadFile(fname);
               }
            }

            public void handlePreferences(ApplicationEvent evt){
               Debug.log(1, "opening preferences setting");
               SikuliIDE ide = SikuliIDE.getInstance();
               ide.showPreferencesWindow();
            }

            public void handleQuit(ApplicationEvent event){
               SikuliIDE.getInstance().quit();
            }
         }
      ); 
   }

}


