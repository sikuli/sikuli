package org.sikuli.guide.model;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.sikuli.script.Debug;

public class GUIModel extends DefaultTreeModel {

   public GUIModel(TreeNode root) {
      super(root);
   }

   public ArrayList<GUINode> getNodesByTag(String tag){
      ArrayList<GUINode> nodes = new ArrayList<GUINode>();
          
      Enumeration<GUINode> e = ((GUINode) getRoot()).breadthFirstEnumeration();
      
      while (e.hasMoreElements()){
         GUINode node = (GUINode) e.nextElement();
         if (node.hasTag(tag)){
            nodes.add(node);
         }
      }
      
      return nodes;
   }
   
}
