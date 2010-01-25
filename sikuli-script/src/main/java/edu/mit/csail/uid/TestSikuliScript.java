package edu.mit.csail.uid;

import java.io.*;

class TestSikuliScript {

   static boolean testOCR(SikuliScript script) throws Exception{
      Matches matches = script._find("Skype", "testimages/mac-desktop.png");
      System.out.println( "[find] number of matches: " + matches.size() );
      System.out.println( "[find] match[0]: " + matches.getFirst());
      return (matches.size()>0);
   }

   static boolean testFind(SikuliScript script) throws Exception{
      Matches matches = script._find("/tmp/crash1.png", "/tmp/crash-screen.png");
      System.out.println( "[find] number of matches: " + matches.size() );
      System.out.println( "[find] match[0]: " + matches.getFirst());
      return (matches.size()>0);
   }

   static boolean testClick(SikuliScript script) throws Exception{
      int ret = script.click("testimages/mac-disk.jpg", 0);
      System.out.println( "[click] returns: " + ret);
      return (ret>0);
   }

   static boolean testClickAll(SikuliScript script) throws Exception{
      int ret = script.clickAll("testimages/mac-disk.jpg", 0);
      System.out.println( "[click] returns: " + ret);
      return (ret>0);
   }

   static boolean testDoubleClick(SikuliScript script) throws Exception{
      int ret = script.doubleClick("testimages/mac-disk.jpg", 0);
      System.out.println( "[doubleClick] returns: " + ret);
      return (ret>0);
   }

   static boolean testDoubleClickAll(SikuliScript script) throws Exception{
      Matches matches = script.find("testimages/mac-disk.jpg");
      int ret = script.doubleClickAll(matches, 0);
      System.out.println( "[doubleClickAll] returns: " + ret);
      return (ret>0);
   }

   static boolean testRightClick(SikuliScript script) throws Exception{
      int ret = script.rightClick("testimages/mac-disk.jpg", 0);
      System.out.println( "[rightClick] returns: " + ret);
      return (ret>0);
   }

   static boolean testDragDrop(SikuliScript script) throws Exception{
      int ret = script.dragDrop("testimages/web-link.png", "testimages/firefox-address-bar.png");
      System.out.println( "[dragDrop] returns: " + ret);
      return (ret>0);
   }

   static boolean testWait(SikuliScript script) throws Exception{
      System.out.println( "[wait] start");
      Matches matches = script.wait("testimages/web-link.png", 10000);
      script.click(matches, 0);
      System.out.println( "[wait] done " + matches.getFirst().score);
      return matches.getFirst().score > 0.4;
   }

   static boolean testType(SikuliScript script) throws Exception{
      int ret = script.type("testimages/firefox-address-bar.png", "WWW.google.com\n");
      System.out.println( "[type] returns: " + ret);
      return (ret>0);
   }

   /*
    * 1. move to the left-top corner
    * 2. wait until the icon appears
    * 3. drag the icon to the left-top (without dropping)
    * 4. find where the firefox address bar is
    * 5. move the mouse to there and drop the icon
    */
   static boolean testTask1(SikuliScript script) throws Exception {
      /*
      script.mouseMove(0,0);
      Matches matches = script.wait("testimages/web-link.png", 10000);
      script.drag(matches.getFirst(), -1280, -800);
      */
      return false;
   }

   static boolean testSubregion(SikuliScript script) throws Exception {
      Matches allDisks = script.find("testimages/mac-disk.jpg");
      Matches MacDisks = allDisks.nearby().find("testimages/MacOSX-text.png");
      System.out.println(MacDisks);
      if(MacDisks.size()>0){
         Match mm = MacDisks.getFirst();
         script.click(mm, 0);
         return true;
      }
      return false;
   }

   static boolean testSwitchApp(SikuliScript si) throws Exception{
      return si.switchApp("System Preferences.app") == 0;
   }

   public static void main(String[] argv){
      try{
         SikuliScript script = new SikuliScript();

         System.out.println("switchApp: " + testSwitchApp(script));

         /*
         testClick(script);
         testOCR(script);
         */

         //testSubregion(script);

         /*
         testFind(script);
         testDoubleClickAll(script);
         */
         /*
         testDragDrop(script);
         testClickAll(script);
         testRightClick(script);
         testWait(script);
         testType(script);
         */
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

}

