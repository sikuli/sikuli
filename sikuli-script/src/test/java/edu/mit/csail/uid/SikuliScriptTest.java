package edu.mit.csail.uid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.event.InputEvent;
import java.util.Date;

/**
 * Unit test for simple SikuliScript.
 */
public class SikuliScriptTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SikuliScriptTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SikuliScriptTest.class );
    }

    // assume the tester is on a mac
    public void testRegion() throws Exception
    {
      Screen scr = new Screen();
      Match ret = scr.find("test-res/apple.png");
      assertEquals(ret.x,16);
      assertEquals(ret.y,0);
      Region region = new Region(10,0,200,200);
      ret = region.find("test-res/apple.png");
      assertEquals(ret.x,16);
      assertEquals(ret.y,0);
    }


    // assume the tester is on a mac
    public void testRegionClick() throws Exception
    {
      Screen scr = new Screen();
      int ret = scr.click("test-res/apple.png", 0);
      assertTrue(ret==1);
    }

    // assume the tester is on a mac
    public void testRegionClickOffset() throws Exception
    {
      Screen scr = new Screen();
      Pattern ptn = new Pattern("test-res/apple.png").targetOffset(30,-2);
      int ret = scr.click(ptn, 0);
      assertTrue(ret==1);
    }

    public void testRegionFind() throws Exception
    {
      Screen scr = new Screen();
      scr.setAutoWaitTimeout(2);
      scr.setThrowException(true);
      long begin = (new Date()).getTime();
      boolean gotFindFailed = false;
      try{
         scr.find("test-res/google.png");
      }
      catch(FindFailed e){
         gotFindFailed = true;
      }
      long end = (new Date()).getTime();
      assertTrue(gotFindFailed);
      assertTrue(end-begin >= 2000);

      scr.setAutoWaitTimeout(0);
      scr.setThrowException(false);
      begin = (new Date()).getTime();
      scr.find("test-res/google.png");
      end = (new Date()).getTime();
      assertTrue(end-begin < 2000);
    }

    /**
     * Rigourous Test :-)
     */
    public void _testClick() throws Exception
    {
       SikuliScript script = new SikuliScript();
       int ret = script.click("test-res/apple.png", 0);
       System.out.println( "[click] returns: " + ret);
       assertTrue(ret>0);
    }

    public void _testPaste() throws Exception
    {
       SikuliScript script = new SikuliScript();
       script.switchApp("TextEdit.app");
       script.wait("test-res/textedit-untitled.png", 5000);
       int ret = script.paste(null, "test paste 中文字");
       System.out.println( "[paste] returns: " + ret);
       assertTrue(ret>0);
    }

    public void _testFind() throws Exception
    {
       SikuliScript script = new SikuliScript();
       script.switchApp("Firefox.app");
       Thread.sleep(3000);
       script.type(null, "t", InputEvent.META_MASK);
       script.type(null, "google.com\n");
       Matches ret = script.find("test-res/google.png");
       System.out.println( "[click] returns: " + ret.size());
       assertTrue(ret.size()==1);
    }

    public void _testPrompt() throws Exception {
       SikuliScript script = new SikuliScript();
       String file = script.capture();
       System.out.println("capture: " + file);
       assertNotNull(file);
    
    }
}
