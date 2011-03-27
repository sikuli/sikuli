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
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.OS;
import org.sikuli.script.Region;
import org.sikuli.script.TransparentWindow;


public class Portal extends JFrame implements ActionListener, KeyListener, Transition{

   Robot robot;
   
   String command;
   public String getActionCommand(){
      return command;
   }
   
   class Item extends JPanel{
      Entry entry;
      public Item(Entry entry){
         this.entry = entry;
         setSelected(false);
      }
      
      public void setSelected(boolean selected){
         if (selected){            
            setBackground(new Color(0.5f,0.5f,0.5f));
         }else{
            setBackground(null);            
         }
      }
      
      public Entry getEntry(){
         return entry;
      }
   }
   
   class Button extends JButton{

      Entry entry;
      public Button(Entry entry){

         super(new ImageIcon(entry.image));         
         
         this.entry = entry;         
         setFont(new Font("sansserif", 
               Font.BOLD, 14));
        // setFocusable(false);

      }
      
      public Entry getEntry(){
         return entry;
      }
   }

   public BufferedImage capture(Rectangle rect) {
      BufferedImage img = null;
      try {
         img = (new Robot()).createScreenCapture(rect);
      } catch (AWTException e) {
      }
      return img;
   }

   
   class Entry{
      Entry(String key, Region region){
         this.key = key;
         this.region = region;
         this.image = capture(region.getRect());
      }
      String key;
      Region region;    
      BufferedImage image;
   }
   
   ArrayList<Entry> entries = new ArrayList<Entry>();
   public void addEntry(String key, Region region){
      
      
      Entry entry = new Entry(key,region); 
      entries.add(entry);
      
      Button btn = new Button(entry);
      btn.addActionListener(this);
      btn.addKeyListener(this);
      
      Item item = new Item(entry);
      item.add(btn);
      //item.addActionListener(this);
      item.addKeyListener(this);
      

      if (entries.size() == 1){
         item.setSelected(true);
      }
      
      getContentPane().add(item);
      pack();
   }
   
   SikuliGuide guide;

   public Portal(SikuliGuide guide){

      this.guide = guide;

      try {
         robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }
      
      setBackground(Color.black);
      setUndecorated(true);      
      
      Container panel = this.getContentPane();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      Border empty = BorderFactory.createEmptyBorder(5,10,5,10);
      ((JComponent) panel).setBorder(empty);
      pack();
//      requestFocus();
//      setFocusable(true);
   }
   
   public void apply(Entry entry){
      
      Debug.log("entry=" + entry);
      
      Location loc = entry.region.getCenter();
      hover(loc);
   
      command = entry.key;
      
      synchronized(guide){
         guide.notify();           
      }
   }
   
   
   @Override
   public void actionPerformed(ActionEvent e) {           
         Entry entry = ((Button)e.getSource()).getEntry();
         apply(entry);
   }


   void click(Location loc){
      try {
         guide.getRegion().click(loc, InputEvent.BUTTON1_MASK);
         // TODO: this will fail because it's an event dispatch thread and waitIdle can not
         // be called by click()
      } catch (FindFailed e) {
      } catch (Exception e){            
      }
   }
   
   void hover(Location loc){
      try {
         guide.getRegion().hover(loc);
         // TODO: this will fail because it's an event dispatch thread and waitIdle can not
         // be called by click()
      } catch (FindFailed e) {
      } catch (Exception e){            
      }
   }
   
   
   
   SikuliGuideCircle circle;   
   SikuliGuideSpotlight spotlight;
   int selected = 0;
   void selectEntry(int i){
      
      Item b = (Item) getContentPane().getComponent(selected);
      b.requestFocus();
      b.setSelected(false);

      
      if (i < 0){
         i = entries.size() - 1;
      }else if (i >= entries.size()){
         i = 0;
      }
      
      selected = i;         
      //Button b = (Button) getContentPane().getComponent(i);
      //b.requestFocus();
      
      b = (Item) getContentPane().getComponent(selected);
      b.requestFocus();
      b.setSelected(true);

      
      //circle.setRegion(b.getEntry().region);
      //spotlight.setRegion(b.getEntry().region);
      spotlight.setBounds(b.getEntry().region.getRect());
      
      guide.repaint();
   }
   
   @Override
   public void keyPressed(KeyEvent k) {

      if (k.getKeyCode() == KeyEvent.VK_DOWN || k.getKeyCode() == KeyEvent.VK_RIGHT){
         selectEntry(selected+1);
      }else if (k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_LEFT ){         
         selectEntry(selected-1);
      }else if (k.getKeyCode() == KeyEvent.VK_ENTER){
         apply(((Item) k.getSource()).getEntry());
      }
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }

   @Override
   public String waitForTransition() {
      
      if (entries.isEmpty()){
      // if nothing is added to the portal, do nothing
         return "Next";
      }
      
      setLocationRelativeTo(null);
      setVisible(true);
      setAlwaysOnTop(true);
      toFront();
      requestFocus();
            
      // add the circle to the first region
//      circle = new AnnotationOval(entries.get(0).region);
//      guide.addAnnotation(circle);
      
       //= new Spotlight(entries.get(0).region.getRect());
       
      spotlight = new SikuliGuideSpotlight(entries.get(0).region);
      spotlight.setShape(SikuliGuideSpotlight.CIRCLE);
      
      guide.addComponent(spotlight);
      
      
      
      guide.repaint();
      
      synchronized(guide){
         try {
            guide.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      dispose();
      setVisible(false);
      return "Next";
   }
   

}


