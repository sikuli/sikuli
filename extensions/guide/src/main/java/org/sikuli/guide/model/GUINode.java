package org.sikuli.guide.model;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.sikuli.script.Debug;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class GUINode extends DefaultMutableTreeNode {

   String name;
   Pattern pattern;
   ArrayList<String> tags = new ArrayList<String>();
   
   public GUINode(Pattern ptn){
      super();
      this.pattern = ptn;
   }
   
   public String getName(){
      return name;
   }

   public void setName(String name){
      this.name = name;
   }

   
   public Pattern getPattern(){
      return pattern;
   }
   
   public void addTag(String tag){
      tags.add(tag);
   }
   
   public ArrayList<String> getTags(){
      return tags;
   }
   
   public boolean hasTag(String tag){
      for (String t : tags){
         if (t.compareToIgnoreCase(tag) == 0){
            return true;
         }
      }
      return false;
   }
   
   
   String tagsToString(){
      if (tags.isEmpty()){
         return "[]";
      }
      
      String str = null;
      for (String t : tags){
         if (str == null){
            str = "[" + t;
         }else{
            str += "," + t;
         }
      }
      str += "]";
      return str;
   }
   
   public String toString(){
      
      String str;
      
      str = pattern + " name:" + name + ", tags:" + tagsToString();
     
      return str;      
   }
   
   public Match findOnScreen(){
      Screen s = new Screen();
      return s.exists(getPattern(), 0);
   }
   
   public Match findAncestorOnScreen(){
      
      GUINode parent = (GUINode) getParent();
      
      if (parent == null){
         Debug.info("no more parent!");   
         return null;
      }
      
      Debug.info("trying to fin parent: " + parent);
      
//      Screen s = new Screen();
//      Match m  = s.exists(parent.getPattern(), 0);
      Match m = parent.findOnScreen();

      if (m == null){
         return parent.findAncestorOnScreen();
      }else {
         Debug.info("found!");
         return m;
      }

//      else{
//         
//         return ancestor.findA
//      }
//      
//      Screen s = new Screen();
//      Match m;
//      while (true){
//         Debug.info("check: " + ancestor);
//
//
//         if (m != null){
//            Debug.info("found!");     
//            break;
//
//         }else{
//
//            n = (GUINode) n.getParent();            
//            if (n == null){
//               // can not be found in the ancestor path
//               Debug.info("not found!");     
//
//               break;
//            }            
//         }
//      }
  //    return m;
   }
   
}
