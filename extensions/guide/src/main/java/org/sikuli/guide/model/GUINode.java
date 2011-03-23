package org.sikuli.guide.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import org.sikuli.guide.SikuliGuide;
import org.sikuli.guide.SikuliGuideArrow;
import org.sikuli.guide.SikuliGuideComponent;
import org.sikuli.guide.SikuliGuideImage;
import org.sikuli.guide.SikuliGuideRectangle;
import org.sikuli.script.Debug;
import org.sikuli.script.ImageLocator;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class GUINode extends DefaultMutableTreeNode {

   String name;
   Pattern pattern;
   Match match = null; // store the most recently found match on the screen
   ArrayList<String> tags = new ArrayList<String>();
   
   public GUINode(Pattern ptn){
      super();
      this.pattern = ptn;
   }
   
   public Match getMatch(){
      return match;
   }
   
   public String getName(){
      return name;
   }

   public void setName(String name){
      this.name = name;
   }
   
   public String getPathString(){
      
      if (!isRoot()){

         GUINode parent = (GUINode) getParent();

         if (isLeaf()) {         
            return parent.getPathString() + name;
         }else{
            return parent.getPathString() + name + " > ";
         }
         
      } else {
         
         return "";
         
      }
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
   
   public void drawPathFromAncestor(SikuliGuide g, GUINode ancestor){

      ArrayList<GUINode> path = new ArrayList<GUINode>();
      
      
      // collect nodes along the path to an array
      Enumeration<GUINode> e = pathFromAncestorEnumeration(ancestor);
      
      while (e.hasMoreElements()){
         GUINode node = (GUINode) e.nextElement();
         path.add(node);
      }
      
      
      
      Match m = ancestor.getMatch();
      
      SikuliGuideRectangle match = new SikuliGuideRectangle(g,m);
      g.addComponent(match);

      int ox = m.x;
      int oy = m.y;
      
      SikuliGuideComponent previous = match;
      
      for (int i = 1; i < path.size(); ++i){
         
         
         ox += previous.getWidth();
         oy += previous.getHeight();
         
         // add spacing between nodes
         ox += 5;
         oy += 10;        
         
         GUINode node = path.get(i);
         SikuliGuideImage current = null;
         try {
            ImageLocator imgLocator = new ImageLocator();
            String full_image_filename = imgLocator.locate(node.getPattern().getFilename());
            current = new SikuliGuideImage(g,full_image_filename);
         } catch (IOException e1) {
         }

         current.setLocation(ox,oy);
         g.addComponent(current);
         
         
         Rectangle r1 = previous.getBounds();
         Rectangle r2 = current.getBounds();

         
         // draw an elbow between the two
         Point p1 = new Point(r1.x + r1.width/2, r1.y + r1.height); // center bottom        
         Point p2 = new Point(r2.x, r2.y + r2.height/2); // left middle
         
         // give some margin between the arrow head and the pointed image
         p2.x -= 5;
         
         SikuliGuideArrow arrow = new SikuliGuideArrow(g, p1, p2);
         arrow.setStyle(SikuliGuideArrow.ELBOW_Y);
         g.addComponent(arrow);
         
         
         
         previous = current;
         
      }
      
      
   }
   
   public GUINode findAncestorOnScreen(){
      
      GUINode parent = (GUINode) getParent();
      
      if (parent == null){
         Debug.info("no more parent!");   
         return null;
      }
      
      Debug.info("trying to find: " + parent);
      Match m = parent.findOnScreen();
      
      if (m == null){
         return parent.findAncestorOnScreen();
      }else {
         parent.match = m;
         Debug.info("found!");
         return parent;
      }

   }
   
}
