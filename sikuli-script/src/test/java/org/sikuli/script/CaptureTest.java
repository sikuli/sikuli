/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.io.*;
import javax.imageio.*;

public class CaptureTest 
{
    @Test
    public void test_capture() throws Exception
    {
       JButtons frame = new JButtons();
       Thread.sleep(500);
       Screen scr = new Screen();
       ScreenImage simg = scr.capture();

       File out = new File("test_capture.png");
       ImageIO.write(simg.getImage(), "png", out);

       Finder finder = new Finder(out.getAbsolutePath());
       Pattern ptn = new Pattern("test-res/network.png");
       finder.find(ptn);
       assertTrue(finder.hasNext());
       Match m = finder.next();
       assertNotNull(m);

       frame.dispose();
    }
}


