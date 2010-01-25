/*
 * $Id: JniExtractor.java 61337 2006-05-21 23:32:09Z richardv $
 *
 * Copyright 2006 MX Telecom Ltd.
 */

package com.wapmx.nativeutils.jniloader;

import java.io.File;
import java.io.IOException;

/**
 * @author Richard van der Hoff <richardv@mxtelecom.com>
 */
public interface JniExtractor {
    /**
     * extract a JNI library to a temporary file
     *
     * @param libname   - "System.loadLibrary()"-compatible library name
     * @return the extracted file
     * @throws IOException
     */
    public File extractJni(String libname) throws IOException;
}
