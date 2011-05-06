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

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.guide.util.SortedListModel;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.sikuli.script.TransparentWindow;


public class SearchDialog extends JFrame implements ActionListener, DocumentListener, KeyListener{

   String message;
   String title = null;
   
   int response;
   JLabel messageLabel;

   String command = null;
   Robot robot;
   
   public void setTitle(String title){
      this.title = title;
      updateMessageLabel();
   }
   
   public String getActionCommand(){
      return command;
   }
   
   class Button extends JButton{

      public Button(String text){
         super(text);
         Font f = new Font("sansserif", Font.BOLD, 14);
//         setBackground(new Color(0.2F,0.2f,0.2f));
//         setForeground(Color.white);
         setFont(f);
         setFocusable(false);
      }
   }

   ArrayList<SearchEntry> entries = new ArrayList<SearchEntry>();
   public void addEntry(String key, Region region){
      entries.add(new SearchEntry(key,region));
   }
   
   public void addEntry(SearchEntry entry){
      entries.add(entry);
   }
   
   Button next;
   Button prev;
   Button close;
   Box button_row;
   JTextField searchTerm;
   SikuliGuide guide;
   //JComboBox termList;
   JTextField termList;
   JList termList1;
   SortedListModel model;
   

  
   
   public SearchDialog(SikuliGuide guide, String message_){
      //super(owner_);
      this.guide = guide;
      
      // this allows the window to be dragged to another location on the screen
      ComponentMover cm = new ComponentMover();
      cm.registerComponent(this);

      
      setVisible(false);
      setUndecorated(true);   
      
      // these are meant to prevent the message box from stealing
      // focus when it's clicked, but they don't seem to work
//      setFocusableWindowState(false);
//      setFocusable(false);
      try {
         robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }
      
      setBackground(Color.black);
      
      //this.owner = owner_;
      this.message = message_;

      //setMinimumSize(new Dimension(200,50));
//      setFocusable(true);

      Container panel = this.getContentPane();
      //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setLayout(new BorderLayout());
      //(panel, BoxLayout.Y_AXIS));
      
      messageLabel = new JLabel();   
      updateMessageLabel();
      
      Box row1 = new Box(BoxLayout.X_AXIS);
      //row1.add(messageLabel);

      JLabel l = new JLabel("Sikuli");
      l.setForeground(Color.white);
      searchTerm = new JTextField();
      searchTerm.setColumns(20);
      searchTerm.setFocusable(true);
      searchTerm.setEnabled(true);
      
      
      searchTerm.addActionListener(this);
      searchTerm.addKeyListener(this);
      searchTerm.getDocument().addDocumentListener(this);

      row1.add(l);
      row1.add(searchTerm);

      
      //termList = new JComboBox();
     // termList = new JTextField();
      //termList.setPreferredSize(new Dimension(0,0));
      //termList.setEditable(true);
      //termList.addKeyListener(this);
      //termList.getEditor().
      
      model = new SortedListModel();
      termList1 = new JList(model);
      termList1.setMinimumSize(new Dimension(100,20));
      
      next = new Button("Next");
  //    next.setActionCommand(NEXT);
      prev = new Button("Previous");
    //  prev.setActionCommand(PREVIOUS);
      close = new Button("Close");
      //close.setActionCommand(CLOSE);
      
      
      button_row = new Box(BoxLayout.X_AXIS); 
    //  setButtons(button_row, style);
     // button_row.add(searchTerm);  
      
//      button_row.add(termList);
      
      panel.add(row1, BorderLayout.NORTH);
      panel.add(button_row);
      //panel.add(termList);
      panel.add(termList1, BorderLayout.SOUTH);

      next.addActionListener(this);
      prev.addActionListener(this);
      close.addActionListener(this);
      
     

   //   setVisible(false);
     // setUndecorated(true);
      
    
      
      pack();

   }
   
   @Override
   public void requestFocus(){
      searchTerm.requestFocus();
   }
   
   void updateMessageLabel(){
      String html = generateHTML(-1);
     
      messageLabel.setText(html);
      
      // hack to limit the width of the text to 300px      
      Dimension size = messageLabel.getPreferredSize();
      if (size.width > 300){
         html = generateHTML(300);
         messageLabel.setText(html);
      }
      
      setAlwaysOnTop(true);
      pack();
      //setLocationRelativeTo((Component) owner);
   }
   
   String generateHTML(int width){
      
      String html; 
      
      if (width < 0){         
         html = "<html><div>";
      }else{
         html = "<html><div style='width:"+width+"'>";        
      }
      
      if (title != null){
         html += "<div style='font-size:10px;color:white;background-color:#333333;padding:2px;'>" + title + "</div>";
      }      
      
      html += "<div style='font-size:12px;color:white;padding:2px;'>" + message + "</div>";
      
      html += "</div></html>";
      
      return html;
   }
   
   public void setStyle(int style){
      button_row.removeAll();
      setButtons(button_row, style);
      setAlwaysOnTop(true);
      pack();
      //setLocationRelativeTo((Component) owner);
//      dialog.pack();
//      dialog.setLocationRelativeTo(this);
   }
   
   private void setButtons(Container c, int style){
      if (style == SikuliGuide.FIRST){
         c.add(next);
         c.add(close);
      } else if (style == SikuliGuide.MIDDLE){
         c.add(prev);
         c.add(next);
         c.add(close);    
      }else if (style == SikuliGuide.LAST){
         c.add(prev);
         c.add(close);         
      }else if (style == SikuliGuide.SIMPLE){
         c.add(next);
      }
   }
   
   
   private ArrayList<SearchEntry> candidateEntries = new ArrayList<SearchEntry>();

   protected void candidateEntriesUpdated(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries){
      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }
         
      //guide.clear();
      guide.updateSpotlights(regions);
   }
   
   void updateCandidateEntries(String search_prefix){
      String term = searchTerm.getText();
      
      

      model.removeAllElements();      
      model.clear();
      candidateEntries.clear();
      
      if (term.length() > 0){

         // filtering
         for (SearchEntry entry : entries){
            
            
            if (entry.key.length() < term.length()){
               continue;
            }


            String prefix = entry.key.substring(0, term.length());

            if (prefix.compareToIgnoreCase(term)==0){
               model.addElement(entry);
               candidateEntries.add(entry);
               Debug.log(entry.key + " " + entry.region);
            }

         }
         
         
      }
      
      termList1.clearSelection();
      
      pack();
      candidateEntriesUpdated(guide, candidateEntries);
   }
   
   
   @Override
   public void actionPerformed(ActionEvent e) {
      
      if (e.getSource() == searchTerm){
         String text = searchTerm.getText();
         Debug.log(searchTerm.getText());
      }
      
      
      command = e.getActionCommand();
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
      updateCandidateEntries(searchTerm.getText());
   }
   

   @Override
   public void insertUpdate(DocumentEvent e) {
      updateCandidateEntries(searchTerm.getText());
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
      updateCandidateEntries(searchTerm.getText());
   }
   
   
   void showAllCandidates(){
      String term = searchTerm.getText();
      if (term.length() == 0 && model.getSize() == 0){
         for (SearchEntry entry : entries){
            model.addElement(entry);
            candidateEntries.add(entry);
         }
         pack();
      }
   }
   
   SikuliGuideCircle circle;
   protected void candidateEntrySelected(SikuliGuide guide, ArrayList<SearchEntry> candidateEntries, SearchEntry selectedEntry){

      ArrayList<Region> regions = new ArrayList<Region>();
      for (SearchEntry entry : candidateEntries){
         regions.add(entry.region);
      }      
      
      Region r = selectedEntry.region;

      Debug.log("selected region:" + r);

      guide.updateSpotlights(regions);
      
      if (circle != null){
         guide.removeComponent(circle);
      }
      
      circle = new SikuliGuideCircle(r);
      guide.addToFront(circle);

      guide.repaint();
   }

   
   String _selectedKey;
   
   protected void entrySelected(SearchEntry selectedEntry){

      Region r = selectedEntry.region;
      _selectedKey = selectedEntry.key;

      
      try {
         
         Location loc = r.getCenter();
         guide.getRegion().click(loc, InputEvent.BUTTON1_MASK);
         // TODO: this will fail because it's an event dispatch thread and waitIdle can not
         // be called by click()
      } catch (FindFailed e) {
      } catch (Exception e){            
      }
      
      setVisible(false);

      
      synchronized(guide){
         guide.notify();           
      }
   }
   
   
   @Override
   public void keyPressed(KeyEvent k) {
      if (k.getKeyCode() == KeyEvent.VK_DOWN){
         
         if (candidateEntries.isEmpty()){
            showAllCandidates();         
            return;
         }
         
         int i = termList1.getSelectedIndex() + 1;
         if (i >= model.getSize())            
            return;

         termList1.setSelectedIndex(i);
         final SearchEntry selectedEntry = (SearchEntry) model.getElementAt(i);
         Thread t = new Thread(){
            public void run(){
               candidateEntrySelected(guide, candidateEntries, selectedEntry);
            }
         };
         t.start();
         
      }else if (k.getKeyCode() == KeyEvent.VK_UP){         
         int i = termList1.getSelectedIndex() - 1;
         if (i < 0)
            return;

         termList1.setSelectedIndex(i);
         final SearchEntry selectedEntry = (SearchEntry) model.getElementAt(i);
         
         Thread t = new Thread(){
            public void run(){
               candidateEntrySelected(guide, candidateEntries, selectedEntry);
            }
         };
         t.start();
         
      }else if (k.getKeyCode() == KeyEvent.VK_ENTER){
         int i = termList1.getSelectedIndex();
         SearchEntry selectedEntry = (SearchEntry) model.getElementAt(i);
         entrySelected(selectedEntry);  
         
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         setVisible(false);
         synchronized(guide){
            guide.notify();           
         }
      }
    
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }

   public String getSelectedKey() {
      return _selectedKey;
   }
   
}


