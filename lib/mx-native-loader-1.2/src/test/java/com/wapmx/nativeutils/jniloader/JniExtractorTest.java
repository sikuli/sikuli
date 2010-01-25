/*
 * $Id: JniExtractorTest.java 61352 2006-06-07 18:55:14Z richardv $
 *
 * Copyright 2006 MX Telecom Ltd.
 */

package com.wapmx.nativeutils.jniloader;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Richard van der Hoff <richardv@mxtelecom.com>
 */
public class JniExtractorTest extends TestCase {
    public void testExtract() throws IOException {
        File output = new File("target/extractortest/foo/outputtest");
        output.delete();
        new File("target/extractortest/foo").delete();
        new File("target/extractortest").delete();
        assertFalse(output.exists());
        
        System.setProperty("java.library.tmpdir", "target/extractortest/foo");
        System.setProperty("java.library.debug", "1");
        
        DefaultJniExtractor jniExtractor = new DefaultJniExtractor();
        jniExtractor.extractResource("com/wapmx/nativeutils/jniloader/JniExtractorTest.class", "outputtest");
        assertTrue(output.exists());
    }
}
