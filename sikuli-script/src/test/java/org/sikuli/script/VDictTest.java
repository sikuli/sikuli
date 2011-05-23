/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.List;
import java.io.*;

public class VDictTest 
{

    @Test
    public void test_vdict_init()
    {
       VDictProxy dict = new VDictProxy();
       assertTrue(dict.size() == 0);
       assertTrue(dict.empty() == true);
    }

    @Test(expected=FileNotFoundException.class)
    public void test_vdict_insert_not_exist_file() throws FileNotFoundException
    {
       VDictProxy dict = new VDictProxy();
       dict.insert("not-exist-file.png", 1);
    }

    @Test
    public void test_vdict_insert() throws FileNotFoundException
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

    @Test
    public void test_vdict_lookup() throws FileNotFoundException
    {
       VDictProxy<Integer> dict = new VDictProxy();
       dict.insert("test-res/1.png", 100);
       Integer val = dict.lookup("test-res/2.png" );
       assertTrue(val == null);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == 100 );
    }


    @Test
    public void test_vdict_lookup_obj() throws FileNotFoundException
    {
       VDictProxy<String> dict = new VDictProxy();
       dict.insert("test-res/1.png", "hello world");
       String r = dict.lookup_similar("test-res/1.png", 0.8);
       assertEquals(r, "hello world");
    }


    @Test
    public void test_vdict_erase() throws FileNotFoundException
    {
       VDictProxy<Integer> dict = new VDictProxy();
       dict.insert("test-res/1.png", 100);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == 100 );
       dict.erase("test-res/1.png");
       assertTrue(dict.size() == 0);
       assertTrue(dict.lookup_similar("test-res/1.png", 0.8) == null );
    }


    @Test
    public void test_vdict_lookup_n() throws FileNotFoundException
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
