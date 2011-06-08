package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sikuli.script.Debug;

import com.sun.medialib.mlib.Image;

public class SklDocumentListView extends JPanel implements KeyListener {

   class Renderer extends JPanel implements ListCellRenderer{

      @Override
      public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
         if (_document != null){
            
            SklStepModel stepModel = _document.getStep(index);
            //stepModel.setSelected(isSelected);         
            
            
            JComponent view = _stepModelViewMap.get(stepModel);
            
            if (view == null){
               JComponent v = new SklStepForegroundView(stepModel);
               
               stepModel.addDataEventListener(new StepDataEventListener(){

                  @Override
                  public void contentsChanged(Object source) {
                     SklDocumentListView.this.repaint();
                     
                  }
                     
               });
               
               view = new ListCellTile(v);
               
               _stepModelViewMap.put(stepModel, view);
            }
            
            //view.setPreferredSize(new Dimension(200,150));
            
            ((ListCellTile) view).setIndex(index);
            ((ListCellTile) view).setSelected(isSelected);
            
            return view;
         }
         else{
            return null;
         }
      }
      
   }
   
   class ListCellTile extends JPanel{
      
      JLabel _indexLabel = new JLabel();
      
      ListCellTile(JComponent comp){        
         setOpaque(true);
         setBorder(BorderFactory.createEmptyBorder(10,10,10,10));         
         setLayout(new BorderLayout());
         
         
         _indexLabel.setFont(new Font("sansserif", Font.BOLD, 16));
         add(_indexLabel, BorderLayout.WEST);
         
         
         add(comp, BorderLayout.CENTER);
         //add(new JButton("Test"),BorderLayout.CENTER);
         
         setPreferredSize(new Dimension(200,200));
         setMinimumSize(new Dimension(200,200));
      }
      
      void setIndex(int index){
         _indexLabel.setText(""+(index+1));
      }
      
      void setSelected(boolean selected){
         if (selected){
            //setBackground(new Color(0,0,128));
            setBackground((new Color(30,144,255)).darker());
            _indexLabel.setForeground(Color.white);
         } else {
            setBackground(Color.white);
            _indexLabel.setForeground(Color.black);
         }
      }
      
   }
   

   Map<SklStepModel, JComponent> _stepModelViewMap = new HashMap<SklStepModel, JComponent>();   
   
   SklDocument _document;
   JList _list;
   
   SklDocumentListView(final SklDocument document) { 
      
      setLayout(new BorderLayout());

      _list = new JList();
      _list.setCellRenderer(new Renderer());
      _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _list.setVisibleRowCount(-1);
      _list.setDragEnabled(true);
      _list.addKeyListener(this);
      
      JScrollPane scrollPane = new JScrollPane(_list);
      scrollPane.setPreferredSize(new Dimension(120, 80));
      add(scrollPane,BorderLayout.CENTER);
      
      
      setDocument(document);
      setFocusable(true);
      
      addComponentListener(new ComponentListener(){

         @Override
         public void componentHidden(ComponentEvent arg0) {
            
         }

         @Override
         public void componentMoved(ComponentEvent arg0) {
            
         }

         @Override
         public void componentResized(ComponentEvent arg0) {
            _list.repaint();
            validate();
         }

         @Override
         public void componentShown(ComponentEvent arg0) {
            repaint();
            validate();
         }
         
      });
      
   }
   
   private ListDataListener _documentStepListModelListener = new ListDataListener(){
      
      @Override
      public void contentsChanged(ListDataEvent e) {
         //Debug.info("content changed");
      }

      @Override
      public void intervalAdded(ListDataEvent e) {
         for (int i = e.getIndex0() ; i <= e.getIndex1(); ++i){
            _list.ensureIndexIsVisible(i);
         }
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
      }
      
   };
   
   void setDocument(SklDocument document){
      
      // remove self as a listener from the previous document model
      if (_document != null){
         _document.getListModel().removeListDataListener(_documentStepListModelListener);
      }
      
      
      _document = document;           
      _document.getListModel().addListDataListener(_documentStepListModelListener); 

      _stepModelViewMap.clear();

      _list.setSelectionModel(_document.getSelectionModel());
      _list.setModel(_document.getListModel());     
      
      _document.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
            _list.ensureIndexIsVisible(_document.getSelectedStepIndex());
         }
         
      });
   }
   
   
   @Override
   public void keyPressed(KeyEvent k) {
      //Debug.log("pressed " + k.getKeyCode());
      
      if (k.getKeyCode() == KeyEvent.VK_BACK_SPACE){
         Debug.log("User pressed DELETE");
         
         int index = _list.getSelectedIndex();
         _document.removeStep(index);
      }
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }
}
