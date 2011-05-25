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

public class ImageLocatorTest 
{
    @Test
    public void test_existing_local_img() throws Exception
    {
       ImageLocator locator = new ImageLocator();
       locator.addImagePath("test-res");
       String fname = locator.locate("network.png");
       assertTrue((new File(fname)).exists());
    }

    @Test(expected=FileNotFoundException.class)
    public void test_not_existing_local_img() throws Exception
    {
       ImageLocator locator = new ImageLocator();
       String fname = locator.locate("some-not-exist-image.png");
    }

    @Test
    public void test_existing_remote_img() throws Exception
    {
       ImageLocator locator = new ImageLocator();
       try{
          String fname = locator.locate("apple.png");
          for(String path:locator.getImagePath())
             locator.removeImagePath(path);
          fname = locator.locate("apple.png");
          fail("locator should throw FileNotFoundException, not " + fname);
       }
       catch(FileNotFoundException e){
         assertNotNull(e);
       }
       String url = "http://sikuli.org/examples/clickapple.sikuli";
       locator.addImagePath(url);
       String fname = locator.locate("apple.png");
       assertTrue((new File(fname)).exists());
    }

    @Test
    public void test_cache() throws Exception
    {
       String url = "http://sikuli.org/examples/clickapple.sikuli";
       ImageLocator locator = new ImageLocator();
       locator.addImagePath(url);
       String fname = locator.locate("apple.png");
       String fname2 = locator.locate(url+"/apple.png");
       assertEquals(fname, fname2);
    }

}



