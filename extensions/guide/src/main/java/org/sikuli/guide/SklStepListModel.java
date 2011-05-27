package org.sikuli.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sikuli.guide.Overview.Renderer;
import org.sikuli.guide.Overview.StepTile;
import org.sikuli.guide.StepView.BackgroundImage;
import org.sikuli.script.Debug;

public class SklStepListModel extends DefaultListModel  {    
}

class SklStepListSelectionModel extends DefaultListSelectionModel {
   
}

//interface StepListViewSelectionEventListener extends EventListener{
//   void valueChanged(StepListViewSelectionEvent e);
//}

//class StepListViewSelectionEvent {      
//   SklStepListModel list;
//   SklStepModel selected;
//   
//   StepListViewSelectionEvent(SklStepListModel list, SklStepModel selected){
//      this.list = list;
//      this.selected = selected;
//   }
//   
//   SklStepModel getSelected(){
//      return selected;
//   }
//   
//   SklStepListModel getList(){
//      return list;
//   }
//}

class SklStepListView extends JPanel {
   
//   EventListenerList listenerList = new EventListenerList();
//   public void addSelectionListener(ListSelectionListener l) {
//      list.addListSelectionListener(l);
//   }
//
//   public void removeSelectionListener(ListSelectionListener l) {
//      list.removeListSelectionListener(l);
   //}
//   
//   protected void fireSelectionValueChanged(SklStepModel selected) {
//      // Guaranteed to return a non-null array
//      Object[] listeners = listenerList.getListenerList();
//      // Process the listeners last to first, notifying
//      // those that are interested in this event
//      for (int i = listeners.length-2; i>=0; i-=2) {
//         StepListViewSelectionEvent editorEvent = null;
//          if (listeners[i]==StepListViewSelectionEventListener.class) {
//              // Lazily create the event:
//              if (editorEvent == null)
//                 editorEvent = new StepListViewSelectionEvent(listModel, selected);
//              ((StepListViewSelectionEventListener)listeners[i+1]).valueChanged(editorEvent);
//          }
//      }
//  }
   
   
   class Renderer extends JPanel implements ListCellRenderer{

      @Override
      public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
         if (listModel != null){
            
            SklStepModel stepModel = (SklStepModel) listModel.get(index);
            stepModel.setSelected(isSelected);         
            
            SklStepThumbnailView view = (SklStepThumbnailView) stepModelViewMap.get(stepModel);
            view.setPreferredSize(new Dimension(200,150));
            
            return view;
         }
         else{
            return null;
         }
      }
      
   }
   
 
   
   class SklStepThumbnailView extends JPanel implements PropertyChangeListener, StepDataEventListener  {
      Rectangle foregroundBounds;
      
      SklStepModel stepModel;
      SklStepThumbnailView(SklStepModel stepModel){
         setLayout(null);
         setOpaque(true);         
         setModel(stepModel);         
      }
      
      void setModel(SklStepModel model){
         removeAll();
         
         if (this.stepModel != null){
            stepModel.removePropertyChangeListener(this);  
            stepModel.removeDataEventListener(this);       
         }            
         
         this.stepModel = model;
         stepModel.addPropertyChangeListener(this);  
         stepModel.addDataEventListener(this);       
         
         SklImageView imageView = (SklImageView) SklViewFactory.createView(stepModel.getReferenceImage());
         add(imageView,0);

         for (SklObjectModel m : stepModel.getModels()){
            SklObjectView view = SklViewFactory.createView(m);
            add(view,0);
         }      
         
         foregroundBounds = getForegroundBounds(stepModel);
         
         Dimension psize = foregroundBounds.getSize();
         psize.width += (2*marginSize);
         psize.height += (2*marginSize);
         setPreferredSize(psize);      
      }
      
      Rectangle getForegroundBounds(SklStepModel stepModel){

         Rectangle bounds = null;
         for (SklObjectModel model : stepModel.getModels()){

            Rectangle modelBounds = new Rectangle();
            modelBounds.x = model.getX();
            modelBounds.y = model.getY();
            modelBounds.width = model.getWidth();
            modelBounds.height = model.getHeight();

            if (bounds == null){
               bounds = modelBounds;             
            }else{
               bounds.add(modelBounds);
            }
         }

         return bounds;     
      }
      
      
      int marginSize = 40;
      
      @Override
      public void paint(Graphics g){
         //((Graphics2D) g).scale(0.5f,0.5f);
         
         Graphics2D g2d = (Graphics2D) g;
         
       
         
         Dimension windowSize = new Dimension(200,120);
         
         Rectangle r = new Rectangle();
         
         
         // if the foreground content is larger than the window
         if (foregroundBounds.width > windowSize.width){
            // scale the foreground content down to fit the window
            
            r = new Rectangle(foregroundBounds);
            r.grow(20,20);
            
            float scalex =  1f * windowSize.width / foregroundBounds.width; 
            float scaley =  1f * windowSize.height / foregroundBounds.height; 
            float minScale = Math.min(scalex,scaley);
            
            g2d.scale(minScale,minScale);
            
         }else{
            // center the foreground content within the window

            r.x = foregroundBounds.x + foregroundBounds.width/2 - windowSize.width/2;
            r.y = foregroundBounds.y + foregroundBounds.height/2 - windowSize.height/2;
            r.width = windowSize.width;
            r.height = windowSize.height;
         }
         

         g2d.clipRect(0,0, r.width, r.height);
         g2d.translate(-r.x, -r.y);

         super.paint(g);

         g2d.translate(r.x, r.y);
         
         if (stepModel.isSelected()){
            //Rectangle r = getBounds();
            g2d.setColor(Color.red);
            g2d.drawRect(0,0,r.width - 1,r.height - 1);
         }
         
      }

      @Override
      public void propertyChange(PropertyChangeEvent e) {
         if (e.getSource() == stepModel){
            foregroundBounds = getForegroundBounds(stepModel);
            setPreferredSize(foregroundBounds.getSize());
         }
      }

      @Override
      public void contentsChanged(Object source) {
         if (source == stepModel){
            setModel((SklStepModel) source);
            
            // TODO: figure out a way to do local update instaed of updating the entire list view
            Rectangle r = getBounds();
            //SklStepListView.this.repaint(r.x,r.y,r.width,r.height);
            SklStepListView.this.repaint();
            //repaintImmediately();
         }
      }
      
   }
   
   Map<SklStepModel, SklStepThumbnailView> stepModelViewMap = new HashMap<SklStepModel, SklStepThumbnailView>();
   
   SklStepListModel listModel;
   SklStepListSelectionModel listSelectionModel;
   JList list;
   SklStepListView(final SklStepListModel listModel, SklStepListSelectionModel listSelectionModel){
      
      setLayout(new BorderLayout());

      list = new JList();
      list.setCellRenderer(new Renderer());
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setVisibleRowCount(-1);
      list.setBackground(new Color(119,136,153));
      
      JScrollPane scrollPane = new JScrollPane(list);
      scrollPane.setPreferredSize(new Dimension(120, 80));
      add(scrollPane,BorderLayout.CENTER);
            
      setListModel(listModel);
      setSelectionModel(listSelectionModel);
   }
   
   void setSelectionModel(SklStepListSelectionModel listSelectionModel){
      this.listSelectionModel = listSelectionModel;
      list.setSelectionModel(listSelectionModel);
   }
   
   
   private StepListModelDataAdapter stepListModelDataAdapter = new StepListModelDataAdapter();
   class StepListModelDataAdapter implements ListDataListener {
      
      @Override
      public void contentsChanged(ListDataEvent e) {
      }

      @Override
      public void intervalAdded(ListDataEvent e) {
         //Debug.info("ListModelView: new steps added: e = " + e);
         SklStepListModel listModel = (SklStepListModel) e.getSource(); 
         
         for (int i = e.getIndex0() ; i <= e.getIndex1(); ++i){
            SklStepModel stepModel = (SklStepModel) listModel.getElementAt(i);
            SklStepThumbnailView stepView = new SklStepThumbnailView(stepModel);
            stepModelViewMap.put(stepModel, stepView);
         }
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
      }
      
   }
   
   void setListModel(SklStepListModel listModel){
      stepModelViewMap.clear();
      
      list.setModel(listModel);      
      listModel.addListDataListener(stepListModelDataAdapter); 
      
      for (Object object : listModel.toArray()){
         SklStepModel stepModel = (SklStepModel) object;
         SklStepThumbnailView view = new SklStepThumbnailView(stepModel);
         stepModelViewMap.put(stepModel, view);
      }
      
      
      if (this.listModel != null){
         this.listModel.removeListDataListener(stepListModelDataAdapter);
      }
      this.listModel = listModel;
   }
   
}
