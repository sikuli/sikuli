package org.sikuli.ide;

import junit.framework.TestCase;

import java.io.*;
import org.sikuli.ide.AutoUpdater;
import org.sikuli.ide.IDESettings;

public class AutoUpdaterTest extends TestCase {

   AutoUpdater au;
   File updateFile;
   String RC2Version = "X-1.0rc2";
   String RC3Version = "X-1.0rc3 (r840)";
   String RC4Version = "X-1.0rc4 (r900)";
   String OfficialVersion = "X.1.0 (r1000)";

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

   public void testRC2Upgrade() throws IOException{
      IDESettings.SikuliVersion = RC2Version;
      createVersion(RC3Version);
      assertTrue(au.checkUpdate());

      IDESettings.SikuliVersion = RC2Version;
      createVersion(RC4Version);
      assertTrue(au.checkUpdate());

      IDESettings.SikuliVersion = RC2Version;
      createVersion(OfficialVersion);
      assertTrue(au.checkUpdate());

   }


   public void testR827() throws IOException{
      IDESettings.SikuliVersion = "r827"; // newer than RC2, older than RC3.
      createVersion(RC3Version);
      assertTrue(au.checkUpdate());

      createVersion(OfficialVersion);
      assertTrue(au.checkUpdate());

      createVersion(RC2Version);
      assertFalse(au.checkUpdate());
   }

   public void testR900() throws IOException{
      IDESettings.SikuliVersion = "r900"; // equals to RC4

      createVersion(RC2Version);
      assertFalse(au.checkUpdate());

      createVersion(RC3Version);
      assertFalse(au.checkUpdate());

      createVersion(RC4Version);
      assertFalse(au.checkUpdate());

      createVersion(OfficialVersion);
      assertTrue(au.checkUpdate());
   }

   public void testRC3Upgrade() throws IOException{
      IDESettings.SikuliVersion = RC3Version;

      createVersion(RC4Version);
      assertTrue(au.checkUpdate());

      createVersion(OfficialVersion);
      assertTrue(au.checkUpdate());
   }


}


