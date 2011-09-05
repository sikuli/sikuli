/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 */
package org.sikuli.ide;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.sikuli.ide.indentation.*;
import org.sikuli.ide.*;

/**
 * Unit test for Sikuli IDE
 */
public class SikuliIDETestSuite 
{
    public static Test suite()
    {
       TestSuite suite = new TestSuite();

       // add more tests here
       suite.addTestSuite(PythonIndentationTest.class);
       suite.addTestSuite(PythonStateTest.class);
       suite.addTestSuite(AutoUpdaterTest.class);

       return suite;
    }

    /**
     * Runs the test suite using the textual runner.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

