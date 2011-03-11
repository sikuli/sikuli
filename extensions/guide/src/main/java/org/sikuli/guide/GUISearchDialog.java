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

import org.sikuli.guide.util.SortedListModel;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.TransparentWindow;


public class GUISearchDialog extends SearchDialog{
   

   
   public GUISearchDialog(SikuliGuide guide) {
      super(guide, null);
   }

   protected void candidateEntriesUpdated(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries){
      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }
         
      guide.clear();
      for (Region cr : regions){
         guide.addCircle(cr);
      }
      guide.repaint();
   }
   
   protected void candidateEntrySelected(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry e){
      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }
      
      Region r = e.region;

      guide.clear();
      for (Region cr : regions){
         guide.addCircle(cr);
      }
      guide.addFlag(r.getCenter().left(r.w/2),"This");
      guide.startAnimation();
      guide.repaint();
   }
   
   
   protected void entrySelected(SearchEntry selectedEntry){

      Region r = selectedEntry.region;
      
      try {
         
         Location loc = r.getCenter();
         guide.getRegion().hover(loc);
         // TODO: this will fail because it's an event dispatch thread and waitIdle can not
         // be called by click()
      } catch (FindFailed e) {
      } catch (Exception e){            
      }
      
   }
   

  
}


