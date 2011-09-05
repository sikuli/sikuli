package org.sikuli.ide.indentation;

import junit.framework.TestCase;

import java.io.*;
import org.sikuli.ide.AutoUpdater;
import org.sikuli.ide.IDESettings;

public class AutoUpdaterTest extends TestCase {

   AutoUpdater au;
   File updateFile;

   public void setUp() throws IOException{
      updateFile = File.createTempFile("sikuli-latest-update", "txt");
      String[] servers = {
         updateFile.toURL().toString()
      };
      au = new AutoUpdater(servers);
   }

   protected void createVersion(String ver) throws IOException{
      FileWriter fw = new FileWriter(updateFile);
      fw.write(ver + "\n" + "some info...");
      fw.close();
   }

   public void testRC2ToRC3() throws IOException{
      IDESettings.SikuliVersion = "X-1.0rc2";
      createVersion("X-1.0rc3");
      assertTrue(au.checkUpdate());
   }
}


