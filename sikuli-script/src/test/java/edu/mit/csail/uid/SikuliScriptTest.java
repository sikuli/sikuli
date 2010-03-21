package edu.mit.csail.uid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.event.InputEvent;
import java.util.Date;
import javax.swing.*;

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
        return new TestSuite( RegionTest.class );
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
