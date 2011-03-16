package org.sikuli.guide;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.sikuli.guide.model.GUIModel;
import org.sikuli.guide.model.GUINode;
import org.sikuli.guide.util.SortedListModel;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;


public class TreeSearchDialog extends SearchDialog{
   
   GUIModel gui;
   
   class TreeSearchEntry extends SearchEntry{
      
      GUINode node;
      public TreeSearchEntry(String key, GUINode node){
         super(null,null);
         this.node = node;
         this.name = node.getName();
         this.key = node.getName();
      }
      
      // this determines what to be displayed in the list
      public String toString(){
         return node.getPathString();
      }
   }
   
   public TreeSearchDialog(SikuliGuide guide, GUIModel gui) {
      super(guide, null);
      this.gui = gui;
      
      GUINode r = (GUINode) gui.getRoot();
      Enumeration<GUINode> e = r.breadthFirstEnumeration();
      
      e.nextElement(); // skip root
      
      while (e.hasMoreElements()){
         GUINode node = (GUINode) e.nextElement();         
         addEntry(new TreeSearchEntry(null, node));
      }
   }

   protected void candidateEntriesUpdated(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries){
      for (SearchEntry se : candidateEntries){
         //TreeSearchEntry tse = (TreeSearchEntry) se; 
         //Debug.info("e: " + tse.node);
      }
      
      if (candidateEntries.isEmpty()){
         guide.clear();
         guide.repaint();
      }
   }
   
   protected void candidateEntrySelected(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry e){
      for (SearchEntry se : candidateEntries){
         //TreeSearchEntry tse = (TreeSearchEntry) se; 
         //Debug.info("e: " + tse.node);
      }
      
      guide.setVisible(false);
      repaint();
      
      Debug.info("selected: " + ((TreeSearchEntry) e).node);
      
      GUINode node =  ((TreeSearchEntry) e).node;
      
      Debug.info("trying to find: " + node);
      Match m = node.findOnScreen();
      
      if (m == null){
         
         Screen s = new Screen();
         m = node.findAncestorOnScreen();
         try {
            guide.focusBelow();
            s.click(m,0);
         } catch (Exception e1) {
         }
         try {
            m = s.wait(node.getPattern());
         } catch (Exception e1) {
         }         
         
         toFront();
         requestFocus();
      }
      
      
      if (m != null){
         guide.setVisible(true);
         guide.clear();
         guide.addComponent(new SikuliGuideRectangle(guide,m));
         guide.addFlag(m.getCenter().left(m.w/2), "      ");
         guide.startAnimation();
         guide.repaint();
      }
      
      
   }
   
   
   protected void entrySelected(SearchEntry selectedEntry){
   }
   

  
}


