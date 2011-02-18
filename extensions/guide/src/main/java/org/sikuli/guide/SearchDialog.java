package org.sikuli.guide;
import java.awt.AWTException;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

   class Entry{
      Entry(String key, Region region){
         this.key = key;
         this.region = region;
      }
      String key;
      Region region;      
   }
   
   ArrayList<Entry> entries = new ArrayList<Entry>();
   public void addEntry(String key, Region region){
      entries.add(new Entry(key,region));
   }
   
   Button next;
   Button prev;
   Button close;
   Box button_row;
   JTextField searchTerm;
   SikuliGuide guide;
   JComboBox termList;
   
   public SearchDialog(SikuliGuide guide, String message_){
      //super(owner_);
      this.guide = guide;
      
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
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      messageLabel = new JLabel();   
      updateMessageLabel();
      
      Box row1 = new Box(BoxLayout.X_AXIS);
      row1.add(messageLabel);

      
      searchTerm = new JTextField();
      searchTerm.setColumns(20);
      searchTerm.setFocusable(true);
      searchTerm.setEnabled(true);
      
      searchTerm.addActionListener(this);
      searchTerm.addKeyListener(this);
      searchTerm.getDocument().addDocumentListener(this);

      
      termList = new JComboBox();
      termList.setPreferredSize(new Dimension(0,0));
      //termList.setEditable(true);
      //termList.addKeyListener(this);
      //termList.getEditor().
      
      next = new Button("Next");
  //    next.setActionCommand(NEXT);
      prev = new Button("Previous");
    //  prev.setActionCommand(PREVIOUS);
      close = new Button("Close");
      //close.setActionCommand(CLOSE);
      
      
      button_row = new Box(BoxLayout.X_AXIS); 
    //  setButtons(button_row, style);
      button_row.add(searchTerm);  
      
      //button_row.add(termList);
      
      panel.add(row1);
      panel.add(button_row);
      panel.add(termList);

      next.addActionListener(this);
      prev.addActionListener(this);
      close.addActionListener(this);
      
      
      pack();

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
   
   
   ArrayList<Region> regions = new ArrayList<Region>();
   ArrayList<Entry> candidateEntries = new ArrayList<Entry>();
   
   void updateCandidateEntries(String search_prefix){
      String term = searchTerm.getText();
      
      termList.removeAllItems();

    
      regions.clear();
      candidateEntries.clear();
      
      if (term.length() > 0){

         // filtering
         for (Entry entry : entries){

            if (entry.key.length() < term.length()){
               continue;
            }


            String prefix = entry.key.substring(0, term.length());

            if (prefix.compareToIgnoreCase(term)==0){
               regions.add(entry.region);           
               termList.addItem(entry.key);  
               candidateEntries.add(entry);
               Debug.log(entry.key + " " + entry.region);
            }

         }

         termList.setPopupVisible(true);
         termList.setSelectedItem(null);
         termList.setSelectedIndex(-1);
      }else{
         termList.setPopupVisible(false);
      }

      guide.clear();
      guide.updateHighlights(regions);

   }
   
   
   @Override
   public void actionPerformed(ActionEvent e) {
      
      if (e.getSource() == searchTerm){
         String text = searchTerm.getText();
         Debug.log(searchTerm.getText());
      }
      
      
      command = e.getActionCommand();
     // dismiss();
      
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
         updateCandidateEntries(searchTerm.getText());
   }
   

   @Override
   public void insertUpdate(DocumentEvent e) {
      updateCandidateEntries(searchTerm.getText());
      //updateCandidateEntries(searchTerm.getText());
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
      updateCandidateEntries(searchTerm.getText());
      //updateCandidateEntries(searchTerm.getText());
   }
   
   
   
   void selectCandidateEntry(int i){
      
      if (i < 0 || i >= candidateEntries.size())
         return;
      
      
      termList.setSelectedIndex(i);
      
      Region r = regions.get(i);
      Debug.log("selected region:" + r);
      guide.clear();
      guide.updateHighlights(regions);
      guide.addRectangle(r);
      
      guide.repaint();
   }

   
   String _selectedKey;
   
   @Override
   public void keyPressed(KeyEvent k) {
      if (k.getKeyCode() == KeyEvent.VK_DOWN){
         Debug.log("down");     
         int i = termList.getSelectedIndex();
         selectCandidateEntry(i+1);         
      }else if (k.getKeyCode() == KeyEvent.VK_UP){         
         int i = termList.getSelectedIndex();
         Debug.log("up");      
         selectCandidateEntry(i-1);         
      }else if (k.getKeyCode() == KeyEvent.VK_ENTER){
         int i = termList.getSelectedIndex();
         
         Entry selectedEntry = candidateEntries.get(i);
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
          
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
      Debug.log("here");      
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
      Debug.log("here");            
   }

   public String getSelectedKey() {
      return _selectedKey;
   }
   
}


