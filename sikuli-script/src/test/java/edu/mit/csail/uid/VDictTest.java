package edu.mit.csail.uid;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class VDictTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public VDictTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( VDictTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void test_vdict_init()
    {
       VDictProxy dict = new VDictProxy();
       assertTrue(dict.size() == 0);
       assertTrue(dict.empty() == true);
    }

    public void test_vdict_insert()
    {
       VDictProxy<Integer> dict = new VDictProxy();
       assertTrue(dict.size() == 0);
       assertTrue(dict.empty() == true);
       dict.insert("test-res/1.png", 100);
       assertTrue(dict.size() == 1);
       assertTrue(dict.empty() == false);
       int val = dict.lookup("test-res/1.png" );
       assertTrue(val == 100);
    }

    public void test_vdict_lookup()
    {
       VDictProxy<Integer> dict = new VDictProxy();
       dict.insert("test-res/1.png", 100);
       Integer val = dict.lookup("test-res/2.png" );
       assertTrue(val == null);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == 100 );
    }


    public void test_vdict_lookup_obj()
    {
       VDictProxy<String> dict = new VDictProxy();
       dict.insert("test-res/1.png", "hello world");
       String r = dict.lookup_similar("test-res/1.png", 0.8);
       assertEquals(r, "hello world");
    }


    public void test_vdict_erase()
    {
       VDictProxy<Integer> dict = new VDictProxy();
       dict.insert("test-res/1.png", 100);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == 100 );
       dict.erase("test-res/1.png");
       assertTrue(dict.size() == 0);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == null );
    }


    public void test_vdict_lookup_n()
    {
       VDictProxy<Integer> dict = new VDictProxy();
       dict.insert("test-res/1.png", 1);
       dict.insert("test-res/2a.png", 2);
       dict.insert("test-res/2b.png", 3);
       List<Integer> vals;
       assertTrue(dict.lookup_similar_n("test-res/big.png", 0.8, 1).size()==0);
       assertTrue(dict.lookup_similar_n("test-res/2.png", 0.8, 1).size()==1);
       vals = dict.lookup_similar_n("test-res/2.png", 0.8, 2);
       assertTrue(vals.size()==2);
       assertTrue(vals.get(0) == 2);
       assertTrue(vals.get(1) == 3);
       vals = dict.lookup_similar_n("test-res/2.png", 0.8, 0);//all
       assertTrue(vals.size()==2);
       assertTrue(vals.get(0) == 2);
       assertTrue(vals.get(1) == 3);
    }

}
