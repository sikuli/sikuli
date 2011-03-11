/**
 * 
 */
package org.sikuli.guide;

import org.sikuli.script.Region;

class SearchEntry implements Comparable{
   SearchEntry(String key, Region region){
      this.key = key;
      this.region = region;
      this.name = key;
   }
   String key;
   Region region;
   String name;
   @Override
   
   public int compareTo(Object e) {
      return name.compareTo(((SearchEntry) e).name);
   }      
   
   public String toString(){
      return name;
   }
}