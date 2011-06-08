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
            stepModel.setSelected(isSelected);         
            
            
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
   
//   class SklStepThumbnailView extends JPanel implements PropertyChangeListener, StepDataEventListener  {
//      
//      SklStepModel _stepModel;
//      SklStepThumbnailView(SklStepModel stepModel){
//         setLayout(null);
//         setOpaque(true);         
//         setModel(stepModel);  
//         
//         addKeyListener(SklDocumentListView.this);
//      }
//      
//      JPanel panel;
//      
//      void setModel(SklStepModel model){
//         removeAll();
//         
//         if (_stepModel != null){
//            _stepModel.removeDataEventListener(this);       
//         }                     
//         _stepModel = model;
//         _stepModel.addDataEventListener(this);       
//         
//         
//         panel = new JPanel();
//         panel.setLayout(null);
//         
//         SklImageView imageView = (SklImageView) SklViewFactory.createView(_stepModel.getReferenceImageModel());
//         panel.add(imageView,0);
//
//         for (SklModel m : _stepModel.getModels()){
//            SklView view = SklViewFactory.createView(m);
//            panel.add(view,0);
//         }      
//         panel.setBounds(imageView.getBounds());
//         
//      }
//      
//      Rectangle getForegroundBounds(SklStepModel stepModel){
//
//         Rectangle bounds = null;
//         for (SklModel model : stepModel.getModels()){
//
//            Rectangle modelBounds = new Rectangle();
//            modelBounds.x = model.getX();
//            modelBounds.y = model.getY();
//            modelBounds.width = model.getWidth();
//            modelBounds.height = model.getHeight();
//
//            if (bounds == null){
//               bounds = modelBounds;             
//            }else{
//               bounds.add(modelBounds);
//            }
//         }
//
//         return bounds;     
//      }
//      
//      
//      int marginSize = 40;
//      
//      
////      private void paintRegionCentered(Graphics g, Rectangle bounds){
////         Graphics2D g2d = (Graphics2D) g;
////         
////         Dimension windowSize = getSize();// Dimension(200,120);
////
////         Rectangle r = new Rectangle();
////         
////         // if the foreground content is larger than the window
////         if (bounds.width > windowSize.width){
////            // scale the foreground content down to fit the window
////
////            r = new Rectangle(bounds);
////            r.grow(20,20);
////
////            float scalex =  1f * windowSize.width / bounds.width; 
////            float scaley =  1f * windowSize.height / bounds.height; 
////            float minScale = Math.min(scalex,scaley);
////
////            g2d.scale(minScale,minScale);
////            
////            g2d.translate(-r.x, -r.y);
////
////            panel.setBounds(0,0,1,1);
////            panel.paint(g2d);
////
////            g2d.translate(r.x, r.y);
////            g2d.scale(1f/minScale,1f/minScale);
////
////         }else{
////            // center the foreground content within the window
////
////            r.x = bounds.x + bounds.width/2 - windowSize.width/2;
////            r.y = bounds.y + bounds.height/2 - windowSize.height/2;
////            r.width = windowSize.width;
////            r.height = windowSize.height;
////         }
////
////
////         //g2d.clipRect(0,0, r.width, r.height);
////        
////         
////      }
//      
//      BufferedImage drawRegion(Graphics g, JComponent comp, Rectangle region){
//         
//         BufferedImage image = new BufferedImage(region.width,region.height,BufferedImage.TYPE_INT_RGB);         
//         Graphics gimg = image.createGraphics();
//         gimg.translate(-region.x, -region.y);
//         // turn off double buffering, otherwise it crashes on windows
//         comp.setDoubleBuffered(false);
//         comp.paint(gimg);
//         
//         
//         Graphics2D g2d = (Graphics2D) g;
//         
//         Dimension size = getSize();
//         
//         // Stretch to fill the available area
//         
//         
//         // Fit and preserve aspect ratio
//         
//        if (size.width < region.width || size.height < region.height){
//         
//           float scalex = 1f* size.width / region.width;
//           float scaley = 1f* size.height / region.height;
//           float minscale = Math.min(scalex,scaley);
//           
//           int height = (int) (region.height * minscale);
//           int width = (int) (region.width * minscale);
//
//           int x = size.width/2 - width/2;
//           int y = size.height/2 - height/2; 
//           
//           g2d.setColor(Color.black);
//           g2d.fillRect(0,0,size.width,size.height);
//           g2d.drawImage(image,x,y,width,height,null);
//        } else{
//           
//           
//           g2d.drawImage(image,0,0,size.width,size.height,null);
//           
//        }
//         
//         
//         return image;
//      }
//      
//      @Override
//      public void paintComponent(Graphics g){
//         super.paintComponent(g);
//         
//         Graphics2D g2d = (Graphics2D) g;
//         
//         Rectangle regionToPaint;
//         
//         // if there are foreground objects
//         if (_stepModel.getModels().size() > 0){
//
//            Rectangle fgbounds = getForegroundBounds(_stepModel);
//            
//            Dimension fgSize = fgbounds.getSize();            
//            Dimension windowSize = getSize();
//            
//            regionToPaint = new Rectangle();
//
//            // center the fg region within the displayable window
//            if (fgSize.width < windowSize.width){
//               regionToPaint.x = fgbounds.x + fgbounds.width/2 - windowSize.width/2;
//               regionToPaint.width = windowSize.width;
//            } else {
//               fgbounds.grow(10,10);
//               regionToPaint.x = fgbounds.x;
//               regionToPaint.width = fgbounds.width;
//               
//            }
//            
//            if (fgSize.height < windowSize.height){
//               regionToPaint.y = fgbounds.y + fgbounds.height/2 - windowSize.height/2;
//               regionToPaint.height = windowSize.height;
//            } else {
//               fgbounds.grow(10,10);
//               regionToPaint.y = fgbounds.y;
//               regionToPaint.height = fgbounds.height;
//            }
//
//            
//         } else{
//         
//            regionToPaint = _stepModel.getReferenceImageModel().getBounds();             
//         }
//         
//         //paintRegionCentered(g,regionToPaint);
//         
//         drawRegion(g, panel, regionToPaint);
//         
//         
//         if (_stepModel.isSelected()){
//            Rectangle r = getBounds();
//            g2d.setStroke(new BasicStroke(3f));
//            g2d.setColor(Color.red);
//            g2d.drawRect(1,1,r.width - 3,r.height - 3);
//         }
//         
//      }
//
//      @Override
//      public void propertyChange(PropertyChangeEvent e) {
//         if (e.getSource() == _stepModel){
//            
//            Rectangle bounds = null;
//            if (_stepModel.getModels().size() == 0){
//               
//               bounds = _stepModel.getReferenceImageModel().getBounds();
//               
//            }else{
//               
//               bounds = getForegroundBounds(_stepModel);
//
//            }
//            
//            setPreferredSize(bounds.getSize());
//         }
//      }
//
//      @Override
//      public void contentsChanged(Object source) {
//         if (source == _stepModel){
//            setModel((SklStepModel) source);
//            
//            // TODO: figure out a way to do local update instaed of updating the entire list view
//            //Rectangle r = getBounds();
//            //SklStepListView.this.repaint(r.x,r.y,r.width,r.height);
//            SklDocumentListView.this.repaint();
//            
//            //repaintImmediately();
//         }
//      }
//      
//   }
   
   Map<SklStepModel, JComponent> _stepModelViewMap = new HashMap<SklStepModel, JComponent>();   
   
   SklDocument _document;
   JList _list;
   
   SklDocumentListView(final SklDocument document) { 
      
      setLayout(new BorderLayout());

      _list = new JList();
      _list.setCellRenderer(new Renderer());
      _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _list.setVisibleRowCount(-1);
      //_list.setBackground(new Color(119,136,153));
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
         Debug.info("content changed");
      }

      @Override
      public void intervalAdded(ListDataEvent e) {
         for (int i = e.getIndex0() ; i <= e.getIndex1(); ++i){
//            SklStepModel eachNewStepModel = _document.getStep(i);
//            SklStepThumbnailView newView = new SklStepThumbnailView(eachNewStepModel);
//            _stepModelViewMap.put(eachNewStepModel, newView);
            _list.ensureIndexIsVisible(i);
            //list.setSelectedIndex(i);
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
