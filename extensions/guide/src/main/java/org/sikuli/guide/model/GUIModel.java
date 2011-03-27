package org.sikuli.guide.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.sikuli.guide.SikuliGuide;
import org.sikuli.guide.SikuliGuideRectangle;
import org.sikuli.script.Debug;
import org.sikuli.script.Match;

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

   public String removeSpaces(String s) {
      StringTokenizer st = new StringTokenizer(s," ",false);
      String t="";
      while (st.hasMoreElements()) t += st.nextElement();
      return t;
    }
   
   public void drawPathTo(SikuliGuide guide, String search_string){


      Enumeration<GUINode> e = ((GUINode) getRoot()).breadthFirstEnumeration();

      e.nextElement(); // pop the root

      GUINode matched_node = null;
      while (e.hasMoreElements()){
         GUINode node = (GUINode) e.nextElement();

         // fuzzy matching hack
         String s1 = removeSpaces(node.name.toLowerCase());
         String s2 = removeSpaces(search_string.toLowerCase());

         if (Math.abs(s1.length()-s2.length()) < 3 && Distance.LD(s1,s2) < 3){
            matched_node = node;
            break;
         }
      }
      
      Debug.info("matched_node: " + matched_node);

      if (matched_node != null){

         Match m = matched_node.findOnScreen();
         if (m != null){
            
            guide.addComponent(new SikuliGuideRectangle(m));

            
         }else{


            GUINode ancestor = matched_node.findAncestorOnScreen();

            if (ancestor != null){

               matched_node.drawPathFromAncestor(guide, ancestor);
            }

         }
      }

   }

}

class Distance {

   //****************************
   // Get minimum of three values
   //****************************

   static private int Minimum (int a, int b, int c) {
   int mi;

     mi = a;
     if (b < mi) {
       mi = b;
     }
     if (c < mi) {
       mi = c;
     }
     return mi;

   }

   //*****************************
   // Compute Levenshtein distance
   //*****************************

   static public int LD (String s, String t) {
   int d[][]; // matrix
   int n; // length of s
   int m; // length of t
   int i; // iterates through s
   int j; // iterates through t
   char s_i; // ith character of s
   char t_j; // jth character of t
   int cost; // cost

     // Step 1

     n = s.length ();
     m = t.length ();
     if (n == 0) {
       return m;
     }
     if (m == 0) {
       return n;
     }
     d = new int[n+1][m+1];

     // Step 2

     for (i = 0; i <= n; i++) {
       d[i][0] = i;
     }

     for (j = 0; j <= m; j++) {
       d[0][j] = j;
     }

     // Step 3

     for (i = 1; i <= n; i++) {

       s_i = s.charAt (i - 1);

       // Step 4

       for (j = 1; j <= m; j++) {

         t_j = t.charAt (j - 1);

         // Step 5

         if (s_i == t_j) {
           cost = 0;
         }
         else {
           cost = 1;
         }

         // Step 6

         d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

       }

     }

     // Step 7

     return d[n][m];

   }

 }
