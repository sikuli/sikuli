package org.sikuli.guide;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;

import org.sikuli.script.Debug;


public class SklEditor extends JFrame {
   
   SklStepModel stepModel;
   SklStepPreview stepView;
   
   SklStepListView stepListView;
   
   SklStepListModel stepListModel;
   SklStepListSelectionModel stepListSelection;
   
   
   public SklEditor(){
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      stepModel = new SklStepModel(); 
      stepView = new SklStepPreview(null);
      
      stepListModel = new SklStepListModel();      
      stepListSelection = new SklStepListSelectionModel();
      // this allows the editing pane to show the selected step in the list
      stepListSelection.addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
                           
            if (e.getValueIsAdjusting()  == false){

               int index = ((SklStepListSelectionModel) e.getSource()).getLeadSelectionIndex();
               stepModel = (SklStepModel) stepListModel.getElementAt(index);
               stepView.setModel(stepModel);
               stepView.repaint();
               
            }
            
         }
         
      });
      
            
      stepListView = new SklStepListView(stepListModel, stepListSelection);

      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            stepListView, stepView);
      splitPane.setDividerLocation(250);
  
      UndoManager manager = new UndoManager();
      
      JToolBar toolbar = new JToolBar();
//      toolbar.add(UndoManagerHelper.getUndoAction(manager));
//      toolbar.add(UndoManagerHelper.getRedoAction(manager));
      toolbar.add(new CaptureAction());
      toolbar.add(new PlayAction());
      toolbar.add(new PlayAllAction());

  //      view.addUndoableEditListener(manager);

      
      Container content = getContentPane();
      content.add(toolbar, BorderLayout.NORTH);
      content.add(splitPane,BorderLayout.CENTER);      
      
      setSize(1000,600);
      setLocationRelativeTo(null);
      setVisible(true);
      
   }
   
   public class CaptureAction extends AbstractAction {
      
      public CaptureAction(){
         super("Capture");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Thread t = new Thread(){
            
            @Override 
            public void run(){
               setVisible(false);
               JFrame f = new JFrame("JFrame");
               f.setSize(0,0);
               f.setLocation(0,0);
               f.setUndecorated(true);
               f.setVisible(true);
               
               ScreenRecorderWindow w = 
                  new ScreenRecorderWindow(f);
               w.startModal();
               
               
               for (RecordedClickEvent e : w.clickEvents){
                  importStep(e);
               }
               
               selectStep(0);
               
               setVisible(true);
               f.setVisible(false);
               f.dispose();

            }
         };

         t.start();
      }
   }

   
   public class PlayAction extends AbstractAction {
      
      public PlayAction(){
         super("Play");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         
         Thread t = new Thread(){

            public void run(){
               setVisible(false);
               SikuliGuide g = new SikuliGuide();                   
               g.playStep(stepModel);               
               setVisible(true);
            }
         };
         
         t.start();
         
         
      }
    }
   
   public class PlayAllAction extends AbstractAction {
      
      public PlayAllAction(){
         super("Play All");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         
         Thread t = new Thread(){

            public void run(){
               setVisible(false);
               SikuliGuide g = new SikuliGuide();               
               g.playStepList(stepListModel);
               setVisible(true);
            }
         };
         
         t.start();
         
         
      }
    }
   
   
   public void importStep(RecordedClickEvent event){

      SklStepModel importedStepModel = new SklStepModel();
      Point clickLocation = event.getClickLocation();
      Rectangle defaultAnchorBounds = new Rectangle(50,50);
      // center the default anchor at the click location
      defaultAnchorBounds.x = clickLocation.x - defaultAnchorBounds.width/2;
      defaultAnchorBounds.y = clickLocation.y - defaultAnchorBounds.height/2;
      
//      Rectangle small = new Rectangle(20,20);
//      small.x = clickLocation.x - small.width/2;
//      small.y = clickLocation.y - small.height/2;
            
      SklImageModel imageModel = new SklImageModel();
      imageModel.setImage(event.getScreenImage()); 
      
      SklAnchorModel anchor = new SklAnchorModel(defaultAnchorBounds);      
      importedStepModel.addModel(anchor);
      
      importedStepModel.setReferenceImage(imageModel);
      //importedStepModel.setReferenceImage(new SklImageModel("screen1.png"));
      
//         // TODO chooses the best location automatically (that does not go outside of display bounds) 
//         SikuliGuideText txt = (SikuliGuideText) 
//            performAddTextAction(new Point(defaultAnchorBounds.x+50,defaultAnchorBounds.y-20));
//         txt.setText("Click");     
//         currentStepContentChanged();
//      }
//
//      if (overview != null){
//         overview.addStep(step);
//      }
//      validate();
//     
//      return step;
      
      addStep(importedStepModel);
   }
   

   public void selectStep(int index){
      stepListSelection.setSelectionInterval(index,index);
   }

   public void removeStep(int index){
      stepListModel.removeElementAt(index);
      stepListSelection.setSelectionInterval(index-1,index-1);
   }
   
   public void addStep(SklStepModel step) {
      stepListModel.addElement(step);
      int n = stepListModel.size();
      // select the step just added
      stepListSelection.setSelectionInterval(n-1,n-1);
   }

}
