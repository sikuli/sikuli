/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.guide.Step.StepListener;
import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;

public class Overview extends JPanel {
      
   Color selectionColor = new Color(240,255,255);   
   JList list;
   DefaultListModel listModel;
   
   class Renderer extends JPanel implements ListCellRenderer{

      @Override
      public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
         
         StepTile comp = (StepTile) listModel.get(index);
         comp.setIndex(index);
         comp.setPreferredSize(comp.getActualSize());         
         comp.setSelected(isSelected);         
         return comp;
      }
      
   }
   


   public Overview(){         
      setLayout(new BorderLayout());

      listModel = new DefaultListModel();      

      list = new JList(listModel);
      list.setCellRenderer(new Renderer());
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setVisibleRowCount(-1);
      list.setBackground(new Color(119,136,153));
      list.addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
            fireSelectionChanged();
         }
         
      });
      
      
      JScrollPane scrollPane = new JScrollPane(list);
      scrollPane.setPreferredSize(new Dimension(250, 80));
      add(scrollPane,BorderLayout.CENTER);
      
   }

   class Thumbnail extends JComponent {
      BufferedImage image;
      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;        
         g2d.drawImage(image,0,0,null,null);
         g2d.setColor(Color.black);
         g2d.drawRect(0,0,image.getWidth()-1,image.getHeight()-1);
      }
   }
   
   class StepTile extends SikuliGuideComponent {

      Step step;
      int index;
      boolean selected;
      
      
      JLabel label;
      
      Thumbnail thumb = new Thumbnail();
      public StepTile(Step step) {
         super();
         this.step = step;

         thumb.image = step.getThumbnailImage();

         label = new JLabel("1");

         label.setSize(15,30);
         label.setLocation(5,0);
         
         thumb.setLocation(17,10);
         thumb.setSize(thumb.image.getWidth(),thumb.image.getHeight());
          
         add(label);
         add(thumb);
         
         Component c = Box.createRigidArea(new Dimension(0,10));
         c.setLocation(0,thumb.image.getHeight());

         add(c);
         
         setActualSize(label.getWidth() + thumb.getWidth()+2, thumb.getHeight()+20);
      }

      public void setIndex(int index) {
         this.index = index;
         label.setText(""+(index+1));
      }

      public Step getStep() {
         return step;
      }

      void refreshImage(){
         thumb.image = step.getThumbnailImage();
         repaint();
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;
         
         if (selected){
            g2d.setStroke(new BasicStroke(3f));
            g2d.setColor(selectionColor);
            g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
         }

      }

      void setSelected(boolean selected){
         this.selected = selected;
      }
   }

   public void addStep(Step step){            
      StepTile tile = new StepTile(step);
      step.listener = new StepListener(){

         @Override
         public void thumbnailRefreshed(Step step) {
            StepTile st = getStepTile(step);
            if (st != null)
               st.refreshImage();   
         }
         
      };
      
      listModel.addElement(tile);
      Debug.info("[Overview] has " + listModel.size() + " elements");
      list.setSelectedIndex(0);
   }

   StepTile getStepTile(Step step){
      // TODO use hashmap to lookup 
      Enumeration<StepTile> e = (Enumeration<StepTile>) listModel.elements();
      while (e.hasMoreElements()){
         StepTile tile = e.nextElement();
         if (tile.step == step){
            return tile;
         }
      }
      return null;
   }

   public void selectStep(Step step){
      StepTile st = getStepTile(step);
      int index = listModel.indexOf(st);
      list.setSelectedIndex(index);
   }
   
   interface OverviewListener extends EventListener{
      void selectionChanged(Step step, int index);
   }
   
   EventListenerList listenerList = new EventListenerList();
   public void addOverviewListener(OverviewListener listener) {
       listenerList.add(OverviewListener.class, listener);
   }
   
   public void removeOverviewListener(OverviewListener listener) {
      listenerList.remove(OverviewListener.class, listener);
   }
   
   protected void fireSelectionChanged() {
      
      int index = list.getSelectedIndex();
      Step step = ((StepTile) listModel.get(index)).getStep();

      for (OverviewListener listener : listenerList.getListeners(OverviewListener.class)){
         listener.selectionChanged(step, index);
      }
   }

}